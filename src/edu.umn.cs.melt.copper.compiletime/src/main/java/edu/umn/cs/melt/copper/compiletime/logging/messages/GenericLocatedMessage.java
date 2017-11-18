package edu.umn.cs.melt.copper.compiletime.logging.messages;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLocatedLogMessage;
import edu.umn.cs.melt.copper.runtime.io.Location;

public class GenericLocatedMessage extends GenericMessage implements CompilerLocatedLogMessage<Location>
{
	private Location location;
	
	public GenericLocatedMessage(CompilerLevel level, Location location, String message, boolean isError, boolean isFatalError)
	{
		super(level, message, isError, isFatalError);
		this.location = location;
	}

	public GenericLocatedMessage(CompilerLevel level, Location location, String message)
	{
		super(level, message);
		this.location = location;
	}

	@Override
	public Location getLocation()
	{
		return location;
	}
	
	@Override
	public String toString()
	{
		return location + ": " + super.toString();
	}
}
