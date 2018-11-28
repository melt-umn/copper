package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

import java.util.HashSet;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.RegexBeanVisitor;

/**
 * Represents an "empty string" regex.
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 *
 */
public class EmptyStringRegex extends Regex
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7647682317354970523L;

	public EmptyStringRegex()
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
