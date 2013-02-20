package edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.auxiliary.CharacterRange;
import edu.umn.cs.melt.copper.legacy.compiletime.auxiliary.DynHashSet;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;



/**
 * Contains a static method for converting a general NFA to a DFA.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class NFA2DFA
{
	private Hashtable< NFAState,HashSet<Symbol> > memoizedEpsClosures;

	public NFA2DFA()
	{
		memoizedEpsClosures = new Hashtable< NFAState,HashSet<Symbol> >();
	}
	
	/**
	 * Converts an NFA to a DFA.
	 * @param toConvert The NFA to convert. It is assumed that all states in this NFA have only one identifying symbol.
	 * @return The converted NFA (deterministic).
	 */
	public NFA determinizeNFA(NFA toConvert)
	{
		// Store the NFA's states in a Hashtable for easy reference.
		Hashtable<Symbol,NFAState> singleIDStates = new Hashtable<Symbol,NFAState>();
		for(NFAState s : toConvert.getStates())
		{
			Symbol sym = s.getIdentifier().iterator().next();
			singleIDStates.put(sym,s);
		}
		// The states of the new DFA.
		DynHashSet<NFAState> states = new DynHashSet<NFAState>();
		// A FIFO queue to perform a nondeterministic breadth-first search of the NFA.
		LinkedList<NFAState> stateQueue = new LinkedList<NFAState>();
		// Add the epsilon-closure of the start state to the BFS queue.
		NFAState startState = new NFAState(epsilonClosure(toConvert.getStartState()),null);
		states.put(startState);
		stateQueue.offer(startState);
		// For each DFA state encountered:
		while(!stateQueue.isEmpty())
		{
			NFAState front = stateQueue.poll();
			// The DFA states reachable from this DFA state.
			Hashtable< CharacterRange,HashSet<Symbol> > nextStates = new Hashtable< CharacterRange,HashSet<Symbol> >();
			// For every NFA state represented by this DFA state:
			for(Symbol sym : front.getIdentifier())
			{
				// Add the accepting symbols of the NFA state to the DFA state.
				front.addAcceptSyms(singleIDStates.get(sym).getAccepts());
				// For every transition from the NFA state:
				for(Pair<CharacterRange,NFAState> pair : singleIDStates.get(sym))
				{
					// If it is an epsilon-transition, skip it
					if(pair.first().equals(new CharacterRange(NFAState.EmptyChar))) continue;
					// Else:
					// If there are no transitions yet recorded for the alphabet
					// symbol in this transition, create a table entry.
					if(!nextStates.containsKey(pair.first())) nextStates.put(pair.first(),new HashSet<Symbol>());
					// Add all states in the epsilon-closure of the transition's
					// destination state to the DFA destination state.
					nextStates.get(pair.first()).addAll(epsilonClosure(pair.second()));
				}
			}
			// For each character for which there was a transition recorded:
			for(CharacterRange c : nextStates.keySet())
			{
				// Create a new destination state from the set of NFA states obtained above.
				HashSet<Symbol> epsClosures = nextStates.get(c);
				NFAState newState = new NFAState(epsClosures,null);
				// If the DFA already contains the new state, add a transition to it from the current state.
				if(states.contains(newState))
				{
					front.addTransition(c,states.get(newState));
				}
				// Else:
				else
				{
					// Put the new state in the DFA.
					states.put(newState);
					// Add a transition to it from the current state.
					front.addTransition(c,newState);
					// Put it in the BFS queue.
					stateQueue.offer(newState);
				}
			}
		}
		// Return an NFA object (DFA) of the new states.
		HashSet<NFAState> newStates = new HashSet<NFAState>();
		for(NFAState ns : states) newStates.add(ns);
		return new NFA(newStates,startState);
	}
	
	/**
	 * Computes the epsilon-closure of an NFA state
	 * (all states reachable by epsilon-transition from that state).
	 * @param s The state for which to compute the closure. 
	 * @return A set consisting of the union of the symbol sets of all NFA states reachable from <CODE>s</CODE>.
	 */
	private HashSet<Symbol> epsilonClosure(NFAState s)
	{
		// This is a memoized method. If the closure of this state has been encountered before,
		// return the previously computed closure.
		if(memoizedEpsClosures.containsKey(s))
		{
			return memoizedEpsClosures.get(s);
		}
		// A FIFO queue to perform a nondeterministic breadth-first search
		// of the NFA appertaining unto s.
		LinkedList<NFAState> stateQueue = new LinkedList<NFAState>();
		HashSet<Symbol> rv = new HashSet<Symbol>();
		// Put s in the BFS queue.
		stateQueue.offer(s);
		// For each NFA state encountered:
		while(!stateQueue.isEmpty())
		{
			NFAState front = stateQueue.poll();
			// Add all identifying symbols to the closure.
			rv.addAll(front.getIdentifier());
			// For every state reachable by epsilon transition from the current,
			// if it is not already a subset of the closure, put it in the queue.
			for(NFAState n : front.getTransitions(new CharacterRange(NFAState.EmptyChar)))
			{
				if(!rv.containsAll(n.getIdentifier())) stateQueue.offer(n);
			}
		}
		// DEBUG-X-BEGIN
		// System.err.println("Epsilon-closure of " + s + ": " + rv);
		// DEBUG-X-END
		// Put the memoized closure in the memory.
		memoizedEpsClosures.put(s,rv);
		return rv;
	}
}
