package edu.umn.cs.melt.copper.compiletime.logging;

/**
 * An interface for log messages.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public interface CompilerLogMessage
{
	/**
	 * The "level" of this message. A logger with a level set to a lower value will not pass on the message.
	 */
	public CompilerLevel getLevel();
	/**
	 * The type of this message. Should be one of the constants declared in {@link CompilerLogMessageType}. 
	 */
	public int getType();
	
	/**
	 * Whether this is an error message.
	 */
	public boolean isError();
	/**
	 * Whether this is a fatal error (i.e., should cause an exception to be thrown immediately when processed).
	 */
	public boolean isFatalError();

	public String toString();
}
