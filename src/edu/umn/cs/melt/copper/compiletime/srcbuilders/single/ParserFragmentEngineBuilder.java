package edu.umn.cs.melt.copper.compiletime.srcbuilders.single;

import edu.umn.cs.melt.copper.compiletime.builders.*;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Regex;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PrecedenceGraph;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder;
import edu.umn.cs.melt.copper.runtime.engines.single.ParserFragmentEngine;
import edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * @author Kevin Viratyosin
 */
public class ParserFragmentEngineBuilder {

    private static final int MARKING_TERMINAL_FRAGMENT_ID = 0;
    private static final int HOST_FRAGMENT_ID = 0;
    private HostFragmentData hostFragment;
    private List<ExtensionFragmentData> extensionFragments;

    private int extensionCount;
    private int fragmentCount;
    private int hostTerminalLength;
    private int[] extTerminalLengths;

    private int[] extStateOffset;

    private GeneralizedDFA markingTerminalScannerDFA;
    private SingleScannerDFAAnnotations markingTerminalScannerDFAAnnotations;

    private int[][] parseTable;
    private int[][][] deltas;
    private int totalStateCount, hostStateCount;
    private ArrayList<MarkingTerminalData> markingTerminalDatas;
    private int markingTerminalOffset;
    private int markingTerminalCount;
    private int[][] extTerminalUses;
    private int[] hostTerminalUses;
    private BitSet[] layoutSets;
    private BitSet[] prefixSets;
    private BitSet[] markingTerminalEmptyStateSets;
    private BitSet[][] prefixMaps;
    private BitSet[][] markingTerminalEmptyPrefixMaps;
    private BitSet[] shiftableSets;
    private BitSet[] markingTerminalShiftableSets;
    private BitSet[] extShiftableUnion;
    private BitSet hostStateShiftableUnion;
    private BitSet markingTerminalShiftableUnion;

    private int totalProductionCount;
    private int[] productionLengths;
    private Map<Integer, Map<Integer, Integer>> productionMap;
    private Map<Integer, Pair<Integer, Integer>> productionMapBack;
    private int[] productionCounts;
    private int[] productionLHSs;

    private static class ObjectToHash {
        public Object obj;
        public String type;
        public String name;

        public ObjectToHash(Object obj, String type, String name) {
            this.obj = obj;
            this.type = type;
            this.name = name;
        }
    }

    private List<ObjectToHash> objectsToHash;

    public ParserFragmentEngineBuilder(HostFragmentData hostFragment, List<ExtensionFragmentData> extensionFragments) {
        this.hostFragment = hostFragment;
        this.extensionFragments = extensionFragments;

        this.extensionCount = extensionFragments.size();
        this.fragmentCount = this.extensionCount + 1;

        this.hostTerminalLength = hostFragment.fullSpec.terminals.length();
        this.extTerminalLengths = new int[extensionCount];
        for (int e = 0; e < this.extensionCount; e++) {
            ExtensionMappingSpec spec = this.extensionFragments.get(e).extensionMappingSpec;
            this.extTerminalLengths[e] = spec.tableOffsetExtensionIndex(spec.extensionTerminalIndices.length());
        }
    }

    public void buildEngine(
            PrintStream out,
            String packageDecl,
            String importDecls,
            String parserName,
            String scannerName,
            String parserAncillaries,
            String scannerAncillaries
    ) throws IOException, CopperException {
        printSignature(out);
        printDecls(out, packageDecl, importDecls);

        String rootType = hostFragment.symbolTable.getNonTerminal(hostFragment.fullSpec.pr.getRHSSym(hostFragment.fullSpec.getStartProduction(), 0)).getReturnType();
        rootType = rootType == null ? Object.class.getName() : rootType;
        String errorType = CopperParserException.class.getName();

        out.println("public class " + parserName + " extends " + ParserFragmentEngine.class.getName() + "<" + rootType + "," + errorType + "> {");

        printFixedMethods(out, errorType);

        printFragmentMethods(out);

        // TODO, Terminals enum and pushToken ...?

        // TODO write methods related to transition

        // TODO print Semantics class and implement related methods (including instatiate Semantics in startEngine)

        makeObjectsToBeHashed();
        writeHashes(out);

        // TODO write "parserAncillaries" -- var decls and getters
        printParserAncillaryDecls(out);
        printParserAncillaryMethods(out);

        System.out.println(scannerAncillaries);

        writeStaticMemberInitializations(out);

        out.println("}");
    }

