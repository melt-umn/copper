package edu.umn.cs.melt.copper.runtime.logging;

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
