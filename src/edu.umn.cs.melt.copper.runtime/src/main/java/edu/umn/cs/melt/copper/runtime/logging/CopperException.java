package edu.umn.cs.melt.copper.runtime.logging;

/**
 * Represents a Copper runtime error in general.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class CopperException extends Exception
{
	private static final long serialVersionUID = 1302727833621043209L;

	public CopperException()
	{
		super();
	}

	public CopperException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CopperException(String message)
	{
		super(message);
	}

	public CopperException(Throwable cause)
	{
		super(cause);
	}
}
