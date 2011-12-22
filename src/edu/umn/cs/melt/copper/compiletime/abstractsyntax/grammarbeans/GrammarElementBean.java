package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

/**
 * Represents elements that belong to grammars (terminals, nonterminals, etc.)
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class GrammarElementBean extends CopperASTBean
{
	protected GrammarElementBean(CopperElementType type)
	{
		super(type);
	}
}
