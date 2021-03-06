package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * This class contains methods to prepare a Copper specification for compilation by checking
 * its consistency and converting it into a normal form. These methods are called automatically
 * by {@link ParserCompiler#compile(ParserBean, edu.umn.cs.melt.copper.main.ParserCompilerParameters)}.
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 */
public class ParserSpecProcessor
{
	/**
	 * Checks that a Copper specification is well-formed and converts it into normal form.
	 * @param spec The specification to check and convert.
	 * @param logger The logger to which to report errors.
	 * @throws CopperException If any consistency errors occurred.
	 */
	public static boolean normalizeParser(ParserBean spec,CompilerLogger logger)
	throws CopperException
	{
		boolean hasError = false;
		GrammarConsistencyChecker checker = new GrammarConsistencyChecker();
		if(spec != null) hasError |= spec.acceptVisitor(checker);
		else hasError = true;
		if(hasError && logger.isLoggable(GrammarSemanticErrorMessage.getGrammarSemanticErrorMessageLevel()))
		{
			for(GrammarError e : checker.getErrors())
			{
				logger.log(new GrammarSemanticErrorMessage(e));
			}
//			StringBuffer sb = new StringBuffer();
//			sb.append("Parser specification").append(spec == null ? "" : " " + spec.getDisplayName()).append(" is not well-formed; the following errors occurred:\n");
//			for(GrammarError e : checker.getErrors())
//			{
//				sb.append("    ").append(e.toString()).append("\n");
//			}
//			if(logger.isLoggable(CompilerLogMessageSort.PARSING_ERROR)) logger.logMessage(CompilerLogMessageSort.PARSING_ERROR,null,sb.toString());
//			logger.flushMessages();
			logger.flush();
			return true;
		}
		GrammarNormalizer normalizer = new GrammarNormalizer();
		if(spec != null) spec.acceptVisitor(normalizer);
		return hasError;
	}
}
