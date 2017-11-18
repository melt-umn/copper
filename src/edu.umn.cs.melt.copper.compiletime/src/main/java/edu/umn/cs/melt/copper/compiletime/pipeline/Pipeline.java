package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.IOException;
import java.util.Set;

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
	
	/**
	 * Returns a set of "custom" switches accepted by this pipeline.
	 */
	public Set<String> getCustomSwitches();
	
	/**
	 * Returns a summary of the "custom" switches accepted by this pipeline,
	 * to be printed when the "usage" message is displayed.
	 */
	public String customSwitchUsage();

	/**
	 * Processes a "custom" switch from the command line, converting it
	 * into an entry in the given object for input arguments. 
	 * @param args The object in which the processed custom switch will be placed.
	 * @param cmdline The full command line.
	 * @param index The array index at which the custom switch starts.
	 * @return The array index immediately following the whole custom switch (e.g., {@code index+1} for
	 *         a plain boolean switch), or -1 if the parameter was not recognized by this pipeline.
	 */
	public int processCustomSwitch(ParserCompilerParameters args,String[] cmdline,int index);
}
