package edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SetTiling;

public class GeneralizedNFA extends GeneralizedFA
{
	private BitSet[][] transitions;
	
	@Override
	public void lengthenArrays()
	{
		BitSet[] newAcceptStates = new BitSet[2 * acceptStates.length];
		System.arraycopy(acceptStates,0,newAcceptStates,0,acceptStates.length);
		acceptStates = newAcceptStates;
		
		BitSet[][] newTransitions = new BitSet[2 * transitions.length][alphabetSize + 1];
		System.arraycopy(transitions,0,newTransitions,0,transitions.length);
		transitions = newTransitions;		
	}

	public GeneralizedNFA(int numRegexes, int alphabetSize)
	{
		super(numRegexes, alphabetSize);
		int initialArraySizes = getInitialArraySizes(numRegexes);
		transitions = new BitSet[initialArraySizes][alphabetSize + 1];
	}
	
	private boolean prepareTransitionDest(SetOfCharsSyntax chars,int src)
	{
		if(src >= stateCount()) return false;
		int charRangeNumber;
		if(!charRangeNumbers.containsKey(chars))
		{
			charRangeNumbers.put(chars,nextNewCharRangeNumber);
			charRanges[nextNewCharRangeNumber] = chars;
			charRangeNumber = nextNewCharRangeNumber++;
		}
		else charRangeNumber = charRangeNumbers.get(chars);
		BitSet destSet;
		if(transitions[src][charRangeNumber] == null)
		{
			destSet = new BitSet(stateCount() + 1);
			transitions[src][charRangeNumber] = destSet;
		}
		else destSet = transitions[src][charRangeNumber];

		return true;
	}

	@Override
	public boolean addTransition(SetOfCharsSyntax chars,int src,int dest)
	{
		if(dest >= stateCount() || !prepareTransitionDest(chars,src)) return false;
		transitions[src][charRangeNumbers.get(chars)].set(dest);
		return true;
	}
	
	public boolean addEpsilonTransition(int src,int dest)
	{
		return addTransition(new SetOfCharsSyntax(),src,dest);
	}
	
	public boolean addTransitions(SetOfCharsSyntax chars,int src,BitSet dests)
	{
		if(dests.nextSetBit(stateCount()) != -1 || !prepareTransitionDest(chars,src)) return false;
		transitions[src][charRangeNumbers.get(chars)].or(dests);
		return true;
	}
	
	public boolean addEpsilonTransitions(int src,BitSet dests)
	{
		return addTransitions(new SetOfCharsSyntax(),src,dests);
	}
	
	public GeneralizedDFA determinize(int start)
	{
		Hashtable<BitSet,BitSet> memoizedEpsClosures = new Hashtable<BitSet,BitSet>();
		Hashtable<BitSet,Integer> mergedStateNames = new Hashtable<BitSet,Integer>();
		BitSet startStates = new BitSet(nextNewStateNumber);
		startStates.set(start);
		GeneralizedDFABuilder dfa = new GeneralizedDFABuilder(nextNewStateNumber,charRanges.length);
		
		// Compute a "tiling" of all the character sets used as transitions in the
		// NFA, i.e., a group of non-overlapping character sets covering the
		// same alphabet.
		SetTiling<SetOfCharsSyntax> tiledCharSets = tileCharSets(charRanges);
				
		LinkedList<BitSet> stateQueue = new LinkedList<BitSet>();
		startStates = getEpsilonClosure(memoizedEpsClosures,startStates);
		int dfaStartStateNumber = dfa.addState();
		mergedStateNames.put(startStates,dfaStartStateNumber);
		BitSet startAccepts = new BitSet();
		for(int i = startStates.nextSetBit(0);i >= 0;i = startStates.nextSetBit(i+1)) if(acceptStates[i] != null) startAccepts.or(acceptStates[i]); 
		dfa.addAcceptSymbols(dfaStartStateNumber,startAccepts);
		dfa.setStartState(dfaStartStateNumber);
		stateQueue.offer(startStates);
		
		// While there are still DFA states to construct:
		while(!stateQueue.isEmpty())
		{
			// Pull the first one out of the queue.
			BitSet front = stateQueue.poll();
			
			// Build a hashtable mapping each *NFA* character set i (in the form of its index
			// in the array charRanges) to its set of destination NFA states: the union of
			// delta(i,j) for all states j represented by the DFA state 'front'. 
			Hashtable<Integer,BitSet> allNFATransitions = new Hashtable<Integer,BitSet>();
			
			for(int i = 1;i < nextNewCharRangeNumber;i++)
			{
				for(int j = front.nextSetBit(0);j >= 0;j = front.nextSetBit(j+1))
				{
					if(transitions[j][i] != null)
					{
						if(!allNFATransitions.containsKey(i)) allNFATransitions.put(i,new BitSet(nextNewStateNumber));
						allNFATransitions.get(i).or(transitions[j][i]);
					}
				}
			}
			
			// Build a hashtable mapping each *DFA* character set i (in the form of its index
			// in the tiled set object tiledCharSets) to this same set of destination NFA states:
			// the union of allNFATransitions[k] for each *NFA* character set k of which i is a subset.
			Hashtable<Integer,BitSet> allDFATransitions = new Hashtable<Integer,BitSet>();
			
			for(int k : allNFATransitions.keySet())
			{
				BitSet tiles = tiledCharSets.getAllTilesCovering(k);
				for(int i = tiles.nextSetBit(0);i >= 0;i = tiles.nextSetBit(i+1))
				{
					if(!allDFATransitions.containsKey(i)) allDFATransitions.put(i,new BitSet(nextNewStateNumber));
					allDFATransitions.get(i).or(allNFATransitions.get(k));
				}
			}

			for(int i : allDFATransitions.keySet())
			{
				BitSet newDestSet = allDFATransitions.get(i);
				// Get the epsilon closure to produce the full identifier of the destination state.
				newDestSet = getEpsilonClosure(memoizedEpsClosures,newDestSet);

				BitSet newAcceptSet = new BitSet(nextAcceptSymbolNumber);
				for(int j = newDestSet.nextSetBit(0);j >= 0;j = newDestSet.nextSetBit(j+1))
				{
					if(acceptStates[j] != null) newAcceptSet.or(acceptStates[j]);
				}

				// If the destination state has not already been put in the DFA,
				// put it in and enqueue it for processing.
				int newDFAState;
				if(mergedStateNames.containsKey(newDestSet)) newDFAState = mergedStateNames.get(newDestSet);
				else
				{
					newDFAState = dfa.addState();
					dfa.addAcceptSymbols(newDFAState,newAcceptSet);
					mergedStateNames.put(newDestSet,newDFAState);
					stateQueue.add(newDestSet);
				}

				// Add transitions.
				dfa.addTransition(tiledCharSets.getTiledObject(i),mergedStateNames.get(front),newDFAState);
			}
		}

		// DEBUG-X-BEGIN
		// System.err.println(mergedStateNames);
		// DEBUG-X-END
		
		return dfa.buildDFA();
	}
	
