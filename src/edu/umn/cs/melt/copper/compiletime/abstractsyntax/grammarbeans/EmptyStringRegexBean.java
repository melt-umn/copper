package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.util.HashSet;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.RegexBeanVisitor;

/**
 * Represents an "empty string" regex.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class EmptyStringRegexBean extends RegexBean
{
	public EmptyStringRegexBean()
	{
	}
	
	
	@Override
	public boolean isComplete()
	{
		return true;
	}

	@Override
	public Set<String> whatIsMissing()
	{
		return new HashSet<String>();
	}

	@Override
	public <RT, E extends Exception> RT acceptVisitor(RegexBeanVisitor<RT, E> visitor)
	throws E
	{
		return visitor.visitEmptyStringRegex(this);
	}

}
