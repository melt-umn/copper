package edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa;

import java.util.BitSet;

/**
 * Holds item sets (without lookahead) and transitions for an LR DFA. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LR0DFA
{
	/** The item set for each state. */
	protected LR0ItemSet[] itemSets;
	/** The set of transitions leading out of each state. */
	protected BitSet[] transitionLabels;
	/** The DFA's transition function. */ 
	protected int[][] transitions;
	/**
	 * The set of "goto items" for each state and transition. {@code gotoItems[s][X]}
	 * contains the indices of items in state {@code s} with symbol {@code X} immediately
	 * to the right of their bullet points. 
	 */
	protected BitSet[][] gotoItems;
	
	/**
	 * Builds an LR0DFA object. 
	 * 
	 * @param itemSets The item set for each state.
	 * @param transitionLabels The set of transitions leading out of each state.
	 * @param transitions The DFA's transition function.
	 * @param gotoItems The set of "goto items" for each state and transition.
	 */
	public LR0DFA(LR0ItemSet[] itemSets,BitSet[] transitionLabels,int[][] transitions,BitSet[][] gotoItems)
	{
		this.itemSets = itemSets;
		this.transitionLabels = transitionLabels;
		this.transitions = transitions;
		this.gotoItems = gotoItems;
	}
	
	public int size() { return itemSets.length; }
	
	public final LR0ItemSet getItemSet(int state) 		   { return itemSets[state]; }
	public final BitSet getTransitionLabels(int state)     { return transitionLabels[state]; }
	public final int getTransition(int state,int symbol)   { return transitions[state][symbol]; }
	public final BitSet getGotoItems(int state,int symbol) { return gotoItems[state][symbol]; }
}
