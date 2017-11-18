package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.util.ArrayList;

import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * Copper input arguments relevant to the specification-parsing task.
 * @see StandardPipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface SpecParserParameters extends UniversalProcessParameters
{
	public ArrayList<Pair<String, Object>> getInputs();
}