    private void printSignature(PrintStream out) {
        out.println("/*");
        out.println(" * Built at " + new java.util.Date(System.currentTimeMillis()));
        out.println(" * by Copper version " + ParserCompiler.VERSION);
        out.println(" *           build " + ParserCompiler.BUILD);
        out.println(" */");
    }

    private void printDecls(PrintStream out, String packageDecl, String importDecls) {
        out.println(packageDecl);
        out.println(importDecls);

        String preambleCode = ""; // TODO fill -- used to be `symbolTable.getParser(spec.parser).getPreambleCode()`
        if (preambleCode != null) {
            out.println(preambleCode);
        }
    }

    private void printFixedMethods(PrintStream out, String errorType) {
        out.println("  protected String formatError(String error) {");
        out.println("    String location = \"\";");
        out.println("    location += \"line \" + virtualLocation.getLine() + \", column \" + virtualLocation.getColumn();");
        out.println("    if (currentState.pos.getFileName().length() > 40) {");
        out.println("      location += \"\\n         \";");
        out.println("    }");
        out.println("    location += \" in file \" + virtualLocation.getFileName();");
        out.println("    location += \"\\n         (parser state: \" + currentState.statenum + \"; real character index: \" + currentState.pos.getPos() + \")\";");
        out.println("    return \"Error at \" + location + \":\\n  \" + error;");
        out.println("  }");

        out.println("  protected void reportError(String message) throws " + errorType + " {");
        out.println("    throw new " + CopperParserException.class.getName() + "(message);");
        out.println("  }");

        out.println("  protected void reportSyntaxError() throws " + errorType + " {");
        out.println("    " + ArrayList.class.getName() + "<String> expectedTerminalsReal = bitVecToRealStringList(getShiftableSets()[currentState.statenum]);");
        out.println("    " + ArrayList.class.getName() + "<String> expectedTerminalsDisplay = bitVecToDisplayStringList(getShiftableSets()[currentState.statenum]);");
        out.println("    " + ArrayList.class.getName() + "<String> matchedTerminalsReal = bitVecToRealStringList(disjointMatch.terms);");
        out.println("    " + ArrayList.class.getName() + "<String> matchedTerminalsDisplay = bitVecToDisplayStringList(disjointMatch.terms);");
        out.println("    throw new edu.umn.cs.melt.copper.runtime.logging.CopperSyntaxError(virtualLocation,currentState.pos,currentState.statenum,expectedTerminalsReal,expectedTerminalsDisplay,matchedTerminalsReal,matchedTerminalsDisplay);");
        out.println("  }");
    }

    private void printFragmentMethods(PrintStream out) {
        out.println("  protected int getFragmentCount() {");
        out.println("  return " + fragmentCount + ";");
        out.println("  }");

        out.println("  protected int stateToFragmentId(int state) {");
        out.println("    if (state < " + hostStateCount + ") {");
        out.println("      return 0;");
        for (int e = 1; e < extensionCount; e++) {
            out.println("    } else if (state < " + extStateOffset[e] + ") {");
            out.println("      return " + e + ";");
        }
        out.println("    } else {");
        out.println("      return " + extensionCount + ";");
        out.println("    }");
        out.println("  }");
    }

    private void printParserAncillaryDecls(PrintStream out) {
        for (ObjectToHash obj: objectsToHash) {
            out.println("  private static " + obj.type + " " + obj.name + ";");
        }
        // TODO finish
    }

