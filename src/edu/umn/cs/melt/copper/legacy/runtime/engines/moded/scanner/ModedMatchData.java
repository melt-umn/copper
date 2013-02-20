package edu.umn.cs.melt.copper.legacy.runtime.engines.moded.scanner;

import java.util.LinkedList;

import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public class ModedMatchData
{
	public int term;
	public InputPosition precedingPos,followingPos;
	public String lexeme;
	public LinkedList<ModedMatchData> layouts;
	
	public ModedMatchData(int term,
			              InputPosition precedingPos,
			              InputPosition followingPos,
			              String lexeme,
			              LinkedList<ModedMatchData> layouts)
	{
		this.term = term;
		this.precedingPos = precedingPos;
		this.followingPos = followingPos;
		this.lexeme = lexeme;
		this.layouts = layouts;
	}
}
