package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;
import edu.umn.cs.melt.copper.runtime.io.Location;

/**
 * The superclass of all Copper grammar objects.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class CopperASTBean
{
	/** The grammar object's type. */
	protected CopperElementType type;
	/** The grammar object's name. */
	protected CopperElementName name;
	/** The "display name" of the grammar object, as distinct from the fully unambiguous internal name used by the parser generator. */
	protected String displayName;
	/** The location of this grammar object (generally containing filename, line, and column number). */
	protected Location location;
	
	protected CopperASTBean(CopperElementType type)
	{
		this.type = type;
		this.name = null;
		this.location = null;
		this.displayName = null;
	}

	/**
	 * @see CopperASTBean#type
	 */
	public CopperElementType getType()
	{
		return type;
	}
	
	/**
	 * @see CopperASTBean#name
	 */
	public CopperElementName getName()
	{
		return name;
	}

	/**
	 * @see CopperASTBean#name
	 */
	public void setName(CopperElementName name)
	{
		if(this.name == null) this.name = name;
	}
	
	/**
	 * @see CopperASTBean#name
	 */
	public void setName(String name)
	throws ParseException
	{
		if(this.name == null) this.name = CopperElementName.newName(name);
	}
	

	/**
	 * @see CopperASTBean#displayName
	 */
	public String getDisplayName()
	{
		if(displayName != null) return displayName;
		else if(name != null) return name.toString();
		else return "null";
	}
	
	/**
	 * @see CopperASTBean#displayName
	 */
	public boolean hasDisplayName()
	{
		return displayName != null;
	}

	/**
	 * @see CopperASTBean#displayName
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	/**
	 * @see CopperASTBean#location
	 */
	public Location getLocation()
	{
		return location;
	}

	/**
	 * @see CopperASTBean#location
	 */
	public void setLocation(Location location)
	{
		if(this.location == null) this.location = location;
	}

	/**
	 * Checks that a grammar object has had values assigned to all its required elements and parameters.
	 * All objects require the fields <code>name</code> and <code>location</code> to be set, and may impose
	 * additional individual requirements.
	 * @return <code>true</code> iff all required elements and parameters have been set to non-null values.
	 */
	public boolean isComplete()
	{
		return (name != null && location != null);
	}
	/**
	 * @return A set containing the required elements or parameters that are missing in this grammar object.
	 */
	public Set<String> whatIsMissing()
	{
		Set<String> rv = new HashSet<String>();
		if(name == null) rv.add("name");
		if(location == null) rv.add("location");
		return rv;
	}
	
	public abstract <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor) throws E;

}
