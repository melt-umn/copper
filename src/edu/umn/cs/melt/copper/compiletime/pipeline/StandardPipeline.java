package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * The template for a "standard" pipeline divided into three tasks:
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
	}

	@Override
	public int execute(ParserCompilerParameters args)
	{
		SCIN spec = null;
		SCOUT constructs = null;
		int errorlevel = 1;
		try
		{
			spec = specParser.parseSpec(args);			
			if(spec != null)
			{
				constructs = specCompiler.compileParser(spec, args);
				if(constructs != null)
				{
					errorlevel = sourceBuilder.buildSource(constructs, args); 
				}
			}
		}
		catch(CopperException ex)
		{
			// Intentionally blank
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
	{
		SCOUT constructs = null;
		int errorlevel = 1;
		try
		{
			constructs = specCompiler.compileParser(spec, args);
			if(constructs != null)
			{
				errorlevel = sourceBuilder.buildSource(constructs, args); 
			}
		}
		catch(CopperException ex)
		{
			// Intentionally blank
		}
		
		return errorlevel;		
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
	throws CopperException
	{
		return specParser.parseSpec(args);
	}
}