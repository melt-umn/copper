package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public interface SourceBuilder<IN>
{
	public int buildSource(IN constructs,SourceBuilderParameters args)
	throws CopperException;
}
