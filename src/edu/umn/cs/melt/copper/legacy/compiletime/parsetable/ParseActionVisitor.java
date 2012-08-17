package edu.umn.cs.melt.copper.legacy.compiletime.parsetable;

/**
 * Interface for visitors to a parse action.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 * @param <RT> The type of values returned by the visitor's methods.
 * @param <E> The type of exceptions thrown by the visitor's methods.
 */
public interface ParseActionVisitor<RT,E extends Exception>
{
	public RT visitAcceptAction(AcceptAction action) throws E;
	public RT visitFullReduceAction(FullReduceAction action) throws E;
	public RT visitShiftAction(ShiftAction action) throws E;
}
