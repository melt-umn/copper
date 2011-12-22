package edu.umn.cs.melt.copper.compiletime.parsetable;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production;

/**
 * Stores a full (right nulled or not) reduce action.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class FullReduceAction extends ParseAction
{

	private Production prod;
	
	public FullReduceAction(Production prod)
	{
		super(FULL_REDUCE_ACTION);
		this.prod = prod;
	}
	
	public Production getProd()
	{
		return prod;
	}

	public boolean equals(Object rhs)
	{
		if(rhs instanceof FullReduceAction) return (prod.equals(((FullReduceAction) rhs).prod));
		else return false;
	}
	
	public int hashCode()
	{
		return prod.hashCode();
	}
	
	public String toString()
	{
		return "REDUCE(" + prod + ")";
	}	

	public <RT, E extends Exception> RT acceptVisitor(ParseActionVisitor<RT, E> visitor)
	throws E
	{
		return visitor.visitFullReduceAction(this);
	}

}
