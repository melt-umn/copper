package edu.umn.cs.melt.copper.compiletime.logging;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.ReadOnlyParseTable;

/**
 * Contains a method for producing the parser compiler's "final report."
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class FinalReporter
{
	private CompilerLogger logger;
	private GrammarSource grammar;
	private ReadOnlyParseTable parseTable;
	
	private int numTerminals;
	private int numNonTerminals;
	private int numProductions;
	private int numDisambigGroups;
	
	private int numScannerStates;
	
	public FinalReporter(CompilerLogger logger,GrammarSource grammar,ReadOnlyParseTable parseTable,int scannerStateCount)
	{
		this.logger = logger;
		this.grammar = grammar;
		this.parseTable = parseTable;
		this.numScannerStates = scannerStateCount;
	}
	
	
	public void logFinalReport()
	{
		if(!logger.isLoggable(CompilerLogMessageSort.FINAL_REPORT)) return;
		
		numTerminals = grammar.tSize();
		numNonTerminals = grammar.ntSize();
		numProductions = 0;
		for(NonTerminal nt : grammar.getNT())
		{
			if(!grammar.pContains(nt)) continue;
			for(@SuppressWarnings("unused") Production p : grammar.getP(nt)) numProductions++;
		}
		numDisambigGroups = grammar.disambiguationGroupSize();

		logger.logMessage(CompilerLogMessageSort.FINAL_REPORT,null,"===== FINAL REPORT =====\n");
		
		logger.logMessage(CompilerLogMessageSort.FINAL_REPORT,null,numTerminals + " terminal" + ((numTerminals == 1) ? "" : "s") + ", " + numNonTerminals + " nonterminal" + ((numNonTerminals == 1) ? "" : "s") + ",\n" + numProductions + " productions, and " + numDisambigGroups + " disambiguation function" + ((numDisambigGroups == 1) ? "" : "s") + " declared,\n");
		logger.logMessage(CompilerLogMessageSort.FINAL_REPORT,null,"producing " + (parseTable.getLastState() + 1) + " unique parse state" + ((parseTable.getLastState() == 0) ? "" : "s") + "\n");
		logger.logMessage(CompilerLogMessageSort.FINAL_REPORT,null,"and " + numScannerStates + " unique scanner states.\n");
		logger.logMessage(CompilerLogMessageSort.FINAL_REPORT,null,grammar.getUselessNonterminalCount() + " useless nonterminal" + ((grammar.getUselessNonterminalCount() == 1) ? "" : "s") + ".\n");
		if(logger.getParseTableConflictCounter() == 0)
		{
			logger.logMessage(CompilerLogMessageSort.FINAL_REPORT,null,"No parse table conflicts detected.\n");			
		}
		else
		{
			logger.logMessage(CompilerLogMessageSort.FINAL_REPORT,null,logger.getParseTableConflictCounter() + " parse table conflict" + ((logger.getParseTableConflictCounter() == 1) ? "" : "s") + " detected; " + logger.getResolvedParseTableConflictCounter() + " resolved.\n");
		}
		if(logger.getLexicalConflictCounter() == 0)
		{
			logger.logMessage(CompilerLogMessageSort.FINAL_REPORT,null,"No lexical ambiguities detected.\n");
		}
		else
		{
			logger.logMessage(CompilerLogMessageSort.FINAL_REPORT,null,logger.getLexicalConflictCounter() + " lexical ambiguit" + ((logger.getLexicalConflictCounter() == 1) ? "y" : "ies") + " detected; " + (logger.getResolvedLexicalConflictByContextCounter() + logger.getResolvedLexicalConflictByGroupCounter()) + " resolved,\n");
			logger.logMessage(CompilerLogMessageSort.FINAL_REPORT,null,"   " + logger.getResolvedLexicalConflictByContextCounter() + " by context, " + logger.getResolvedLexicalConflictByGroupCounter() + " by disambiguation function/group.\n");
		}
	}
}
