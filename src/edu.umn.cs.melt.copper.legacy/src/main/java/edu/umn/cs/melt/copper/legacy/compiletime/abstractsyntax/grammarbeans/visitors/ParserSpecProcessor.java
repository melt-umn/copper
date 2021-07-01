package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;

public class ParserSpecProcessor {

	public static GrammarSource buildGrammarSource(ParserBean spec,CompilerLogger logger)
	{
		GrammarSourceBuilder builder = new GrammarSourceBuilder();
		builder.visitParserBean(spec);
		return builder.getGrammar();
	}

}
