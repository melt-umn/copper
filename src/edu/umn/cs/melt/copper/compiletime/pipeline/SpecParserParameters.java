package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.Reader;
import java.util.ArrayList;

import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

public interface SpecParserParameters extends UniversalProcessParameters
{
	public ArrayList<Pair<String, Reader>> getFiles();
}
