package edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSymbol;

public interface LALRTransition<ITEM,STATE extends LALRState<ITEM>>
{
	/**
	 * Gets the destination state of this transition. 
	 * @return The destination state.
	 */
	public STATE getDest();

	/**
	 * Gets the label of this transition.
	 * @return The label.
	 */
	public GrammarSymbol getLabel();

	/**
	 * Gets the source state of this transition. 
	 * @return The source state.
	 */
	public STATE getSrc();

	public boolean equals(Object rhs);

	public int hashCode();
}
