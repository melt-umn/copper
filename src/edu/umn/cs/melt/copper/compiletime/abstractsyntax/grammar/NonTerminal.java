package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar;


public class NonTerminal extends GrammarSymbol
{
    public NonTerminal(String nid)
    {
    	id = Symbol.symbol(nid);
    }

    public NonTerminal(Symbol nid)
    {
    	id = nid;
    }    

    public String toString()
    {
		String rv = id.toString();
		return rv;
    }

    public boolean equals(Object rhs)
    {
		if(rhs instanceof NonTerminal) return id.equals(((NonTerminal) rhs).id);
		else return false;
    }

    public int hashCode() { return id.hashCode(); }
}
