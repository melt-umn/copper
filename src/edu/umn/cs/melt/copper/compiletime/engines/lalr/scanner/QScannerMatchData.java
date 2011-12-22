package edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner;

import java.util.ArrayList;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.compiletime.auxiliary.Mergable;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;


public class QScannerMatchData implements Mergable<QScannerMatchData>
{
	private ArrayList<QScannerMatchData> layouts;
	private Terminal token;
	private InputPosition positionPreceding,positionFollowing;
	
	public QScannerMatchData(Terminal token,InputPosition positionPreceding,InputPosition positionFollowing,ArrayList<QScannerMatchData> layouts)
	{
		this.token = token;
		this.positionPreceding = positionPreceding;
		this.positionFollowing = positionFollowing;
		this.layouts = layouts;
	}

	public QScannerMatchData(Terminal token,InputPosition positionPreceding,InputPosition positionFollowing,QScannerMatchData layout)
	{
		this.token = token;
		this.positionPreceding = positionPreceding;
		this.positionFollowing = positionFollowing;
		layouts = new ArrayList<QScannerMatchData>();
		layouts.add(layout);
	}

	public Terminal getToken()
	{
		return token;
	}

	public InputPosition getPositionPreceding()
	{
		return positionPreceding;
	}

	public InputPosition getPositionFollowing()
	{
		return positionFollowing;
	}

	public ArrayList<QScannerMatchData> getLayouts()
	{
		return layouts;
	}

	public boolean equals(Object rhs)
	{
		if(rhs instanceof QScannerMatchData)
		{
			return token.equals(((QScannerMatchData) rhs).token);
		}
		else return false;
	}
	
	public boolean union(QScannerMatchData rhs)
	{
		if(!equals(rhs)) return false;
		return layouts.addAll(rhs.layouts);
	}

	public boolean intersect(QScannerMatchData rhs)
	{
		if(!equals(rhs)) return false;
		return layouts.retainAll(rhs.layouts);
	}

	public int hashCode()
	{
		return token.hashCode();
	}
	
	public String toString()
	{
		return token + ":(" + positionFollowing + ")";
	}
}
