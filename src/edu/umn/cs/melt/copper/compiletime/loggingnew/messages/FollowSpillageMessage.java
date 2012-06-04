package edu.umn.cs.melt.copper.compiletime.loggingnew.messages;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessageType;

public class FollowSpillageMessage implements CompilerLogMessage
{
	private SymbolTable<CopperASTBean> symbolTable;
	//private ParserSpec spec;
	//private LR0DFA dfa;
	private int nonterminal;
	private BitSet diff;

	public FollowSpillageMessage(SymbolTable<CopperASTBean> symbolTable,
			int nonterminal, BitSet diff)
	{
		this.symbolTable = symbolTable;
		this.nonterminal = nonterminal;
		this.diff = diff;
	}

	@Override
	public CompilerLevel getLevel()
	{
		return CompilerLevel.QUIET;
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.FOLLOW_SPILLAGE;
	}

	@Override
	public boolean isError()
	{
		return true;
	}

	@Override
	public boolean isFatalError()
	{
		return false;
	}
	
	public String toString()
	{
		return "Nonterminal " + symbolTable.get(nonterminal).getDisplayName() + " has follow spillage of\n" + PSSymbolTable.bitSetPrettyPrint(diff,symbolTable,"   ",80);		
	}

}
