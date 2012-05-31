package edu.umn.cs.melt.copper.main;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarName;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.NumericParserSpecBuilder;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.ParserSpecPlaintextPrinter;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.ParserSpecProcessor;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.SymbolTableBuilder;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ContextSets;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.builders.ContextSetBuilder;
import edu.umn.cs.melt.copper.compiletime.builders.LALRLookaheadAndLayoutSetBuilder;
import edu.umn.cs.melt.copper.compiletime.builders.LR0DFABuilder;
import edu.umn.cs.melt.copper.compiletime.builders.LRParseTableBuilder;
import edu.umn.cs.melt.copper.compiletime.builders.TransparentPrefixSetBuilder;
import edu.umn.cs.melt.copper.compiletime.concretesyntax.GrammarParser;
import edu.umn.cs.melt.copper.compiletime.concretesyntax.oldxml.XMLGrammarParser;
import edu.umn.cs.melt.copper.compiletime.concretesyntax.skins.cup.CupSkinParser;
import edu.umn.cs.melt.copper.compiletime.concretesyntax.skins.xml.XMLSkinParser;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.lalr1.LALR1DFABuilder;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LRDFAPrinter;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.FinalReporter;
import edu.umn.cs.melt.copper.compiletime.logging.GrammarDumper;
import edu.umn.cs.melt.copper.compiletime.logging.PlainTextGrammarDumper;
import edu.umn.cs.melt.copper.compiletime.logging.StringBasedCompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.XMLGrammarDumper;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTablePrinter;
import edu.umn.cs.melt.copper.compiletime.semantics.lalr1.ComposabilityChecker;
import edu.umn.cs.melt.copper.compiletime.semantics.lalr1.WellFormedGrammarChecker;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.EngineBuilder;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.lalr.LALREngineBuilder;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.moded.ModedEngineBuilder;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.single.SingleDFAEngineBuilder;
import edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.split.SplitEngineBuilder;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;


