package edu.umn.cs.melt.copper.compiletime.srcbuilders.single;

import edu.umn.cs.melt.copper.compiletime.builders.ExtensionFragmentData;
import edu.umn.cs.melt.copper.compiletime.builders.ExtensionMappingSpec;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.pipeline.StandardSpecCompilerReturnData;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder;
import edu.umn.cs.melt.copper.runtime.engines.single.ParserFragmentEngine;
import edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

/**
 * @author Kevin Viratyosin
 */
public class ParserFragmentEngineBuilder {

    private StandardSpecCompilerReturnData hostFragment;
    private List<ExtensionFragmentData> extensionFragments;

    private int extensionCount;
    private int fragmentCount;

    private int[] extStateOffset;

    private GeneralizedDFA markingTerminalScannerDFA;

    private int[][] parseTable;
    private int[][][] deltas;
    private int[][] productionLengths;
    private int hostStateCount;

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

    public ParserFragmentEngineBuilder(StandardSpecCompilerReturnData hostFragment, List<ExtensionFragmentData> extensionFragments) {
        this.hostFragment = hostFragment;
        this.extensionFragments = extensionFragments;

        this.extensionCount = extensionFragments.size();
        this.fragmentCount = this.extensionCount + 1;

        // TODO generate marking terminal scanner
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

        // TODO, Terminals enum and pushToken ...?

        // TODO write methods related to transition

        // TODO print Semantics class and implement related methods (including instatiate Semantics in startEngine)

        makeObjectsToBeHashed();
        writeHashes(out);

        // TODO write "parserAncillaries" -- var decls and getters
        printParserAncillaryDecls(out);

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

    private void printParserAncillaryDecls(PrintStream out) {
        for (ObjectToHash obj: objectsToHash) {
            out.println("  private static " + obj.type + " " + obj.name + ";");
        }
        // TODO finish
    }

    private void printParserAncillaryMethods(PrintStream out) {
        out.println("  protected int[][] getParseTable() {");
        out.println("    return parseTable;");
        out.println("  }");

        out.println("  protected int[][] getFragmentTransitionTable(int fragmentId) {");
        out.println("    return deltas[fragmentId];");
        out.println("  }");

        out.println("  protected int[] getProductionLengths(int fragmentId) {");
        out.println("    return productionLengths[fragmentId]");
        out.println("  }");

        // TODO finish
    }

    private void makeObjectsToBeHashed() {
        objectsToHash = new ArrayList<ObjectToHash>();

        makeParseTable();
        objectsToHash.add(new ObjectToHash(parseTable, "int[][]", "parseTable"));

        deltas[0] = markingTerminalScannerDFA.getTransitions();
        for (int i = 0; i < extensionCount; i++) {
            deltas[i + 1] = extensionFragments.get(i).scannerDFA.getTransitions();
        }
        objectsToHash.add(new ObjectToHash(deltas, "int[][][]", "deltas"));

        // TODO make productionLengths
        makeProductionLengths();
        objectsToHash.add(new ObjectToHash(productionLengths, "int[][]", "productionLengths"));

        // TODO finish
    }

    private static class MarkingTerminalData {
        public int extensionId;
        public int extensionTerminal;
        public int hostLHS;
        public int offsetTransitionState;
        public MarkingTerminalData(int extensionId, int extensionTerminal, int hostLHS, int offsetTransitionState) {
            this.extensionId = extensionId;
            this.extensionTerminal = extensionTerminal;
            this.hostLHS = hostLHS;
            this.offsetTransitionState = offsetTransitionState;
        }
    }

    private void makeParseTable() {
        hostStateCount = hostFragment.parseTable.size();
        int stateTotal = hostStateCount;
        int maxTableSymbols = hostFragment.fullSpec.nonterminals.length();
        ArrayList<MarkingTerminalData> markingTerminalDatas = new ArrayList<MarkingTerminalData>();

        extStateOffset = new int[extensionCount];
        for (int i = 0; i < extensionCount; i++) {
            ExtensionFragmentData fragment = extensionFragments.get(i);
            ExtensionMappingSpec spec = extensionFragments.get(i).extensionMappingSpec;

            extStateOffset[i] = stateTotal;
            stateTotal += fragment.appendedExtensionTable.size();
            int extTableWidth = spec.tableOffsetExtensionIndex(spec.extensionNonterminalIndices.length());
            if (extTableWidth > maxTableSymbols) {
                maxTableSymbols = extTableWidth;
            }

            Set<Integer> markingTerminals = fragment.markingTerminalLHS.keySet();
            for (int mt: markingTerminals) {
                int lhs = fragment.markingTerminalLHS.get(mt);
                int offsetState = fragment.markingTerminalStates.get(mt) + extStateOffset[i];
                markingTerminalDatas.add(new MarkingTerminalData(i, mt, lhs, offsetState));
            }
        }

        parseTable = new int[stateTotal][maxTableSymbols + markingTerminalDatas.size()];
        copyParseTable(hostFragment.parseTable, hostFragment.fullSpec.terminals, hostFragment.fullSpec.nonterminals, -1, null, null);
        for (int i = 0; i < extensionCount; i++) {
            ExtensionFragmentData fragment = extensionFragments.get(i);
            ExtensionMappingSpec spec = extensionFragments.get(i).extensionMappingSpec;

            copyParseTable(fragment.appendedExtensionTable, spec.hostTerminalIndices, spec.hostNonterminalIndices, i, spec.extensionTerminalIndices, spec.extensionNonterminalIndices);
        }

        fillMarkingTerminalMetaData(markingTerminalDatas, maxTableSymbols);
    }

    private void copyParseTable(LRParseTable table, BitSet hostTerminals, BitSet hostNonterminals, int extensionId, BitSet extTerminals, BitSet extNonterminals) {
        int symType;

        boolean isExtension = extensionId >= 0;
        ExtensionFragmentData extensionFragmentData = null;
        ExtensionMappingSpec extSpec = null;
        if (isExtension) {
            extensionFragmentData = extensionFragments.get(extensionId);
            extSpec = extensionFragmentData.extensionMappingSpec;
        }

        for (int state = 0; state < table.size(); state++) {
            for (int t = table.getValidLA(state).nextSetBit(0); t >= 0; t = table.getValidLA(state).nextSetBit(t+1)) {
                boolean isTerminal, isNonterminal;
                if (extSpec != null && t >= extSpec.extensionSymbolTableOffset) {
                    int extensionIndex = t - extSpec.extensionSymbolTableOffset;
                    isTerminal = hostTerminals.get(extensionIndex);
                    isNonterminal = hostNonterminals.get(extensionIndex);
                } else {
                    isTerminal = hostTerminals.get(t);
                    isNonterminal = hostNonterminals.get(t);
                }

                int actionParameter = table.getActionParameter(state, t);

                if(isTerminal) {
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
                        // TODO Q: translate action parameter (production)? -- A: no, (TODO) handle on the other side for now
                    } else {
                        symType = SingleDFAEngine.STATE_ERROR;
                    }

                    parseTable[state][t] = SingleDFAEngine.newAction(symType, actionParameter);
                } else if (isNonterminal) {
                    if (isExtension && actionParameter < 0) { // ext state
                        actionParameter = ExtensionMappingSpec.decodeExtensionIndex(actionParameter) + extStateOffset[extensionId];
                    }
                    parseTable[state][t] = SingleDFAEngine.newAction(SingleDFAEngine.STATE_GOTO, actionParameter);
                }
            }
        }
    }

    private void fillMarkingTerminalMetaData(List<MarkingTerminalData> markingTerminalDatas, int markingTerminalOffset) {
        // TODO finish
    }

    private void makeProductionLengths() {
        productionLengths = new int[fragmentCount][];
        // TODO finish
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
