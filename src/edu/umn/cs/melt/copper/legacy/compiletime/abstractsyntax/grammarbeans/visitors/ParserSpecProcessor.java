package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;

public class ParserSpecProcessor {

	// TODO: Remove this when the other skins can produce ParserBeans instead of GrammarSource objects.
	public static GrammarSource buildGrammarSource(ParserBean spec,CompilerLogger logger)
	{
		GrammarSourceBuilder builder = new GrammarSourceBuilder();
		builder.visitParserBean(spec);
		return builder.getGrammar();
	}

}
