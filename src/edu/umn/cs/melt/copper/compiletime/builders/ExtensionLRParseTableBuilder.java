package edu.umn.cs.melt.copper.compiletime.builders;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.MutableLRParseTable;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import java.util.BitSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kviratyosin on 12/22/15.
 */
public class ExtensionLRParseTableBuilder {

    public class ExtensionCompilerReturnData {
        public LRParseTable hostSideTable;
        public LRParseTable extensionSideTable;
        public PSSymbolTable extensionSymbolTable;
        // public ExtensionParserSpec extensionParserSpec; // to be defined
    }

    public class ExtensionMappingSpec {
        public Map<Integer, Integer>
            composedToDecomposedStates, extensionToComposedStates,
            composedToDecomposedTerminals, composedToDecomposedNonterminals, composedToDecomposedProductions;
        public BitSet
            composedExtensionStates,
            extensionTerminalIndices, extensionNonterminalIndices, extensionProductionIndices;
        public BitSet
            hostTerminalIndices, hostNonterminalIndices, hostProductionIndices;

        public ExtensionMappingSpec(ParserSpec fullSpec, ParserSpec hostSpec, Map<Integer, Integer> composedToHostMap, BitSet composedExtensionStates) {
            this.composedToDecomposedStates = new TreeMap<Integer, Integer>();
            this.extensionToComposedStates = new TreeMap<Integer, Integer>();

            this.composedExtensionStates = composedExtensionStates;

            // Build list of extension states
            // Build 'reverse' composed to decomposed state map, extension states
            this.composedToDecomposedStates = new TreeMap<Integer, Integer>();
            for (int extensionState = composedExtensionStates.nextSetBit(0), i = 0;
                 extensionState >= 0;
                 extensionState = composedExtensionStates.nextSetBit(extensionState+1), i++
                    ) {
                extensionToComposedStates.put(i, extensionState);
                composedToDecomposedStates.put(extensionState, encodeExtensionIndex(i));
            }

            // Build 'reverse' composed to decomposed state map, host states
            for (Map.Entry<Integer, Integer> entry : composedToHostMap.entrySet()) {
                composedToDecomposedStates.put(entry.getKey(), entry.getValue());
            }

            this.generateSymbolMaps(fullSpec, hostSpec);
        }

        private void generateSymbolMaps(ParserSpec fullSpec, ParserSpec hostSpec) {
            this.extensionTerminalIndices = new BitSet();
            this.extensionNonterminalIndices = new BitSet();
            this.extensionProductionIndices = new BitSet();

            this.hostTerminalIndices = new BitSet();
            this.hostNonterminalIndices = new BitSet();
            this.hostProductionIndices = new BitSet();

            int extensionIndex = 0;
            int hostIndex = 0;

            this.composedToDecomposedTerminals = generateSymbolPartitionMap(fullSpec.terminals, hostSpec.terminals, extensionTerminalIndices, extensionIndex, hostTerminalIndices, hostIndex);
            extensionIndex += extensionTerminalIndices.cardinality();
            hostIndex += hostTerminalIndices.cardinality();

            this.composedToDecomposedNonterminals = generateSymbolPartitionMap(fullSpec.nonterminals, hostSpec.nonterminals, extensionNonterminalIndices, extensionIndex, hostNonterminalIndices, hostIndex);
            extensionIndex += extensionNonterminalIndices.cardinality();
            hostIndex += hostNonterminalIndices.cardinality();

            this.composedToDecomposedProductions = generateSymbolPartitionMap(fullSpec.productions, hostSpec.productions, extensionProductionIndices, extensionIndex, hostProductionIndices, hostIndex);
            extensionIndex += extensionProductionIndices.cardinality();
            hostIndex += hostProductionIndices.cardinality();
        }

        private Map<Integer, Integer> generateSymbolPartitionMap(BitSet fullSpecSymbols, BitSet hostSpecSymbols, BitSet extensionSymbolIndices, int eStartIndex, BitSet hostSymbolIndicies, int hStartIndex) {
            Map<Integer, Integer> symbolMap = new TreeMap<Integer, Integer>(); // full to host/ext

            int t = fullSpecSymbols.nextSetBit(0);
            int hi = hStartIndex;
            int ei = eStartIndex;
            while (t >= 0) {
                if (hostSpecSymbols.get(t)) {
                    symbolMap.put(t, hi);
                    hostSymbolIndicies.set(hi);
                    hi += 1;
                } else {
                    symbolMap.put(t, encodeExtensionIndex(ei));
                    extensionSymbolIndices.set(ei);
                    ei += 1;
                }
                t = fullSpecSymbols.nextSetBit(t + 1);
            }

            return symbolMap;
        }
    }

    private ParserSpec fullSpec;
    private LRParseTable fullParseTable;

    // composed to decomposed maps
    private ExtensionMappingSpec mappingSpec;

