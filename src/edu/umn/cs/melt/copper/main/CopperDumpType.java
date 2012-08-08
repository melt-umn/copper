package edu.umn.cs.melt.copper.main;

import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents the formats for grammar dumps available in Copper.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public enum CopperDumpType
{
	/** A plain-text dump format similar to that produced by JavaCUP. */
	PLAIN
	{
		@Override
		String usageMessage() { return "A plain-text format similar to JavaCUP's."; }
	},
	/** An XML-based dump format. */
	XML
	{
		@Override
		String usageMessage() { return "XML format."; }
	},
	/**
	 * An HTML dump format with all references to grammar elements, DFA states, and parse table states hyperlinked,
	 * allowing for easier navigation. This is produced from the XML dump by means of an XSL transform.
	 */
	HTML
	{
		@Override
		String usageMessage() { return "An HTML-based format with all references to grammar\n\t\t  and parse table elements hyperlinked."; }
	},
	/**
	 * A reproduction of the grammar spec in Copper's XML skin, suitable for re-input to Copper.
	 * Intended primarily as a method of translation from other skins.
	 * @see CopperSkinType#XML
	 */
	XML_SPEC
	{
		@Override
		String usageMessage() { return "A reproduction of the grammar specification in\n\t\t  Copper's XML skin (intended primarily as a method of\n\t\t  translating grammars from other skins)."; }
	};
	
	
	private static Hashtable<String,CopperDumpType> fromStringTable = null;
	
	static void initTable()
	{
		if(fromStringTable != null) return;
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
	
	static Set<String> strings()
	{
		initTable();
		return new TreeSet<String>(fromStringTable.keySet());
	}
	
	static CopperDumpType fromString(String s)
	{
		return fromStringTable.get(s);
	}
	
	abstract String usageMessage();
}
