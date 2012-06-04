package edu.umn.cs.melt.copper.compiletime.loggingnew.messages;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessageType;

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
			rv.append(stats.parseTableConflictCount + " parse table conflict" + ((stats.parseTableConflictCount == 1) ? "" : "s") + " detected; " + (stats.parseTableConflictCount - stats.unresolvedParseTableConflictCount) + " resolved.\n");
		}
		if(stats.lexicalAmbiguityCount == 0)
		{
			rv.append("No lexical ambiguities detected.\n");
		}
		else
		{
			rv.append(stats.lexicalAmbiguityCount + " lexical ambiguit" + ((stats.lexicalAmbiguityCount == 1) ? "y" : "ies") + " detected; " + (stats.contextResolvedLexicalAmbiguityCount + stats.disambiguationFunctionResolvedLexicalAmbiguityCount) + " resolved,\n");
			rv.append("   " + stats.contextResolvedLexicalAmbiguityCount + " by context, " + stats.disambiguationFunctionResolvedLexicalAmbiguityCount + " by disambiguation function/group.\n");
		}
		return rv.toString();
	}

}