    private void printParserAncillaryMethods(PrintStream out) {
        out.println("  public int[][] getParseTable() {");
        out.println("    return parseTable;");
        out.println("  }");

        out.println("  protected int[] getProductionLengths() {");
        out.println("    return productionLengths;");
        out.println("  }");

        out.println("  public int[] getProductionLHSs() {");
        out.println("    return productionLHSs;");
        out.println("  }");

        out.println("  protected int[][] getFragmentTransitionTable(int fragmentId) {");
        out.println("    return deltas[fragmentId];");
        out.println("  }");

        out.println("  protected " + BitSet.class.getName() + "[] getFragmentAcceptSets(int fragmentId) {");
        out.println("    return acceptSetss[fragmentId];");
        out.println("  }");

        out.println("  protected " + BitSet.class.getName() + "[] getFragmentRejectSets(int fragmentId) {");
        out.println("    return rejectSetss[fragmentId];");
        out.println("  }");

        out.println("  protected " + BitSet.class.getName() + "[] getFragmentPossibleSets(int fragmentId) {");
        out.println("    return possibleSetss[fragmentId];");
        out.println("  }");

        out.println("  protected int getFragmentTerminalCount(int fragmentId) {");
        out.println("    if (fragmentId == " + MARKING_TERMINAL_FRAGMENT_ID + ") {");
        out.println("      return " + markingTerminalCount + ";");
        out.println("    } else {");
        out.println("      return extTerminalLengths[fragmentId - 1];");
        out.println("    }");
        out.println("  }");

        // TODO convert to array access?
        out.println("  protected int getFragmentStartState(int fragmentId) {");
        out.println("    switch (fragmentId) {");
        out.println("      case 0:");
        out.println("        return " + markingTerminalScannerDFA.getStartState() + ";");
        out.println("        break;");
        for (int e = 0; e < extensionCount; e++) {
            out.println("      case " + (e+1) + ":");
            out.println("        return " + extensionFragments.get(e).scannerDFA.getStartState() + ";");
            out.println("        break;");
        }
        out.println("      default:");
        out.println("        return -1;"); // TODO fragment error
        out.println("    }");
        out.println("  }");

        out.println("  protected int getFragmentEOFSymNum(int fragmentId) {");
        out.println("    return " + hostFragment.fullSpec.getEOFTerminal() + ";");
        out.println("  }");

        out.println("  public " + BitSet.class.getName() + "[][] getFragmentPrefixMaps(int fragmentId) {");
        out.println("    if (fragmentId == " + MARKING_TERMINAL_FRAGMENT_ID + ") {");
        out.println("      return markingTerminalEmptyPrefixMaps;");
        out.println("    } else {");
        out.println("      return prefixMaps;");
        out.println("    }");
        out.println("  }");

        out.println("  public " + BitSet.class.getName() + "[] getFragmentLayoutSets(int fragmentId) {");
        out.println("    if (fragmentId == " + MARKING_TERMINAL_FRAGMENT_ID + ") {");
        out.println("      return markingTerminalEmptyStateSets;");
        out.println("    } else {");
        out.println("      return layoutSets;");
        out.println("    }");
        out.println("  }");

        out.println("  public " + BitSet.class.getName() + "[] getFragmentPrefixSets(int fragmentId) {");
        out.println("    if (fragmentId == " + MARKING_TERMINAL_FRAGMENT_ID + ") {");
        out.println("      return markingTerminalEmptyStateSets;");
        out.println("    } else {");
        out.println("      return prefixSets;");
        out.println("    }");
        out.println("  }");

        out.println("  protected int getFragmentTerminalUses(int fragmentId, int t) {");
        out.println("    if (fragmentId == " + MARKING_TERMINAL_FRAGMENT_ID + ") {");
        out.println("      return " + SingleDFAEngine.TERMINAL_EXCLUSIVELY_SHIFTABLE + ";");
        out.println("    } else if (t >= hostTerminalUses.length) {");
        out.println("      return extTerminalUses[fragmentId - 1][t];");
        out.println("    } else {");
        out.println("      return hostTerminalUses[t] & extTerminalUses[fragmentId - 1][t];");
        out.println("    }");
        out.println("  }");

        out.println("  public " + BitSet.class.getName() + " getFragmentShiftableUnion(int fragmentId) {");
        out.println("    if (fragmentId == " + MARKING_TERMINAL_FRAGMENT_ID + ") {");
        out.println("      return markingTerminalShiftableUnion;");
        out.println("    } else {");
        out.println("      return extShiftableUnion[fragmentId - 1];");
        out.println("    }");
        out.println("  }");

        out.println("  public " + BitSet.class.getName() + "[] getFragmentShiftableSets(int fragmentId) {");
        out.println("    if (fragmentId == " + MARKING_TERMINAL_FRAGMENT_ID + ") {");
        out.println("      return markingTerminalShiftableSets;");
        out.println("    } else {");
        out.println("      return shiftableSets;");
        out.println("    }");
        out.println("  }");

        // TODO finish
    }

