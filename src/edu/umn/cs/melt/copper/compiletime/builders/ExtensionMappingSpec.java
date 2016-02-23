package edu.umn.cs.melt.copper.compiletime.builders;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Production;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Kevin Viratyosin
 */
public class ExtensionMappingSpec implements Serializable {

    public Map<Integer, Integer>
            composedToDecomposedStates, // non-neg composed indexes -> non-neg (host) or neg (ext) indices
            extensionToComposedStates, // non-neg ext indices -> non-neg composed indices
            composedToDecomposedSymbols, // non-neg composed indexes -> non-neg (host) or neg (ext) indices
            extensionToComposedSymbols; // non-neg ext indices -> non-neg composed indices
    public BitSet
            composedExtensionStates, // true if index corresponds to the extension state in composed enumeration
            // true if index corresponds to a symbol kind in extension enumeration
            extensionTerminalIndices, extensionNonterminalIndices, extensionProductionIndices,
            extensionDisambiguationFunctionIndices, extensionTerminalClassIndices, extensionOperatorClassIndices, extensionParserAttributeIndices,
            extensionGrammarIndices, extensionParserIndices;
    public BitSet
            // true if index corresponds to a symbol kind in host enumeration
            hostTerminalIndices, hostNonterminalIndices, hostProductionIndices,
            hostDisambiguationFunctionIndices, hostTerminalClassIndices, hostOperatorClassIndices, hostParserAttributeIndices,
            hostGrammarIndices, hostParserIndices;
    public int extensionSymbolCount, extensionSymbolOffset, extensionSymbolTableOffset;

    // indexed by extension enumeration, however references to other symbols in the data use offset indices
    transient public ParserSpec.TerminalData t;
    transient public ParserSpec.NonterminalData nt;
    public ParserSpec.ProductionData pr;
    public ParserSpec.DisambiguationFunctionData df;
    transient public ParserSpec.TerminalClassData tc;
    transient public ParserSpec.GrammarData g;
    transient public ParserSpec.ParserData p;

    public PSSymbolTable extensionSymbolTable;

    public ExtensionMappingSpec(ParserSpec fullSpec, PSSymbolTable fullSymbolTable, ParserSpec hostSpec, Map<Integer, Integer> composedToHostStates, BitSet composedExtensionStates) {

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
        for (Map.Entry<Integer, Integer> entry : composedToHostStates.entrySet()) {
            composedToDecomposedStates.put(entry.getKey(), entry.getValue());
        }

        this.generateSymbolMaps(fullSpec, hostSpec);

        this.generateSymbolTable(fullSymbolTable);

        this.generateSymbolData(fullSpec);
    }

    private class SymbolMapData {
        public BitSet fullSpecSymbols;
        public BitSet hostSpecSymbols;
        public BitSet extensionSymbolIndices;
        public BitSet hostSymbolIndices;

        public SymbolMapData(BitSet fullSpecSymbols, BitSet hostSpecSymbols, BitSet extensionSymbolIndices, BitSet hostSymbolIndices) {
            this.fullSpecSymbols = fullSpecSymbols;
            this.hostSpecSymbols = hostSpecSymbols;
            this.extensionSymbolIndices = extensionSymbolIndices;
            this.hostSymbolIndices = hostSymbolIndices;
        }
    }

