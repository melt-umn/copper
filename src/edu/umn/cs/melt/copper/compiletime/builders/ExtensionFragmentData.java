package edu.umn.cs.melt.copper.compiletime.builders;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Regex;

import java.io.*;
import java.util.*;

/**
 * @author Kevin Viratyosin
 */
public class ExtensionFragmentData implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3157469499066944639L;
	public LRParseTable appendedExtensionTable; // columns are table offset symbols
    //  No PSSymbolTable since it's generated in ExtensionMappingSpec
    public LRLookaheadAndLayoutSets extensionLookaheadAndLayoutSets;
    public ExtensionMappingSpec extensionMappingSpec;
    public TransparentPrefixes transparentPrefixes; // BitSet of table offset symbols
    public GeneralizedDFA scannerDFA; // DFA for both host and extension symbols
    public SingleScannerDFAAnnotations scannerDFAAnnotations;

    // map from marking terminal extension index to lhs (host index) of bridge production
    public Map<Integer, Integer> markingTerminalLHS;
    // map from marking terminal extension index to state (extension index) to transition to on marking terminal shift
    public Map<Integer, Integer> markingTerminalStates;
    // map from marking terminal extension index to associated Regex
    public Map<Integer, Regex> markingTerminalRegexes;

    // table offset initNTs indexed by extension state
    public BitSet[] initNTs;
    // extension state indexed list of maps: (table offset nt -> set of decomposed (mixed +/-) productions)
    public Map<Integer, Map<Integer, Set<Integer>>> laSources;
}
