package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.IOException;

import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * Encapsulates a "pipeline": a process that takes a set of input arguments, processes it
 * (e.g., by compiling a parser and outputting its source code), and outputs a return
 * code.  
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface Pipeline
{
	/**
	 * Executes the pipeline.
	 * @param args Input arguments.
	 * @return Return code: 0 if successful, non-zero if unsuccessful.
	 */
	public int execute(ParserCompilerParameters args)
	throws IOException,CopperException;
}
