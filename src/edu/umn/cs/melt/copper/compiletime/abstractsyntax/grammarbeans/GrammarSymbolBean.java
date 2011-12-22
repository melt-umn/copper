package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

/**
 * Represents grammar symbols (terminals and nonterminals).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class GrammarSymbolBean extends GrammarElementBean
{
	/**
	 * The type of the value returned by semantic actions involving this symbol:
	 * if a terminal, on the symbol itself; if a nonterminal, on productions with
	 * the symbol on their left-hand side.
	 */
	protected String returnType;
	
	protected GrammarSymbolBean(CopperElementType type)
	{
		super(type);
	}

	/**
	 * @see GrammarSymbolBean#returnType
	 */
	public String getReturnType()
	{
		return returnType;
	}

	/**
	 * @see GrammarSymbolBean#returnType
	 */
	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}
}
