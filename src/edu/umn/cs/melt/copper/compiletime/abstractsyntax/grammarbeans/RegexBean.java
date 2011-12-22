package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.RegexBeanVisitor;

/**
 * Represents a regular expression.
 * @author schwerdf
 *
 */
public abstract class RegexBean
{
	protected RegexBean()
	{
	}

	public abstract boolean isComplete();
	public abstract Set<String> whatIsMissing();
	
	public abstract <RT, E extends Exception> RT acceptVisitor(RegexBeanVisitor<RT, E> visitor)
	throws E;
}
