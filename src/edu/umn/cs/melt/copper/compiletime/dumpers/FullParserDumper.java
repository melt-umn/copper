package edu.umn.cs.melt.copper.compiletime.dumpers;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

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
