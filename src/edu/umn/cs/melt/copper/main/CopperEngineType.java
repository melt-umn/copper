package edu.umn.cs.melt.copper.main;

import java.util.Hashtable;

/**
 * Represents the parse-engine targets available in the Copper parser generator.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public enum CopperEngineType
{
	/** 
	 * Copper's original parse engine. Not included in <code>CopperRuntime.jar</code>.
	 * @deprecated 2008
	 */
	OLD_AND_SLOW,
	/** 
	 * Copper's primary parse engine, implementing the "single-DFA" context-aware scanning algorithm.
	 * This is the default engine and should be used for all practical applications.
	 */
	SINGLE,
	/**
	 * A parse engine implementing the "multiple-DFA" context-aware scanning algorithm. For experimental use only.
	 */
	MODED,
	/**
	 * A parse engine implementing the runtime part of the parse-table composition outlined in the paper
	 * "Verifiable Parse Table Composition for Deterministic Parsing." For experimental use only.
	 */
	SPLIT;
	
	private static Hashtable<String,CopperEngineType> fromStringTable;
	
	static void initTable()
	{
		fromStringTable = new Hashtable<String,CopperEngineType>();
		fromStringTable.put("oldnslow",OLD_AND_SLOW);
		fromStringTable.put("single",SINGLE);
		fromStringTable.put("moded",MODED);
		fromStringTable.put("split",SPLIT);
	}
	
	static boolean contains(String s)
	{
		return fromStringTable.containsKey(s);
	}
	
	static CopperEngineType fromString(String s)
	{
		return fromStringTable.get(s);
	}

}
