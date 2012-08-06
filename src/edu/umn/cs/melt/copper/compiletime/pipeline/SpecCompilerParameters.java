package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.main.CopperDumpType;

public interface SpecCompilerParameters extends UniversalProcessParameters
{
	public boolean isComposition();
	public boolean isDumpReport();
	public boolean isDumpOnlyOnError();
	public CopperDumpType getDumpType();
	public String getDumpFile();
}
