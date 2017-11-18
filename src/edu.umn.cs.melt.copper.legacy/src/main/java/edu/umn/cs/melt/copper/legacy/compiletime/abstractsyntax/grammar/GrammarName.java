package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar;



public class GrammarName
{
    private Symbol name;

    public GrammarName(String nid)
    {
    	name = Symbol.symbol(nid);
    }

    public GrammarName(Symbol nid)
    {
    	name = nid;
    }    

    public String toString()
    {
		String rv = name.toString();
		return rv;
    }

    public boolean equals(Object rhs)
    {
		if(rhs instanceof GrammarName) return name.equals(((GrammarName) rhs).name);
		else return false;
    }

    public int hashCode() { return name.hashCode(); }

    public Symbol getName()
	{
		return name;
	}
}
