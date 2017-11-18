package edu.umn.cs.melt.copper.legacy.compiletime.statistics;

import java.util.Hashtable;

public class ParserStatistics
{
	public ScannerStatistics scannerStats;
	public Hashtable<Integer,Integer> parserStatesVisited;
	public int totalParserStates;
	
	public ParserStatistics(ScannerStatistics scannerStats)
	{
		this.scannerStats = scannerStats;
		this.parserStatesVisited = new Hashtable<Integer,Integer>();
	}
	
	public String toString()
	{
		String scannerStatsS;
		if(scannerStats == null) scannerStatsS = "NO SCANNER STATISTICS";
		else scannerStatsS = scannerStats.toString();
		return scannerStatsS + "\nNumber of parser states visited: " + parserStatesVisited.keySet().size() + " out of " + totalParserStates + " (" + (((double) parserStatesVisited.keySet().size()) / totalParserStates) + ")";
	}
}
