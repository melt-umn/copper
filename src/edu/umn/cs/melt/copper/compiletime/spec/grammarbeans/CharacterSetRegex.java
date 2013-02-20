package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

import java.util.HashSet;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.RegexBeanVisitor;

/**
 * Represents the base regex: a set of characters.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class CharacterSetRegex extends Regex
{
	public static CharacterSetRegex union(CharacterSetRegex left,CharacterSetRegex right)
	{
		CharacterSetRegex rv = new CharacterSetRegex();
		rv.chars = SetOfCharsSyntax.union(left.chars,right.chars);
		return rv;
	}
	
	private SetOfCharsSyntax chars;
	
	/**
	 * Constructs an empty character set.
	 */
	public CharacterSetRegex()
	{
		chars = new SetOfCharsSyntax();
	}
	
	/**
	 * If this character set contains only one character, return that character.
	 * @throws ArrayIndexOutOfBoundsException If the character set does not contain exactly one character.
	 */
	public char getOnlyChar()
	{
		if(cardinality() == 1) return chars.getMembers()[0][0];
		else throw new ArrayIndexOutOfBoundsException();
	}
	
	/**
	 * Alters this character set to add a range of characters.
	 * @param lowerBound The lower bound of the new set of characters.
	 * @param upperBound The upper bound of the new set of characters.
	 * @return {@code this} (to enable chaining of mutator calls).
	 */
	public CharacterSetRegex addRange(char lowerBound,char upperBound)
	{
		SetOfCharsSyntax rhs = new SetOfCharsSyntax(lowerBound,upperBound);
		chars = SetOfCharsSyntax.union(chars,rhs);
		return this;
	}
	
	/**
	 * Alters this character set to add a single character.
	 * @param c The character to add.
	 * @return {@code this} (to enable chaining of mutator calls).
	 */
	public CharacterSetRegex addLooseChar(char c)
	{
		SetOfCharsSyntax rhs = new SetOfCharsSyntax(c,c);
		chars = SetOfCharsSyntax.union(chars,rhs);
		return this;
	}
	
	/**
	 * Alters this character set to turn it into its complement.
	 * @return {@code this} (to enable chaining of mutator calls).
	 */
	public CharacterSetRegex invert()
	{
		chars = chars.invert();
		return this;
	}
	
	@Override
	public boolean isComplete()
	{
		return !chars.isEmpty();
	}
	
	/**
	 * Returns the total number of characters in the character set.
	 */
	public int cardinality()
	{
		return chars.size();
	}

	@Override
	public Set<String> whatIsMissing()
	{
		Set<String> rv = new HashSet<String>();
		if(chars.isEmpty()) rv.add("characters");
		return rv;
	}

	@Override
	public <RT, E extends Exception> RT acceptVisitor(RegexBeanVisitor<RT, E> visitor)
	throws E
	{
		return visitor.visitCharacterSetRegex(this,chars);
	}

}
