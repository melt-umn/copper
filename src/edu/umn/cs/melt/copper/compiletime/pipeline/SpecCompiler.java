package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public interface SpecCompiler<IN, OUT>
{
	public OUT compileParser(IN spec,SpecCompilerParameters args)
	throws CopperException;
}