    private void generateSymbolMaps(ParserSpec fullSpec, ParserSpec hostSpec) {
        this.extensionTerminalIndices = new BitSet();
        this.extensionNonterminalIndices = new BitSet();
        this.extensionProductionIndices = new BitSet();
        this.extensionDisambiguationFunctionIndices = new BitSet();
        this.extensionTerminalClassIndices = new BitSet();
        this.extensionOperatorClassIndices = new BitSet();
        this.extensionParserAttributeIndices = new BitSet();
        this.extensionGrammarIndices = new BitSet();
        this.extensionParserIndices = new BitSet();

        this.hostTerminalIndices = new BitSet();
        this.hostNonterminalIndices = new BitSet();
        this.hostProductionIndices = new BitSet();
        this.hostDisambiguationFunctionIndices = new BitSet();
        this.hostTerminalClassIndices = new BitSet();
        this.hostOperatorClassIndices = new BitSet();
        this.hostParserAttributeIndices = new BitSet();
        this.hostGrammarIndices = new BitSet();
        this.hostParserIndices = new BitSet();

        this.composedToDecomposedSymbols = new TreeMap<Integer, Integer>();
        this.extensionToComposedSymbols = new TreeMap<Integer, Integer>();

        ArrayList<SymbolMapData> symbolMapDataList = new ArrayList<SymbolMapData>();
        symbolMapDataList.add(new SymbolMapData(fullSpec.terminals, hostSpec.terminals, extensionTerminalIndices, hostTerminalIndices));
        symbolMapDataList.add(new SymbolMapData(fullSpec.nonterminals, hostSpec.nonterminals, extensionNonterminalIndices, hostNonterminalIndices));
        symbolMapDataList.add(new SymbolMapData(fullSpec.productions, hostSpec.productions, extensionProductionIndices, hostProductionIndices));

        symbolMapDataList.add(new SymbolMapData(fullSpec.disambiguationFunctions, hostSpec.disambiguationFunctions, extensionDisambiguationFunctionIndices, hostDisambiguationFunctionIndices));
        symbolMapDataList.add(new SymbolMapData(fullSpec.terminalClasses, hostSpec.terminalClasses, extensionTerminalClassIndices, hostTerminalClassIndices));
        symbolMapDataList.add(new SymbolMapData(fullSpec.operatorClasses, hostSpec.operatorClasses, extensionOperatorClassIndices, hostOperatorClassIndices));
        symbolMapDataList.add(new SymbolMapData(fullSpec.parserAttributes, hostSpec.parserAttributes, extensionParserAttributeIndices, hostParserAttributeIndices));
        symbolMapDataList.add(new SymbolMapData(fullSpec.grammars, hostSpec.grammars, extensionGrammarIndices, hostGrammarIndices));

        int extensionIndex = 0;
        int hostIndex = 0;

        for (SymbolMapData data : symbolMapDataList) {
            generateSymbolPartitionMap(data.fullSpecSymbols, data.hostSpecSymbols, data.extensionSymbolIndices, extensionIndex, data.hostSymbolIndices, hostIndex);
            extensionIndex += data.extensionSymbolIndices.cardinality();
            hostIndex += data.hostSymbolIndices.cardinality();
        }

        hostParserIndices.set(hostIndex);
        this.composedToDecomposedSymbols.put(fullSpec.parser, hostIndex);
        hostIndex += 1;

        this.extensionSymbolCount = extensionIndex;
        this.extensionSymbolOffset = hostIndex;

        this.extensionSymbolTableOffset = Math.max(hostTerminalIndices.length(), hostNonterminalIndices.length());
    }

    private void generateSymbolPartitionMap(BitSet fullSpecSymbols, BitSet hostSpecSymbols, BitSet extensionSymbolIndices, int eStartIndex, BitSet hostSymbolIndicies, int hStartIndex) {
        int t = fullSpecSymbols.nextSetBit(0);
        int hi = hStartIndex;
        int ei = eStartIndex;
        while (t >= 0) {
            if (hostSpecSymbols.get(t)) {
                this.composedToDecomposedSymbols.put(t, hi);
                hostSymbolIndicies.set(hi);
                hi += 1;
            } else {
                this.composedToDecomposedSymbols.put(t, encodeExtensionIndex(ei));
                this.extensionToComposedSymbols.put(ei, t);
                extensionSymbolIndices.set(ei);
                ei += 1;
            }
            t = fullSpecSymbols.nextSetBit(t + 1);
        }
    }