    private void makeObjectsToBeHashed() {
        objectsToHash = new ArrayList<ObjectToHash>();

        prepProductionIndices();

        objectsToHash.add(new ObjectToHash(extTerminalLengths, "int[]", "extTerminalLengths"));

        makeParseTableAndSets();
        objectsToHash.add(new ObjectToHash(parseTable, "int[][]", "parseTable"));
        objectsToHash.add(new ObjectToHash(hostTerminalUses, "int[]", "hostTerminalUses"));
        objectsToHash.add(new ObjectToHash(extTerminalUses, "int[][]", "extTerminalUses"));
        objectsToHash.add(new ObjectToHash(markingTerminalEmptyStateSets, BitSet.class.getName() + "[]", "markingTerminalEmptyStateSets"));
        objectsToHash.add(new ObjectToHash(layoutSets, BitSet.class.getName() + "[]", "layoutSets"));
        objectsToHash.add(new ObjectToHash(prefixSets, BitSet.class.getName() + "[]", "prefixSets"));
        objectsToHash.add(new ObjectToHash(prefixMaps, BitSet.class.getName() + "[][]", "prefixMaps"));
        objectsToHash.add(new ObjectToHash(markingTerminalEmptyPrefixMaps, BitSet.class.getName() + "[][]", "markingTerminalEmptyPrefixMaps"));
        objectsToHash.add(new ObjectToHash(shiftableSets, BitSet.class.getName() + "[]", "shiftableSets"));
        objectsToHash.add(new ObjectToHash(extShiftableUnion, BitSet.class.getName() + "[]", "extShiftableUnion"));
        objectsToHash.add(new ObjectToHash(markingTerminalShiftableUnion, BitSet.class.getName(), "markingTerminalShiftableUnion"));

        generateMarkingTerminalScanner();

        deltas[0] = markingTerminalScannerDFA.getTransitions();
        for (int i = 0; i < extensionCount; i++) {
            deltas[i + 1] = extensionFragments.get(i).scannerDFA.getTransitions();
        }
        objectsToHash.add(new ObjectToHash(deltas, "int[][][]", "deltas"));

        addScannerAnnotationsToBeHashed();

        makeProductionLengths();
        objectsToHash.add(new ObjectToHash(productionLengths, "int[]", "productionLengths"));

        makeProductionLHSs();
        objectsToHash.add(new ObjectToHash(productionLHSs, "int[]", "productionLHSs"));
        // TODO finish
    }

    private void prepProductionIndices() {
        productionMap = new TreeMap<Integer, Map<Integer, Integer>>();
        productionMapBack = new TreeMap<Integer, Pair<Integer, Integer>>();
        productionCounts = new int[fragmentCount];

        int newIndex = 0;

        BitSet hostProductions = hostFragment.fullSpec.productions;
        productionCounts[0] = hostProductions.cardinality();
        productionMap.put(0, new TreeMap<Integer, Integer>());
        for (int p = hostProductions.nextSetBit(0); p >= 0; p = hostProductions.nextSetBit(p + 1)) {
            productionMap.get(0).put(p, newIndex);
            productionMapBack.put(newIndex, new Pair<Integer, Integer>(0, p));
            newIndex += 1;
        }
        for (int e = 0; e < extensionCount; e++) {
            BitSet productions = extensionFragments.get(e).extensionMappingSpec.extensionProductionIndices;
            productionCounts[e + 1] = productions.cardinality();
            productionMap.put(e + 1, new TreeMap<Integer, Integer>());
            for (int p = productions.nextSetBit(0); p >= 0; p = productions.nextSetBit(p + 1)) {
                productionMap.get(e + 1).put(p, newIndex);
                productionMapBack.put(newIndex, new Pair<Integer, Integer>(e + 1, p));
                newIndex += 1;
            }
        }

        totalProductionCount = newIndex;
    }

    private void addScannerAnnotationsToBeHashed() {
        BitSet[][] acceptSetss = new BitSet[fragmentCount][];
        acceptSetss[0] = markingTerminalScannerDFAAnnotations.acceptSets;
        for (int e = 0; e < extensionCount; e++) {
            acceptSetss[e + 1] = extensionFragments.get(e).scannerDFAAnnotations.acceptSets;
        }
        objectsToHash.add(new ObjectToHash(acceptSetss, "BitSet[][]", "acceptSetss"));

        BitSet[][] rejectSetss = new BitSet[fragmentCount][];
        rejectSetss[0] = markingTerminalScannerDFAAnnotations.rejectSets;
        for (int e = 0; e < extensionCount; e++) {
            rejectSetss[e + 1] = extensionFragments.get(e).scannerDFAAnnotations.rejectSets;
        }
        objectsToHash.add(new ObjectToHash(rejectSetss, "BitSet[][]", "rejectSetss"));

        BitSet[][] possibleSetss = new BitSet[fragmentCount][];
        possibleSetss[0] = markingTerminalScannerDFAAnnotations.possibleSets;
        for (int e = 0; e < extensionCount; e++) {
            possibleSetss[e + 1] = extensionFragments.get(e).scannerDFAAnnotations.possibleSets;
        }
        objectsToHash.add(new ObjectToHash(possibleSetss, "BitSet[][]", "possibleSetss"));
    }

