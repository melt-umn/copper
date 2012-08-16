package edu.umn.cs.melt.copper.compiletime.parsetable;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;

public class LRParseTablePrinter
{
	public static String toString(SymbolTable<CopperASTBean> symbolTable,ParserSpec spec,LRParseTable parseTable)
	{
		StringBuffer rv = new StringBuffer();
		for(int state = 0;state < parseTable.size();state++)
		{
			rv.append("State ").append(state).append(":\n");
			rv.append("  Valid lookahead:\n");
			rv.append(PSSymbolTable.bitSetPrettyPrint(parseTable.getValidLA(state), symbolTable, "        ", 80));
			rv.append("\n  Actions:\n");
			for(int t = parseTable.getValidLA(state).nextSetBit(0);t >= 0;t = parseTable.getValidLA(state).nextSetBit(t+1))
			{
				rv.append("    ").append(symbolTable.get(t).getDisplayName()).append(" : ");
				printAction(rv, symbolTable, spec, state, t, parseTable.getActionType(state,t), parseTable.getActionParameter(state,t));
				rv.append("\n");
			}
		}
		for(int i = 0;i < parseTable.getConflictCount();i++)
		{
			LRParseTableConflict conflict = parseTable.getConflict(i);
			rv.append("  Parse table conflict in cell (" + conflict.getState() + "," + symbolTable.get(conflict.getSymbol()).getDisplayName() + ") among actions\n");
			if(conflict.shift != -1)
			{
				rv.append("    ");
				printAction(rv,symbolTable,spec,conflict.getState(),conflict.getSymbol(),LRParseTable.SHIFT,conflict.shift);
				rv.append("\n");
			}
			for(int p = conflict.reduce.nextSetBit(0);p >= 0;p = conflict.reduce.nextSetBit(p+1))
			{
				rv.append("    ");
				printAction(rv,symbolTable,spec,conflict.getState(),conflict.getSymbol(),LRParseTable.REDUCE,p);
				rv.append("\n");
			}
			if(parseTable.getActionType(conflict.getState(),conflict.getSymbol()) != LRParseTable.CONFLICT)
			{
				rv.append("  Resolved in favor of \"");
				printAction(rv, symbolTable, spec, conflict.getState(), conflict.getSymbol(), parseTable.getActionType(conflict.getState(),conflict.getSymbol()), parseTable.getActionParameter(conflict.getState(),conflict.getSymbol()));
				rv.append("\"\n");
			}
			else rv.append("  UNRESOLVED");
		}
		return rv.toString();
	}
	
	private static void printAction(StringBuffer rv,SymbolTable<CopperASTBean> symbolTable,ParserSpec spec,int state,int t,byte actionType,int actionParameter)
	{
		switch(actionType)
		{
		case LRParseTable.SHIFT:
			if(t == spec.getEOFTerminal()) rv.append("ACCEPT");
			else if(spec.terminals.get(t)) rv.append("SHIFT(").append(actionParameter).append(")");
			else                          rv.append("GOTO(").append(actionParameter).append(")");
			break;
		case LRParseTable.REDUCE:
			rv.append("REDUCE(");
			rv.append(symbolTable.get(spec.pr.getLHS(actionParameter)).getDisplayName());
			rv.append(" ::=");
			for(int i = 0;i < spec.pr.getRHSLength(actionParameter);i++)
			{
				rv.append(" ").append(symbolTable.get(spec.pr.getRHSSym(actionParameter,i)).getDisplayName());
			}
			rv.append(")");
			break;
		case LRParseTable.CONFLICT:
			rv.append("*UNRESOLVED CONFLICT*");
			break;
		default:
			rv.append("[ERROR]");
		}
		
	}
	
	public static String printAction(SymbolTable<CopperASTBean> symbolTable,ParserSpec spec,int state,int t,byte actionType,int actionParameter)
	{
		StringBuffer rv = new StringBuffer();
		printAction(rv, symbolTable, spec, state, t, actionType, actionParameter);
		return rv.toString();
	}
}
