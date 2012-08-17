package edu.umn.cs.melt.copper.legacy.compiletime.concretesyntax;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarName;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.StringBasedCompilerLogger;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

public class SkinParserCompiler
{
	public static int generateSkinParser(String skinName)
	{
		String skinNameCapitalized = Character.toUpperCase(skinName.charAt(0)) + skinName.substring(1);
		CompilerLogger logger = new StringBasedCompilerLogger();
		logger.setOut(System.err);
		logger.setLevel(CompilerLogMessageSort.ERROR.getLevel());
		GrammarSource grammar = null;
		ArrayList< Pair<String,Reader> > files = new ArrayList< Pair<String,Reader> >();
		try
		{
			files.add(Pair.cons(skinNameCapitalized + "SkinGrammar.txt",(Reader) new FileReader("edu/umn/cs/melt/copper/compiletime/concretesyntax/skins/" + skinName + "/" + skinNameCapitalized + "SkinGrammar.txt")));
			grammar = GrammarParser.parseGrammar(files,logger);
			grammar.addContainedGrammar(new GrammarName(FringeSymbols.STARTPRIME.getId()));
		}
		catch(Exception ex)
		{
			ex.printStackTrace(System.err);
			return 1;
		}
		
		return GrammarParserCompiler.generateParser(grammar,"edu.umn.cs.melt.copper.compiletime.concretesyntax.skins." + skinName,skinNameCapitalized + "SkinParser",logger);
	}
	
	public static void main(String[] args)
	{
		if(args.length != 1)
		{
			System.err.println("Usage: SkinParserCompiler [skin name]");
			System.exit(1);
		}
		int errorlevel = generateSkinParser(args[0]);
		System.exit(errorlevel);
	}
}
