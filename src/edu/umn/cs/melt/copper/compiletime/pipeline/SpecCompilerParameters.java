package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.File;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.main.CopperDumpControl;
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
	public boolean isWarnUselessNTs();
	public CopperDumpControl getDump();
	public CopperDumpType getDumpFormat();
	public CopperIOType getDumpOutputType();
	public File getDumpFile();
	public PrintStream getDumpStream();
}