/**
 * Running frontend for the entire parser compiler.
 * The <code>main()</code> method contains the command-line interface,
 * while the <code>compile()</code> methods provide a Java API
 * usable by ANT tasks or other Java methods.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class ParserCompiler
{
	private static String usageMessage()
	{
		String rv = "";
		rv += "Usage: ParserCompiler [switches] grammar-file1 grammar-file2 ... grammar-filen > package/class.java\n";
		rv += "Switches (all optional) are:\n";
		rv += "\t-?\t\tDisplay this usage information.\n";
		rv += "\t-package [package]\tThe package of the generated parser.\n\t\tDefaults to the default package or what is set in\n\t\tthe parser specification.\n";
		rv += "\t-parser [class]\tThe class name of the generated parser.\n\t\tDefaults to 'Parser' or what is set in\n\t\tthe parser specification.\n";
		rv += "\t-q\t\tRun the compiler quietly.\n";
		rv += "\t-v\t\tRun the compiler with extra verbosity.\n";
		rv += "\t-vv\t\tRun the compiler with even more extra verbosity.\n";
		rv += "\t-gatherstats\tSet the output parser to print parsing statistics\n";
		rv += "\t\t\tinstead of a parse tree. DEPRECATED.\n";
		rv += "\t-runv\t\tSet the output parser to run with extra verbosity.\n";
		rv += "\t-pretty\t\tSet the output parser to \"pretty-print\" its output in\n";
		rv += "\t\t\thuman-readable form. DEPRECATED.\n";
		rv += "\t-compose\tUse with exactly two input files, the first the host\n";
		rv += "\t\t\tgrammar and the second an extension, to test the\n";
		rv += "\t\t\textension's composability. EXPERIMENTAL.\n";
		rv += "\t-logfile [lout]\tPipe all log output to the file 'lout'\n\t\t\t(default standard error).\n";
		rv += "\t-dump\tProduce a detailed report of the grammar and generated parser.\n";
		rv += "\t-errordump\tProduce a detailed report, but only if the parser compiler has generated an error.\n";
		rv += "\t-dumpfile [dout]\tPipe the dumped report to the file 'dout'\n\t\t\t\t(default to log output).\n";
		rv += "\t-dumptype [type]\tGenerate the dumped report in the specified format (plain, xml, html).\n";
		rv += "\t-skin [skin]\tGenerate a parser based on input from the specified input skin:\n";
		rv += "\t\tnative: The original input format used by Silver. DEPRECATED.\n";
		rv += "\t\toldxml: The original XML input schema. DEPRECATED.\n";
		rv += "\t\txml: An XML input schema.\n";
		rv += "\t\tcup: (DEFAULT) A JavaCUP-like skin.\n";
		rv += "\t-engine [eng]\tGenerate a parser based on the specified parsing engine:\n";
		rv += "\t\toldnslow: The original JCF-based parsing engine.\n\t\t           Not included in 'CopperRuntime.jar'.\n";
		rv += "\t\tsingle: (DEFAULT) A parsing engine with a single scanner for all parsing contexts.\n";
		rv += "\t\tmoded: An engine with a separate scanner for each different parsing context. EXPERIMENTAL.\n";
		rv += "\t\tsplit: An engine made for assembling pieces of parse tables on-the-fly. EXPERIMENTAL.";
		return rv;
	}
	
	private static void usageMessageNoError()
	{
		System.err.println(usageMessage());
		System.exit(0);
	}
	
	private static void usageMessageError()
	{
		System.err.println(usageMessage());
		System.exit(1);
	}
	
	/**
	 * Returns Copper's default parse-engine target, which is currently {@link CopperEngineType#SINGLE}.
	 */
	public static CopperEngineType getDefaultEngine()
	{
		return CopperEngineType.SINGLE;
	}
	/**
	 * Returns Copper's default input skin, which is currently {@link CopperSkinType#CUP}.
	 */
	public static CopperSkinType getDefaultSkin()
	{
		return CopperSkinType.CUP;
	}
	/**
	 * Returns the default format of Copper grammar dumps, which is currently {@link CopperDumpType#PLAIN}. 
	 */
	public static CopperDumpType getDefaultDumpType()
	{
		return CopperDumpType.PLAIN;
	}
		
	private static CompilerLogger getOrMakeLogger(ParserCompilerParameters args)
	{
		CompilerLogger logger;
		if(args.getLogger() == null)
		{
			PrintStream log = null;
			if(args.getLogFile().equals("")) log = System.err;
			else
			{
				try { log = new PrintStream(new FileOutputStream(args.getLogFile())); }
				catch(FileNotFoundException ex)
				{
					ex.printStackTrace();
					return null;
				}
			}
			logger = new StringBasedCompilerLogger();
			logger.setOut(log);
			logger.setLevel(args.getQuietLevel().getLevel());
			args.setLogger(logger);
		}
		else logger = args.getLogger();
		return logger;
	}
	
	@SuppressWarnings("deprecation")
	private static GrammarSource parseInputGrammar(ParserCompilerParameters args)
	{
		ArrayList< Pair<String,Reader> > files = args.getFiles(); 
		boolean isComposition = args.isComposition();
		CopperSkinType useSkin = args.getUseSkin();
		CompilerLogger logger;

		if(isComposition &&
		   ((useSkin != CopperSkinType.XML && files.size() != 2) ||
		    (useSkin == CopperSkinType.XML && files.size() != 1)))
		{
			System.err.println("Switch -compose requires exactly two input grammars");
			usageMessageError();
		}
		logger = getOrMakeLogger(args);

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
				ParserBean parser = new XMLSkinParser(files,logger).parse();
				grammar = ParserSpecProcessor.buildGrammarSource(parser,logger);
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
	private static int compileParser(ParserCompilerParameters args,GrammarSource grammar)
	throws CopperException
	{
		boolean isPretty = args.isPretty();
		boolean isComposition = args.isComposition();
		boolean gatherStatistics = args.isGatherStatistics();
		CopperEngineType useEngine = args.getUseEngine();
		CompilerLogger logger = args.getLogger();
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
		String runtimeQuietLevel = args.getRuntimeQuietLevel();

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

		PrintStream out = args.getOutput();
		EngineBuilder engineBuilder;
		String ancillaries;
		String rootType = grammar.getNTAttributes(grammar.getStartSym()).getType();
		String errorType = CopperParserException.class.getName();

		switch(useEngine)
		{
		case SINGLE:
			engineBuilder = new SingleDFAEngineBuilder(builder.getGrammar(),builder.getLALR1DFA(),builder.getParseTable(),logger);
			ancillaries = edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.single.MainFunctionBuilders.buildSingleDFAParserAncillaries(packageDecl,parserName,gatherStatistics,isPretty,runtimeQuietLevel) + 
			              edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.single.MainFunctionBuilders.buildSingleDFAParserMainFunction(packageDecl,parserName,rootType,errorType,gatherStatistics,isPretty,runtimeQuietLevel);
			break;
		case MODED:
			engineBuilder = new ModedEngineBuilder(builder.getGrammar(),builder.getLALR1DFA(),builder.getParseTable(),logger);
			ancillaries = edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.moded.MainFunctionBuilders.buildModedParserAncillaries(packageDecl,parserName,gatherStatistics,isPretty,runtimeQuietLevel) +
			              edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.moded.MainFunctionBuilders.buildModedParserMainFunction(packageDecl,parserName,rootType,errorType,gatherStatistics,isPretty,runtimeQuietLevel);
			break;
		case SPLIT:
			engineBuilder = new SplitEngineBuilder(builder.getGrammar(),builder.getLALR1DFA(),builder.getParseTable(),logger);
			ancillaries = edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.split.MainFunctionBuilders.buildSplitParserAncillaries(packageDecl,parserName,gatherStatistics,isPretty,runtimeQuietLevel) +
			              edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.split.MainFunctionBuilders.buildSplitParserMainFunction(packageDecl,parserName,rootType,errorType,gatherStatistics,isPretty,runtimeQuietLevel);
			break;
		case OLD_AND_SLOW:
		default:
			engineBuilder = new LALREngineBuilder(builder.getGrammar(),builder.getLALR1DFA(),builder.getParseTable(),logger);
			ancillaries = edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.lalr.MainFunctionBuilders.buildGeneralParserAncillaries(packageDecl,parserName,gatherStatistics,isPretty,runtimeQuietLevel);
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
			switch(args.getDumpType())
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
			switch(args.getDumpType())
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
	
	/**
	 * Compiles a parser from a Java-object-based parser specification.
	 * @param spec The parser specification.
	 * @param args Arguments to the parser generator. Attempting to include any input files in this object will result in an error.
	 * @return 0 if successful, non-zero if an error occurred.
	 * @throws CopperException If an error occurred during compilation.
	 */
	public static int compile(ParserBean spec,ParserCompilerParameters args)
	throws CopperException
	{
		CompilerLogger logger = getOrMakeLogger(args);
		if(args.getFiles() != null)
		{
			if(logger.isLoggable(CompilerLogMessageSort.PARSING_ERROR)) logger.logMessage(CompilerLogMessageSort.PARSING_ERROR,null,"Input files cannot be specified when compiling a parser from Java objects");
			return 1;
		}
		ParserSpecProcessor.normalizeParser(spec,logger);
		GrammarSource grammar = ParserSpecProcessor.buildGrammarSource(spec,logger);

		return compileParser(args,grammar);
	}

	public static int compile(ParserCompilerParameters args)
	{
		GrammarSource grammar = parseInputGrammar(args);
		int errorlevel;
		if(grammar == null) errorlevel = 1;
		else
		{
			try
			{
				errorlevel = compileParser(args,grammar);
			}
			catch(CopperException ex)
			{
				if(args.getLogger().isLoggable(CompilerLogMessageSort.DEBUG)) ex.printStackTrace(System.err);
				//else System.err.println(ex.getMessage());
				errorlevel = 1;
			}
		}
		return errorlevel;
	}
	
	private static ParserBean parseInputGrammarNew(ParserCompilerParameters args)
	{
		ArrayList< Pair<String,Reader> > files = args.getFiles(); 
		//boolean isComposition = args.isComposition();
		CopperSkinType useSkin = args.getUseSkin();
		CompilerLogger logger;

		logger = getOrMakeLogger(args);

		//WellFormedGrammarChecker wfcheck = new WellFormedGrammarChecker(logger);
		
		ParserBean spec = null;
		switch(useSkin)
		{
		/*case NATIVE:
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
			break;*/
		case XML:
		default:
			try
			{
				spec = new XMLSkinParser(files,logger).parse();
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
			if(args.getPackageDecl() != null) spec.setPackageDecl(args.getPackageDecl());
			if(args.getParserName() != null && !args.getParserName().equals("")) spec.setClassName(args.getParserName());
			break;
		/*case CUP:
		default:
			try { grammar = CupSkinParser.parseGrammar(files,logger); }
			catch(Exception ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) System.err.println();
				if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) ex.printStackTrace(System.err);
				return null;
			}
			if(args.getPackageDecl() != null) grammar.getParserSources().setPackageName(args.getPackageDecl());
			if(args.getParserName() != null && !args.getParserName().equals("")) grammar.getParserSources().setParserName(args.getParserName());*/
		}
		if(spec != null)
		{
			if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) logger.logMessage(CompilerLogMessageSort.DEBUG,null,ParserSpecPlaintextPrinter.specToString(spec));
//			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\nChecking grammar well-formedness");
//
//			try
//			{
//				wfcheck.checkWellFormedness(grammar,args.isWarnUselessNTs());
//			}
//			catch(CopperException ex)
//			{
//				if(logger.isLoggable(CompilerLogMessageSort.TICK)) System.err.println();
//				if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) ex.printStackTrace(System.err);
//				grammar = null;
//			}
		}
		return spec;
	}

	private static int compileParser(ParserCompilerParameters args,ParserBean spec)
	throws CopperException
	{
		CompilerLogger logger = args.getLogger();
		PSSymbolTable symbolTable = SymbolTableBuilder.build(spec);
		if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) logger.logMessage(CompilerLogMessageSort.DEBUG,null,"Symbol table:\n" + symbolTable);
		logger.flushMessages();
		ParserSpec numericSpec = NumericParserSpecBuilder.build(spec,symbolTable);
		if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) logger.logMessage(CompilerLogMessageSort.DEBUG,null,"Numeric spec:\n" + numericSpec.toString(symbolTable));
		logger.flushMessages();
		System.err.println("Constructing context sets");
		ContextSets contextSets = ContextSetBuilder.build(numericSpec);
		if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) logger.logMessage(CompilerLogMessageSort.DEBUG,null,"Context sets:\n" + contextSets.toString(symbolTable));
		logger.flushMessages();
		System.err.println("Constructing LR(0) DFA");
		LR0DFA dfa = LR0DFABuilder.build(numericSpec);
		if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) logger.logMessage(CompilerLogMessageSort.DEBUG,null,"LR(0) DFA:\n" + LRDFAPrinter.toString(symbolTable,numericSpec,dfa));
		logger.flushMessages();
		System.err.println("Constructing LALR lookahead/layout sets");
		LRLookaheadAndLayoutSets lookaheadSets = LALRLookaheadAndLayoutSetBuilder.build(numericSpec,contextSets,dfa);
		if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) logger.logMessage(CompilerLogMessageSort.DEBUG,null,"LALR(1) DFA:\n" + LRDFAPrinter.toString(symbolTable,numericSpec,dfa,lookaheadSets));
		logger.flushMessages();
		System.err.println("Constructing parse table");
		LRParseTable parseTable = LRParseTableBuilder.build(numericSpec,dfa,lookaheadSets);
		if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) logger.logMessage(CompilerLogMessageSort.DEBUG,null,"Parse table:\n" + LRParseTablePrinter.toString(symbolTable,numericSpec,parseTable));
		logger.flushMessages();
		System.err.println("Constructing transparent prefix sets");
		TransparentPrefixes prefixes = TransparentPrefixSetBuilder.build(numericSpec,parseTable);
		if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) logger.logMessage(CompilerLogMessageSort.DEBUG,null,"Transparent prefix sets:\n" + prefixes.toString(symbolTable));
		logger.flushMessages();
		
		return 0;
	}

	public static int compileNew(ParserCompilerParameters args)
	{
		ParserBean spec = parseInputGrammarNew(args);
		int errorlevel;
		try
		{
			errorlevel = compileParser(args,spec);
		}
		catch(CopperException ex)
		{
			errorlevel = 1;
		}
		
		return errorlevel;
	}

	/**
	 * Copper's command-line interface.
	 * @param args Run this class with a single parameter, "-?", to see a list of parameters and switches.
	 */
	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			usageMessageError();
		}
		else if(args.length == 1 && args[0].equals("-?"))
		{
			usageMessageNoError();
		}
		CompilerLogMessageSort quietLevel = CompilerLogMessageSort.getDefaultSort();
		boolean isPretty = false;
		boolean isComposition = false;
		boolean gatherStatistics = false;
		boolean dumpReport = false;
		boolean dumpOnlyOnError = false;
		CopperDumpType.initTable();
		CopperDumpType dumpType = getDefaultDumpType();
		CopperEngineType.initTable();
		CopperEngineType useEngine = getDefaultEngine();
		CopperSkinType.initTable();
		CopperSkinType useSkin = getDefaultSkin();
		String logFile = "";
		String dumpFile = "";
		String runtimeQuietLevel = "ERROR";
		String packageDecl = null;
		String parserName = null;
		// FIXME: Rip this out when GrammarSource is gone.
		boolean useNewPipeline = false;
		int i;
		for(i = 0;i < args.length;i++)
		{
			if(args[i].charAt(0) != '-') break;
			// FIXME: Rip this out when GrammarSource is gone. 
			else if(args[i].equals("-n")) useNewPipeline = true;
			else if(args[i].equals("-q"))
			{
				quietLevel = CompilerLogMessageSort.getQuietSort();
			}
			else if(args[i].equals("-v"))
			{
				quietLevel = CompilerLogMessageSort.getVerboseSort();
			}
			else if(args[i].equals("-vv"))
			{
				quietLevel = CompilerLogMessageSort.getVeryVerboseSort();
			}
			else if(args[i].equals("-gatherstats"))
			{
				if(runtimeQuietLevel.equals("ERROR")) runtimeQuietLevel = "NOTA_BENE";
				gatherStatistics = true;
			}
			else if(args[i].equals("-dump"))
			{
				dumpReport = true;
				dumpOnlyOnError = false;
			}
			else if(args[i].equals("-errordump"))
			{
				dumpReport = true;
				dumpOnlyOnError = true;
			}
			else if(args[i].equals("-runv"))
			{
				runtimeQuietLevel = "INFO";
			}
			else if(args[i].equals("-pretty"))
			{
				isPretty = true;
			}
			else if(args[i].equals("-skin"))
			{
				if(++i == args.length || !CopperSkinType.contains(args[i])) usageMessageError();
				else useSkin = CopperSkinType.fromString(args[i]);
			}
			else if(args[i].equals("-engine"))
			{
				if(++i == args.length || !CopperEngineType.contains(args[i])) usageMessageError();
				else useEngine = CopperEngineType.fromString(args[i]);
			}
			else if(args[i].equals("-compose"))
			{
				isComposition = true;
			}
			else if(args[i].equals("-logfile"))
			{
				if(++i == args.length) usageMessageError();
				else logFile = args[i];
			}
			else if(args[i].equals("-dumpfile"))
			{
				if(++i == args.length) usageMessageError();
				else dumpFile = args[i];
			}
			else if(args[i].equals("-dumptype"))
			{
				if(++i == args.length || !CopperDumpType.contains(args[i])) usageMessageError();
				else
				{
					dumpType = CopperDumpType.fromString(args[i]);
				}
			}
			else if(args[i].equals("-package"))
			{
				if(++i == args.length) usageMessageError();
				else packageDecl = args[i]; 
			}
			else if(args[i].equals("-parser"))
			{
				if(++i == args.length) usageMessageError();
				else parserName = args[i]; 
			}
			else usageMessageError();
		}
		if(i >= args.length) usageMessageError();

		ArrayList< Pair<String,Reader> > files = new ArrayList< Pair<String,Reader> >(); 
		
		boolean failed = false;
		for(;i < args.length;i++)
		{
			FileReader file = null;
			try
			{
				file = new FileReader(args[i]);
			}
			catch(FileNotFoundException ex)
			{
				System.err.println("Grammar file not found: '" + args[i] + "'");
				failed = true;
			}
			if(file != null) files.add(Pair.cons(args[i],(Reader) file));
		}
		if(failed) System.exit(1);
		
		ParserCompilerParameters argTable = new ParserCompilerParameters();
		argTable.setFiles(files);
		argTable.setQuietLevel(quietLevel);
		argTable.setPretty(isPretty);
		argTable.setComposition(isComposition);
		argTable.setDumpReport(dumpReport);
		argTable.setDumpOnlyOnError(dumpOnlyOnError);
		argTable.setGatherStatistics(gatherStatistics);
		argTable.setUseEngine(useEngine);
		argTable.setUseSkin(useSkin);
		argTable.setLogFile(logFile);
		argTable.setDumpFile(dumpFile);
		argTable.setDumpType(dumpType);
		argTable.setRuntimeQuietLevel(runtimeQuietLevel);
		argTable.setPackageDecl(packageDecl);
		argTable.setParserName(parserName);
		
		int errorlevel;
		
		if(useNewPipeline) errorlevel = compileNew(argTable);
		else errorlevel = compile(argTable);
		
		System.exit(errorlevel);
	}
}

