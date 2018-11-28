package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.compiletime.builders.ExtensionFragmentData;
import edu.umn.cs.melt.copper.compiletime.builders.HostFragmentData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Viratyosin
 */
public class ParserFragments {
    public HostFragmentData hostFragment;
    public List<ExtensionFragmentData> extensionFragments;

    public ParserFragments() {
        this.hostFragment = null;
        this.extensionFragments = new ArrayList<ExtensionFragmentData>();
    }
}
