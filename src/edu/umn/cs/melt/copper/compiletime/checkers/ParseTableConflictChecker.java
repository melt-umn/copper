package edu.umn.cs.melt.copper.compiletime.checkers;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.ParseTableConflictMessage;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

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
		stats.shiftReduceParseTableConflictCount = 0;
		stats.reduceReduceParseTableConflictCount = 0;
		stats.unresolvedParseTableConflictCount = 0;
		
		for(int i = 0;i < parseTable.getConflictCount();i++)
		{
			boolean logConflict = logger.isLoggable(CompilerLevel.VERBOSE);
			LRParseTableConflict conflict = parseTable.getConflict(i);
		
			if(conflict.shift != -1) stats.shiftReduceParseTableConflictCount++;
			if(conflict.reduce.cardinality() > 1) stats.reduceReduceParseTableConflictCount++;
			
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