    private static class MarkingTerminalData implements Comparable {
        public int extensionId;
        public int extensionTerminal;
        public int hostLHS;
        public int offsetTransitionState;
        public int endIndex;
        public MarkingTerminalData(int extensionId, int extensionTerminal, int hostLHS, int offsetTransitionState, int endIndex) {
            this.extensionId = extensionId;
            this.extensionTerminal = extensionTerminal;
            this.hostLHS = hostLHS;
            this.offsetTransitionState = offsetTransitionState;
            this.endIndex = endIndex;
        }

        @Override
        public int compareTo(Object o) {
            return this.endIndex - ((MarkingTerminalData) o).endIndex;
        }
    }

    // Must be called after makeParseTable
    private void generateMarkingTerminalScanner() {
        Collections.sort(markingTerminalDatas);
        TreeMap<Integer, Regex> regexes = new TreeMap<Integer, Regex>();
        BitSet terminals = new BitSet();

        for (int t = 0; t < markingTerminalCount; t++) {
            MarkingTerminalData data = markingTerminalDatas.get(t);
            regexes.put(t, extensionFragments.get(data.extensionId).markingTerminalRegexes.get(data.extensionTerminal));
            terminals.set(t);
        }

        markingTerminalScannerDFA = SingleScannerDFABuilder.build(regexes, terminals, -1);
        markingTerminalScannerDFAAnnotations = SingleScannerDFAAnnotationBuilder.build(new PrecedenceGraph(markingTerminalCount), markingTerminalScannerDFA);
    }

    private void makeParseTableAndSets() {
        hostStateCount = hostFragment.parseTable.size();
        totalStateCount = hostStateCount;
        int maxTableSymbols = hostFragment.fullSpec.nonterminals.length();
        markingTerminalDatas = new ArrayList<MarkingTerminalData>();

        extStateOffset = new int[extensionCount];
        for (int i = 0; i < extensionCount; i++) {
            ExtensionFragmentData fragment = extensionFragments.get(i);
            ExtensionMappingSpec spec = extensionFragments.get(i).extensionMappingSpec;

            extStateOffset[i] = totalStateCount;
            totalStateCount += fragment.appendedExtensionTable.size();
            int extTableWidth = spec.tableOffsetExtensionIndex(spec.extensionNonterminalIndices.length());
            if (extTableWidth > maxTableSymbols) {
                maxTableSymbols = extTableWidth;
            }
        }

        markingTerminalOffset = maxTableSymbols;
        for (int i = 0; i < extensionCount; i++) {
            ExtensionFragmentData fragment = extensionFragments.get(i);
            Set<Integer> markingTerminals = fragment.markingTerminalLHS.keySet();
            for (int mt: markingTerminals) {
                int lhs = fragment.markingTerminalLHS.get(mt);
                int offsetState = fragment.markingTerminalStates.get(mt) + extStateOffset[i];
                markingTerminalDatas.add(new MarkingTerminalData(i, mt, lhs, offsetState, maxTableSymbols));
                maxTableSymbols += 1;
            }
        }
        markingTerminalCount = markingTerminalDatas.size();

        parseTable = new int[totalStateCount][maxTableSymbols];
        layoutSets = new BitSet[totalStateCount];
        prefixSets = new BitSet[totalStateCount];
        prefixMaps = new BitSet[totalStateCount][];
        shiftableSets = new BitSet[totalStateCount];

        hostTerminalUses = new int[hostTerminalLength];
        hostStateShiftableUnion = SingleDFAEngine.newBitVec(hostTerminalLength);
        copyParseTable(-1);

        extTerminalUses = new int[extensionCount][];
        extShiftableUnion = new BitSet[extensionCount];
        for (int i = 0; i < extensionCount; i++) {
            ExtensionFragmentData fragment = extensionFragments.get(i);
            ExtensionMappingSpec spec = extensionFragments.get(i).extensionMappingSpec;

            extTerminalUses[i] = new int[extTerminalLengths[i]];
            extShiftableUnion[i] = SingleDFAEngine.newBitVec(extTerminalLengths[i]);
            copyParseTable(i);
            extShiftableUnion[i].or(hostStateShiftableUnion);
        }

        fillMarkingTerminalMetaData(markingTerminalDatas);

        markingTerminalEmptyStateSets = new BitSet[totalStateCount];
        markingTerminalEmptyPrefixMaps = new BitSet[totalStateCount][markingTerminalCount];
        BitSet empty = SingleDFAEngine.newBitVec(markingTerminalCount);
        for (int state = 0; state < totalStateCount; state++) {
            markingTerminalEmptyStateSets[state] = empty;
            for (int t = 0; t < markingTerminalCount; t++) {
                markingTerminalEmptyPrefixMaps[state][t] = empty;
            }
        }
    }

