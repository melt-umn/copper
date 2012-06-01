package edu.umn.cs.melt.copper.compiletime.checkers;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.LexicalAmbiguities;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;

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
				logConflict = logger.isLoggable(CompilerLogMessageSort.UNRESOLVED_LEXICAL_CONFLICT);
			}
			else
			{
				logConflict = logger.isLoggable(CompilerLogMessageSort.LEXICAL_CONFLICT);
			}
			
			if(logConflict)
			{
				System.err.println("Ambiguity at state" + (ambiguities.getLocations(i).cardinality() == 1 ? "" : "s") + " " + ambiguities.getLocations(i) + ":");
				System.err.println(PSSymbolTable.bitSetPrettyPrint(ambiguities.getAmbiguity(i),symbolTable,"  ",80));
			}
			if(ambiguities.isUnresolved(i))
			{
				passed = false;
				if(logConflict) System.err.println("Unresolvable");
				stats.lexicalAmbiguityCount++;
			}
			else
			{
				if(logConflict) System.err.print("Resolved by ");
				if(ambiguities.getResolution(i) == -1)
				{
					if(logConflict) System.err.println("context");
					stats.contextResolvedLexicalAmbiguityCount++;
				}
				else
				{
					if(logConflict) System.err.println("disambiguation function");
					stats.disambiguationFunctionResolvedLexicalAmbiguityCount++;
				}
			}
		}
		
		return passed;
	}	
}