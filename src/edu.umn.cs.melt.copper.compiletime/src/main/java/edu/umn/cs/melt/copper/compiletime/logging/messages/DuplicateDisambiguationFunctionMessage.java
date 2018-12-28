package edu.umn.cs.melt.copper.compiletime.logging.messages;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;

public class DuplicateDisambiguationFunctionMessage implements CompilerLogMessage
{
	private SymbolTable<CopperASTBean> symbolTable;
	private BitSet members;

	public DuplicateDisambiguationFunctionMessage(SymbolTable<CopperASTBean> symbolTable,
			BitSet members)
	{
		this.symbolTable = symbolTable;
		this.members = members;
	}

	@Override
	public CompilerLevel getLevel()
	{
		return CompilerLevel.QUIET;
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.DISAMBIGUATION_FUNCTION_CONFLICT;
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
		return "Multiple disambiguation functions for\n" + PSSymbolTable.bitSetPrettyPrint(members,symbolTable,"   ",80);		
	}

}
