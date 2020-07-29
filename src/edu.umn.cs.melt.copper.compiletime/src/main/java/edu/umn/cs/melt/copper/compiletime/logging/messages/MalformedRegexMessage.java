package edu.umn.cs.melt.copper.compiletime.logging.messages;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLocatedLogMessage;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Terminal;
import edu.umn.cs.melt.copper.runtime.io.Location;

public class MalformedRegexMessage implements CompilerLocatedLogMessage<Location>
{
	private Terminal t;
	
	public MalformedRegexMessage(Terminal t)
	{
		this.t = t;
	}

	@Override
	public CompilerLevel getLevel()
	{
		return CompilerLevel.QUIET;
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.MALFORMED_REGEX;
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
		return t.getLocation();
	}
	
	public String toString()
	{
		return getLocation() + ": regex for terminal '" + t.getDisplayName() + "' is malformed";
	}

}