    private void generateSymbolTable(PSSymbolTable fullSymbolTable) {
        ArrayList<CopperASTBean> beans = new ArrayList<CopperASTBean>();

        for (Map.Entry<Integer, Integer> entry : composedToDecomposedSymbols.entrySet()) {
            int decomposedIndex = entry.getValue();
            int composedIndex = entry.getKey();
            if (decomposedIndex < 0) { // is extension symbol?
                int extensionSymbolIndex = decodeExtensionIndex(decomposedIndex);
                CopperASTBean composedBean = fullSymbolTable.get(composedIndex);
                // Note, the beans don't need to be modified since they don't contain index information, just names
                beans.add(extensionSymbolIndex, composedBean);
            }
        }

        extensionSymbolTable = new PSSymbolTable(beans);
    }

    private void generateSymbolData(ParserSpec fullSpec) {
        int maxRHS = 2; // The RHS length of the special start production ^ ::= S $.
        for(int i = extensionProductionIndices.nextSetBit(0); i >= 0; i = extensionProductionIndices.nextSetBit(i+1))
        {
            maxRHS = Math.max(maxRHS,((Production) extensionSymbolTable.get(i)).getRhs().size());
        }
        t = new ParserSpec.TerminalData(extensionTerminalIndices.length());
        nt = new ParserSpec.NonterminalData(extensionNonterminalIndices.length());
        pr = new ParserSpec.ProductionData(extensionProductionIndices.length(), maxRHS);
        df = new ParserSpec.DisambiguationFunctionData(extensionDisambiguationFunctionIndices.length());
        tc = new ParserSpec.TerminalClassData(extensionTerminalClassIndices.length());

        generateTerminalData(fullSpec);
        generateNonTerminalData(fullSpec);
        generateProductionData(fullSpec);
        generateDisambiguationFunctionData(fullSpec);
        generateTerminalClassData(fullSpec);
    }

    private void generateTerminalData(ParserSpec fullSpec) {
        for(int i = extensionTerminalIndices.nextSetBit(0); i >= 0; i = extensionTerminalIndices.nextSetBit(i+1)) {
            int composedIndex = extensionToComposedSymbols.get(i);

            t.setRegex(i, fullSpec.t.getRegex(composedIndex));

            translateSymbolBitSetWithOffset(fullSpec.t.getTerminalClasses(composedIndex), t.getTerminalClasses(i));

            t.setTransparentPrefix(i, convertValidIndex(fullSpec.t.getTransparentPrefix(composedIndex)));
            t.setOperatorClass(i, convertValidIndex(fullSpec.t.getOperatorClass(composedIndex)));

            t.setOperatorPrecedence(i, fullSpec.t.getOperatorPrecedence(composedIndex)); // TODO translate?
            t.setOperatorAssociativity(i, fullSpec.t.getOperatorAssociativity(composedIndex));

            // t.precedences is ignored here, but
            // a PrecedenceGraph for host+ext terminals (using table offset) is generated elsewhere
            //   for the specific purpose of building the scanner DFA annotations
        }
    }

    private void generateNonTerminalData(ParserSpec fullSpec) {
        for(int i = extensionNonterminalIndices.nextSetBit(0); i >= 0; i = extensionNonterminalIndices.nextSetBit(i+1)) {
            int composedIndex = extensionToComposedSymbols.get(i);
            translateSymbolBitSetWithOffset(fullSpec.nt.getProductions(composedIndex), nt.getProductions(i));
        }
    }

    private void generateProductionData(ParserSpec fullSpec) {
        for(int i = extensionProductionIndices.nextSetBit(0); i >= 0; i = extensionProductionIndices.nextSetBit(i+1)) {
            int composedIndex = extensionToComposedSymbols.get(i);

            pr.setLHS(i, translateAndOffsetComposedSymbol(fullSpec.pr.getLHS(composedIndex)));
            int rhsLength = fullSpec.pr.getRHSLength(composedIndex);
            pr.setRHSLength(i, rhsLength);
            for (int j = 0; j < rhsLength; j++) {
                pr.setRHSSym(i, j, translateAndOffsetComposedSymbol(fullSpec.pr.getRHSSym(composedIndex, j)));
            }
            pr.setOperator(i, convertValidIndex(fullSpec.pr.getOperator(composedIndex)));
            pr.setPrecedence(i, fullSpec.pr.getPrecedence(composedIndex)); // TODO translate?
            pr.setHasLayout(i, fullSpec.pr.hasLayout(composedIndex));

            translateSymbolBitSetWithOffset(fullSpec.pr.getLayouts(composedIndex), pr.getLayouts(i));
        }
    }

