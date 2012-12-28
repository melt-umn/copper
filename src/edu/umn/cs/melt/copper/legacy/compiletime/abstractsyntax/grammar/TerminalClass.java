package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar;

public class TerminalClass extends ClassifiedSymbol
{
    /** Creates a new terminal class symbol.
     * @param nid The string representation of the symbol.
     */    
    public TerminalClass(String nid)
    {
    	id = Symbol.symbol(nid);
    }

    /** Creates a new terminal class symbol.
     * @param nid The symbol of the class.
     */    
    public TerminalClass(Symbol nid)
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
		if(rhs instanceof TerminalClass) return id.equals(((TerminalClass) rhs).id);
		else return false;
    }

    public int hashCode() { return id.hashCode(); }
}
