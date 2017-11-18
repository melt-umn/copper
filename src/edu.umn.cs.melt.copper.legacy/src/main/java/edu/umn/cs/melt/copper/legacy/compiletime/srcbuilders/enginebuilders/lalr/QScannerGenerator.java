package edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.lalr;

import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LexicalAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.PrecedenceRelationGraph;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.ParsedRegex;
import edu.umn.cs.melt.copper.legacy.compiletime.auxiliary.CharacterRange;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.QScannerStateInfo;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner.QScanner;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner.QScannerMatch;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner.QScannerMatchData;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner.QScannerMatchLongest;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFA;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFA2DFA;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFAState;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.RegexInfo;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.QuotedStringFormatter;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * Generates a QScanner from a set of labeled regexes provided
 * in abstract syntax tree form.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class QScannerGenerator
{
	public static int STATE_BATCH_SIZE = 512;
	
	private Hashtable<Symbol,RegexInfo> regexes;
	private CompilerLogger logger;
	
	/**
	 * Creates a new instance of QScannerGenerator.
	 *
	 */
	public QScannerGenerator(CompilerLogger logger)
	{
		regexes = new Hashtable<Symbol,RegexInfo>();
		this.logger = logger;
	}
	
	/**
	 * Adds a regex if there is not one already present for the given symbol.
	 * @param newSym The label (grammar terminal symbol) with which the regex should be marked.  
	 * @param newRegex The regex, in abstract syntax tree form.
	 * @param attributes The lexical attributes of the new terminal.
	 * @param newSemAction Code for a semantic action, or <CODE>null</CODE> for no semantic action. Code has access to <CODE>yytext</CODE> (match lexeme).
	 * @return <CODE>true</CODE> iff there was not already an entry for <CODE>sym</CODE>.
	 * @see edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner.QScannerMatchMap
	 */
	public boolean addRegex(Symbol newSym,ParsedRegex newRegex,LexicalAttributes attributes,String newSemAction)
	{
		if(regexes.contains(newSym)) return false;
		else
		{
			regexes.put(newSym,
					    new RegexInfo(
					     newRegex,
					     newSemAction));
			return true;
		}
	}

	/**
	 * Compiles the set of regexes into a QScanner that will recognize all of them.
	 * @param grammarData The grammar for which the scanner is being built.
	 * @param out The output stream to which to print the QScanner.
	 * @param packageDecl The package in which the QScanner will be placed.
	 * @param importDecls Necessary import declarations.
	 * @param accessLevel The access level (public/default) of the QScanner.
	 * @param className The class name of the QScanner. 
	 * @param ancillaries User code to be inserted into the class.
	 * @param linePrefix A prefix (tab or whitespace) to be inserted before each line.
	 * @return The generated DFA and numerical state designators.
	 */
	public QScannerStateInfo[] compile(GrammarSource grammarData,
			                           PrintStream out,
			                           String packageDecl,
			                           String importDecls,
			                           String accessLevel,
			                           String className,
			                           String ancillaries,
			                           String linePrefix)
	throws CopperException
	{
		// DEBUG-BEGIN
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"    NFA generation/DFA conversion...\n");
		// DEBUG-END

		// Collect the sub-NFAs for all regexes.
		HashSet<NFA> nfas = new HashSet<NFA>();
		for(Symbol forRegex : regexes.keySet())
		{
			NFA newNFA = regexes.get(forRegex).getRegex().generateAutomaton(forRegex);
			if(logger.isLoggable(CompilerLogMessageSort.DEBUG))
			{
				logger.logMessage(CompilerLogMessageSort.DEBUG,null,"NFA for " + forRegex + ": " + newNFA.toString());
			}
			nfas.add(newNFA);
		}
		// Bind them together with an epsilon-transition from
		// the new start state to the start state of each sub-NFA.
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
		int startState = -1;
		QScannerStateInfo[] stateInfo = new QScannerStateInfo[mergedDFA.getStates().size() + 1];
		for(int i = 0;i < stateInfo.length;i++) stateInfo[i] = new QScannerStateInfo();
		int nextStateNum = 1;
		// For each state in the DFA:
		for(NFAState state : mergedDFA.getStates())
		{
			// Assign it a number.
			numericalMapping.put(state,nextStateNum);
			// If it is the start state, record its number.
			if(state.equals(mergedDFA.getStartState())) startState = nextStateNum;
			// For each regex for which it accepts, add the information
			// to the stateInfo array.
			for(Symbol sym : state.getAccepts())
			{
				stateInfo[nextStateNum].addAcceptingSyms(new Terminal(sym));
			}
			// Compress its transitions into ranges.
			state.compressTransitions();
			nextStateNum++;
		}
		
		// DEBUG-BEGIN
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"    Static lexical disambiguation...\n");
		// DEBUG-END
		
		// For each state in the DFA:
		if(grammarData != null)
		{
			for(NFAState state : mergedDFA.getStates())
			{
				HashSet<Terminal> accF = stateInfo[numericalMapping.get(state)].getAcceptingSyms();
				PrecedenceRelationGraph accFG = grammarData.getPrecedenceRelationsGraph().makeCut(accF);
				HashSet<Terminal> rej = accFG.partitionAcceptSet(logger,"static precedence disambiguator, scanner state " + numericalMapping.get(state));
				for(Terminal t : rej)
				{
					stateInfo[numericalMapping.get(state)].removeAcceptingSyms(t);
					stateInfo[numericalMapping.get(state)].addRejectingSyms(t);
				}
			}
		}

		// DEBUG-BEGIN
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"    Transitive closure...\n");
		// DEBUG-END

		int numStates = mergedDFA.getStates().size() + 1;
		boolean[][] transClosure = new boolean[numStates][numStates];
		// Compute the transitive closure of the DFA's states
		// using the Floyd-Warshall algorithm, as presented in
		// Cormen, Leiserson, Rivest and Stein's
		// "Introduction to Algorithms," Second Edition,
		// section 25.2.
		for(int i = 0;i < numStates;i++) transClosure[i][i] = true;
		for(NFAState i : mergedDFA.getStates())
		{
			for(Pair<CharacterRange,NFAState> jp : i)
			{
					transClosure[numericalMapping.get(i)][numericalMapping.get(jp.second())] = true;
			}
		}
		for(int k = 0;k < numStates;k++)
		{
			for(int i = 0;i < numStates;i++)
			{
				for(int j = 0;j < numStates;j++)
				{
					transClosure[i][j] = transClosure[i][j] || (transClosure[i][k] && transClosure[k][j]);
				}
			}
		}
		// Use the transitive closure to compute possible sets:
		for(int i = 0;i < numStates;i++)
		{
			for(int j = 0;j < numStates;j++)
			{
				// If states i and j be connected by transition:
				if(transClosure[i][j])
				{
					// Union the current possible set of i with
					// the accepting set of j.
					for(Terminal t : stateInfo[j].getAcceptingSyms())
					{
						stateInfo[i].addPossibleSyms(t);
					}
					for(Terminal t : stateInfo[j].getRejectingSyms())
					{
						stateInfo[i].addPossibleSyms(t);
					}
				}
			}
		}

		// Cast the information in the form of a Java source file.
		out.print(linePrefix + packageDecl + "\n");
		out.print(linePrefix + importDecls + "\n");
		out.print(linePrefix + accessLevel + " class " + className + " extends " + QScanner.class.getName() + "\n");
		out.print(linePrefix + "{\n");
		out.print(linePrefix + "    public " + className + "(" + Reader.class.getName() + " reader," + CompilerLogger.class.getName() + " logger)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        super(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".TICK));\n");
		out.print(linePrefix + "        this.buffer = " + ScannerBuffer.class.getName() + ".instantiate(reader);\n");
		out.print(linePrefix + "        this.logger = logger;\n");
		out.print(linePrefix + "        startState = " + startState + ";\n");
		out.print(linePrefix + "    }\n");
		out.print(linePrefix + "\n");
		
		
		out.print(linePrefix + "    /** Create a symbol. */\n");
		out.print(linePrefix + "    protected static " + Symbol.class.getName() + " s(String sym)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        return " + Symbol.class.getName() + ".symbol(sym);\n");
		out.print(linePrefix + "    }\n");

		out.print(linePrefix + "    /** Create a terminal from a symbol. */\n");
		out.print(linePrefix + "    protected static " + Terminal.class.getName() + " t(String sym)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        return new " + Terminal.class.getName() + "(sym);\n");
		out.print(linePrefix + "    }\n");

		out.print(linePrefix + "    /** Setup accepting symbols for a state. */\n");
		out.print(linePrefix + "    protected static void sas(int index," + Terminal.class.getName() + "... aSyms)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        staticStateInfo[index].addAcceptingSyms(aSyms);\n");
		out.print(linePrefix + "    }\n");

		out.print(linePrefix + "    /** Setup possible symbols for a state. */\n");
		out.print(linePrefix + "    protected static void sps(int index," + Terminal.class.getName() + "... pSyms)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        staticStateInfo[index].addPossibleSyms(pSyms);\n");
		out.print(linePrefix + "    }\n");
		
		out.print(linePrefix + "    /** Setup rejecting symbols for a state. */\n");
		out.print(linePrefix + "    protected static void srs(int index," + Terminal.class.getName() + "... rSyms)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        staticStateInfo[index].addRejectingSyms(rSyms);\n");
		out.print(linePrefix + "    }\n");
		

		out.print(linePrefix + "    /** Return maximal-munch match objects. */\n");
		out.print(linePrefix + "    protected static " + QScannerMatchLongest.class.getName() + " newlong(" + Terminal.class.getName() + " t," + InputPosition.class.getName() + " positionPreceding," + InputPosition.class.getName() + " positionFollowing," + ArrayList.class.getName() + "<" + QScannerMatchData.class.getName() + "> layouts)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        return new " + QScannerMatchLongest.class.getName() + "(t,positionPreceding,positionFollowing,layouts);\n");
		out.print(linePrefix + "    }\n");
	
		out.print(linePrefix + "    /** Functions for determining character ranges. */\n");
		out.print(linePrefix + "    protected static boolean cheq(char input,char single)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        return (input == single);\n");
		out.print(linePrefix + "    }\n");
		out.print(linePrefix + "    protected static boolean chin(char input,char min,char max)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        return (input >= min && input <= max);\n");
		out.print(linePrefix + "    }\n");
		
		// DEBUG-BEGIN
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"    Accept/possible info...\n");
		// DEBUG-END

		// DYNCODE-BEGIN
		Hashtable<Symbol,Integer> symbolTransTable = new Hashtable<Symbol,Integer>();
		int regexNum = 1;
		out.print(linePrefix + "    private static " + Terminal.class.getName() + " ");
		for(Symbol regex : regexes.keySet())
		{
			out.print("t_" + regexNum);
			if(regexNum == regexes.keySet().size()) out.print(";\n");
			else out.print(",");
			symbolTransTable.put(regex,regexNum++);
		}
		out.print(";\n");
		for(int i = 0;i < stateInfo.length;i++)
		{
			if(i % STATE_BATCH_SIZE == 0)
			{
				if(i != 0) out.print(linePrefix + "    }\n\n");
				out.print(linePrefix + "    private static void symAdd_" + (i / STATE_BATCH_SIZE) + "()\n");
				out.print(linePrefix + "    {\n");
			}
			if(!stateInfo[i].getAcceptingSyms().isEmpty())
			{
				out.print(linePrefix + "        sas(" + i);
				for(Terminal sym : stateInfo[i].getAcceptingSyms())
				{
					out.print(",t_" + symbolTransTable.get(sym.getId()));
				}
				out.print(");\n");
			}
			if(!stateInfo[i].getPossibleSyms().isEmpty())
			{
				out.print(linePrefix + "        sps(" + i);
				for(Terminal sym : stateInfo[i].getPossibleSyms())
				{
					out.print(",t_" + symbolTransTable.get(sym.getId()));
				}
				out.print(");\n");
			}
			if(!stateInfo[i].getRejectingSyms().isEmpty())
			{
				out.print(linePrefix + "        srs(" + i);
				for(Terminal sym : stateInfo[i].getRejectingSyms())
				{
					out.print(",t_" + symbolTransTable.get(sym.getId()));
				}
				out.print(");\n");
			}
		}
		out.print(linePrefix + "    }\n\n");
		out.print(linePrefix + "    private static " + QScannerStateInfo.class.getName() + "[] staticStateInfo;\n");
		out.print(linePrefix + "    static\n");
		out.print(linePrefix + "    {\n");
		for(Symbol regex : regexes.keySet())
		{
			out.print(linePrefix + "        t_" + symbolTransTable.get(regex) + " = t(\"" + regex.toString() + "\");\n");
		}
		out.print(linePrefix + "        staticStateInfo = new "  + QScannerStateInfo.class.getName() + "[" + (numericalMapping.size() + 1) + "];\n");
		out.print(linePrefix + "        for(int i = 0;i < " + (numericalMapping.size() + 1) + ";i++) staticStateInfo[i] = new " + QScannerStateInfo.class.getName() + "();\n");
		for(int i = 0;i * STATE_BATCH_SIZE < stateInfo.length;i++)
		{
			out.print(linePrefix + "        symAdd_" + i + "();\n");
		}
		out.print(linePrefix + "    }\n");
		// DYNCODE-END
		
		
		// DEBUG-BEGIN
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"    Transition table...\n");
		// DEBUG-END
		
		
		// DYNCODE-BEGIN
		out.print(linePrefix + "    protected int transition(int state,char ch)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        switch(state)\n");
		out.print(linePrefix + "        {\n");
		for(NFAState state : mergedDFA.getStates())
		{
			out.print(linePrefix + "            case " + numericalMapping.get(state) + ":\n");
			out.print(linePrefix + "                return tr_" + numericalMapping.get(state) + "(ch);\n");
		}
		out.print(linePrefix + "        default: return 0;\n");
		out.print(linePrefix + "        }\n");
		out.print(linePrefix + "    }\n");
		for(NFAState state : mergedDFA.getStates())
		{
			out.print(linePrefix + "    private int tr_" + numericalMapping.get(state) + "(char ch)\n");
			out.print(linePrefix + "    {\n");
			for(Pair<CharacterRange,NFAState> transition : state)
			{
				if(transition.first().isSingleChar()) out.print(linePrefix + "        if(cheq(ch,'" + QuotedStringFormatter.quoteChar(transition.first().charValue()) + "')) ");
				else out.print(linePrefix + "        if(chin(ch,'" + QuotedStringFormatter.quoteChar(transition.first().firstChar()) + "','" + QuotedStringFormatter.quoteChar(transition.first().lastChar()) + "')) ");
				out.print("return " + numericalMapping.get(transition.second()) + ";\n");
			}
			out.print(linePrefix + "        return 0;\n");
			out.print(linePrefix + "    }\n");
		}
		// DYNCODE-END
		// DYNCODE-BEGIN
		out.print(linePrefix + "    protected " + QScannerMatch.class.getName() + " getMatch(" + Terminal.class.getName() + " t," + InputPosition.class.getName() + " pp," + InputPosition.class.getName() + " pf," + ArrayList.class.getName() + "<" + QScannerMatchData.class.getName() + "> l)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        return newlong(t,pp,pf,l);\n");
		out.print(linePrefix + "    }\n");
		// DYNCODE-END
		out.print(linePrefix + "    protected " + QScannerStateInfo.class.getName() + " getStateInfo(int state)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        return staticStateInfo[state];\n");
		out.print(linePrefix + "    }\n");
		out.print(linePrefix + "    protected String runSemanticAction(" + Terminal.class.getName() + " token)\n");
		out.print(linePrefix + "    {\n");
		out.print(linePrefix + "        String yytext = token.getLexeme();\n");
		out.print(linePrefix + "        if(yytext == null) return \"\";\n");
		for(Symbol regex : regexes.keySet())
		{
			String semAction = regexes.get(regex).getSemanticAction();
			if(semAction == null) continue;
			out.print(linePrefix + "        else if(token.getId().equals(" + Symbol.class.getName() + ".symbol(\"" + regex + "\")))\n");
			out.print(linePrefix + "        {\n");
			out.print(linePrefix + semAction + "\n");
			out.print(linePrefix + "        }\n");
		}
		out.print(linePrefix + "        return yytext;\n");
		out.print(linePrefix + "    }\n");
		out.print(linePrefix + "\n");
		out.print(ancillaries);
		out.print(linePrefix + "\n");
		out.print(linePrefix + "}\n");
		
		return stateInfo;
	}
}
