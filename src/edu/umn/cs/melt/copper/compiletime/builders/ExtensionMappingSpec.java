package edu.umn.cs.melt.copper.compiletime.builders;

import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import java.util.BitSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kviratyosin on 1/25/16.
 */
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

    private int encodeExtensionIndex(int i) { return -1 * (i + 1); }
}

