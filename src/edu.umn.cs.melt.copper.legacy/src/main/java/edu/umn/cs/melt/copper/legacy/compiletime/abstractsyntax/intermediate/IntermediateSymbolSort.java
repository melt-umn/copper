package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate;

import java.util.Hashtable;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;

public enum IntermediateSymbolSort
{
	TERMINAL 			 { public String toString() { return "terminal"; } },
	NON_TERMINAL 		 { public String toString() { return "nonterminal"; } },
	PRODUCTION			 { public String toString() { return "production"; } },
	GRAMMAR_NAME		 { public String toString() { return "grammar name"; } },
	TERMINAL_CLASS		 { public String toString() { return "terminal class"; } },
	DISAMBIGUATION_GROUP { public String toString() { return "disambiguation group"; } },
	PARSER_ATTRIBUTE	 { public String toString() { return "parser attribute"; } },
	DIRECTIVE            { public String toString() { return "directive"; } };
	
	public abstract String toString();
	
	public static void populateHashtable(Hashtable<IntermediateSymbolSort,Hashtable<Symbol,IntermediateSymbolNode> > table)
	{
		table.put(TERMINAL,new Hashtable<Symbol,IntermediateSymbolNode>());
		table.put(NON_TERMINAL,new Hashtable<Symbol,IntermediateSymbolNode>());
		table.put(PRODUCTION,new Hashtable<Symbol,IntermediateSymbolNode>());
		table.put(GRAMMAR_NAME,new Hashtable<Symbol,IntermediateSymbolNode>());
		table.put(TERMINAL_CLASS,new Hashtable<Symbol,IntermediateSymbolNode>());
		table.put(DISAMBIGUATION_GROUP,new Hashtable<Symbol,IntermediateSymbolNode>());
		table.put(PARSER_ATTRIBUTE,new Hashtable<Symbol,IntermediateSymbolNode>());
		table.put(DIRECTIVE,new Hashtable<Symbol,IntermediateSymbolNode>());
	}
}
