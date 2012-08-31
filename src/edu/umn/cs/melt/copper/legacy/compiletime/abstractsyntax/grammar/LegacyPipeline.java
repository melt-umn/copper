package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammarbeans.visitors.ParserSpecProcessor;
import edu.umn.cs.melt.copper.legacy.compiletime.concretesyntax.GrammarParser;
import edu.umn.cs.melt.copper.legacy.compiletime.concretesyntax.oldxml.XMLGrammarParser;
import edu.umn.cs.melt.copper.legacy.compiletime.concretesyntax.skins.cup.CupSkinParser;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.lalr1.LALR1DFABuilder;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.FinalReporter;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.GrammarDumper;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.PlainTextGrammarDumper;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.StringBasedCompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.XMLGrammarDumper;
import edu.umn.cs.melt.copper.legacy.compiletime.semantics.lalr1.ComposabilityChecker;
import edu.umn.cs.melt.copper.legacy.compiletime.semantics.lalr1.WellFormedGrammarChecker;
import edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.EngineBuilder;
import edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.lalr.LALREngineBuilder;
import edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.moded.ModedEngineBuilder;
import edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.single.SingleDFAEngineBuilder;
import edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.split.SplitEngineBuilder;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogHandler;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.PrintCompilerLogHandler;
import edu.umn.cs.melt.copper.compiletime.pipeline.AuxiliaryMethods;
import edu.umn.cs.melt.copper.compiletime.pipeline.Pipeline;
import edu.umn.cs.melt.copper.compiletime.pipeline.UniversalProcessParameters;
import edu.umn.cs.melt.copper.compiletime.skins.xml.XMLSkinParser;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.main.CopperEngineType;
import edu.umn.cs.melt.copper.main.CopperSkinType;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public class LegacyPipeline implements Pipeline
{
	private HashSet<String> customParameters;
	
	public LegacyPipeline()
	{
		customParameters = new HashSet<String>();
		customParameters.add("isPretty");
		customParameters.add("gatherStatistics");
		customParameters.add("runtimeQuietLevel");
	}
	
	private edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger getOldStyleLogger(CompilerLogger newStyleLogger,UniversalProcessParameters args)
	{
		edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger logger = new StringBasedCompilerLogger();
		CompilerLogHandler handler = newStyleLogger.getHandler();
		if(handler instanceof PrintCompilerLogHandler)
		{
			logger.setOut(((PrintCompilerLogHandler) handler).getOut());
		}
		switch(newStyleLogger.getLevel())
		{
		case MUTE:
		case QUIET:
			logger.setLevel(CompilerLogMessageSort.getQuietSort().getLevel());
			break;
		case REGULAR:
			logger.setLevel(CompilerLogMessageSort.getDefaultSort().getLevel());
			break;
		case VERBOSE:
			logger.setLevel(CompilerLogMessageSort.getVerboseSort().getLevel());
			break;
		case VERY_VERBOSE:
			logger.setLevel(CompilerLogMessageSort.getVeryVerboseSort().getLevel());
			break;
		}
		return logger;
	}
	

	@SuppressWarnings("deprecation")
	private GrammarSource parseInputGrammarLegacy(ParserCompilerParameters args)
	{
		ArrayList< Pair<String,Reader> > files = args.getFiles(); 
		boolean isComposition = args.isComposition();
		CopperSkinType useSkin = args.getUseSkin();
		CompilerLogger newStyleLogger;

		if(isComposition &&
		   ((useSkin != CopperSkinType.XML && files.size() != 2) ||
		    (useSkin == CopperSkinType.XML && files.size() != 1)))
		{
			System.err.println("Switch -compose requires exactly two input grammars");
			return null;
		}
		newStyleLogger = AuxiliaryMethods.getOrMakeLogger(args);
		edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger logger = getOldStyleLogger(newStyleLogger,args);

		WellFormedGrammarChecker wfcheck = new WellFormedGrammarChecker(logger);
		
		GrammarSource grammar = null;
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"Reading grammar");
		switch(useSkin)
		{
		case NATIVE:
			try { grammar = GrammarParser.parseGrammar(files,logger); }
			catch(Exception ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) System.err.println();
				if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) ex.printStackTrace(System.err);
				return null;
			}
			grammar.getParserSources().setPackageName(args.getPackageDecl());
			grammar.getParserSources().setParserName(args.getParserName());
			break;
		case OLD_XML:
			try { grammar = XMLGrammarParser.parseGrammar(files,logger); }
			catch(CopperException ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) System.err.println();
				if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) ex.printStackTrace(System.err);
				return null;
			}
			catch(Exception ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) System.err.println();
				if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) ex.printStackTrace(System.err);
				else System.err.println("An unexpected fatal error has occurred. Run with -vv for debug information.");
				return null;
			}
			if(args.getPackageDecl() != null) grammar.getParserSources().setPackageName(args.getPackageDecl());
			if(args.getParserName() != null && !args.getParserName().equals("")) grammar.getParserSources().setParserName(args.getParserName());
			break;
		case XML:
			try
			{
				ParserBean parser = new XMLSkinParser(files,newStyleLogger).parse();
				grammar = ParserSpecProcessor.buildGrammarSource(parser,newStyleLogger);
			}
			catch(CopperException ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) System.err.println();
				if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) ex.printStackTrace(System.err);
				return null;
			}
			catch(Exception ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) System.err.println();
				if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) ex.printStackTrace(System.err);
				else System.err.println("An unexpected fatal error has occurred. Run with -vv for debug information.");
				return null;
			}
			if(args.getPackageDecl() != null) grammar.getParserSources().setPackageName(args.getPackageDecl());
			if(args.getParserName() != null && !args.getParserName().equals("")) grammar.getParserSources().setParserName(args.getParserName());
			break;
		case CUP:
		default:
			try { grammar = CupSkinParser.parseGrammar(files,logger); }
			catch(Exception ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) System.err.println();
				if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) ex.printStackTrace(System.err);
				return null;
			}
			if(args.getPackageDecl() != null) grammar.getParserSources().setPackageName(args.getPackageDecl());
			if(args.getParserName() != null && !args.getParserName().equals("")) grammar.getParserSources().setParserName(args.getParserName());
		}
		if(grammar != null)
		{
			if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) logger.logMessage(CompilerLogMessageSort.DEBUG,null,grammar.toString());
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\nChecking grammar well-formedness");

			try
			{
				wfcheck.checkWellFormedness(grammar,args.isWarnUselessNTs());
			}
			catch(CopperException ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) System.err.println();
				if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) ex.printStackTrace(System.err);
				grammar = null;
			}
		}
		return grammar;
	}

	@SuppressWarnings("deprecation")
	private int compileParserLegacy(ParserCompilerParameters args,GrammarSource grammar)
	throws CopperException
	{
		boolean isPretty = args.getCustomParameter("isPretty", Boolean.class, Boolean.FALSE);
		if(args.hasCustomParameter("isPretty") && args.getCustomParameter("isPretty") instanceof Boolean) isPretty = (Boolean) args.getCustomParameter("isPretty");
		boolean isComposition = args.isComposition();
		boolean gatherStatistics = args.getCustomParameter("gatherStatistics", Boolean.class, Boolean.FALSE);
		CopperEngineType useEngine = args.getUseEngine();
		CompilerLogger newStyleLogger = args.getLogger();
		edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger logger = getOldStyleLogger(newStyleLogger,args);
		String packageDecl = 
				(args.getPackageDecl() != null && !args.getPackageDecl().equals("")) ?
						args.getPackageDecl() :
						(grammar.getParserSources().getPackageName() != null && !grammar.getParserSources().getPackageName().equals("") ?
								grammar.getParserSources().getPackageName() : 
						        "");
		String parserName =
			(args.getParserName() != null && !args.getParserName().equals("")) ?
					args.getParserName() :
					(grammar.getParserSources().getParserName() != null && !grammar.getParserSources().getParserName().equals("") ?
							grammar.getParserSources().getParserName() : 
					        "Parser");
		String runtimeQuietLevel = args.getCustomParameter("runtimeQuietLevel",String.class,"ERROR");

		if(useEngine != CopperEngineType.OLD_AND_SLOW && gatherStatistics)
		{
			System.err.println("Error: No facilities for gathering statistics in newer parse engines.\nUse -oldnslow in addition to -gatherstats.");
			return 1;
		}
		HashSet<GrammarName> containedGrammars = new HashSet<GrammarName>();
		containedGrammars.add(new GrammarName(FringeSymbols.STARTPRIME.getId()));
		for(GrammarName gn : grammar.getContainedGrammars()) containedGrammars.add(gn);
		LALR1DFABuilder builder = new LALR1DFABuilder(grammar,containedGrammars,logger);
		LALR1DFABuilder hostBuilder = null;
		GrammarSource hostGrammar = null;
		HashSet<GrammarName> hostOnly = new HashSet<GrammarName>();
		hostOnly.add(new GrammarName(FringeSymbols.STARTPRIME.getId()));
		if(isComposition)
		{
			hostOnly.add(grammar.getHostGrammarName());
			hostGrammar = ComposabilityChecker.extractWantedGrammars(grammar,hostOnly);
			// DEBUG-X-BEGIN
			//System.err.println(hostGrammar + "\n=========\n" + grammar);
			// DEBUG-X-END
			hostBuilder = new LALR1DFABuilder(grammar,hostOnly,logger);
		}
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\nComputing context sets");
		try
		{
			grammar.getContextSets().compute(grammar,containedGrammars,logger);
			if(isComposition)
			{
				hostGrammar.getContextSets().compute(hostGrammar,hostOnly,logger);
			}
		}
		catch(CopperException ex)
		{
			return 1;
		}
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\nBuilding LR DFA");
		try
		{
			builder.buildDFA();
			if(isComposition) hostBuilder.buildDFA();
		}
		catch(CopperException ex)
		{
			return 1;
		}
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\nLALRizing DFA");
		try
		{
			builder.LALRize();
			if(isComposition) hostBuilder.LALRize();
		}
		catch(CopperException ex)
		{
			return 1;
		}
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\nBuilding parse table");
		try
		{
			builder.buildLALR1Table();
			if(isComposition) hostBuilder.buildLALR1Table();
		}
		catch(CopperException ex)
		{
			return 1;
		}
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\nCulling parse table conflicts");
		try
		{
			builder.cullConflictsLALR1();
			if(isComposition) hostBuilder.cullConflictsLALR1();
		}
		catch(CopperException ex)
		{
			return 1;
		}
		if(isComposition)
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\nChecking grammar composability");

			ComposabilityChecker cchecker = new ComposabilityChecker(logger);
			boolean passed = false;
			try { passed = cchecker.checkComposability(hostGrammar,grammar,hostBuilder.getLALR1DFA(),builder.getLALR1DFA()); }
			catch(CopperException ex) { System.exit(1); }
			if(!passed)
			{
				if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logMessage(CompilerLogMessageSort.ERROR,null,"Grammars " + containedGrammars + " not composable");
			}
		}

		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\nGenerating parser source code...\n");

		PrintStream out = null;
		
		if(args.getOutputType() == null) out = System.out;
		else switch(args.getOutputType())
		{
		case FILE:
			try
			{
				out = new PrintStream(args.getOutputFile());
			}
			catch(FileNotFoundException ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,"Output file " + args.getOutputFile() + " could not be opened for writing");
				out = System.out;
			}
			break;
		case STREAM:
			out = args.getOutputStream();
			break;
		}
		
		EngineBuilder engineBuilder;
		String ancillaries;
		String rootType = grammar.getNTAttributes(grammar.getStartSym()).getType();
		String errorType = CopperParserException.class.getName();

		switch(useEngine)
		{
		case SINGLE:
			engineBuilder = new SingleDFAEngineBuilder(builder.getGrammar(),builder.getLALR1DFA(),builder.getParseTable(),logger);
			ancillaries = edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.single.MainFunctionBuilders.buildSingleDFAParserAncillaries(packageDecl,parserName,gatherStatistics,isPretty,runtimeQuietLevel) + 
			              edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.single.MainFunctionBuilders.buildSingleDFAParserMainFunction(packageDecl,parserName,rootType,errorType,gatherStatistics,isPretty,runtimeQuietLevel);
			break;
		case MODED:
			engineBuilder = new ModedEngineBuilder(builder.getGrammar(),builder.getLALR1DFA(),builder.getParseTable(),logger);
			ancillaries = edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.moded.MainFunctionBuilders.buildModedParserAncillaries(packageDecl,parserName,gatherStatistics,isPretty,runtimeQuietLevel) +
			              edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.moded.MainFunctionBuilders.buildModedParserMainFunction(packageDecl,parserName,rootType,errorType,gatherStatistics,isPretty,runtimeQuietLevel);
			break;
		case SPLIT:
			engineBuilder = new SplitEngineBuilder(builder.getGrammar(),builder.getLALR1DFA(),builder.getParseTable(),logger);
			ancillaries = edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.split.MainFunctionBuilders.buildSplitParserAncillaries(packageDecl,parserName,gatherStatistics,isPretty,runtimeQuietLevel) +
			              edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.split.MainFunctionBuilders.buildSplitParserMainFunction(packageDecl,parserName,rootType,errorType,gatherStatistics,isPretty,runtimeQuietLevel);
			break;
		case OLD_AND_SLOW:
		default:
			engineBuilder = new LALREngineBuilder(builder.getGrammar(),builder.getLALR1DFA(),builder.getParseTable(),logger);
			ancillaries = edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.lalr.MainFunctionBuilders.buildGeneralParserAncillaries(packageDecl,parserName,gatherStatistics,isPretty,runtimeQuietLevel);
		}
		try
		{
			engineBuilder.buildLALREngine(out,
					       ((packageDecl == null || packageDecl.equals("")) ? "" : "package " + packageDecl + ";"),
				           "",
				           parserName,parserName + "Scanner",
				           ancillaries,
				           "");
		}
		catch(IOException ex)
		{
			System.err.println("I/O error in code generation");
			ex.printStackTrace(System.err);
			return 1;
		}
		catch(CopperException ex)
		{
			if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) ex.printStackTrace(System.err);
			return 1;
		}
		catch(Exception ex)
		{
			System.err.println("Unexpected error in code generation");
			ex.printStackTrace(System.err);
			return 1;
		}
		if(args.isDumpReport() && (!args.isDumpOnlyOnError() || logger.hasErrors()))
		{
			PrintStream dumpStream = null;
			if(!args.isDumpReport() || args.getDumpFile().equals("") || args.getDumpFile().equals(args.getLogFile())) dumpStream = logger.getOut();
			else
			{
				try { dumpStream = new PrintStream(new FileOutputStream(args.getDumpFile())); }
				catch(FileNotFoundException ex)
				{
					ex.printStackTrace();
				}
			}
			GrammarDumper dumper = null;
			switch(args.getDumpFormat())
			{
			case HTML:
			case XML:
				try
				{
					dumper = new XMLGrammarDumper(dumpStream,grammar,builder.getLALR1DFA(),builder.getParseTable());
				}
				catch(ParserConfigurationException ex)
				{
					ex.printStackTrace();
				}
				break;
			case PLAIN:
				dumper = new PlainTextGrammarDumper(dumpStream,grammar,builder.getLALR1DFA(),builder.getParseTable());
				break;
			default:
				System.err.println("Invalid dump type -- bug in CLI.");
				System.exit(1);
			}
			dumper.completeDump();
			switch(args.getDumpFormat())
			{
			case HTML:
				dumper.logHTML();
				break;
			case XML:
				dumper.logXML();
				break;
			case PLAIN:
				dumper.logPlain();
				break;
			default:
				// Blank
			}
		}
		FinalReporter finalReporter = new FinalReporter(logger,grammar,builder.getParseTable(),engineBuilder.getScannerStateCount());
		if(logger.isLoggable(CompilerLogMessageSort.FINAL_REPORT))
		{
			finalReporter.logFinalReport();
		}
		logger.flushMessages();
		return 0;
	}
	
	@Override
	public int execute(ParserCompilerParameters args)
	{
		GrammarSource grammar = parseInputGrammarLegacy(args);
		int errorlevel;
		if(grammar == null) errorlevel = 1;
		else
		{
			try
			{
				errorlevel = compileParserLegacy(args,grammar);
			}
			catch(CopperException ex)
			{
				if(args.getLogger().isLoggable(CompilerLevel.VERY_VERBOSE)) ex.printStackTrace(System.err);
				//else System.err.println(ex.getMessage());
				errorlevel = 1;
			}
		}
		return errorlevel;
	}


	@Override
	public Set<String> getCustomParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int processCustomParameter(ParserCompilerParameters args, String[] cmdline, int index)
	{
		if(cmdline[index].equals("-pretty"))
		{
			args.setCustomParameter("isPretty",true);
			return index+1;
		}
		else if(cmdline[index].equals("-gatherstats"))
		{
			if(args.getCustomParameter("gatherStatistics",String.class,"ERROR").equals("ERROR")) args.setCustomParameter("runtimeQuietLevel","NOTA_BENE");
			args.setCustomParameter("gatherStatistics",true);
			return index+1;
		}
		else if(cmdline[index].equals("-runv"))
		{
			args.setCustomParameter("runtimeQuietLevel","INFO");
			return index+1;
		}
		else return -1;		
	}

	@Override
	public String customParameterUsage()
	{
		String rv = "";
		rv += "\t-gatherstats\tSet the output parser to print parsing statistics\n";
		rv += "\t\t\tinstead of a parse tree.\n";
		rv += "\t-pretty\t\tSet the output parser to \"pretty-print\" its output in\n";
		rv += "\t\t\thuman-readable form.\n";
		rv += "\t-runv\t\tSet the output parser to run with extra verbosity.\n";
		return rv;
	}
}
