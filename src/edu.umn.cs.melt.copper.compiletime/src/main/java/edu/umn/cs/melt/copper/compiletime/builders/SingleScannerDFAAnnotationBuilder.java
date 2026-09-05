package edu.umn.cs.melt.copper.compiletime.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PrecedenceGraph;

/**
 * Modified by Kevin Viratyosin to take PrecedenceGraph instead of the whole ParserSpec
 */
public class SingleScannerDFAAnnotationBuilder
{
	private PrecedenceGraph precedences;
	private GeneralizedDFA dfa;
	
	private SingleScannerDFAAnnotationBuilder(PrecedenceGraph precedences, GeneralizedDFA dfa)
	{
		this.precedences = precedences;
		this.dfa = dfa;
	}

	public static SingleScannerDFAAnnotations build(ParserSpec spec, GeneralizedDFA dfa)
	{
		return new SingleScannerDFAAnnotationBuilder(spec.t.precedences, dfa).buildAnnotations();
	}

	public static SingleScannerDFAAnnotations build(PrecedenceGraph precedences, GeneralizedDFA dfa)
	{
		return new SingleScannerDFAAnnotationBuilder(precedences, dfa).buildAnnotations();
	}

	private SingleScannerDFAAnnotations buildAnnotations()
	{
		HashSet<BitSet> circularDependencies = new HashSet<BitSet>();
		Queue<BitSet> stateCircularDependencies = new LinkedList<BitSet>();

		// Set up holders for expanded state information.
		int SCANNER_STATE_COUNT = dfa.stateCount();
		BitSet[] acceptSets = new BitSet[SCANNER_STATE_COUNT];
		BitSet[] rejectSets = new BitSet[SCANNER_STATE_COUNT];
		BitSet[] possibleSets = new BitSet[SCANNER_STATE_COUNT];
		int[] cMap = new int[Character.MAX_VALUE + 1];

		// To replace Floyd-Warshall, we build a reverse-edge graph.
		// This allows us to propagate reachable token sets backwards through the DFA.
		List<Integer>[] revEdges = new ArrayList[SCANNER_STATE_COUNT];
		for (int i = 0; i < SCANNER_STATE_COUNT; i++) {
			revEdges[i] = new ArrayList<>();
		}

		// For each state in the DFA, initialize base sets and build the reverse graph:
		for(int state = 0; state < SCANNER_STATE_COUNT; state++)
		{
			BitSet accF = dfa.getAcceptSymbols(state);

			stateCircularDependencies.clear();
			BitSet rej = precedences.partitionAcceptSet(stateCircularDependencies, accF);
			if(!stateCircularDependencies.isEmpty())
			{
				circularDependencies.addAll(stateCircularDependencies);
			}

			acceptSets[state] = new BitSet();
			rejectSets[state] = new BitSet();
			possibleSets[state] = new BitSet();

			acceptSets[state].or(accF);
			acceptSets[state].andNot(rej);
			rejectSets[state].or(rej);

			// A state can always reach itself, so seed possibleSets with its own accept/reject sets
			possibleSets[state].or(acceptSets[state]);
			possibleSets[state].or(rejectSets[state]);

			// Extract forward connections to build reverse edges
			BitSet connections = dfa.getConnectedStates(state);
			for(int next = connections.nextSetBit(0); next >= 0; next = connections.nextSetBit(next + 1))
			{
				revEdges[next].add(state);
			}
		}

		// --- WORKLIST ALGORITHM ---
		// Propagate 'possibleSets' backwards through the graph.
		// Uses a primitive array-based ring buffer queue for maximum CPU cache efficiency.
		int[] q = new int[SCANNER_STATE_COUNT];
		int head = 0, tail = 0, size = 0;
		boolean[] inQueue = new boolean[SCANNER_STATE_COUNT];

		// Initialize the queue with all states that have a non-empty possibleSet
		for(int state = 0; state < SCANNER_STATE_COUNT; state++) {
			if(!possibleSets[state].isEmpty()) {
				q[tail] = state;
				tail = (tail + 1) % SCANNER_STATE_COUNT;
				size++;
				inQueue[state] = true;
			}
		}

		// Process until the graph reaches a fixed point (no sets change)
		while(size > 0) {
			int curr = q[head];
			head = (head + 1) % SCANNER_STATE_COUNT;
			size--;
			inQueue[curr] = false;

			List<Integer> preds = revEdges[curr];
			for (int i = 0; i < preds.size(); i++) {
				int prev = preds.get(i);
				int oldCard = possibleSets[prev].cardinality();

				// Union the current state's reachable tokens into its predecessor
				possibleSets[prev].or(possibleSets[curr]);

				// If the predecessor gained new reachable tokens, re-queue it
				if (possibleSets[prev].cardinality() > oldCard) {
					if (!inQueue[prev]) {
						q[tail] = prev;
						tail = (tail + 1) % SCANNER_STATE_COUNT;
						size++;
						inQueue[prev] = true;
					}
				}
			}
		}

		for(int cr = 0; cr < dfa.charRangeCount(); cr++)
		{
			char[][] canonicalRanges = dfa.getCharRange(cr).getMembers();
			for(int j = 0; j < canonicalRanges.length; j++)
			{
				Arrays.fill(cMap, canonicalRanges[j][0], canonicalRanges[j][1] + 1, cr);
			}
		}

		BitSet[] circularDependenciesA = new BitSet[circularDependencies.size()];
		circularDependencies.toArray(circularDependenciesA);

		return new SingleScannerDFAAnnotations(acceptSets, rejectSets, possibleSets, cMap, circularDependenciesA);
	}
}
