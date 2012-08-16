package edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.single;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.LexicalDisambiguationGroup;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.PrecedenceRelationGraph;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex;
import edu.umn.cs.melt.copper.compiletime.auxiliary.CharacterRange;
import edu.umn.cs.melt.copper.compiletime.auxiliary.ParseActionPrettyPrinter;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.engines.lalr.QScannerStateInfo;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.GeneralizedNFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.lalr1.LALR1DFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.oldnfa.NFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.oldnfa.NFA2DFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.oldnfa.NFAState;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.AcceptAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.FullReduceAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.GLRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.ParseAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.ParseActionVisitor;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.ShiftAction;
import edu.umn.cs.melt.copper.compiletime.semantics.lalr1.LexicalAmbiguityChecker;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.EngineBuilder;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.RegexInfo;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter;
import edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes;
import edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine;
import edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData;
import edu.umn.cs.melt.copper.runtime.engines.single.semantics.SingleDFASemanticActionContainer;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public class SingleDFAEngineBuilder implements EngineBuilder,ParseActionVisitor< Pair<Integer,String>,CopperException >
{
	private GrammarSource grammar;
	private GLRParseTable builtParseTable;
	private CompilerLogger logger;
	private Hashtable<Symbol,RegexInfo> regexes;
	private QScannerStateInfo[] scannerInfo;
	
	private Hashtable<Symbol,Integer> symbolTransTable;
	private Hashtable<LexicalDisambiguationGroup,Integer> lexGroupTransTable;
	
	/** Names of terminals, nonterminals, productions, etc. */
	public String[] symbolNames;
	/** Display names of terminals, nonterminals, productions, etc. */
	public String[] symbolDisplayNames;
	/** Lengths of productions, types of symbols. */
	public int[] symbolNumbers;
	/** Symbols on the left-hand sides of productions. */
	public int[] productionLHSs;
	
	/** Parse actions. */
	public int[][] parseTable;
	/** Shiftable sets. */
	public BitSet[] shiftableSets;
	/** Layout sets. */
	public BitSet[] layoutSets;
	/** Prefix sets. */
	public BitSet[] prefixSets;
	/** Maps of layout terminals. */
	public BitSet[][] layoutMaps;
	/** Maps of prefix terminals. */
	public BitSet[][] prefixMaps;
	/** What terminals are used as: only layout, only prefix, only shiftable, or versatile. */
	public int[] terminalUses;
	
	/** Shiftable union --- all terminals with a parse action,
	 * plus all layout and prefixes that can appear before them. */
	public BitSet shiftableUnion;
	
	public BitSet[] disambiguationGroups;
	
	/** State sets for scanner DFA. */
	public BitSet[] acceptSets,rejectSets,possibleSets;
	
	public int[][] delta;
	public int[] cmap;

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

	public SingleDFAEngineBuilder(GrammarSource grammar,LALR1DFA dfa,GLRParseTable builtParseTable,CompilerLogger logger)
	{
		this.grammar = grammar;
		this.builtParseTable = builtParseTable;
		this.logger = logger;
		this.regexes = new Hashtable<Symbol,RegexInfo>();
		
		for(int i = 0;i < PARSER_STATE_COUNT;i++)
		{
			shiftableSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			layoutSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			prefixSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			for(int j = 0;j < TERMINAL_COUNT;j++)
			{
				layoutMaps[i][j] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
				prefixMaps[i][j] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			}
		}
		for(int i = 0;i < SCANNER_STATE_COUNT;i++)
		{
			acceptSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			rejectSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			possibleSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
		}
		for(int i = 0;i < TERMINAL_COUNT;i++)
		{
			terminalUses[i] = SingleDFAEngine.TERMINAL_UNUSED;
		}
	}

	public void buildLALREngine(PrintStream out,
			  					String packageDecl,
			  					String importDecls,
			  					String parserName,
			  					String scannerName,
			  					String parserAncillaries,
			  					String scannerAncillaries)
	throws IOException,CopperException
	{
		symbolTransTable = new Hashtable<Symbol,Integer>();
		lexGroupTransTable = new Hashtable<LexicalDisambiguationGroup,Integer>();
		
		if(packageDecl.equals("") &&
		   importDecls.equals("")) packageDecl = grammar.getParserSources().getClassFilePreambleCode();
		else importDecls += "\n" + grammar.getParserSources().getClassFilePreambleCode();
		
		String rootType = grammar.getNTAttributes(grammar.getStartSym()).getType();
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
	    parserAncillaries += "		private static " + BitSet.class.getName() + "[][] layoutMaps;\n";
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
	    parserAncillaries += "			return layoutMaps;\n";
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
	    parserAncillaries += "    this.buffer = " + ScannerBuffer.class.getName() + ".instantiate(input);\n";
	    parserAncillaries += "    setupEngine();\n";
	    parserAncillaries += "    startEngine(" + InputPosition.class.getName() + ".initialPos(inputName));\n";
	    parserAncillaries += "    " + rootType + " parseTree = (" + rootType + ") runEngine();\n";
	    parserAncillaries += "    return parseTree;\n";
	    parserAncillaries += "    }\n";
	    parserAncillaries += "\n";
		parserAncillaries += grammar.getParserSources().getParserClassAuxCode();

		out.print(packageDecl + "\n");
		out.print(importDecls + "\n");
		// DEBUG-BEGIN
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"  Parser code");
		// DEBUG-END
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
		int i = 0;
		for(Terminal t : grammar.getT())
		{
			symbolTransTable.put(t.getId(),i++);
		}
/*		if(!symbolTransTable.containsKey(FringeSymbols.EMPTY.getId()))
		{
*/			// DEBUG-X-BEGIN
			//System.err.println("No EMPTY");
			// DEBUG-X-END
			symbolTransTable.put(FringeSymbols.EMPTY.getId(),i++);
/*		}
		if(!symbolTransTable.containsKey(FringeSymbols.EOF.getId()))
		{
			// DEBUG-X-BEGIN
			System.err.println("No EOF");
			// DEBUG-X-END
			symbolTransTable.put(FringeSymbols.EOF.getId(),i++);
		}
*/		TERMINAL_COUNT = i;
		EOF_SYMNUM = symbolTransTable.get(FringeSymbols.EOF.getId());
		EPS_SYMNUM = symbolTransTable.get(FringeSymbols.EMPTY.getId());
		for(NonTerminal nt : grammar.getNT())
		{
			symbolTransTable.put(nt.getId(),i++);
		}
		GRAMMAR_SYMBOL_COUNT = i;
		for(NonTerminal nt : grammar.getNT())
		{
			if(!grammar.pContains(nt)) continue;
			for(Production p : grammar.getP(nt))
			{
				symbolTransTable.put(p.getName(),i++);
			}
		}
		SYMBOL_COUNT = i;
		PARSER_START_STATENUM = 0;

		i = 0;
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			lexGroupTransTable.put(group,i++);
		}
		DISAMBIG_GROUP_COUNT = i;

		TreeSet<Integer> sortedStates = new TreeSet<Integer>();
		for(int statenum : builtParseTable.getStates()) sortedStates.add(statenum);
		
		PARSER_STATE_COUNT = sortedStates.last() + 1;

		symbolNames = new String[SYMBOL_COUNT];
		symbolDisplayNames = new String[SYMBOL_COUNT];
		symbolNumbers = new int[SYMBOL_COUNT];
		productionLHSs = new int[SYMBOL_COUNT - GRAMMAR_SYMBOL_COUNT];
        parseTable = new int[PARSER_STATE_COUNT][GRAMMAR_SYMBOL_COUNT];
        shiftableSets = new BitSet[PARSER_STATE_COUNT];
        layoutSets = new BitSet[PARSER_STATE_COUNT];
        prefixSets = new BitSet[PARSER_STATE_COUNT];
        terminalUses = new int[TERMINAL_COUNT];
		layoutMaps = new BitSet[PARSER_STATE_COUNT][TERMINAL_COUNT];
		prefixMaps = new BitSet[PARSER_STATE_COUNT][TERMINAL_COUNT];
		shiftableUnion = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
		disambiguationGroups = new BitSet[DISAMBIG_GROUP_COUNT];
		cmap = new int[Character.MAX_VALUE + 1];

		for(Symbol s : symbolTransTable.keySet())
		{
			symbolNames[symbolTransTable.get(s)] = s.toString();
			symbolDisplayNames[symbolTransTable.get(s)] = grammar.getDisplayName(s).toString();
		}

		for(Terminal t : grammar.getT())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			if(t.equals(FringeSymbols.EOF)) continue;
			ParsedRegex pr = grammar.getRegex(t);
			if(pr == null)
			{
				if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,"No regex provided for terminal " + t);
				return;
			}
			regexes.put(t.getId(),
				    new RegexInfo(
				     pr,
				     null));
		}

		for(int statenum : sortedStates)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			
			shiftableSets[statenum] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			
			if(builtParseTable.hasShiftable(statenum))
			{
				for(Terminal t : builtParseTable.getShiftable(statenum))
				{
					shiftableSets[statenum].set(symbolTransTable.get(t.getId()));
					terminalUses[symbolTransTable.get(t.getId())] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_SHIFTABLE;
				}
			}
			if(builtParseTable.hasShiftable(statenum))
			{
				for(Terminal t : builtParseTable.getShiftable(statenum))
				{
					Iterable<ParseAction> actions = builtParseTable.getParseActions(statenum,t);
					if(builtParseTable.countParseActions(statenum,t) > 1)
					{
						logger.logParseTableConflict(CompilerLogMessageSort.UNRESOLVED_CONFLICT,false,statenum,grammar.getDisplayName(t.getId()),new ParseActionPrettyPrinter(grammar).prettyPrintConflict(actions));
					}
					for(ParseAction action : actions)
					{
						Pair<Integer,String> inf = action.acceptVisitor(this);
						parseTable[statenum][symbolTransTable.get(t.getId())] = inf.first();
						shiftableUnion.set(symbolTransTable.get(t.getId()));
					}
				}
			}
			if(builtParseTable.hasGotoable(statenum))
			{
				for(NonTerminal nt : builtParseTable.getGotoable(statenum))
				{
					ShiftAction action = builtParseTable.getGotoAction(statenum,nt);
					Pair<Integer,String> inf = action.acceptVisitor(this);
					parseTable[statenum][symbolTransTable.get(nt.getId())] = inf.first();
				}
			}
			layoutSets[statenum] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			if(builtParseTable.hasLayout(statenum))
			{
				for(Terminal layout : builtParseTable.getLayout(statenum))
				{
					layoutSets[statenum].set(symbolTransTable.get(layout.getId()));
					shiftableSets[statenum].set(symbolTransTable.get(layout.getId()));
					terminalUses[symbolTransTable.get(layout.getId())] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_LAYOUT;
					shiftableUnion.set(symbolTransTable.get(layout.getId()));
				}
			}
			if(builtParseTable.hasLayout(statenum))
			{
				for(Terminal layout : builtParseTable.getLayout(statenum))
				{
					layoutMaps[statenum][symbolTransTable.get(layout.getId())] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
					
					for(Terminal t : builtParseTable.getShiftableFollowingLayout(statenum,layout))
					{
						layoutMaps[statenum][symbolTransTable.get(layout.getId())].set(symbolTransTable.get(t.getId()));
					}
				}
			}
			prefixSets[statenum] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			if(builtParseTable.hasPrefixes(statenum))
			{
				for(Terminal prefix : builtParseTable.getPrefixes(statenum))
				{
					prefixSets[statenum].set(symbolTransTable.get(prefix.getId()));
					shiftableSets[statenum].set(symbolTransTable.get(prefix.getId()));
					terminalUses[symbolTransTable.get(prefix.getId())] &= SingleDFAEngine.TERMINAL_EXCLUSIVELY_PREFIX;
					shiftableUnion.set(symbolTransTable.get(prefix.getId()));
				}
			}
			if(builtParseTable.hasPrefixes(statenum))
			{
				for(Terminal prefix : builtParseTable.getPrefixes(statenum))
				{
					if(prefix.equals(FringeSymbols.EMPTY)) continue;
					prefixMaps[statenum][symbolTransTable.get(prefix.getId())] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
					for(Terminal t : builtParseTable.getShiftableFollowingPrefix(statenum,prefix))
					{
						prefixMaps[statenum][symbolTransTable.get(prefix.getId())].set(symbolTransTable.get(t.getId()));
					}
				}
			}
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n  Scanner code...");
		
		// To switch between scanner generation engines, change this line.
