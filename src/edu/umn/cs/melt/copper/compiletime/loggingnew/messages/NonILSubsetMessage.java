package edu.umn.cs.melt.copper.compiletime.loggingnew.messages;

import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessageType;

public class NonILSubsetMessage implements CompilerLogMessage
{
	private int state;
	private int iSubsetOf;	
	
	public NonILSubsetMessage(int state, int iSubsetOf)
	{
		this.state = state;
		this.iSubsetOf = iSubsetOf;
	}

	@Override
	public CompilerLevel getLevel()
	{
		return CompilerLevel.QUIET;
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.NON_IL_SUBSET;
	}

	@Override
	public boolean isError()
	{
		return true;
	}

	@Override
	public boolean isFatalError()
	{
		return false;
	}

	public String toString()
	{
		if(iSubsetOf == -1) return "DFA state " + state + " is a new-host state and not an IL-subset";
		else return "DFA state " + state + " is a new-host state and is an I-subset but not an IL-subset of state " + iSubsetOf;
	}
}
