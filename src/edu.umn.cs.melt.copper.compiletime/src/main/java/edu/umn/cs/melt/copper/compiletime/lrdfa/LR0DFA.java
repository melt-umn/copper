package edu.umn.cs.melt.copper.compiletime.lrdfa;

import java.util.BitSet;

/**
 * Holds item sets (without lookahead) and transitions for an LR DFA. 
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 * @author Kevin Viratyosin
 *
 * Modified by Kevin to included initNTs
 */
public class LR0DFA
{
	/** The item set for each state. */
	protected LR0ItemSet[] itemSets;
	/** The set of transitions leading out of each state. Each index is a symbol that leads to a transition. */
	protected BitSet[] transitionLabels;
	/** The DFA's transition function/GOTO table */
	protected int[][] transitions;
	/**
	 * The set of "goto items" for each state and transition. {@code gotoItems[s][X]}
	 * contains the indices of items in state {@code s} with symbol {@code X} immediately
	 * to the right of their bullet points. 
	 */
	protected BitSet[][] gotoItems;

	/* The set of LHS nonterminals of a state's items */
	protected BitSet[] initNTs;
	
	/**
	 * Builds an LR0DFA object. 
	 * 
	 * @param itemSets The item set for each state.
	 * @param transitionLabels The set of transitions leading out of each state.
	 * @param transitions The DFA's transition function.
	 * @param gotoItems The set of "goto items" for each state and transition.
	 */
	public LR0DFA(LR0ItemSet[] itemSets, BitSet[] transitionLabels, int[][] transitions, BitSet[][] gotoItems, BitSet[] initNTs)
	{
		this.itemSets = itemSets;
		this.transitionLabels = transitionLabels;
		this.transitions = transitions;
		this.gotoItems = gotoItems;
		this.initNTs = initNTs;
	}

	/**number of states in the DFA */
	public int size() { return itemSets.length; }

	public final LR0ItemSet getItemSet(int state) 		   { return itemSets[state]; }
	public final BitSet getTransitionLabels(int state)     { return transitionLabels[state]; }
	public final int getTransition(int state,int symbol)   { return transitions[state][symbol]; }
	public final int getTransitionLength()                 { return transitions.length; }
	public final BitSet getGotoItems(int state,int symbol) { return gotoItems[state][symbol]; }
	public final BitSet getInitNTs(int state) { return initNTs[state]; }
}
