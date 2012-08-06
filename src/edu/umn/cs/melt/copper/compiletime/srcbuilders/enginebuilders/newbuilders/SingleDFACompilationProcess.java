package edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.newbuilders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.messages.FinalReportMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.messages.TimingMessage;
import edu.umn.cs.melt.copper.compiletime.pipeline.AuxiliaryMethods;
import edu.umn.cs.melt.copper.compiletime.pipeline.CompilerReturnData;
import edu.umn.cs.melt.copper.compiletime.pipeline.SourceBuilder;
import edu.umn.cs.melt.copper.compiletime.pipeline.SourceBuilderParameters;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public class SingleDFACompilationProcess implements SourceBuilder<CompilerReturnData>
{
	boolean outputSource;
	
	public SingleDFACompilationProcess(boolean outputSource)
	{
		this.outputSource = outputSource;
	}

	@Override
	public int buildSource(CompilerReturnData c,SourceBuilderParameters args)
	throws CopperException
	{
		CompilerLogger oldStyleLogger;
		oldStyleLogger = AuxiliaryMethods.getOrMakeLogger(args);
		edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogger logger = AuxiliaryMethods.getNewStyleLogger(oldStyleLogger,args);
		
		
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
					logger.logError(new GenericMessage(CompilerLevel.MUTE,"Output file " + args.getOutputFile() + " could not be opened for writing"));
					c.succeeded = false;
					c.errorlevel = 2;
					return 2;
				}
				break;
			case STREAM:
				out = args.getOutputStream();
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
			String runtimeQuietLevel = args.getRuntimeQuietLevel();
	
			String rootType = c.symbolTable.getNonTerminal(c.fullSpec.pr.getRHSSym(c.fullSpec.getStartProduction(),0)).getReturnType();
			if(rootType == null) rootType = Object.class.getName();
			String errorType = CopperParserException.class.getName();
			String ancillaries = edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.single.MainFunctionBuilders.buildSingleDFAParserAncillaries(c.packageDecl,c.parserName,false,false,runtimeQuietLevel) + 
		              edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.single.MainFunctionBuilders.buildSingleDFAParserMainFunction(c.packageDecl,c.parserName,rootType,errorType,false,false,runtimeQuietLevel);
			SingleDFAEngineBuilderNew engineBuilder = new SingleDFAEngineBuilderNew(c.symbolTable, c.fullSpec, c.lookaheadSets, c.parseTable, c.prefixes, c.scannerDFA, c.scannerDFAAnnotations);
				
			try
			{
				timeBefore = System.currentTimeMillis();
				engineBuilder.buildLALREngine(out,
					       ((c.packageDecl == null || c.packageDecl.equals("")) ? "" : "package " + c.packageDecl + ";"),
				           "",
				           c.parserName,c.parserName + "Scanner",
				           ancillaries,
				           "");
				if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Generating parser code",System.currentTimeMillis() - timeBefore));
				logger.flush();
			}
			catch(IOException ex)
			{
				System.err.println("I/O error in code generation");
				ex.printStackTrace(System.err);
				return 1;
			}
			catch(CopperException ex)
			{
				if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) ex.printStackTrace(System.err);
				return 1;
			}
			catch(Exception ex)
			{
				System.err.println("Unexpected error in code generation");
				ex.printStackTrace(System.err);
				return 1;
			}
		}

		logger.log(new FinalReportMessage(c.stats));
		
		logger.flush();
		
		return c.errorlevel;
	}
}
