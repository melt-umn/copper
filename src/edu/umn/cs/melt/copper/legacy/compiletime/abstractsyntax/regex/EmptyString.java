package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex;

import java.util.BitSet;
import java.util.HashSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedNFA;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFA;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFAState;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;



/**
 * Represents an "empty string" regex.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class EmptyString extends ParsedRegex
{
	/**
	 * Creates a new instance of EmptyString.
	 *
	 */
	public EmptyString()
	{
		NFANumber = nextNFANumber++;		
	}
	
	public NFA generateAutomaton(Symbol forRegex)
	{
		NFAState start = new NFAState(Symbol.symbol(NFANumber + "-1"),forRegex);
		HashSet<NFAState> states = new HashSet<NFAState>();
		states.add(start);
		NFA rv = new NFA(states,start);
		return rv;
	}
	
	@Override
	public Pair<Integer, BitSet> generateAutomaton(GeneralizedNFA nfa)
	{
		// Add exactly one new state, which is both the start and accept state.
		int newState = nfa.addState();
		BitSet accepts = new BitSet(newState + 1);
		accepts.set(newState);
		return Pair.cons(newState,accepts);
	}

	public EmptyString fillMacroHoles(GrammarSource fillers)
	{
		return this;
	}

	public HashSet<SetOfCharsSyntax> getTransitionLabels()
	{
		return new HashSet<SetOfCharsSyntax>();
	}

	public EmptyString clone()
	{
		return new EmptyString();
	}
	
	public String toString()
	{
		return "";
	}

	public <SYNTYPE,INHTYPE,E extends Exception> SYNTYPE
        acceptVisitor(ParsedRegexVisitor<SYNTYPE,INHTYPE,E> visitor,INHTYPE inheritance)
	throws E
	{
		return visitor.visitEmptyString(this,inheritance);
	}
}
