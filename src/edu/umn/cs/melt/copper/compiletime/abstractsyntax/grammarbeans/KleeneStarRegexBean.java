package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.util.HashSet;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.RegexBeanVisitor;

/**
 * Holds the Kleene Star of a regex.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class KleeneStarRegexBean extends RegexBean
{
	/** The regex's constituent. */
	private RegexBean subexp;
	
	/**
	 * Constructs an initially empty Kleene star regex.
	 */
	public KleeneStarRegexBean()
	{
		subexp = null;
	}
	
	/**
	 * Constructs a Kleene star regex complete with constituent.
	 * @param subexp The regex's constituent.
	 */
	public KleeneStarRegexBean(RegexBean subexp)
	{
		this.subexp = subexp;
	}
	
	/**
	 * @see #subexp
	 */
	public RegexBean getSubexp()
	{
		return subexp;
	}

	/**
	 * @see #subexp
	 */
	public void setSubexp(RegexBean subexp)
	{
		this.subexp = subexp;
	}	
	
	
	@Override
	public boolean isComplete()
	{
		return subexp != null;
	}

	@Override
	public Set<String> whatIsMissing()
	{
		Set<String> rv = new HashSet<String>();
		if(subexp == null) rv.add("subexp");
		return rv;
	}

	@Override
	public <RT, E extends Exception> RT acceptVisitor(RegexBeanVisitor<RT, E> visitor)
	throws E
	{
		return visitor.visitKleeneStarRegex(this);
	}

}
