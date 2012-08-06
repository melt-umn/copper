package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.File;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.main.CopperOutputType;

public interface SourceBuilderParameters extends UniversalProcessParameters
{
	public String getRuntimeQuietLevel();
	public PrintStream getOutputStream();
	public File getOutputFile();
	public CopperOutputType getOutputType();
}
