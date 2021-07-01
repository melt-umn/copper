package edu.umn.cs.melt.copper.legacy.compiletime.parsetable;

public class AcceptAction extends ParseAction
{
	public AcceptAction()
	{
		super(ACCEPT_ACTION);
	}
	
	public String toString()
	{
		return "ACCEPT";
	}
	
	public boolean equals(Object rhs)
	{
		if(rhs == null && rhs instanceof AcceptAction) return true;
		else return false;
	}
	
	public int hashCode() { return "a".hashCode(); }

	public <RT, E extends Exception> RT acceptVisitor(ParseActionVisitor<RT, E> visitor)
	throws E
	{
		return visitor.visitAcceptAction(this);
	}
}
