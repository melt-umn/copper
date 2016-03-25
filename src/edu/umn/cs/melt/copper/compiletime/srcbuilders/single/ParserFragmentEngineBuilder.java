package edu.umn.cs.melt.copper.compiletime.srcbuilders.single;

import edu.umn.cs.melt.copper.compiletime.builders.*;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Regex;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PrecedenceGraph;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter;
import edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes;
import edu.umn.cs.melt.copper.runtime.engines.single.ParserFragmentEngine;
import edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine;
import edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData;
import edu.umn.cs.melt.copper.runtime.engines.single.semantics.SingleDFASemanticActionContainer;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
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
    private String rootType;
    private String errorType;

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

        rootType = hostFragment.symbolTable.getNonTerminal(hostFragment.fullSpec.pr.getRHSSym(hostFragment.fullSpec.getStartProduction(), 0)).getReturnType();
        rootType = rootType == null ? Object.class.getName() : rootType;
        errorType = CopperParserException.class.getName();

        out.println("public class " + parserName + " extends " + ParserFragmentEngine.class.getName() + "<" + rootType + "," + errorType + "> {");

        makeObjectsToBeHashed();

        printFixedMethods(out);

        printFragmentMethods(out);

        // TODO, Terminals enum and pushToken ...?

        // TODO write methods related to transition

        // TODO print Semantics class and implement related methods (including instatiate Semantics in startEngine)
        writeSemanticsClass(out);

        writeHashes(out);

        // TODO write "parserAncillaries" -- var decls and getters
        printParserAncillaryDecls(out);
        printParserAncillaryMethods(out);

        System.out.println(scannerAncillaries);

        writeStaticMemberInitializations(out);

        out.println("}");
    }

    private void writeSemanticsClass(PrintStream out) {
        out.println("  public class Semantics extends " + SingleDFASemanticActionContainer.class.getName() + "<" + errorType + "> {");

        /* TODO -- SemanticActionAuxCode? -- seems pretty important
        if(parser.getSemanticActionAuxCode() != null) out.print(parser.getSemanticActionAuxCode());
        for(int attrN = spec.parserAttributes.nextSetBit(0);attrN >= 0;attrN = spec.parserAttributes.nextSetBit(attrN+1))
        {
            ParserAttribute attr = symbolTable.getParserAttribute(attrN);
            out.print("        public " + attr.getAttributeType() + " " + generateVariableName(attrN) + ";\n");
        }
        */

        out.println("    public Semantics() throws " + IOException.class.getName() + "," + errorType + " {");
        out.println("      runInit();");
        out.println("    }");

        out.println("    public void error(" + InputPosition.class.getName() + " pos," + String.class.getName() + " message) throws " + errorType + " {");
        out.println("      reportError(\"Error at \" + pos.toString() + \":\\n  \" + message);");
        out.println("    }");

        out.println("    public void runDefaultTermAction() throws " + IOException.class.getName() + "," + errorType + " {");
        // TODO ; if(parser.getDefaultTerminalCode() != null) out.println("            " + parser.getDefaultTerminalCode() + "");
        out.println("    }");

        out.println("    public void runDefaultProdAction() throws " + IOException.class.getName() + "," + errorType + " {");
        /// TODO ; if(parser.getDefaultProductionCode() != null) out.println("            " + parser.getDefaultProductionCode() + "");
        out.println("    }");

        out.println("    public void runInit() throws " + IOException.class.getName() + "," + errorType + " {");
        /* TODO fill parserInitCode
        if(parser.getParserInitCode() != null) out.print("            " + parser.getParserInitCode());// grammar.getParserSources().getParserAttrInitCode());
        for(int attrN = spec.parserAttributes.nextSetBit(0);attrN >= 0;attrN = spec.parserAttributes.nextSetBit(attrN+1))
        {
            ParserAttribute attr = symbolTable.getParserAttribute(attrN);
            if(attr.getCode() != null) out.print("            " + attr.getCode() + "\n");
        }
        */
        out.println("    }");

        writeRunSemanticAction(out);
        writeRunProductionSemanticAction(out);

        out.println("    public void runPostParseCode(" + Object.class.getName() + " __root) {");
        /* TODO fill runPostParseCode
        if(parser.getPostParseCode() != null && !QuotedStringFormatter.isJavaWhitespace(parser.getPostParseCode()))
        {
            out.println("      " + rootType + " root = (" + rootType + ") __root;");
            out.println("      " + parser.getPostParseCode());
        }
        */
        out.println("    }");

        out.println("  }");
    }

    private void writeRunSemanticAction(PrintStream out) {
        // Object runSemanticAction(InputPosition _pos, Object[] _children, int _prod)
        out.println("    public " + Object.class.getName() + " runSemanticAction(" + InputPosition.class.getName() + " _pos, " + Object.class.getName() + "[] _children, int _prod)");
        out.println("    throws " + IOException.class.getName() + "," + errorType + " {");
        out.println("      this._pos = _pos;");
        out.println("      this._children = _children;");
        out.println("      this._prod = _prod;");
        out.println("      this._specialAttributes = new " + SpecialParserAttributes.class.getName() + "(virtualLocation);");
        out.println("      " + Object.class.getName() + " RESULT = null;");
        out.println("      switch (_prod) {");
        for (Map.Entry<Integer, Pair<Integer, Integer>> entry : productionMapBack.entrySet()) {
            int p = entry.getKey();
            int fragment = entry.getValue().first();
            int fragmentIndex = entry.getValue().second();

            PSSymbolTable symbolTable = fragment == 0 ? hostFragment.symbolTable : extensionFragments.get(fragment - 1).extensionMappingSpec.extensionSymbolTable;
            String productionCode = symbolTable.getProduction(fragmentIndex).getCode();
            if (fragment == 0 && fragmentIndex == hostFragment.fullSpec.getStartProduction()) {
                continue;
            } else if (productionCode != null && !QuotedStringFormatter.isJavaWhitespace(productionCode)) {
                out.println("        case " + p + ":");
                out.println("          RESULT = runSemanticAction_p" + p + "();");
                out.println("          break;");
            }
        }
        out.println("        default:");
        out.println("          runDefaultProdAction();");
        out.println("          break;");
        out.println("      }");
        out.println("      return RESULT;");
        out.println("    }");

        out.println("    public " + Object.class.getName() + " runSemanticAction(" + InputPosition.class.getName() + " _pos," + SingleDFAMatchData.class.getName() + " _terminal)");
        out.println("    throws " + IOException.class.getName() + "," + errorType + " {");
        out.println("      this._pos = _pos;");
        out.println("      this._terminal = _terminal;");
        out.println("      this._specialAttributes = new " + SpecialParserAttributes.class.getName() + "(virtualLocation);");
        out.println("      String lexeme = _terminal.lexeme;");
        out.println("      " + Object.class.getName() + " RESULT = null;");
        out.println("      switch(_terminal.firstTerm) {");
        for (int fragmentId = 0; fragmentId < fragmentCount; fragmentId++) {
            int extensionId = fragmentId - 1;
            boolean isExtension = fragmentId != 0;
            ExtensionMappingSpec extSpec = isExtension ? extensionFragments.get(extensionId).extensionMappingSpec : null;
            BitSet terminals = isExtension ? extSpec.extensionTerminalIndices : hostFragment.fullSpec.terminals;
            PSSymbolTable symbolTable = isExtension ? extSpec.extensionSymbolTable : hostFragment.symbolTable;
            for (int t = terminals.nextSetBit(0); t >= 0; t = terminals.nextSetBit(t + 1)) {
                if (!isExtension && t == hostFragment.fullSpec.getEOFTerminal()) {
                    continue;
                } else {
                    String code = symbolTable.getTerminal(t).getCode();
                    if (code != null && !QuotedStringFormatter.isJavaWhitespace(code)) {
                        int offsetT = isExtension ? extSpec.tableOffsetExtensionIndex(t) : t;
                        out.println("        case " + offsetT + ":");
                        out.println("          RESULT = runSemanticAction_t" + offsetT + "(lexeme);");
                        out.println("          break;");
                    }
                }
            }
        }
        out.println("        default:");
        out.println("          runDefaultTermAction();");
        out.println("          break;");
        out.println("      }");
        out.println("      return RESULT;");
        out.println("    }");
    }

    private void writeRunProductionSemanticAction(PrintStream out) {
        for (Map.Entry<Integer, Pair<Integer, Integer>> entry : productionMapBack.entrySet()) {
            int p = entry.getKey();
            int fragment = entry.getValue().first();
            int fragmentP = entry.getValue().second();

            boolean isExtension = fragment > 0;

            ExtensionMappingSpec extSpec = isExtension ? extensionFragments.get(fragment - 1).extensionMappingSpec : null;
            PSSymbolTable symbolTable = isExtension ? extSpec.extensionSymbolTable : hostFragment.symbolTable;
            String productionCode = symbolTable.getProduction(fragmentP).getCode();
            ParserSpec.ProductionData pr = isExtension ? extSpec.pr : hostFragment.fullSpec.pr;

            if (fragment == 0 && fragmentP == hostFragment.fullSpec.getStartProduction()) {
                continue;
            } else if (productionCode == null || QuotedStringFormatter.isJavaWhitespace(productionCode)) {
                continue;
            }

            //String returnType = symbolTable.getNonTerminal(pr.getLHS(fragmentP)).getReturnType();
            String returnType = null;
            if (isExtension) {
                int tableLHS = pr.getLHS(fragmentP);
                if (tableLHS > extSpec.extensionSymbolTableOffset) {
                    returnType = symbolTable.getNonTerminal(tableLHS - extSpec.extensionSymbolTableOffset).getReturnType();
                } else {
                    returnType = hostFragment.symbolTable.getNonTerminal(tableLHS).getReturnType();
                }
            } else {
                returnType = symbolTable.getNonTerminal(pr.getLHS(fragmentP)).getReturnType();
            }
            returnType = returnType == null ? Object.class.getName() : returnType;

            out.println("    public " + returnType + " runSemanticAction_p" + p + "() throws " + errorType + " {");
            if (symbolTable.getProduction(fragmentP).getRhsVarNames() != null) {
                int k = 0;
                for (String var : symbolTable.getProduction(fragmentP).getRhsVarNames()) {
                    if(var != null) {
                        int sym = pr.getRHSSym(fragmentP, k);
                        String type = null;

                        if (isExtension && sym >= extSpec.extensionSymbolTableOffset) {
                            int extSym = sym - extSpec.extensionSymbolTableOffset;
                            if (extSpec.extensionTerminalIndices.get(extSym)) {
                                type = symbolTable.getTerminal(sym).getReturnType();
                            } else if (extSpec.extensionNonterminalIndices.get(extSym)) {
                                type = symbolTable.getNonTerminal(sym).getReturnType();
                            }
                        } else {
                            if (hostFragment.fullSpec.terminals.get(sym)) {
                                type = symbolTable.getTerminal(sym).getReturnType();
                            } else if (hostFragment.fullSpec.nonterminals.get(sym)) {
                                type = symbolTable.getNonTerminal(sym).getReturnType();
                            }
                        }
                        type = type == null ? Object.class.getName() : type;
                        out.print("            ");
                        String suppressWarnings = type.contains("<") ? "@SuppressWarnings(\"unchecked\") " : "";
                        out.println("      " + suppressWarnings + type + " " + var + " = (" + type + ") _children[" + k + "];");
                    }
                    k++;
                }
            }
            out.print("      " + returnType + " RESULT = null;\n");
            out.print("      " + productionCode + "\n");
            out.print("      return RESULT;\n");
            out.print("    }\n");
        }
    }

    private void writeRunTerminalSemanticAction(PrintStream out) {
        for (int fragmentId = 0; fragmentId < fragmentCount; fragmentId++) {
            int extensionId = fragmentId - 1;
            boolean isExtension = fragmentId != 0;
            ExtensionMappingSpec extSpec = isExtension ? extensionFragments.get(extensionId).extensionMappingSpec : null;
            BitSet terminals = isExtension ? extSpec.extensionTerminalIndices : hostFragment.fullSpec.terminals;
            PSSymbolTable symbolTable = isExtension ? extSpec.extensionSymbolTable : hostFragment.symbolTable;

            for (int t = terminals.nextSetBit(0); t >= 0; t = terminals.nextSetBit(t + 1)) {
                if (!isExtension && t == hostFragment.fullSpec.getEOFTerminal()) {
                    continue;
                } else {
                    String code = symbolTable.getTerminal(t).getCode();
                    if (code != null && !QuotedStringFormatter.isJavaWhitespace(code)) {
                        String returnType = symbolTable.getTerminal(t).getReturnType();
                        returnType = returnType == null ? Object.class.getName() : null;

                        int offsetT = isExtension ? extSpec.tableOffsetExtensionIndex(t) : t;
                        out.println("    public " + returnType + " runSemanticAction_t" + offsetT + "(final String lexeme)");
                        out.println("    throws " + errorType + " {");
                        out.println("      " + returnType + " RESULT = null;");
                        out.println("      " + code + "");
                        out.println("      return RESULT;");
                        out.println("    }");
                    }
                }
            }
        }
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

    private void printFixedMethods(PrintStream out) {
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
                if (extSpec != null) {
                    isTerminal = extSpec.isTableOffsetTerminal(t);
                    isNonterminal = extSpec.isTableOffsetNonterminal(t);
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
