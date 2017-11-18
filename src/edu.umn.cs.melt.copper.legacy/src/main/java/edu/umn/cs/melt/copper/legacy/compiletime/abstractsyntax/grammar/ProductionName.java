package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar;

public class ProductionName extends ClassifiedSymbol
{
	public ProductionName(String pid)
	{
		id = Symbol.symbol(pid);
	}
	
	public ProductionName(Symbol pid)
	{
		id = pid;
	}

	public int hashCode()
	{
		return id.hashCode();
	}

    public boolean equals(Object rhs)
    {
		if(rhs != null && rhs instanceof ProductionName) return id.equals(((ProductionName) rhs).id);
		else return false;
    }

    public String toString()
    {
		String rv = id.toString();
		return rv;
    }

}
