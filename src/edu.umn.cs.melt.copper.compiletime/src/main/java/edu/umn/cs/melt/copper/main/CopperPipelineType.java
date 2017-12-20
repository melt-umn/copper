package edu.umn.cs.melt.copper.main;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.pipeline.*;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.fragment.FragmentSerializationProcess;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.fragment.ParserFragmentCompositionProcess;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

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
		boolean isAvailable() {
			return true;
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
		boolean isAvailable() {
			return true;
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
		boolean isAvailable() {
			return true;
		}

		@Override
		String usageMessage()
		{
			return "Compose parser and scanner fragments generated in the 'compose' pipeline.";
		}

		@Override
		String stringName()
		{
			return "fragmentCompose";
		}
	},
	/** This pipeline uses Copper 0.5/0.6's parser compilation classes and methods.
	  * Removing this enum element will completely sever the legacy bundle from
	  * the Copper compile-time. */
	LEGACY
	{
		@Override
		Pipeline getPipeline(ParserCompilerParameters args)
		{
			try {
				Class<?> c = Pipeline.class.getClassLoader().loadClass("edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LegacyPipeline");
				return (Pipeline) c.newInstance();
			} catch(ClassNotFoundException ex) {
			} catch(IllegalAccessException ex) {
			} catch(InstantiationException ex) {	}
			return new ErrorPipeline();
			//return new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LegacyPipeline();
		}
		
		@Override
		boolean isAvailable() {
			return (!(getPipeline(null) instanceof ErrorPipeline));
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
			if(pt.isAvailable()) {
				fromStringTable.put(pt.stringName(),pt);
			}
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
	
	abstract boolean isAvailable();
	abstract Pipeline getPipeline(ParserCompilerParameters args);
	
	private static class ErrorPipeline implements Pipeline {

		@Override
		public int execute(ParserCompilerParameters args) throws IOException, CopperException {
			CompilerLogger logger = AuxiliaryMethods.getOrMakeLogger(args);
			if(logger.isLoggable(CompilerLevel.QUIET)) {
				logger.log(new GenericMessage(CompilerLevel.QUIET, "This pipeline is not available"));
			}
			return 1;
		}

		@Override
		public Set<String> getCustomSwitches() {
			return new HashSet<String>();
		}

		@Override
		public String customSwitchUsage() {
			return "";
		}

		@Override
		public int processCustomSwitch(ParserCompilerParameters args, String[] cmdline, int index) {
			return index;
		}
		
	}
}
