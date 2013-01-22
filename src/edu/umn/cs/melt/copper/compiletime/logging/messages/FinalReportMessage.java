package edu.umn.cs.melt.copper.compiletime.logging.messages;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.GrammarStatistics;

public class FinalReportMessage implements CompilerLogMessage
{
	private GrammarStatistics stats;
	
	public FinalReportMessage(GrammarStatistics stats)
	{
		this.stats = stats;
	}

	@Override
	public CompilerLevel getLevel()
	{
		return CompilerLevel.REGULAR;
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.FINAL_REPORT;
	}

	@Override
	public boolean isError()
	{
		return false;
	}

	@Override
	public boolean isFatalError()
	{
		return false;
	}
	
	public String toString()
	{
		StringBuffer rv = new StringBuffer();
		rv.append("===== FINAL REPORT =====\n");
		
		rv.append(stats.terminalCount + " terminal" + ((stats.terminalCount == 1) ? "" : "s") + ", " + stats.nonterminalCount + " nonterminal" + ((stats.nonterminalCount == 1) ? "" : "s") + ",\n" + stats.productionCount + " productions, and " + stats.disambiguationFunctionCount + " disambiguation function" + ((stats.disambiguationFunctionCount == 1) ? "" : "s") + " declared,\n");
		rv.append("producing " + stats.parseStateCount + " unique parse state" + ((stats.parseStateCount == 1) ? "" : "s") + "\n");
		rv.append("and " + stats.scannerStateCount + " unique scanner state" + ((stats.scannerStateCount == 1) ? "" : "s") + ".\n");
		rv.append(stats.uselessNTs.cardinality() + " useless nonterminal" + ((stats.uselessNTs.cardinality() == 1) ? "" : "s") + ".\n");
		if(stats.parseTableConflictCount == 0)
		{
			rv.append("No parse table conflicts detected.\n");			
		}
		else
		{
			rv.append(stats.parseTableConflictCount + " parse table conflict" + ((stats.parseTableConflictCount == 1) ? "" : "s") + " detected\n   (" + stats.shiftReduceParseTableConflictCount + " shift/reduce, " + stats.reduceReduceParseTableConflictCount + " reduce/reduce); " + (stats.parseTableConflictCount - stats.unresolvedParseTableConflictCount) + " resolved.\n");
		}
		if(stats.lexicalAmbiguityCount == 0)
		{
			rv.append("No lexical ambiguities detected.");
		}
		else
		{
			rv.append(stats.lexicalAmbiguityCount + " lexical ambiguit" + ((stats.lexicalAmbiguityCount == 1) ? "y" : "ies") + " detected; " + (stats.contextResolvedLexicalAmbiguityCount + stats.disambiguationFunctionResolvedLexicalAmbiguityCount) + " resolved,\n");
			rv.append("   " + stats.contextResolvedLexicalAmbiguityCount + " by context, " + stats.disambiguationFunctionResolvedLexicalAmbiguityCount + " by disambiguation function/group.");
		}
		if(stats.codeOutput)
		{
			rv.append("\nParser code output to ").append(stats.codeOutputTo).append(".");
		}
		else
		{
			rv.append("\nNo parser code output.");
		}
		if(stats.mdaRun)
		{
			rv.append("\n----------\nModular determinism analysis " + (stats.mdaPassed ? "passed" : "failed") + ".\n");
			rv.append(stats.followSpilledNTCount + " nonterminal" + (stats.followSpilledNTCount == 1 ? "" : "s") + " with follow spillage.\n");
			rv.append("Composed parser has " + stats.hostStateCount + " host state" + (stats.hostStateCount == 1 ? "" : "s") + ", " + stats.extStateCount + " extension state" + (stats.extStateCount == 1 ? "" : "s") + ",\n");
			rv.append(stats.newHostStateCount + " new-host state" + (stats.newHostStateCount == 1 ? "" : "s") + ", and " + stats.unpartitionableStateCount + " unpartitionable state" + (stats.unpartitionableStateCount == 1 ? "" : "s") + ".");
		}
		return rv.toString();
	}

}
