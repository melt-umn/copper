package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;

public interface UniversalProcessParameters
{
	public CompilerLogger getLogger();
	public String getLogFile();
	public CompilerLogMessageSort getQuietLevel();
	public void setLogger(CompilerLogger logger);
	public String getPackageDecl();
	public String getParserName();
}
