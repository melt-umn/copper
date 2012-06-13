package edu.umn.cs.melt.copper.main;

import java.util.Hashtable;

/**
 * Represents the formats for grammar dumps available in Copper.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public enum CopperDumpType
{
	/** A plain-text dump format similar to that produced by JavaCUP. */
	PLAIN,
	/** An XML-based dump format. */
	XML,
	/**
	 * An HTML dump format with all references to grammar elements, DFA states, and parse table states hyperlinked,
	 * allowing for easier navigation. This is produced from the XML dump by means of an XSL transform.
	 */
	HTML,
	/**
	 * A reproduction of the grammar spec in Copper's XML skin, suitable for re-input to Copper.
	 * Intended primarily as a method of translation from other skins.
	 * @see CopperSkinType#XML
	 */
	XML_SPEC;
	
	
	private static Hashtable<String,CopperDumpType> fromStringTable;
	
	static void initTable()
	{
		fromStringTable = new Hashtable<String,CopperDumpType>();
		fromStringTable.put("plain",PLAIN);
		fromStringTable.put("xml",XML);
		fromStringTable.put("html",HTML);
		fromStringTable.put("xmlspec",XML_SPEC);
	}
	
	static boolean contains(String s)
	{
		return fromStringTable.containsKey(s);
	}
	
	static CopperDumpType fromString(String s)
	{
		return fromStringTable.get(s);
	}
}
