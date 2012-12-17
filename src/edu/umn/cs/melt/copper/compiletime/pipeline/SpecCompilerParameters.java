package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.File;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.main.CopperDumpType;
import edu.umn.cs.melt.copper.main.CopperIOType;

/**
 * Copper input arguments relevant to the parser compilation task.
 * @see StandardPipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface SpecCompilerParameters extends UniversalProcessParameters
{
	public boolean isRunMDA();
	public boolean isDumpReport();
	public boolean isDumpOnlyOnError();
	public CopperDumpType getDumpFormat();
	public CopperIOType getDumpType();
	public File getDumpFile();
	public PrintStream getDumpStream();
}
