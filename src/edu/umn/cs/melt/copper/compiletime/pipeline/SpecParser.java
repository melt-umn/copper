package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.IOException;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * The interface for classes performing the specification-parsing task ("skins").
 * @see StandardPipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public interface SpecParser<OUT> extends CustomSwitcher
{
	public OUT parseSpec(SpecParserParameters args)
	throws IOException,CopperException;
}
