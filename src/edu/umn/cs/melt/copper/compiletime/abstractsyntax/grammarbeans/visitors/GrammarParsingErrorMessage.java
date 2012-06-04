package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLocatedLogMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessageType;
import edu.umn.cs.melt.copper.runtime.io.Location;

public class GrammarParsingErrorMessage implements CompilerLocatedLogMessage<Location>
{
	private GrammarError errorMessage;
	
	public GrammarParsingErrorMessage(GrammarError errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	@Override
	public CompilerLevel getLevel()
	{
		return CompilerLevel.QUIET;
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.GRAMMAR_ERROR;
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

	@Override
	public Location getLocation()
	{
		return errorMessage.getLocation();
	}
	
	public String toString()
	{
		return errorMessage.toString();
	}
}
