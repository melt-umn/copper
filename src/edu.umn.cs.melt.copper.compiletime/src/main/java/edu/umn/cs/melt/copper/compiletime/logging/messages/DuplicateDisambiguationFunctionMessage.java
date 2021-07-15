package edu.umn.cs.melt.copper.compiletime.logging.messages;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;

public class DuplicateDisambiguationFunctionMessage implements CompilerLogMessage
{
	private SymbolTable<CopperASTBean> symbolTable;
	private int function1, function2;
	private BitSet members;

	public DuplicateDisambiguationFunctionMessage(SymbolTable<CopperASTBean> symbolTable,
			int function1, int function2, BitSet members)
	{
		this.symbolTable = symbolTable;
		this.function1 = function1;
		this.function2 = function2;
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
		return "Disambiguation function " + symbolTable.get(function1).getDisplayName() +
				" conflicts with " + symbolTable.get(function2).getDisplayName() +
				" for group\n" + PSSymbolTable.bitSetPrettyPrint(members,symbolTable,"   ",80);
	}

}
