package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar;

import java.util.Hashtable;

/**
 * A class to store string representations of symbols of all sorts, replacing them
 * with integers for storage.
 */
public class Symbol implements Comparable<Symbol>
{
    private int tag;

    private static int nextTag = 0;
    private static Hashtable<Integer,String> names = new Hashtable<Integer,String>();
    private static Hashtable<String,Integer> tags = new Hashtable<String,Integer>();

    /** Creates a new instance of Symbol.
     * @param name The string representation of the Symbol.
     * @return The new Symbol.
     */    
    public static Symbol symbol(String name)
    {
    	return new Symbol(name);
    }
    
    protected Symbol(String name)
    {
    	if(tags.containsKey(name)) tag = tags.get(name);
    	else
    	{
    		tag = nextTag++;
    		names.put(tag,name);
    		tags.put(name,tag);
    	}
    }

    /**
     * Gets the string representation of this Symbol.
     * @return The string representation.
     */    
    public String getName() { return names.get(tag); }
    public String toString() { return getName(); }

    public int compareTo(Symbol rhs)
    {
    	if(rhs instanceof Symbol) return tag - ((Symbol) rhs).tag;
    	else return toString().compareTo(rhs.toString());
    }

    public boolean equals(Object rhs)
    {
	if(rhs instanceof Symbol) return (compareTo((Symbol) rhs) == 0);
	else return false;
    }

    public int hashCode() { return tag; }
}
