package edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.moded;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
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
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Choice;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.KleeneStar;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.MacroHole;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegexVisitor;
import edu.umn.cs.melt.copper.compiletime.auxiliary.CharacterRange;
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
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.EngineBuilder;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.RegexInfo;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.ByteArrayEncoder;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter;
import edu.umn.cs.melt.copper.runtime.engines.moded.ModedEngine;
import edu.umn.cs.melt.copper.runtime.engines.moded.scanner.ModedMatchData;
import edu.umn.cs.melt.copper.runtime.engines.moded.semantics.ModedSemanticActionContainer;
import edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes;
import edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public class ModedEngineBuilder implements EngineBuilder,ParseActionVisitor<Pair<Integer, String>, CopperException>
{
	private GrammarSource grammar;
	private GLRParseTable builtParseTable;
	private CompilerLogger logger;
	private Hashtable<Symbol,RegexInfo> regexes;
	
	private Hashtable<Symbol,Integer> symbolTransTable;
	private Hashtable< HashSet<Terminal>,LexicalDisambiguationGroup > lexGroupTransTable;
	private Hashtable<NFAState,Integer> numericalStateMapping; 
	TreeSet<Integer> sortedStates; 
	Hashtable< HashSet<Terminal>,NFA > allDFAs;
	Hashtable< HashSet<Symbol>,HashSet< HashSet<Terminal> > > encounteredAmbiguities;
	Hashtable< HashSet<Symbol>,Integer > schroedingerAmbiguities;
	int nextSchroedingerIndex;
	int nextStateNum;
	
	/** Names of terminals, nonterminals, productions, etc. */
	public String[] symbolNames;
	/** Lengths of productions, types of symbols. */
	public int[] symbolNumbers;
	/** Symbols on the left-hand sides of productions. */
	public int[] productionLHSs;
	
	/** Parse actions. */
	public int[][] parseTable;
	/** Scanner states corresponding to shiftable sets. */
	public int[] shiftableStates;
	/** Shiftable sets. */
	public BitSet[] shiftableSets;
	/** Layout sets. */
	public int[] layoutSets;
	/** Prefix sets. */
	public int[] prefixSets;
	/** Maps of prefix terminals. */
	public int[][] prefixMaps;
	/** Whether or not the improved layout scanner is used. */
	public byte[] meldedSituations;
	public int meldedSituation;
	
	NFA shiftableUnionDFA;
	NFA shiftableUnionMeldedDFA;
	HashSet<NFAState> ambiguousStates = null;
	HashSet<NFAState> ambiguousStatesMelded = null;
	
	private class DFAAnalysisData
	{
		public DFAAnalysisData()
		{
			transClosure = new Hashtable<NFA,boolean[][]>();
			transClosureMinusReflexive = new Hashtable<NFA,boolean[][]>();
			possibleSets = new Hashtable< NFAState,HashSet<Terminal> >();
		}
		Hashtable<Terminal,String> keywords;
		Hashtable<NFA,boolean[][]> transClosure;
		Hashtable<NFA,boolean[][]> transClosureMinusReflexive;
		Hashtable< Terminal,HashSet<Terminal> > followsGraph;
		Hashtable< Terminal,boolean[][] > matches;
		Hashtable< Integer,HashSet< HashSet<Terminal> > > differences;
		Hashtable< NFAState,HashSet<Terminal> > possibleSets;
	}
	
	DFAAnalysisData dfaData;

	
	/** Shiftable union --- all terminals with a parse action
	 * (usually all non-prefix, non-layout terminals). */
	public int shiftableUnion,shiftableUnionMelded;
	
	/** State sets for scanner DFA. */
	public int[] acceptSets;
	
	public int[][] delta;
	public int[] cmap;

	/* Counts for building arrays statically. */
	private int TERMINAL_COUNT;
	private int GRAMMAR_SYMBOL_COUNT;
	private int GRAMMAR_STRUCTURE_COUNT;
	private int SYMBOL_COUNT;
	private int PARSER_STATE_COUNT;
	private int SCANNER_STATE_COUNT;
	private int DISAMBIG_GROUP_COUNT;
	
	//private int SCANNER_START_STATENUM;
	private int PARSER_START_STATENUM;
	private int EOF_SYMNUM;
	private int EPS_SYMNUM;

	public ModedEngineBuilder(GrammarSource grammar,LALR1DFA dfa,GLRParseTable builtParseTable,CompilerLogger logger)
	{
		this.grammar = grammar;
		this.builtParseTable = builtParseTable;
		this.logger = logger;
		this.regexes = new Hashtable<Symbol,RegexInfo>();
		this.dfaData = new DFAAnalysisData();
	}
	
	public <E> boolean areDisjoint(HashSet<E> x,HashSet<E> y)
	{
		HashSet<E> intersection = new HashSet<E>(x);
		intersection.retainAll(y);
		return intersection.isEmpty();
	}
	
	public HashSet<Terminal> getAugmentedShiftable(HashSet<Terminal> shiftable)
	{
		return grammar.getPrecedenceRelationsGraph().getClosure(shiftable);
	}
	
	public NFA buildDFA(int statenum)
	{
		if(!builtParseTable.hasShiftable(statenum)) return null;
		HashSet<Terminal> shiftable = new HashSet<Terminal>(builtParseTable.getShiftable(statenum));
		if(ModedEngine.isMelded(meldedSituations[statenum]))
		{
			if(builtParseTable.hasLayout(statenum)) shiftable.addAll(builtParseTable.getLayout(statenum));
			if(builtParseTable.hasPrefixes(statenum)) shiftable.addAll(builtParseTable.getPrefixes(statenum));
		}
		if(shiftable.isEmpty() ||
		   (shiftable.size() == 1 && shiftable.contains(FringeSymbols.EOF)) ||
	       allDFAs.containsKey(shiftable)) return null;
		NFA dfa = buildDFA(shiftable);
		allDFAs.put(shiftable,dfa);
		if(ModedEngine.isMelded(meldedSituations[statenum]) &&
	       dfa.getStartState().getAccepts().isEmpty() &&
	       (builtParseTable.hasLayout(statenum) &&
	        (builtParseTable.getLayout(statenum).size() > 1 ||
	         !builtParseTable.getLayout(statenum).contains(FringeSymbols.EMPTY))))
		{
			// DEBUG-X-BEGIN
			//System.err.println("One layout at least required in state " + statenum + " having layout " + builtParseTable.getLayout(statenum));
			// DEBUG-X-END
			meldedSituations[statenum] |= ModedEngine.SITUATION_REQ_LAYOUT;
		}
		return dfa;
	}

	public NFA buildDFA(HashSet<Terminal> shiftable)
	{
		HashSet<NFA> NFAs = new HashSet<NFA>();
		BitSet shiftableB = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
		HashSet<Terminal> higherPrecShiftable = getAugmentedShiftable(shiftable);//grammar.getPrecedenceRelationsGraph().getClosure(shiftable);
		for(Terminal t : higherPrecShiftable)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			if(!regexes.containsKey(t.getId())) continue;
			NFAs.add(regexes.get(t.getId()).getRegex().generateAutomaton(t.getId()));
			shiftableB.set(symbolTransTable.get(t.getId()));
		}
		HashSet<NFAState> allStates = new HashSet<NFAState>();
		NFAState newStartState = new NFAState(Symbol.symbol("START-" + shiftableB.toString()),null);
		allStates.add(newStartState);
		for(NFA curNFA : NFAs)
		{
			newStartState.addTransition(new Character(NFAState.EmptyChar),curNFA.getStartState());
			allStates.addAll(curNFA.getStates());
		}
		NFA NFA = new NFA(allStates,newStartState);
		NFA DFA = new NFA2DFA().determinizeNFA(NFA);
		return DFA;
	}
	
	public HashSet<NFAState> findAmbiguousStates(NFA shiftableUnionDFA)
	{
		int i;
		HashSet<NFAState> ambiguousStates = new HashSet<NFAState>();
		//boolean unionIsUnambiguous = true;
		for(NFAState state : shiftableUnionDFA.getAcceptStates())
		{
			if(state.getAccepts().size() > 1)
			{
				ambiguousStates.add(state);
				/*if(unionIsUnambiguous)
				{
					HashSet<Terminal> ambiguousAccept = new HashSet<Terminal>();
					for(Symbol s : state.getAccepts()) ambiguousAccept.add(new Terminal(s));
					unionIsUnambiguous = lexGroupTransTable.containsKey(ambiguousAccept);
				}*/
			}
			// DEBUG-X-BEGIN
			//if(state.getAccepts().size() > 1) System.err.println("State that did it: " + state.getAccepts());
			// DEBUG-X-END
		}
		
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n    Transitive closure");
		
		SCANNER_STATE_COUNT = nextStateNum;
		dfaData.transClosure.put(shiftableUnionDFA,new boolean[SCANNER_STATE_COUNT][SCANNER_STATE_COUNT]);
		dfaData.transClosureMinusReflexive.put(shiftableUnionDFA,new boolean[SCANNER_STATE_COUNT][SCANNER_STATE_COUNT]);
		boolean[][] transClosure = dfaData.transClosure.get(shiftableUnionDFA);
		boolean[][] transClosureMinusReflexive = dfaData.transClosureMinusReflexive.get(shiftableUnionDFA);
		// Compute the transitive closure of the DFA's states
		// using the Floyd-Warshall algorithm, as presented in
		// Cormen, Leiserson, Rivest and Stein's
		// "Introduction to Algorithms," Second Edition,
		// section 25.2.
		for(i = 0;i < SCANNER_STATE_COUNT;i++) transClosure[i][i] = true;
		for(NFAState st : shiftableUnionDFA.getStates())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			for(Pair<CharacterRange,NFAState> jp : st)
			{
				transClosure
					[numericalStateMapping.get(st)]
					 [numericalStateMapping.get(
							 jp.second()
							 )] = true;
				transClosureMinusReflexive
					[numericalStateMapping.get(st)]
					 [numericalStateMapping.get(
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
					transClosureMinusReflexive[i][j] = transClosureMinusReflexive[i][j] || (transClosureMinusReflexive[i][k] && transClosureMinusReflexive[k][j]);
				}
			}
		}
		// Use the transitive closure to compute possible sets:
		for(NFAState fromState : shiftableUnionDFA.getAcceptStates())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			for(NFAState toState : shiftableUnionDFA.getAcceptStates())
			{
				// If states i and j are connected by transition:
				if(transClosure[numericalStateMapping.get(fromState)][numericalStateMapping.get(toState)])
				{
					// Union the current possible set of i with
					// the accepting set of j.
					if(!dfaData.possibleSets.containsKey(fromState)) dfaData.possibleSets.put(fromState,new HashSet<Terminal>());
					for(Symbol acc : toState.getAccepts()) dfaData.possibleSets.get(fromState).add(new Terminal(acc));
				}
			}
			// DEBUG-X-BEGIN
			//System.err.println(fromState.getAccepts() + " ->* " + dfaData.possibleSets.get(fromState));
			// DEBUG-X-END
		}
		return ambiguousStates;
	}

	public void findShiftableUnionAmbiguousStates()
	{
		ambiguousStates = null;
		ambiguousStatesMelded = null;

		if(shiftableUnionDFA != null)
		{
			numberDFAStates(shiftableUnionDFA);
			shiftableUnion = numericalStateMapping.get(shiftableUnionDFA.getStartState());
			ambiguousStates = findAmbiguousStates(shiftableUnionDFA);
		}
		if(shiftableUnionMeldedDFA != null)
		{
			numberDFAStates(shiftableUnionMeldedDFA);
			shiftableUnionMelded = numericalStateMapping.get(shiftableUnionMeldedDFA.getStartState());
			ambiguousStatesMelded = findAmbiguousStates(shiftableUnionMeldedDFA);
		}
	}
	
	public void compressDFATransitions(NFA dfa)
	{
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
		for(NFAState state : dfa.getStates())
		{
			/*for(Symbol sym : state.getAccepts())
			{
				scannerInfo[nextStateNum].addAcceptingSyms(new Terminal(sym));
			}*/
			state.compressTransitions();
		}

	}
	
	public void disambiguateDFA(NFA dfa)
	throws CopperException
	{
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
		for(NFAState state : dfa.getStates())
		{
			HashSet<Terminal> accF = new HashSet<Terminal>();
			for(Symbol s : state.getAccepts()) accF.add(new Terminal(s));
			PrecedenceRelationGraph accFG = grammar.getPrecedenceRelationsGraph().makeCut(accF);
			HashSet<Terminal> rej = accFG.partitionAcceptSet(logger,"static precedence disambiguator");
			for(Iterator<Symbol> it = state.getAccepts().iterator();it.hasNext();)
			{
				if(rej.contains(new Terminal(it.next())))
				{
					it.remove();
				}
			}
		}
	}
	
	public void numberDFAStates(NFA dfa)
	{
		for(NFAState state : dfa.getStates())
		{
			if(!numericalStateMapping.containsKey(state)) numericalStateMapping.put(state,nextStateNum);
			nextStateNum++;
		}
	}

	
	public void populateArrays(HashSet<Terminal> shiftable,NFA dfa,boolean tolerateAmbiguities)
	{
		for(NFAState state : dfa.getStates())
		{
			// if(!numericalStateMapping.containsKey(state)) continue;
			if(state.getAccepts().isEmpty())
			{
				acceptSets[numericalStateMapping.get(state)] = -1;
			}
			else if(state.getAccepts().size() == 1)
			{
				acceptSets[numericalStateMapping.get(state)] = symbolTransTable.get(state.getAccepts().iterator().next());
			}
			else
			{
				HashSet<Terminal> ambiguousAccept = new HashSet<Terminal>();
				for(Symbol s : state.getAccepts()) ambiguousAccept.add(new Terminal(s));
				if(lexGroupTransTable.containsKey(ambiguousAccept))
				{
					acceptSets[numericalStateMapping.get(state)] = symbolTransTable.get(lexGroupTransTable.get(ambiguousAccept).getName().getId());
				}
				else if(tolerateAmbiguities)
				{
					acceptSets[numericalStateMapping.get(state)] = nextSchroedingerIndex;
					// Tolerate the ambiguity as it is in the "Shiftable Union" special DFA.
					if(!schroedingerAmbiguities.containsKey(state.getAccepts()))
					{
						schroedingerAmbiguities.put(state.getAccepts(),nextSchroedingerIndex++);
					}
				}
				else
				{
					HashSet<Symbol> originals = new HashSet<Symbol>();
					for(Symbol s : state.getAccepts())
					{
						if(shiftable.contains(new Terminal(s))) originals.add(s);
					}
					if(originals.isEmpty())
					{
						if(!schroedingerAmbiguities.containsKey(state.getAccepts()))
						{
							acceptSets[numericalStateMapping.get(state)] = nextSchroedingerIndex;
							schroedingerAmbiguities.put(state.getAccepts(),nextSchroedingerIndex++);
						}
						else acceptSets[numericalStateMapping.get(state)] = schroedingerAmbiguities.get(state.getAccepts());
					}
					else if(originals.size() == 1)
					{
						acceptSets[numericalStateMapping.get(state)] = symbolTransTable.get(originals.iterator().next());
					}
					else
					{
						acceptSets[numericalStateMapping.get(state)] = -1;
						if(!encounteredAmbiguities.containsKey(originals)) encounteredAmbiguities.put(originals,new HashSet< HashSet<Terminal> >());
						encounteredAmbiguities.get(originals).add(shiftable);
					}
				}
			}
		}
	}
	
	public boolean testUnionAdequacyConservative(int statenum)
	{
		boolean unionIsAdequate = true;
		HashSet<NFAState> specificAmbiguousStates;
		HashSet<Terminal> shiftable = new HashSet<Terminal>(builtParseTable.getShiftable(statenum));
		if(ModedEngine.isMelded(meldedSituations[statenum]))
		{
			if(builtParseTable.hasLayout(statenum)) shiftable.addAll(builtParseTable.getLayout(statenum));
			if(builtParseTable.hasPrefixes(statenum)) shiftable.addAll(builtParseTable.getPrefixes(statenum));
			specificAmbiguousStates = ambiguousStatesMelded;
		}
		else specificAmbiguousStates = ambiguousStates;
		HashSet<Terminal> augmentedShiftable = getAugmentedShiftable(shiftable);
		for(NFAState state : specificAmbiguousStates)
		{
			HashSet<Terminal> ambiguousAccept = new HashSet<Terminal>();
			for(Symbol s : state.getAccepts()) ambiguousAccept.add(new Terminal(s));
			if(!areDisjoint(ambiguousAccept,shiftable))
			{
				unionIsAdequate &= shiftable.containsAll(ambiguousAccept) &&
					               lexGroupTransTable.containsKey(ambiguousAccept);
			}
			if(!unionIsAdequate) break;
		}
		if(unionIsAdequate)
		{
			NFA specificShiftableUnionDFA;
			if(ModedEngine.isMelded(meldedSituations[statenum])) specificShiftableUnionDFA = shiftableUnionMeldedDFA;
			else specificShiftableUnionDFA = shiftableUnionDFA;
			for(NFAState state : specificShiftableUnionDFA.getAcceptStates())
			{
				if(specificShiftableUnionDFA == shiftableUnionMeldedDFA && state == specificShiftableUnionDFA.getStartState()) continue;
				HashSet<Terminal> accept = new HashSet<Terminal>();
				for(Symbol s : state.getAccepts()) accept.add(new Terminal(s));
				if(areDisjoint(accept,shiftable)) continue;
				if(!dfaData.possibleSets.containsKey(state)) continue;
				if(!augmentedShiftable.containsAll(dfaData.possibleSets.get(state)))
				{
					HashSet<Terminal> diff = new HashSet<Terminal>(dfaData.possibleSets.get(state));
					diff.removeAll(augmentedShiftable);
					//if(!dfaData.differences.containsKey(statenum)) dfaData.differences.put(statenum,new HashSet< HashSet<Terminal> >());
					dfaData.differences.get(statenum).add(diff);
				}
				unionIsAdequate &= augmentedShiftable.containsAll(dfaData.possibleSets.get(state));
				//if(!unionIsAdequate) break;
			}
		}
		return unionIsAdequate;
	}
	
	private class RegexIsKeyword implements ParsedRegexVisitor<String,Object,RuntimeException>
	{
		public String visitCharacterSet(CharacterSet regex, Object inheritance)
		{
			if(regex.size() != 1) return null;
			else return String.valueOf(regex.getFirstChar());
		}

		public String visitChoice(Choice regex, Object inheritance)
		{
			return null;
		}

		public String visitConcatenation(Concatenation regex, Object inheritance)
		{
			String concat = "";
			for(ParsedRegex r : regex.getConstituents())
			{
				String currentKeyword = r.acceptVisitor(this,inheritance);
				if(currentKeyword == null) return null;
				concat += currentKeyword;
			}
			return concat;
		}

		public String visitEmptyString(EmptyString regex, Object inheritance)
		{
			return "";
		}

		public String visitKleeneStar(KleeneStar regex, Object inheritance)
		{
			return null;
		}

		public String visitMacroHole(MacroHole regex, Object inheritance)
		{
			return null;
		}
		
	}
	
	public void findKeywords(HashSet<Terminal> fromAmong)
	{
		dfaData.keywords = new Hashtable<Terminal,String>();
		RegexIsKeyword keywordFinder = new RegexIsKeyword();
		for(Terminal t : fromAmong)
		{
			if(!grammar.hasRegex(t)) continue;
			String LofT = grammar.getRegex(t).acceptVisitor(keywordFinder,null);
			if(LofT != null) dfaData.keywords.put(t,LofT);
		}
	}
	
	public void findFollowGraph(HashSet<Terminal> fromAmong)
	{
		dfaData.followsGraph = new Hashtable< Terminal,HashSet<Terminal> >();
		for(Terminal from : fromAmong)
		{
			dfaData.followsGraph.put(from,new HashSet<Terminal>());
			for(Terminal to : fromAmong)
			{
				boolean mayFollow = false;
				for(NonTerminal nt : grammar.getNT())
				{
					if(grammar.getP(nt) == null) continue;
					for(Production p : grammar.getP(nt))
					{
						boolean sawFrom = false;
						for(GrammarSymbol sym : p.getRight())
						{
							mayFollow |= (sawFrom && (sym.equals(to) || grammar.getContextSets().firstContains(sym,to)));
							if(mayFollow) break;
							sawFrom &= grammar.getContextSets().getNullable().contains(sym);
							sawFrom |= sym.equals(from);
						}
						mayFollow |= sawFrom && grammar.getContextSets().followContains(p.getLeft(),to);
						if(mayFollow) break;
					}
					if(mayFollow) break;
				}
				if(mayFollow) dfaData.followsGraph.get(from).add(to);
			}
		}
	}
	
	public boolean testUnionAdequacyKeyword(Collection<Terminal> shiftable,HashSet< HashSet<Terminal> > differences)
	{
		if(differences.isEmpty()) return false;
		boolean[][] transClosure = dfaData.transClosure.get(shiftableUnionMeldedDFA);
		for(HashSet<Terminal> oneDiff : differences)
		for(Terminal difference : oneDiff)
		{
			if(!dfaData.keywords.containsKey(difference))
            {
                 return false;
			}
			String diffLexeme = dfaData.keywords.get(difference);
			NFAState currentState = shiftableUnionDFA.getStartState(),lastShiftableState = null;
			int lastShiftableAccepted = -1;
			for(int i = 0;i < diffLexeme.length();i++)
			{
				for(Terminal t : shiftable)
				{
					if(currentState.getAccepts().contains(t.getId()))
					{
						lastShiftableAccepted = i;
						lastShiftableState = currentState;
						break;
					}
				}
				char nextChar = diffLexeme.charAt(i);
				for(CharacterRange cr : currentState.getTransitionSymbols())
				{
					if(cr.isInRange(nextChar))
					{
						currentState = currentState.getTransitions(cr).iterator().next();
						break;
					}
				}
			}
			HashSet<Terminal> acceptsUnion = new HashSet<Terminal>();
			//System.err.println(transClosure.length + "," + numericalStateMapping.get(currentState));
			for(NFAState state : shiftableUnionDFA.getAcceptStates())
			{
				//System.err.println(state);
				if(transClosure
						  [numericalStateMapping.get(currentState)]
						  [numericalStateMapping.get(state)])
				{
					for(Symbol s : state.getAccepts())
					{
						acceptsUnion.add(new Terminal(s));
					}
				}
			}
			for(Terminal t : shiftable)
			{
				if(acceptsUnion.contains(t)) return false;
			}
			if(lastShiftableAccepted == -1)
			{
				//System.err.println(difference + ":" + diffLexeme + " - " + shiftable);
				return true;
				//lastShiftableAccepted = 0;
				//lastShiftableState = shiftableUnionDFA.getStartState();
			}
			if(lastShiftableState.getAccepts().size() > 1)
			{
				//System.err.println(difference + "::::" + lastShiftableAccepted + " " + currentState.getAccepts().size());
				return false;
			}
			//String y = diffLexeme.substring(lastShiftableAccepted);
			dfaData.matches = new Hashtable< Terminal,boolean[][] >();
			for(Terminal t : grammar.getT())
			{
				dfaData.matches.put(t,new boolean[diffLexeme.length()][diffLexeme.length()]);
			}
			for(int i = lastShiftableAccepted;i < diffLexeme.length();i++)
			{
				currentState = shiftableUnionDFA.getStartState();
				for(int j = i;j < diffLexeme.length();j++)
				{
					for(Terminal t : shiftable)
					{
						if(currentState.getAccepts().contains(t))
						{
							dfaData.matches.get(t)[i][j+1] = true;
						}
					}
					char nextChar = diffLexeme.charAt(j);
					for(CharacterRange cr : currentState.getTransitionSymbols())
					{
						if(cr.isInRange(nextChar))
						{
							currentState = currentState.getTransitions(cr).iterator().next();
						}
					}
				}
			}
			//System.err.println(lastShiftableAccepted);
			HashSet< ArrayList<Terminal> > validTerminalSeqs = getValidTermSeqs(new Terminal(lastShiftableState.getAccepts().iterator().next()),lastShiftableAccepted,diffLexeme.length());
			//System.err.println(getPowerSetSlice(2,3));
			//System.err.println(difference + ": " + validTerminalSeqs);
			for(ArrayList<Terminal> seq : validTerminalSeqs)
			{
				for(ArrayList<Integer> combination : getPowerSetSlice(validTerminalSeqs.size(),diffLexeme.length()))
				{
					boolean combinationWorks = true;
					for(int i = 0;combinationWorks && i < (combination.size() - 1);i++)
					{
						combinationWorks &= dfaData.matches.get(seq.get(i))[combination.get(i)][combination.get(i + 1)];
					}
					if(combinationWorks) return false;
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private HashSet< ArrayList<Integer> > getPowerSetSlice(int seqLength,int maxStringLength)
	{
		HashSet< ArrayList<Integer> > rv = new HashSet< ArrayList<Integer> >();
		LinkedList< ArrayList<Integer> > fringe = new LinkedList< ArrayList<Integer> >();
		ArrayList<Integer> buffer = new ArrayList<Integer>(),buffer2;
		buffer.add(0);
		fringe.offer(buffer);
		while(!fringe.isEmpty())
		{
			buffer = fringe.poll();
			if(buffer.size() == seqLength) rv.add(buffer);
			else for(int i = buffer.get(buffer.size() - 1) + 1;i <= maxStringLength;i++)
			{
				buffer2 = (ArrayList<Integer>) buffer.clone();
				buffer2.add(i);
				fringe.offer(buffer2);
			}
		}
		return(rv);
	}
	
	@SuppressWarnings("unchecked")
	private HashSet< ArrayList<Terminal> > getValidTermSeqs(Terminal start,int lastShiftableAccepted,int maxLength)
	{
		HashSet< ArrayList<Terminal> > rvs = new HashSet< ArrayList<Terminal> >();
		
		LinkedList< ArrayList<Terminal> > fringe = new LinkedList< ArrayList<Terminal> >();
		ArrayList<Terminal> buffer = new ArrayList<Terminal>(),buffer2;
		buffer.add(start);
		fringe.offer(buffer);
		while(!fringe.isEmpty())
		{
			buffer = fringe.poll();
			rvs.add(buffer);
			if(buffer.size() >= maxLength) continue;
			for(Terminal t : dfaData.followsGraph.get(buffer.get(buffer.size() - 1)))
			{
				buffer2 = (ArrayList<Terminal>) buffer.clone();
				buffer2.add(t);
				fringe.offer(buffer2);
			}
		}
		
		return rvs;
		
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
		lexGroupTransTable = new Hashtable< HashSet<Terminal>,LexicalDisambiguationGroup>();

		if(packageDecl.equals("") &&
				   importDecls.equals("")) packageDecl = grammar.getParserSources().getClassFilePreambleCode();
		else importDecls += "\n" + grammar.getParserSources().getClassFilePreambleCode();
		String rootType = grammar.getNTAttributes(grammar.getStartSym()).getType();
		String errorType = CopperParserException.class.getName();

	    parserAncillaries += "    public " + rootType + " parse(" + Reader.class.getName() + " input,String inputName)\n";
	    parserAncillaries += "    throws " + IOException.class.getName() + "," + errorType + "\n";
	    parserAncillaries += "    {\n";
	    parserAncillaries += "        this.buffer = " + ScannerBuffer.class.getName() + ".instantiate(input);\n";
	    parserAncillaries += "        setupEngine();\n";
	    parserAncillaries += "        startEngine(" + InputPosition.class.getName() + ".initialPos(inputName));\n";
	    parserAncillaries += "        " + rootType + " parseTree = runEngine();\n";
	    parserAncillaries += "        return parseTree;\n";
	    parserAncillaries += "    }\n";
	    parserAncillaries += "\n";
		parserAncillaries += grammar.getParserSources().getParserClassAuxCode();

		out.print(packageDecl + "\n");
		out.print(importDecls + "\n");
		// DEBUG-BEGIN
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"  Parser code");
		// DEBUG-END
		out.print("\n");
		
		out.print("public class " + parserName + " extends " + ModedEngine.class.getName() + "<" + rootType + "," + errorType + ">\n");
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
		symbolTransTable.put(FringeSymbols.EMPTY.getId(),i++);
		TERMINAL_COUNT = i;
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
		GRAMMAR_STRUCTURE_COUNT = i;
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			symbolTransTable.put(group.getName().getId(),i++);
			lexGroupTransTable.put(group.getMembers(),group);
		}
		SYMBOL_COUNT = i;
		PARSER_START_STATENUM = 0;

		sortedStates = new TreeSet<Integer>();
		for(int statenum : builtParseTable.getStates()) sortedStates.add(statenum);
		
		PARSER_STATE_COUNT = sortedStates.last() + 1;

		symbolNames = new String[SYMBOL_COUNT];
		symbolNumbers = new int[SYMBOL_COUNT];
		productionLHSs = new int[GRAMMAR_STRUCTURE_COUNT - GRAMMAR_SYMBOL_COUNT];
        parseTable = new int[PARSER_STATE_COUNT][GRAMMAR_SYMBOL_COUNT];
        shiftableStates = new int[PARSER_STATE_COUNT];
        shiftableSets = new BitSet[PARSER_STATE_COUNT + 1];
        HashSet< HashSet<Terminal> > shiftableJCFSets = new HashSet< HashSet<Terminal> >();
        layoutSets = new int[PARSER_STATE_COUNT];
        HashSet< HashSet<Terminal> > layoutJCFSets = new HashSet< HashSet<Terminal> >();
        prefixSets = new int[PARSER_STATE_COUNT];
        HashSet< HashSet<Terminal> > prefixJCFSets = new HashSet< HashSet<Terminal> >();
		prefixMaps = new int[PARSER_STATE_COUNT][TERMINAL_COUNT];
		HashSet< HashSet<Terminal> > prefixJCFMaps = new HashSet< HashSet<Terminal> >();
		HashSet<Terminal> shiftableJCFUnion = new HashSet<Terminal>();
		meldedSituations = new byte[PARSER_STATE_COUNT];
		cmap = new int[Character.MAX_VALUE];

		for(Terminal t : grammar.getT())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
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
		
		shiftableSets[PARSER_STATE_COUNT] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);

		for(int statenum : sortedStates)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 3,".");
			
			boolean isMelded = true;
			
			shiftableSets[statenum] = SingleDFAEngine.newBitVec(TERMINAL_COUNT);
			
			if(builtParseTable.hasShiftable(statenum))
			{
				for(Terminal t : builtParseTable.getShiftable(statenum))
				{
					shiftableSets[statenum].set(symbolTransTable.get(t.getId()));
					isMelded &= (!builtParseTable.hasLayout(statenum) || !builtParseTable.getLayout(statenum).contains(t));
					isMelded &= (!builtParseTable.hasPrefixes(statenum) || !builtParseTable.getPrefixes(statenum).contains(t));
				}
				HashSet<Terminal> newSet = new HashSet<Terminal>(builtParseTable.getShiftable(statenum));
				shiftableJCFSets.add(newSet);
				for(Terminal t : builtParseTable.getShiftable(statenum))
				{
					Iterable<ParseAction> actions = builtParseTable.getParseActions(statenum,t);
					if(builtParseTable.countParseActions(statenum,t) > 1)
					{
						logger.logParseTableConflict(CompilerLogMessageSort.UNRESOLVED_CONFLICT,false,statenum,t.toString(),actions.toString());
					}
					for(ParseAction action : actions)
					{
						Pair<Integer,String> inf = action.acceptVisitor(this);
						parseTable[statenum][symbolTransTable.get(t.getId())] = inf.first();
						shiftableSets[PARSER_STATE_COUNT].set(symbolTransTable.get(t.getId()));
						shiftableJCFUnion.add(t);
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
			if(builtParseTable.hasLayout(statenum))
			{
				HashSet<Terminal> newSet = new HashSet<Terminal>(builtParseTable.getLayout(statenum));
				for(Terminal t : newSet)
				{
					shiftableSets[PARSER_STATE_COUNT].set(symbolTransTable.get(t.getId()));
					isMelded &= (!builtParseTable.hasPrefixes(statenum) || !builtParseTable.getPrefixes(statenum).contains(newSet));
				}
				if(isMelded) layoutJCFSets.add(newSet);
			}
			if(builtParseTable.hasPrefixes(statenum))
			{
				HashSet<Terminal> newSet = new HashSet<Terminal>(builtParseTable.getPrefixes(statenum));
				if(isMelded) prefixJCFSets.add(newSet);
				for(Terminal t : newSet) shiftableSets[PARSER_STATE_COUNT].set(symbolTransTable.get(t.getId()));
				for(Terminal prefix : builtParseTable.getPrefixes(statenum))
				{
					if(prefix.equals(FringeSymbols.EMPTY)) continue;
					newSet = new HashSet<Terminal>(builtParseTable.getShiftableFollowingPrefix(statenum,prefix));
					prefixJCFMaps.add(newSet);
				}
			}
			if(isMelded) meldedSituations[statenum] |= ModedEngine.SITUATION_IS_MELDED;
		}
		
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n  Scanner code...\n    NFA generation/DFA conversion...\n");
		// DEBUG-X-BEGIN
		//int maxSingleStates = -1;
		//int totalStates = 0;
		// DEBUG-X-END
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"      Unified DFA");

		// DEBUG-X-BEGIN
		//System.err.println(PrettyPrinter.prettyPrint(shiftable,"  ",1) + "\n---------TO----------\n" + PrettyPrinter.prettyPrint(higherPrecShiftable,"  ",1) + ": (" + shiftableB.toString() + ") " + mergedDFA.getStates().size() + " states\n========================================");
		//maxSingleStates = Math.max(mergedDFA.getStates().size(),maxSingleStates);
		//totalStates += shiftableUnionDFA.getStates().size();
		i = 0;
		for(byte x : meldedSituations) if(ModedEngine.isMelded(x)) i++;
		if(i == 0) meldedSituation = ModedEngine.MELDED_NO_STATES;
		else if(i == PARSER_STATE_COUNT) meldedSituation = ModedEngine.MELDED_ALL_STATES;
		else meldedSituation = ModedEngine.MELDED_SOME_STATES;
		//System.err.println(i + " of " + PARSER_STATE_COUNT + " melded");
		// DEBUG-X-END
		
		shiftableUnionDFA = null;
		shiftableUnionMeldedDFA = null;

		if(meldedSituation == ModedEngine.MELDED_NO_STATES)
		{
			shiftableUnionDFA = buildDFA(shiftableJCFUnion);
			compressDFATransitions(shiftableUnionDFA);
			if(grammar != null) disambiguateDFA(shiftableUnionDFA);
			shiftableUnion = 0;
			shiftableUnionMelded = -1;
		}
		else //if(meldedSituation == ModedEngine.MELDED_SOME_STATES)
		{
			shiftableUnionDFA = buildDFA(shiftableJCFUnion);
			compressDFATransitions(shiftableUnionDFA);
			if(grammar != null) disambiguateDFA(shiftableUnionDFA);
			HashSet<Terminal> shiftableJCFUnionMelded = new HashSet<Terminal>(shiftableJCFUnion);
			for(int statenum : sortedStates)
			{
				if(builtParseTable.hasLayout(statenum)) shiftableJCFUnionMelded.addAll(builtParseTable.getLayout(statenum));
				if(builtParseTable.hasPrefixes(statenum)) shiftableJCFUnionMelded.addAll(builtParseTable.getPrefixes(statenum));
			}
			shiftableUnionMeldedDFA = buildDFA(shiftableJCFUnionMelded);
			compressDFATransitions(shiftableUnionMeldedDFA);
			if(grammar != null) disambiguateDFA(shiftableUnionMeldedDFA);
			shiftableUnion = 0;
			shiftableUnionMelded = 0;
		}
		/*else // if(meldedSituation == ModedEngine.MELDED_ALL_STATES)
		{
			HashSet<Terminal> shiftableJCFUnionMelded = new HashSet<Terminal>(shiftableJCFUnion);
			for(int statenum : sortedStates)
			{
				if(builtParseTable.hasLayout(statenum)) shiftableJCFUnionMelded.addAll(builtParseTable.getLayout(statenum));
				if(builtParseTable.hasPrefixes(statenum)) shiftableJCFUnionMelded.addAll(builtParseTable.getPrefixes(statenum));
			}
			shiftableUnionMeldedDFA = buildDFA(shiftableJCFUnionMelded);
			compressDFATransitions(shiftableUnionMeldedDFA);
			if(grammar != null) disambiguateDFA(shiftableUnionMeldedDFA);
			shiftableUnion = -1;
			shiftableUnionMelded = 0;
		}*/
		nextStateNum = 1;
		
		numericalStateMapping = new Hashtable<NFAState,Integer>();
		
		findShiftableUnionAmbiguousStates();
		findKeywords(shiftableJCFUnion);
		findFollowGraph(shiftableJCFUnion);
		// DEBUG-BEGIN
		//System.err.println("Keywords: " + PrettyPrinter.prettyPrint(dfaData.keywords.entrySet(),"  ",1));
		//System.err.println("Follow graph: " + dfaData.followsGraph);
		// DEBUG-END
		

		// DEBUG-BEGIN
		//System.err.println("Shiftable union is unambiguous: " + unionIsUnambiguous);
		// DEBUG-END

		HashSet< HashSet<Terminal> > allShiftables = new HashSet< HashSet<Terminal> >();
		allShiftables.addAll(layoutJCFSets);
		allShiftables.addAll(prefixJCFSets);
		allShiftables.addAll(prefixJCFMaps);
		allDFAs = new Hashtable< HashSet<Terminal>,NFA >();
		
		HashSet<Terminal> removeTemplate = new HashSet<Terminal>();
		allShiftables.remove(removeTemplate);
		removeTemplate.add(FringeSymbols.EOF);
		allShiftables.remove(removeTemplate);
		
		int maxIndividualDFAs = shiftableJCFSets.size() + allShiftables.size();

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n      Up to "  + maxIndividualDFAs + " state-specific DFAs - ");

                dfaData.differences = new Hashtable< Integer,HashSet< HashSet<Terminal> > >();
		i = 1;
		//for(HashSet<Terminal> shiftable : allShiftables)
		for(int statenum : sortedStates)
		{
			boolean unionIsAdequate = true;
			if(builtParseTable.hasShiftable(statenum))
			{
				if(!dfaData.differences.containsKey(statenum)) dfaData.differences.put(statenum,new HashSet< HashSet<Terminal> >());
				unionIsAdequate &= testUnionAdequacyConservative(statenum) || testUnionAdequacyKeyword(builtParseTable.getShiftable(statenum),dfaData.differences.get(statenum));
				if(unionIsAdequate)
				{
					// DEBUG-X-BEGIN
					//System.err.println(shiftable + " " + augmentedShiftable);
					// DEBUG-X-END
					continue;
				}
			}
			NFA mergedDFA = buildDFA(statenum/*shiftable*/);
			if(mergedDFA == null) continue;
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"(" + (i++) + ")");
			//allDFAs.put(shiftable,mergedDFA);
			compressDFATransitions(mergedDFA);
			if(grammar != null) disambiguateDFA(mergedDFA);
			numberDFAStates(mergedDFA);
			// DEBUG-X-BEGIN
			//System.err.println(PrettyPrinter.prettyPrint(shiftable,"  ",1) + "\n---------TO----------\n" + PrettyPrinter.prettyPrint(higherPrecShiftable,"  ",1) + ": (" + shiftableB.toString() + ") " + mergedDFA.getStates().size() + " states\n========================================");
			//maxSingleStates = Math.max(mergedDFA.getStates().size(),maxSingleStates);
			//totalStates += mergedDFA.getStates().size();
			// DEBUG-X-END
		}
		for(HashSet<Terminal> shiftable : allShiftables)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"(" + (i++) + ")");
			NFA mergedDFA = buildDFA(shiftable);
			allDFAs.put(shiftable,mergedDFA);
			compressDFATransitions(mergedDFA);
			if(grammar != null) disambiguateDFA(mergedDFA);
			numberDFAStates(mergedDFA);
			// DEBUG-X-BEGIN
			//System.err.println(PrettyPrinter.prettyPrint(shiftable,"  ",1)/* + "\n---------TO----------\n" + PrettyPrinter.prettyPrint(higherPrecShiftable,"  ",1) + ": (" + shiftableB.toString() + ") "*/ + mergedDFA.getStates().size() + " states\n========================================");
			//maxSingleStates = Math.max(mergedDFA.getStates().size(),maxSingleStates);
			//totalStates += mergedDFA.getStates().size();
			// DEBUG-X-END
		}
		
		// DEBUG-X-BEGIN
		//System.err.println("Maximum states in a single DFA: " + maxSingleStates);
		//System.err.println("Total: " + totalStates);
		// DEBUG-X-END
	
		// DEBUG-X-BEGIN
		//System.err.println("After state-chop: " + nextStateNum + " states");
		// DEBUG-X-END

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n        ... " + (int)(((double)(maxIndividualDFAs - (i-1))/maxIndividualDFAs) * 100) + "% eliminated" + "\n    Accept info");
		

		for(int statenum : sortedStates)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			if(builtParseTable.hasShiftable(statenum))
			{
				if(!(ModedEngine.isMelded(meldedSituations[statenum])) && allDFAs.containsKey(builtParseTable.getShiftable(statenum)))
				{
					shiftableStates[statenum] = numericalStateMapping.get(allDFAs.get(builtParseTable.getShiftable(statenum)).getStartState());
				}
				else if(ModedEngine.isMelded(meldedSituations[statenum]))
				{
					HashSet<Terminal> meldedShiftable = new HashSet<Terminal>();
					meldedShiftable.addAll(builtParseTable.getShiftable(statenum));
					if(builtParseTable.hasLayout(statenum)) meldedShiftable.addAll(builtParseTable.getLayout(statenum));
					if(builtParseTable.hasPrefixes(statenum)) meldedShiftable.addAll(builtParseTable.getPrefixes(statenum));
					if(allDFAs.containsKey(meldedShiftable))
					{
						shiftableStates[statenum] = numericalStateMapping.get(allDFAs.get(meldedShiftable).getStartState());
					}
					else
					{
						shiftableStates[statenum] = shiftableUnionMelded;
					}
				}
				else
				{
					shiftableStates[statenum] = shiftableUnion;
				}
			}
			if(builtParseTable.hasLayout(statenum))
			{
				if(allDFAs.containsKey(builtParseTable.getLayout(statenum)))
				{
					layoutSets[statenum] = numericalStateMapping.get(allDFAs.get(builtParseTable.getLayout(statenum)).getStartState());
					for(Terminal layout : builtParseTable.getLayout(statenum))
					{
						parseTable[statenum][symbolTransTable.get(layout.getId())] = ModedEngine.newAction(ModedEngine.STATE_IGNORELAYOUT,shiftableStates[statenum]);
					}
				}
				else
				{
					layoutSets[statenum] = shiftableUnion;
				}
			}
			if(builtParseTable.hasPrefixes(statenum))
			{
				if(allDFAs.containsKey(builtParseTable.getPrefixes(statenum)))
				{
					prefixSets[statenum] = numericalStateMapping.get(allDFAs.get(builtParseTable.getPrefixes(statenum)).getStartState());
					for(Terminal prefix : builtParseTable.getPrefixes(statenum))
					{
						prefixMaps[statenum][symbolTransTable.get(prefix.getId())] = numericalStateMapping.get(allDFAs.get(builtParseTable.getShiftableFollowingPrefix(statenum,prefix)).getStartState());
						parseTable[statenum][symbolTransTable.get(prefix.getId())] = ModedEngine.newAction(ModedEngine.STATE_IGNORELAYOUT,numericalStateMapping.get(allDFAs.get(builtParseTable.getShiftableFollowingPrefix(statenum,prefix)).getStartState()));
					}
				}
				else
				{
					prefixSets[statenum] = shiftableUnion;
					for(Terminal prefix : builtParseTable.getPrefixes(statenum))
					{
						prefixMaps[statenum][symbolTransTable.get(prefix.getId())] = shiftableUnion;
					}
				}
			}
			
			// DEBUG-X-BEGIN
		    // System.err.println(statenum + ": " + shiftableStates[statenum] + " " + shiftableUnion + " " + shiftableUnionMelded);
			// DEBUG-X-END
		}
		
		encounteredAmbiguities = new Hashtable< HashSet<Symbol>,HashSet< HashSet<Terminal> > >();
		schroedingerAmbiguities = new Hashtable< HashSet<Symbol>,Integer >();
		nextSchroedingerIndex = SYMBOL_COUNT;

		SCANNER_STATE_COUNT = nextStateNum;
		acceptSets = new int[SCANNER_STATE_COUNT];

		populateArrays(shiftableJCFUnion,shiftableUnionDFA,true);
		for(HashSet<Terminal> shiftable : allDFAs.keySet())
		{
			populateArrays(shiftable,allDFAs.get(shiftable),false);
		}
		
		// Report all ambiguities.
		if(logger.isLoggable(CompilerLogMessageSort.ERROR))
		{
			for(HashSet<Symbol> ambiguity : encounteredAmbiguities.keySet())
			{
				HashSet< HashSet<Terminal> > places = encounteredAmbiguities.get(ambiguity);
				String ambigHash = PrettyPrinter.iterablePrettyPrint(ambiguity,"   ",1);
				logger.logMessage(CompilerLogMessageSort.ERROR,null,"Lexical ambiguity in " + places.size() + " shiftable sets " + (ambiguity.size() == 2 ? "between" : "among") + " tokens:\n" + ambigHash);
			}
		}
		
		if(nextSchroedingerIndex > SYMBOL_COUNT)
		{
			String[] newSymbolNames = new String[nextSchroedingerIndex];
			int[] newSymbolNumbers = new int[nextSchroedingerIndex];
			System.arraycopy(symbolNames,0,newSymbolNames,0,SYMBOL_COUNT);
			System.arraycopy(symbolNumbers,0,newSymbolNumbers,0,SYMBOL_COUNT);
			for(HashSet<Symbol> acceptSet : schroedingerAmbiguities.keySet())
			{
				newSymbolNames[schroedingerAmbiguities.get(acceptSet)] = PrettyPrinter.iterablePrettyPrint(acceptSet,"   ",1);
				if(acceptSet.size() != 1) newSymbolNumbers[schroedingerAmbiguities.get(acceptSet)] = ModedEngine.newSymbol(ModedEngine.SYMBOL_DISAMBIG_GROUP_SCHROEDINGER,-1);
				else newSymbolNumbers[schroedingerAmbiguities.get(acceptSet)] = ModedEngine.newSymbol(ModedEngine.SYMBOL_DISAMBIG_GROUP_SCHROEDINGER,symbolTransTable.get(acceptSet.iterator().next()));

				// DEBUG-X-BEGIN
				//System.err.println("Schroedinger group:\n" + newSymbolNames[i]);
				//if(ModedEngine.actionIndex(newSymbolNumbers[i]) == -1) System.err.println("Unresolved");
				//else System.err.println("Resolved in favor of\n" + newSymbolNames[i]);
				// DEBUG-X-END
			}
			symbolNames = newSymbolNames;
			symbolNumbers = newSymbolNumbers;
			SYMBOL_COUNT = nextSchroedingerIndex;
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
		for(NFAState state : numericalStateMapping.keySet())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 2,".");
			for(CharacterRange cr : state.getTransitionSymbols())
			{
				for(char c = cr.firstChar();c <= cr.lastChar();c++)
				{
					if(cmap[c] == 0) cmap[c] = ++i;
				}
			}
		}

		delta = new int[SCANNER_STATE_COUNT][i+1];
		for(NFAState state : numericalStateMapping.keySet())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW * 2,".");
			for(Pair<CharacterRange,NFAState> transition : state)
			{
				for(char c = transition.first().firstChar();c <= transition.first().lastChar();c++)
				{
					int destStateNum = 0;
					if(numericalStateMapping.containsKey(transition.second())) destStateNum = numericalStateMapping.get(transition.second()); 
					delta[numericalStateMapping.get(state)][cmap[c]] = destStateNum;
				}
			}
		}
		
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n  Semantic action framework...\n");
		
		out.print("    public class Semantics extends " + ModedSemanticActionContainer.class.getName() + "<" + errorType + ">\n");
		out.print("    {\n");
		
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
		out.print("            this._prod = _prod;\n");
		out.print("            this._children = _children;\n");
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

		out.print("        public Object runSemanticAction(" + InputPosition.class.getName() + " _pos," + ModedMatchData.class.getName() + " _terminal)\n");
	    out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
		out.print("        {\n");
		out.print("            this._pos = _pos;\n");
		out.print("            this._terminal = _terminal;\n");
		out.print("            this._specialAttributes = new " + SpecialParserAttributes.class.getName() + "(virtualLocation);\n");
		out.print("            @SuppressWarnings(\"unused\") String lexeme = _terminal.lexeme;\n");
		out.print("            Object RESULT = null;\n");
		out.print("            switch(_terminal.term)\n");
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
			out.print("        public void runPostParseCode(" + rootType + " __root)\n");
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

		out.print("        public int runDisambiguationAction(" + InputPosition.class.getName() + " _pos," + ModedMatchData.class.getName() + " match)\n");
	    out.print("        throws " + IOException.class.getName() + "," + errorType + "\n");
	    out.print("        {\n");
		out.print("            @SuppressWarnings(\"unused\") String lexeme = match.lexeme;\n");
		out.print("            switch(match.term)\n");
		out.print("            {\n");
		/*boolean first = true;*/
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			symbolNumbers[symbolTransTable.get(group.getName().getId())] = ModedEngine.newAction(ModedEngine.SYMBOL_DISAMBIG_GROUP_CODE,symbolTransTable.get(group.getName().getId()));
			out.print("            case " + symbolTransTable.get(group.getName().getId()) + ":\n");
			out.print("                return disambiguate_" + symbolTransTable.get(group.getName().getId()) + "(lexeme);\n");
			/*if(!first) out.print("else ");
			else first = false;
			out.print("if(match.terms.equals(disambiguationGroups[" + lexGroupTransTable.get(group) + "])) return disambiguate_" + lexGroupTransTable.get(group) + "(lexeme);\n");*/
		}
		out.print("            default:\n");
		/*if(!first) out.print("else ");*/
		out.print("                return -1;\n");
		out.print("            }\n");
	    out.print("        }\n");
	    
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			out.print("        public int disambiguate_" + symbolTransTable.get(group.getName().getId()) + "(String lexeme)\n");
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
	    out.print("    public Object runSemanticAction(" + InputPosition.class.getName() + " _pos," + ModedMatchData.class.getName() + " _terminal)\n");
	    out.print("    throws " + IOException.class.getName() + "," + errorType + "\n");
	    out.print("    {\n");
	    out.print("        return semantics.runSemanticAction(_pos,_terminal);\n");
	    out.print("    }\n");
		if(!QuotedStringFormatter.isJavaWhitespace(grammar.getParserSources().getPostParseCode()))
		{
		    out.print("    public void runPostParseCode(" + rootType + " __root)\n");
		    out.print("    {\n");
		    out.print("        semantics.runPostParseCode(__root);\n");
		    out.print("    }\n");
		}
		out.print("    public int runDisambiguationAction(" + InputPosition.class.getName() + " _pos," + ModedMatchData.class.getName() + " matches)\n");
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
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			symbolNames[symbolTransTable.get(group.getName().getId())] = PrettyPrinter.iterablePrettyPrint(group.getMembers(),"    ",1);
		}
		for(Terminal t : grammar.getT())
		{
			symbolNumbers[symbolTransTable.get(t.getId())] = ModedEngine.newSymbol(ModedEngine.SYMBOL_TERMINAL,0);
		}
		// If "No EMPTY" does not appear, comment this out.
		symbolNumbers[symbolTransTable.get(FringeSymbols.EMPTY.getId())] = ModedEngine.newSymbol(ModedEngine.SYMBOL_TERMINAL,0);
		for(NonTerminal nt : grammar.getNT())
		{
			symbolNumbers[symbolTransTable.get(nt.getId())] = ModedEngine.newSymbol(ModedEngine.SYMBOL_NONTERM,0);
			if(grammar.pContains(nt))
			{
				for(Production p : grammar.getP(nt))
				{
					symbolNumbers[symbolTransTable.get(p.getName())] = ModedEngine.newSymbol(ModedEngine.SYMBOL_PRODUCTION,p.length());
					productionLHSs[symbolTransTable.get(p.getName()) - GRAMMAR_SYMBOL_COUNT] = ModedEngine.newSymbol(ModedEngine.SYMBOL_NONTERM,symbolTransTable.get(p.getLeft().getId()));
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
		outp.writeObject(parseTable);
		out.println("public static final byte[] parseTableHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(shiftableSets);
		out.println("public static final byte[] shiftableSetsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(shiftableStates);
		out.println("public static final byte[] shiftableStatesHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
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
		outp.writeObject(acceptSets);
		out.println("public static final byte[] acceptSetsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
		stringOut.reset();
		outp = new ObjectOutputStream(stringOut);
		outp.writeObject(meldedSituations);
		out.println("public static final byte[] meldedSituationsHash = " + ByteArrayEncoder.class.getName() + ".literalToByteArray\n(new String[]{ " + ByteArrayEncoder.byteArrayToLiteral(16,stringOut.toByteArray()) + "});\n");
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
		out.print("    symbolNumbers = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(symbolNumbersHash);\n");
		out.print("    productionLHSs = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(productionLHSsHash);\n");
		out.print("    parseTable = (int[][]) " + ByteArrayEncoder.class.getName() + ".readHash(parseTableHash);\n");
		out.print("    shiftableStates = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(shiftableStatesHash);\n");
		out.print("    shiftableSets = (" + BitSet.class.getName() + "[]) " + ByteArrayEncoder.class.getName() + ".readHash(shiftableSetsHash);\n");
		out.print("    layoutSets = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(layoutSetsHash);\n");
		out.print("    prefixSets = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(prefixSetsHash);\n");
		out.print("    prefixMaps = (int[][]) " + ByteArrayEncoder.class.getName() + ".readHash(prefixMapsHash);\n");
		out.print("    meldedSituations = (byte[]) " + ByteArrayEncoder.class.getName() + ".readHash(meldedSituationsHash);\n");
		out.print("    meldedSituation = " + meldedSituation + ";\n");
		out.print("    shiftableUnion = " + shiftableUnion + ";\n");
		out.print("    acceptSets = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(acceptSetsHash);\n");
		out.print("    cmap = (int[]) " + ByteArrayEncoder.class.getName() + ".readHash(cMapHash);\n");
		out.print("    delta = (int[][]) " + ByteArrayEncoder.class.getName() + ".readHash(deltaHash);\n");
		out.print("    }\n");

		out.print("    static\n");
		out.print("    {\n");
		out.print("        TERMINAL_COUNT = " + TERMINAL_COUNT + ";\n");
		out.print("        GRAMMAR_SYMBOL_COUNT = " + GRAMMAR_SYMBOL_COUNT + ";\n");
		out.print("        GRAMMAR_STRUCTURE_COUNT = " + GRAMMAR_STRUCTURE_COUNT + ";\n");
		out.print("        SYMBOL_COUNT = " + SYMBOL_COUNT + ";\n");
		out.print("        PARSER_STATE_COUNT = " + PARSER_STATE_COUNT + ";\n");
		out.print("        SCANNER_STATE_COUNT = " + SCANNER_STATE_COUNT + ";\n");
		out.print("        DISAMBIG_GROUP_COUNT = " + DISAMBIG_GROUP_COUNT + ";\n");
		out.print("        PARSER_START_STATENUM = " + PARSER_START_STATENUM + ";\n");
		out.print("        EOF_SYMNUM = " + EOF_SYMNUM + ";\n");
		out.print("        EPS_SYMNUM = " + EPS_SYMNUM + ";\n");
		out.print("        try { initArrays(); }\n");
		out.print("        catch(" + IOException.class.getName() + " ex) { System.err.println(\"IO Exception\"); }\n");
		out.print("        catch(" + ClassNotFoundException.class.getName() + " ex) { System.err.println(\"Class Not Found Exception\"); }");
		out.print("    }\n");
		out.print("\n");
		out.print(parserAncillaries);
		out.print("\n");
		out.print(scannerAncillaries);
		out.print("\n");
		out.print("}\n");
	}
	
	public Pair<Integer,String> visitAcceptAction(AcceptAction action)
	{
		return Pair.cons(ModedEngine.newAction(ModedEngine.STATE_ACCEPT,0),"accept");
	}

	public Pair<Integer,String> visitFullReduceAction(FullReduceAction action)
	{
		return Pair.cons(ModedEngine.newAction(ModedEngine.STATE_REDUCE,symbolTransTable.get(action.getProd().getName())),"reduce(" + action.getProd() + ")");
	}

	public Pair<Integer,String> visitShiftAction(ShiftAction action)
	{
		return Pair.cons(ModedEngine.newAction(ModedEngine.STATE_SHIFT,action.getDestState()),"shift(" + action.getDestState() + ")");
	}

	
	public int getScannerStateCount()
	{
		return SCANNER_STATE_COUNT;
	}
}
