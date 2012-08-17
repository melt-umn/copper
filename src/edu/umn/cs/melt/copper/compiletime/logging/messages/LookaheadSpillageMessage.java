package edu.umn.cs.melt.copper.compiletime.logging.messages;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

public class LookaheadSpillageMessage implements CompilerLogMessage
{
	private SymbolTable<CopperASTBean> symbolTable;
	//private ParserSpec spec;
	//private LR0DFA dfa;
	private int state;
	private int item;
	private BitSet diff;

	public LookaheadSpillageMessage(SymbolTable<CopperASTBean> symbolTable,
			ParserSpec spec, LR0DFA dfa, int state, int item, BitSet diff) {
		this.symbolTable = symbolTable;
		//this.spec = spec;
		//this.dfa = dfa;
		this.state = state;
		this.item = item;
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
		return CompilerLogMessageType.LOOKAHEAD_SPILLAGE;
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
		return "DFA state " + state + ", item " + item + " has lookahead spillage of\n" + PSSymbolTable.bitSetPrettyPrint(diff,symbolTable,"   ",80);		
	}

}
