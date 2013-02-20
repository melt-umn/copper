package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.IOException;
import java.util.Set;

import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * The interface for classes performing the specification-parsing task ("skins").
 * @see StandardPipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public interface SpecParser<OUT>
{
	public OUT parseSpec(SpecParserParameters args)
	throws IOException,CopperException;
	
	/**
	 * Returns a set of "custom" switches accepted by this spec parser.
	 */
	public Set<String> getCustomSwitches();
	
	/**
	 * Returns a summary of the "custom" switches accepted by this spec parser,
	 * to be printed when the "usage" message is displayed.
	 */
	public String customSwitchUsage();

	/**
	 * Processes a "custom" parameter from the command line, converting it
	 * into an entry in the given object for input arguments.
	 * @param args The object in which the processed custom switch will be placed.
	 * @param cmdline The full command line.
	 * @param index The array index at which the custom switch starts.
	 * @return The array index immediately following the whole custom switch (e.g., {@code index+1} for
	 *         a plain boolean switch), or -1 if the parameter was not recognized by this spec parser.
	 */
	public int processCustomSwitch(ParserCompilerParameters args,String[] cmdline,int index);
	
}
