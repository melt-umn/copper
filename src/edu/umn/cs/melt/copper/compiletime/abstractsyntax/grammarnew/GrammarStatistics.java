package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew;

import java.util.BitSet;

public class GrammarStatistics
{
	public GrammarStatistics(ParserSpec spec)
	{
		terminalCount = spec.terminals.cardinality();
		nonterminalCount = spec.nonterminals.cardinality();
		productionCount = spec.productions.cardinality();
		disambiguationFunctionCount = spec.disambiguationFunctions.cardinality();
	}
	
	public int terminalCount;
	public int nonterminalCount;
	public int productionCount;
	public int disambiguationFunctionCount;
	public int parseStateCount;
	public int scannerStateCount;

	public BitSet uselessNTs;
	public BitSet nonTerminalNTs;
	public int parseTableConflictCount;
	public int unresolvedParseTableConflictCount;
	public int lexicalAmbiguityCount;
	public int contextResolvedLexicalAmbiguityCount;
	public int disambiguationFunctionResolvedLexicalAmbiguityCount;
	public int unresolvableLexicalAmbiguityCount;
}
