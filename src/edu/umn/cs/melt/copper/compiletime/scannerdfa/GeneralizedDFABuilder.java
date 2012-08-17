package edu.umn.cs.melt.copper.compiletime.scannerdfa;

import java.util.BitSet;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;

public class GeneralizedDFABuilder extends GeneralizedFA
{
	// TODO: Add a new addState() method that adds state, transitions, and accept symbols
	//       all in one go. If a state exists with the same set of transitions and accept symbols,
	//       return the number of this existing state.
	private Hashtable< Integer,Hashtable<SetOfCharsSyntax,Integer> > transitions;
	private int startState;
	
	@Override
	public void lengthenArrays()
	{
		BitSet[] newAcceptStates = new BitSet[2 * acceptStates.length];
		System.arraycopy(acceptStates,0,newAcceptStates,0,acceptStates.length);
		acceptStates = newAcceptStates;
	}
	
	public void lengthenCharRangeArray()
	{
		SetOfCharsSyntax[] newCharRanges = new SetOfCharsSyntax[2 * charRanges.length];
		System.arraycopy(charRanges,0,newCharRanges,0,charRanges.length);
		charRanges = newCharRanges;
	}

	public GeneralizedDFABuilder(int numRegexes, int alphabetSize)
	{
		super(numRegexes, alphabetSize);
		transitions = new Hashtable< Integer,Hashtable<SetOfCharsSyntax,Integer> >();
	}

	@Override
	public boolean addTransition(SetOfCharsSyntax chars,int src,int dest)
	{
		if(src >= stateCount() || dest >= stateCount()) return false;
		if(charRangeNumbers.containsKey(chars) &&
				   transitions.containsKey(src) &&
				   transitions.get(src).containsKey(chars)) return false;
		if(!charRangeNumbers.containsKey(chars))
		{
			if(nextNewCharRangeNumber == charRanges.length) lengthenCharRangeArray();
			charRangeNumbers.put(chars,nextNewCharRangeNumber);
			charRanges[nextNewCharRangeNumber] = chars;
			nextNewCharRangeNumber++;
		}
		if(!transitions.containsKey(src)) transitions.put(src,new Hashtable<SetOfCharsSyntax,Integer>());
		transitions.get(src).put(chars,dest);
		return true;		
	}
	
	public int getStartState()
	{
		return startState;
	}

	public void setStartState(int startState)
	{
		this.startState = startState;
	}

	public GeneralizedDFA buildDFA()
	{
		GeneralizedDFA dfa = new GeneralizedDFA(this);
		for(int s : transitions.keySet())
		{
			for(SetOfCharsSyntax c : transitions.get(s).keySet())
			{
				dfa.transitions[s][charRangeNumbers.get(c)] = transitions.get(s).get(c);
			}
		}
		dfa.setStartState(startState);
		return dfa;
	}
}
