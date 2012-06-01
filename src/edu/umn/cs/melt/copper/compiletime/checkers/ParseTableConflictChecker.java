package edu.umn.cs.melt.copper.compiletime.checkers;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTablePrinter;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;

/**
 * Counts the parse table conflicts in a compiled parser specification, and checks if they have all been resolved.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ParseTableConflictChecker
{
	private CompilerLogger logger;
	private SymbolTable<CopperASTBean> symbolTable;
	private ParserSpec spec;
	private LRParseTable parseTable;
	private GrammarStatistics stats;
	
	public static boolean check(CompilerLogger logger,SymbolTable<CopperASTBean> symbolTable,ParserSpec spec,LRParseTable parseTable,GrammarStatistics stats)
	{
		return new ParseTableConflictChecker(logger, symbolTable, spec, parseTable, stats).checkConflicts();
	}
	
	private ParseTableConflictChecker(CompilerLogger logger,
			SymbolTable<CopperASTBean> symbolTable, ParserSpec spec,
			LRParseTable parseTable, GrammarStatistics stats)
	{
		this.logger = logger;
		this.symbolTable = symbolTable;
		this.spec = spec;
		this.parseTable = parseTable;
		this.stats = stats;
	}

	private boolean checkConflicts()
	{
		boolean passed = true;
		
		stats.parseTableConflictCount = parseTable.getConflictCount();
		stats.unresolvedParseTableConflictCount = 0;
		
		for(int i = 0;i < parseTable.getConflictCount();i++)
		{
			LRParseTableConflict conflict = parseTable.getConflict(i);
			
			if(parseTable.getActionType(conflict.getState(),conflict.getSymbol()) == LRParseTable.CONFLICT)
			{
				logger.logParseTableConflict(CompilerLogMessageSort.UNRESOLVED_CONFLICT,false,conflict.getState(),symbolTable.get(conflict.getSymbol()).getDisplayName(),printConflict(conflict));
				stats.unresolvedParseTableConflictCount++;
				passed = false;
			}
			else
			{
				if(logger.isLoggable(CompilerLogMessageSort.PARSE_TABLE_CONFLICT)) logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,conflict.getState(),symbolTable.get(conflict.getSymbol()).getDisplayName(),generateParseTableConflictMessage(conflict));
				else logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,conflict.getState(), symbolTable.get(conflict.getSymbol()).getDisplayName(),"");				
			}
		}
		return passed;
	}
	
	private String printConflict(LRParseTableConflict conflict)
	{
		String[] actions = new String[(conflict.shift != -1 ? 1 : 0) + conflict.reduce.cardinality()];
		int i = 0;
		if(conflict.shift != -1) actions[i++] = LRParseTablePrinter.printAction(symbolTable, spec, conflict.getState(), conflict.getSymbol(), LRParseTable.SHIFT, conflict.shift);
		for(int k = conflict.reduce.nextSetBit(0);k >= 0;k = conflict.reduce.nextSetBit(k+1)) actions[i++] = LRParseTablePrinter.printAction(symbolTable, spec, conflict.getState(), conflict.getSymbol(), LRParseTable.REDUCE, k);
		BitSet allActions = new BitSet();
		allActions.set(0,actions.length);
		return PrettyPrinter.bitSetPrettyPrint(allActions, actions, "  ", 1);
	}
	
	private String generateParseTableConflictMessage(LRParseTableConflict conflict)
	{
		if(parseTable.getActionType(conflict.getState(),conflict.getSymbol()) == LRParseTable.ERROR)
		{
			return printConflict(conflict) + "\n  Resolved in favor of an error action.";
		}
		else
		{
			return printConflict(conflict) + "\n  Resolved in favor of " + LRParseTablePrinter.printAction(symbolTable, spec, conflict.getState(), conflict.getSymbol(), parseTable.getActionType(conflict.getState(), conflict.getSymbol()), parseTable.getActionParameter(conflict.getState(), conflict.getSymbol()));
		}
	}
}
