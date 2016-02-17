package edu.umn.cs.melt.copper.compiletime.builders;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.MutableLRParseTable;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Regex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Terminal;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PrecedenceGraph;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Kevin Viratyosin on 12/22/15.
 */
public class ExtensionLRParseTableBuilder {

    public class ExtensionCompilerReturnData {
        public LRParseTable appendedExtensionTable; // columns are table offset symbols
        public PSSymbolTable extensionSymbolTable;
        public LRLookaheadAndLayoutSets extensionLookaheadAndLayoutSets;
        public ExtensionMappingSpec extensionMappingSpec;
        public TransparentPrefixes transparentPrefixes; // BitSet of table offset symbols
        public GeneralizedDFA scannerDFA; // DFA for both host and extension symbols
        public SingleScannerDFAAnnotations scannerDFAAnnotations;
    }

    private ParserSpec fullSpec;
    private LRParseTable fullParseTable;
    private PSSymbolTable fullSymbolTable;
    private LRLookaheadAndLayoutSets fullLookaheadAndLayoutSets;
    private TransparentPrefixes fullPrefixes;

    private int extensionStateCount;

    // composed to decomposed maps
    private ExtensionMappingSpec mappingSpec;

    public static ExtensionCompilerReturnData build(ParserSpec fullSpec, LR0DFA fullDFA, LRParseTable fullParseTable, PSSymbolTable fullSymbolTable, ParserSpec hostSpec, Map<Integer, Integer> hostPartitionMap, BitSet extensionStatePartition, LRLookaheadAndLayoutSets fullLookaheadAndLayoutSets, TransparentPrefixes fullPrefixes) {
        ExtensionLRParseTableBuilder builder = new ExtensionLRParseTableBuilder(fullSpec, fullDFA, fullParseTable, fullSymbolTable, hostSpec, hostPartitionMap, extensionStatePartition, fullLookaheadAndLayoutSets, fullPrefixes);

        ExtensionCompilerReturnData data = builder.build();

        System.out.println("== BEGIN ExtensionLRParseTableBuilder ==");
        System.out.println("Indicies:");
        System.out.println("  host terminals: " + builder.bitSetIndicesToString(builder.mappingSpec.hostTerminalIndices));
        System.out.println("  host nonterminals: " + builder.bitSetIndicesToString(builder.mappingSpec.hostNonterminalIndices));
        System.out.println("  host productions: " + builder.bitSetIndicesToString(builder.mappingSpec.hostProductionIndices));
        System.out.println("  extension terminals: " + builder.bitSetIndicesToString(builder.mappingSpec.extensionTerminalIndices));
        System.out.println("  extension nonterminals: " + builder.bitSetIndicesToString(builder.mappingSpec.extensionNonterminalIndices));
        System.out.println("  extension productions: " + builder.bitSetIndicesToString(builder.mappingSpec.extensionProductionIndices));

        System.out.println("Maps:");
        System.out.println("  composed to decomposed symbols:");
        builder.printPartitionMap(builder.mappingSpec.composedToDecomposedSymbols);
        System.out.println("  extension state num to composed state num:");
        builder.printPartitionMap(builder.mappingSpec.extensionToComposedStates);
        System.out.println("  composed to decomposed state map:");
        builder.printPartitionMap(builder.mappingSpec.composedToDecomposedStates);

        System.out.println("Appended Extension Parse Table");
        data.appendedExtensionTable.print();

        System.out.println("== END ExtensionLRParseTableBuilder ==");

        return data;
    }

    private String bitSetIndicesToString(BitSet bitSet) {
        String ret = "";
        for (int i = bitSet.nextSetBit(0); i != -1; i = bitSet.nextSetBit(i + 1)) {
            ret += i + ", ";
        }
        return ret;
    }

