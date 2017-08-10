package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.util.Set;

import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * The interface for classes containing custom switches
 * @author Patrick Stephen
 */
public interface CustomSwitcher
{
	/**
	 * Returns a set of "custom" switches accepted by this switcher.
	 */
	public Set<String> getCustomSwitches();
	
	/**
	 * Returns a summary of the "custom" switches accepted by this switcher,
	 * to be printed when the "usage" message is displayed.
	 */
	public String customSwitchUsage();

	/**
	 * Processes a "custom" parameter from the command line, converting it
	 * into an entry in the given object for input arguments.
	 * @param args The object in which the processed custom switch will be placed.
	 * @param flag The parsed flag and value given to said flag
	 * @return Whether this flag was recognized by this custom switcher
	 */
	public boolean processCustomSwitch(ParserCompilerParameters args, Pair<String,String> flag);
	
}
