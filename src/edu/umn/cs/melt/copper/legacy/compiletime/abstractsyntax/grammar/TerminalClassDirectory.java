package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar;

import java.util.HashSet;


public class TerminalClassDirectory
{
	protected TerminalClass name;
	protected HashSet<Terminal> members;

	public TerminalClassDirectory(TerminalClass name,HashSet<Terminal> members)
	{
		this.name = name;
		this.members = members;
	}

	public TerminalClass getName()
	{
		return name;
	}
	
	/**
	 * @return Returns the members.
	 */
	public HashSet<Terminal> getMembers()
	{
		return members;
	}
	
	public boolean addMember(Terminal member)
	{
		return members.add(member);
	}

	public boolean equals(Object rhs)
	{
		if(rhs != null && rhs instanceof TerminalClassDirectory)
		{
			return name.equals(((TerminalClassDirectory) rhs).name) &&
			       members.equals(((TerminalClassDirectory) rhs).members);
		}
		else return false;
	}
	
	public int hashCode()
	{
		return members.hashCode();
	}

	public String toString()
	{
		return "Terminal class '" + name + "': " + members;
	}
}
