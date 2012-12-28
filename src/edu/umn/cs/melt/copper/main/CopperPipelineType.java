package edu.umn.cs.melt.copper.main;

import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.pipeline.StandardSpecCompilerReturnData;
import edu.umn.cs.melt.copper.compiletime.pipeline.Pipeline;
import edu.umn.cs.melt.copper.compiletime.pipeline.StandardPipeline;
import edu.umn.cs.melt.copper.compiletime.pipeline.StandardSpecCompiler;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;

/**
 * Represents the parser compilation pipelines available in the Copper parser generator.
 * @see edu.umn.cs.melt.copper.compiletime.pipeline.Pipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public enum CopperPipelineType
{
	/**
	 * This pipeline first converts a parser specification into the class of objects in the
	 * {@link edu.umn.cs.melt.copper.compiletime.spec.grammarbeans} package,
	 * from which a parser is compiled.
	 */
	GRAMMARBEANS
	{
		@Override
		StandardPipeline<ParserBean,StandardSpecCompilerReturnData> getPipeline(ParserCompilerParameters args)
		{
			return new StandardPipeline<ParserBean, StandardSpecCompilerReturnData>(args.getUseSkin().getStandardSpecParser(args),new StandardSpecCompiler(),args.getUseEngine().getStandardSourceBuilder(args));
		}

		@Override
		String usageMessage()
		{
			return "The default pipeline.";
		}
		
		@Override
		String stringName()
		{
			return "default";
		}
	},
	// TODO: Rip this out when GrammarSource is gone. 
	/** This pipeline uses Copper 0.5/0.6's parser compilation classes and methods. */
	LEGACY
	{
		@Override
		edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LegacyPipeline getPipeline(ParserCompilerParameters args)
		{
			return new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LegacyPipeline();
		}

		@Override
		String usageMessage()
		{
			return "The pipeline used in Copper 0.5 and 0.6.";
		}

		@Override
		String stringName()
		{
			return "legacy";
		}
	};
	
	private static Hashtable<String,CopperPipelineType> fromStringTable = null;
	
	static void initTable()
	{
		if(fromStringTable != null) return;
		fromStringTable = new Hashtable<String,CopperPipelineType>();
		for(CopperPipelineType pt : CopperPipelineType.values())
		{
			fromStringTable.put(pt.stringName(),pt);
		}
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

	static CopperPipelineType fromString(String s)
	{
		return fromStringTable.get(s);
	}

	abstract String stringName();
	abstract String usageMessage();
	
	abstract Pipeline getPipeline(ParserCompilerParameters args);
}
