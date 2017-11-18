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
 * Holds the Kleene Star of a regex.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class KleeneStar extends ParsedRegex
{
	private ParsedRegex base;
	
	/**
	 * Creates a new instance of KleeneStar.
	 * @param base The regex to star.
	 */
	public KleeneStar(ParsedRegex base)
	{
		NFANumber = nextNFANumber++;
		this.base = base;
	}
	
	public NFA generateAutomaton(Symbol forRegex)
	{
		NFA baseNFA = base.generateAutomaton(forRegex);
		NFAState firstState = new NFAState(Symbol.symbol(NFANumber + "-1"),forRegex);
		firstState.addTransition(NFAState.EmptyChar,baseNFA.getStartState());
		HashSet<NFAState> states = new HashSet<NFAState>();
		states.add(firstState);
		for(NFAState s : baseNFA.getStates())
		{
			states.add(s);
			if(!s.getAccepts().isEmpty() &&
			   !s.equals(baseNFA.getStartState()))
			{
				s.addTransition(NFAState.EmptyChar,baseNFA.getStartState());
			}
		}
		NFA rv = new NFA(states,firstState);
		return rv;
	}
	
	
	@Override
	public Pair<Integer,BitSet> generateAutomaton(GeneralizedNFA nfa)
	{
		int newStartState = nfa.addState();
		BitSet newAccepts = new BitSet(newStartState + 1);
		newAccepts.set(newStartState);
		Pair<Integer,BitSet> sub = base.generateAutomaton(nfa);
		nfa.addEpsilonTransition(newStartState,sub.first());
		for(int i = sub.second().nextSetBit(0);i >= 0;i = sub.second().nextSetBit(i+1))
		{
			nfa.addEpsilonTransition(i,newStartState);
		}
		return Pair.cons(newStartState,newAccepts);
	}

	
	public KleeneStar fillMacroHoles(GrammarSource fillers)
	{
		return new KleeneStar(base.fillMacroHoles(fillers));
	}

	public KleeneStar clone()
	{
		return new KleeneStar(base.clone());
	}
	
	public HashSet<SetOfCharsSyntax> getTransitionLabels()
	{
		return base.getTransitionLabels();
	}

	public String toString()
	{
		String rv = "",baseS;
		baseS = base.toString();
		if(baseS.length() > 1 && baseS.charAt(0) != '[') rv += '(';
		rv += baseS;
		if(baseS.length() > 1 && baseS.charAt(0) != '[') rv += ')';
		rv += '*';
		return rv;
	}
	
	public ParsedRegex getConstituent()
	{
		return base;
	}
	
	public <SYNTYPE,INHTYPE,E extends Exception> SYNTYPE
    acceptVisitor(ParsedRegexVisitor<SYNTYPE,INHTYPE,E> visitor,INHTYPE inheritance)
	throws E
	{
		return visitor.visitKleeneStar(this,inheritance);
	}
}
