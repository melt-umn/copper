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

    private ParserSpec fullSpec;
    private ParserSpec hostSpec;
    private LRParseTable fullParseTable;

    private Map<Integer, Integer> hostPartitionMap; // composed state num to extension state num
    private Map<Integer, Integer> extensionToComposedMap; // extension state num to composed state num

    private BitSet extensionStatePartition;
    private Map<Integer, Integer> composedToDecomposedMap;

    private BitSet extensionTerminalIndices;
    private BitSet extensionNonterminalIndices;
    private BitSet extensionProductionIndices;

    private BitSet hostTerminalIndices;
    private BitSet hostNonterminalIndices;
    private BitSet hostProductionIndices;

    // composed to decomposed maps
    private Map<Integer, Integer> terminalsMap;
    private Map<Integer, Integer> nonterminalsMap;
    private Map<Integer, Integer> productionsMap;
    private Map<Integer, Integer> dfMap;
    private Map<Integer, Integer> terminalClassesMap;
    private Map<Integer, Integer> operatorClassesMap;
    private Map<Integer, Integer> parserAttributesMap;
    private Map<Integer, Integer> grammarsMap;

    private int fullSpecSymbolCount, fullSpecStateCount;

    public static ExtensionCompilerReturnData build(ParserSpec fullSpec, LR0DFA fullDFA, LRParseTable fullParseTable, ParserBean parserBean, ParserSpec hostSpec, Map<Integer, Integer> hostPartitionMap, BitSet extensionStatePartition) {
        ExtensionLRParseTableBuilder builder = new ExtensionLRParseTableBuilder(fullSpec, fullDFA, fullParseTable, parserBean, hostSpec, hostPartitionMap, extensionStatePartition);

        ExtensionCompilerReturnData data = builder.build();

        System.out.println("== BEGIN ExtensionLRParseTableBuilder ==");
        System.out.println("Indicies:");
        System.out.println("  host terminals: " + builder.bitSetIndicesToString(builder.hostTerminalIndices));
        System.out.println("  host nonterminals: " + builder.bitSetIndicesToString(builder.hostNonterminalIndices));
        System.out.println("  host productions: " + builder.bitSetIndicesToString(builder.hostProductionIndices));
        System.out.println("  extension terminals: " + builder.bitSetIndicesToString(builder.extensionTerminalIndices));
        System.out.println("  extension nonterminals: " + builder.bitSetIndicesToString(builder.extensionNonterminalIndices));
        System.out.println("  extension productions: " + builder.bitSetIndicesToString(builder.extensionProductionIndices));

        System.out.println("Maps:");
        System.out.println("  terminalsMap:");
        builder.printPartitionMap(builder.terminalsMap);
        System.out.println("  nonterminalsMap:");
        builder.printPartitionMap(builder.nonterminalsMap);
        System.out.println("  productionsMap:");
        builder.printPartitionMap(builder.productionsMap);
        System.out.println("  extension state num to composed state num:");
        builder.printPartitionMap(builder.extensionToComposedMap);
        System.out.println("  composed to decomposed state map:");
        builder.printPartitionMap(builder.composedToDecomposedMap);

        System.out.println("Parse Table Host Side");
        data.hostSideTable.print();
        System.out.println("Parse Table Extension Side");
        data.extensionSideTable.print();

        System.out.println("== END ExtensionLRParseTableBuilder ==");

        return data;
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
        this.hostSpec = hostSpec;
        this.hostPartitionMap = hostPartitionMap;

        this.extensionToComposedMap = new TreeMap<Integer, Integer>();
        this.extensionStatePartition = extensionStatePartition;

        this.fullSpecSymbolCount = Math.max(fullSpec.terminals.length(), fullSpec.nonterminals.length());
        this.fullSpecStateCount = fullDFA.size();
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

    private void generateSymbolMap() {
        this.extensionTerminalIndices = new BitSet();
        this.extensionNonterminalIndices = new BitSet();
        this.extensionProductionIndices = new BitSet();

        this.hostTerminalIndices = new BitSet();
        this.hostNonterminalIndices = new BitSet();
        this.hostProductionIndices = new BitSet();

        int extensionIndex = 0;
        int hostIndex = 0;

        this.terminalsMap = generateSymbolPartitionMap(fullSpec.terminals, hostSpec.terminals, extensionTerminalIndices, extensionIndex, hostTerminalIndices, hostIndex);
        extensionIndex += extensionTerminalIndices.cardinality();
        hostIndex += hostTerminalIndices.cardinality();

        this.nonterminalsMap = generateSymbolPartitionMap(fullSpec.nonterminals, hostSpec.nonterminals, extensionNonterminalIndices, extensionIndex, hostNonterminalIndices, hostIndex);
        extensionIndex += extensionNonterminalIndices.cardinality();
        hostIndex += hostNonterminalIndices.cardinality();

        this.productionsMap = generateSymbolPartitionMap(fullSpec.productions, hostSpec.productions, extensionProductionIndices, extensionIndex, hostProductionIndices, hostIndex);
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

    private void setExtensionSymbolBitSets(Map<Integer, Integer> symbolMap, BitSet bitSet) {
        for (Map.Entry<Integer, Integer> entry : symbolMap.entrySet()) {
            int index = entry.getValue();
            if (index < 0) {
                bitSet.set(decodeExtensionIndex(index));
            }
        }
    }

    private int encodeExtensionIndex(int i) { return -1 * (i + 1); }

    private int decodeExtensionIndex(int i) { return (-1 * i) - 1; }

    private ExtensionCompilerReturnData build() {
        ExtensionCompilerReturnData data = new ExtensionCompilerReturnData();

        /* BUILD index maps ========================================================== */

        // Build list of extension states
        // Build 'reverse' composed to decomposed state map, extension states
        this.composedToDecomposedMap = new TreeMap<Integer, Integer>();
        for (int extensionState = extensionStatePartition.nextSetBit(0), i = 0;
             extensionState >= 0;
             extensionState = extensionStatePartition.nextSetBit(extensionState+1), i++
                ) {
            extensionToComposedMap.put(i, extensionState);
            composedToDecomposedMap.put(extensionState, encodeExtensionIndex(i));
        }

        // Build 'reverse' composed to decomposed state map, host states
        for (Map.Entry<Integer, Integer> entry : hostPartitionMap.entrySet()) {
            composedToDecomposedMap.put(entry.getKey(), entry.getValue());
        }

        this.generateSymbolMap();

        /* BUILD parse tables ======================================================== */

        this.generateExtensionParseTables(data);

        // TODO finish

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
        int extensionStateCount = extensionStatePartition.cardinality();

        int hostColumnCount = Math.max(hostTerminalIndices.length(), hostNonterminalIndices.length());
        int extensionColumnCount = Math.max(extensionTerminalIndices.length(), extensionNonterminalIndices.length());

        MutableLRParseTable hostSideTable = new MutableLRParseTable(extensionStateCount, hostColumnCount);
        MutableLRParseTable extensionSideTable = new MutableLRParseTable(extensionStateCount, extensionColumnCount);

        for (int i = 0; i < extensionStateCount; i++) {
            int composedStateNumber = extensionToComposedMap.get(i);
            for (int symbol = 0; symbol < fullSpecSymbolCount; symbol++) {
                int partialSymbolIndex;
                if (this.fullSpec.terminals.get(symbol)) { // if terminal
                    partialSymbolIndex = this.terminalsMap.get(symbol);
                } else if (this.fullSpec.nonterminals.get(symbol)) { // if nonterminal
                    partialSymbolIndex = this.nonterminalsMap.get(symbol);
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
                return this.productionsMap.get(composedActionParameter);
            case LRParseTable.SHIFT: // Same as GOTO (SHIFT == GOTO)
                return this.composedToDecomposedMap.get(composedActionParameter);
            default:
                return 0;
        }
    }
}
