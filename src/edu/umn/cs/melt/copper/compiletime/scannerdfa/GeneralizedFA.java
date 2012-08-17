package edu.umn.cs.melt.copper.compiletime.scannerdfa;

import java.util.BitSet;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;

public abstract class GeneralizedFA
{
	protected int alphabetSize;
	protected int nextNewStateNumber;
	protected int nextNewCharRangeNumber;
	protected int nextAcceptSymbolNumber;
	
	protected Hashtable<SetOfCharsSyntax,Integer> charRangeNumbers;
	protected SetOfCharsSyntax[] charRanges;
	protected BitSet[] acceptStates;
	
	public int getInitialArraySizes(int numRegexes)
	{
		return 4 * numRegexes + 1;
	}
	
	public abstract void lengthenArrays();
	
	public GeneralizedFA(int numRegexes,int alphabetSize)
	{
		this.alphabetSize = alphabetSize + 1;
		int initialArraySizes = getInitialArraySizes(numRegexes);
		nextNewStateNumber = 1;
		nextNewCharRangeNumber = 1;
		charRangeNumbers = new Hashtable<SetOfCharsSyntax,Integer>();
		charRanges = new SetOfCharsSyntax[alphabetSize + 1];
		acceptStates = new BitSet[initialArraySizes];
		
		SetOfCharsSyntax epsilon = new SetOfCharsSyntax();
		acceptStates[0] = new BitSet();
		charRangeNumbers.put(epsilon,0);
		charRanges[0] = epsilon;
	}
	
	public int addState()
	{
		if(nextNewStateNumber == acceptStates.length) lengthenArrays();
		acceptStates[nextNewStateNumber] = new BitSet(nextAcceptSymbolNumber);
		return nextNewStateNumber++;
	}
	
	public boolean addAcceptSymbol(int state,int symbol)
	{
		if(state >= stateCount()) return false;
		if(acceptStates[state] == null) acceptStates[state] = new BitSet(nextAcceptSymbolNumber);
		acceptStates[state].set(symbol);
		return true;
	}
	
	public boolean addAcceptSymbols(int state,BitSet symbol)
	{
		if(state >= stateCount()) return false;
		if(acceptStates[state] == null) acceptStates[state] = new BitSet(nextAcceptSymbolNumber);
		acceptStates[state].or(symbol);
		return true;
	}
	
	public SetOfCharsSyntax getCharRange(int charRangeNumber)
	{
		if(charRangeNumber >= charRangeCount()) return null;
		return charRanges[charRangeNumber];
	}

	public abstract boolean addTransition(SetOfCharsSyntax chars,int src,int dest);

	public int stateCount()
	{
		return nextNewStateNumber;
	}
	
	public int charRangeCount()
	{
		return nextNewCharRangeNumber;
	}
	
	public BitSet getAcceptSymbols(int state)
	{
		if(state >= stateCount()) return null;
		if(acceptStates[state] == null) return new BitSet();
		return acceptStates[state];
	}
}
