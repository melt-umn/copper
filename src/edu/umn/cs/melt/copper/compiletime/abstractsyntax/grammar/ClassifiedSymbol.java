package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar;

public abstract class ClassifiedSymbol
{
    protected Symbol id;

    public abstract int hashCode();
    public abstract boolean equals(Object rhs);
    
	public Symbol getId()
	{
		return id;
	}
}