    private void copyParseTable(int extensionId) {
        LRParseTable table = null;
        BitSet hostTerminals = hostFragment.fullSpec.terminals;
        BitSet hostNonterminals = hostFragment.fullSpec.nonterminals;
        ExtensionFragmentData extensionFragmentData = null;
        ExtensionMappingSpec extSpec = null;
        BitSet extTerminals = null;
        BitSet extNonterminals = null;
        BitSet offsetExtNonterminals = null;
        LRLookaheadAndLayoutSets layouts = null;
        TransparentPrefixes prefixes = null;
        BitSet shiftableUnion = null;

        boolean isExtension = extensionId >= 0;
        int stateOffset = 0;
        if (isExtension) {
            extensionFragmentData = extensionFragments.get(extensionId);
            table = extensionFragmentData.appendedExtensionTable;
            extSpec = extensionFragmentData.extensionMappingSpec;
            extTerminals = extSpec.extensionTerminalIndices;
            extNonterminals = extSpec.extensionNonterminalIndices;

            offsetExtNonterminals = new BitSet();
            for (int nt = extNonterminals.nextSetBit(0); nt >= 0; nt = extNonterminals.nextSetBit(nt + 1)) {
                offsetExtNonterminals.set(extSpec.tableOffsetExtensionIndex(nt));
            }

            stateOffset = extStateOffset[extensionId];
            layouts = extensionFragmentData.extensionLookaheadAndLayoutSets;
            prefixes = extensionFragmentData.transparentPrefixes;
            shiftableUnion = extShiftableUnion[extensionId];
        } else {
            table = hostFragment.parseTable;
            layouts = hostFragment.lookaheadSets;
            prefixes = hostFragment.prefixes;
            shiftableUnion = hostStateShiftableUnion;
        }

        int symType;
        for (int state = 0; state < table.size(); state++) {
            for (int t = table.getValidLA(state).nextSetBit(0); t >= 0; t = table.getValidLA(state).nextSetBit(t+1)) {
                boolean isTerminal, isNonterminal;
                if (extSpec != null && t >= extSpec.extensionSymbolTableOffset) {
                    int extensionIndex = t - extSpec.extensionSymbolTableOffset;
                    isTerminal = extTerminals.get(extensionIndex);
                    isNonterminal = extNonterminals.get(extensionIndex);
                } else {
                    isTerminal = hostTerminals.get(t);
                    isNonterminal = hostNonterminals.get(t);
                }

                int actionParameter = table.getActionParameter(state, t);

                if (isTerminal) {
                    byte actionType = table.getActionType(state, t);

                    if (actionType == LRParseTable.ERROR) {
                        symType = SingleDFAEngine.STATE_ERROR;
                    } else if (actionType == LRParseTable.ACCEPT && t == hostFragment.fullSpec.getEOFTerminal()) {
                        symType = SingleDFAEngine.STATE_ACCEPT;
                    } else if (actionType == LRParseTable.SHIFT) {
                        symType = SingleDFAEngine.STATE_SHIFT;
                        if (isExtension && actionParameter < 0) { // ext state
                            actionParameter = ExtensionMappingSpec.decodeExtensionIndex(actionParameter) + extStateOffset[extensionId];
                        }
                    } else if (actionType == LRParseTable.REDUCE) {
                        symType = SingleDFAEngine.STATE_REDUCE;
                        if (isExtension && actionParameter < 0) {
                            actionParameter = productionMap.get(extensionId + 1).get(ExtensionMappingSpec.decodeExtensionIndex(actionParameter));
                        } else {
                            actionParameter = productionMap.get(0).get(actionParameter);
                        }
                    } else {
                        symType = SingleDFAEngine.STATE_ERROR;
                    }

                    parseTable[state + stateOffset][t] = SingleDFAEngine.newAction(symType, actionParameter);
                    if (isExtension) {
                        extTerminalUses[extensionId][t] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_SHIFTABLE;
                    } else {
                        hostTerminalUses[t] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_SHIFTABLE;
                    }
                } else if (isNonterminal) {
                    if (isExtension && actionParameter < 0) { // ext state
                        actionParameter = ExtensionMappingSpec.decodeExtensionIndex(actionParameter) + extStateOffset[extensionId];
                    }
                    parseTable[state + stateOffset][t] = SingleDFAEngine.newAction(SingleDFAEngine.STATE_GOTO, actionParameter);
                }
            }

            shiftableSets[state + stateOffset] = SingleDFAEngine.newBitVec(isExtension ? extTerminalLengths[extensionId] : hostTerminalLength);
            shiftableSets[state + stateOffset].or(table.getValidLA(state));

            layoutSets[state + stateOffset] = SingleDFAEngine.newBitVec(isExtension ? extTerminalLengths[extensionId] : hostTerminalLength);
            layoutSets[state + stateOffset].or(layouts.getLayout(state));
            shiftableSets[state + stateOffset].or(layouts.getLayout(state));

            prefixSets[state + stateOffset] = SingleDFAEngine.newBitVec(isExtension ? extTerminalLengths[extensionId] : hostTerminalLength);
            prefixSets[state + stateOffset].or(prefixes.getPrefixes(state));
            shiftableSets[state + stateOffset].or(prefixes.getPrefixes(state));

            shiftableSets[state + stateOffset].andNot(isExtension ? offsetExtNonterminals : hostNonterminals);

            shiftableUnion.or(shiftableSets[state + stateOffset]);

            for (int layout = layouts.getLayout(state).nextSetBit(0); layout >= 0; layout = layouts.getLayout(state).nextSetBit(layout + 1)) {
                if (isExtension) {
                    extTerminalUses[extensionId][layout] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_LAYOUT;
                } else {
                    hostTerminalUses[layout] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_LAYOUT;
                }
            }

            prefixMaps[state + stateOffset] = new BitSet[isExtension ? extTerminalLengths[extensionId] : hostTerminalLength];
            for (int prefix = prefixes.getPrefixes(state).nextSetBit(0); prefix >= 0; prefix = prefixes.getPrefixes(state).nextSetBit(prefix + 1)) {
                if (isExtension) {
                    extTerminalUses[extensionId][prefix] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_PREFIX;
                } else {
                    hostTerminalUses[prefix] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_PREFIX;
                }
                if (prefix != hostFragment.fullSpec.getEOFTerminal()) {
                    prefixMaps[state + stateOffset][prefix] = SingleDFAEngine.newBitVec(isExtension ? extTerminalLengths[extensionId] : hostTerminalLength);
                    prefixMaps[state + stateOffset][prefix].or(prefixes.getFollowingTerminals(state, prefix));
                }
            }
        }
    }

