package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

/**
 * Represents grammar symbols (terminals and nonterminals).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class GrammarSymbol extends GrammarElement
{
	/**
	 * The type of the value returned by semantic actions involving this symbol:
	 * if a terminal, on the symbol itself; if a nonterminal, on productions with
	 * the symbol on their left-hand side.
	 */
	protected String returnType;
	
	protected GrammarSymbol(CopperElementType type)
	{
		super(type);
	}

	/**
	 * @see GrammarSymbol#returnType
	 */
	public String getReturnType()
	{
		return returnType;
	}

	/**
	 * @see GrammarSymbol#returnType
	 */
	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}
}
