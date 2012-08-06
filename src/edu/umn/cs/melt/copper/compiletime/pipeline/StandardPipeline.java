package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

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
	public int compile(ParserCompilerParameters args)
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

	public int compile(SCIN spec,ParserCompilerParameters args)
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