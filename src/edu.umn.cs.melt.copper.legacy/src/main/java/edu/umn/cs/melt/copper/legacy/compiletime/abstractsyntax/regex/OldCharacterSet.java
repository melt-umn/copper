package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex;

import java.util.BitSet;
import java.util.HashSet;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedNFA;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.concretesyntax.GrammarConcreteSyntax;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFA;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFAState;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;




/**
 * Represents the base regex: a set of characters.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class OldCharacterSet extends CharacterSet
{
	protected HashSet<Character> characterSet;
	
	/**
	 * Creates a new instance of CharacterSet.
	 * @param type May be <CODE>CharacterSet.LOOSE_CHARACTERS</CODE> or <CODE>CharacterSet.RANGES</CODE>. 
	 * @param characters The characters to put in this character set: <UL><LI>If <CODE>type == LOOSE_CHARACTERS</CODE>, all characters will be added as themselves.<LI>If <CODE>type == RANGES</CODE>, the characters will be taken in triples: the first character in each triple should be '+' or '-', indicating whether this is an inclusive or exclusive range, and the next two should be the inclusive limits of the range.</UL>
	 */
	public OldCharacterSet(int type,char... characters)
	{
		NFANumber = nextNFANumber++;
		characterSet = new HashSet<Character>();
		if(type == LOOSE_CHARACTERS)
		{
			for(char c : characters) characterSet.add(c);
		}
		else if(type == RANGES)
		{
			for(int i = 0;i+2 < characters.length;i += 3)
			{
				if(characters[i] == '+')
				{
					for(char j = characters[i+1];j <= characters[i+2];j++)
					{
						characterSet.add(j);
					}
				}
				else if(characters[i] == '-')
				{
					for(char j = Character.MIN_VALUE;j < characters[i+1];j++)
					{
						characterSet.add(j);
					}
					for(char j = Character.MAX_VALUE;j > characters[i+2];j--)
					{
						characterSet.add(j);
					}
				}
			}
		}
	}
	
	protected OldCharacterSet(HashSet<Character> charSet)
	{
		NFANumber = nextNFANumber++;
		characterSet = charSet;
	}
	
	public static OldCharacterSet union(OldCharacterSet x,OldCharacterSet y)
	{
		HashSet<Character> newSet = new HashSet<Character>();
		newSet.addAll(x.characterSet);
		newSet.addAll(y.characterSet);
		return new OldCharacterSet(newSet);
	}
	
	@Override
	public CharacterSet invertSet()
	{
		return invertSet(GrammarConcreteSyntax.UNIVERSAL_CHARACTER_SET.toCharArray());
	}
	
	/**
	 * Returns the complement of a set of characters.
	 * @param universal The "universal" set against which to check the inverse.
	 * @return <CODE>universal \ this.getChars()</CODE>.
	 */
	public CharacterSet invertSet(char... universal)
	{
		HashSet<Character> newSet = new HashSet<Character>();
		for(char x : universal)
		{
			if(!characterSet.contains(x) && Character.isDefined(x)) newSet.add(x);
		}
		return new OldCharacterSet(newSet);
	}
	
	/**
	 * Gets the characters in this CharacterSet.
	 * @return An iterable collection of characters.
	 */
	/*public Iterable<Character> getChars()
	{
		return characterSet;
	}*/
	
	@Override
	public char getFirstChar()
	{
		if(characterSet.isEmpty()) return Character.MIN_VALUE;
		return characterSet.iterator().next();
	}

	@Override
	public int size()
	{
		return characterSet.size();
	}

	public NFA generateAutomaton(Symbol forRegex)
	{
		NFAState start = new NFAState(Symbol.symbol(NFANumber + "-1"),null);
		NFAState accept = new NFAState(Symbol.symbol(NFANumber + "-2"),forRegex);
		for(char c : characterSet) start.addTransition(c,accept);
		HashSet<NFAState> states = new HashSet<NFAState>();
		states.add(start);
		states.add(accept);
		NFA rv = new NFA(states,start);
		return rv;
	}
	
	@Override
	public Pair<Integer, BitSet> generateAutomaton(GeneralizedNFA nfa)
	{
		throw new UnsupportedOperationException("Old 'CharacterSet' class does not support new DFA generator");
	}
	
	public HashSet<SetOfCharsSyntax> getTransitionLabels()
	{
		throw new UnsupportedOperationException("Old 'CharacterSet' class does not support new DFA generator");
	}
	
	public CharacterSet fillMacroHoles(GrammarSource fillers)
	{
		return this;
	}
	
	public OldCharacterSet clone()
	{
		HashSet<Character> newCharSet = new HashSet<Character>(characterSet);
		return new OldCharacterSet(newCharSet);
	}
	
	public String toString()
	{
		if(characterSet.size() == 1) return String.valueOf(characterSet.iterator().next());
		else
		{
			String rv = "[";
			TreeSet<Character> sortedCharSet = new TreeSet<Character>(characterSet);
			for(char c : sortedCharSet) rv += c;
			rv += "]";
			return rv;
		}
	}
	
	public <SYNTYPE,INHTYPE,E extends Exception> SYNTYPE
	    acceptVisitor(ParsedRegexVisitor<SYNTYPE,INHTYPE,E> visitor,INHTYPE inheritance)
	throws E
	{
		return visitor.visitCharacterSet(this,inheritance);
	}
}
