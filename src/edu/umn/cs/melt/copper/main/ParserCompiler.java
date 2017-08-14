package edu.umn.cs.melt.copper.main;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.pipeline.AuxiliaryMethods;
import edu.umn.cs.melt.copper.compiletime.pipeline.Pipeline;
import edu.umn.cs.melt.copper.compiletime.pipeline.StandardPipeline;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.ParserSpecProcessor;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;


/**
 * Running frontend for the entire parser compiler.
 * The <code>main()</code> method contains the command-line interface,
 * while the <code>compile()</code> methods provide a Java API
 * usable by ANT tasks or other Java methods.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class ParserCompiler
{
	/** Copper's current version number. */
	public static final String VERSION;
	/** If running from a JAR, the timestamp recording when the JAR was built. */
	public static final String BUILD;

	public static FlagParser flagParser;
	
	static
	{
		Properties p = new Properties();
		try
		{
			InputStream manifest = ParserCompiler.class.getClassLoader().getResourceAsStream("etc/Copper.properties");
			if(manifest == null) manifest = ParserCompiler.class.getClassLoader().getResourceAsStream("Build.properties");
			
			if(manifest != null)
			{
				InputStreamReader propertiesReader = new InputStreamReader(manifest);
				p.load(propertiesReader);
			}
		}
		catch(IOException ex)
		{
		}
		String version = p.getProperty("Version");
		String build = p.getProperty("Build");
		VERSION = (version == null || !version.matches("[0-9\\.]+")) ? "unknown" : version; 
		BUILD = (build == null || !build.matches("[0-9]{8}-[0-9]{4}")) ? "unknown" : build; 
		
		Map<String,String> flags = new HashMap<>();
		// Todo: bake usage into flag parser
		flags.put("-?", null);
		flags.put("-version", null);
		flags.put("-o", null);
		flags.put("-q", null);
		flags.put("-v", null);
		flags.put("-vv", null);
		flags.put("-dump", null);
		flags.put("-errordump", null);
		flags.put("-skin", null);
		flags.put("-engine", null);
		flags.put("-pipeline", null);
		flags.put("-mda", null);
		flags.put("-logfile", null);
		flags.put("-dumpfile",null);
		flags.put("-dumptype", null);
		flags.put("-avoidRecompile", null);
		flags.put("-package", null);
		flags.put("-parser", null);
		flagParser = new FlagParser(flags);
	}
	private static void versionMessage(ParserCompilerParameters args)
	{
		System.err.println("Copper version " + VERSION);
		System.exit(0);
	}
	
	private static String usageMessage(ParserCompilerParameters args)
	{
		String rv = "";
		rv += "Usage: ParserCompiler [built-in-switches] [custom-switches] spec-file1 spec-file2 ... spec-filen\n";
		rv += "Built-in switches (all optional) are:\n";
		rv += "\t-?\tDisplay this usage information.\n\t\tUse ParserCompiler -pipeline [pipeline] -? to list\n\t\toptions specific to a given pipeline.\n";
		rv += "\t-version\tDisplay version information.\n";
		rv += "\t-package [package]\tThe package of the generated parser.\n\t\tDefaults to the default package or what is set in\n\t\tthe parser specification.\n";
		rv += "\t-parser [class]\tThe class name of the generated parser.\n\t\tDefaults to 'Parser' or what is set in\n\t\tthe parser specification.\n";
		rv += "\t-o [out]\tOutput the generated parser or fragment to the file 'out'.\n\t\tUse '-' to redirect to standard output, or no parameter\n\t\tto suppress output altogether.\n";
		rv += "\t-q\t\tRun the compiler quietly.\n";
		rv += "\t-v\t\tRun the compiler with extra verbosity.\n";
		rv += "\t-vv\t\tRun the compiler with even more extra verbosity.\n";
		rv += "\t-mda\tRun Copper's modular determinism analysis on the input.\n\t\t\tIf this switch is used, the input must comprise exactly\n\t\t\ttwo grammars: the host and an extension to test.\n";
		rv += "\t-avoidRecompile\tRun Copper only if one spec-file has a later\n\t\t\tmodification time than the output file.\n\t\t\tIf this switch is used, an output file must also\n\t\t\tbe specified.\n";
		rv += "\t-logfile [lout]\tPipe all log output to the file 'lout'\n\t\t\t(default standard error).\n";
		rv += "\t-dump\tProduce a detailed report of the grammar and generated parser.\n";
		rv += "\t-errordump\tProduce a detailed report, but only if the parser\n\t\t\tcompiler has generated an error.\n";
		rv += "\t-dumpfile [dout]\tPipe the dumped report to the file 'dout'\n\t\t\t\t(default to log output).\n";
		rv += "\t-dumptype [type]\tGenerate the dumped report in the\n\t\t\t\tspecified format:\n";
		for(String dt : CopperDumpType.strings())
		{
			rv += "\t\t" + dt + ": " + CopperDumpType.fromString(dt).usageMessage() + "\n";
		}
		rv += "\t-skin [skin]\tGenerate a parser based on input from the specified\n\t\t\tinput skin:\n";
		for(String st : CopperSkinType.strings())
		{
			rv += "\t\t" + st + ": " + CopperSkinType.fromString(st).usageMessage() + "\n";
		}
		rv += "\t-engine [eng]\tGenerate a parser based on the specified parsing engine:\n";
		for(String et : CopperEngineType.strings())
		{
			rv += "\t\t" + et + ": " + CopperEngineType.fromString(et).usageMessage() + "\n";
		}
		rv += "\t-pipeline [pipe]\tGenerate a parser using the specified\n\t\t\t\tcompilation \"pipeline\":\n";
		for(String pt : CopperPipelineType.strings())
		{
			rv += "\t\t" + pt + ": " + CopperPipelineType.fromString(pt).usageMessage() + "\n";
		}
		if(args != null && args.getPipeline() != null)
		{
			String customs = args.getPipeline().customSwitchUsage();
			if(!customs.equals(""))
			{
				rv += "\nCustom switches specific to the given pipeline configuration are:\n";
				rv += customs;
			}
		}
		return rv;
	}
	

	private static void usageMessageNoError(ParserCompilerParameters args)
	{
		System.err.println(usageMessage(args));
		System.exit(0);
	}
	
	private static void usageMessageError(ParserCompilerParameters args)
	{
		System.err.println(usageMessage(args));
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
	
	/**
	 * Returns the default verbosity level, which is currently {@link CompilerLevel#REGULAR}.
	 */
	public static CompilerLevel getDefaultQuietLevel()
	{
		return CompilerLevel.REGULAR;
	}
	
	/** Returns the default pipeline, which is currently {@link CopperPipelineType#GRAMMARBEANS}. */
	public static CopperPipelineType getDefaultPipeline()
	{
		return CopperPipelineType.GRAMMARBEANS;
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
		CompilerLogger logger = AuxiliaryMethods.getOrMakeLogger(args);
		if(args.getInputs() != null)
		{
			if(logger.isLoggable(CompilerLevel.QUIET)) logger.log(new GenericMessage(CompilerLevel.QUIET,"Input files cannot be specified when compiling a parser from Java objects"));
			return 1;
		}
		ParserSpecProcessor.normalizeParser(spec,logger);
		return compileParser(args,spec);
	}
	
	@SuppressWarnings("unchecked")
	private static int compileParser(ParserCompilerParameters args,ParserBean spec)
	throws CopperException
	{
		if(args.getUsePipeline() != CopperPipelineType.GRAMMARBEANS) return 1;
		return ((StandardPipeline<ParserBean,?>) CopperPipelineType.GRAMMARBEANS.getPipeline(args)).execute(spec,args);
	}

	public static int compile(ParserCompilerParameters args)
	throws IOException,CopperException
	{
		return args.getPipeline().execute(args);
	}


	/**
	 * Copper's command-line interface.
	 * @param args Run this class with a single parameter, "-?", to see a list of parameters and switches.
	 */
	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			usageMessageError(null);
		}
		else if(args.length == 1 && args[0].equals("-?"))
		{
			usageMessageNoError(null);
		}
		
		boolean success = flagParser.parse(args);
		if (!success) {
			usageMessageError(null);
		}
	
		boolean displayHelp = flagParser.get("-?").first();
		boolean displayVersion = flagParser.get("-version").first();
		boolean runMDA = flagParser.get("-mda").first();;
		boolean avoidRecompile = flagParser.get("-avoidRecompile").first();

		String logFile = flagParser.get("-logfile").second();
		String packageDecl = flagParser.get("-package").second();
		String parserName = flagParser.get("-parser").second();
		String output = flagParser.get("-o").second();
		String dumpFile = flagParser.get("-dumpfile").second();

		CompilerLevel quietLevel = getDefaultQuietLevel();
		if (flagParser.get("-vv").first()) {
			quietLevel = CompilerLevel.VERY_VERBOSE;
		} else if (flagParser.get("-v").first()) {
			quietLevel = CompilerLevel.VERBOSE;
		} else if ((flagParser.get("-q").first())) {
			quietLevel = CompilerLevel.QUIET;
		}

		CopperDumpControl dumpControl = CopperDumpControl.OFF;
		if (flagParser.get("-dump").first()) {
			dumpControl = CopperDumpControl.ON;
		} else if (flagParser.get("-errordump").first()) {
			dumpControl = CopperDumpControl.ERROR_ONLY;
		}

		CopperDumpType dumpFormat = getDefaultDumpType();
		CopperEngineType.initTable();
		CopperEngineType useEngine = getDefaultEngine();
		CopperSkinType.initTable();
		CopperSkinType useSkin = getDefaultSkin();
		CopperPipelineType.initTable();
		CopperPipelineType usePipeline = getDefaultPipeline();

		Pair<Boolean,String> skin = flagParser.get("-skin");
		if (skin.first()) {
			if (!CopperSkinType.contains(skin.second())) {
				usageMessageError(null);
			}
			useSkin = CopperSkinType.fromString(skin.second());
		}

		Pair<Boolean,String> pipe = flagParser.get("-pipeline");
		if (skin.first()) {
			if (!CopperPipelineType.contains(pipe.second())) {
				usageMessageError(null);
			}
			usePipeline = CopperPipelineType.fromString(pipe.second());
		}

		Pair<Boolean,String> engine = flagParser.get("-engine");
		if (engine.first()) {
			if (!CopperEngineType.contains(engine.second())) {
				usageMessageError(null);
			}
			useEngine = CopperEngineType.fromString(engine.second());
		}

		Pair<Boolean,String> dumpF = flagParser.get("-dumptype");
		if (engine.first()) {
			if (!CopperDumpType.contains(dumpF.second())) {
				usageMessageError(null);
			}
			dumpFormat = CopperDumpType.fromString(dumpF.second());
		}
		
		ParserCompilerParameters argTable = new ParserCompilerParameters();
		argTable.setQuietLevel(quietLevel);
		argTable.setRunMDA(runMDA);
		argTable.setAvoidRecompile(avoidRecompile);
		argTable.setDump(dumpControl);
		argTable.setUseEngine(useEngine);
		argTable.setUseSkin(useSkin);
		argTable.setUsePipeline(usePipeline);
		argTable.setDumpFormat(dumpFormat);
		argTable.setPackageName(packageDecl);
		argTable.setParserName(parserName);

		if(output == null)
		{
			argTable.setOutputType(null);
		}
		else if(!output.equals("-"))
		{
			argTable.setOutputType(CopperIOType.FILE);
			argTable.setOutputFile(new File(output));
		}
		else
		{
			argTable.setOutputType(CopperIOType.STREAM);
			argTable.setOutputStream(System.out);
		}
		
		if(logFile == null || logFile.equals("-"))
		{
			argTable.setLogType(CopperIOType.STREAM);
			argTable.setLogStream(System.err);
		}
		else
		{
			argTable.setLogType(CopperIOType.FILE);
			argTable.setLogFile(new File(logFile));			
		}

		if(dumpFile == null)
		{
			argTable.setDumpOutputType(argTable.getLogType());
			argTable.setDumpStream(argTable.getLogStream());
			argTable.setDumpFile(argTable.getLogFile());
		}
		else if(dumpFile.equals("-"))
		{
			argTable.setDumpOutputType(CopperIOType.STREAM);
			argTable.setDumpStream(System.err);			
		}
		else
		{
			argTable.setDumpOutputType(CopperIOType.FILE);
			argTable.setDumpFile(new File(dumpFile));			
		}

		Pipeline pipeline = argTable.getPipeline();
		
		if(displayHelp) usageMessageNoError(argTable);
		else if(displayVersion) versionMessage(argTable);
				
		for (Pair<String,String> flag : flagParser.customFlags) {
			if (!pipeline.processCustomSwitch(argTable,flag)) break;
		}

		argTable.setInputs(flagParser.inputs);
				
		int errorlevel = 1;
		
		try
		{
			errorlevel = compile(argTable);
			AuxiliaryMethods.getOrMakeLogger(argTable).flush();
		}
		catch(IOException ex)
		{
			if(quietLevel == CompilerLevel.VERY_VERBOSE) ex.printStackTrace();
			System.err.println("I/O error: " + ex.getMessage());
		}
		catch(Exception ex)
		{
			if(quietLevel == CompilerLevel.VERY_VERBOSE) ex.printStackTrace();
			else System.err.println("An unexpected fatal error has occurred. Run with -vv for debug information.");
		}
		
		System.exit(errorlevel);
	}
}

