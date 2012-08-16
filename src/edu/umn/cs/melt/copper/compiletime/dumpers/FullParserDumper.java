package edu.umn.cs.melt.copper.compiletime.dumpers;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;

public abstract class FullParserDumper implements Dumper
{
	protected PSSymbolTable symbolTable;
	protected ParserSpec spec;
	protected LR0DFA dfa;
	protected LRLookaheadAndLayoutSets lookahead;
	protected LRParseTable parseTable;
	protected TransparentPrefixes prefixes;
	
	public FullParserDumper(PSSymbolTable symbolTable, ParserSpec spec,
			LR0DFA dfa, LRLookaheadAndLayoutSets lookahead,
			LRParseTable parseTable, TransparentPrefixes prefixes)
	{
		super();
		this.symbolTable = symbolTable;
		this.spec = spec;
		this.dfa = dfa;
		this.lookahead = lookahead;
		this.parseTable = parseTable;
		this.prefixes = prefixes;
	}
}
