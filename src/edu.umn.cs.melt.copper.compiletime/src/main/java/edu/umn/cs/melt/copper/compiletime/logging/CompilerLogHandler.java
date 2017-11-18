package edu.umn.cs.melt.copper.compiletime.logging;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * An interface for {@link CompilerLogger} objects to pass log
 * messages.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface CompilerLogHandler
{
	public void handleMessage(CompilerLogMessage message);
	
	public void handleErrorMessage(CompilerLogMessage message)
	throws CopperException;
	
	public void flush()
	throws CopperException;
}
