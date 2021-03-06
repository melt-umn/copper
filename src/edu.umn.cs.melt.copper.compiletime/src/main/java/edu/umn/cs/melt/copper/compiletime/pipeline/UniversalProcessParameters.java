package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.File;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.main.CopperIOType;

/**
 * Copper input arguments relevant to all three of the "standard" pipeline tasks.
 * @see StandardPipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 *
 */
public interface UniversalProcessParameters
{
	public boolean hasCustomSwitch(String key);
	public Object getCustomSwitch(String key);
	public <T> T getCustomSwitch(String key,Class<T> mustBeType,T defaultValue);
	public CompilerLogger getLogger();
	public CopperIOType getLogType();
	public File getLogFile();
	public PrintStream getLogStream();
	public CompilerLevel getQuietLevel();
	public void setLogger(CompilerLogger logger);
	public String getPackageName();
	public String getParserName();
}
