package edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.split;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
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
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.TerminalClass;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex;
import edu.umn.cs.melt.copper.compiletime.auxiliary.CharacterRange;
import edu.umn.cs.melt.copper.compiletime.engines.lalr.QScannerStateInfo;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.lalr1.LALR1DFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.oldnfa.NFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.oldnfa.NFA2DFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.oldnfa.NFAState;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.parsetable.AcceptAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.FullReduceAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.GLRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.ParseAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.ParseActionVisitor;
import edu.umn.cs.melt.copper.compiletime.parsetable.ShiftAction;
import edu.umn.cs.melt.copper.compiletime.semantics.lalr1.GenericLexicalAmbiguityChecker;
import edu.umn.cs.melt.copper.compiletime.semantics.lalr1.LexicalAmbiguityChecker;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.EngineBuilder;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.RegexInfo;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter;
import edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes;
import edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData;
import edu.umn.cs.melt.copper.runtime.engines.single.semantics.SingleDFASemanticActionContainer;
import edu.umn.cs.melt.copper.runtime.engines.split.SplitEngine;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public class SplitEngineBuilder implements EngineBuilder,ParseActionVisitor< Pair<Integer,String>,CopperException >
{
	public static final int ZERO = 0, ONE = 1, TWO = 2;
	
	private GrammarSource grammar;
	//private LALR1DFA dfa;
	private GLRParseTable builtParseTable;
	private CompilerLogger logger;
	private Hashtable<Symbol,RegexInfo> regexes;
	private QScannerStateInfo[][] scannerInfo;
	
	private Hashtable<Symbol,Integer> symbolTransTable;
	private Hashtable<LexicalDisambiguationGroup,Integer> lexGroupTransTable;
	
	/** Names of terminals, nonterminals, productions, etc. */
	public String[] symbolNames;
	/** Lengths of productions, types of symbols. */
	public int[] symbolNumbers;
	/** Symbols on the left-hand sides of productions. */
	public int[] productionLHSs;
	
	/** Parse actions. */
	public int[][] parseTableHost,parseTableExts,parseTableMarking;
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
	
	/** Shiftable union --- all terminals with a parse action
	 * (usually all non-prefix, non-layout terminals). */
	public BitSet shiftableUnion;
	
	public BitSet[] disambiguationGroups;
	
	public BitSet[] neededScanners, neededLayoutScanners, neededPrefixScanners;
	
	/** State sets for scanner DFA. */
	public BitSet[][] acceptSets,rejectSets,possibleSets;
	
	public int[][][] delta;
	public int[][] cmap;

	/* Counts for building arrays statically. */
	private int TERMINAL_COUNT;
	private int GRAMMAR_SYMBOL_COUNT;
	private int SYMBOL_COUNT;
	private int PARSER_STATE_COUNT;
	private int[] scannerStateCounts;
	//private int SCANNER_STATE_COUNT;
	private int DISAMBIG_GROUP_COUNT;
	
	private int[] scannerStartStates;
	//private int SCANNER_START_STATENUM;
	private int PARSER_START_STATENUM;
	private int EOF_SYMNUM;
	private int EPS_SYMNUM;

	public void setParseTableCell(int state,int termIndex,ParseAction action)
	throws CopperException
	{
		/*if(grammar.tClassContains(new TerminalClass("marking"),new Terminal(symbolNames[termIndex])))
		{
			if(parseTableMarking.length < (state + 1) ||
			   parseTableMarking[0].length < (termIndex + 1))
			{
				int[][] newParseTableMarking = new int[PARSER_STATE_COUNT][grammar.getTClassMembers(new TerminalClass("marking")).size()];
				for(int i = 0;i < parseTableMarking.length;i++) System.arraycopy(parseTableMarking[i],0,newParseTableMarking[i],0,parseTableMarking[i].length);
				parseTableMarking = newParseTableMarking;
			}
			Pair<Integer,String> inf = action.acceptVisitor(this);			
			parseTableMarking[state][termIndex] = inf.first();
		}
		else*/ if(state % 3 == 0)
		{
			if(parseTableExts.length < ((state / 3) + 1) ||
					   parseTableExts[0].length < (termIndex + 1))
			{
						int[][] newParseTableExts = new int[PARSER_STATE_COUNT][GRAMMAR_SYMBOL_COUNT];
						for(int i = 0;i < parseTableExts.length;i++) System.arraycopy(parseTableExts[i],0,newParseTableExts[i],0,parseTableExts[i].length);
						parseTableExts = newParseTableExts;
			}
			Pair<Integer,String> inf = action.acceptVisitor(this);			
			parseTableExts[state / 3][termIndex] = inf.first();
		}
		else
		{
			Pair<Integer,String> inf = action.acceptVisitor(this);
			parseTableHost[state][termIndex] = inf.first();
		}
	}
	
	public SplitEngineBuilder(GrammarSource grammar,LALR1DFA dfa,GLRParseTable builtParseTable,CompilerLogger logger)
	{
		this.grammar = grammar;
		//this.dfa = dfa;
		this.builtParseTable = builtParseTable;
		this.logger = logger;
		this.regexes = new Hashtable<Symbol,RegexInfo>();
		
		for(int i = 0;i < PARSER_STATE_COUNT;i++)
		{
			shiftableSets[i] = SplitEngine.newBitVec(TERMINAL_COUNT);
			layoutSets[i] = SplitEngine.newBitVec(TERMINAL_COUNT);
			prefixSets[i] = SplitEngine.newBitVec(TERMINAL_COUNT);
			for(int j = 0;j < TERMINAL_COUNT;j++)
			{
				layoutMaps[i][j] = SplitEngine.newBitVec(TERMINAL_COUNT);
				prefixMaps[i][j] = SplitEngine.newBitVec(TERMINAL_COUNT);
			}
		}
		/*for(int i = 0;i < scannerStateCounts[ZERO];i++)
		{
			acceptSets[ZERO][i] = SplitEngine.newBitVec(TERMINAL_COUNT);
			rejectSets[ZERO][i] = SplitEngine.newBitVec(TERMINAL_COUNT);
			possibleSets[ZERO][i] = SplitEngine.newBitVec(TERMINAL_COUNT);
		}*/
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

		parserAncillaries += "    public " + rootType + " parse(" + Reader.class.getName() + " input,String inputName)\n";
	    parserAncillaries += "    throws " + IOException.class.getName() + "," + errorType + "\n";
	    parserAncillaries += "    {\n";
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
		
		out.print("public class " + parserName + " extends " + SplitEngine.class.getName() + "<" + rootType + "," + errorType + ">\n");
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
			// DEBUG-BEGIN
			System.err.println("No EOF");
			// DEBUG-END
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
		//out.print(";\n\n");
		DISAMBIG_GROUP_COUNT = i;

		TreeSet<Integer> sortedStates = new TreeSet<Integer>();
		for(int statenum : builtParseTable.getStates()) sortedStates.add(statenum);
		
		PARSER_STATE_COUNT = sortedStates.last() + 1;

		symbolNames = new String[SYMBOL_COUNT];
		symbolNumbers = new int[SYMBOL_COUNT];
		productionLHSs = new int[SYMBOL_COUNT - GRAMMAR_SYMBOL_COUNT];
        parseTableHost = new int[PARSER_STATE_COUNT][GRAMMAR_SYMBOL_COUNT];
        parseTableExts = new int[ONE][ONE];
        parseTableMarking = new int[ONE][ONE];
        shiftableSets = new BitSet[PARSER_STATE_COUNT];
        layoutSets = new BitSet[PARSER_STATE_COUNT];
        prefixSets = new BitSet[PARSER_STATE_COUNT];
		layoutMaps = new BitSet[PARSER_STATE_COUNT][TERMINAL_COUNT];
		prefixMaps = new BitSet[PARSER_STATE_COUNT][TERMINAL_COUNT];
		shiftableUnion = SplitEngine.newBitVec(TERMINAL_COUNT);
		disambiguationGroups = new BitSet[DISAMBIG_GROUP_COUNT];
		cmap = new int[ONE][Character.MAX_VALUE];
		delta = new int[ONE][ONE][ONE];
		neededScanners = new BitSet[PARSER_STATE_COUNT];
		neededLayoutScanners = new BitSet[PARSER_STATE_COUNT];
		neededPrefixScanners = new BitSet[PARSER_STATE_COUNT];
		scannerStartStates = new int[ONE];
		scannerStateCounts = new int[ONE];
		scannerInfo = new QScannerStateInfo[ONE][ONE];

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
			
			shiftableSets[statenum] = SplitEngine.newBitVec(TERMINAL_COUNT);
			
			if(builtParseTable.hasShiftable(statenum))
			{
				for(Terminal t : builtParseTable.getShiftable(statenum))
				{
					shiftableSets[statenum].set(symbolTransTable.get(t.getId()));
				}
			}
			if(builtParseTable.hasShiftable(statenum))
			{
				for(Terminal t : builtParseTable.getShiftable(statenum))
				{
					Iterable<ParseAction> actions = builtParseTable.getParseActions(statenum,t);
					if(builtParseTable.countParseActions(statenum,t) > 1)
					{
						logger.logParseTableConflict(CompilerLogMessageSort.UNRESOLVED_CONFLICT,false,statenum,t.toString(),actions.toString());
						//if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logMessage(CompilerLogMessageSort.ERROR,null,"Parse table conflict in cell (" + statenum + "," + t + ") between/among actions " + actions);
					}
					for(ParseAction action : actions)
					{
						setParseTableCell(statenum,symbolTransTable.get(t.getId()),action);
						shiftableUnion.set(symbolTransTable.get(t.getId()));
					}
				}
			}
			if(builtParseTable.hasGotoable(statenum))
			{
				for(NonTerminal nt : builtParseTable.getGotoable(statenum))
				{
					ShiftAction action = builtParseTable.getGotoAction(statenum,nt);
					/*Pair<Integer,String> inf =*/ action.acceptVisitor(this);
					setParseTableCell(statenum,symbolTransTable.get(nt.getId()),action);
				}
			}
				layoutSets[statenum] = SplitEngine.newBitVec(TERMINAL_COUNT);
				if(builtParseTable.hasLayout(statenum))
				{
					for(Terminal layout : builtParseTable.getLayout(statenum))
					{
						layoutSets[statenum].set(symbolTransTable.get(layout.getId()));
					}
				}
				if(builtParseTable.hasLayout(statenum))
				{
					for(Terminal layout : builtParseTable.getLayout(statenum))
					{
						layoutMaps[statenum][symbolTransTable.get(layout.getId())] = SplitEngine.newBitVec(TERMINAL_COUNT);
						
						for(Terminal t : builtParseTable.getShiftableFollowingLayout(statenum,layout))
						{
							layoutMaps[statenum][symbolTransTable.get(layout.getId())].set(symbolTransTable.get(t.getId()));
						}
					}
				}
				prefixSets[statenum] = SplitEngine.newBitVec(TERMINAL_COUNT);
				if(builtParseTable.hasPrefixes(statenum))
				{
					for(Terminal prefix : builtParseTable.getPrefixes(statenum))
					{
						prefixSets[statenum].set(symbolTransTable.get(prefix.getId()));
					}
				}
				if(builtParseTable.hasPrefixes(statenum))
				{
					for(Terminal prefix : builtParseTable.getPrefixes(statenum))
					{
						if(prefix.equals(FringeSymbols.EMPTY)) continue;
						prefixMaps[statenum][symbolTransTable.get(prefix.getId())] = SplitEngine.newBitVec(TERMINAL_COUNT);
						for(Terminal t : builtParseTable.getShiftableFollowingPrefix(statenum,prefix))
						{
							prefixMaps[statenum][symbolTransTable.get(prefix.getId())].set(symbolTransTable.get(t.getId()));
						}
					}
				}
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n  Scanner code...\n    NFA generation/DFA conversion");
		
		HashSet<Symbol> realRegexes = new HashSet<Symbol>();
		HashSet<Symbol> markingTerminals = new HashSet<Symbol>();
		
		realRegexes.addAll(regexes.keySet());
		for(Iterator<Symbol> it = realRegexes.iterator();it.hasNext();)
		{
			Symbol s = it.next();
			if(grammar.tClassContains(new TerminalClass("marking"),new Terminal(s)))
			{
				markingTerminals.add(s);
				it.remove();
			}
		}

		acceptSets = new BitSet[ONE][ONE];//[scannerStateCounts[ZERO]];
		rejectSets = new BitSet[ONE][ONE];//[scannerStateCounts[ZERO]];
		possibleSets = new BitSet[ONE][ONE];//[scannerStateCounts[ZERO]];

		if(!realRegexes.isEmpty()) buildNFA(ZERO,realRegexes,out);
		if(!markingTerminals.isEmpty()) buildNFA(ONE,markingTerminals,out);
		
		for(i = 0;i < PARSER_STATE_COUNT;i++)
		{
			neededScanners[i] = new BitSet(TWO);
			neededLayoutScanners[i] = new BitSet(TWO);
			neededPrefixScanners[i] = new BitSet(TWO);
			for(Terminal t : builtParseTable.getShiftable(i))
			{
				if(realRegexes.contains(t.getId())) neededScanners[i].set(ZERO);
				else if(markingTerminals.contains(t.getId())) neededScanners[i].set(ONE);
			}
			if(builtParseTable.hasLayout(i))
			{
				for(Terminal t : builtParseTable.getLayout(i))
				{
					if(realRegexes.contains(t.getId())) neededLayoutScanners[i].set(ZERO);
					else if(markingTerminals.contains(t.getId())) neededLayoutScanners[i].set(ONE);
				}
			}
			if(builtParseTable.hasPrefixes(i))
			{
				for(Terminal t : builtParseTable.getPrefixes(i))
				{
					if(realRegexes.contains(t.getId())) neededPrefixScanners[i].set(ZERO);
					else if(markingTerminals.contains(t.getId())) neededPrefixScanners[i].set(ONE);
				}
			}
		}

		out.print("    public void setupEngine()\n");
		out.print("    {\n");
		out.print("    }\n");

		for(i = 0;i < scannerStateCounts.length;i++)
		{
			out.print("    public static int[][] delta_" + i + ";\n");
			out.print("    public static int[] cmap_" + i + ";\n");
			out.print("    public static " + BitSet.class.getName() + "[] acceptSets_" + i + ";\n");
			out.print("    public static " + BitSet.class.getName() + "[] rejectSets_" + i + ";\n");
			out.print("    public static " + BitSet.class.getName() + "[] possibleSets_" + i + ";\n");
		}
		
		out.print("    public int getParseTableAction(int state,int symbol)\n");
		out.print("    {\n");
		out.print("        if(state % 3 == 0) return parseTableExts[state / 3][symbol];\n");
		out.print("        else return parseTableHost[state][symbol];\n");
		out.print("    }\n");

		out.print("    public int transition(int scanner,int state,char ch)\n");
		out.print("    {\n");
		out.print("         switch(scanner)\n");
		out.print("         {\n");
		for(i = 0;i < scannerStateCounts.length;i++)
		{
			out.print("         case " + i + ":\n");
			out.print("             return delta_" + i + "[state][cmap_" + i + "[ch]];\n");
		}
		out.print("         default:\n");
		out.print("              formatError(\"Scanner \" + scanner + \" does not exist\");\n");
		out.print("              return -1;\n");
		out.print("         }\n");
		out.print("    }\n");

		out.print("    public " + BitSet.class.getName() + " getAcceptSet(int scanner,int state)\n");
		out.print("    {\n");
		out.print("         switch(scanner)\n");
		out.print("         {\n");
		for(i = 0;i < scannerStateCounts.length;i++)
		{
			out.print("         case " + i + ":\n");
			out.print("             return acceptSets_" + i + "[state];\n");
		}
		out.print("         default:\n");
		out.print("              formatError(\"Scanner \" + scanner + \" does not exist\");\n");
		out.print("              return null;\n");
		out.print("         }\n");
		out.print("    }\n");
		
		out.print("    public " + BitSet.class.getName() + " getRejectSet(int scanner,int state)\n");
		out.print("    {\n");
		out.print("         switch(scanner)\n");
		out.print("         {\n");
		for(i = 0;i < scannerStateCounts.length;i++)
		{
			out.print("         case " + i + ":\n");
			out.print("             return rejectSets_" + i + "[state];\n");
		}
		out.print("         default:\n");
		out.print("              formatError(\"Scanner \" + scanner + \" does not exist\");\n");
		out.print("              return null;\n");
		out.print("         }\n");
		out.print("    }\n");

		out.print("    public " + BitSet.class.getName() + " getPossibleSet(int scanner,int state)\n");
		out.print("    {\n");
		out.print("         switch(scanner)\n");
		out.print("         {\n");
		for(i = 0;i < scannerStateCounts.length;i++)
		{
			out.print("         case " + i + ":\n");
			out.print("             return possibleSets_" + i + "[state];\n");
		}
		out.print("         default:\n");
		out.print("              formatError(\"Scanner \" + scanner + \" does not exist\");\n");
		out.print("              return null;\n");
		out.print("         }\n");
		out.print("    }\n");

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

		for(Symbol s : symbolTransTable.keySet())
		{
			symbolNames[symbolTransTable.get(s)] = s.toString();
		}
		for(Terminal t : grammar.getT())
		{
			symbolNumbers[symbolTransTable.get(t.getId())] = SplitEngine.newSymbol(SplitEngine.SYMBOL_TERMINAL,0);
		}
		// If "No EMPTY" does not appear, comment this out.
		symbolNumbers[symbolTransTable.get(FringeSymbols.EMPTY.getId())] = SplitEngine.newSymbol(SplitEngine.SYMBOL_TERMINAL,0);
		for(NonTerminal nt : grammar.getNT())
		{
			symbolNumbers[symbolTransTable.get(nt.getId())] = SplitEngine.newSymbol(SplitEngine.SYMBOL_NONTERM,0);
			if(grammar.pContains(nt))
			{
				for(Production p : grammar.getP(nt))
				{
					symbolNumbers[symbolTransTable.get(p.getName())] = SplitEngine.newSymbol(SplitEngine.SYMBOL_PRODUCTION,p.length());
					productionLHSs[symbolTransTable.get(p.getName()) - GRAMMAR_SYMBOL_COUNT] = SplitEngine.newSymbol(SplitEngine.SYMBOL_NONTERM,symbolTransTable.get(p.getLeft().getId()));
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
		outp.writeObject(symbolNumbers);
		out.println("public static final byte[] symbolNumbersHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(productionLHSs);
		out.println("public static final byte[] productionLHSsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(parseTableHost);
		out.println("public static final byte[] parseTableHostHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(parseTableExts);
		out.println("public static final byte[] parseTableExtsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(parseTableMarking);
		out.println("public static final byte[] parseTableMarkingHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
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
		outp.writeObject(shiftableUnion);
		out.println("public static final byte[] shiftableUnionHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		
		for(i = 0;i < scannerStateCounts.length;i++)
		{
			stringOut.reset();
			outp = new ObjectOutputStream(stringOut);
			outp.writeObject(acceptSets[i]);
			out.println("public static final byte[] acceptSets_" + i + "Hash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
			stringOut.reset();
			outp = new ObjectOutputStream(stringOut);
			outp.writeObject(rejectSets[i]);
			out.println("public static final byte[] rejectSets_" + i + "Hash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
			stringOut.reset();
			outp = new ObjectOutputStream(stringOut);
			outp.writeObject(possibleSets[i]);
			out.println("public static final byte[] possibleSets_" + i + "Hash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
			stringOut.reset();
			outp = new ObjectOutputStream(stringOut);
			outp.writeObject(cmap[i]);
			out.println("public static final byte[] cMap_" + i + "Hash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
			stringOut.reset();
			outp = new ObjectOutputStream(stringOut);
			outp.writeObject(delta[i]);
			out.println("public static final byte[] delta_" + i + "Hash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		}
		
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(neededScanners);
		out.println("public static final byte[] neededScannersHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(neededLayoutScanners);
		out.println("public static final byte[] neededLayoutScannersHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(neededPrefixScanners);
		out.println("public static final byte[] neededPrefixScannersHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(scannerStartStates);
		out.println("public static final byte[] scannerStartStatesHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(scannerStateCounts);
		out.println("public static final byte[] scannerStateCountsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		
		out.print("public static void initArrays()\n");
		out.print("throws " + IOException.class.getName() + "," + ClassNotFoundException.class.getName() + "\n");
		out.print("{\n");
		out.print("    symbolNames = (String[]) " + ByteArrayEncoder.class.getName() + ".readHash(symbolNamesHash);\n");
		out.print("    symbolNumbers = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(symbolNumbersHash);\n");
		out.print("    productionLHSs = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(productionLHSsHash);\n");
		out.print("    parseTableHost = (int[][]) " + ByteArrayEncoder.class.getName() + ".readHash(parseTableHostHash);\n");
		out.print("    parseTableExts = (int[][]) " + ByteArrayEncoder.class.getName() + ".readHash(parseTableExtsHash);\n");
		out.print("    parseTableMarking = (int[][]) " + ByteArrayEncoder.class.getName() + ".readHash(parseTableMarkingHash);\n");
		out.print("    shiftableSets = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(shiftableSetsHash);\n");
		out.print("    layoutSets = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(layoutSetsHash);\n");
		out.print("    prefixSets = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(prefixSetsHash);\n");
		out.print("    layoutMaps = (" + BitSet.class.getName() + "[][]) " + ByteArrayEncoder.class.getName() + ".readHash(layoutMapsHash);\n");
		out.print("    prefixMaps = (" + BitSet.class.getName() + "[][]) " + ByteArrayEncoder.class.getName() + ".readHash(prefixMapsHash);\n");
		out.print("    shiftableUnion = (" + BitSet.class.getName() + ") " + ByteArrayEncoder.class.getName() + ".readHash(shiftableUnionHash);\n");
		
		for(i = 0;i < scannerStateCounts.length;i++)
		{
			out.print("    acceptSets_" + i + " = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(acceptSets_" + i + "Hash);\n");
			out.print("    rejectSets_" + i + " = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(rejectSets_" + i + "Hash);\n");
			out.print("    possibleSets_" + i + " = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(possibleSets_" + i + "Hash);\n");
			out.print("    cmap_" + i + " = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(cMap_" + i + "Hash);\n");
			out.print("    delta_" + i + " = (int[][]) " + ByteArrayEncoder.class.getName() + ".readHash(delta_" + i + "Hash);\n");
		}
		
		out.print("    scannerStartStates = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(scannerStartStatesHash);\n");
		out.print("    neededScanners = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(neededScannersHash);\n");
		out.print("    neededLayoutScanners = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(neededLayoutScannersHash);\n");
		out.print("    neededPrefixScanners = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(neededPrefixScannersHash);\n");
		out.print("    scannerStateCounts = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(scannerStateCountsHash);\n");
		out.print("    }\n");

		out.print("    static\n");
		out.print("    {\n");
		out.print("        TERMINAL_COUNT = " + TERMINAL_COUNT + ";\n");
		out.print("        GRAMMAR_SYMBOL_COUNT = " + GRAMMAR_SYMBOL_COUNT + ";\n");
		out.print("        SYMBOL_COUNT = " + SYMBOL_COUNT + ";\n");
		out.print("        PARSER_STATE_COUNT = " + PARSER_STATE_COUNT + ";\n");
		//out.print("        SCANNER_STATE_COUNT = " + SCANNER_STATE_COUNT + ";\n");
		out.print("        DISAMBIG_GROUP_COUNT = " + DISAMBIG_GROUP_COUNT + ";\n");
		//out.print("        SCANNER_START_STATENUM = " + SCANNER_START_STATENUM + ";\n");
		out.print("        PARSER_START_STATENUM = " + PARSER_START_STATENUM + ";\n");
		out.print("        EOF_SYMNUM = " + EOF_SYMNUM + ";\n");
		out.print("        EPS_SYMNUM = " + EPS_SYMNUM + ";\n");
		out.print("        try { initArrays(); }\n");
		out.print("        catch(" + IOException.class.getName() + " ex) { System.err.println(\"IO Exception\"); }\n");
		out.print("        catch(" + ClassNotFoundException.class.getName() + " ex) { System.err.println(\"Class Not Found Exception\"); }");
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
		out.print(parserAncillaries);
		out.print("\n");
		out.print(scannerAncillaries);
		out.print("\n");
		out.print("}\n");
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"  Lexical ambiguity check");
		LexicalAmbiguityChecker lexChecker = new GenericLexicalAmbiguityChecker(logger);
		for(i = 0;i < scannerInfo.length;i++) lexChecker.checkLexicalAmbiguities(grammar,scannerInfo[i],builtParseTable);
	}

	public void buildNFA(int scannerNumber,Iterable<Symbol> validLA,PrintStream out)
	throws CopperException
	{
		int i;
		// Collect the sub-NFAs for all regexes.
		HashSet<NFA> nfas = new HashSet<NFA>();
		for(Symbol forRegex : validLA)
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

		if(scannerStateCounts.length < (scannerNumber + 1))
		{
			int[] newScannerStateCounts = new int[scannerNumber + 1];
			System.arraycopy(scannerStateCounts,0,newScannerStateCounts,0,scannerStateCounts.length);
			scannerStateCounts = newScannerStateCounts;
			int[] newScannerStartStates = new int[scannerNumber + 1];
			System.arraycopy(scannerStartStates,0,newScannerStartStates,0,scannerStartStates.length);
			scannerStartStates = newScannerStartStates;
		}
		scannerStateCounts[scannerNumber] = mergedDFA.getStates().size() + 1;
		
		if(acceptSets.length < scannerStateCounts.length ||
		   acceptSets[scannerNumber].length < scannerStateCounts[scannerNumber])
		{
			BitSet[][] newAcceptSets = new BitSet[scannerStateCounts.length][Math.max(acceptSets[0].length,scannerStateCounts[scannerNumber])];
			BitSet[][] newRejectSets = new BitSet[scannerStateCounts.length][Math.max(acceptSets[0].length,scannerStateCounts[scannerNumber])];
			BitSet[][] newPossibleSets = new BitSet[scannerStateCounts.length][Math.max(acceptSets[0].length,scannerStateCounts[scannerNumber])];
			for(i = 0;i < acceptSets.length;i++)
			{
				System.arraycopy(acceptSets[i],0,newAcceptSets[i],0,acceptSets[i].length);
				System.arraycopy(rejectSets[i],0,newRejectSets[i],0,rejectSets[i].length);
				System.arraycopy(possibleSets[i],0,newPossibleSets[i],0,possibleSets[i].length);
			}
			acceptSets = newAcceptSets;
			rejectSets = newRejectSets;
			possibleSets = newPossibleSets;
		}

		
		if(scannerInfo.length < scannerStateCounts.length ||
		   scannerInfo[scannerNumber].length < scannerStateCounts[scannerNumber])
		{
			QScannerStateInfo[][] newScannerInfo = new QScannerStateInfo[scannerStateCounts.length][Math.max(scannerInfo[0].length,scannerStateCounts[scannerNumber])];
			for(i = 0;i < scannerInfo.length;i++)
			{
				System.arraycopy(scannerInfo[i],0,newScannerInfo[i],0,scannerInfo[i].length);
			}
			scannerInfo = newScannerInfo;
		}
		for(i = 0;i < scannerInfo[scannerNumber].length;i++) scannerInfo[scannerNumber][i] = new QScannerStateInfo();
		int nextStateNum = 1;
		// For each state in the DFA:
		for(NFAState state : mergedDFA.getStates())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			// Assign it a number.
			numericalMapping.put(state,nextStateNum);
			// If it be the start state, record its number.
			if(state.equals(mergedDFA.getStartState()))
			{
				scannerStartStates[scannerNumber] = nextStateNum;
			}
			// For each regex for which it accepts, add the information
			// to the stateInfo array.
			for(Symbol sym : state.getAccepts())
			{
				scannerInfo[scannerNumber][nextStateNum].addAcceptingSyms(new Terminal(sym));
			}
			// Compress its transitions into ranges.
			state.compressTransitions();
			nextStateNum++;
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Static lexical disambiguation");

		// For each state in the DFA:
		if(grammar != null)
		{
			//Hashtable<Symbol,Integer> maxStaticPrecedences;
			for(NFAState state : mergedDFA.getStates())
			{
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
				HashSet<Terminal> accF = scannerInfo[scannerNumber][numericalMapping.get(state)].getAcceptingSyms();
				PrecedenceRelationGraph accFG = grammar.getPrecedenceRelationsGraph().makeCut(accF);
				HashSet<Terminal> rej = accFG.partitionAcceptSet(logger,"static precedence disambiguator, scanner state " + numericalMapping.get(state));
				for(Terminal t : rej)
				{
					scannerInfo[scannerNumber][numericalMapping.get(state)].removeAcceptingSyms(t);
					scannerInfo[scannerNumber][numericalMapping.get(state)].addRejectingSyms(t);
				}
			}
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Transitive closure");
		
		boolean[][] transClosure = new boolean[scannerStateCounts[scannerNumber]][scannerStateCounts[scannerNumber]];
		// Compute the transitive closure of the DFA's states
		// using the Floyd-Warshall algorithm, as presented in
		// Cormen, Leiserson, Rivest and Stein's
		// "Introduction to Algorithms," Second Edition,
		// section 25.2.
		for(i = 0;i < scannerStateCounts[scannerNumber];i++) transClosure[i][i] = true;
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
		for(int k = 0;k < scannerStateCounts[scannerNumber];k++)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			for(i = 0;i < scannerStateCounts[scannerNumber];i++)
			{
				for(int j = 0;j < scannerStateCounts[scannerNumber];j++)
				{
					transClosure[i][j] = transClosure[i][j] || (transClosure[i][k] && transClosure[k][j]);
				}
			}
		}
		// Use the transitive closure to compute possible sets:
		for(i = 0;i < scannerStateCounts[scannerNumber];i++)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			for(int j = 0;j < scannerStateCounts[scannerNumber];j++)
			{
				// If states i and j be connected by transition:
				if(transClosure[i][j])
				{
					// Union the current possible set of i with
					// the accepting set of j.
					for(Terminal t : scannerInfo[scannerNumber][j].getAcceptingSyms())
					{
						scannerInfo[scannerNumber][i].addPossibleSyms(t);
					}
					for(Terminal t : scannerInfo[scannerNumber][j].getRejectingSyms())
					{
						scannerInfo[scannerNumber][i].addPossibleSyms(t);
					}
				}
			}
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Accept/possible info");

		for(i = 0;i < scannerInfo[scannerNumber].length;i++)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
				acceptSets[scannerNumber][i] = SplitEngine.newBitVec(TERMINAL_COUNT);
				for(Terminal sym : scannerInfo[scannerNumber][i].getAcceptingSyms())
				{
					acceptSets[scannerNumber][i].set(symbolTransTable.get(sym.getId()));
				}
				possibleSets[scannerNumber][i] = SplitEngine.newBitVec(TERMINAL_COUNT);
				for(Terminal sym : scannerInfo[scannerNumber][i].getPossibleSyms())
				{
					possibleSets[scannerNumber][i].set(symbolTransTable.get(sym.getId()));
				}
				rejectSets[scannerNumber][i] = SplitEngine.newBitVec(TERMINAL_COUNT);
				for(Terminal sym : scannerInfo[scannerNumber][i].getRejectingSyms())
				{
					rejectSets[scannerNumber][i].set(symbolTransTable.get(sym.getId()));
				}
		}
		
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Transition table");

		if(cmap.length < (scannerNumber + 1))
		{
			int[][] newcmap = new int[scannerNumber + 1][Character.MAX_VALUE];
			for(int j = 0;j < cmap.length;j++)
			{
				System.arraycopy(cmap[j],0,newcmap[j],0,Character.MAX_VALUE);
			}
			cmap = newcmap;
		}
		i = 0;
		for(NFAState state : mergedDFA.getStates())
		{
			for(CharacterRange cr : state.getTransitionSymbols())
			{
				for(char c = cr.firstChar();c <= cr.lastChar();c++)
				{
					// DEBUG-X-BEGIN
					//System.err.println("'" + c + "' maps to " + (i+1));
					// DEBUG-X-END
					if(cmap[scannerNumber][c] == 0) cmap[scannerNumber][c] = ++i;
				}
			}
		}
		if(delta.length < scannerStateCounts[scannerNumber] ||
		   delta[scannerNumber].length < scannerStateCounts[scannerNumber] ||
		   delta[scannerNumber][0].length < (i+1))
		{
			int[][][] newDelta =
				  new int[scannerStateCounts.length]
				         [Math.max(delta[0].length,scannerStateCounts[scannerNumber])]
				         [Math.max(delta[0][0].length,(i+1))];
			for(int j = 0;j < delta.length;j++)
			{
				for(int k = 0;k < delta[0].length;k++)
				{
					System.arraycopy(delta[j][k],0,newDelta[j][k],0,delta[j][k].length);
				}
			}
			delta = newDelta;
		}
		//delta = new int[scannerStateCounts[scannerNumber]][i+1];
		for(NFAState state : mergedDFA.getStates())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 2,".");
			for(Pair<CharacterRange,NFAState> transition : state)
			{
				for(char c = transition.first().firstChar();c <= transition.first().lastChar();c++)
				{
					delta[scannerNumber][numericalMapping.get(state)][cmap[scannerNumber][c]] = numericalMapping.get(transition.second());
				}
			}
		}
		// DEBUG-X-BEGIN
/*		System.err.println("Final:");
		for(i = 0;i < possibleSets.length;i++)
		{
			for(int j = 0;j < possibleSets[0].length;j++)
			{
				System.err.print(acceptSets[i][j] + ":");
				System.err.print(possibleSets[i][j] + " ");
			}
			System.err.println();
		}
		System.err.println("Start states: ");
		for(i = 0;i < scannerStartStates.length;i++)
		{
			System.err.print(scannerStartStates[i] + " ");
		}
		System.err.println();*/
		// DEBUG-X-END
	}
	public Pair<Integer,String> visitAcceptAction(AcceptAction action)
	{
		return Pair.cons(SplitEngine.newAction(SplitEngine.STATE_ACCEPT,0),"accept");
	}

	public Pair<Integer,String> visitFullReduceAction(FullReduceAction action)
	{
		return Pair.cons(SplitEngine.newAction(SplitEngine.STATE_REDUCE,symbolTransTable.get(action.getProd().getName())),"reduce(" + action.getProd() + ")");
	}

	public Pair<Integer,String> visitShiftAction(ShiftAction action)
	{
		return Pair.cons(SplitEngine.newAction(SplitEngine.STATE_SHIFT,action.getDestState()),"shift(" + action.getDestState() + ")");
	}

	
	public int getScannerStateCount()
	{
		return scannerInfo.length;
	}
}
