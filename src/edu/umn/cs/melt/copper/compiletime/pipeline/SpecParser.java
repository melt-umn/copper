package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public interface SpecParser<OUT>
{
	public OUT parseSpec(SpecParserParameters args)
	throws CopperException;
}
