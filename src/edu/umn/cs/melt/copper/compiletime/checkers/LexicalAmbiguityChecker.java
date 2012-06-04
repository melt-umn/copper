package edu.umn.cs.melt.copper.compiletime.checkers;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.LexicalAmbiguities;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.loggingnew.messages.LexicalAmbiguityMessage;

/**
 * Counts the lexical ambiguities in a compiled parser specification, and checks if they have all been resolved. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LexicalAmbiguityChecker
{
	private CompilerLogger logger;
	private SymbolTable<CopperASTBean> symbolTable;
	private LexicalAmbiguities ambiguities;
	private GrammarStatistics stats;
	
	private LexicalAmbiguityChecker(CompilerLogger logger,SymbolTable<CopperASTBean> symbolTable,LexicalAmbiguities ambiguities,GrammarStatistics stats)
	{
		this.logger = logger;
		this.symbolTable = symbolTable;
		this.ambiguities = ambiguities;
		this.stats = stats;
	}
	
	public static boolean check(CompilerLogger logger,SymbolTable<CopperASTBean> symbolTable,LexicalAmbiguities ambiguities,GrammarStatistics stats)
	{
		return new LexicalAmbiguityChecker(logger, symbolTable, ambiguities, stats).checkLexicalAmbiguities();
	}
	
	private boolean checkLexicalAmbiguities()
	{
		boolean passed = true;
		
		stats.lexicalAmbiguityCount = ambiguities.size();
		
		for(int i = 0;i < ambiguities.size();i++)
		{
			boolean logConflict;
			if(ambiguities.isUnresolved(i))
			{
				passed = false;
				logConflict = logger.isLoggable(CompilerLevel.QUIET);
			}
			else
			{
				logConflict = logger.isLoggable(CompilerLevel.VERBOSE);
			}
			
			if(logConflict)
			{
				logger.log(new LexicalAmbiguityMessage(symbolTable, ambiguities, i));
			}
			if(ambiguities.isUnresolved(i))
			{
				passed = false;
				stats.lexicalAmbiguityCount++;
			}
			else
			{
				if(ambiguities.getResolution(i) == -1)
				{
					stats.contextResolvedLexicalAmbiguityCount++;
				}
				else
				{
					stats.disambiguationFunctionResolvedLexicalAmbiguityCount++;
				}
			}
		}
		
		return passed;
	}	
}