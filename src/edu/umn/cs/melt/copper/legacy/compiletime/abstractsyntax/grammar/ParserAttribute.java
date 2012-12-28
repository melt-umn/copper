package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar;


/**
 * Represents a "parser attribute," a specialization of a threaded
 * attribute.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class ParserAttribute
{
	private Symbol name;
	private String type;
	private String initCode;
	
	public ParserAttribute(Symbol name, String type, String initCode)
	{
		this.name = name;
		this.type = type;
		this.initCode = initCode;
	}

	public String getInitCode()
	{
		return initCode;
	}

	public Symbol getName()
	{
		return name;
	}

	public String getType()
	{
		return type;
	}
	
	public boolean equals(Object rhs)
	{
		if(rhs != null && rhs instanceof ParserAttribute)
		{
			return name.equals(((ParserAttribute) rhs).name);
		}
		else return false;
	}
	
	public int hashCode()
	{
		return name.hashCode();
	}
	
	public String toString()
	{
		return "(Parser attribute " + type + " " + name + "; {" + initCode + "})";
	}
}