    public static ExtensionCompilerReturnData build(ParserSpec fullSpec, LR0DFA fullDFA, LRParseTable fullParseTable, ParserBean parserBean, ParserSpec hostSpec, Map<Integer, Integer> hostPartitionMap, BitSet extensionStatePartition) {
        ExtensionLRParseTableBuilder builder = new ExtensionLRParseTableBuilder(fullSpec, fullDFA, fullParseTable, parserBean, hostSpec, hostPartitionMap, extensionStatePartition);

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
        System.out.println("  terminalsMap:");
        builder.printPartitionMap(builder.mappingSpec.composedToDecomposedTerminals);
        System.out.println("  nonterminalsMap:");
        builder.printPartitionMap(builder.mappingSpec.composedToDecomposedNonterminals);
        System.out.println("  productionsMap:");
        builder.printPartitionMap(builder.mappingSpec.composedToDecomposedProductions);
        System.out.println("  extension state num to composed state num:");
        builder.printPartitionMap(builder.mappingSpec.extensionToComposedStates);
        System.out.println("  composed to decomposed state map:");
        builder.printPartitionMap(builder.mappingSpec.composedToDecomposedStates);

        System.out.println("Parse Table Host Side");
        data.hostSideTable.print();
        System.out.println("Parse Table Extension Side");
        data.extensionSideTable.print();

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
                ParserBean parserBean,
                ParserSpec hostSpec,
                Map<Integer, Integer> hostPartitionMap,
                BitSet extensionStatePartition
    ) {
        this.fullSpec = fullSpec;
        this.fullParseTable = fullParseTable;

        this.mappingSpec = new ExtensionMappingSpec(fullSpec, hostSpec, hostPartitionMap, extensionStatePartition);
    }

    private int encodeExtensionIndex(int i) { return -1 * (i + 1); }

    private int decodeExtensionIndex(int i) { return (-1 * i) - 1; }

    private ExtensionCompilerReturnData build() {
        ExtensionCompilerReturnData data = new ExtensionCompilerReturnData();

        this.generateExtensionParseTables(data);

        // TODO this.extensionSymbolTable = ?
        // TODO this.extensionParserSpec = ?

        // TODO others

        return data;
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
    private void generateExtensionParseTables(ExtensionCompilerReturnData data) {
        int fullSpecSymbolCount = Math.max(fullSpec.terminals.length(), fullSpec.nonterminals.length());
        int extensionStateCount = mappingSpec.composedExtensionStates.cardinality();

        int hostColumnCount = Math.max(mappingSpec.hostTerminalIndices.length(), mappingSpec.hostNonterminalIndices.length());
        int extensionColumnCount = Math.max(mappingSpec.extensionTerminalIndices.length(), mappingSpec.extensionNonterminalIndices.length());

        MutableLRParseTable hostSideTable = new MutableLRParseTable(extensionStateCount, hostColumnCount);
        MutableLRParseTable extensionSideTable = new MutableLRParseTable(extensionStateCount, extensionColumnCount);

        for (int i = 0; i < extensionStateCount; i++) {
            int composedStateNumber = mappingSpec.extensionToComposedStates.get(i);
            for (int symbol = 0; symbol < fullSpecSymbolCount; symbol++) {
                int partialSymbolIndex;
                if (this.fullSpec.terminals.get(symbol)) { // if terminal
                    partialSymbolIndex = mappingSpec.composedToDecomposedTerminals.get(symbol);
                } else if (this.fullSpec.nonterminals.get(symbol)) { // if nonterminal
                    partialSymbolIndex = mappingSpec.composedToDecomposedNonterminals.get(symbol);
                } else {
                    continue; // TODO error? Assumes that nonterminals and terminals come first
                }

                MutableLRParseTable table;
                int convertedSymbol;
                if (partialSymbolIndex < 0) {
                    convertedSymbol = decodeExtensionIndex(partialSymbolIndex);
                    table = extensionSideTable;
                } else {
                    convertedSymbol = partialSymbolIndex;
                    table = hostSideTable;
                }

                byte actionType = fullParseTable.getActionType(composedStateNumber, symbol);
                int composedActionParameter = fullParseTable.getActionParameter(composedStateNumber, symbol);
                int extensionActionParameter = translateActionParameter(actionType, composedActionParameter);

                table.getValidLA(i).set(convertedSymbol);
                table.setActionType(i, convertedSymbol, actionType);
                table.setActionParameter(i, convertedSymbol, extensionActionParameter);
            }
        }

        data.extensionSideTable = extensionSideTable;
        data.hostSideTable = hostSideTable;
    }

    private int translateActionParameter(byte actionType, int composedActionParameter) {
        switch (actionType) {
            case LRParseTable.CONFLICT:
                // TODO Covert conflict number?
                return 0;
            case LRParseTable.REDUCE:
                return mappingSpec.composedToDecomposedProductions.get(composedActionParameter);
            case LRParseTable.SHIFT: // Same as GOTO (SHIFT == GOTO)
                return mappingSpec.composedToDecomposedStates.get(composedActionParameter);
            default:
                return 0;
        }
    }
}
