package edu.umn.cs.melt.copper.main;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
	/** The Mercurial revision from which this version of Copper was built (if available). */
	public static final String REVISION;
	/** If running from a JAR, the timestamp recording when the JAR was built. */
	public static final String BUILD;
	
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
		String revision = p.getProperty("Revision");
		String build = p.getProperty("Build");
		VERSION = (version == null || !version.matches("[0-9\\.]+")) ? "unknown" : version; 
		REVISION = (revision == null || !revision.matches("[0-9a-f]{40}\\+?")) ? "unknown" : revision; 
		BUILD = (build == null || !build.matches("[0-9]{8}-[0-9]{4}")) ? "unknown" : build; 
	}
	private static void versionMessage(ParserCompilerParameters args)
	{
		System.err.println("Copper version " + VERSION);
		if(!REVISION.equals("unknown") && !BUILD.equals("unknown"))
		{
			System.err.println("Revision " + (args.getQuietLevel().compareTo(CompilerLevel.VERBOSE) <= 0 ? REVISION : REVISION.substring(0,8) + (REVISION.indexOf("+") != -1 ? "+" : "")) + ", build " + BUILD);
		}
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
		rv += "\t-o [out]\tOutput the generated parser to the file 'out'.\n\t\tUse '-' to redirect to standard output, or no parameter\n\t\tto suppress output altogether.\n";
		rv += "\t-q\t\tRun the compiler quietly.\n";
		rv += "\t-v\t\tRun the compiler with extra verbosity.\n";
		rv += "\t-vv\t\tRun the compiler with even more extra verbosity.\n";
		rv += "\t-mda\tRun Copper's modular determinism analysis on the input.\n\t\t\tIf this switch is used, the input must comprise exactly\n\t\t\ttwo grammars: the host and an extension to test.\n";
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
		CompilerLevel quietLevel = getDefaultQuietLevel();
		boolean displayHelp = false;
		boolean displayVersion = false;
		boolean runMDA = false;
		CopperDumpControl dumpControl = CopperDumpControl.OFF;
		CopperDumpType.initTable();
		CopperDumpType dumpFormat = getDefaultDumpType();
		CopperEngineType.initTable();
		CopperEngineType useEngine = getDefaultEngine();
		CopperSkinType.initTable();
		CopperSkinType useSkin = getDefaultSkin();
		CopperPipelineType.initTable();
		CopperPipelineType usePipeline = getDefaultPipeline();
		Pipeline pipeline;
		String logFile = null;
		String dumpFile = "";
		String packageDecl = null;
		String parserName = null;
		String output = null;
		int i;
		for(i = 0;i < args.length;i++)
		{
			if(args[i].charAt(0) != '-') break;
			else if(args[i].equals("-?"))
			{
				displayHelp = true;
			}
			else if(args[i].equals("-version"))
			{
				displayVersion = true;
			}
			else if(args[i].equals("-o"))
			{
				if(++i == args.length) usageMessageError(null);
				else output = args[i];
			}
			else if(args[i].equals("-q"))
			{
				quietLevel = CompilerLevel.QUIET;
			}
			else if(args[i].equals("-v"))
			{
				quietLevel = CompilerLevel.VERBOSE;
			}
			else if(args[i].equals("-vv"))
			{
				quietLevel = CompilerLevel.VERY_VERBOSE;
			}
			else if(args[i].equals("-dump"))
			{
				dumpControl = CopperDumpControl.ON;
			}
			else if(args[i].equals("-errordump"))
			{
				dumpControl = CopperDumpControl.ERROR_ONLY;
			}
			else if(args[i].equals("-skin"))
			{
				if(++i == args.length || !CopperSkinType.contains(args[i])) usageMessageError(null);
				else useSkin = CopperSkinType.fromString(args[i]);
			}
			else if(args[i].equals("-engine"))
			{
				if(++i == args.length || !CopperEngineType.contains(args[i])) usageMessageError(null);
				else useEngine = CopperEngineType.fromString(args[i]);
			}
			else if(args[i].equals("-pipeline"))
			{
				if(++i == args.length || !CopperPipelineType.contains(args[i])) usageMessageError(null);
				else usePipeline = CopperPipelineType.fromString(args[i]);
			}
			else if(args[i].equals("-mda"))
			{
				runMDA = true;
			}
			else if(args[i].equals("-logfile"))
			{
				if(++i == args.length) usageMessageError(null);
				else logFile = args[i];
			}
			else if(args[i].equals("-dumpfile"))
			{
				if(++i == args.length) usageMessageError(null);
				else dumpFile = args[i];
			}
			else if(args[i].equals("-dumptype"))
			{
				if(++i == args.length || !CopperDumpType.contains(args[i])) usageMessageError(null);
				else
				{
					dumpFormat = CopperDumpType.fromString(args[i]);
				}
			}
			else if(args[i].equals("-package"))
			{
				if(++i == args.length) usageMessageError(null);
				else packageDecl = args[i]; 
			}
			else if(args[i].equals("-parser"))
			{
				if(++i == args.length) usageMessageError(null);
				else parserName = args[i]; 
			}
			else
			{
				break;
			}
		}
		
		ParserCompilerParameters argTable = new ParserCompilerParameters();
		argTable.setQuietLevel(quietLevel);
		argTable.setRunMDA(runMDA);
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

		if(dumpFile == null || dumpFile.equals(""))
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

		pipeline = argTable.getPipeline();
		
		if(displayHelp) usageMessageNoError(argTable);
		else if(displayVersion) versionMessage(argTable);
		else if(i >= args.length) usageMessageError(null);
				
		if(args[i].startsWith("-"))
		{
			while(i >= 0 && i < args.length)
			{
				if(args[i].charAt(0) != '-') break;
				i = pipeline.processCustomSwitch(argTable,args,i);
			}
		}
		
		if(i == -1 || i >= args.length) usageMessageError(argTable);

		ArrayList< Pair<String,Object> > files = new ArrayList< Pair<String,Object> >();
		
		for(;i < args.length;i++) files.add(Pair.cons(args[i],(Object) args[i]));
		
		argTable.setInputs(files);
				
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

