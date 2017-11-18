package edu.umn.cs.melt.copper.runtime.engines.single.scanner;

import java.util.BitSet;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public class SingleDFAMatchData
{
	private Boolean isEmpty;
	
	public int firstTerm;
	public BitSet terms;
	public InputPosition precedingPos,followingPos;
	public String lexeme;
	public LinkedList<SingleDFAMatchData> layouts;
	
	public SingleDFAMatchData(BitSet terms,
			         InputPosition precedingPos,
			         InputPosition followingPos,
			         String lexeme,
			         LinkedList<SingleDFAMatchData> layouts)
	{
		this.isEmpty = null;
		this.firstTerm = terms.nextSetBit(0);
		this.terms = terms;
		this.precedingPos = precedingPos;
		this.followingPos = followingPos;
		this.lexeme = lexeme;
		this.layouts = layouts;
	}
	
	public boolean isEmpty()
	{
		if(isEmpty == null) isEmpty = precedingPos.equals(followingPos);
		return isEmpty;
	}
}
