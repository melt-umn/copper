package edu.umn.cs.melt.copper.compiletime.scannerdfa;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;

public class GeneralizedDFA extends GeneralizedFA
{
	protected int[][] transitions;
	int startState;
	
	public GeneralizedDFA(GeneralizedFA fa)
	{
		super(0,0);
		nextNewStateNumber = fa.nextNewStateNumber;
		nextNewCharRangeNumber = fa.nextNewCharRangeNumber;
		charRangeNumbers = fa.charRangeNumbers;
		charRanges = fa.charRanges;
		acceptStates = fa.acceptStates;
		transitions = new int[nextNewStateNumber][nextNewCharRangeNumber];
	}
	
	public int getStartState()
	{
		return startState;
	}

	public void setStartState(int startState)
	{
		this.startState = startState;
	}

	@Override
	public void lengthenArrays()
	{
		BitSet[] newAcceptStates = new BitSet[2 * acceptStates.length];
		System.arraycopy(acceptStates,0,newAcceptStates,0,acceptStates.length);
		acceptStates = newAcceptStates;
		
		int[][] newTransitions = new int[2 * transitions.length][alphabetSize + 1];
		System.arraycopy(transitions,0,newTransitions,0,transitions.length);
		newTransitions = transitions;		
	}

	public GeneralizedDFA(int numRegexes, int alphabetSize)
	{
		super(numRegexes, alphabetSize);
		int initialArraySizes = getInitialArraySizes(numRegexes);
		transitions = new int[initialArraySizes][alphabetSize + 1];
	}

	@Override
	public boolean addTransition(SetOfCharsSyntax chars,int src,int dest)
	{
		if(src >= stateCount() || dest >= stateCount()) return false;
		if(charRangeNumbers.containsKey(chars) &&
				   transitions[src][charRangeNumbers.get(chars)] != 0) return false;
		int charRangeNumber;
		if(!charRangeNumbers.containsKey(chars))
		{
			charRangeNumbers.put(chars,nextNewCharRangeNumber);
			charRanges[nextNewCharRangeNumber] = chars;
			charRangeNumber = nextNewCharRangeNumber++;
		}
		else charRangeNumber = charRangeNumbers.get(chars);
		transitions[src][charRangeNumber] = dest;
		return true;
	}
	
	public int[][] getTransitions()
	{
		return transitions;
	}
	
	public BitSet getConnectedStates(int state)
	{
		if(state >= stateCount()) return null;
		BitSet rv = new BitSet(stateCount());
		for(int i = 0;i < transitions[state].length;i++) rv.set(transitions[state][i]);
		return rv;
	}
	
	public String toString()
	{
		StringBuffer rv = new StringBuffer();
		rv.append("Start state: " + startState + "\n");
		rv.append("Character sets:\n");
		for(int i = 0;i < nextNewCharRangeNumber;i++)
		{
			rv.append(i + ": " + charRanges[i] + "\n");
		}
		for(int i = 0;i < nextNewStateNumber;i++)
		{
			rv.append("   " + i);
			for(int k = 0;k < (String.valueOf(nextNewStateNumber - 1).length() - String.valueOf(i).length() + 1);k++)
			{
				rv.append(" ");
			}
			rv.append("- ");
			for(int j = 0;j < nextNewCharRangeNumber;j++)
			{
				rv.append(transitions[i][j]);
				for(int k = 0;k < (String.valueOf(nextNewStateNumber - 1).length() - String.valueOf(transitions[i][j]).length() + 1);k++)
				{
					rv.append(" ");
				}
			}
			rv.append(" - Accepts: " + acceptStates[i]);
			rv.append("\n");
		}
		return rv.toString();
	}
}
