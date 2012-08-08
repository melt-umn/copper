package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * The interface for classes performing the source-code conversion task.
 * @see StandardPipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public interface SourceBuilder<IN>
{
	public int buildSource(IN constructs,SourceBuilderParameters args)
	throws CopperException;
}
