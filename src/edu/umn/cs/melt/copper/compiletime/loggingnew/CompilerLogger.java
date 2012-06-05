
package edu.umn.cs.melt.copper.compiletime.loggingnew;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * A class for handling logging in parser compilation.
 * 
 * A {@code CompilerLogger} has two constituent objects: a <em>handler</em> (of type {@link CompilerLogHandler})
 * and a <em>level</em> (of type {@link CompilerLevel}).
 * When the logger is sent a message (<em>e.g.</em>, via the {@link #log(CompilerLogMessage)} method),
 * if the message has a level at or above the logger's level (see {@link CompilerLogMessage#getLevel()}
 * the logger will send it on to the handler.
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class CompilerLogger
{
	private CompilerLevel level;
	private CompilerLogHandler handler;
	
	/**
	 * Initializes a CompilerLogger with the given handler and the level {@link CompilerLevel#REGULAR}.
	 */
	public CompilerLogger(CompilerLogHandler handler)
	{
		this(handler,CompilerLevel.REGULAR);
	}
	
	public CompilerLogger(CompilerLogHandler handler,CompilerLevel level)
	{
		this.level = CompilerLevel.REGULAR;
		this.handler = handler;
	}

	/**
	 * Sends the given message through to the logger's handler.
	 */
	public void log(CompilerLogMessage message)
	{
		if(isLoggable(message.getLevel())) handler.handleMessage(message);
	}
	
	/**
	 * Sends the given message, which is understood to be an error message,
	 * through to the logger's handler.
	 * @throws CopperException If a fatal error is among the messages flushed.
	 */
	public void logError(CompilerLogMessage message)
	throws CopperException
	{
		if(isLoggable(message.getLevel()))
		{
			handler.handleErrorMessage(message);
		}
	}
	
	public CompilerLevel getLevel() { return level; }
	public void setLevel(CompilerLevel level) { this.level = level; }
	public CompilerLogHandler getHandler() { return handler; }
	public void setHandler(CompilerLogHandler handler) { this.handler = handler; }
	
	/**
	 * Determines if a message of the given level will be passed through to the
	 * handler (i.e., if the given level is at or greater than the level of the
	 * logger).
	 */
	public boolean isLoggable(CompilerLevel level)
	{
		return (level.ordinal() >= this.level.ordinal());
	}
	
	/**
	 * Tells the logger's handler to "flush" itself (e.g., print out any
	 * messages it has stored).
	 * @throws CopperException If a fatal error is among the messages flushed.
	 */
	public void flush()
	throws CopperException
	{
		handler.flush();
	}
}
