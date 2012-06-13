package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.text.ParseException;

import edu.umn.cs.melt.copper.runtime.io.Location;

/**
 * Holds a reference to a Copper grammar element. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class CopperElementReference implements Comparable<CopperElementReference>
{
	/**
	 * The grammar in which the element being referred to is located.
	 */
	protected CopperElementName grammarName;
	
	/**
	 * The name of the element being referred to. If <code>grammarName</code> is set to null,
	 * it will be resolved within the scope of the grammar in which the reference appears.
	 */
	protected CopperElementName name;
	
	/**
	 * The location of the reference.
	 */
	protected Location location;
	
	protected CopperElementReference(CopperElementName name,Location location)
	{
		this(null,name,location);
	}
	
	protected CopperElementReference(CopperElementName grammarName,CopperElementName name,Location location)
	{
		this.grammarName = grammarName;
		this.name = name;
		this.location = location;
	}
	
	/**
	 * Creates a reference from an existing Copper name object.
	 * @param name The name to refer to.
	 * @param location The location of the reference.
	 */
	public static CopperElementReference ref(CopperElementName name,Location location)
	{
		return new CopperElementReference(name,location);
	}
	    
	/**
	 * Creates a reference with a name object built from a given string parameter.
     * @param name The string representation of the name. This may be a string
     * matching the regex <code>[A-Za-z_][A-Za-z0-9_]*([.][A-Za-z_][A-Za-z0-9_]*)*</code>,
     * in which case it will be made into a local reference; or it may be two such strings
     * separated by a colon, in which case it will be made into a fully-qualified reference.
	 * @param location The location of the reference.
	 * @throws ParseException If the string <code>name</code> is not a valid name.
	 */
	public static CopperElementReference ref(String name,Location location)
	throws ParseException
	{
    	String[] components = name.split(":");
		if(components.length == 1) return new CopperElementReference(CopperElementName.newName(name),location);
		else if(components.length == 2) return ref(CopperElementName.newName(components[0]),components[1],location);
    	else throw new ParseException("Invalid name for a Copper element: " + name,0);
	}
	
	/**
	 * Creates a reference with a fully-qualified name object built from a given grammar
	 * name and string parameter. 
	 * @param grammarName The name of a grammar.
	 * @param localName A string representing a grammar element name.
	 * @param location The location of the reference.
	 * @throws ParseException If the string <code>localName</code> is not a valid element name.
	 */
	public static CopperElementReference ref(CopperElementName grammarName,String localName,Location location)
	throws ParseException
	{
		return new CopperElementReference(grammarName,CopperElementName.newName(localName),location);
	}

	/**
	 * Creates a reference with a fully-qualified name object built from a given grammar
	 * name and string parameter. 
	 * @param grammarName The name of a grammar.
	 * @param localName The name of an element.
	 * @param location The location of the reference.
	 */
	public static CopperElementReference ref(CopperElementName grammarName,CopperElementName localName,Location location)
	{
		return new CopperElementReference(grammarName,localName,location);
	}

	/**
	 * @see CopperElementReference#grammarName
	 */
	public CopperElementName getGrammarName()
	{
		return grammarName;
	}

	/**
	 * @see CopperElementReference#name
	 */
	public CopperElementName getName()
	{
		return name;
	}

	/**
	 * @see CopperElementReference#location
	 */
	public Location getLocation()
	{
		return location;
	}
	
	/**
	 * Determines whether this reference is fully qualified (contains an explicit grammar name).
	 */
	public boolean isFQ()
	{
		return (grammarName != null);
	}
	
	@Override
	public int hashCode()
	{
		return (grammarName == null) ? name.hashCode() : name.hashCode() + grammarName.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return (o instanceof CopperElementReference) &&
		        ((grammarName != null && grammarName.equals(((CopperElementReference) o).grammarName)) || grammarName == null && ((CopperElementReference) o).grammarName == null) &&
		        name.equals(((CopperElementReference) o).name); 
	}
	
	public String toString()
	{
		if(grammarName == null) return name.toString();
		else return grammarName + ":" + name;
	}

	@Override
	public int compareTo(CopperElementReference o)
	{
		int gC = grammarName.compareTo(o.grammarName);
		if(gC != 0) return gC;
		else return name.compareTo(o.name);
	}
}
