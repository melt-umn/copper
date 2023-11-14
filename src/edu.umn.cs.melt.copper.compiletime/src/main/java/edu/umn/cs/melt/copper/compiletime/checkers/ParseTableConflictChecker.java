package edu.umn.cs.melt.copper.compiletime.checkers;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.CounterexampleMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.ParseTableConflictMessage;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadSets;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ContextSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Counts the parse table conflicts in a compiled parser specification, and checks if they have all been resolved.
 * Modified by Kelton to produce counterexamples.
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 * @author Kelton OBrien &lt;<a href="mailto:obri0707@umn.edu">obri0707@umn.edu</a>&gt;
 *
 */
public class ParseTableConflictChecker
{
	private LRLookaheadSets lookaheadSets;
	private CompilerLogger logger;
	private PSSymbolTable symbolTable;
	private ParserSpec spec;
	private LRParseTable parseTable;
	private GrammarStatistics stats;

	// The LR0DFA contains the LR item sets which is needed for the construction of the
	// lookahead-sensitive graph used in the construction of {,non}unifying counterexamples
	private LR0DFA dfa;
	private ContextSets contextSets;

	private File dotOut;

	private boolean colorExample;
	
	public static boolean check(CompilerLogger logger, File dotOut,
								PSSymbolTable symbolTable, ParserSpec spec,
								LRParseTable parseTable, LR0DFA dfa,
								ContextSets contextSets, LRLookaheadSets lookaheadSets,
								GrammarStatistics stats, boolean colorExample)
	{
		return new ParseTableConflictChecker(logger, dotOut, symbolTable, spec, parseTable, dfa, contextSets, lookaheadSets, stats, colorExample).checkConflicts();
	}
	
	private ParseTableConflictChecker(CompilerLogger logger, File dotOut, PSSymbolTable symbolTable, ParserSpec spec,
			LRParseTable parseTable, LR0DFA dfa, ContextSets contextSets, LRLookaheadSets lookaheadSets, GrammarStatistics stats, boolean colorExample)
	{
		this.logger = logger;
		this.dotOut = dotOut;
		this.symbolTable = symbolTable;
		this.spec = spec;
		this.parseTable = parseTable;
		this.stats = stats;
		this.dfa = dfa;
		this.contextSets = contextSets;
		this.lookaheadSets = lookaheadSets;
		this.colorExample = colorExample;
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
			if(logConflict) logger.log(new ParseTableConflictMessage(symbolTable, spec, parseTable, conflict));
		}
		if(firstUnresolvedConflict != null){
			CounterexampleMessage counterexample = new CounterexampleMessage(symbolTable,dfa,firstUnresolvedConflict,contextSets,spec,lookaheadSets,colorExample);
			logger.log(counterexample);
			if(dotOut != null){
				try {
					FileWriter writer = new FileWriter(dotOut,false);
					writer.write(counterexample.toDot());
					writer.close();
				} catch (IOException e) {
					logger.log(new GenericMessage(CompilerLevel.QUIET,"Failed to write dot mark-up representation of counterexample due to IOException " + e));
				}
			}
		}
		return passed;
	}
}
