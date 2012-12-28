package edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa;

import java.util.HashSet;

/**
 * Holds a non-deterministic finite automaton
 * (may be a deterministic finite automaton, by definition).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class NFA
{
	private HashSet<NFAState> states;
	private HashSet<NFAState> acceptStates;
	private NFAState startState;
	
	/**
	 * Creates a new instance of NFA.
	 * @param states The new NFA's states.
	 * @param startState The start state (assumed to be a member of <CODE>states</CODE>).
	 */
	public NFA(HashSet<NFAState> states,NFAState startState)
	{
		this.states = new HashSet<NFAState>();
		this.acceptStates = new HashSet<NFAState>();
		this.startState = startState;
		for(NFAState s : states)
		{
			this.states.add(s);
			if(!s.getAccepts().isEmpty()) this.acceptStates.add(s);
		}
	}

	/**
	 * @return Returns the acceptStates.
	 */
	public HashSet<NFAState> getAcceptStates()
	{
		return acceptStates;
	}

	/**
	 * @return Returns the startState.
	 */
	public NFAState getStartState()
	{
		return startState;
	}

	/**
	 * @return Returns the states.
	 */
	public HashSet<NFAState> getStates()
	{
		return states;
	}
	
	public String toString()
	{
		String rv = "NFA -- Start state " + startState.getIdentifier() + ", states:\n";
		for(NFAState s : states) rv += " " + s + "\n";
		return rv;
	}
}
