package edu.umn.cs.melt.copper.compiletime.loggingnew.messages;

import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessageType;

public class GenericMessage implements CompilerLogMessage
{
	private CompilerLevel level;
	private String message;
	private boolean isError;
	private boolean isFatalError;
	
	public GenericMessage(CompilerLevel level,String message)
	{
		this(level,message,false,false);
	}
	
	public GenericMessage(CompilerLevel level, String message, boolean isError,	boolean isFatalError)
	{
		this.level = level;
		this.message = message;
		this.isError = isError;
		this.isFatalError = isFatalError;
	}

	@Override
	public CompilerLevel getLevel()
	{
		return level;
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.GENERIC;
	}

	@Override
	public boolean isError()
	{
		return isError;
	}

	@Override
	public boolean isFatalError()
	{
		return isFatalError;
	}
	
	@Override
	public String toString()
	{
		return message;
	}
}
