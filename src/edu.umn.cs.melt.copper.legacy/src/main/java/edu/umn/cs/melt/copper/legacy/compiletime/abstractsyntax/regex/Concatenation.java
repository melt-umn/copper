package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.HashSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedNFA;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFA;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa.NFAState;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * Holds a concatenation of regexes.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class Concatenation extends ParsedRegex
{
	private ArrayList<ParsedRegex> subexps;

	/**
	 * Creates a new instance of Concatenation.
	 * @param subexps The subexpressions of this Concatenation, in order.
	 */
	public Concatenation(ParsedRegex... subexps)
	{
		NFANumber = nextNFANumber++;
		this.subexps = new ArrayList<ParsedRegex>();
		for(ParsedRegex pr : subexps)
		{
			if(pr instanceof Concatenation)
			{
				for(ParsedRegex prc : ((Concatenation) pr).subexps) this.subexps.add(prc);
			}
			else this.subexps.add(pr);
		}
	}
	
	public NFA generateAutomaton(Symbol forRegex)
	{
		NFA[] baseNFAs = new NFA[subexps.size()];
		for(int i = 0;i < subexps.size();i++)
		{
			baseNFAs[i] = subexps.get(i).generateAutomaton(forRegex);
			
		}
		HashSet<NFAState> states = new HashSet<NFAState>();
		for(int i = 0;i+1 < baseNFAs.length;i++)
		{
			for(NFAState s : baseNFAs[i].getStates())
			{
				states.add(s);
				if(!s.getAccepts().isEmpty())
				{
					s.addTransition(NFAState.EmptyChar,baseNFAs[i+1].getStartState());
					s.getAccepts().clear();
				}
			}
		}
		states.addAll(baseNFAs[baseNFAs.length - 1].getStates());
		NFA rv = new NFA(states,baseNFAs[0].getStartState());
		return rv;
	}
	
	@Override
	public Pair<Integer,BitSet> generateAutomaton(GeneralizedNFA nfa)
	{
		int newStartState = -1;
		Pair<Integer,BitSet> currentSub = null,prevSub = null;
		// For each constituent of the concatenation:
		for(ParsedRegex subexp : subexps)
		{
			// Generate its states.
			currentSub = subexp.generateAutomaton(nfa);
			// If it is not the first constituent, add epsilon transitions
			// from all the accept states of the previous constituent to
			// the start state of this constituent.
			if(prevSub != null)
			{
				for(int i = prevSub.second().nextSetBit(0);i >= 0;i = prevSub.second().nextSetBit(i+1))
				{
					nfa.addEpsilonTransition(i,currentSub.first());
				}
			}
			else newStartState = currentSub.first();
			
			prevSub = currentSub;
		}
		return Pair.cons(newStartState,currentSub.second());
	}
	
	public Concatenation fillMacroHoles(GrammarSource fillers)
	{
		ParsedRegex[] newSubexps = new ParsedRegex[subexps.size()];
		int i = 0;
		for(ParsedRegex subexp : subexps)
		{
			newSubexps[i++] = subexp.fillMacroHoles(fillers);
		}
		return new Concatenation(newSubexps);
	}

	public HashSet<SetOfCharsSyntax> getTransitionLabels()
	{
		HashSet<SetOfCharsSyntax> rv = new HashSet<SetOfCharsSyntax>();
		for(ParsedRegex sexp : subexps) rv.addAll(sexp.getTransitionLabels());
		return rv;
	}
	
	public Concatenation clone()
	{
		ParsedRegex[] newSubexps = new ParsedRegex[subexps.size()];
		int i = 0;
		for(ParsedRegex subexp : subexps)
		{
			newSubexps[i++] = subexp.clone();
		}
		return new Concatenation(newSubexps);
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
		return visitor.visitConcatenation(this,inheritance);
	}
}
