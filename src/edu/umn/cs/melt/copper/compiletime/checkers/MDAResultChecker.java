package edu.umn.cs.melt.copper.compiletime.checkers;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.FollowSpillageMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.LookaheadSpillageMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.NonILSubsetMessage;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.mda.MDAResults;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

public class MDAResultChecker
{
	public static boolean check(CompilerLogger logger, MDAResults results,	PSSymbolTable symbolTable, ParserSpec spec, LR0DFA fullDFA, GrammarStatistics stats)
	{
		return new MDAResultChecker(logger,results,symbolTable,spec,fullDFA,stats).check();
	}
	
	private CompilerLogger logger;
	private MDAResults results;
	private PSSymbolTable symbolTable;
	private ParserSpec spec;
	private LR0DFA fullDFA;
	private GrammarStatistics stats;
	
	private MDAResultChecker(CompilerLogger logger, MDAResults results,	PSSymbolTable symbolTable, ParserSpec spec, LR0DFA fullDFA, GrammarStatistics stats)
	{
		this.logger = logger;
		this.results = results;
		this.symbolTable = symbolTable;
		this.spec = spec;
		this.fullDFA = fullDFA;
		this.stats = stats;
	}



	public boolean check()
	{
		BitSet diff;
		
		BitSet followSpilledNTs = new BitSet();
		BitSet hostPartition = new BitSet();
		hostPartition.or(results.getHostPartition());
		BitSet extPartition = new BitSet();
		extPartition.or(results.getExtPartition());
		BitSet newHostPartition = new BitSet();
		newHostPartition.or(results.getNewHostPartition());
		BitSet unpartitionableStates = new BitSet();
				
		for(int i = 0;i < results.size();i++)
		{
			switch(results.getErrorType(i))
			{
			case MDAResults.FOLLOW_SPILLAGE:
				diff = new BitSet();
				diff.or(results.getFullFollowSet(i));
				diff.andNot(results.getHostFollowSet(i));
				followSpilledNTs.set(results.getNonterminal(i));
				logger.log(new FollowSpillageMessage(symbolTable,results.getNonterminal(i),diff));
				break;
			case MDAResults.LOOKAHEAD_SPILLAGE:
				diff = new BitSet();
				diff.or(results.getFullLookaheadSet(i));
				diff.andNot(results.getHostLookaheadSet(i));
				hostPartition.clear(results.getFullState(i));
				unpartitionableStates.set(results.getFullState(i));
				for(int item = results.getItems(i).nextSetBit(0);item >= 0;item = results.getItems(i).nextSetBit(item+1))
				{
					logger.log(new LookaheadSpillageMessage(symbolTable, spec, fullDFA, results.getFullState(i), item, diff));
				}
				break;
			case MDAResults.NON_IL_SUBSET:
				newHostPartition.clear(results.getFullState(i));
				unpartitionableStates.set(results.getFullState(i));
				logger.log(new NonILSubsetMessage(results.getFullState(i)));
				break;
			case MDAResults.I_SUBSET_ONLY:
				newHostPartition.clear(results.getFullState(i));
				unpartitionableStates.set(results.getFullState(i));
				logger.log(new NonILSubsetMessage(results.getFullState(i),results.getISuperset(i)));
			}
		}

		stats.mdaRun = true;
		stats.mdaPassed = (results.size() == 0);
		stats.followSpilledNTCount = followSpilledNTs.cardinality();
		stats.hostStateCount = hostPartition.cardinality();
		stats.extStateCount  = extPartition.cardinality();
		stats.newHostStateCount = newHostPartition.cardinality();
		stats.unpartitionableStateCount = unpartitionableStates.cardinality();

		if(logger.isLoggable(CompilerLevel.VERBOSE))
		{
			logger.log(new GenericMessage(CompilerLevel.VERBOSE,stats.followSpilledNTCount + " nonterminals with follow spillage: " + PSSymbolTable.bitSetPrettyPrint(followSpilledNTs,symbolTable,"   ",80)));
			logger.log(new GenericMessage(CompilerLevel.VERBOSE,stats.hostStateCount + " host states: " + hostPartition));
			logger.log(new GenericMessage(CompilerLevel.VERBOSE,stats.extStateCount + " extension states: " + extPartition));
			logger.log(new GenericMessage(CompilerLevel.VERBOSE,stats.newHostStateCount + " new-host states: " + newHostPartition));
			logger.log(new GenericMessage(CompilerLevel.VERBOSE,stats.unpartitionableStateCount + " unpartitionable states: " + unpartitionableStates));
		}
		
		return (results.size() == 0);
	}
}
