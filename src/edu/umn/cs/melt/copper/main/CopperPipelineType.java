package edu.umn.cs.melt.copper.main;

import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.pipeline.*;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.fragment.FragmentSerializationProcess;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.fragment.ParserFragmentCompositionProcess;

/**
 * Represents the parser compilation pipelines available in the Copper parser generator.
 * @see edu.umn.cs.melt.copper.compiletime.pipeline.Pipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 * @author Kevin Viratyosin
 *
 * Modified by Kevin to include FRAGMENT, FRAGMENT_COMPOSE type
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
	/**
	 * This pipeline first converts a parser specification into the class of objects in the
	 * {@link edu.umn.cs.melt.copper.compiletime.spec.grammarbeans} package,
	 * from which a parser fragment is compiled and serialized.
	 */
	FRAGMENT
	{
		@Override
		StandardPipeline<ParserBean,FragmentGeneratorReturnData> getPipeline(ParserCompilerParameters args)
		{
			return new StandardPipeline<ParserBean, FragmentGeneratorReturnData>(args.getUseSkin().getStandardSpecParser(args),new FragmentGenerator(), new FragmentSerializationProcess());
		}

		@Override
		String usageMessage()
		{
			return "Generates parser and scanner fragments to be composed later.";
		}

		@Override
		String stringName()
		{
			return "fragment";
		}
	},
	/**
	 * This pipeline is meant to be used after the FRAGMENT pipeline.
	 * It writes a ParserFragmentEngine java class from a set of given fragments.
	 */
	FRAGMENT_COMPOSE
	{
		@Override
		StandardPipeline<ParserFragments, ParserFragments> getPipeline(ParserCompilerParameters args)
		{
			return new StandardPipeline<ParserFragments, ParserFragments>(new ParserFragmentsDeserializer(args), new ParserFragmentsPasser(args), new ParserFragmentCompositionProcess(args));
		}

		@Override
		String usageMessage()
		{
			return "Generates parser and scanner fragments to be composed later.";
		}

		@Override
		String stringName()
		{
			return "fragmentCompose";
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
