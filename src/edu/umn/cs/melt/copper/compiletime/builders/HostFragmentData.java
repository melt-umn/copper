package edu.umn.cs.melt.copper.compiletime.builders;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.pipeline.StandardSpecCompilerReturnData;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import java.io.Serializable;
import java.util.*;

/**
 * @author Kevin Viratyosin
 */
public class HostFragmentData implements Serializable {

    public PSSymbolTable symbolTable;
    public ParserSpec fullSpec;
    public String packageDecl;
    public String parserName;
    public LRLookaheadAndLayoutSets lookaheadSets;
    public LRParseTable parseTable;
    public GrammarStatistics stats;
    public TransparentPrefixes prefixes;
    public GeneralizedDFA scannerDFA;
    public SingleScannerDFAAnnotations scannerDFAAnnotations;

    // initNTs indexed by state
    public BitSet[] initNTs;
    // state indexed list of maps: (nt -> set of productions)
    public Map<Integer, Map<Integer, Set<Integer>>> laSources;

    public HostFragmentData(StandardSpecCompilerReturnData returnData, LR0DFA parserDFA) {
        this.symbolTable = returnData.symbolTable;
        this.fullSpec = returnData.fullSpec;
        this.packageDecl = returnData.packageDecl;
        this.parserName = returnData.parserName;
        this.lookaheadSets = returnData.lookaheadSets;
        this.parseTable = returnData.parseTable;
        this.stats = returnData.stats;
        this.prefixes = returnData.prefixes;
        this.scannerDFA = returnData.scannerDFA;
        this.scannerDFAAnnotations = returnData.scannerDFAAnnotations;

        generateMarkingTerminalMetadata(parserDFA);
    }

    private void generateMarkingTerminalMetadata(LR0DFA parserDFA) {
        initNTs = new BitSet[parserDFA.size()];
        laSources = new TreeMap<Integer, Map<Integer, Set<Integer>>>();

        for (int state = 0; state < parserDFA.size(); state++) {
            initNTs[state] = parserDFA.getInitNTs(state);

            // build laSources
            Map<Integer, Set<Integer>> stateLASources = new TreeMap<Integer, Set<Integer>>();
            int items = parserDFA.getItemSet(state).size();
            for (int item = 0; item < items; item++) {
                int production = parserDFA.getItemSet(state).getProduction(item);
                BitSet sources = lookaheadSets.getItemLASources(state, item);
                for (int nt = sources.nextSetBit(0); nt >= 0; nt = sources.nextSetBit(nt + 1)) {
                    if (stateLASources.get(nt) == null) {
                        stateLASources.put(nt, new HashSet<Integer>());
                    }
                    stateLASources.get(nt).add(production);
                }
            }
            laSources.put(state, stateLASources);
        }
    }
}
