package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.DisambiguationFunctionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtensionGrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserAttributeBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ProductionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.OperatorClassBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalClassBean;

public interface CopperASTBeanVisitor<RT, E extends Exception>
{
	public RT visitDisambiguationFunctionBean(DisambiguationFunctionBean bean) throws E;
	public RT visitGrammarBean(GrammarBean bean) throws E;
	public RT visitExtensionGrammarBean(ExtensionGrammarBean bean) throws E;
	public RT visitNonTerminalBean(NonTerminalBean bean) throws E;
	public RT visitParserAttributeBean(ParserAttributeBean bean) throws E;
	public RT visitParserBean(ParserBean bean) throws E;
	public RT visitExtendedParserBean(ExtendedParserBean bean) throws E;
	public RT visitProductionBean(ProductionBean bean) throws E;
	public RT visitTerminalBean(TerminalBean bean) throws E;
	public RT visitTerminalClassBean(TerminalClassBean bean) throws E;
	public RT visitOperatorClassBean(OperatorClassBean bean) throws E;
}
