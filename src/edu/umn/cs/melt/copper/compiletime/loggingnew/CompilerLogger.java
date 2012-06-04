
package edu.umn.cs.melt.copper.compiletime.loggingnew;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class CompilerLogger
{
	private CompilerLevel level;
	private CompilerLogHandler handler;
	
	public CompilerLogger(CompilerLogHandler handler)
	{
		this.level = CompilerLevel.REGULAR;
		this.handler = handler;
	}
	
	public void log(CompilerLogMessage message)
	{
		if(isLoggable(message.getLevel())) handler.handleMessage(message);
	}
	
	public void logErrorMessage(CompilerLogMessage message)
	throws CopperException
	{
		if(isLoggable(message.getLevel())) handler.handleErrorMessage(message);
	}
	
	public CompilerLevel getLevel() { return level; }
	public void setLevel(CompilerLevel level) { this.level = level; }
	
	public boolean isLoggable(CompilerLevel level)
	{
		return (level.ordinal() >= this.level.ordinal());
	}
	
	public void flush()
	throws CopperException
	{
		handler.flush();
	}
}
