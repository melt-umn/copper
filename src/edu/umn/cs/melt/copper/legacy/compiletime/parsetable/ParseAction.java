package edu.umn.cs.melt.copper.legacy.compiletime.parsetable;

/**
 * Superclass for all types of parse actions.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class ParseAction implements Comparable<ParseAction>
{
	public static final int ACCEPT_ACTION = 0;
	public static final int SHIFT_ACTION = 1;
	public static final int FULL_REDUCE_ACTION = 2;
	public static final int BINARY_REDUCE_ACTION = 3;
	protected int peckingPos;
	
	protected ParseAction(int peckingPos)
	{
		this.peckingPos = peckingPos;
	}

	public final boolean isAcceptAction()       { return peckingPos == ACCEPT_ACTION; }
	public final boolean isShiftAction()        { return peckingPos == SHIFT_ACTION; }
	public final boolean isFullReduceAction()   { return peckingPos == FULL_REDUCE_ACTION; }
	public final boolean isBinaryReduceAction() { return peckingPos == BINARY_REDUCE_ACTION; }
	
	public final int compareTo(ParseAction rhs)
	{
		return (peckingPos + "-" + toString()).compareTo(rhs.peckingPos + "-" + rhs.toString());
	}
	
	public abstract boolean equals(Object rhs);
	
	public abstract int hashCode();
	
	public abstract String toString();
	
	public abstract <RT, E extends Exception> RT acceptVisitor(ParseActionVisitor<RT,E> visitor) throws E;
}
