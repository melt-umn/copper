package edu.umn.cs.melt.copper.legacy.compiletime.logging;

import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public class FatalCompileErrorException extends RuntimeException
{
	private static final long serialVersionUID = 3644639690364953652L;

	private InputPosition location;
	
	public FatalCompileErrorException() {
		super();
		location = null;
	}

	public FatalCompileErrorException(String message, Throwable cause) {
		super(message, cause);
		location = null;
	}

	public FatalCompileErrorException(String message) {
		super(message);
		location = null;
	}

	public FatalCompileErrorException(Throwable cause) {
		super(cause);
		location = null;
	}
	
	public FatalCompileErrorException(String message,InputPosition location)
	{
		super(message);
		this.location = location;
	}

	public InputPosition getLocation() {
		return location;
	}

	public void setLocation(InputPosition location) {
		this.location = location;
	}

}