    private void fillMarkingTerminalMetaData(List<MarkingTerminalData> markingTerminalDatas) {
        markingTerminalShiftableSets = new BitSet[totalStateCount];

        markingTerminalShiftableUnion = SingleDFAEngine.newBitVec(markingTerminalCount);
        markingTerminalShiftableUnion.set(0, markingTerminalCount);

        for (int state = 0; state < hostStateCount; state++) {
            markingTerminalShiftableSets[state] = SingleDFAEngine.newBitVec(markingTerminalCount);

            BitSet stateInitNTs = hostFragment.initNTs[state];
            for (int nt = stateInitNTs.nextSetBit(0); nt >= 0; nt = stateInitNTs.nextSetBit(nt + 1)) {
                for (MarkingTerminalData mtData: markingTerminalDatas) {
                    if (nt == mtData.hostLHS) {
                        parseTable[state][mtData.endIndex] = SingleDFAEngine.newAction(SingleDFAEngine.STATE_SHIFT, mtData.offsetTransitionState);
                        markingTerminalShiftableSets[state].set(mtData.endIndex - markingTerminalOffset);
                    }
                }
            }
            Map<Integer, Set<Integer>> stateLASources = hostFragment.laSources.get(state);
            for (MarkingTerminalData mtData: markingTerminalDatas) {
                Set<Integer> productions = stateLASources.get(mtData.hostLHS);
                if (productions != null && !productions.isEmpty()) {
                    for (int production: productions) {
                        int newProductionIndex = productionMap.get(0).get(production);
                        parseTable[state][mtData.endIndex] = SingleDFAEngine.newAction(SingleDFAEngine.STATE_REDUCE, newProductionIndex);
                        markingTerminalShiftableSets[state].set(mtData.endIndex - markingTerminalOffset);
                    }
                }
            }
        }

        for (int extensionId = 0; extensionId < extensionCount; extensionId++) {
            ExtensionFragmentData fragment = extensionFragments.get(extensionId);
            for (int state = 0; state < fragment.appendedExtensionTable.size(); state++) {
                int offsetState = state + extStateOffset[extensionId];

                markingTerminalShiftableSets[offsetState] = SingleDFAEngine.newBitVec(markingTerminalCount);

                BitSet stateInitNTs = fragment.initNTs[state];
                for (int nt = stateInitNTs.nextSetBit(0); nt >= 0; nt = stateInitNTs.nextSetBit(nt + 1)) {
                    for (MarkingTerminalData mtData: markingTerminalDatas) {
                        if (nt == mtData.hostLHS) {
                            parseTable[offsetState][mtData.endIndex] = SingleDFAEngine.newAction(SingleDFAEngine.STATE_SHIFT, mtData.offsetTransitionState);
                            markingTerminalShiftableSets[offsetState].set(mtData.endIndex - markingTerminalOffset);
                        }
                    }
                }
                Map<Integer, Set<Integer>> stateLASources = fragment.laSources.get(state);
                for (MarkingTerminalData mtData: markingTerminalDatas) {
                    Set<Integer> productions = stateLASources.get(mtData.hostLHS);
                    if (productions != null && !productions.isEmpty()) {
                        for (int production: productions) {
                            int newProductionIndex = 0;
                            if (production < 0) { // ext production
                                newProductionIndex = productionMap.get(extensionId + 1).get(ExtensionMappingSpec.decodeExtensionIndex(production));
                            } else {
                                newProductionIndex = productionMap.get(0).get(production);
                            }
                            parseTable[offsetState][mtData.endIndex] = SingleDFAEngine.newAction(SingleDFAEngine.STATE_REDUCE, newProductionIndex);
                            markingTerminalShiftableSets[offsetState].set(mtData.endIndex - markingTerminalOffset);
                        }
                    }
                }
            }
        }

        // TODO reduce code dup
    }

