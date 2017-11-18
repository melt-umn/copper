package edu.umn.cs.melt.copper.main;

import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.pipeline.SpecParser;
import edu.umn.cs.melt.copper.compiletime.skins.cup.CupParsingProcess;
import edu.umn.cs.melt.copper.compiletime.skins.xml.XMLParsingProcess;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;

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
	NATIVE
	{
		@Override
		SpecParser<ParserBean> getStandardSpecParser(ParserCompilerParameters args)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		String usageMessage() { return "The original input format used by Silver. DEPRECATED."; }
	},
	/**
	 * Copper's old XML-based skin (an XML/custom-parser hybrid).
	 * @deprecated 2011
	 */
	OLD_XML
	{
		@Override
		SpecParser<ParserBean> getStandardSpecParser(ParserCompilerParameters args)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		String usageMessage() { return "The original XML input schema. DEPRECATED."; }
	},
	/**
	 * An XML-based skin meant primarily for use with machine-generated specs, when
	 * Copper is used as a back-end to other tools (e.g., Silver). It is a
	 * textual front end to Copper's Java API.
	 */
	XML
	{
		@Override
		XMLParsingProcess getStandardSpecParser(ParserCompilerParameters args)
		{
			return new XMLParsingProcess();
		}
		
		@Override
		String usageMessage() { return "An XML input schema."; }
	},
	/**
	 * A skin based on the input format of JavaCUP (which is, in turn, based on the input
	 * format of YACC). This is the default skin for when Copper is used as a standalone tool.
	 */
	CUP
	{
		@Override
		CupParsingProcess getStandardSpecParser(ParserCompilerParameters args)
		{
			return new CupParsingProcess();
		}
		
		@Override
		String usageMessage() { return "(DEFAULT) A JavaCUP-like skin."; }
	};
	
	abstract SpecParser<ParserBean> getStandardSpecParser(ParserCompilerParameters args);
	
	private static Hashtable<String,CopperSkinType> fromStringTable = null;
	
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
	
	static Set<String> strings()
	{
		initTable();
		return new TreeSet<String>(fromStringTable.keySet());
	}

	static CopperSkinType fromString(String s)
	{
		return fromStringTable.get(s);
	}

	abstract String usageMessage();
}
