package edu.umn.cs.melt.copper.compiletime.builders;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.MutableLRParseTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kviratyosin on 12/22/15.
 */
public class ExtensionLRParseTableBuilder {

    public class ExtensionParseTable {

    }

    private ParserSpec fullSpec;
    private LRParseTable fullParseTable;
    private Map<Integer, Integer> hostPartitionMap; // host state num to composed state num
    private Map<Integer, Integer> extensionPartitionMap; // extension state num to composed state num
    private BitSet extensionStatePartition;
    private Map<Integer, Integer> composedToDecomposedMap;

    private int fullSpecSymbolCount, fullSpecStateCount;

    private ExtensionLRParseTableBuilder(ParserSpec fullSpec, LR0DFA fullDFA, LRParseTable fullParseTable, Map<Integer, Integer> hostPartitionMap, BitSet extensionStatePartition) {
        this.fullSpec = fullSpec;
        this.fullParseTable = fullParseTable;
        this.hostPartitionMap = hostPartitionMap;
        this.extensionPartitionMap = new TreeMap<Integer, Integer>();
        this.extensionStatePartition = extensionStatePartition;

        this.fullSpecSymbolCount = Math.max(fullSpec.terminals.length(), fullSpec.nonterminals.length());
        this.fullSpecStateCount = fullDFA.size();

        // Build list of extension states
        // Build 'reverse' composed to decomposed state map, extension states
        this.composedToDecomposedMap = new TreeMap<Integer, Integer>();
        for (int extensionState = extensionStatePartition.nextSetBit(0), i = 0;
             extensionState >= 0;
             extensionState = extensionStatePartition.nextSetBit(extensionState+1), i++
                ) {
            extensionPartitionMap.put(i, extensionState);
            composedToDecomposedMap.put(extensionState, (-1 * i) - 1);
        }

        // Build 'reverse' composed to decomposed state map, host states
        for (Map.Entry<Integer, Integer> entry : hostPartitionMap.entrySet()) {
            composedToDecomposedMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static ExtensionParseTable build(ParserSpec fullSpec, LR0DFA fullDFA, LRParseTable fullParseTable, Map<Integer, Integer> hostPartitionMap, BitSet extensionStatePartition) {
        return new ExtensionLRParseTableBuilder(fullSpec, fullDFA, fullParseTable, hostPartitionMap, extensionStatePartition).buildParseTable();
    }

    private ExtensionParseTable buildParseTable() {
        return null;
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
    private LRParseTable extractExtensionParseTable() {
        int extensionStateCount = extensionStatePartition.cardinality();
        // TODO DOUBT on the fullSpecSymbolCount usage
        MutableLRParseTable extensionParseTable = new MutableLRParseTable(extensionStateCount, fullSpecSymbolCount);

        for (int i = 0; i < extensionStateCount; i++) {
            int composedStateNumber = extensionPartitionMap.get(i);
            // TODO IMPORTANT I don't know that the symbols are numbered the same...
            for (int symbol = 0; i < fullSpecSymbolCount; i++) {
                byte actionType = fullParseTable.getActionType(composedStateNumber, symbol);
                int composedActionParameter = fullParseTable.getActionParameter(composedStateNumber, symbol);
                int extensionActionParamter = translateActionParamter(actionType, composedActionParameter);
                extensionParseTable.setActionType(i, symbol, actionType);
                extensionParseTable.setActionParameter(i, symbol, extensionActionParamter);
            }
        }

        return extensionParseTable;
    }

    private int translateActionParamter(byte actionType, int composedActionParameter) {
        switch (actionType) {
            case LRParseTable.CONFLICT:
                // TODO Covert conflict number?
                return 0;
            case LRParseTable.REDUCE:
                // TODO Convert production number
                return 0;
            case LRParseTable.SHIFT:
                return this.composedToDecomposedMap.get(composedActionParameter);
            default:
                return 0;
        }
    }
}
