package edu.umn.cs.melt.copper.compiletime.logging.messages;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;

public class OverlappingDisambiguationFunctionMessage implements CompilerLogMessage
{
	private SymbolTable<CopperASTBean> symbolTable;
	private BitSet intersect;

	public OverlappingDisambiguationFunctionMessage(SymbolTable<CopperASTBean> symbolTable,
			BitSet intersect)
	{
		this.symbolTable = symbolTable;
		this.intersect = intersect;
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
		return "Multiple disambiguation functions applicable to subsets contain members\n" + PSSymbolTable.bitSetPrettyPrint(intersect,symbolTable,"   ",80);		
	}

}
