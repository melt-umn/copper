package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

import java.util.HashSet;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.RegexBeanVisitor;

/**
 * Represents a "macro hole" -- a reference to another regex that will be substituted for it.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class MacroHoleRegex extends Regex
{
	/**
	 * A reference to the regex that will be brought in for the substitution
	 * (usually the name of a terminal whose regex will be used).
	 */
	private CopperElementReference macroName;
	
	/**
	 * Creates a new empty macro hole.
	 */
	public MacroHoleRegex()
	{
		macroName = null;
	}
	
	/**
	 * Creates a macro hole complete with regex reference.
	 * @param macroName A reference to the regex that will be brought in for the substitution
	 * (usually the name of a terminal whose regex will be used).
	 */
	public MacroHoleRegex(CopperElementReference macroName)
	{
		this.macroName = macroName;
	}
	
	/**
	 * @see #macroName
	 */
	public CopperElementReference getMacroName()
	{
		return macroName;
	}

	/**
	 * @see #macroName
	 */
	public void setMacroName(CopperElementReference macroName)
	{
		this.macroName = macroName;
	}

	
	@Override
	public boolean isComplete()
	{
		return macroName != null;
	}

	@Override
	public Set<String> whatIsMissing()
	{
		Set<String> rv = new HashSet<String>();
		if(macroName == null) rv.add("macroName");
		return rv;
	}

	@Override
	public <RT, E extends Exception> RT acceptVisitor(RegexBeanVisitor<RT, E> visitor)
	throws E
	{
		return visitor.visitMacroHoleRegex(this);
	}
}
