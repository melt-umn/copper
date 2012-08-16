package edu.umn.cs.melt.copper.compiletime.checkers;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.loggingnew.messages.ParseTableConflictMessage;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;

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
			boolean logConflict = logger.isLoggable(CompilerLevel.VERBOSE);
			LRParseTableConflict conflict = parseTable.getConflict(i);
			
			if(parseTable.getActionType(conflict.getState(),conflict.getSymbol()) == LRParseTable.CONFLICT)
			{
				stats.unresolvedParseTableConflictCount++;
				logConflict = logger.isLoggable(CompilerLevel.QUIET);
				passed = false;
			}
			if(logConflict) logger.log(new ParseTableConflictMessage(symbolTable, spec, parseTable, conflict));
		}
		return passed;
	}
}
