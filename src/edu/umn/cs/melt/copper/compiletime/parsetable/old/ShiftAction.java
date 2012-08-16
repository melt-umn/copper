package edu.umn.cs.melt.copper.compiletime.parsetable.old;

/**
 * Represents a shift or goto action.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ShiftAction extends ParseAction
{
	private int destState;
	
	public ShiftAction(int destState)
	{
		super(SHIFT_ACTION);
		this.destState = destState;
	}
	
	public int getDestState()
	{
		return destState;
	}
	
	public boolean equals(Object rhs)
	{
		if(rhs instanceof ShiftAction) return (destState == ((ShiftAction) rhs).destState);
		else return false;
	}
	
	public int hashCode()
	{
		return destState;
	}
	
	public String toString()
	{
		return "SHIFT(" + destState + ")";
	}

	public <RT, E extends Exception> RT acceptVisitor(ParseActionVisitor<RT, E> visitor)
	throws E
	{
		return visitor.visitShiftAction(this);
	}
}
