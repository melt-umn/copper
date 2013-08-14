package edu.umn.cs.melt.copper.compiletime.srcbuilders.single;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementType;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter;
import edu.umn.cs.melt.copper.runtime.engines.CopperTerminalEnum;
import edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes;
import edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine;
import edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData;
import edu.umn.cs.melt.copper.runtime.engines.single.semantics.SingleDFASemanticActionContainer;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public class SingleDFAEngineBuilder
{
	private PSSymbolTable symbolTable;
	private ParserSpec spec;
	private LRLookaheadAndLayoutSets layouts;
	private LRParseTable builtParseTable;
	private TransparentPrefixes prefixes;
	private GeneralizedDFA scannerDFA;
	private SingleScannerDFAAnnotations scannerDFAAnnotations;
	
	
	/** Names of terminals, nonterminals, productions, etc. */
	private String[] symbolNames;
	/** Display names of terminals, nonterminals, productions, etc. */
	private String[] symbolDisplayNames;
	/** Lengths of productions, types of symbols. */
	private int[] symbolNumbers;
	/** Symbols on the left-hand sides of productions. */
	private int[] productionLHSs;
	
	/** Parse actions. */
	private int[][] parseTable;
	/** Shiftable sets. */
	private BitSet[] shiftableSets;
	/** Layout sets. */
	private BitSet[] layoutSets;
	/** Prefix sets. */
	private BitSet[] prefixSets;
	/** Maps of prefix terminals. */
	private BitSet[][] prefixMaps;
	/** What terminals are used as: only layout, only prefix, only shiftable, or versatile. */
	private int[] terminalUses;
	
	/** Shiftable union --- all terminals with a parse action,
	 * plus all layout and prefixes that can appear before them. */
	private BitSet shiftableUnion;
	
	private int[][] delta;

	/* Counts for building arrays statically. */
	private int TERMINAL_COUNT;
	private int GRAMMAR_SYMBOL_COUNT;
	private int SYMBOL_COUNT;
	private int PARSER_STATE_COUNT;
	private int SCANNER_STATE_COUNT;
	private int DISAMBIG_GROUP_COUNT;
	
	private int SCANNER_START_STATENUM;
	private int PARSER_START_STATENUM;
	private int EOF_SYMNUM;
	private int EPS_SYMNUM;

	public SingleDFAEngineBuilder(PSSymbolTable symbolTable,
			ParserSpec spec, LRLookaheadAndLayoutSets layouts,
			LRParseTable parseTable, TransparentPrefixes prefixes,
			GeneralizedDFA scannerDFA,
			SingleScannerDFAAnnotations scannerDFAAnnotations)
	{
		this.symbolTable = symbolTable;
		this.spec = spec;
		this.layouts = layouts;
		this.builtParseTable = parseTable;
		this.prefixes = prefixes;
		this.scannerDFA = scannerDFA;
		this.scannerDFAAnnotations = scannerDFAAnnotations;
	}

	public void buildLALREngine(PrintStream out, String packageDecl,
			String importDecls, String parserName, String scannerName,
			String parserAncillaries, String scannerAncillaries)
	throws IOException, CopperException
	{
		ParserBean parser = symbolTable.getParser(spec.parser);
		
		if(packageDecl.equals("") &&
		   importDecls.equals("") &&
		   parser.getPreambleCode() != null) packageDecl = parser.getPreambleCode();
		else if(parser.getPreambleCode() != null) importDecls += "\n" + parser.getPreambleCode();
		
		String rootType = symbolTable.getNonTerminal(spec.pr.getRHSSym(spec.getStartProduction(),0)).getReturnType();
		if(rootType == null) rootType = Object.class.getName();
		String errorType = CopperParserException.class.getName();

	    parserAncillaries += "		private static int TERMINAL_COUNT;\n";
	    parserAncillaries += "		private static int GRAMMAR_SYMBOL_COUNT;\n";
	    parserAncillaries += "		private static int SYMBOL_COUNT;\n";
	    parserAncillaries += "		private static int PARSER_STATE_COUNT;\n";
	    parserAncillaries += "		private static int SCANNER_STATE_COUNT;\n";
	    parserAncillaries += "		private static int DISAMBIG_GROUP_COUNT;\n";
	    parserAncillaries += "		\n";
	    parserAncillaries += "		private static int SCANNER_START_STATENUM;\n";
	    parserAncillaries += "		private static int PARSER_START_STATENUM;\n";
	    parserAncillaries += "		private static int EOF_SYMNUM;\n";
	    parserAncillaries += "		private static int EPS_SYMNUM;\n";
	    parserAncillaries += "		\n";
	    parserAncillaries += "		private static String[] symbolNames;\n";
	    parserAncillaries += "		private static String[] symbolDisplayNames;\n";
	    parserAncillaries += "		private static int[] symbolNumbers;\n";
	    parserAncillaries += "		private static int[] productionLHSs;\n";
	    parserAncillaries += "		\n";
	    parserAncillaries += "		private static int[][] parseTable;\n";
	    parserAncillaries += "		private static " + BitSet.class.getName() + "[] shiftableSets;\n";
	    parserAncillaries += "		private static " + BitSet.class.getName() + "[] layoutSets;\n";
	    parserAncillaries += "		private static " + BitSet.class.getName() + "[] prefixSets;\n";
	    //parserAncillaries += "		private static " + BitSet.class.getName() + "[][] layoutMaps;\n";
	    parserAncillaries += "		private static " + BitSet.class.getName() + "[][] prefixMaps;\n";
	    parserAncillaries += "		private static int[] terminalUses;\n";
	    parserAncillaries += "		\n";
	    parserAncillaries += "		private static " + BitSet.class.getName() + "[] disambiguationGroups;\n";
	    parserAncillaries += "		\n";
	    parserAncillaries += "		private static " + BitSet.class.getName() + " shiftableUnion;\n";
	    parserAncillaries += "		\n";
	    parserAncillaries += "		private static " + BitSet.class.getName() + "[] acceptSets,rejectSets,possibleSets;\n";
	    parserAncillaries += "		\n";
	    parserAncillaries += "		private static int[][] delta;\n";
	    parserAncillaries += "		private static int[] cmap;\n";
	    parserAncillaries += "		\n";
	    parserAncillaries += "		public int getTERMINAL_COUNT() {\n";
	    parserAncillaries += "			return TERMINAL_COUNT;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int getGRAMMAR_SYMBOL_COUNT() {\n";
	    parserAncillaries += "			return GRAMMAR_SYMBOL_COUNT;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int getSYMBOL_COUNT() {\n";
	    parserAncillaries += "			return SYMBOL_COUNT;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int getPARSER_STATE_COUNT() {\n";
	    parserAncillaries += "			return PARSER_STATE_COUNT;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int getSCANNER_STATE_COUNT() {\n";
	    parserAncillaries += "			return SCANNER_STATE_COUNT;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int getDISAMBIG_GROUP_COUNT() {\n";
	    parserAncillaries += "			return DISAMBIG_GROUP_COUNT;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int getSCANNER_START_STATENUM() {\n";
	    parserAncillaries += "			return SCANNER_START_STATENUM;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int getPARSER_START_STATENUM() {\n";
	    parserAncillaries += "			return PARSER_START_STATENUM;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int getEOF_SYMNUM() {\n";
	    parserAncillaries += "			return EOF_SYMNUM;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int getEPS_SYMNUM() {\n";
	    parserAncillaries += "			return EPS_SYMNUM;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public String[] getSymbolNames() {\n";
	    parserAncillaries += "			return symbolNames;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public String[] getSymbolDisplayNames() {\n";
	    parserAncillaries += "			return symbolDisplayNames;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int[] getSymbolNumbers() {\n";
	    parserAncillaries += "			return symbolNumbers;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int[] getProductionLHSs() {\n";
	    parserAncillaries += "			return productionLHSs;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int[][] getParseTable() {\n";
	    parserAncillaries += "			return parseTable;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public " + BitSet.class.getName() + "[] getShiftableSets() {\n";
	    parserAncillaries += "			return shiftableSets;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public " + BitSet.class.getName() + "[] getLayoutSets() {\n";
	    parserAncillaries += "			return layoutSets;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public " + BitSet.class.getName() + "[] getPrefixSets() {\n";
	    parserAncillaries += "			return prefixSets;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public " + BitSet.class.getName() + "[][] getLayoutMaps() {\n";
	    //parserAncillaries += "			return layoutMaps;\n";
	    parserAncillaries += "			return null;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public " + BitSet.class.getName() + "[][] getPrefixMaps() {\n";
	    parserAncillaries += "			return prefixMaps;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int[] getTerminalUses() {\n";
	    parserAncillaries += "			return terminalUses;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public " + BitSet.class.getName() + "[] getDisambiguationGroups() {\n";
	    parserAncillaries += "			return disambiguationGroups;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public " + BitSet.class.getName() + " getShiftableUnion() {\n";
	    parserAncillaries += "			return shiftableUnion;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public " + BitSet.class.getName() + "[] getAcceptSets() {\n";
	    parserAncillaries += "			return acceptSets;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public " + BitSet.class.getName() + "[] getRejectSets() {\n";
	    parserAncillaries += "			return rejectSets;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public " + BitSet.class.getName() + "[] getPossibleSets() {\n";
	    parserAncillaries += "			return possibleSets;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int[][] getDelta() {\n";
	    parserAncillaries += "			return delta;\n";
	    parserAncillaries += "		}\n";
	    parserAncillaries += "		public int[] getCmap() {\n";
	    parserAncillaries += "			return cmap;\n";
	    parserAncillaries += "		}	\n";
	    
		parserAncillaries += "    public " + rootType + " parse(" + Reader.class.getName() + " input,String inputName)\n";
	    parserAncillaries += "    throws " + IOException.class.getName() + "," + errorType + "\n";
	    parserAncillaries += "    {\n"; 
	    //parserAncillaries += "    this.reporter = reporter;\n";
	    parserAncillaries += "    this.charBuffer = " + ScannerBuffer.class.getName() + ".instantiate(input);\n";
	    parserAncillaries += "    setupEngine();\n";
	    parserAncillaries += "    startEngine(" + InputPosition.class.getName() + ".initialPos(inputName));\n";
	    parserAncillaries += "    " + rootType + " parseTree = (" + rootType + ") runEngine();\n";
	    parserAncillaries += "    return parseTree;\n";
	    parserAncillaries += "    }\n";
	    parserAncillaries += "\n";
		if(parser.getParserClassAuxCode() != null) parserAncillaries += parser.getParserClassAuxCode();

		out.print("/*\n * Built at " + new java.util.Date(System.currentTimeMillis()) + "\n");
		out.print(" * by Copper version " + ParserCompiler.VERSION + ",\n");
		out.print(" *      revision " + ParserCompiler.REVISION + ",\n");
		out.print(" *      build " + ParserCompiler.BUILD + "\n */\n");
		
		out.print(packageDecl + "\n");
		out.print(importDecls + "\n");

		out.print("\n");
		
		out.print("public class " + parserName + " extends " + SingleDFAEngine.class.getName() + "<" + rootType + "," + errorType + ">\n");
		out.print("{\n");
		out.print("    protected String formatError(String error)\n");
		out.print("    {\n");
		out.print("    	   String location = \"\";\n");
	    out.print("        location += \"line \" + virtualLocation.getLine() + \", column \" + virtualLocation.getColumn();\n");
	    out.print("        if(currentState.pos.getFileName().length() > 40) location += \"\\n         \";\n");
	    out.print("        location += \" in file \" + virtualLocation.getFileName();\n");
	    out.print("        location += \"\\n         (parser state: \" + currentState.statenum + \"; real character index: \" + currentState.pos.getPos() + \")\";\n");
	    out.print("        return \"Error at \" + location + \":\\n  \" + error;\n");
	    out.print("    }\n");
	    out.print("    protected void reportError(String message)\n");
		out.print("    throws " + errorType + "\n");
	    out.print("    {\n");
	    out.print("        throw new " + CopperParserException.class.getName() + "(message);\n");
	    out.print("    }\n");
	    out.print("    protected void reportSyntaxError()\n");
	    out.print("    throws " + errorType + "\n");
	    out.print("    {\n");
	    out.print("    " + ArrayList.class.getName() + "<String> expectedTerminalsReal = bitVecToRealStringList(getShiftableSets()[currentState.statenum]);\n");
	    out.print("    " + ArrayList.class.getName() + "<String> expectedTerminalsDisplay = bitVecToDisplayStringList(getShiftableSets()[currentState.statenum]);\n");
	    out.print("    " + ArrayList.class.getName() + "<String> matchedTerminalsReal = bitVecToRealStringList(disjointMatch.terms);\n");
	    out.print("    " + ArrayList.class.getName() + "<String> matchedTerminalsDisplay = bitVecToDisplayStringList(disjointMatch.terms);\n");
	    out.print("    throw new edu.umn.cs.melt.copper.runtime.logging.CopperSyntaxError(virtualLocation,currentState.pos,currentState.statenum,expectedTerminalsReal,expectedTerminalsDisplay,matchedTerminalsReal,matchedTerminalsDisplay);\n");
	    out.print("    }\n");
	    EOF_SYMNUM = spec.getEOFTerminal();
	    EPS_SYMNUM = -1;
	    TERMINAL_COUNT = spec.terminals.length();
		GRAMMAR_SYMBOL_COUNT = spec.nonterminals.length();
		SYMBOL_COUNT = spec.productions.length();
		PARSER_START_STATENUM = 1;

		/*i = 0;
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			lexGroupTransTable.put(group,i++);
		}*/
		DISAMBIG_GROUP_COUNT = spec.disambiguationFunctions.length() - spec.disambiguationFunctions.nextSetBit(0);
		PARSER_STATE_COUNT = builtParseTable.size();

		symbolNames = new String[SYMBOL_COUNT];
		symbolDisplayNames = new String[SYMBOL_COUNT];
		symbolNumbers = new int[SYMBOL_COUNT];
		productionLHSs = new int[SYMBOL_COUNT - GRAMMAR_SYMBOL_COUNT];
        parseTable = new int[PARSER_STATE_COUNT][GRAMMAR_SYMBOL_COUNT];
        shiftableSets = new BitSet[PARSER_STATE_COUNT];
        layoutSets = new BitSet[PARSER_STATE_COUNT];
        prefixSets = new BitSet[PARSER_STATE_COUNT];
        terminalUses = new int[TERMINAL_COUNT];
		prefixMaps = new BitSet[PARSER_STATE_COUNT][TERMINAL_COUNT];
		shiftableUnion = SingleDFAEngine.newBitVec(TERMINAL_COUNT);

		for(int i = 0;i < SYMBOL_COUNT;i++)
		{
			symbolNames[i] = generateVariableName(i);
			symbolDisplayNames[i] = symbolTable.get(i).getDisplayName();
		}

		for(int statenum = 0;statenum < builtParseTable.size();statenum++)
		{
			shiftableSets[statenum] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);

			shiftableSets[statenum].or(builtParseTable.getValidLA(statenum));
			shiftableUnion.or(builtParseTable.getValidLA(statenum));
			
			for(int t = builtParseTable.getValidLA(statenum).nextSetBit(0);t >= 0;t = builtParseTable.getValidLA(statenum).nextSetBit(t+1))
			{
				// TODO Remove nonterminals from the 'validLA' sets in the parse table.
				if(spec.terminals.get(t))
				{
					terminalUses[t] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_SHIFTABLE;
				}
			}
			
			int symType;
			
			for(int t = builtParseTable.getValidLA(statenum).nextSetBit(0);t >= 0;t = builtParseTable.getValidLA(statenum).nextSetBit(t+1))
			{
				if(spec.terminals.get(t))
				{
					byte actionType = builtParseTable.getActionType(statenum,t);
					
					if(actionType == LRParseTable.ERROR)
					{
						symType = SingleDFAEngine.STATE_ERROR;
					}
					else if(actionType == LRParseTable.ACCEPT && t == spec.getEOFTerminal())
					{
						symType = SingleDFAEngine.STATE_ACCEPT;
					}
					else if(actionType == LRParseTable.SHIFT)
					{
						symType = SingleDFAEngine.STATE_SHIFT;
					}
					else if(actionType == LRParseTable.REDUCE)
					{
						symType = SingleDFAEngine.STATE_REDUCE;
					}
					else
					{
						symType = SingleDFAEngine.STATE_ERROR;
					}
					
					parseTable[statenum][t] = SingleDFAEngine.newAction(symType,builtParseTable.getActionParameter(statenum,t));
				}
				else if(spec.nonterminals.get(t))
				{
					parseTable[statenum][t] = SingleDFAEngine.newAction(SingleDFAEngine.STATE_GOTO,builtParseTable.getActionParameter(statenum,t));
				}
			}
			
	
			layoutSets[statenum] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			layoutSets[statenum].or(layouts.getLayout(statenum));
			shiftableSets[statenum].or(layouts.getLayout(statenum));
			shiftableUnion.or(layouts.getLayout(statenum));
			
			for(int layout = layouts.getLayout(statenum).nextSetBit(0);layout >= 0;layout = layouts.getLayout(statenum).nextSetBit(layout+1))
			{
				terminalUses[layout] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_LAYOUT;
			}

			prefixSets[statenum] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			prefixSets[statenum].or(prefixes.getPrefixes(statenum));
			shiftableSets[statenum].or(prefixes.getPrefixes(statenum));
			shiftableUnion.or(prefixes.getPrefixes(statenum));
			
			for(int prefix = prefixes.getPrefixes(statenum).nextSetBit(0);prefix >= 0;prefix = prefixes.getPrefixes(statenum).nextSetBit(prefix+1))
			{
				terminalUses[prefix] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_PREFIX;
				if(prefix == spec.getEOFTerminal()) continue;
				prefixMaps[statenum][prefix] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
				prefixMaps[statenum][prefix].or(prefixes.getFollowingTerminals(statenum,prefix));
			}

			// TODO Remove nonterminals from the 'validLA' sets in the parse table.
			shiftableSets[statenum].andNot(spec.nonterminals);
		}

		// TODO Remove nonterminals from the 'validLA' sets in the parse table.
		shiftableUnion.andNot(spec.nonterminals);

		// To switch between scanner generation engines, change this line.
//		generateScanner(out);
//		generateScannerNew(out);
		
		out.print("    public static enum Terminals implements " + CopperTerminalEnum.class.getName() + "\n");
		out.print("    {\n");
		boolean first = true;
		for(int i = spec.terminals.nextSetBit(0);i >= 0;i = spec.terminals.nextSetBit(i+1))
		{
			if(i != EOF_SYMNUM)
			{
				if(first) first = false;
				else out.print(",\n");
				out.print("        " + symbolNames[i] + "(" + i + ")");
			}
		}
		out.print(";\n\n        private final int num;\n");
		out.print("        Terminals(int num) { this.num = num; }\n");
		out.print("        public int num() { return num; }\n");
		out.print("    }\n\n");
		
		out.print("    public void pushToken(Terminals t,String lexeme)\n");
		out.print("    {\n");
		out.print("        " + BitSet.class.getName() + " ts = new " + BitSet.class.getName() + "();\n");
		out.print("        ts.set(t.num());\n");
		out.print("        tokenBuffer.offer(new " + SingleDFAMatchData.class.getName() + "(ts,currentState.pos,currentState.pos,lexeme,new " + LinkedList.class.getName() + "<" + SingleDFAMatchData.class.getName() + ">()));\n");
		out.print("    }\n");
		
		out.print("    public void setupEngine()\n");
		out.print("    {\n");
		out.print("    }\n");

		out.print("    public int transition(int state,char ch)\n");
		out.print("    {\n");
		out.print("         return delta[state][cmap[ch]];\n");
		out.print("    }\n");

		SCANNER_STATE_COUNT = scannerDFA.stateCount();
		SCANNER_START_STATENUM = scannerDFA.getStartState();
		delta = scannerDFA.getTransitions();
		
		out.print("    public class Semantics extends " + SingleDFASemanticActionContainer.class.getName() + "<" + errorType + ">\n");
		out.print("    {\n");
		
		if(parser.getSemanticActionAuxCode() != null) out.print(parser.getSemanticActionAuxCode());
		
		for(int attrN = spec.parserAttributes.nextSetBit(0);attrN >= 0;attrN = spec.parserAttributes.nextSetBit(attrN+1))
		{
			ParserAttribute attr = symbolTable.getParserAttribute(attrN);
			out.print("        public " + attr.getAttributeType() + " " + generateVariableName(attrN) + ";\n");
		}
		out.print("\n");
		out.print("        public Semantics()\n");
		out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
	    out.print("        {\n");
		out.print("            runInit();\n");
		out.print("        }\n");
		out.print("\n");
		out.print("        public void error(" + InputPosition.class.getName() + " pos," + String.class.getName() + " message)\n");
		out.print("        throws " + errorType + "\n");
		out.print("        {\n");
		//out.print("            if(reporter.willHaveEffect(" + ErrorDegree.class.getName() + ".DEGREE_ERROR)) reporter.report(" + ErrorDegree.class.getName() + ".DEGREE_ERROR,pos.toString(),message);\n");
		out.print("            reportError(\"Error at \" + pos.toString() + \":\\n  \" + message);\n");
		out.print("        }\n");
		out.print("\n");
		out.print("        public void runDefaultTermAction()\n");
		out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
		out.print("        {\n");
		if(parser.getDefaultTerminalCode() != null) out.print("            " + parser.getDefaultTerminalCode() + "\n");
		out.print("        }\n");
	    out.print("        public void runDefaultProdAction()\n");
		out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
		out.print("        {\n");
		if(parser.getDefaultProductionCode() != null) out.print("            " + parser.getDefaultProductionCode() + "\n");
		out.print("        }\n");
		out.print("        public void runInit()\n");
		out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
		out.print("        {\n");
		if(parser.getParserInitCode() != null) out.print("            " + parser.getParserInitCode());// grammar.getParserSources().getParserAttrInitCode());
		for(int attrN = spec.parserAttributes.nextSetBit(0);attrN >= 0;attrN = spec.parserAttributes.nextSetBit(attrN+1))
		{
			ParserAttribute attr = symbolTable.getParserAttribute(attrN);
			if(attr.getCode() != null) out.print("            " + attr.getCode() + "\n");
		}
		out.print("        }\n");

		out.print("        public " + Object.class.getName() + " runSemanticAction(" + InputPosition.class.getName() + " _pos," + Object.class.getName() + "[] _children,int _prod)\n");
	    out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
		out.print("        {\n");
		out.print("            this._pos = _pos;\n");
		out.print("            this._children = _children;\n");
		out.print("            this._prod = _prod;\n");
		out.print("            this._specialAttributes = new " + SpecialParserAttributes.class.getName() + "(virtualLocation);\n");
		out.print("            " + Object.class.getName() + " RESULT = null;\n");
		out.print("            switch(_prod)\n");
		out.print("            {\n");
		for(int p = spec.productions.nextSetBit(0);p >= 0;p = spec.productions.nextSetBit(p+1))
		{
			if(p == spec.getStartProduction() ||
			   symbolTable.getProduction(p).getCode() == null ||
			   QuotedStringFormatter.isJavaWhitespace(symbolTable.getProduction(p).getCode()))
			{
				continue;
			}
			out.print("            case " + p + ":\n");
			out.print("                RESULT = runSemanticAction_" + p + "();\n");
			out.print("                break;\n");
		}
		out.print("            default:\n");
		out.print("        runDefaultProdAction();\n");
		out.print("                 break;\n");
		out.print("            }\n");
		out.print("            return RESULT;\n");
		out.print("        }\n");

		out.print("        public " + Object.class.getName() + " runSemanticAction(" + InputPosition.class.getName() + " _pos," + SingleDFAMatchData.class.getName() + " _terminal)\n");
	    out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
		out.print("        {\n");
		out.print("            this._pos = _pos;\n");
		out.print("            this._terminal = _terminal;\n");
		out.print("            this._specialAttributes = new " + SpecialParserAttributes.class.getName() + "(virtualLocation);\n");
		boolean lexemeUsed = false;
		for(int t = spec.terminals.nextSetBit(0);t >= 0;t = spec.terminals.nextSetBit(t+1))
		{
			if(t == spec.getEOFTerminal() ||
			   symbolTable.getTerminal(t).getCode() == null ||
			   QuotedStringFormatter.isJavaWhitespace(symbolTable.getTerminal(t).getCode()))
			{
				lexemeUsed = true;
				break;
			}
		}
		if(lexemeUsed) out.print("            String lexeme = _terminal.lexeme;\n");
		out.print("            " + Object.class.getName() + " RESULT = null;\n");
		out.print("            switch(_terminal.firstTerm)\n");
		out.print("            {\n");
		for(int t = spec.terminals.nextSetBit(0);t >= 0;t = spec.terminals.nextSetBit(t+1))
		{
			if(t == spec.getEOFTerminal() ||
			   symbolTable.getTerminal(t).getCode() == null ||
			   QuotedStringFormatter.isJavaWhitespace(symbolTable.getTerminal(t).getCode()))
			{
				continue;
			}
			out.print("            case " + t + ":\n");
			out.print("                RESULT = runSemanticAction_" + t + "(lexeme);\n");
			out.print("                break;\n");
		}
		out.print("            default:\n");
		out.print("        runDefaultTermAction();\n");
		out.print("                 break;\n");
		out.print("            }\n");
		out.print("            return RESULT;\n");
		out.print("        }\n");

		if(parser.getPostParseCode() != null && !QuotedStringFormatter.isJavaWhitespace(parser.getPostParseCode()))
		{
			out.print("        public void runPostParseCode(" + Object.class.getName() + " __root)\n");
			out.print("        {\n");
			out.print("            " + rootType + " root = (" + rootType + ") __root;\n");
			out.print("            " + parser.getPostParseCode() + "\n");
			out.print("        }\n");
		}

		for(int p = spec.productions.nextSetBit(0);p >= 0;p = spec.productions.nextSetBit(p+1))
		{
			if(p == spec.getStartProduction() ||
			   symbolTable.getProduction(p).getCode() == null ||
			   QuotedStringFormatter.isJavaWhitespace(symbolTable.getProduction(p).getCode()))
			{
				continue;
			}
			String returnType = symbolTable.getNonTerminal(spec.pr.getLHS(p)).getReturnType();
			if(returnType == null) returnType = Object.class.getName();
			out.print("        public " + returnType + " runSemanticAction_" + p + "()\n");
			out.print("        throws " + errorType + "\n");
			out.print("        {\n");
			if(symbolTable.getProduction(p).getRhsVarNames() != null)
			{
				int k = 0;
				for(String var : symbolTable.getProduction(p).getRhsVarNames())
				{
					if(var != null)
					{
						int sym = spec.pr.getRHSSym(p,k);
						String type = null;
						if(spec.terminals.get(sym))
						{
							type = symbolTable.getTerminal(sym).getReturnType();
						}
						else if(spec.nonterminals.get(sym))
						{
							type = symbolTable.getNonTerminal(sym).getReturnType();
						}
						out.print("            ");
						if(type == null) type = Object.class.getName();
						if(type.contains("<")) out.print("@SuppressWarnings(\"unchecked\") ");
						out.print(type + " " + var + " = (" + type + ") _children[" + k + "];\n");
					}
					k++;
				}
			}
			out.print("            " + returnType + " RESULT = null;\n");
			out.print("            " + symbolTable.getProduction(p).getCode() + "\n");
			out.print("            return RESULT;\n");
			out.print("        }\n");
		}
		for(int t = spec.terminals.nextSetBit(0);t >= 0;t = spec.terminals.nextSetBit(t+1))
		{
			if(t == spec.getEOFTerminal() ||
			   symbolTable.getTerminal(t).getCode() == null ||
			   QuotedStringFormatter.isJavaWhitespace(symbolTable.getTerminal(t).getCode()))
			{
				continue;
			}
			String returnType = symbolTable.getTerminal(t).getReturnType();
			if(returnType == null) returnType = Object.class.getName();
			
			out.print("        public " + returnType + " runSemanticAction_" + t + "(String lexeme)\n");
			out.print("        throws " + errorType + "\n");
			out.print("        {\n");
			out.print("            " + returnType + " RESULT = null;\n");
			out.print("            " + symbolTable.getTerminal(t).getCode() + "\n");
			out.print("            return RESULT;\n");
			out.print("        }\n");
		}

		out.print("        public int runDisambiguationAction(" + InputPosition.class.getName() + " _pos," + SingleDFAMatchData.class.getName() + " match)\n");
	    out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
	    out.print("        {\n");
		if(spec.disambiguationFunctions.nextSetBit(0) >= 0) out.print("            String lexeme = match.lexeme;\n");
		first = true;
	    for(int group = spec.disambiguationFunctions.nextSetBit(0);group >= 0;group = spec.disambiguationFunctions.nextSetBit(group+1))
		{
			out.print("            ");
			if(!first) out.print("else ");
			else first = false;
			out.print("if(match.terms.equals(disambiguationGroups[" + (group - spec.disambiguationFunctions.nextSetBit(0)) + "])) return disambiguate_" + (group - spec.disambiguationFunctions.nextSetBit(0)) + "(lexeme);\n");
		}
		out.print("            ");
		if(!first) out.print("else ");
		out.print("return -1;\n");
	    out.print("        }\n");
	    
	    for(int group = spec.disambiguationFunctions.nextSetBit(0);group >= 0;group = spec.disambiguationFunctions.nextSetBit(group+1))
		{
			out.print("        public int disambiguate_" + (group - spec.disambiguationFunctions.nextSetBit(0)) + "(String lexeme)\n");
			out.print("        throws " + errorType + "\n");
			out.print("        {\n");
			if(spec.df.hasDisambiguateTo(group))
			{
				out.print("            return /* " + symbolNames[spec.df.getDisambiguateTo(group)] + " */ " + spec.df.getDisambiguateTo(group) + ";\n");
			}
			else
			{
				for(int t = spec.df.getMembers(group).nextSetBit(0);t >= 0;t = spec.df.getMembers(group).nextSetBit(t+1))
				{
					out.print("            @SuppressWarnings(\"unused\") int " + symbolNames[t] + " = " + t + ";\n");
				}
				out.print("            " + symbolTable.getDisambiguationFunction(group).getCode() + "\n");
			}
			out.print("        }\n");
		}

		out.print("    }\n");
	    
	    out.print("    public Semantics semantics;\n");
		out.print("    public " + Object.class.getName() + " runSemanticAction(" + InputPosition.class.getName() + " _pos," + Object.class.getName() + "[] _children,int _prod)\n");
	    out.print("    throws " + IOException.class.getName() + "," + errorType + "\n");
	    out.print("    {\n");
	    out.print("        return semantics.runSemanticAction(_pos,_children,_prod);\n");
	    out.print("    }\n");
	    out.print("    public " + Object.class.getName() + " runSemanticAction(" + InputPosition.class.getName() + " _pos," + SingleDFAMatchData.class.getName() + " _terminal)\n");
	    out.print("    throws " + IOException.class.getName() + "," + errorType + "\n");
	    out.print("    {\n");
	    out.print("        return semantics.runSemanticAction(_pos,_terminal);\n");
	    out.print("    }\n");
		if(parser.getPostParseCode() != null && !QuotedStringFormatter.isJavaWhitespace(parser.getPostParseCode()))
		{
		    out.print("    public void runPostParseCode(" + Object.class.getName() + " __root)\n");
		    out.print("    {\n");
		    out.print("        semantics.runPostParseCode(__root);\n");
		    out.print("    }\n");
		}
		out.print("    public int runDisambiguationAction(" + InputPosition.class.getName() + " _pos," + SingleDFAMatchData.class.getName() + " matches)\n");
	    out.print("    throws " + IOException.class.getName() + "," + errorType + "\n");
	    out.print("    {\n");
	    out.print("        return semantics.runDisambiguationAction(_pos,matches);\n");
	    out.print("    }\n");
	    out.print("    public " + SpecialParserAttributes.class.getName() + " getSpecialAttributes()\n");
	    out.print("    {\n");
	    out.print("        return semantics.getSpecialAttributes();\n");
	    out.print("    }\n");
	    out.print("    public void startEngine(" + InputPosition.class.getName() + " initialPos)\n");
	    out.print("    throws " + IOException.class.getName() + "," + errorType + "\n");
	    out.print("    {\n");
	    out.print("         super.startEngine(initialPos);\n");
	    out.print("         semantics = new Semantics();\n");
	    out.print("    }\n");
		out.print("\n");

		for(int t = spec.terminals.nextSetBit(0);t >= 0;t = spec.terminals.nextSetBit(t+1))
		{
			symbolNumbers[t] = SingleDFAEngine.newSymbol(SingleDFAEngine.SYMBOL_TERMINAL,0);
		}
		// If "No EMPTY" does not appear, comment this out.
		//symbolNumbers[symbolTransTable.get(FringeSymbols.EMPTY.getId())] = SingleDFAEngine.newSymbol(SingleDFAEngine.SYMBOL_TERMINAL,0);
		for(int nt = spec.nonterminals.nextSetBit(0);nt >= 0;nt = spec.nonterminals.nextSetBit(nt+1))
		{
			symbolNumbers[nt] = SingleDFAEngine.newSymbol(SingleDFAEngine.SYMBOL_NONTERM,0);
		}
		for(int p = spec.productions.nextSetBit(0);p >= 0;p = spec.productions.nextSetBit(p+1))
		{
			symbolNumbers[p] = SingleDFAEngine.newSymbol(SingleDFAEngine.SYMBOL_PRODUCTION,spec.pr.getRHSLength(p));
			productionLHSs[p - GRAMMAR_SYMBOL_COUNT] = SingleDFAEngine.newSymbol(SingleDFAEngine.SYMBOL_NONTERM,spec.pr.getLHS(p));
		}

		ByteArrayOutputStream stringOut = new ByteArrayOutputStream();
		ObjectOutputStream outp;
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(symbolNames);
		out.println("public static final byte[] symbolNamesHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(symbolDisplayNames);
		out.println("public static final byte[] symbolDisplayNamesHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(symbolNumbers);
		out.println("public static final byte[] symbolNumbersHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(productionLHSs);
		out.println("public static final byte[] productionLHSsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(parseTable);
		out.println("public static final byte[] parseTableHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(shiftableSets);
		out.println("public static final byte[] shiftableSetsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(layoutSets);
		out.println("public static final byte[] layoutSetsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(prefixSets);
		out.println("public static final byte[] prefixSetsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(prefixMaps);
		out.println("public static final byte[] prefixMapsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(terminalUses);
		out.println("public static final byte[] terminalUsesHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(shiftableUnion);
		out.println("public static final byte[] shiftableUnionHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(scannerDFAAnnotations.acceptSets);
		out.println("public static final byte[] acceptSetsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(scannerDFAAnnotations.rejectSets);
		out.println("public static final byte[] rejectSetsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(scannerDFAAnnotations.possibleSets);
		out.println("public static final byte[] possibleSetsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(scannerDFAAnnotations.charMap);
		out.println("public static final byte[] cMapHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(delta);
		out.println("public static final byte[] deltaHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		
		out.print("public static void initArrays()\n");
		out.print("throws " + IOException.class.getName() + "," + ClassNotFoundException.class.getName() + "\n");
		out.print("{\n");
		out.print("    symbolNames = (String[]) " + ByteArrayEncoder.class.getName() + ".readHash(symbolNamesHash);\n");
		out.print("    symbolDisplayNames = (String[]) " + ByteArrayEncoder.class.getName() + ".readHash(symbolDisplayNamesHash);\n");
		out.print("    symbolNumbers = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(symbolNumbersHash);\n");
		out.print("    productionLHSs = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(productionLHSsHash);\n");
		out.print("    parseTable = (int[][]) " + ByteArrayEncoder.class.getName() + ".readHash(parseTableHash);\n");
		out.print("    shiftableSets = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(shiftableSetsHash);\n");
		out.print("    layoutSets = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(layoutSetsHash);\n");
		out.print("    prefixSets = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(prefixSetsHash);\n");
		//out.print("    layoutMaps = (" + BitSet.class.getName() + "[][]) " + ByteArrayEncoder.class.getName() + ".readHash(layoutMapsHash);\n");
		out.print("    prefixMaps = (" + BitSet.class.getName() + "[][]) " + ByteArrayEncoder.class.getName() + ".readHash(prefixMapsHash);\n");
		out.print("    terminalUses = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(terminalUsesHash);\n");
		out.print("    shiftableUnion = (" + BitSet.class.getName() + ") " + ByteArrayEncoder.class.getName() + ".readHash(shiftableUnionHash);\n");
		out.print("    acceptSets = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(acceptSetsHash);\n");
		out.print("    rejectSets = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(rejectSetsHash);\n");
		out.print("    possibleSets = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(possibleSetsHash);\n");
		out.print("    cmap = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(cMapHash);\n");
		out.print("    delta = (int[][]) " + ByteArrayEncoder.class.getName() + ".readHash(deltaHash);\n");
		out.print("    }\n");

		out.print(parserAncillaries);
		out.print("\n");
		out.print(scannerAncillaries);
		out.print("\n");
		out.print("    static\n");
		out.print("    {\n");
		out.print("        TERMINAL_COUNT = " + TERMINAL_COUNT + ";\n");
		out.print("        GRAMMAR_SYMBOL_COUNT = " + GRAMMAR_SYMBOL_COUNT + ";\n");
		out.print("        SYMBOL_COUNT = " + SYMBOL_COUNT + ";\n");
		out.print("        PARSER_STATE_COUNT = " + PARSER_STATE_COUNT + ";\n");
		out.print("        SCANNER_STATE_COUNT = " + SCANNER_STATE_COUNT + ";\n");
		out.print("        DISAMBIG_GROUP_COUNT = " + DISAMBIG_GROUP_COUNT + ";\n");
		out.print("        SCANNER_START_STATENUM = " + SCANNER_START_STATENUM + ";\n");
		out.print("        PARSER_START_STATENUM = " + PARSER_START_STATENUM + ";\n");
		out.print("        EOF_SYMNUM = " + EOF_SYMNUM + ";\n");
		out.print("        EPS_SYMNUM = " + EPS_SYMNUM + ";\n");
		out.print("        try { initArrays(); }\n");
		out.print("        catch(" + IOException.class.getName() + " ex) { ex.printStackTrace(); System.exit(1); }\n");
		out.print("        catch(" + ClassNotFoundException.class.getName() + " ex) { ex.printStackTrace(); System.exit(1); }\n");
		out.print("        disambiguationGroups = new " + BitSet.class.getName() + "[" + DISAMBIG_GROUP_COUNT + "];\n");
	    for(int group = spec.disambiguationFunctions.nextSetBit(0);group >= 0;group = spec.disambiguationFunctions.nextSetBit(group+1))
		{
			out.print("        disambiguationGroups[" + (group - spec.disambiguationFunctions.nextSetBit(0)) + "] = newBitVec(" + TERMINAL_COUNT);
			for(int t = spec.df.getMembers(group).nextSetBit(0);t >= 0;t = spec.df.getMembers(group).nextSetBit(t+1))
			{
				out.print("," + t);
			}
			out.print(");\n");
		}
		out.print("    }\n");
		out.print("\n");
		out.print("}\n");
		//if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"  Lexical ambiguity check");
		//LexicalAmbiguityChecker lexChecker = new SingleDFALexicalAmbiguityChecker(logger);
		//lexChecker.checkLexicalAmbiguities(grammar,scannerInfo,builtParseTable);
	}

	public int getScannerStateCount()
	{
		return scannerDFA.stateCount();
	}
	
	private String generateVariableName(int element)
	{
		if(symbolTable.get(element).getType() == CopperElementType.SPECIAL)
		{
			if(element == spec.getEOFTerminal()) return "EOF";
			else if(element == spec.getStartNonterminal()) return "START";
			else if(element == spec.getStartProduction()) return "STARTP";
			else return null;
		}
		else if(symbolTable.getParser(spec.parser).isUnitary())
		{
			return symbolTable.get(element).getName().toString();
		}
		else
		{
			return symbolTable.getGrammar(spec.owners[element]).getName().toString() + "$" + symbolTable.get(element).getName().toString(); 
		}
	}

}
