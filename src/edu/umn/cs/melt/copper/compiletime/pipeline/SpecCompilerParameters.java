package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.main.CopperDumpType;

/**
 * Copper input arguments relevant to the parser compilation task.
 * @see StandardPipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface SpecCompilerParameters extends UniversalProcessParameters
{
	public boolean isComposition();
	public boolean isDumpReport();
	public boolean isDumpOnlyOnError();
	public CopperDumpType getDumpType();
	public String getDumpFile();
}
