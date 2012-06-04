package edu.umn.cs.melt.copper.compiletime.loggingnew.messages;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTablePrinter;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;

public class ParseTableConflictMessage implements CompilerLogMessage
{
	private SymbolTable<CopperASTBean> symbolTable;
	private ParserSpec spec;
	private LRParseTableConflict conflict;
	private byte resolvedActionType;
	private int resolvedActionParameter;
	
	public ParseTableConflictMessage(SymbolTable<CopperASTBean> symbolTable,ParserSpec spec,LRParseTable parseTable,LRParseTableConflict conflict)
	{
		this.symbolTable = symbolTable;
		this.spec = spec;
		this.conflict = conflict;
		this.resolvedActionType = parseTable.getActionType(conflict.getState(),conflict.getSymbol());
		this.resolvedActionParameter = parseTable.getActionParameter(conflict.getState(),conflict.getSymbol());
	}
	
	@Override
	public CompilerLevel getLevel()
	{
		if(resolvedActionType == LRParseTable.CONFLICT) return CompilerLevel.QUIET;
		else return CompilerLevel.VERBOSE;
	}
	
	@Override
	public int getType()
	{
		return CompilerLogMessageType.PARSE_TABLE_CONFLICT;
	}
	
	public boolean isResolved()
	{
		return (resolvedActionType == LRParseTable.CONFLICT);		
	}
	
	@Override
	public boolean isError()
	{
		return isResolved();
	}
	
	@Override
	public boolean isFatalError()
	{
		return false;
	}
	
	public String toString()
	{
		StringBuffer rv = new StringBuffer();
		if(!isResolved()) rv.append("P");
		else rv.append("Unresolvable p");
		rv.append("arse table conflict at ").append("parser state ").append(conflict.getState()).append(", on terminal ").append(symbolTable.get(conflict.getSymbol()).getDisplayName()).append(":\n");
		String[] actions = new String[(conflict.shift != -1 ? 1 : 0) + conflict.reduce.cardinality()];
		int i = 0;
		if(conflict.shift != -1) actions[i++] = LRParseTablePrinter.printAction(symbolTable, spec, conflict.getState(), conflict.getSymbol(), LRParseTable.SHIFT, conflict.shift);
		for(int k = conflict.reduce.nextSetBit(0);k >= 0;k = conflict.reduce.nextSetBit(k+1)) actions[i++] = LRParseTablePrinter.printAction(symbolTable, spec, conflict.getState(), conflict.getSymbol(), LRParseTable.REDUCE, k);
		BitSet allActions = new BitSet();
		allActions.set(0,actions.length);
		rv.append(PrettyPrinter.bitSetPrettyPrint(allActions, actions, "  ", 1));
		if(resolvedActionType == LRParseTable.ERROR)
		{
			rv.append("\n  Resolved in favor of an error action.");
		}
		else if(resolvedActionType != LRParseTable.CONFLICT)
		{
			rv.append("\n  Resolved in favor of " + LRParseTablePrinter.printAction(symbolTable, spec, conflict.getState(), conflict.getSymbol(),resolvedActionType,resolvedActionParameter));
		}
		return rv.toString();
	}
}
