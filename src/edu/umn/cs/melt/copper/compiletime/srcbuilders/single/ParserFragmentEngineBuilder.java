package edu.umn.cs.melt.copper.compiletime.srcbuilders.single;

import edu.umn.cs.melt.copper.compiletime.builders.ExtensionFragmentData;
import edu.umn.cs.melt.copper.compiletime.pipeline.StandardSpecCompilerReturnData;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder;
import edu.umn.cs.melt.copper.runtime.engines.single.ParserFragmentEngine;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Viratyosin
 */
public class ParserFragmentEngineBuilder {

    private StandardSpecCompilerReturnData hostFragment;
    private List<ExtensionFragmentData> extensionFragments;

    private int extensionCount;
    private int fragmentCount;

    private GeneralizedDFA markingTerminalScannerDFA;

    private int[][][] deltas;
    private int[][] productionLengths;

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
        out.println("  private static int[][][] deltas;");
        out.println("  private static int[][] productionLengths;");
        // TODO finish
    }

    private void printParserAncillaryMethods(PrintStream out) {
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
