package edu.umn.cs.melt.copper.main;

import java.util.Hashtable;

/**
 * Represents the "skins" (input formats) available in Copper.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public enum CopperSkinType
{
	/**
	 * Copper's original skin. Now used only within Copper itself.
	 * @deprecated 2008
	 */
	NATIVE,
	/**
	 * Copper's old XML-based skin (an XML/custom-parser hybrid).
	 * @deprecated 2011
	 */
	OLD_XML,
	/**
	 * An XML-based skin meant primarily for use with machine-generated specs, when
	 * Copper is used as a back-end to other tools (e.g., Silver). It is a
	 * textual front end to Copper's Java API.
	 */
	XML,
	/**
	 * A skin based on the input format of JavaCUP (which is, in turn, based on the input
	 * format of YACC). This is the default skin for when Copper is used as a standalone tool.
	 */
	CUP;
	
	private static Hashtable<String,CopperSkinType> fromStringTable;
	
	static void initTable()
	{
		fromStringTable = new Hashtable<String,CopperSkinType>();
		fromStringTable.put("native",NATIVE);
		fromStringTable.put("oldxml",OLD_XML);
		fromStringTable.put("xml",XML);
		fromStringTable.put("cup",CUP);
	}
	
	static boolean contains(String s)
	{
		return fromStringTable.containsKey(s);
	}
	
	static CopperSkinType fromString(String s)
	{
		return fromStringTable.get(s);
	}
}
