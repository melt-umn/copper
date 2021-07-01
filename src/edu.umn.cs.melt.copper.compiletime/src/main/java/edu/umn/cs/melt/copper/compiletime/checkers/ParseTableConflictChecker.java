package edu.umn.cs.melt.copper.compiletime.checkers;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.CounterexampleMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.ParseTableConflictMessage;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ContextSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

/**
 * Counts the parse table conflicts in a compiled parser specification, and checks if they have all been resolved.
 * Modified by Kelton to produce counterexamples.
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 * @author Kelton O'Brien &lt;<a href="mailto:obri0707@umn.edu">obri0707@umn.edu</a>&gt;
 *
 */
public class ParseTableConflictChecker
{
	private CompilerLogger logger;
	private PSSymbolTable symbolTable;
	private ParserSpec spec;
	private LRParseTable parseTable;
	private GrammarStatistics stats;

	// The LR0DFA contains the LR item sets which is needed for the construction of the
	// lookahead-sensitive graph used in the construction of {,non}unifying counterexamples
	private LR0DFA dfa;
	private ContextSets contextSets;
	
	public static boolean check(CompilerLogger logger,
								PSSymbolTable symbolTable, ParserSpec spec,
								LRParseTable parseTable, LR0DFA dfa,
								 ContextSets contextSets,
								GrammarStatistics stats)
	{
		return new ParseTableConflictChecker(logger, symbolTable, spec, parseTable, dfa, contextSets, stats).checkConflicts();
	}
	
	private ParseTableConflictChecker(CompilerLogger logger, PSSymbolTable symbolTable, ParserSpec spec,
			LRParseTable parseTable, LR0DFA dfa, ContextSets contextSets, GrammarStatistics stats)
	{
		this.logger = logger;
		this.symbolTable = symbolTable;
		this.spec = spec;
		this.parseTable = parseTable;
		this.stats = stats;
		this.dfa = dfa;
		this.contextSets = contextSets;
	}

	private boolean checkConflicts()
	{
		boolean passed = true;
		LRParseTableConflict firstUnresolvedConflict = null;

		
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
				if(firstUnresolvedConflict == null){
					firstUnresolvedConflict = conflict;
				}
			}
			//considering logger.log already checks the message level, this if seems redundant
			if(logConflict) logger.log(new ParseTableConflictMessage(symbolTable, spec, parseTable, conflict));
		}
		//TODO implement a command line option to disable counterexamples
		if(firstUnresolvedConflict != null){
			logger.log(new CounterexampleMessage(symbolTable,dfa,firstUnresolvedConflict,contextSets,spec));
		}
		return passed;
	}
}
