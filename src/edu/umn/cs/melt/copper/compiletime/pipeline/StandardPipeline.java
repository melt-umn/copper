package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.InterfaceErrorMessage;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.main.CopperIOType;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * The template for a "standard" pipeline that is divided into three tasks:
 * <ol>
 * <li>Parsing the input specification from a text or other specification into a structured form.</li>
 * <li>Compiling the parsed specification into an LR DFA, parse table, etc.</li>
 * <li>Converting that data into the source code of a parser.</li>
 * </ol>
 * 
 * To perform these tasks, the StandardPipeline holds objects of type {@link SpecParser},
 * {@link SpecCompiler}, and {@link SourceBuilder} respectively.
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 * @param <SCIN> The type used to hold the input specification in structured form (e.g., {@link ParserBean}).
 * @param <SCOUT> The type used to hold the compiled parser information prior to conversion to source code
 *                (e.g., {@link StandardSpecCompilerReturnData).
 */
public class StandardPipeline<SCIN,SCOUT> implements Pipeline,SpecParser<SCIN>,SpecCompiler<SCIN,SCOUT>,SourceBuilder<SCOUT>
{
	private HashSet<String> customSwitches;
	
	private SpecParser<SCIN> specParser;
	private SpecCompiler<SCIN,SCOUT> specCompiler;
	private SourceBuilder<SCOUT> sourceBuilder;
		
	public StandardPipeline(SpecParser<SCIN> specParser,
			SpecCompiler<SCIN, SCOUT> specCompiler,
			SourceBuilder<SCOUT> sourceBuilder)
	{
		super();
		this.specParser = specParser;
		this.specCompiler = specCompiler;
		this.sourceBuilder = sourceBuilder;
		
		customSwitches = new HashSet<String>();
		Set<String> buf;
		buf = specParser.getCustomSwitches(); 
		if(buf != null) customSwitches.addAll(buf);
		buf = specCompiler.getCustomSwitches();
		if(buf != null) customSwitches.addAll(buf);
		buf = sourceBuilder.getCustomSwitches();
		if(buf != null) customSwitches.addAll(buf);
	}

	@Override
	public int execute(ParserCompilerParameters args)
	throws IOException,CopperException
	{
		SCIN spec = null;
		SCOUT constructs = null;
		int errorlevel = 1;
		
		if(args.isAvoidRecompile())
		{
			CompilerLogger logger = AuxiliaryMethods.getOrMakeLogger(args);
			int result = checkIsRecompilation(args);
			if(result == -1)
			{
				logger.log(new GenericMessage(CompilerLevel.REGULAR,"Parser '" + args.getOutputFile() + "' is up-to-date"));
				return 0;
			}
			else if(result != 0) return 1;
		}
		
		spec = specParser.parseSpec(args);			
		if(spec != null)
		{
			constructs = specCompiler.compileParser(spec, args);
		}
		if(constructs != null)
		{
			errorlevel = sourceBuilder.buildSource(constructs, args); 
		}
		
		return errorlevel;
	}

	/**
	 * Executes only the compilation and source-code conversion phases of the pipeline,
	 * starting from a pre-existing specification.
	 * @param spec Input parser specification.
	 * @param args Input arguments. Any input filenames included as part of these arguments will be ignored.
	 * @return Return code: 0 if successful, non-zero if unsuccessful.
	 */
	public int execute(SCIN spec,ParserCompilerParameters args)
	throws CopperException
	{
		SCOUT constructs = null;
		int errorlevel = 1;
		constructs = specCompiler.compileParser(spec, args);
		if(constructs != null)
		{
			errorlevel = sourceBuilder.buildSource(constructs, args); 
		}
		
		return errorlevel;		
	}
	
	/**
	 * Checks if a parser would be a recompilation: if the last modification time of its 
	 * designated output file is later than the last modification time of any of its input
	 * files.
	 * @return -1 if the parser is up-to-date, 0 if not, a positive integer if there is an error.
	 */
	private int checkIsRecompilation(ParserCompilerParameters args)
	{
		long latestInputModification = 1L;
		for(Pair<String,Object> input : args.getInputs())
		{
			if(input.second() instanceof Reader)
			{
				args.getLogger().log(new InterfaceErrorMessage("When the 'avoidRecompile' option is set, all inputs must be files"));
				return 1;
			}
			File f = new File(input.second().toString());
			if(!f.exists())
			{
				args.getLogger().log(new InterfaceErrorMessage("Grammar file not found: '" + input.second() + "'"));				
			}
			else if(f.lastModified() == 0)
			{
				args.getLogger().log(new InterfaceErrorMessage("Error reading modification time for input file '" + input.second() + "'"));
			}
			else latestInputModification = Math.max(latestInputModification,f.lastModified());
		}
		if(args.getOutputType() != CopperIOType.FILE || args.getOutputFile() == null)
		{
			args.getLogger().log(new InterfaceErrorMessage("When the 'avoidRecompile' option is set, the output must be a file"));
			return 1;
		}
		else if(!args.getOutputFile().exists())
		{
			return 0;
		}
		else if(args.getOutputFile().lastModified() == 0)
		{
			args.getLogger().log(new InterfaceErrorMessage("Error reading modification time for output file '" + args.getOutputFile() + "'"));
			return 1;
		}
		else if(latestInputModification < args.getOutputFile().lastModified())
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}

	@Override
	public int buildSource(SCOUT constructs, SourceBuilderParameters args)
	throws CopperException
	{
		return sourceBuilder.buildSource(constructs,args);
	}

	@Override
	public SCOUT compileParser(SCIN spec, SpecCompilerParameters args)
	throws CopperException
	{
		return specCompiler.compileParser(spec,args);
	}

	@Override
	public SCIN parseSpec(SpecParserParameters args)
	throws IOException,CopperException
	{
		return specParser.parseSpec(args);
	}

	@Override
	public Set<String> getCustomSwitches()
	{
		return customSwitches;
	}

	@Override
	public String customSwitchUsage()
	{
		String rv = "";
		if(specParser != null) rv += specParser.customSwitchUsage();
		if(specCompiler != null) rv += specCompiler.customSwitchUsage();
		if(sourceBuilder != null) rv += sourceBuilder.customSwitchUsage();
		return rv;
	}

	@Override
	public int processCustomSwitch(ParserCompilerParameters args,
			String[] cmdline, int index)
	{
		int rv;
		rv = specParser.processCustomSwitch(args,cmdline,index);
		if(rv != -1) return rv;
		rv = specCompiler.processCustomSwitch(args,cmdline,index);
		if(rv != -1) return rv;
		rv = sourceBuilder.processCustomSwitch(args,cmdline,index);
		if(rv != -1) return rv;
		return -1;
	}
}