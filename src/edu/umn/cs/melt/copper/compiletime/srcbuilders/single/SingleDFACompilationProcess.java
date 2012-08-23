package edu.umn.cs.melt.copper.compiletime.srcbuilders.single;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.FinalReportMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.TimingMessage;
import edu.umn.cs.melt.copper.compiletime.pipeline.AuxiliaryMethods;
import edu.umn.cs.melt.copper.compiletime.pipeline.StandardSpecCompilerReturnData;
import edu.umn.cs.melt.copper.compiletime.pipeline.SourceBuilder;
import edu.umn.cs.melt.copper.compiletime.pipeline.SourceBuilderParameters;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class SingleDFACompilationProcess implements SourceBuilder<StandardSpecCompilerReturnData>
{
	boolean outputSource;
	
	public SingleDFACompilationProcess(boolean outputSource)
	{
		this.outputSource = outputSource;
	}

	@Override
	public int buildSource(StandardSpecCompilerReturnData c,SourceBuilderParameters args)
	throws CopperException
	{
		CompilerLogger logger = AuxiliaryMethods.getOrMakeLogger(args);
		
		
		PrintStream out;

		if(c.succeeded && outputSource)
		{
			if(args.getOutputType() == null) out = null;
			else switch(args.getOutputType())
			{
			case FILE:
				try
				{
					out = new PrintStream(args.getOutputFile());
				}
				catch(FileNotFoundException ex)
				{
					logger.logError(new GenericMessage(CompilerLevel.QUIET,"Output file " + args.getOutputFile() + " could not be opened for writing",true,false));
					c.succeeded = false;
					c.errorlevel = 2;
					return 2;
				}
				c.stats.codeOutputTo = args.getOutputFile().toString();
				break;
			case STREAM:
				out = args.getOutputStream();
				if(out == System.out) c.stats.codeOutputTo = "<standard output>";
				else c.stats.codeOutputTo = "<stream " + Integer.toHexString(out.hashCode()) + ">";
				break;
			default:
				out = null;
			}
		}
		else out = null;
		
		if(out != null)
		{
			long timeBefore;
			
			//boolean isPretty = args.isPretty();
			//boolean gatherStatistics = args.isGatherStatistics();
			//String runtimeQuietLevel = args.getRuntimeQuietLevel();
	
			String rootType = c.symbolTable.getNonTerminal(c.fullSpec.pr.getRHSSym(c.fullSpec.getStartProduction(),0)).getReturnType();
			if(rootType == null) rootType = Object.class.getName();
			//String errorType = CopperParserException.class.getName();
			//String ancillaries = edu.umn.cs.melt.copper.compiletime.srcbuilders.single.MainFunctionBuilders.buildSingleDFAParserAncillaries(c.packageDecl,c.parserName,false,false,runtimeQuietLevel) + 
		    //          edu.umn.cs.melt.copper.compiletime.srcbuilders.single.MainFunctionBuilders.buildSingleDFAParserMainFunction(c.packageDecl,c.parserName,rootType,errorType,false,false,runtimeQuietLevel);
			SingleDFAEngineBuilder engineBuilder = new SingleDFAEngineBuilder(c.symbolTable, c.fullSpec, c.lookaheadSets, c.parseTable, c.prefixes, c.scannerDFA, c.scannerDFAAnnotations);
				
			try
			{
				timeBefore = System.currentTimeMillis();
				engineBuilder.buildLALREngine(out,
					       ((c.packageDecl == null || c.packageDecl.equals("")) ? "" : "package " + c.packageDecl + ";"),
				           "",
				           c.parserName,c.parserName + "Scanner",
				           "public " + c.parserName + "() {}\n\n",
				           "");
				if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Generating parser code",System.currentTimeMillis() - timeBefore));
				logger.flush();
				c.stats.codeOutput = true;
			}
			catch(IOException ex)
			{
				if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) ex.printStackTrace(System.err);
				logger.logError(new GenericMessage(CompilerLevel.QUIET,"I/O error in code generation: " + ex.getMessage(),true,true));
				return 1;
			}
		}
		
		logger.log(new FinalReportMessage(c.stats));
		
		logger.flush();
		
		return c.errorlevel;
	}
}
