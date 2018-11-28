package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.util.ArrayList;

import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * Copper input arguments relevant to the specification-parsing task.
 * @see StandardPipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 *
 */
public interface SpecParserParameters extends UniversalProcessParameters
{
	public ArrayList<Pair<String, Object>> getInputs();
}
