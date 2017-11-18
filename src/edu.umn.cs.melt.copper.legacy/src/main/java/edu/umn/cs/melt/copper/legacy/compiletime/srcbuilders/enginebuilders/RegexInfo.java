package edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.ParsedRegex;

/**
 * Holds information about a terminal symbol for the generator.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class RegexInfo
{
	private ParsedRegex regex;
	private String semanticAction;
	
	public RegexInfo(ParsedRegex regex, String semanticAction)
	throws IndexOutOfBoundsException
	{
		this.regex = regex;
		this.semanticAction = semanticAction;
	}

	/**
	 * @return Returns the regex.
	 */
	public ParsedRegex getRegex()
	{
		return regex;
	}

	/**
	 * @return Returns the semanticAction.
	 */
	public String getSemanticAction()
	{
		return semanticAction;
	}
}
