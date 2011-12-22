package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents a nonterminal symbol.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class NonTerminalBean extends GrammarSymbolBean
{
	public NonTerminalBean()
	{
		super(CopperElementType.NON_TERMINAL);
	}
	
	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitNonTerminalBean(this);
	}
}
