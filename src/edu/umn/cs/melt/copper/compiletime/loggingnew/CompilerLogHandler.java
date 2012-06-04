package edu.umn.cs.melt.copper.compiletime.loggingnew;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public interface CompilerLogHandler
{
	public void handleMessage(CompilerLogMessage message);
	
	public void handleErrorMessage(CompilerLogMessage message)
	throws CopperException;
	
	public void flush()
	throws CopperException;
}
