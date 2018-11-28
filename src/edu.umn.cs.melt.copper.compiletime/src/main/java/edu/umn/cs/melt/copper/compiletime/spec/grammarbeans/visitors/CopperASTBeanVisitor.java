package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.DisambiguationFunction;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ExtensionGrammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Grammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.OperatorClass;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Production;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Terminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.TerminalClass;

public interface CopperASTBeanVisitor<RT, E extends Exception>
{
	public RT visitDisambiguationFunction(DisambiguationFunction bean) throws E;
	public RT visitGrammar(Grammar bean) throws E;
	public RT visitExtensionGrammar(ExtensionGrammar bean) throws E;
	public RT visitNonTerminal(NonTerminal bean) throws E;
	public RT visitParserAttribute(ParserAttribute bean) throws E;
	public RT visitParserBean(ParserBean bean) throws E;
	public RT visitExtendedParserBean(ExtendedParserBean bean) throws E;
	public RT visitProduction(Production bean) throws E;
	public RT visitTerminal(Terminal bean) throws E;
	public RT visitTerminalClass(TerminalClass bean) throws E;
	public RT visitOperatorClass(OperatorClass bean) throws E;
}
