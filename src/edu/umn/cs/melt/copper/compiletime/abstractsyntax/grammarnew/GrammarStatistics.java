package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew;

import java.util.BitSet;

public class GrammarStatistics
{
	public BitSet uselessNTs;
	public BitSet nonTerminalNTs;
	public int parseTableConflictCount;
	public int unresolvedParseTableConflictCount;
	public int lexicalAmbiguityCount;
	public int contextResolvedLexicalAmbiguityCount;
	public int disambiguationFunctionResolvedLexicalAmbiguityCount;
	public int unresolvableLexicalAmbiguityCount;
}
