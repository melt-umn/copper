package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex;

import java.util.BitSet;
import java.util.HashSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedNFA;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFA;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * Represents a regular expression in AST form (after its
 * string representation has been parsed).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class ParsedRegex implements Cloneable
{
	protected static int nextNFANumber = 0;
	protected int NFANumber;
	/**
	 * Generates an NFA from a regex as described in Sipser's
	 * "Introduction to the Theory of Computation."
	 * @param forRegex The name of the regex for which the NFA is being generated. This will be inserted into all accept states in the NFA. 
	 * @return The NFA object.
	 */
	public abstract NFA generateAutomaton(Symbol forRegex);
	public abstract Pair<Integer,BitSet> generateAutomaton(GeneralizedNFA nfa);
	
	public abstract ParsedRegex clone();
	
	public abstract ParsedRegex fillMacroHoles(GrammarSource fillers);
	
	public abstract HashSet<SetOfCharsSyntax> getTransitionLabels();
	
	public abstract <SYNTYPE,INHTYPE,E extends Exception> SYNTYPE acceptVisitor(ParsedRegexVisitor<SYNTYPE,INHTYPE,E> visitor,INHTYPE inheritance) throws E;
	
	/**
	 * Produces a regex to match an ordinary string (a concatenation of single characters).
	 * @param str The string to translate.
	 * @return The regex representing the language {str}.
	 */
	public static ParsedRegex simpleStringRegex(String str)
	{
		if(str.equals("")) return new EmptyString();
		ParsedRegex[] rvIn = new ParsedRegex[str.length()];
		for(int i = 0;i < str.length();i++)
		{
			rvIn[i] = CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,str.charAt(i));
		}
		return new Concatenation(rvIn);
	}
}