    private boolean isHostFragment(int fragmentId) {
        return fragmentId == 0;
    }

    private int getFragmentIdFromState(int state) {
        int fragmentId = 0;
        for (int i = 0; i < extensionCount; i++) {
            if (state < extStateOffset[i]) {
                break;
            } else {
                fragmentId += 1;
            }
        }
        return fragmentId;
    }

    private void makeProductionLengths() {
        productionLengths = new int[totalProductionCount];

        for (Map.Entry<Integer, Pair<Integer, Integer>> entry : productionMapBack.entrySet()) {
            int index = entry.getKey();
            int fragment = entry.getValue().first();
            int fragmentIndex = entry.getValue().second();

            if (fragment == 0) {
                productionLengths[index] = hostFragment.fullSpec.pr.getRHSLength(fragmentIndex);
            } else {
                productionLengths[index] = extensionFragments.get(fragment - 1).extensionMappingSpec.pr.getRHSLength(fragmentIndex);
            }
        }
    }

    private void makeProductionLHSs() {
        productionLHSs = new int[totalProductionCount];

        for (Map.Entry<Integer, Pair<Integer, Integer>> entry : productionMapBack.entrySet()) {
            int index = entry.getKey();
            int fragment = entry.getValue().first();
            int fragmentIndex = entry.getValue().second();

            if (fragment == 0) {
                productionLHSs[index] = hostFragment.fullSpec.pr.getLHS(fragmentIndex);
            } else {
                ExtensionMappingSpec spec = extensionFragments.get(fragment - 1).extensionMappingSpec;
                int encodedLHS = spec.untranslateAndOffsetComposedSymbol(spec.pr.getLHS(fragmentIndex));
                productionLHSs[index] = encodedLHS < 0 ? spec.decodeAndTableOffsetExtensionIndex(encodedLHS) : encodedLHS;
            }
        }
    }

    private void writeHashes(PrintStream out) throws IOException {
        ByteArrayOutputStream stringOut = new ByteArrayOutputStream();
        for (ObjectToHash obj: objectsToHash) {
            writeHash(obj.obj, obj.name, stringOut, out);
        }
    }

    private void writeHash(Object obj, String prefix, ByteArrayOutputStream stringOut, PrintStream out) throws IOException {
        stringOut.reset();
        ObjectOutputStream outp = new ObjectOutputStream(stringOut);
        outp.writeObject(obj);
        out.println("public static final byte[] " + prefix + "Hash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
    }

    private void writeStaticMemberInitializations(PrintStream out) {
        // TODO finish

        out.println("  public static void initArrays() throws " + IOException.class.getName() + "," + ClassNotFoundException.class.getName() + " {");
        for (ObjectToHash obj: objectsToHash) {
            out.println("    " + obj.name + " = (" + obj.type + ") " + ByteArrayEncoder.class.getName() + ".readHash(" + obj.name + "Hash);");
        }
        out.println("  }");

        out.println("  static {");
        out.println("    try { initArrays(); }");
        out.println("  }");
    }
}
