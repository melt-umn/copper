package edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.oldnfa;


import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.auxiliary.CharacterRange;
import edu.umn.cs.melt.copper.legacy.compiletime.auxiliary.Mergable;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;


/**
 * Represents a state in a finite automaton.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class NFAState implements Iterable< Pair<CharacterRange,NFAState> >,Mergable<NFAState>
{
	/** The character value used to represent an epsilon-transition. */
	public static final char EmptyChar = ScannerBuffer.EOFIndicator;
	// The set of identifiers (can represent a single NFA state,
	// or a compounded DFA state).
	private HashSet<Symbol> identifier;
	private HashSet<Symbol> accepts;
	private Hashtable< CharacterRange,HashSet<NFAState> > transitions;
	
	/**
	 * Creates a new instance of FAState.
	 * @param identifier The symbol used to identify this state.
	 * @param acceptsFor Whether or not this state is an accept state.
	 */
	public NFAState(Symbol identifier,Symbol acceptsFor)
	{
		this.identifier = new HashSet<Symbol>();
		if(identifier != null) this.identifier.add(identifier);
		this.accepts = new HashSet<Symbol>();
		if(acceptsFor != null) this.accepts.add(acceptsFor);
		transitions = new Hashtable< CharacterRange,HashSet<NFAState> >();		
	}

	/**
	 * Creates a new instance of FAState.
	 * @param identifier The symbols used to identify this state.
	 * @param accepts The symbols by virtue of which this state is an accept state.
	 */
	public NFAState(HashSet<Symbol> identifier,HashSet<Symbol> accepts)
	{
		this.identifier = new HashSet<Symbol>();
		if(identifier != null) this.identifier.addAll(identifier);
		this.accepts = new HashSet<Symbol>();
		if(accepts != null) this.accepts.addAll(accepts);
		transitions = new Hashtable< CharacterRange,HashSet<NFAState> >();		
	}

	/**
	 * @return Returns the identifier.
	 */
	public HashSet<Symbol> getIdentifier()
	{
		return identifier;
	}
	
	/**
	 * @return Returns the set of regexes for which this state accepts.
	 */
	public HashSet<Symbol> getAccepts()
	{
		return accepts;
	}

	private class FAStateIterator implements Iterator< Pair<CharacterRange,NFAState> >
	{
		private Hashtable< CharacterRange,HashSet<NFAState> > table;
		private CharacterRange presentKey;
		private Iterator<CharacterRange> keyIterator;
		private Iterator<NFAState> valueIterator;

		public FAStateIterator(Hashtable<CharacterRange, HashSet<NFAState>> table)
		{
			this.table = table;
			this.keyIterator = table.keySet().iterator();
			this.valueIterator = null;
		}
		
		public Pair<CharacterRange,NFAState> next()
		{
			if(valueIterator == null)
			{
				if(!keyIterator.hasNext()) throw new NoSuchElementException();
				presentKey = keyIterator.next();
				valueIterator = table.get(presentKey).iterator();
			}
			if(!valueIterator.hasNext()) throw new NoSuchElementException();
			Pair<CharacterRange,NFAState> rv = new Pair<CharacterRange,NFAState>(presentKey,valueIterator.next());
			if(!valueIterator.hasNext()) valueIterator = null;
			return rv;
		}
		
		public boolean hasNext()
		{
			if(valueIterator != null) return valueIterator.hasNext();
			else return keyIterator.hasNext();
		}
		
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
	
	public Iterator< Pair<CharacterRange,NFAState> > iterator()
	{
		return new FAStateIterator(transitions);
	}
	
	/**
	 * Gets all the symbols for which there exist a transition out of this state.
	 * @return The set of transition symbols.
	 */
	public Iterable<CharacterRange> getTransitionSymbols()
	{
		return transitions.keySet();
	}
	
	/**
	 * Gets all the transitions for a particular symbol from this state.
	 * @param sym The alphabet symbol in question.
	 * @return The set of transitions from this state marked with <CODE>sym</CODE>.
	 */
	public Iterable<NFAState> getTransitions(CharacterRange sym)
	{
		if(transitions.containsKey(sym)) return transitions.get(sym);
		else return new HashSet<NFAState>();
	}
	
	/**
	 * Adds regexes for which this state accepts.
	 * @param newAccepts The set of regex symbols to add.
	 * @return Whether the union operation changed the accepts set.
	 */
	public boolean addAcceptSyms(HashSet<Symbol> newAccepts)
	{
		return accepts.addAll(newAccepts);
	}
	
	public boolean addTransition(Character symbol,NFAState newState)
	{
		return addTransition(new CharacterRange(symbol),newState);
	}

	/**
	 * Adds a transition to this state.
	 * @param symbol The symbol with which the transition should be marked.
	 * @param newState The state to which the transition should point.
	 * @return Whether or not the addition changed the transition set.
	 */
	public boolean addTransition(CharacterRange symbol,NFAState newState)
	{
		if(!transitions.containsKey(symbol)) transitions.put(symbol,new HashSet<NFAState>());
		return transitions.get(symbol).add(newState);
	}
	
	public void compressTransitions()
	{
		Hashtable< HashSet<NFAState>,HashSet<CharacterRange> > revMap = new Hashtable< HashSet<NFAState>,HashSet<CharacterRange> >();
		for(CharacterRange cr : transitions.keySet())
		{
			if(!revMap.containsKey(transitions.get(cr))) revMap.put(transitions.get(cr),new HashSet<CharacterRange>());
			revMap.get(transitions.get(cr)).add(cr);
		}
		transitions.clear();
		for(HashSet<NFAState> dest : revMap.keySet())
		{
			for(CharacterRange cr : CharacterRange.consolidateAdjacentRanges(revMap.get(dest)))
			{
				if(!transitions.containsKey(cr)) transitions.put(cr,new HashSet<NFAState>());
				transitions.get(cr).addAll(dest);
			}
		}
	}

	public boolean equals(Object rhs)
	{
		if(rhs instanceof NFAState) return identifier.equals(((NFAState) rhs).identifier);
		else return false;
	}
	
	public String toString()
	{
		String rv = "State " + identifier;
		if(!accepts.isEmpty()) rv += ", accepts for " + accepts + ",";
		rv += " -> [ ";
		for(Pair<CharacterRange,NFAState> transition : this)
		{
			rv += "(";
			if(transition.first().equals(new CharacterRange(EmptyChar))) rv += "(eps)";
			else rv += transition.first();
			rv += "," + transition.second().identifier.toString() + ") ";
		}
		rv += "]";
		return rv;
	}
	
	public int hashCode() { return identifier.hashCode(); }
	
	public boolean union(NFAState rhs)
	{
		if(equals(rhs)) return accepts.addAll(rhs.accepts);
		else return false;
	}
	
	public boolean intersect(NFAState rhs)
	{
		if(equals(rhs)) return accepts.retainAll(rhs.accepts);
		return false;
	}
}
