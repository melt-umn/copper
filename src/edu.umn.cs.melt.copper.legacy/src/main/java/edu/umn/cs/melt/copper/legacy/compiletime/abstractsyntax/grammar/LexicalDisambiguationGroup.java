package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar;

import java.util.HashSet;




/**
 * Holds the information appertaining to a lexical disambiguation group.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LexicalDisambiguationGroup extends TerminalClassDirectory
{
	private String disambigCode;
	
	public LexicalDisambiguationGroup(TerminalClass name,HashSet<Terminal> members,String disambigCode)
	{
		super(name,members);
		this.disambigCode = disambigCode;
	}

	/**
	 * @return Returns the disambigCode.
	 */
	public String getDisambigCode()
	{
		return disambigCode;
	}

	public boolean equals(Object rhs)
	{
		if(rhs != null && rhs instanceof LexicalDisambiguationGroup)
		{
			return members.equals(((LexicalDisambiguationGroup) rhs).members);
		}
		else return false;
	}
	
	public String toString()
	{
		return "(Disambiguation code among " + members + ": { " + disambigCode + " })";
	}
	
}
