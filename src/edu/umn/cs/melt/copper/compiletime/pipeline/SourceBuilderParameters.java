package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.File;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.main.CopperOutputType;

/**
 * Copper input arguments relevant to the source-code conversion task.
 * @see StandardPipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface SourceBuilderParameters extends UniversalProcessParameters
{
	public String getRuntimeQuietLevel();
	public PrintStream getOutputStream();
	public File getOutputFile();
	public CopperOutputType getOutputType();
}
