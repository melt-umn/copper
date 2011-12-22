package edu.umn.cs.melt.copper.compiletime.srcbuilders.parsetablebuilders;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.lalr1.LALR1DFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.lalr1.LALR1StateItem;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.parsetable.AcceptAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.FullReduceAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.GLRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.ParseAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.ParseActionVisitor;
import edu.umn.cs.melt.copper.compiletime.parsetable.ReadOnlyParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.ShiftAction;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class GLRParseTableBuilder extends ParseTableBuilder implements ParseActionVisitor<String,CopperException>
{
	private GrammarSource grammar;
	private LALR1DFA dfa;
	private GLRParseTable parseTable;
	private CompilerLogger logger;
	private Hashtable<GrammarSymbol,Integer> symbolTransTable;
	private Hashtable<Production,Integer> productionTransTable;
	
	public GLRParseTableBuilder(GrammarSource grammar,
			                    LALR1DFA dfa,
			                    GLRParseTable parseTable,
			                    CompilerLogger logger,
			                    Hashtable<GrammarSymbol,Integer> symbolTransTable,
			                    Hashtable<Production,Integer> productionTransTable)
	{
		this.grammar = grammar;
		this.dfa = dfa;
		this.parseTable = parseTable;
		this.logger = logger;
		this.symbolTransTable = symbolTransTable;
		this.productionTransTable = productionTransTable;
	}


	public void outputInitFunctions(PrintStream out)
	throws CopperException
	{
		out.print("    private static " + GLRParseTable.class.getName() + " parseTable;\n\n");

		out.print("    public " + ReadOnlyParseTable.class.getName() + " getParseTable()\n");
		out.print("    {\n");
		out.print("        return parseTable;\n");
		out.print("    }\n");

		out.print("    /** Create a set of terminals. */\n");
		out.print("    protected static " + HashSet.class.getName() + "<" + Terminal.class.getName() + "> tset(" + Terminal.class.getName() + "... ts)\n");
		out.print("    {\n");
		out.print("        " + HashSet.class.getName() + "<" + Terminal.class.getName() + "> rv = new " + HashSet.class.getName() + "<" + Terminal.class.getName() + ">();\n");
		out.print("        for(" + Terminal.class.getName() + " t : ts) rv.add(t);\n");
		out.print("        return rv;\n");
		out.print("    }\n");
		
		out.print("    /** Add a parse action. */\n");
		out.print("    protected static void addA(int statenum," + Terminal.class.getName() + " sym," + ParseAction.class.getName() + " action)\n");
		out.print("    {\n");
		out.print("        parseTable.addAction(statenum,sym,action);\n");
		out.print("    }\n");
		
		out.print("    /** Add a goto action. */\n");
		out.print("    protected static void addG(int statenum," + NonTerminal.class.getName() + " sym," + ShiftAction.class.getName() + " action)\n");
		out.print("    {\n");
		out.print("        parseTable.addGotoAction(statenum,sym,action);\n");
		out.print("    }\n");
		
		out.print("    /** Add layout. */\n");
		out.print("    protected static void addL(int statenum," + Terminal.class.getName() + " layout," + Terminal.class.getName() + "... tokensFollowing)\n");
		out.print("    {\n");
		out.print("        for(" + Terminal.class.getName() + " t : tokensFollowing) parseTable.addLayout(statenum,layout,t);\n");
		out.print("    }\n");
		
		out.print("    /** Add transparent prefixes. */\n");
		out.print("    protected static void addTP(int statenum," + Terminal.class.getName() + " prefix," + Terminal.class.getName() + "... tokensFollowing)\n");
		out.print("    {\n");
		out.print("        for(" + Terminal.class.getName() + " t : tokensFollowing) parseTable.addPrefix(statenum,prefix,t);\n");
		out.print("    }\n");

		TreeSet<Integer> sortedStates = new TreeSet<Integer>();
		for(int statenum : parseTable.getStates()) sortedStates.add(statenum);
		for(int statenum : sortedStates)
		{
			out.print("    /*\n");
			for(LALR1StateItem item : dfa.getState(statenum).getItems())
			{
				out.print("        " + item);
				if(item.isReducible()) out.print("\t" + dfa.getLookahead(dfa.getState(statenum),item));
				out.print("\n");
			}
			out.print("\n    */\n");
			out.print("    public static void init_" + statenum + "()\n");
			out.print("    {\n");
			
			if(parseTable.hasShiftable(statenum))
			{
				for(Terminal t : parseTable.getShiftable(statenum))
				{
					int j = 0;
					Iterable<ParseAction> actions = parseTable.getParseActions(statenum,t);
					for(ParseAction action : actions)
					{
						if(j > 0)
						{
							if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logMessage(CompilerLogMessageSort.ERROR,null,"Parse table conflict in cell (" + statenum + "," + t + ") between/among actions " + actions);
						}
						out.print("        addA(" + statenum + ",sym_" + symbolTransTable.get(t) + "," + action.acceptVisitor(this) + ");\n");
						j++;
					}
				}
			}
			if(parseTable.hasGotoable(statenum))
			{
				for(NonTerminal nt : parseTable.getGotoable(statenum))
				{
					ShiftAction action = parseTable.getGotoAction(statenum,nt);
					out.print("        addG(" + statenum + ",sym_" + symbolTransTable.get(nt) + "," + action.acceptVisitor(this) + ");\n");
				}
			}
			if(parseTable.hasLayout(statenum))
			{
				for(Terminal layout : parseTable.getLayout(statenum))
				{
					out.print("        addL(" + statenum + ",");
					if(layout.equals(FringeSymbols.EMPTY)) out.print("eps()");
					else out.print("sym_" + symbolTransTable.get(layout));
					for(Terminal t : parseTable.getShiftableFollowingLayout(statenum,layout))
					{
						out.print(",sym_" + symbolTransTable.get(t));
					}
					out.print(");\n");
				}
			}
			if(parseTable.hasPrefixes(statenum))
			{
				for(Terminal prefix : parseTable.getPrefixes(statenum))
				{
					if(prefix.equals(FringeSymbols.EMPTY)) continue;
					out.print("        addTP(" + statenum + ",");
					out.print("sym_" + symbolTransTable.get(prefix));
					for(Terminal t : parseTable.getShiftableFollowingPrefix(statenum,prefix))
					{
						out.print(",sym_" + symbolTransTable.get(t));
					}
					out.print(");\n");
				}
			}

			out.print("    }\n");
		}
	}
	
	public void outputInitStatements(PrintStream out)
	throws CopperException
	{
		for(Terminal t : grammar.getT())
		{
			out.print("        sym_" + symbolTransTable.get(t) + " = t(\"" + t.getId() + "\");\n");
		}
		for(NonTerminal nt : grammar.getNT())
		{
			out.print("        sym_" + symbolTransTable.get(nt) + " = nt(\"" + nt.getId() + "\");\n");
		}
		for(NonTerminal nt : grammar.getNT())
		{
			if(!grammar.pContains(nt)) continue;
			for(Production p : grammar.getP(nt))
			{
				out.print("        p_" + productionTransTable.get(p) + " = p(\"" + p.getName() + "\",sym_" + symbolTransTable.get(p.getLeft())); 
				for(GrammarSymbol sym : p.getRight())
				{
					out.print(",sym_" + symbolTransTable.get(sym));
				}
				out.print(");\n");
			}
		}
		
		
		
		out.print("        parseTable = new " + GLRParseTable.class.getName() + "();\n");
		for(int statenum : parseTable.getStates())
		{
			out.print("        init_" + statenum + "();\n");
		}
	}

	public String visitAcceptAction(AcceptAction action)
	{
		return "a()";
	}

	public String visitFullReduceAction(FullReduceAction action)
	{
		return "fr(p_" + productionTransTable.get(action.getProd()) + ")";
	}

	public String visitShiftAction(ShiftAction action)
	{
		return "sh(" + action.getDestState() + ")";
	}
}