//		generateScanner(out);
		generateScannerNew(out);

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n  Semantic action framework...\n");
		
		out.print("    public class Semantics extends " + SingleDFASemanticActionContainer.class.getName() + "<" + errorType + ">\n");
		out.print("    {\n");
		
		out.print(grammar.getParserSources().getSemanticActionAuxCode());
		
		for(ParserAttribute attr : grammar.getParserAttributes())
		{
			out.print("        public " + attr.getType() + " " + attr.getName().toString() + ";\n");
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
		out.print("            " + grammar.getDefaultTCode() + "\n");
		out.print("        }\n");
	    out.print("        public void runDefaultProdAction()\n");
		out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
		out.print("        {\n");
		out.print("            " + grammar.getDefaultProdCode() + "\n");
		out.print("        }\n");
		out.print("        public void runInit()\n");
		out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
		out.print("        {\n");
		out.print("            " + grammar.getParserSources().getParserAttrInitCode());
		for(ParserAttribute attr : grammar.getParserAttributes())
		{
			out.print("            " + attr.getInitCode() + "\n");
		}
		out.print("        }\n");

		out.print("        public Object runSemanticAction(" + InputPosition.class.getName() + " _pos,Object[] _children,int _prod)\n");
	    out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
		out.print("        {\n");
		out.print("            this._pos = _pos;\n");
		out.print("            this._children = _children;\n");
		out.print("            this._prod = _prod;\n");
		out.print("            this._specialAttributes = new " + SpecialParserAttributes.class.getName() + "(virtualLocation);\n");
		out.print("            Object RESULT = null;\n");
		out.print("            switch(_prod)\n");
		out.print("            {\n");
		i = 0;
		for(NonTerminal nt : grammar.getNT())
		{
			if(!grammar.pContains(nt)) continue;
			for(Production p : grammar.getP(nt))
			{
				if(QuotedStringFormatter.isJavaWhitespace(grammar.getProductionAttributes(p).getActionCode()))
				{
					continue;
				}
				out.print("            case " + symbolTransTable.get(p.getName()) + ":\n");
				out.print("                RESULT = runSemanticAction_" + symbolTransTable.get(p.getName()) + "();\n");
				out.print("                break;\n");
			}
		}
		out.print("            default:\n");
		out.print("        runDefaultProdAction();\n");
		out.print("                 break;\n");
		out.print("            }\n");
		out.print("            return RESULT;\n");
		out.print("        }\n");

		out.print("        public Object runSemanticAction(" + InputPosition.class.getName() + " _pos," + SingleDFAMatchData.class.getName() + " _terminal)\n");
	    out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
		out.print("        {\n");
		out.print("            this._pos = _pos;\n");
		out.print("            this._terminal = _terminal;\n");
		out.print("            this._specialAttributes = new " + SpecialParserAttributes.class.getName() + "(virtualLocation);\n");
		out.print("            @SuppressWarnings(\"unused\") String lexeme = _terminal.lexeme;\n");
		out.print("            Object RESULT = null;\n");
		out.print("            switch(_terminal.firstTerm)\n");
		out.print("            {\n");
		for(Terminal t : grammar.getT())
		{
			if(QuotedStringFormatter.isJavaWhitespace(grammar.getLexicalAttributes(t).getParserSemanticActionCode()))
			{
				continue;
			}
			out.print("            case " + symbolTransTable.get(t.getId()) + ":\n");
			out.print("                RESULT = runSemanticAction_" + symbolTransTable.get(t.getId()) + "(lexeme);\n");
			out.print("                break;\n");
		}
		out.print("            default:\n");
		out.print("        runDefaultTermAction();\n");
		out.print("                 break;\n");
		out.print("            }\n");
		out.print("            return RESULT;\n");
		out.print("        }\n");

		if(!QuotedStringFormatter.isJavaWhitespace(grammar.getParserSources().getPostParseCode()))
		{
			out.print("        public void runPostParseCode(Object __root)\n");
			out.print("        {\n");
			out.print("            " + grammar.getNTAttributes(grammar.getStartSym()).getType() + " root = (" + grammar.getNTAttributes(grammar.getStartSym()).getType() + ") __root;\n");
			out.print("            " + grammar.getParserSources().getPostParseCode() + "\n");
			out.print("        }\n");
		}

		i = 0;
		for(NonTerminal nt : grammar.getNT())
		{
			if(!grammar.pContains(nt)) continue;
			for(Production p : grammar.getP(nt))
			{
				if(QuotedStringFormatter.isJavaWhitespace(grammar.getProductionAttributes(p).getActionCode()))
				{
					continue;
				}
				out.print("        public " + grammar.getNTAttributes(p.getLeft()).getType() + " runSemanticAction_" + symbolTransTable.get(p.getName()) + "()\n");
				out.print("        throws " + errorType + "\n");
				out.print("        {\n");
				if(grammar.getProductionAttributes(p).getVars() != null)
				{
					int k = 0;
					for(String var : grammar.getProductionAttributes(p).getVars())
					{
						if(var != null)
						{
							GrammarSymbol sym = p.getSymbol(k);
							String type = "Object";
							if(sym instanceof Terminal)
							{
								type = grammar.getLexicalAttributes((Terminal) sym).getType();
							}
							else if(sym instanceof NonTerminal)
							{
								type = grammar.getNTAttributes((NonTerminal) sym).getType();
							}
							out.print("            " + type + " " + var + " = (" + type + ") _children[" + k + "];\n");
						}
						k++;
					}
				}
				out.print("            " + grammar.getNTAttributes(p.getLeft()).getType() + " RESULT = null;\n");
				out.print("            " + grammar.getProductionAttributes(p).getActionCode() + "\n");
				out.print("            return RESULT;\n");
				out.print("        }\n");
			}
		}
		for(Terminal t : grammar.getT())
		{
			if(QuotedStringFormatter.isJavaWhitespace(grammar.getLexicalAttributes(t).getParserSemanticActionCode()))
			{
				continue;
			}
			out.print("        public " + grammar.getLexicalAttributes(t).getType() + " runSemanticAction_" + symbolTransTable.get(t.getId()) + "(String lexeme)\n");
			out.print("        throws " + errorType + "\n");
			out.print("        {\n");
			out.print("            " + grammar.getLexicalAttributes(t).getType() + " RESULT = null;\n");
			out.print("            " + grammar.getLexicalAttributes(t).getParserSemanticActionCode() + "\n");
			out.print("            return RESULT;\n");
			out.print("        }\n");
		}

		out.print("        public int runDisambiguationAction(" + InputPosition.class.getName() + " _pos," + SingleDFAMatchData.class.getName() + " match)\n");
	    out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
	    out.print("        {\n");
		out.print("            @SuppressWarnings(\"unused\") String lexeme = match.lexeme;\n");
		boolean first = true;
		for(LexicalDisambiguationGroup group : lexGroupTransTable.keySet())
		{
			out.print("            ");
			if(!first) out.print("else ");
			else first = false;
			out.print("if(match.terms.equals(disambiguationGroups[" + lexGroupTransTable.get(group) + "])) return disambiguate_" + lexGroupTransTable.get(group) + "(lexeme);\n");
		}
		out.print("            ");
		if(!first) out.print("else ");
		out.print("return -1;\n");
	    out.print("        }\n");
	    
		for(LexicalDisambiguationGroup group : lexGroupTransTable.keySet())
		{
			out.print("        public int disambiguate_" + lexGroupTransTable.get(group) + "(String lexeme)\n");
			out.print("        throws " + errorType + "\n");
			out.print("        {\n");
			for(Terminal t : group.getMembers())
			{
				out.print("            @SuppressWarnings(\"unused\") int " + t.getId() + " = " + symbolTransTable.get(t.getId()) + ";\n");
			}
			out.print("            " + group.getDisambigCode() + "\n");
			out.print("        }\n");
		}

		out.print("    }\n");
	    
	    out.print("    public Semantics semantics;\n");
		out.print("    public Object runSemanticAction(" + InputPosition.class.getName() + " _pos,Object[] _children,int _prod)\n");
	    out.print("    throws " + IOException.class.getName() + "," + errorType + "\n");
	    out.print("    {\n");
	    out.print("        return semantics.runSemanticAction(_pos,_children,_prod);\n");
	    out.print("    }\n");
	    out.print("    public Object runSemanticAction(" + InputPosition.class.getName() + " _pos," + SingleDFAMatchData.class.getName() + " _terminal)\n");
	    out.print("    throws " + IOException.class.getName() + "," + errorType + "\n");
	    out.print("    {\n");
	    out.print("        return semantics.runSemanticAction(_pos,_terminal);\n");
	    out.print("    }\n");
		if(!QuotedStringFormatter.isJavaWhitespace(grammar.getParserSources().getPostParseCode()))
		{
		    out.print("    public void runPostParseCode(Object __root)\n");
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

		for(Terminal t : grammar.getT())
		{
			symbolNumbers[symbolTransTable.get(t.getId())] = SingleDFAEngine.newSymbol(SingleDFAEngine.SYMBOL_TERMINAL,0);
		}
		// If "No EMPTY" does not appear, comment this out.
		symbolNumbers[symbolTransTable.get(FringeSymbols.EMPTY.getId())] = SingleDFAEngine.newSymbol(SingleDFAEngine.SYMBOL_TERMINAL,0);
		for(NonTerminal nt : grammar.getNT())
		{
			symbolNumbers[symbolTransTable.get(nt.getId())] = SingleDFAEngine.newSymbol(SingleDFAEngine.SYMBOL_NONTERM,0);
			if(grammar.pContains(nt))
			{
				for(Production p : grammar.getP(nt))
				{
					symbolNumbers[symbolTransTable.get(p.getName())] = SingleDFAEngine.newSymbol(SingleDFAEngine.SYMBOL_PRODUCTION,p.length());
					productionLHSs[symbolTransTable.get(p.getName()) - GRAMMAR_SYMBOL_COUNT] = SingleDFAEngine.newSymbol(SingleDFAEngine.SYMBOL_NONTERM,symbolTransTable.get(p.getLeft().getId()));
				}
			}
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
		outp.writeObject(layoutMaps);
		out.println("public static final byte[] layoutMapsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
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
		outp.writeObject(acceptSets);
		out.println("public static final byte[] acceptSetsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(rejectSets);
		out.println("public static final byte[] rejectSetsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(possibleSets);
		out.println("public static final byte[] possibleSetsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(cmap);
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
		out.print("    layoutMaps = (" + BitSet.class.getName() + "[][]) " + ByteArrayEncoder.class.getName() + ".readHash(layoutMapsHash);\n");
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
		out.print("        catch(" + IOException.class.getName() + " ex) { System.err.println(\"IO Exception\"); }\n");
		out.print("        catch(" + ClassNotFoundException.class.getName() + " ex) { System.err.println(\"Class Not Found Exception\"); }\n");
		out.print("        disambiguationGroups = new " + BitSet.class.getName() + "[" + DISAMBIG_GROUP_COUNT + "];\n");
		for(LexicalDisambiguationGroup group : lexGroupTransTable.keySet())
		{
			out.print("        disambiguationGroups[" + lexGroupTransTable.get(group) + "] = newBitVec(" + TERMINAL_COUNT);
			for(Terminal t : group.getMembers())
			{
				out.print("," + symbolTransTable.get(t.getId()));
			}
			out.print(");\n");
		}
		out.print("    }\n");
		out.print("\n");
		out.print("}\n");
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"  Lexical ambiguity check");
		LexicalAmbiguityChecker lexChecker = new SingleDFALexicalAmbiguityChecker(logger);
		lexChecker.checkLexicalAmbiguities(grammar,scannerInfo,builtParseTable);
	}
	
	public void generateScannerNew(PrintStream out)
	throws CopperException
	{
		int i;
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    NFA generation/DFA conversion");
		// Collect the sub-NFAs for all regexes.
		HashSet<SetOfCharsSyntax> allCharRanges = new HashSet<SetOfCharsSyntax>();
		for(Symbol forRegex : regexes.keySet()) allCharRanges.addAll(regexes.get(forRegex).getRegex().getTransitionLabels());
		GeneralizedNFA nfa = new GeneralizedNFA(regexes.size(),allCharRanges.size());
		BitSet allStartStates = new BitSet();
		// Add accept symbols and gather start states for individual regexes.
		for(Symbol forRegex : regexes.keySet())
		{
			Pair<Integer,BitSet> states = regexes.get(forRegex).getRegex().generateAutomaton(nfa);
			allStartStates.set(states.first());
			for(int j = states.second().nextSetBit(0);j >= 0;j = states.second().nextSetBit(j+1))
			{
				nfa.addAcceptSymbol(j,symbolTransTable.get(forRegex));
			}
		}
		// Create a scanner-wide start state and add epsilon transitions
		// into the start states for all the individual regexes.
		int newStartState = nfa.addState();
		nfa.addEpsilonTransitions(newStartState,allStartStates);

		// DEBUG-X-BEGIN
		//System.err.println("\n====== NFA ======\n");
		//System.err.println("Start state: " + newStartState);
		//System.err.println(nfa.toString());
		// DEBUG-X-END		
		
		// Convert the NFA to a DFA.
		GeneralizedDFA dfa = nfa.determinize(newStartState);
		
		// DEBUG-X-BEGIN
		//System.err.println("\n====== DFA ======\n");
		//System.err.println(dfa.toString());
		// DEBUG-X-END

		
		// Set up holders for expanded state information.
		SCANNER_STATE_COUNT = dfa.stateCount();
		acceptSets = new BitSet[SCANNER_STATE_COUNT];
		rejectSets = new BitSet[SCANNER_STATE_COUNT];
		possibleSets = new BitSet[SCANNER_STATE_COUNT];

		scannerInfo = new QScannerStateInfo[SCANNER_STATE_COUNT];
		for(i = 0;i < scannerInfo.length;i++) scannerInfo[i] = new QScannerStateInfo();
		SCANNER_START_STATENUM = dfa.getStartState();
		// For each state in the DFA:
		for(int state = 0;state < SCANNER_STATE_COUNT;state++)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			// For each regex for which it accepts, add the information
			// to the stateInfo array.
			BitSet acceptSet = dfa.getAcceptSymbols(state);
			for(int j = acceptSet.nextSetBit(0);j >= 0;j = acceptSet.nextSetBit(j+1))
			{
				scannerInfo[state].addAcceptingSyms(new Terminal(symbolNames[j]));
			}
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Static lexical disambiguation");

		// For each state in the DFA:
		if(grammar != null)
		{
			//Hashtable<Symbol,Integer> maxStaticPrecedences;
			for(int state = 0;state < dfa.stateCount();state++)
			{
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
				HashSet<Terminal> accF = scannerInfo[state].getAcceptingSyms();
				PrecedenceRelationGraph accFG = grammar.getPrecedenceRelationsGraph().makeCut(accF);
				HashSet<Terminal> rej = accFG.partitionAcceptSet(logger,"static precedence disambiguator, scanner state " + state);
				for(Terminal t : rej)
				{
					scannerInfo[state].removeAcceptingSyms(t);
					scannerInfo[state].addRejectingSyms(t);
				}
			}
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Transitive closure");
		
		boolean[][] transClosure = new boolean[SCANNER_STATE_COUNT][SCANNER_STATE_COUNT];
		// Compute the transitive closure of the DFA's states
		// using the Floyd-Warshall algorithm, as presented in
		// Cormen, Leiserson, Rivest and Stein's
		// "Introduction to Algorithms," Second Edition,
		// section 25.2.
		for(i = 0;i < SCANNER_STATE_COUNT;i++) transClosure[i][i] = true;
		for(int state = 0;state < dfa.stateCount();state++)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			BitSet connections = dfa.getConnectedStates(state);
			for(int j = connections.nextSetBit(0);j >= 0;j = connections.nextSetBit(j+1))
			{
				transClosure[state][j] = true;
			}
		}
		for(int k = 0;k < SCANNER_STATE_COUNT;k++)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			for(i = 0;i < SCANNER_STATE_COUNT;i++)
			{
				for(int j = 0;j < SCANNER_STATE_COUNT;j++)
				{
					transClosure[i][j] = transClosure[i][j] || (transClosure[i][k] && transClosure[k][j]);
				}
			}
		}
		// Use the transitive closure to compute possible sets:
		for(i = 0;i < SCANNER_STATE_COUNT;i++)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			for(int j = 0;j < SCANNER_STATE_COUNT;j++)
			{
				// If states i and j are connected by transition:
				if(transClosure[i][j])
				{
					// Union the current possible set of i with
					// the accepting set of j.
					for(Terminal t : scannerInfo[j].getAcceptingSyms())
					{
						scannerInfo[i].addPossibleSyms(t);
					}
					for(Terminal t : scannerInfo[j].getRejectingSyms())
					{
						scannerInfo[i].addPossibleSyms(t);
					}
				}
			}
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Accept/possible info");

		for(i = 0;i < scannerInfo.length;i++)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			acceptSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			for(Terminal sym : scannerInfo[i].getAcceptingSyms())
			{
				acceptSets[i].set(symbolTransTable.get(sym.getId()));
			}
			possibleSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			for(Terminal sym : scannerInfo[i].getPossibleSyms())
			{
				possibleSets[i].set(symbolTransTable.get(sym.getId()));
			}
			rejectSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			for(Terminal sym : scannerInfo[i].getRejectingSyms())
			{
				rejectSets[i].set(symbolTransTable.get(sym.getId()));
			}
		}
		
		out.print("    public void setupEngine()\n");
		out.print("    {\n");
		out.print("    }\n");

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Transition table");

		out.print("    public int transition(int state,char ch)\n");
		out.print("    {\n");
		out.print("         return delta[state][cmap[ch]];\n");
		out.print("    }\n");
		
		for(int cr = 0;cr < dfa.charRangeCount();cr++)
		{
			char[][] canonicalRanges = dfa.getCharRange(cr).getMembers();
			for(int j = 0;j < canonicalRanges.length;j++)
			{
				for(int c = canonicalRanges[j][0];c <= canonicalRanges[j][1];c++)
				{
					cmap[c] = cr;
				}
			}
		}
		
		delta = dfa.getTransitions();				
	}
	
	public void generateScanner(PrintStream out)
	throws CopperException
	{
		int i;
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    NFA generation/DFA conversion");
		// Collect the sub-NFAs for all regexes.
		HashSet<NFA> nfas = new HashSet<NFA>();
		for(Symbol forRegex : regexes.keySet())
		{
			NFA newNFA = regexes.get(forRegex).getRegex().generateAutomaton(forRegex);
			if(logger.isLoggable(CompilerLogMessageSort.DEBUG))
			{
				logger.logMessage(CompilerLogMessageSort.DEBUG,null,"NFA for " + forRegex + ":\n" + newNFA.toString());
			}
			nfas.add(newNFA);
		}
		HashSet<NFAState> allStates = new HashSet<NFAState>();
		NFAState newStartState = new NFAState(Symbol.symbol("START"),null);
		allStates.add(newStartState);
		for(NFA curNFA : nfas)
		{
			newStartState.addTransition(new Character(NFAState.EmptyChar),curNFA.getStartState());
			allStates.addAll(curNFA.getStates());
		}
		NFA mergedNFA = new NFA(allStates,newStartState);
		// Convert the NFA to a DFA.
		NFA mergedDFA = new NFA2DFA().determinizeNFA(mergedNFA);
		// Set up holders for expanded state information.
		Hashtable<NFAState,Integer> numericalMapping = new Hashtable<NFAState,Integer>();

		SCANNER_STATE_COUNT = mergedDFA.getStates().size() + 1;
		acceptSets = new BitSet[SCANNER_STATE_COUNT];
		rejectSets = new BitSet[SCANNER_STATE_COUNT];
		possibleSets = new BitSet[SCANNER_STATE_COUNT];

		scannerInfo = new QScannerStateInfo[SCANNER_STATE_COUNT];
		for(i = 0;i < scannerInfo.length;i++) scannerInfo[i] = new QScannerStateInfo();
		int nextStateNum = 1;
		// For each state in the DFA:
		for(NFAState state : mergedDFA.getStates())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			// Assign it a number.
			numericalMapping.put(state,nextStateNum);
			// If it is the start state, record its number.
			if(state.equals(mergedDFA.getStartState())) SCANNER_START_STATENUM = nextStateNum;
			// For each regex for which it accepts, add the information
			// to the stateInfo array.
			for(Symbol sym : state.getAccepts())
			{
				scannerInfo[nextStateNum].addAcceptingSyms(new Terminal(sym));
			}
			// Compress its transitions into ranges.
			state.compressTransitions();
			nextStateNum++;
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Static lexical disambiguation");

		// For each state in the DFA:
		if(grammar != null)
		{
			for(NFAState state : mergedDFA.getStates())
			{
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
				HashSet<Terminal> accF = scannerInfo[numericalMapping.get(state)].getAcceptingSyms();
				PrecedenceRelationGraph accFG = grammar.getPrecedenceRelationsGraph().makeCut(accF);
				HashSet<Terminal> rej = accFG.partitionAcceptSet(logger,"static precedence disambiguator, scanner state " + numericalMapping.get(state));
				for(Terminal t : rej)
				{
					scannerInfo[numericalMapping.get(state)].removeAcceptingSyms(t);
					scannerInfo[numericalMapping.get(state)].addRejectingSyms(t);
				}
			}
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Transitive closure");
		
		boolean[][] transClosure = new boolean[SCANNER_STATE_COUNT][SCANNER_STATE_COUNT];
		// Compute the transitive closure of the DFA's states
		// using the Floyd-Warshall algorithm, as presented in
		// Cormen, Leiserson, Rivest and Stein's
		// "Introduction to Algorithms," Second Edition,
		// section 25.2.
		for(i = 0;i < SCANNER_STATE_COUNT;i++) transClosure[i][i] = true;
		for(NFAState st : mergedDFA.getStates())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			for(Pair<CharacterRange,NFAState> jp : st)
			{
					transClosure
					[numericalMapping.get(st)]
					 [numericalMapping.get(
							 jp.second()
							 )] = true;
			}
		}
		for(int k = 0;k < SCANNER_STATE_COUNT;k++)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			for(i = 0;i < SCANNER_STATE_COUNT;i++)
			{
				for(int j = 0;j < SCANNER_STATE_COUNT;j++)
				{
					transClosure[i][j] = transClosure[i][j] || (transClosure[i][k] && transClosure[k][j]);
				}
			}
		}
		// Use the transitive closure to compute possible sets:
		for(i = 0;i < SCANNER_STATE_COUNT;i++)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			for(int j = 0;j < SCANNER_STATE_COUNT;j++)
			{
				// If states i and j are connected by transition:
				if(transClosure[i][j])
				{
					// Union the current possible set of i with
					// the accepting set of j.
					for(Terminal t : scannerInfo[j].getAcceptingSyms())
					{
						scannerInfo[i].addPossibleSyms(t);
					}
					for(Terminal t : scannerInfo[j].getRejectingSyms())
					{
						scannerInfo[i].addPossibleSyms(t);
					}
				}
			}
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Accept/possible info");

		for(i = 0;i < scannerInfo.length;i++)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			acceptSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			for(Terminal sym : scannerInfo[i].getAcceptingSyms())
			{
				acceptSets[i].set(symbolTransTable.get(sym.getId()));
			}
			possibleSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			for(Terminal sym : scannerInfo[i].getPossibleSyms())
			{
				possibleSets[i].set(symbolTransTable.get(sym.getId()));
			}
			rejectSets[i] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			for(Terminal sym : scannerInfo[i].getRejectingSyms())
			{
				rejectSets[i].set(symbolTransTable.get(sym.getId()));
			}
		}
		
		out.print("    public void setupEngine()\n");
		out.print("    {\n");
		out.print("    }\n");

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Transition table");

		out.print("    public int transition(int state,char ch)\n");
		out.print("    {\n");
		out.print("         return delta[state][cmap[ch]];\n");
		out.print("    }\n");
		
		i = 0;
		for(NFAState state : mergedDFA.getStates())
		{
			for(CharacterRange cr : state.getTransitionSymbols())
			{
				for(char c = cr.firstChar();c <= cr.lastChar();c++)
				{
					if(cmap[c] == 0) cmap[c] = ++i;
				}
			}
		}
		delta = new int[SCANNER_STATE_COUNT][i+1];
		for(NFAState state : mergedDFA.getStates())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 2,".");
			for(Pair<CharacterRange,NFAState> transition : state)
			{
				for(char c = transition.first().firstChar();c <= transition.first().lastChar();c++)
				{
					delta[numericalMapping.get(state)][cmap[c]] = numericalMapping.get(transition.second());
				}
			}
		}		
	}
	
	public Pair<Integer,String> visitAcceptAction(AcceptAction action)
	{
		return Pair.cons(SingleDFAEngine.newAction(SingleDFAEngine.STATE_ACCEPT,0),"accept");
	}

	public Pair<Integer,String> visitFullReduceAction(FullReduceAction action)
	{
		return Pair.cons(SingleDFAEngine.newAction(SingleDFAEngine.STATE_REDUCE,symbolTransTable.get(action.getProd().getName())),"reduce(" + action.getProd() + ")");
	}

	public Pair<Integer,String> visitShiftAction(ShiftAction action)
	{
		return Pair.cons(SingleDFAEngine.newAction(SingleDFAEngine.STATE_SHIFT,action.getDestState()),"shift(" + action.getDestState() + ")");
	}
	
	public int getScannerStateCount()
	{
		return SCANNER_STATE_COUNT;
	}
}
