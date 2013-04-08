package edu.umn.cs.melt.copper.compiletime.builders;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

public class SingleScannerDFAAnnotationBuilder
{
	private ParserSpec spec;
	private GeneralizedDFA dfa;
	
	private SingleScannerDFAAnnotationBuilder(ParserSpec spec,GeneralizedDFA dfa)
	{
		this.spec = spec;
		this.dfa = dfa;
	}
	
	public static SingleScannerDFAAnnotations build(ParserSpec spec,GeneralizedDFA dfa)
	{
		return new SingleScannerDFAAnnotationBuilder(spec,dfa).buildAnnotations();
	}
	
	private SingleScannerDFAAnnotations buildAnnotations()
	{
		int i;

		HashSet<BitSet> circularDependencies = new HashSet<BitSet>();
		Queue<BitSet> stateCircularDependencies = new LinkedList<BitSet>();
		
		// Set up holders for expanded state information.
		int SCANNER_STATE_COUNT = dfa.stateCount();
		BitSet[] acceptSets = new BitSet[SCANNER_STATE_COUNT];
		BitSet[] rejectSets = new BitSet[SCANNER_STATE_COUNT];
		BitSet[] possibleSets = new BitSet[SCANNER_STATE_COUNT];
		int[] cMap = new int[Character.MAX_VALUE + 1];

		// For each state in the DFA:
		for(int state = 0;state < dfa.stateCount();state++)
		{
			BitSet accF = dfa.getAcceptSymbols(state);
			
			stateCircularDependencies.clear();
			BitSet rej = spec.t.precedences.partitionAcceptSet(stateCircularDependencies,accF);
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
			
			// DEBUG-X-BEGIN
			//System.err.println("accF = " + accF + "; acc = " + acceptSets[state] + "; rej = " + rejectSets[state]);
			// DEBUG-X-END
		}
		
		boolean[][] transClosure = new boolean[SCANNER_STATE_COUNT][SCANNER_STATE_COUNT];
		// Compute the transitive closure of the DFA's states
		// using the Floyd-Warshall algorithm, as presented in
		// Cormen, Leiserson, Rivest and Stein's
		// "Introduction to Algorithms," Second Edition,
		// section 25.2.
		for(i = 0;i < SCANNER_STATE_COUNT;i++) transClosure[i][i] = true;
		for(int state = 0;state < dfa.stateCount();state++)
		{
			BitSet connections = dfa.getConnectedStates(state);
			for(int j = connections.nextSetBit(0);j >= 0;j = connections.nextSetBit(j+1))
			{
				transClosure[state][j] = true;
			}
		}
		for(int k = 0;k < SCANNER_STATE_COUNT;k++)
		{
			for(i = 0;i < SCANNER_STATE_COUNT;i++)
			{
				for(int j = 0;j < SCANNER_STATE_COUNT;j++)
				{
					transClosure[i][j] = transClosure[i][j] || (transClosure[i][k] && transClosure[k][j]);
				}
			}
		}
		// Use the transitive closure to compute possible sets:
		for(i = 0;i < SCANNER_STATE_COUNT;i++)
		{
			for(int j = 0;j < SCANNER_STATE_COUNT;j++)
			{
				// If states i and j are connected by transition:
				if(transClosure[i][j])
				{
					// Union the current possible set of i with
					// the accepting set of j.
					possibleSets[i].or(acceptSets[j]);
					possibleSets[i].or(rejectSets[j]);
				}
			}
		}
		
		for(int cr = 0;cr < dfa.charRangeCount();cr++)
		{
			char[][] canonicalRanges = dfa.getCharRange(cr).getMembers();
			for(int j = 0;j < canonicalRanges.length;j++)
			{
				Arrays.fill(cMap, canonicalRanges[j][0], canonicalRanges[j][1] + 1,cr);
			}
		}
		
		BitSet[] circularDependenciesA = new BitSet[circularDependencies.size()];
		circularDependencies.toArray(circularDependenciesA);
		
		return new SingleScannerDFAAnnotations(acceptSets, rejectSets, possibleSets, cMap, circularDependenciesA);
	}
}
