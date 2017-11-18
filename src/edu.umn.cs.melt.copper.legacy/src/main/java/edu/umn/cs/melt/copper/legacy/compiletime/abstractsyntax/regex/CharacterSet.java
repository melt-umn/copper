package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;

/**
 * Represents the base regex: a set of characters.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class CharacterSet extends ParsedRegex
{
	/** Indicates to the constructor that the given characters are "loose" (not defining anything else). */
	public static final int LOOSE_CHARACTERS = 0;
	/** Indicates to the constructor that the given characters are triples defining character ranges. */
	public static final int RANGES = 1;
	
	/**
	 * Unions two CharacterSet regexes.
	 * @param x Any CharacterSet regex.
	 * @param y Any CharacterSet regex.
	 * @return A CharacterSet with characters <CODE>union(x.getChars(),y.getChars())</CODE>.
	 */
	public static CharacterSet union(CharacterSet x,CharacterSet y)
	{
		if(x instanceof OldCharacterSet && y instanceof OldCharacterSet)
		{
			return OldCharacterSet.union((OldCharacterSet) x,(OldCharacterSet) y);
		}
		else if(x instanceof NewCharacterSet && y instanceof NewCharacterSet)
		{
			return NewCharacterSet.union((NewCharacterSet) x,(NewCharacterSet) y);
		}
		else return null;
	}
	
	public abstract CharacterSet invertSet();
	public abstract char getFirstChar();
	public abstract int size();
	
	public static CharacterSet instantiate(int type,char... characters)
	{
		// To switch between scanner generation engines, change this line.
//		return new OldCharacterSet(type,characters);
		return new NewCharacterSet(type,characters);
	}
	
	public static CharacterSet instantiate(SetOfCharsSyntax chars)
	{
		return new NewCharacterSet(chars);
	}
}
