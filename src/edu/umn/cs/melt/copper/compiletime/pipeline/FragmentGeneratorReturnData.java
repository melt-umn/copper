package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.compiletime.builders.ExtensionFragmentData;
import edu.umn.cs.melt.copper.compiletime.builders.HostFragmentData;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.GrammarStatistics;

/**
 * @author Kevin Viratyosin
 */
public class FragmentGeneratorReturnData {
    public boolean isExtensionFragmentData;
    public ExtensionFragmentData extensionFragmentData;
    public HostFragmentData hostFragmentData;
    public GrammarStatistics stats;
}
