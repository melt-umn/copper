package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.OperatorAssociativity;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ProductionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.RegexBean;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;

/**
 * Holds a representation of a parser that is (1) flat and non-hierarchical; (2) entirely numeric,
 * with all nomenclature being relegated to a symbol table (see {@link SymbolTable}). 
 *
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class ParserSpec
{
	/**
	 * A wrapper around {@link java.util.BitSet.or} that returns <code>true</code>
	 * if the operation changes the LHS.
	 */
	public static boolean union(BitSet lhs,BitSet rhs)
	{
		int card = lhs.cardinality();
		lhs.or(rhs);
		return lhs.cardinality() > card;
	}

	/**
	 * Holds attributes on terminals.
	 */
	public final class TerminalData
	{
		/** Regexes for each terminal. */
		protected RegexBean[] regexes;
		/** The set of terminal classes to which each terminal belongs. */
		protected BitSet[] terminalClasses;
		/** Each terminal's transparent prefix, or -1 if it has none. */
		protected int[] transparentPrefixes;
		/** The operator class to which each terminal belongs, or -1 for the default. */
		protected int[] operatorClasses;
		/** The operator precedence of each terminal. */
		protected int[] operatorPrecedences;
		/** The operator associativity of each terminal. */
		protected OperatorAssociativity[] operatorAssociativities;
		/** A digraph representing a general precedence relation among the terminals. */ 
		public PrecedenceGraph precedences;
		
		public final RegexBean getRegex(int t) { return regexes[t]; }
		public final BitSet getTerminalClasses(int t) { return terminalClasses[t]; }
		public final int getTransparentPrefix(int t) { return transparentPrefixes[t]; }
		public final int getOperatorClass(int t) { return operatorClasses[t]; }
		public final int getOperatorPrecedence(int t) { return operatorPrecedences[t]; }
		public final OperatorAssociativity getOperatorAssociativity(int t) { return operatorAssociativities[t]; }
		
		public final void setRegex(int t,RegexBean regex) { regexes[t] = regex; }
		public final void setTransparentPrefix(int t,int prefix) { transparentPrefixes[t] = prefix; }
		public final void setOperatorClass(int t,int c) { operatorClasses[t] = c; }
		public final void setOperatorPrecedence(int t,int p) { operatorPrecedences[t] = p; }
		public final void setOperatorAssociativity(int t,OperatorAssociativity a) { operatorAssociativities[t] = a; } 
		
		public TerminalData(int count)
		{
			regexes = new RegexBean[count];
			terminalClasses = new BitSet[count];
			transparentPrefixes = new int[count];
			operatorClasses = new int[count];
			operatorPrecedences = new int[count];
			operatorAssociativities = new OperatorAssociativity[count];
			precedences = new PrecedenceGraph(count);
			
			for(int i = 0;i < count;i++)
			{
				terminalClasses[i] = new BitSet();
			}
		}
	}

	/**
	 * Holds attributes on nonterminals.
	 */
	public final class NonterminalData
	{
		/** For each nonterminal, the set of productions with that nonterminal as their left hand side. */
		protected BitSet[] productions;
		
		public final BitSet getProductions(int nt) { return productions[nt]; }
		
		public NonterminalData(int count)
		{
			productions = new BitSet[count];
			
			for(int i = 0;i < count;i++)
			{
				productions[i] = new BitSet();
			}
		}
	}

	/**
	 * Holds attributes on productions.
	 */
	public final class ProductionData
	{
		/** Each production's left hand side. */
		protected int[] LHSs;
		/** For each production, the count of the symbols on its right hand side. */
		protected int[] RHSLengths;
		/** Each production's right hand side. */
		protected int[][] RHSs;
		/** Each production's designated operator (defaults to the rightmost terminal on the right-hand side). */
		protected int[] operators;
		/** Each production's operator precedence. */
		protected int[] precedences;
		/** Each production's precedence class (note that this is distinct from the precedence of its operator -- see {@link ProductionBean#precedenceClass()}. */
		protected int[] precedenceClasses;
		/** For each production, whether it specifies its own set of layout (which might be the empty set) or uses the grammar-wide set. */ 
		protected boolean[] hasLayout;
		/** For each production, the set of layout symbols that may appear between the symbols on its right-hand side. */ 
		protected BitSet[] layouts;
		
		public final int getLHS(int p) { return LHSs[p]; }
		public final int getRHSLength(int p) { return RHSLengths[p]; }
		public final int getRHSSym(int p,int s) { return RHSs[p][s]; }
		public final int getOperator(int p) { return operators[p]; }
		public final int getPrecedence(int p) { return precedences[p]; }
		public final int getPrecedenceClass(int p) { return precedenceClasses[p]; }
		public final boolean hasLayout(int p) { return hasLayout[p]; }
		public final BitSet getLayouts(int p) { return layouts[p]; }
		
		public final void setLHS(int p,int lhs) { LHSs[p] = lhs; }
		public final void setRHSLength(int p,int length) { RHSLengths[p] = length; }
		public final void setRHSSym(int p,int s,int sym) { RHSs[p][s] = sym; }
		public final void setOperator(int p,int o) { operators[p] = o; }
		public final void setPrecedence(int p,int pr) { precedences[p] = pr; }
		public final void setPrecedenceClass(int p,int pc) { precedenceClasses[p] = pc; }
		public final void setHasLayout(int p,boolean h) { hasLayout[p] = h; }
		
		public ProductionData(int count,int maxRHS)
		{
			LHSs = new int[count];
			RHSLengths = new int[count];
			RHSs = new int[count][maxRHS];
			operators = new int[count];
			precedences = new int[count];
			precedenceClasses = new int[count];
			hasLayout = new boolean[count];
			layouts = new BitSet[count];
			
			for(int i = 0;i < count;i++)
			{
				layouts[i] = new BitSet();
			}			
		}
	}

	/**
	 * Holds attributes on disambiguation functions/groups.
	 *
	 */
	public final class DisambiguationFunctionData
	{
		/** The members of each disambiguation function/group. */
		protected BitSet[] members;
		/** The terminal to which the disambiguation group disambiguates, or -1 for disambiguation functions that disambiguate using code. */
		protected int[] disambiguateTos; // -1 for all but disambiguation groups.
		
		public final BitSet getMembers(int dg) { return members[dg]; }
		public final int getDisambiguateTo(int dg) { return disambiguateTos[dg]; }
		public final boolean hasDisambiguateTo(int dg) { return disambiguateTos[dg] != -1; }
		
		public final void setDisambiguateTo(int dg,int t) { disambiguateTos[dg] = t; }
		
		public DisambiguationFunctionData(int count)
		{
			members = new BitSet[count];
			disambiguateTos = new int[count];
			
			for(int i = 0;i < count;i++)
			{
				members[i] = new BitSet();
			}
		}
	}

	/**
	 * Holds attributes on terminal classes.
	 */
	public final class TerminalClassData
	{
		/** The members of each terminal class. */
		protected BitSet[] members;
		
		public final BitSet getMembers(int tc) { return members[tc]; }

		public TerminalClassData(int count)
		{
			members = new BitSet[count];
			
			for(int i = 0;i < count;i++)
			{
				members[i] = new BitSet();
			}
		}
	}

	/**
	 * Holds attributes on grammars.
	 *
	 */
	public final class GrammarData
	{
		/** Grammar layout for each grammar. */
		protected BitSet[] layouts;

		public final BitSet getLayouts(int tc) { return layouts[tc]; }

		public GrammarData(int count)
		{
			layouts = new BitSet[count];
			
			for(int i = 0;i < count;i++)
			{
				layouts[i] = new BitSet();
			}
		}
	}
	
	/** Holds attributes on parser specs. */
	public final class ParserData
	{
		/** Start layout. */
		protected BitSet layout;
		
		public final BitSet getLayout() { return layout; }
		
		public ParserData(int count)
		{
			layout = new BitSet();
		}
	}
	
	public BitSet terminals;
	public BitSet nonterminals;
	public BitSet productions;
	public BitSet disambiguationFunctions;
	public BitSet terminalClasses;
	public BitSet operatorClasses;
	public BitSet parserAttributes;
	public BitSet grammars;
	public int parser;
	
	/** The grammar in which each grammar element was declared. */
	public int[] owners;
	
	public TerminalData t;
	public NonterminalData nt;
	public ProductionData pr;
	public DisambiguationFunctionData df;
	public TerminalClassData tc;
	public GrammarData g;
	public ParserData p;
	
	/** Returns the number for the special "end-of-file" or "end-of-input" terminal, {@code $}. */
	public int getEOFTerminal() { return terminals.nextSetBit(0); }
	/** Returns the number for the special start nonterminal, {@code ^}. */
	public int getStartNonterminal() { return nonterminals.nextSetBit(0); }
	/** Returns the number for the special start production, {@code ^ ::= S $}. */
	public int getStartProduction() { return productions.nextSetBit(0); }

	/** 
	 * Initializes a spec object of sufficient size to hold all the elements in
	 * {@code symbolTable}. It does not populate the object with these symbols.
	 * <br/><b>N.B.:</b> This only initializes the bit sets for classifying symbols
	 * for each grammar element, not the classes holding attributes on these elements. That
	 * must be done by {@link #initAttributes(SymbolTable)} after the bit sets have been
	 * populated.
	 */
	public ParserSpec(SymbolTable<CopperASTBean> symbolTable)
	{
		int symbolCount = symbolTable.size();
		terminals = new BitSet(symbolCount);
		nonterminals = new BitSet(symbolCount);
		productions = new BitSet(symbolCount);
		disambiguationFunctions = new BitSet(symbolCount);
		terminalClasses = new BitSet(symbolCount);
		operatorClasses = new BitSet(symbolCount);
		parserAttributes = new BitSet(symbolCount);
		grammars = new BitSet(symbolCount);
		owners = new int[symbolCount];
	}
	
	/**
	 * Initializes the classes for holding attributes. <b>N.B.:</b> This depends on the number, so do not call it until
	 */
	public void initAttributes(SymbolTable<CopperASTBean> symbolTable)
	{
		int maxRHS = 2; // The RHS length of the special start production ^ ::= S $.
		for(int i = productions.nextSetBit(productions.nextSetBit(0) + 1);i >= 0;i = productions.nextSetBit(i+1))
		{
			maxRHS = Math.max(maxRHS,((ProductionBean) symbolTable.get(i)).getRhs().size());
		}
		// TODO: Reduce the sizes of the arrays in the inner classes
		//       by allocating only as many elements as are required
		//       for the respective sort of grammar element, and using
		//       offsets in the getter functions. See if this saves
		//       more memory than it costs time.
		t = new TerminalData(terminals.length());
		nt = new NonterminalData(nonterminals.length());
		pr = new ProductionData(productions.length(),maxRHS);
		df = new DisambiguationFunctionData(disambiguationFunctions.length());
		tc = new TerminalClassData(terminalClasses.length());
		g = new GrammarData(grammars.length());
		p = new ParserData(parser + 1);
	}
	
	public String productionToString(SymbolTable<CopperASTBean> symbolTable,int production)
	{
		StringBuffer rv = new StringBuffer();
		rv.append(symbolTable.get(pr.getLHS(production)).getDisplayName()).append(" ::=");
		for(int rhs = 0;rhs < pr.getRHSLength(production);rhs++) rv.append(" ").append(symbolTable.get(pr.getRHSSym(production,rhs)).getDisplayName());
		return rv.toString();
	}
	
	public String toString(SymbolTable<CopperASTBean> symbolTable)
	{
		StringBuffer rv = new StringBuffer();
		rv.append("Terminals:\n");
		for(int i = terminals.nextSetBit(0);i >= 0;i = terminals.nextSetBit(i+1))
		{
			rv.append("    [").append(i).append("] ").append(symbolTable.get(i).getName()).append(" (").append(symbolTable.get(i).getDisplayName()).append(")\n");
		}
		rv.append("Nonterminals:\n");
		for(int i = nonterminals.nextSetBit(0);i >= 0;i = nonterminals.nextSetBit(i+1))
		{
			rv.append("    [").append(i).append("] ").append(symbolTable.get(i).getName()).append(" (").append(symbolTable.get(i).getDisplayName()).append(")\n");
		}
		rv.append("Productions:\n");
		for(int i = productions.nextSetBit(0);i >= 0;i = productions.nextSetBit(i+1))
		{
			rv.append("    [").append(i).append("] ").append(symbolTable.get(pr.getLHS(i)).getDisplayName()).append(" ::=");
			for(int rhs = 0;rhs < pr.getRHSLength(i);rhs++) rv.append(" ").append(symbolTable.get(pr.getRHSSym(i,rhs)).getDisplayName());
			rv.append("\n");
		}
		rv.append("Disambiguation functions:\n");
		for(int i = disambiguationFunctions.nextSetBit(0);i >= 0;i = disambiguationFunctions.nextSetBit(i+1))
		{
			rv.append("    [").append(i).append("] ").append(symbolTable.get(i).getName()).append(" (").append(symbolTable.get(i).getDisplayName()).append(")\n");
		}
		rv.append("Terminal classes:\n");
		for(int i = terminalClasses.nextSetBit(0);i >= 0;i = terminalClasses.nextSetBit(i+1))
		{
			rv.append("    [").append(i).append("] ").append(symbolTable.get(i).getName()).append(" (").append(symbolTable.get(i).getDisplayName()).append(")\n");
		}
		rv.append("Operator classes:\n");
		for(int i = operatorClasses.nextSetBit(0);i >= 0;i = operatorClasses.nextSetBit(i+1))
		{
			rv.append("    [").append(i).append("] ").append(symbolTable.get(i).getName()).append(" (").append(symbolTable.get(i).getDisplayName()).append(")\n");
		}
		rv.append("Parser attributes:\n");
		for(int i = parserAttributes.nextSetBit(0);i >= 0;i = parserAttributes.nextSetBit(i+1))
		{
			rv.append("    [").append(i).append("] ").append(symbolTable.get(i).getName()).append(" (").append(symbolTable.get(i).getDisplayName()).append(")\n");
		}
		rv.append("Grammars:\n");
		for(int i = grammars.nextSetBit(0);i >= 0;i = grammars.nextSetBit(i+1))
		{
			rv.append("    [").append(i).append("] ").append(symbolTable.get(i).getName()).append(" (").append(symbolTable.get(i).getDisplayName()).append(")\n");
		}
		rv.append("Parsers:\n");
		rv.append("    [").append(parser).append("] ").append(symbolTable.get(parser).getName()).append(" (").append(symbolTable.get(parser).getDisplayName()).append(")\n");
		rv.append("Precedence graph:\n");
		rv.append(t.precedences);
		return rv.toString();
	}
}