    private void printPartitionMap(Map<Integer, Integer> map) {
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println("    " + entry.getKey() + " -> " + entry.getValue());
        }
    }

    private ExtensionLRParseTableBuilder(
                ParserSpec fullSpec,
                LR0DFA fullDFA,
                LRParseTable fullParseTable,
                PSSymbolTable fullSymbolTable,
                ParserSpec hostSpec,
                Map<Integer, Integer> hostPartitionMap,
                BitSet extensionStatePartition,
                LRLookaheadAndLayoutSets fullLookaheadAndLayoutSets,
                TransparentPrefixes fullPrefixes
    ) {
        this.fullSpec = fullSpec;
        this.fullParseTable = fullParseTable;
        this.fullSymbolTable = fullSymbolTable;
        this.fullLookaheadAndLayoutSets = fullLookaheadAndLayoutSets;
        this.fullPrefixes = fullPrefixes;
        this.extensionStateCount = extensionStatePartition.cardinality();

        this.mappingSpec = new ExtensionMappingSpec(fullSpec, fullSymbolTable, hostSpec, hostPartitionMap, extensionStatePartition);
    }

    private ExtensionCompilerReturnData build() {
        ExtensionCompilerReturnData data = new ExtensionCompilerReturnData();

        data.extensionMappingSpec = mappingSpec;

        this.generateExtensionSymbolTable(data, mappingSpec);
        this.generateExtensionLookaheadAndLayout(data, mappingSpec);
        this.generateExtensionParseTables(data, mappingSpec);
        this.generateExtensionTransparentPrefixes(data, mappingSpec);
        this.generateScannerDFA(data, mappingSpec);
        this.generateScannerDFAAnnotations(data, mappingSpec);

        // TODO others

        return data;
    }

    // TODO Why do I have this and the same thing in ExtensionMappingSpec?
    private void generateExtensionSymbolTable(ExtensionCompilerReturnData data, ExtensionMappingSpec mappingSpec) {
        ArrayList<CopperASTBean> beans = new ArrayList<CopperASTBean>();

        // TODO populate beans in order based on mappingSpec
        for (Map.Entry<Integer, Integer> entry : mappingSpec.composedToDecomposedSymbols.entrySet()) {
            int decomposedIndex = entry.getValue();
            int composedIndex = entry.getKey();
            if (decomposedIndex < 0) { // is extension symbol?
                int extensionSymbolIndex = ExtensionMappingSpec.decodeExtensionIndex(decomposedIndex);
                CopperASTBean composedBean = fullSymbolTable.get(composedIndex);
                // Note, the beans don't need to be modified since they don't contain index information, just names
                beans.add(extensionSymbolIndex, composedBean);
                // TODO ? create an accompanying traditional ParserSpec ?
            }
        }

        PSSymbolTable symbolTable = new PSSymbolTable(beans);
        data.extensionSymbolTable = symbolTable;
    }

    private void generateExtensionLookaheadAndLayout(ExtensionCompilerReturnData data, ExtensionMappingSpec mappingSpec) {
        int maxItemCount = fullLookaheadAndLayoutSets.getMaxItemCount();
        LRLookaheadAndLayoutSets extensionSets = new LRLookaheadAndLayoutSets(extensionStateCount, maxItemCount);
        for (int extState = 0; extState < extensionStateCount; extState++) {
            int composedState = mappingSpec.extensionToComposedStates.get(extState);
            mappingSpec.translateSymbolBitSetWithOffset(fullLookaheadAndLayoutSets.getLayout(composedState), extensionSets.getLayout(extState));
            for (int item = 0; item < maxItemCount; item++) {
                if (fullLookaheadAndLayoutSets.getLookahead(composedState, item) != null) {
                    mappingSpec.translateSymbolBitSetWithOffset(fullLookaheadAndLayoutSets.getLookahead(composedState, item), extensionSets.getLookahead(extState, item));
                }
                if (fullLookaheadAndLayoutSets.getItemLASources(composedState, item) != null) {
                    mappingSpec.translateSymbolBitSetWithOffset(fullLookaheadAndLayoutSets.getItemLASources(composedState, item), extensionSets.getItemLASources(extState, item));
                }
            }
        }
        data.extensionLookaheadAndLayoutSets = extensionSets;
    }

    private void generateExtensionTransparentPrefixes(ExtensionCompilerReturnData data, ExtensionMappingSpec mappingSpec) {
        LRParseTable parseTable = data.appendedExtensionTable;
        int offsetTerminalsLength = mappingSpec.offsetExtensionIndex(mappingSpec.extensionTerminalIndices.length() - 1);
        TransparentPrefixes prefixes = new TransparentPrefixes(offsetTerminalsLength, parseTable.size());

        for (Map.Entry<Integer, Integer> entry : mappingSpec.extensionToComposedStates.entrySet()) {
            int extensionState = entry.getKey();
            int composedState = entry.getValue();

            mappingSpec.translateSymbolBitSetWithTableOffset(fullPrefixes.getPrefixes(composedState), prefixes.getPrefixes(extensionState));

            for (int t = 0; t < fullSpec.terminals.length(); t++) {
                BitSet originalFollowingTerminals = fullPrefixes.getFollowingTerminals(composedState, t);
                if (originalFollowingTerminals != null) {
                    int translatedT = mappingSpec.translateAndTableOffsetComposedSymbol(t);
                    prefixes.initializePrefixMap(extensionState, translatedT);
                    mappingSpec.translateSymbolBitSetWithTableOffset(originalFollowingTerminals, prefixes.getFollowingTerminals(extensionState, translatedT));
                }
            }
        }

        data.transparentPrefixes = prefixes;
    }

    // Returns partial parse table
    // only states are extension states
    // uses all symbols
    // extension states as actions parameters are referred to in negative, e.g.
    //   parameter | referenced state
    //   0         | host 0
    //   1         | host 1
    //   -1        | extension 0
    //   -2        | extension 1
    private void generateExtensionParseTables(ExtensionCompilerReturnData data, ExtensionMappingSpec mappingSpec) {
        int fullSpecSymbolCount = Math.max(fullSpec.terminals.length(), fullSpec.nonterminals.length());
        int extensionStateCount = mappingSpec.composedExtensionStates.cardinality();

        int hostColumnCount = mappingSpec.extensionSymbolTableOffset;
        int extensionColumnCount = Math.max(mappingSpec.extensionTerminalIndices.length(), mappingSpec.extensionNonterminalIndices.length());

        MutableLRParseTable appendedExtensionTable = new MutableLRParseTable(extensionStateCount, hostColumnCount + extensionColumnCount);

        for (int i = 0; i < extensionStateCount; i++) {
            int composedStateNumber = mappingSpec.extensionToComposedStates.get(i);
            for (int symbol = 0; symbol < fullSpecSymbolCount; symbol++) {
                int convertedSymbol = mappingSpec.translateAndTableOffsetComposedSymbol(symbol);

                byte actionType = fullParseTable.getActionType(composedStateNumber, symbol);
                int composedActionParameter = fullParseTable.getActionParameter(composedStateNumber, symbol);
                int extensionActionParameter = translateActionParameter(actionType, composedActionParameter);

                appendedExtensionTable.getValidLA(i).set(convertedSymbol);
                appendedExtensionTable.setActionType(i, convertedSymbol, actionType);
                appendedExtensionTable.setActionParameter(i, convertedSymbol, extensionActionParameter);
            }
        }

        data.appendedExtensionTable = appendedExtensionTable;
    }

    private int translateActionParameter(byte actionType, int composedActionParameter) {
        switch (actionType) {
            case LRParseTable.CONFLICT:
                // TODO Covert conflict number?
                return 0;
            case LRParseTable.REDUCE: // convert production
                return mappingSpec.composedToDecomposedSymbols.get(composedActionParameter);
            case LRParseTable.SHIFT: // Same as GOTO (SHIFT == GOTO)
                return mappingSpec.composedToDecomposedStates.get(composedActionParameter);
            default:
                return 0;
        }
    }

    private void generateScannerDFA(ExtensionCompilerReturnData data, ExtensionMappingSpec mappingSpec) {
        TreeMap<Integer, Regex> regexes = new TreeMap<Integer, Regex>();
        BitSet appendedTerminals = new BitSet();
        for (int i = fullSpec.terminals.nextSetBit(0); i >= 0; i = fullSpec.terminals.nextSetBit(i + 1)) {
            Regex regex = fullSpec.t.getRegex(i);
            int oi = mappingSpec.translateAndTableOffsetComposedSymbol(i);
            appendedTerminals.set(oi);
            regexes.put(oi, regex);
        }
        int translatedEOF = mappingSpec.translateAndTableOffsetComposedSymbol(fullSpec.getEOFTerminal());
        data.scannerDFA = SingleScannerDFABuilder.build(regexes, appendedTerminals, translatedEOF);
    }

    private void generateScannerDFAAnnotations(ExtensionCompilerReturnData data, ExtensionMappingSpec mappingSpec) {
        int appendedTerminalsLength = mappingSpec.extensionSymbolTableOffset + mappingSpec.extensionTerminalIndices.length();
        PrecedenceGraph precedenceGraph = new PrecedenceGraph(appendedTerminalsLength);
        int composedTerminalsLength = fullSpec.terminals.length();

        for (int i = 0; i < composedTerminalsLength; i++) {
            int oi = mappingSpec.translateAndTableOffsetComposedSymbol(i);
            for (int j = 0; j < composedTerminalsLength; j++) {
                if (fullSpec.t.precedences.hasEdge(i, j)) {
                    int oj = mappingSpec.translateAndOffsetComposedSymbol(j);
                    precedenceGraph.addEdge(oi, oj);
                }
            }
        }

        SingleScannerDFAAnnotations annotations = SingleScannerDFAAnnotationBuilder.build(precedenceGraph, data.scannerDFA);
        data.scannerDFAAnnotations = annotations;
    }
}
