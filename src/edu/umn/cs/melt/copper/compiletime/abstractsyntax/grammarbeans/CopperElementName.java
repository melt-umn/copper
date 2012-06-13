package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.text.ParseException;
import java.util.regex.Pattern;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol;

/**
 * Represents the name of a CopperASTBean.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class CopperElementName implements Comparable<CopperElementName>
{
	private static final Pattern validNames = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
	
	// TODO: Remove this class's dependence on grammar.Symbol.
    private Symbol symbol;

    protected CopperElementName(String name)
    {
    	symbol = Symbol.symbol(name);
    }
    
    /**
     * Creates a new local element name. Names must match the regular expression {@code [A-Za-z_][A-Za-z0-9_]*}.
     * @param name The string representation of the name.
     * @return The new name.
     * @throws ParseException If the given name is invalid for a Copper element.
     */
    public static CopperElementName newName(String name)
    throws ParseException
    {
    	if(!validNames.matcher(name).matches()) throw new ParseException("Invalid name for a Copper element: " + name,0);
    	return new CopperElementName(name);    	
    }
    
    @Override
    public boolean equals(Object o)
    {
    	return (o instanceof CopperElementName) && symbol.equals(((CopperElementName) o).symbol);
    }
    
    @Override
    public int compareTo(CopperElementName n)
    {
    	if(n instanceof CopperElementName) return symbol.toString().compareTo(((CopperElementName) n).symbol.toString());
    	else return -1;
    }
        
    @Override
    public int hashCode()
    {
    	return symbol.hashCode();
    }

    @Override
    public String toString()
    {
    	return symbol.toString();
    }
}
