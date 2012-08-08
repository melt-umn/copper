package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;

/**
 * Copper input arguments relevant to all three of the "standard" pipeline tasks.
 * @see StandardPipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface UniversalProcessParameters
{
	public CompilerLogger getLogger();
	public String getLogFile();
	public CompilerLogMessageSort getQuietLevel();
	public void setLogger(CompilerLogger logger);
	public String getPackageDecl();
	public String getParserName();
}
