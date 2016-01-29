package edu.umn.cs.melt.copper.compiletime.builders;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.MutableLRParseTable;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;

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
    private LRParseTable fullParseTable;
    private PSSymbolTable fullSymbolTable;

    // composed to decomposed maps
    private ExtensionMappingSpec mappingSpec;

    public static ExtensionCompilerReturnData build(ParserSpec fullSpec, LR0DFA fullDFA, LRParseTable fullParseTable, PSSymbolTable fullSymbolTable, ParserSpec hostSpec, Map<Integer, Integer> hostPartitionMap, BitSet extensionStatePartition) {
        ExtensionLRParseTableBuilder builder = new ExtensionLRParseTableBuilder(fullSpec, fullDFA, fullParseTable, fullSymbolTable, hostSpec, hostPartitionMap, extensionStatePartition);

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
                PSSymbolTable fullSymbolTable,
                ParserSpec hostSpec,
                Map<Integer, Integer> hostPartitionMap,
                BitSet extensionStatePartition
    ) {
        this.fullSpec = fullSpec;
        this.fullParseTable = fullParseTable;
        this.fullSymbolTable = fullSymbolTable;

        this.mappingSpec = new ExtensionMappingSpec(fullSpec, fullSymbolTable, hostSpec, hostPartitionMap, extensionStatePartition);
    }

    private ExtensionCompilerReturnData build() {
        ExtensionCompilerReturnData data = new ExtensionCompilerReturnData();

        this.generateExtensionSymbolTable(data, mappingSpec);
        this.generateExtensionParseTables(data, mappingSpec);

        // TODO this.extensionParserSpec = ?

        // TODO others

        return data;
    }

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

        int hostColumnCount = Math.max(mappingSpec.hostTerminalIndices.length(), mappingSpec.hostNonterminalIndices.length());
        int extensionColumnCount = Math.max(mappingSpec.extensionTerminalIndices.length(), mappingSpec.extensionNonterminalIndices.length());

        MutableLRParseTable hostSideTable = new MutableLRParseTable(extensionStateCount, hostColumnCount);
        MutableLRParseTable extensionSideTable = new MutableLRParseTable(extensionStateCount, extensionColumnCount);

        for (int i = 0; i < extensionStateCount; i++) {
            int composedStateNumber = mappingSpec.extensionToComposedStates.get(i);
            for (int symbol = 0; symbol < fullSpecSymbolCount; symbol++) {
                int partialSymbolIndex = mappingSpec.composedToDecomposedSymbols.get(symbol);
                // null check?

                MutableLRParseTable table;
                int convertedSymbol;
                if (partialSymbolIndex < 0) {
                    convertedSymbol = ExtensionMappingSpec.decodeExtensionIndex(partialSymbolIndex);
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
                return mappingSpec.composedToDecomposedSymbols.get(composedActionParameter);
            case LRParseTable.SHIFT: // Same as GOTO (SHIFT == GOTO)
                return mappingSpec.composedToDecomposedStates.get(composedActionParameter);
            default:
                return 0;
        }
    }
}
