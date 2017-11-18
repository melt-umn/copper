package edu.umn.cs.melt.copper.runtime.logging;

/**
 * Represents a generic error encountered during a parse's run. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class CopperParserException extends CopperException {

	private static final long serialVersionUID = -485007149121703648L;

	public CopperParserException()
	{
	}

	public CopperParserException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CopperParserException(String message)
	{
		super(message);
	}

	public CopperParserException(Throwable cause)
	{
		super(cause);
	}

}
