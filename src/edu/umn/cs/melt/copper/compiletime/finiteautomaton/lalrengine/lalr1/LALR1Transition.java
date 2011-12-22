package edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.lalr1;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.LALRTransition;

/**
 * Represents a transition in a LALR(1) DFA.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LALR1Transition implements LALRTransition<LALR1StateItem,LALR1State>
{
	private LALR1State src,dest;
	private GrammarSymbol label;
	
	public LALR1Transition(LALR1State src,GrammarSymbol label,LALR1State dest)
	{
		this.src = src;
		this.dest = dest;
		this.label = label;
	}

	/**
	 * Gets the destination state of this transition. 
	 * @return The destination state.
	 */
	public LALR1State getDest()
	{
		return dest;
	}

	/**
	 * Gets the label of this transition.
	 * @return The label.
	 */
	public GrammarSymbol getLabel()
	{
		return label;
	}

	/**
	 * Gets the source state of this transition. 
	 * @return The source state.
	 */
	public LALR1State getSrc()
	{
		return src;
	}
	
	public boolean equals(Object rhs)
	{
		if(rhs instanceof LALR1Transition)
		{
			LALR1Transition trhs = (LALR1Transition) rhs;
			return src.equals(trhs.src) &&
			       label.equals(trhs.label) &&
			       dest.equals(trhs.dest);
		}
		else return false;
	}
	
	public int hashCode()
	{
		String rvStr = src.hashCode() + " " + label.hashCode() + " " + dest.hashCode();
		return rvStr.hashCode();
	}
}
