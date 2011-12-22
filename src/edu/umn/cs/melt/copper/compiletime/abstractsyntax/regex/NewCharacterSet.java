package edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex;

import java.util.BitSet;
import java.util.HashSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.GeneralizedNFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.oldnfa.NFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.oldnfa.NFAState;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

public class NewCharacterSet extends CharacterSet
{
	protected SetOfCharsSyntax characterSet;
	
	protected NewCharacterSet(SetOfCharsSyntax characterSet)
	{
		this.characterSet = characterSet;
	}
	
	protected NewCharacterSet(int type,char... characters)
	{
		NFANumber = nextNFANumber++;
		char[][] ranges;
		if(type == LOOSE_CHARACTERS)
		{
			ranges = new char[characters.length][2];
			for(int i = 0;i < characters.length;i++) ranges[i][0] = ranges[i][1] = characters[i];
			characterSet = new SetOfCharsSyntax(ranges);
		}
		else if(type == RANGES)
		{
			characterSet = new SetOfCharsSyntax();
			for(int i = 0;i+2 < characters.length;i += 3)
			{
				if(characters[i] == '+')
				{
					characterSet = SetOfCharsSyntax.union(characterSet,new SetOfCharsSyntax(characters[i+1],characters[i+2]));
				}
				else if(characters[i] == '-')
				{
					characterSet = SetOfCharsSyntax.union(characterSet,new SetOfCharsSyntax(characters[i+1],characters[i+2]).invert());
				}
			}
		}		
	}
	
	@Override
	public CharacterSet invertSet()
	{
		return new NewCharacterSet(characterSet.invert());
	}

	@Override
	public char getFirstChar()
	{
		if(characterSet.isEmpty()) throw new IndexOutOfBoundsException();
		else return characterSet.getMembers()[0][0];
	}

	@Override
	public int size()
	{
		return characterSet.size();
	}
	
	public static NewCharacterSet union(NewCharacterSet x,NewCharacterSet y)
	{
		return new NewCharacterSet(SetOfCharsSyntax.union(x.characterSet,y.characterSet));
	}

	@Override
	public NFA generateAutomaton(Symbol forRegex)
	{
		NFAState start = new NFAState(Symbol.symbol(NFANumber + "-1"),null);
		NFAState accept = new NFAState(Symbol.symbol(NFANumber + "-2"),forRegex);
		char[][] members = characterSet.getMembers();
		for(int i = 0;i < members.length;i++)
		{
			for(char c = members[i][0];c <= members[i][1];c++) start.addTransition(c,accept);
		}
		HashSet<NFAState> states = new HashSet<NFAState>();
		states.add(start);
		states.add(accept);
		NFA rv = new NFA(states,start);
		return rv;
	}

	@Override
	public Pair<Integer, BitSet> generateAutomaton(GeneralizedNFA nfa)
	{
		int startState = nfa.addState();
		int acceptState = nfa.addState();
		nfa.addTransition(characterSet,startState,acceptState);
		BitSet accepts = new BitSet(acceptState + 1);
		accepts.set(acceptState);
		return Pair.cons(startState,accepts);
	}

	@Override
	public ParsedRegex clone()
	{
		return new NewCharacterSet(characterSet);
	}

	@Override
	public ParsedRegex fillMacroHoles(GrammarSource fillers)
	{
		return this;
	}

	@Override
	public HashSet<SetOfCharsSyntax> getTransitionLabels()
	{
		HashSet<SetOfCharsSyntax> rv = new HashSet<SetOfCharsSyntax>();
		rv.add(characterSet);
		return rv;
	}
	
	public String toString()
	{
		return "[" + characterSet.toString() + "]";
	}

	@Override
	public <SYNTYPE, INHTYPE, E extends Exception> SYNTYPE acceptVisitor(
			ParsedRegexVisitor<SYNTYPE, INHTYPE, E> visitor, INHTYPE inheritance)
			throws E
	{
		return visitor.visitCharacterSet(this,inheritance);
	}

}
