package edu.umn.cs.melt.copper.main;

import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.pipeline.StandardSpecCompilerReturnData;
import edu.umn.cs.melt.copper.compiletime.pipeline.SourceBuilder;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.newbuilders.SingleDFACompilationProcess;

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
	OLD_AND_SLOW
	{
		@Override
		SourceBuilder<StandardSpecCompilerReturnData> getStandardSourceBuilder(ParserCompilerParameters args)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		String usageMessage() { return "The original JCF-based parsing engine.\n\t\t           DEPRECATED. Not included in 'CopperRuntime.jar'."; }
	},
	/** 
	 * Copper's primary parse engine, implementing the "single-DFA" context-aware scanning algorithm.
	 * This is the default engine and should be used for all practical applications.
	 */
	SINGLE
	{
		@Override
		SingleDFACompilationProcess getStandardSourceBuilder(ParserCompilerParameters args)
		{
			return new SingleDFACompilationProcess(true);
		}	

		@Override
		String usageMessage() { return "(DEFAULT) A parsing engine with a single scanner for\n\t\t        all parsing contexts."; }
	},
	/**
	 * A parse engine implementing the "multiple-DFA" context-aware scanning algorithm. For experimental use only.
	 */
	MODED
	{
		@Override
		SourceBuilder<StandardSpecCompilerReturnData> getStandardSourceBuilder(ParserCompilerParameters args)
		{
			throw new UnsupportedOperationException();
		}		

		@Override
		String usageMessage() { return "An engine with a separate scanner for each different\n\t\t       parsing context. EXPERIMENTAL."; }
	},
	/**
	 * A parse engine implementing the runtime part of the parse-table composition outlined in the paper
	 * "Verifiable Parse Table Composition for Deterministic Parsing." For experimental use only.
	 */
	SPLIT
	{
		@Override
		SourceBuilder<StandardSpecCompilerReturnData> getStandardSourceBuilder(ParserCompilerParameters args)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		String usageMessage() { return "An engine made for assembling pieces of parse tables\n\t\t       on-the-fly. EXPERIMENTAL."; }
	};
	
	abstract SourceBuilder<StandardSpecCompilerReturnData> getStandardSourceBuilder(ParserCompilerParameters args); 
	
	private static Hashtable<String,CopperEngineType> fromStringTable = null;
	
	static void initTable()
	{
		if(fromStringTable != null) return;
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
	
	static Set<String> strings()
	{
		initTable();
		return new TreeSet<String>(fromStringTable.keySet());
	}

	static CopperEngineType fromString(String s)
	{
		return fromStringTable.get(s);
	}

	abstract String usageMessage();
}
