package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

/**
 * Represents elements that belong to grammars (terminals, nonterminals, etc.)
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class GrammarElement extends CopperASTBean
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7002650799932829601L;

	protected GrammarElement(CopperElementType type)
	{
		super(type);
	}
}
