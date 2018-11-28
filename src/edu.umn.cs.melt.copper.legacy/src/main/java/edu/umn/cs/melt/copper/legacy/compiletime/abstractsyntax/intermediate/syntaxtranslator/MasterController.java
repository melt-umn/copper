package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.syntaxtranslator;

import java.util.Hashtable;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.ParsedRegex;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class MasterController
{
	public static final int AST_DOT_WINDOW = 64;
	public static final int DOT_WINDOW = 16;
	
	public static GrammarSource buildAST(CompilerLogger logger,Hashtable<Symbol,IntermediateSymbolNode> intermediateRep)
	throws CopperException
	{
		GrammarSource grammarData = new GrammarSource();
		
		
		SymGatherer symGatherer = new SymGatherer(grammarData);
		symGatherer.symGather(logger,intermediateRep);
		
		if(grammarData.getStartSym() == null)
		{
			if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,"No start symbol specified");
			return null;
		}

		grammarData.constructPrecedenceRelationsGraph();

		RegexDependencyChecker regexDepChecker = new RegexDependencyChecker(grammarData,symGatherer.sortedNodes);
		regexDepChecker.regexDepCheck(logger,intermediateRep);

		LinkedList<Terminal> regexQueue = new LinkedList<Terminal>();
		regexDepChecker.regexDependencies.detectCycles(logger,"Regex dependency check",regexQueue);
		
		for(Terminal regexTerm : regexQueue)
		{
			ParsedRegex holeyRegex = (ParsedRegex) intermediateRep.get(regexTerm.getId()).attributes.get("regex").second();
			ParsedRegex filledRegex = null;
			try
			{
				filledRegex = holeyRegex.fillMacroHoles(grammarData); 
			}
			catch(UnsupportedOperationException ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,intermediateRep.get(regexTerm.getId()).attributes.get("regex").first(),ex.getMessage());
				continue;
			}
			grammarData.addRegex(regexTerm,filledRegex);
		}
		
		return grammarData;
	}
}
