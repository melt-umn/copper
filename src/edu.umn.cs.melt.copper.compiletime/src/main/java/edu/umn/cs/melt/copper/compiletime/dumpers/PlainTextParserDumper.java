package edu.umn.cs.melt.copper.compiletime.dumpers;

import java.io.IOException;
import java.io.PrintStream;
import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0ItemSet;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import edu.umn.cs.melt.copper.main.CopperDumpType;

public class PlainTextParserDumper extends FullParserDumper
{
	private int textWidth;
	
	public PlainTextParserDumper(int textWidth, PSSymbolTable symbolTable, ParserSpec spec,
			LR0DFA dfa, LRLookaheadAndLayoutSets lookahead,
			LRParseTable parseTable, TransparentPrefixes prefixes)
	{
		super(symbolTable, spec, dfa, lookahead, parseTable, prefixes);
		this.textWidth = textWidth;
	}

	@Override
	public void dump(CopperDumpType type, PrintStream out)
	throws IOException,	UnsupportedOperationException
	{
		if(type != CopperDumpType.PLAIN) throw new UnsupportedOperationException(getClass().getName() + " only supports dump type " + CopperDumpType.PLAIN);
		
		int charIndex = 0;
		out.print("===== TERMINALS =====\n");
		for(int t = spec.terminals.nextSetBit(0);t >= 0;t = spec.terminals.nextSetBit(t+1))
		{
			String term = generateName(symbolTable.get(t));
			String termS = "[" + t + "]" + term;
			if(symbolTable.get(t).hasDisplayName()) termS += " (" + symbolTable.get(t).getDisplayName() + ")";
			if(charIndex + termS.length() + 2 > textWidth && charIndex > 0)
			{
				out.print("\n");
				charIndex = 0;
			}
			out.print("  " + termS);
			charIndex += (termS.length() + 2);
		}
		out.println();
		charIndex = 0;
		out.print("===== NONTERMINALS =====\n");
		for(int nt = spec.nonterminals.nextSetBit(0);nt >= 0;nt = spec.nonterminals.nextSetBit(nt+1))
		{
			String nonterm = generateName(symbolTable.get(nt));
			String nontermS = "[" + nt + "]" + nonterm;
			if(symbolTable.get(nt).hasDisplayName()) nontermS += " (" + symbolTable.get(nt).getDisplayName() + ")";
			if(charIndex + nontermS.length() + 2 > textWidth && charIndex > 0)
			{
				out.print("\n");
				charIndex = 0;
			}
			out.print("  " + nontermS);
			charIndex += (nontermS.length() + 2);
		}
		out.println();
		charIndex = 0;
		out.print("===== PRODUCTIONS =====\n");
		for(int p = spec.productions.nextSetBit(0);p >= 0;p = spec.productions.nextSetBit(p+1))
		{
			String productionS = "[" + p + "]" + spec.productionToString(symbolTable,p);
			if(charIndex + productionS.length() + 2 > textWidth && charIndex > 0)
			{
				out.print("\n");
				charIndex = 0;
			}
			out.print("  " + productionS);
			charIndex += (productionS.length() + 2);
		}
		out.println();
		out.print("===== LEXICAL PRECEDENCE GRAPH =====\n");
		out.print(spec.t.precedences.toEquivalenceClassDot("PrecedenceGraph"));
		out.print("===== DISAMBIGUATION FUNCTIONS/GROUPS =====\n");
		for(int dg = spec.disambiguationFunctions.nextSetBit(0);dg >= 0;dg = spec.disambiguationFunctions.nextSetBit(dg+1))
		{
			out.print("[" + dg + "] " + symbolTable.getDisambiguationFunction(dg).getDisplayName() + " : " + spec.df.getMembers(dg));
			if(spec.df.hasDisambiguateTo(dg)) out.print(" -> " + spec.df.getDisambiguateTo(dg));
			out.print("\n");
		}
		out.print("===== LALR(1) DFA =====\n");
		
		for(int statenum = 1;statenum < dfa.size();statenum++)
		{
			LR0ItemSet state = dfa.getItemSet(statenum);
			out.print("-------------------\nlalr_state [" + statenum + "]: {\n");
			for(int item = 0;item < state.size();item++)
			{
				int production = state.getProduction(item);
				out.print("    [");
				out.print(symbolTable.get(spec.pr.getLHS(production)).getDisplayName());
				out.print(" ::=");
				for(int rhs = 0;rhs < spec.pr.getRHSLength(production);rhs++)
				{
					if(rhs == state.getPosition(item)) out.print(" (*)");
					out.print(" " + symbolTable.get(spec.pr.getRHSSym(production,rhs)).getDisplayName());
				}
				out.print(", {");
				boolean first = true;
				for(int k = lookahead.getLookahead(statenum,item).nextSetBit(0);k >= 0;k = lookahead.getLookahead(statenum,item).nextSetBit(k+1))
				{
					if(first) first = false;
					else out.print(",");
					out.print(symbolTable.get(k).getDisplayName());
				}
				out.print("}]\n");
			}
			out.print("}\n");
			for(int X = dfa.getTransitionLabels(statenum).nextSetBit(0);X >= 0;X = dfa.getTransitionLabels(statenum).nextSetBit(X+1))
			{
				out.print("transition on " + symbolTable.get(X).getDisplayName() + " to state " + dfa.getTransition(statenum,X) + "\n");
			}
		}
		out.print("===== PARSE TABLE =====\n");
		
		for(int statenum = 1;statenum < parseTable.size();statenum++)
		{
			// TODO Remove nonterminals from the 'validLA' sets in the parse table.
			BitSet shiftable = new BitSet(parseTable.getValidLA(statenum).length());
			shiftable.or(parseTable.getValidLA(statenum));
			shiftable.andNot(spec.nonterminals);
			
			if(shiftable.isEmpty()) continue;
			
			out.print("From state #" + statenum + "\n");
			
			for(int layout = lookahead.getLayout(statenum).nextSetBit(0);layout >= 0;layout = lookahead.getLayout(statenum).nextSetBit(layout+1))
			{
				out.print(" [layout term " + layout + "]\n");
			}

			for(int prefix = prefixes.getPrefixes(statenum).nextSetBit(0);prefix >= 0;prefix = prefixes.getPrefixes(statenum).nextSetBit(prefix+1))
			{
				out.print(" [prefix term " + prefix + " -> terms " + prefixes.getFollowingTerminals(statenum, prefix) + "]\n");
			}
			
			for(int t = shiftable.nextSetBit(0);t >= 0;t = shiftable.nextSetBit(t+1))
			{
				byte actionType = parseTable.getActionType(statenum,t);
				if(actionType == LRParseTable.SHIFT || actionType == LRParseTable.REDUCE) out.println(parseActionToString(t,actionType,parseTable.getActionParameter(statenum,t)));
				else if(actionType == LRParseTable.CONFLICT)
				{
					LRParseTableConflict conflict = parseTable.getConflict(parseTable.getActionParameter(statenum,t));
					if(conflict.shift != -1) out.println(parseActionToString(t,LRParseTable.SHIFT,conflict.shift));
					for(int r = conflict.reduce.nextSetBit(0);r >= 0;r = conflict.reduce.nextSetBit(r+1))
					{
						out.println(parseActionToString(t,LRParseTable.REDUCE,r));
					}
				}
			}
		}
		
		out.print("===== GOTO TABLE =====\n");
		
		for(int statenum = 1;statenum < parseTable.size();statenum++)
		{
			// TODO Remove nonterminals from the 'validLA' sets in the parse table.
			BitSet gotoable = new BitSet(parseTable.getValidLA(statenum).length());
			gotoable.or(parseTable.getValidLA(statenum));
			gotoable.andNot(spec.terminals);

			if(gotoable.isEmpty()) continue;

			out.print("From state #" + statenum + "\n");

			for(int nt = gotoable.nextSetBit(0);nt >= 0;nt = gotoable.nextSetBit(nt+1))
			{
				out.println(" [nonterm " + nt + "->state " + parseTable.getActionParameter(statenum, nt) + "]");
			}
		}
	}

	private String generateName(CopperASTBean bean)
	{
		if(symbolTable.getParser(spec.parser).isUnitary() || !spec.grammars.get(spec.owners[symbolTable.get(bean)])) return bean.getName().toString();
		else return symbolTable.getGrammar(spec.owners[symbolTable.get(bean)]).getName() + "$" + bean.getName();
	}
	
	private String parseActionToString(int t,byte type,int parameter)
	{
		if(type == LRParseTable.ACCEPT && t == spec.getEOFTerminal())
		{
			return " [term " + t + ":ACCEPT]";
		}
		else if(type == LRParseTable.SHIFT)
		{
			return " [term " + t + ":SHIFT(to state " + parameter + ")]";
		}
		else /*if(type == LRParseTable.REDUCE) */
		{
			return " [term " + t + ":REDUCE(with prod " + parameter + ")]";
		}
	}
}
