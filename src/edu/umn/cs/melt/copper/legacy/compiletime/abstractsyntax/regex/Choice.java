package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedNFA;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFA;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFAState;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;




/**
 * Holds a "choice" or union between several regexes.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class Choice extends ParsedRegex
{
	private HashSet<ParsedRegex> subexps;
	
	/**
	 * Creates a new instance of Choice.
	 * @param subexps The subexpressions of this Choice.
	 */
	public Choice(ParsedRegex... subexps)
	{
		NFANumber = nextNFANumber++;
		this.subexps = new HashSet<ParsedRegex>();
		for(ParsedRegex pr : subexps)
		{
			if(pr instanceof Choice)
			{
				for(ParsedRegex prc : ((Choice) pr).subexps) this.subexps.add(prc);
			}
			else this.subexps.add(pr);
		}
	}
	
	public NFA generateAutomaton(Symbol forRegex)
	{
		NFAState firstState = new NFAState(Symbol.symbol(NFANumber + "-1"),null);
		HashSet<NFAState> states = new HashSet<NFAState>();
		states.add(firstState);
		for(ParsedRegex sexp : subexps)
		{
			NFA baseNFA = sexp.generateAutomaton(forRegex);
			states.addAll(baseNFA.getStates());
			firstState.addTransition(NFAState.EmptyChar,baseNFA.getStartState());
		}
		NFA rv = new NFA(states,firstState);
		return rv;
	}

	@Override
	public Pair<Integer,BitSet> generateAutomaton(GeneralizedNFA nfa)
	{
		// Add a new start state.
		int newStartState = nfa.addState();
		BitSet accepts = new BitSet();
		Pair<Integer,BitSet> subs;
		// For each constituent of the choice:
		for(ParsedRegex sexp : subexps)
		{
			// Generate its states.
			subs = sexp.generateAutomaton(nfa);
			// Make all of its accept states into accept states of
			// this automaton.
			accepts.or(subs.second());
			// Add an epsilon transition from this automaton's
			// start state to the constituent's.
			nfa.addEpsilonTransition(newStartState,subs.first());
		}
		return Pair.cons(newStartState,accepts);
	}
	
	public HashSet<SetOfCharsSyntax> getTransitionLabels()
	{
		HashSet<SetOfCharsSyntax> rv = new HashSet<SetOfCharsSyntax>();
		for(ParsedRegex sexp : subexps) rv.addAll(sexp.getTransitionLabels());
		return rv;
	}
	
	public Choice fillMacroHoles(GrammarSource fillers)
	{
		ParsedRegex[] newSubexps = new ParsedRegex[subexps.size()];
		int i = 0;
		for(ParsedRegex subexp : subexps)
		{
			newSubexps[i++] = subexp.fillMacroHoles(fillers);
		}
		return new Choice(newSubexps);
	}
	
	public Choice clone()
	{
		ParsedRegex[] newSubexps = new ParsedRegex[subexps.size()];
		int i = 0;
		for(ParsedRegex subexp : subexps)
		{
			newSubexps[i++] = subexp.clone();
		}
		return new Choice(newSubexps);
	}
	
	public String toString()
	{
		String rv = "";
		for(Iterator<ParsedRegex> it = subexps.iterator();it.hasNext();)
		{
			String part = it.next().toString();
			if(part.length() > 1 && part.charAt(0) != '[') rv += '(';
			rv += part;
			if(part.length() > 1 && part.charAt(0) != '[') rv += ')';
			if(it.hasNext()) rv += '|';
		}
		return rv;
	}

	public Iterable<ParsedRegex> getConstituents()
	{
		return subexps;
	}

	public <SYNTYPE,INHTYPE,E extends Exception> SYNTYPE
    acceptVisitor(ParsedRegexVisitor<SYNTYPE,INHTYPE,E> visitor,INHTYPE inheritance)
	throws E
	{
		return visitor.visitChoice(this,inheritance);
	}
}
