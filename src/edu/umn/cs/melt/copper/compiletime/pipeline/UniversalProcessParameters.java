package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.File;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogger;
import edu.umn.cs.melt.copper.main.CopperIOType;

/**
 * Copper input arguments relevant to all three of the "standard" pipeline tasks.
 * @see StandardPipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface UniversalProcessParameters
{
	public CompilerLogger getLogger();
	public CopperIOType getLogType();
	public File getLogFile();
	public PrintStream getLogStream();
	public CompilerLevel getQuietLevel();
	public void setLogger(CompilerLogger logger);
	public String getPackageDecl();
	public String getParserName();
}
