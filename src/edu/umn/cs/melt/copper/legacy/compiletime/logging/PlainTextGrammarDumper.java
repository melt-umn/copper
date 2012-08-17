package edu.umn.cs.melt.copper.legacy.compiletime.logging;

import java.io.PrintStream;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LexicalDisambiguationGroup;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.lalr1.LALR1DFA;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.lalr1.LALR1State;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.lalr1.LALR1StateItem;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.lalr1.LALR1Transition;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.AcceptAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.FullReduceAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ParseAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ParseActionVisitor;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ReadOnlyParseTable;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ShiftAction;

public class PlainTextGrammarDumper extends GrammarDumper implements ParseActionVisitor<String,RuntimeException>
{
	private PrintStream finalOutputStream;
	private GrammarSource grammar;
	private LALR1DFA dfa;
	private ReadOnlyParseTable parseTable;
	
	private Hashtable<Object,Integer> numbering;
	
	private static final int TERMINALS_PER_LINE = 5;
	private static final int NONTERMINALS_PER_LINE = 5;
	private static final int PRODUCTIONS_PER_LINE = 5;
	private static final int ACTIONS_PER_LINE = 2;
	private static final int GOTO_ACTIONS_PER_LINE = 3;
	
	public PlainTextGrammarDumper(PrintStream finalOutputStream,
			                      GrammarSource grammar,
			                      LALR1DFA dfa,
			                      ReadOnlyParseTable parseTable)
	{
		super();
		this.finalOutputStream = finalOutputStream;
		this.grammar = grammar;
		this.dfa = dfa;
		this.parseTable = parseTable;
		
		numbering = new Hashtable<Object,Integer>();
	}
	
	public void logPlain() {}

