package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

import java.util.Set;
import java.util.HashSet;

/**
 * A zero switcher returns zero values when queried for its custom switches.
 * @author Patrick Stephen
 */
public abstract class ZeroSwitcher implements CustomSwitcher {

    /**
	 * Returns a set of "custom" switches accepted by this switcher. 
     * As a zero switcher, this returns an empty set.
	 */
    public Set<String> getCustomSwitches() {
        return new HashSet<String>();
    }

    /**
	 * Returns a summary of the "custom" switches accepted by this switcher,
	 * to be printed when the "usage" message is displayed. As a zero switcher,
     * this returns an empty string.
	 */
    public String customSwitchUsage() {
        return "";
    }

    /**
	 * Processes a "custom" parameter from the command line, converting it
	 * into an entry in the given object for input arguments. As a zero switcher,
     * this accepts no flags and returns false. 
	 * @param args The object in which the processed custom switch will be placed.
	 * @param flag The parsed flag and value given to said flag
	 * @return Whether this flag was recognized by this custom switcher
	 */
    public boolean processCustomSwitch(ParserCompilerParameters args, Pair<String,String> flag) {
        return false;
    }
}
