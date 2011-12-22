package edu.umn.cs.melt.copper.compiletime.logging;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class CopperCompileErrorException extends CopperException
{
	private static final long serialVersionUID = 2791464362730865421L;

	public CopperCompileErrorException()
	{
		super();
	}

	public CopperCompileErrorException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CopperCompileErrorException(String message)
	{
		super(message);
	}

	public CopperCompileErrorException(Throwable cause)
	{
		super(cause);
	}

}
