package edu.umn.cs.melt.copper.legacy.compiletime.concretesyntax;



import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.lalr1.LALR1DFABuilder;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.StringBasedCompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.lalr.LALREngineBuilder;
import edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.lalr.MainFunctionBuilders;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;



/**
 * Compiles the class GrammarParser from scratch.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class GrammarParserCompiler
{
	public static int generateParser(GrammarSource grammar,String parserPackage,String parserClassName,CompilerLogger logger)
	{
		LALR1DFABuilder builder = new LALR1DFABuilder(grammar,grammar.getContainedGrammars(),logger);
		try
		{
			grammar.getContextSets().compute(grammar,grammar.getContainedGrammars(),logger);
			builder.buildDFA();
			//System.err.println(builder.getGrammar());
			//System.err.println(builder.getLR0DFA());
			builder.LALRize();
			//System.err.println(builder.getLALR1DFA());
			builder.buildLALR1Table();
			//System.err.println(builder.getParseTable().prettyPrint());
		}
		catch(CopperException ex)
		{
			ex.printStackTrace(System.err);
			return 1;
		}
		LALREngineBuilder engineBuilder = new LALREngineBuilder(builder.getGrammar(),builder.getLALR1DFA(),builder.getParseTable(),logger);
		String ancillaries = MainFunctionBuilders.buildGrammarParserAncillaries(parserPackage + "." + parserClassName);
		try
		{
			engineBuilder.buildLALREngine(System.out,
					       "package " + parserPackage + ";",
				           "",
				           parserClassName,parserClassName + "Scanner",
				           ancillaries,
				           "");
		}
		catch(CopperException ex)
		{
			ex.printStackTrace(System.err);
			return 1;
		}		
		
		return 0;
	}
	
	public static int generateNativeParser()
	{
		//System.out.println(grammar);
	
		CompilerLogger logger = new StringBasedCompilerLogger();
		logger.setLevel(CompilerLogMessageSort.ERROR.getLevel());
		return generateParser(GrammarConcreteSyntax.grammar,"edu.umn.cs.melt.copper.compiletime.concretesyntax","GrammarParser",logger);
		/*HashSet<GrammarName> grammarNames = new HashSet<GrammarName>();
		grammarNames.add(new GrammarName(FringeSymbols.STARTPRIME.getId()));
		grammarNames.add(new GrammarName("GrammarGrammar"));*/
		//System.err.println(grammar.getContainedGrammars());
	}
	
	public static void main(String[] args)
	{
		int errorlevel = generateNativeParser();
		System.exit(errorlevel);
	}
}
