package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar;


/**
 * Holds types of operator precedence and associativity for terminals.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class OperatorAttributes
{
	/** No associativity. */
	public static final int ASSOC_NONE = 0;
	/** Forced nonassociativity. */
	public static final int ASSOC_NONASSOC = 1;
	/** Left associativity. */
	public static final int ASSOC_LEFT = 2;
	/** Right associativity. */
	public static final int ASSOC_RIGHT = 3;
	
	private int associativityType;
	private int operatorPrecedence;
	private TerminalClass precClass;
	
	public OperatorAttributes(int associativityType,int operatorPrecedence,TerminalClass precClass)
	throws IndexOutOfBoundsException
	{
		switch(associativityType)
		{
		case ASSOC_NONE:
		case ASSOC_NONASSOC:
		case ASSOC_LEFT:
		case ASSOC_RIGHT:
			this.associativityType = associativityType;
			this.operatorPrecedence = operatorPrecedence;
			this.precClass = precClass;
			return;
		default:
			throw new IndexOutOfBoundsException("Invalid associativity type");
		}
	}
	
	/**
	 * @return The associativity type.
	 */
	public int getAssociativityType()
	{
		return associativityType;
	}
	
	/**
	 * @return The operator precedence.
	 */
	public Integer getOperatorPrecedence()
	{
		return operatorPrecedence;
	}
	
	/**
	 * @return The operator precedence class.
	 */
	public TerminalClass getPrecClass()
	{
		return precClass;
	}
	
	public boolean equals(Object rhs)
	{
		if(rhs instanceof OperatorAttributes)
		{
			return (associativityType == ((OperatorAttributes) rhs).associativityType &&
					operatorPrecedence == ((OperatorAttributes) rhs).operatorPrecedence);
		}
		else return false;
	}
	
	public String toString()
	{
		String rv = "";
		rv += "Precedence class: " + precClass + "; Precedence: " + operatorPrecedence + "; Associativity: ";
		switch(associativityType)
		{
		case ASSOC_NONE:
			rv += "None";
			break;
		case ASSOC_LEFT:
			rv += "Left";
			break;
		case ASSOC_RIGHT:
			rv += "Right";
			break;
		default:
			rv += "INVALID";	
		}
		return rv;
	}
}