    private void generateDisambiguationFunctionData(ParserSpec fullSpec) {
        for(int i = extensionDisambiguationFunctionIndices.nextSetBit(0); i >= 0; i = extensionDisambiguationFunctionIndices.nextSetBit(i+1)) {
            int composedIndex = extensionToComposedSymbols.get(i);

            translateSymbolBitSetWithOffset(fullSpec.df.getMembers(composedIndex), df.getMembers(i));

            df.setDisambiguateTo(i, convertValidIndex(fullSpec.df.getDisambiguateTo(composedIndex)));
        }
    }

    private void generateTerminalClassData(ParserSpec fullSpec) {
        for(int i = extensionTerminalClassIndices.nextSetBit(0); i >= 0; i = extensionTerminalClassIndices.nextSetBit(i+1)) {
            int composedIndex = extensionToComposedSymbols.get(i);
            translateSymbolBitSetWithOffset(fullSpec.tc.getMembers(composedIndex), tc.getMembers(i));
        }
    }

    // Some values in ParserSpec use -1 as a null value
    // if the composed index is valid (not null by this definition),
    // it is converted to the (offset) extension index
    private int convertValidIndex(int index) {
        return index < 0 ? index : translateAndOffsetComposedSymbol(index);
    }

    // TODO Are operator precedences just numbers? Can they be left alone?

    // TODO make sure that these are being used correctly -- esp composedToDecomposed...
    public static int encodeExtensionIndex(int i) { return -1 * (i + 1); }

    public static int decodeExtensionIndex(int i) { return (-1 * i) - 1; }

    public int translateAndOffsetComposedSymbol(int i) {
        int decomposedIndex = composedToDecomposedSymbols.get(i);
        return decomposedIndex < 0 ? decodeAndOffsetExtensionIndex(decomposedIndex) : decomposedIndex;
    }

    public int translateAndTableOffsetComposedSymbol(int i) {
        int decomposedIndex = composedToDecomposedSymbols.get(i);
        return decomposedIndex < 0 ? decodeAndTableOffsetExtensionIndex(decomposedIndex) : decomposedIndex;
    }

    public void translateSymbolBitSetWithOffset(BitSet from, BitSet to) {
        to.clear();
        for (int i = from.nextSetBit(0); i >= 0; i = from.nextSetBit(i+1)) {
            to.set(translateAndOffsetComposedSymbol(i));
        }
    }
    public void translateSymbolBitSetWithTableOffset(BitSet from, BitSet to) {
        to.clear();
        for (int i = from.nextSetBit(0); i >= 0; i = from.nextSetBit(i+1)) {
            to.set(translateAndTableOffsetComposedSymbol(i));
        }
    }

    public int decodeAndOffsetExtensionIndex(int i) {
        return (-1 * i) - 1 + this.extensionSymbolOffset;
    }

    public int decodeAndTableOffsetExtensionIndex(int i) {
        return (-1 * i) - 1 + this.extensionSymbolTableOffset;
    }

    public int offsetExtensionIndex(int i) {
        return i + this.extensionSymbolOffset;
    }

    public int tableOffsetExtensionIndex(int i) {
        return i + this.extensionSymbolTableOffset;
    }

    public int unOffsetExtensionIndex(int i) {
        return i - this.extensionSymbolOffset;
    }

    public int encodeOffsetExtensionIndex(int i) {
        return -1 * ((i - this.extensionSymbolOffset) + 1);
    }
}

