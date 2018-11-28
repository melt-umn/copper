package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

import java.io.Serializable;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.RegexBeanVisitor;

/**
 * Represents a regular expression.
 * @author @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 * @author Kevin Viratyosin
 *
 * Modified by Kevin to implement Serializable
 */
public abstract class Regex implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5753105739767763666L;

	protected Regex()
	{
	}

	public abstract boolean isComplete();
	public abstract Set<String> whatIsMissing();
	
	public abstract <RT, E extends Exception> RT acceptVisitor(RegexBeanVisitor<RT, E> visitor)
	throws E;
}