	@Override
	public void dumpDisambigGroups()
	{
		int i = 0;
		finalOutputStream.print("===== DISAMBIGUATION FUNCTIONS/GROUPS =====\n");
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			finalOutputStream.print("[" + i + "]{" + group.getName() + " : " + group.getMembers().toString() + "}\n");
			numbering.put(group,i++);
		}
	}

	@Override
	public void dumpNonTerminals()
	{
		int i = 0;
		finalOutputStream.print("===== NONTERMINALS =====\n");
		for(NonTerminal nt : grammar.getNT())
		{
			finalOutputStream.print("[" + i + "]" + nt + " ");
			if(!grammar.getDisplayName(nt.getId()).equals(nt.getId().toString())) finalOutputStream.print("(" + grammar.getDisplayName(nt.getId()) + ") ");
			numbering.put(nt,i++);
			if(i % NONTERMINALS_PER_LINE == 0) finalOutputStream.print("\n");
		}
		if(i % NONTERMINALS_PER_LINE != 0) finalOutputStream.print("\n");
	}

	@Override
	public void dumpLALR1DFA()
	{
		finalOutputStream.print("===== LALR(1) DFA =====\n");
		
		for(int statenum = 0;statenum <= parseTable.getLastState();statenum++)
		{
			LALR1State state = dfa.getState(statenum);
			finalOutputStream.print("-------------------\nlalr_state [" + statenum + "]: {\n");
			for(LALR1StateItem item : state.getItems())
			{
				finalOutputStream.print("    [" + item + ", {");
				for(Terminal lookahead : dfa.getLookahead(state,item))
				{
					finalOutputStream.print(lookahead.toString() + " ");
				}
				finalOutputStream.print("}]\n");
			}
			finalOutputStream.print("}\n");
			for(LALR1Transition transition : dfa.getTransitions(state))
			{
				finalOutputStream.print("transition on " + transition.getLabel() + " to state " + dfa.getLabel(transition.getDest()) + "\n");
			}
		}
		
	}
	
	@Override
	public void dumpParseTable()
	{
		finalOutputStream.print("===== PARSE TABLE =====\n");
		
		for(int statenum = 0;statenum <= parseTable.getLastState();statenum++)
		{
			finalOutputStream.print("From state #" + statenum + "\n");
			int i = 0;
			
			if(!parseTable.hasShiftable(statenum)) continue;
			
			if(parseTable.hasLayout(statenum))
			{
				for(Terminal layout : parseTable.getLayout(statenum))
				{
					String terms = "";
					for(Terminal t : parseTable.getShiftableFollowingLayout(statenum,layout))
					{
						if(!terms.equals("")) terms += ",";
						terms += numbering.get(t);
					}
					finalOutputStream.print(" [layout term " + numbering.get(layout) + " -> terms " + terms + "]\n");
				}
			}

			if(parseTable.hasPrefixes(statenum))
			{
				for(Terminal prefix : parseTable.getPrefixes(statenum))
				{
					String terms = "";
					for(Terminal t : parseTable.getShiftableFollowingPrefix(statenum,prefix))
					{
						if(!terms.equals("")) terms += ",";
						terms += numbering.get(t);
					}
					finalOutputStream.print(" [prefix term " + numbering.get(prefix) + " -> terms " + terms + "]\n");
				}
			}
			
			for(Terminal sym : parseTable.getShiftable(statenum))
			{
				for(ParseAction action : parseTable.getParseActions(statenum,sym))
				{
					finalOutputStream.print(" [term " + numbering.get(sym) + ":" + action.acceptVisitor(this) + "]");
					if(++i % ACTIONS_PER_LINE == 0) finalOutputStream.print("\n");
				}
			}
			if(i % ACTIONS_PER_LINE != 0) finalOutputStream.print("\n");
		}
		
		finalOutputStream.print("===== GOTO TABLE =====\n");
		
		for(int statenum = 0;statenum <= parseTable.getLastState();statenum++)
		{
			finalOutputStream.print("From state #" + statenum + "\n");

			int i = 0;
			
			if(!parseTable.hasGotoable(statenum)) continue;

			for(NonTerminal sym : parseTable.getGotoable(statenum))
			{
				ShiftAction action = parseTable.getGotoAction(statenum,sym);
				finalOutputStream.print(" [nonterm " + numbering.get(sym) + "->state " + action.getDestState() + "]");
				if(++i % GOTO_ACTIONS_PER_LINE == 0) finalOutputStream.print("\n");
			}
			if(i % GOTO_ACTIONS_PER_LINE != 0) finalOutputStream.print("\n");
		}
	}

	@Override
	public void dumpPrecedenceGraph()
	{
		finalOutputStream.print("===== LEXICAL PRECEDENCE GRAPH =====\n");
		finalOutputStream.print(grammar.getPrecedenceRelationsGraph().toString());
	}

	@Override
	public void dumpProductions()
	{
		int i = 0;
		finalOutputStream.print("===== PRODUCTIONS =====\n");
		for(NonTerminal nt : grammar.getNT())
		{
			if(!grammar.pContains(nt)) continue;
			for(Production p : grammar.getP(nt))
			{
				finalOutputStream.print("[" + i + "]" + p + " ");
				numbering.put(p,i++);
				if(i % PRODUCTIONS_PER_LINE == 0) finalOutputStream.print("\n");
			}
		}
		if(i % PRODUCTIONS_PER_LINE != 0) finalOutputStream.print("\n");
	}

	@Override
	public void dumpTerminals()
	{
		int i = 0;
		finalOutputStream.print("===== TERMINALS =====\n");
		for(Terminal t : grammar.getT())
		{
			finalOutputStream.print("[" + i + "]" + t + " ");
			if(!grammar.getDisplayName(t.getId()).equals(t.getId().toString())) finalOutputStream.print("(" + grammar.getDisplayName(t.getId()) + ") ");
			numbering.put(t,i++);
			if(i % TERMINALS_PER_LINE == 0) finalOutputStream.print("\n");
		}
		if(i % TERMINALS_PER_LINE != 0) finalOutputStream.print("\n");
	}

	public String visitAcceptAction(AcceptAction action)
	throws RuntimeException
	{
		return "ACCEPT";
	}

	public String visitFullReduceAction(FullReduceAction action)
	throws RuntimeException
	{
		return "REDUCE(with prod " + numbering.get(action.getProd()) + ")";
	}

	public String visitShiftAction(ShiftAction action)
	throws RuntimeException
	{
		return "SHIFT(to state " + action.getDestState() + ")";
	}

	@Override
	public void dumpPreamble() { /* No preamble required in plaintext */ }
	@Override
	public void dumpPostamble() { /* No postamble required in plaintext */ }
}