	public BitSet getEpsilonClosure(Hashtable<BitSet,BitSet> epsClosures,BitSet states)
	{
		if(epsClosures.containsKey(states)) return epsClosures.get(states);
		
		// A FIFO queue to perform a nondeterministic search
		// of the NFA.
		BitSet stateQueue = new BitSet(nextNewStateNumber);
		BitSet closure = new BitSet(nextNewStateNumber);
		epsClosures.put(states,closure);
		stateQueue.or(states);
		// Search the NFA:
		while(!stateQueue.isEmpty())
		{
			int front = stateQueue.nextSetBit(0);
			stateQueue.clear(front);
			// Put the state currently being searched in the epsilon closure.
			closure.set(front);
			// Get all destination states of the epsilon transition not already
			// in the epsilon closure.
			if(transitions[front][0] == null) continue;
			BitSet intersection = (BitSet) transitions[front][0].clone();
			intersection.andNot(closure);
			// Put these states on the queue.
			stateQueue.or(intersection);
		}
		
		return closure;
	}
	
	public static SetTiling<SetOfCharsSyntax> tileCharSets(SetOfCharsSyntax... sets)
	{
		Hashtable<BitSet,SetOfCharsSyntax> rv = new Hashtable<BitSet,SetOfCharsSyntax>();
		Hashtable<Character,BitSet> openingExtrema = new Hashtable<Character,BitSet>();
		Hashtable<Character,BitSet> closingExtrema = new Hashtable<Character,BitSet>();
		for(int i = 0;i < sets.length;i++)
		{
			char[][] members = sets[i].getMembers();
			for(int j = 0;j < members.length;j++)
			{
				if(!openingExtrema.containsKey(members[j][0])) openingExtrema.put(members[j][0],new BitSet(sets.length));
				if(!closingExtrema.containsKey(members[j][1])) closingExtrema.put(members[j][1],new BitSet(sets.length));
				
				openingExtrema.get(members[j][0]).set(i);
				closingExtrema.get(members[j][1]).set(i);
			}
		}
		
		TreeSet<Character> allExtremaS = new TreeSet<Character>();
		allExtremaS.addAll(openingExtrema.keySet());
		allExtremaS.addAll(closingExtrema.keySet());
		Character[] allExtrema = new Character[allExtremaS.size()];
		allExtremaS.toArray(allExtrema);
		
		BitSet on = new BitSet(sets.length);

		for(int i = 0;i < allExtrema.length;i++)
		{
			SetOfCharsSyntax toPut;

			if(i != 0 && allExtrema[i-1] + 1 != allExtrema[i] && !on.isEmpty())
			{
				toPut = new SetOfCharsSyntax((char)(allExtrema[i-1] + 1),(char)(allExtrema[i] - 1));
				if(rv.containsKey(on)) toPut = SetOfCharsSyntax.union(rv.get(on),toPut);
				rv.put((BitSet) on.clone(),toPut);
			}

			if(openingExtrema.containsKey(allExtrema[i])) on.or(openingExtrema.get(allExtrema[i]));
			
			if(!on.isEmpty())
			{
				toPut = new SetOfCharsSyntax(allExtrema[i],allExtrema[i]);
				if(rv.containsKey(on)) toPut = SetOfCharsSyntax.union(rv.get(on),toPut);
				rv.put((BitSet) on.clone(),toPut);
			}
			
			if(closingExtrema.containsKey(allExtrema[i])) on.andNot(closingExtrema.get(allExtrema[i]));
		}
		
		return new SetTiling<SetOfCharsSyntax>(rv);
	}
	
	public String toString()
	{
		StringBuffer rv = new StringBuffer();
		rv.append("Character sets:\n");
		for(int i = 0;i < nextNewCharRangeNumber;i++)
		{
			rv.append(i + ": " + charRanges[i] + "\n");
		}
		for(int i = 0;i < nextNewStateNumber;i++)
		{
			rv.append("State " + i + ":\n  Transitions -");
			for(int j = 0;j < nextNewCharRangeNumber;j++)
			{
				rv.append(" ").append(j).append(" -> ").append(transitions[i][j]);
			}
			rv.append("\n  Accepts: " + acceptStates[i]);
			rv.append("\n");
		}
		return rv.toString();		
	}
}
