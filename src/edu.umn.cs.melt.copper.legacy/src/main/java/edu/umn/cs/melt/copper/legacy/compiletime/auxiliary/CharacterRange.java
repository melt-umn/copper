package edu.umn.cs.melt.copper.legacy.compiletime.auxiliary;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;


/* (non-Javadoc)
 * Holds a range of characters, inclusive.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class CharacterRange implements Comparable<CharacterRange>
{
	Pair<Character,Character> range;
	
	/* (non-Javadoc)
	 * Constructs a CharacterRange of a single character.
	 * @param singleChar The single character.
	 */
	public CharacterRange(char singleChar)
	{
		range = Pair.cons(singleChar,singleChar);
	}
	
	/* (non-Javadoc)
	 * Constructs a CharacterRange of several characters.
	 * @param least The least-numbered character in the range (inclusive).
	 * @param greatest The greatest-numbered character in the range (inclusive).
	 */
	public CharacterRange(char least,char greatest)
	{
		range = Pair.cons(least,greatest);
	}
	
	/* (non-Javadoc)
	 * Gets the least-numbered character in the range.
	 * @return The least-numbered character in the range.
	 */
	public char firstChar()
	{
		return range.first();
	}
	
	public char charValue() { return firstChar(); }
	
	/* (non-Javadoc)
	 * Gets the greatest-numbered character in the range.
	 * @return The greatest-numbered character in the range.
	 */
	public char lastChar()
	{
		return range.second();
	}
	
	/* (non-Javadoc)
	 * Tests if a given character is in the range.
	 * @param toTest The character to test.
	 * @return <CODE>true</CODE> iff firstChar() &lt;= toTest &lt;= secondChar(). 
	 */
	public boolean isInRange(char toTest)
	{
		return (toTest >= range.first() && toTest <= range.second());
	}
	
	/* (non-Javadoc)
	 * Determines if another range falls entirely or in part within this range.
	 * @param otherRange The other range.
	 * @return <CODE>true</CODE> iff there exists a character c, such that this.isInRange(c) && otherRange.isInRange(c).
	 */
	public boolean contains(CharacterRange otherRange)
	{
		int f1 = firstChar(),f2 = otherRange.firstChar();
		int l1 = lastChar(),l2 = otherRange.lastChar();
		return (f1 <= l2 && f2 <= l1);
	}
	
	/* (non-Javadoc)
	 * Tests whether a range is of a single character.
	 * @return <CODE>true</CODE> iff firstChar() == secondChar().
	 */
	public boolean isSingleChar()
	{
		return (range.first() == range.second());
	}
	
	public boolean equals(Object rhs)
	{
		if(rhs == null || !(rhs instanceof CharacterRange)) return false;
		return (range.first() == ((CharacterRange) rhs).range.first() &&
				range.second() == ((CharacterRange) rhs).range.second());
	}
	
	public int hashCode()
	{
		if(isSingleChar()) return (int)firstChar();
		return range.hashCode();
	}
	
	public int compareTo(CharacterRange rhs)
	{
		int firstDiff = range.first() - rhs.range.first();
		if(firstDiff < 0) return firstDiff - 1;
		else if(firstDiff > 0) return firstDiff + 1;
		else if(size() < rhs.size()) return -1;
		else if(size() > rhs.size()) return 1;
		else /* if(firstDiff == 0 && size() == rhs.size()) */  return 0; 
	}
	
	public int size()
	{
		return range.second() - range.first();
	}
	
	public String toString()
	{
		if(isSingleChar()) return "[" + range.first() + "]";
		else return "[" + range.first() + "-" + range.second() + "]";
	}
	
	/* (non-Javadoc)
	 * Takes an iterable of character ranges and "consolidates" adjacent ones.
	 * @param origSet The iterable of ranges.
	 * @return A set of ranges covering the same characters, but with adjacent ranges consolidated, i.e., [a-c],[d],[e],[i],[j-m] -> [a-e],[i-m].
	 */
	public static HashSet<CharacterRange> consolidateAdjacentRanges(Iterable<CharacterRange> origSet)
	{
		TreeSet<CharacterRange> original = new TreeSet<CharacterRange>();
		HashSet<CharacterRange> rv = new HashSet<CharacterRange>();
		for(CharacterRange cr : origSet) original.add(cr);
		CharacterRange current = null;
		for(CharacterRange cr : original)
		{
			if(current == null) current = cr;
			else if(cr.contains(current) || current.lastChar() + 1 == cr.firstChar())
			{
				current = new CharacterRange((char) Math.min(current.firstChar(),cr.firstChar()),
						                     (char) Math.max(current.lastChar(),cr.lastChar()));
			}
			else
			{
				rv.add(current);
				current = cr;
			}
		}
		rv.add(current);
		return rv;
	}
	
	// Designations for extrema of overlapping ranges: whether they begin a range,
	// end a range, or both.
	private static final int BEGINS = 1,ENDS = 2,BOTH = 3;

	/* (non-Javadoc)
	 * Takes an iterable of character ranges, possibly overlapping, and returns another
	 * set of character ranges, covering the same characters without overlapping.
	 * @param origSet The set of ranges.
	 * @return The non-overlapping set of ranges, each containing a mapping to the set of ranges they touch in origSet.
	 */
	public static Hashtable< CharacterRange,HashSet<CharacterRange> > eliminateOverlaps(Iterable<CharacterRange> origSet)
	{
		// The sorted set of extrema.
		TreeSet<Character> sortedExtrema = new TreeSet<Character>();
		// The map of designations for extrema.
		Hashtable<Character,Integer> extremumInfo = new Hashtable<Character,Integer>();
		Hashtable< CharacterRange,HashSet<CharacterRange> > rv = new Hashtable< CharacterRange,HashSet<CharacterRange> >();
		// For every range in the input set:
		for(CharacterRange cr : origSet)
		{
			// Add its extrema to the sorted-set.
			sortedExtrema.add(cr.firstChar());
			sortedExtrema.add(cr.lastChar());
			// Add its information to the info map.
			if(!extremumInfo.containsKey(cr.firstChar())) extremumInfo.put(cr.firstChar(),BEGINS);
			else if(extremumInfo.get(cr.firstChar()) == ENDS) extremumInfo.put(cr.firstChar(),BOTH);
			if(!extremumInfo.containsKey(cr.lastChar())) extremumInfo.put(cr.lastChar(),ENDS);
			else if(extremumInfo.get(cr.lastChar()) == BEGINS) extremumInfo.put(cr.lastChar(),BOTH);
		}
		// DEBUG-X-BEGIN
		//System.out.println("\nSorted extrema: " + sortedExtrema + "; -- Extrema: " + extremumInfo);
		// DEBUG-X-END
		if(sortedExtrema.isEmpty()) return rv;
		Iterator<Character> it = sortedExtrema.iterator();
		char prev = it.next();
		if(extremumInfo.get(prev) == BOTH)
		{
			CharacterRange soleChar = new CharacterRange(prev);
			for(CharacterRange cr : origSet)
			{
				if(cr.contains(soleChar))
				{
					if(!rv.containsKey(soleChar)) rv.put(soleChar,new HashSet<CharacterRange>());
					rv.get(soleChar).add(cr);
				}
			}
		}
		for(;it.hasNext();)
		{
			char central = it.next();
			char prevFirst,prevLast;
			CharacterRange cr0 = null;
			CharacterRange crN = null;
			if(extremumInfo.get(prev) == BEGINS) prevFirst = prev;
			else prevFirst = (char)(prev + 1);
			if(extremumInfo.get(central) == ENDS) prevLast = central;
			else prevLast = (char)(central - 1);
			if(extremumInfo.get(central) == BOTH) crN = new CharacterRange(central);
			if(prevFirst <= prevLast) cr0 = new CharacterRange(prevFirst,prevLast);
			for(CharacterRange cr : origSet)
			{
				if(cr0 != null && cr.contains(cr0))
				{
					if(!rv.containsKey(cr0)) rv.put(cr0,new HashSet<CharacterRange>());
					rv.get(cr0).add(cr);
				}
				if(crN != null && cr.contains(crN))
				{
					if(!rv.containsKey(crN)) rv.put(crN,new HashSet<CharacterRange>());
					rv.get(crN).add(cr);
				}
			}
			prev = central;
        }
		return rv;
	}
		
	/* (non-Javadoc)
	 * "Rangifies" an array of characters: sorts them and forms them into the
	 * smallest possible set of contiguous blocks.
	 * @param characters The characters.
	 * @return A set of character ranges specifying the loose characters in the array.
	 */
	public static HashSet<CharacterRange> rangify(char... characters)
	{
		HashSet<CharacterRange> rv = new HashSet<CharacterRange>();
		if(characters.length == 0) return rv;
		TreeSet<Character> sortedChars = new TreeSet<Character>();
		for(char c : characters) sortedChars.add(c);
		char rangeStartChar = characters[0];
		char previousChar = 0;
		char currentChar = 1;
		for(char c : sortedChars)
		{
			currentChar = c;
			if(rangeStartChar <= previousChar && previousChar + 1 != currentChar)
			{
				rv.add(new CharacterRange(rangeStartChar,previousChar));
				rangeStartChar = currentChar;
			}
			previousChar = currentChar;
			
		}
		rv.add(new CharacterRange(rangeStartChar,currentChar));
		return rv;
	}
	
	/* (non-Javadoc)
	 * "Rangifies" an iterable of characters: sorts them and forms them into the
	 * smallest possible set of contiguous blocks.
	 * @param characters The characters.
	 * @return A set of character ranges specifying the loose characters in the iterable.
	 */
	public static HashSet<CharacterRange> rangify(Iterable<Character> characters)
	{
		HashSet<CharacterRange> rv = new HashSet<CharacterRange>();
		if(!characters.iterator().hasNext()) return rv;
		TreeSet<Character> sortedChars = new TreeSet<Character>();
		for(char c : characters) sortedChars.add(c);
		char rangeStartChar = characters.iterator().next();
		char previousChar = 0;
		char currentChar = 1;
		for(char c : sortedChars)
		{
			currentChar = c;
			if(rangeStartChar <= previousChar && previousChar + 1 != currentChar)
			{
				rv.add(new CharacterRange(rangeStartChar,previousChar));
				rangeStartChar = currentChar;
			}
			previousChar = currentChar;
			
		}
		rv.add(new CharacterRange(rangeStartChar,currentChar));
		return rv;
	}
}
