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
	public RT visitDisambiguationFunctionBean(DisambiguationFunction bean) throws E;
	public RT visitGrammarBean(Grammar bean) throws E;
	public RT visitExtensionGrammarBean(ExtensionGrammar bean) throws E;
	public RT visitNonTerminalBean(NonTerminal bean) throws E;
	public RT visitParserAttributeBean(ParserAttribute bean) throws E;
	public RT visitParserBean(ParserBean bean) throws E;
	public RT visitExtendedParserBean(ExtendedParserBean bean) throws E;
	public RT visitProductionBean(Production bean) throws E;
	public RT visitTerminalBean(Terminal bean) throws E;
	public RT visitTerminalClassBean(TerminalClass bean) throws E;
	public RT visitOperatorClassBean(OperatorClass bean) throws E;
}
