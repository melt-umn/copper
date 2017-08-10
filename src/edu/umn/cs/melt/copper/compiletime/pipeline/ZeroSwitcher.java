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

    public Set<String> getCustomSwitches() {
        return new HashSet<String>();
    }

    public String customSwitchUsage() {
        return "";
    }

    public boolean processCustomSwitch(ParserCompilerParameters args, Pair<String,String> flag) {
        return false;
    }
}
