package edu.umn.cs.melt.copper.main;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.pipeline.AuxiliaryMethods;
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
	private static String usageMessage()
	{
		String rv = "";
		rv += "Usage: ParserCompiler [switches] grammar-file1 grammar-file2 ... grammar-filen\n";
		rv += "Switches (all optional) are:\n";
		rv += "\t-?\t\tDisplay this usage information.\n";
		rv += "\t-package [package]\tThe package of the generated parser.\n\t\tDefaults to the default package or what is set in\n\t\tthe parser specification.\n";
		rv += "\t-parser [class]\tThe class name of the generated parser.\n\t\tDefaults to 'Parser' or what is set in\n\t\tthe parser specification.\n";
		rv += "\t-o [out]\tOutput the generated parser to the file 'out'.\n\t\tUse '-' to redirect to standard output, or no parameter\n\t\tto suppress output altogether.\n";
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
		if(args.getFiles() != null)
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
		return args.getUsePipeline().getPipeline(args).execute(args);
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
		CompilerLevel quietLevel = getDefaultQuietLevel();
		boolean isPretty = false;
		boolean isComposition = false;
		boolean gatherStatistics = false;
		boolean dumpReport = false;
		boolean dumpOnlyOnError = false;
		CopperDumpType.initTable();
		CopperDumpType dumpFormat = getDefaultDumpType();
		CopperEngineType.initTable();
		CopperEngineType useEngine = getDefaultEngine();
		CopperSkinType.initTable();
		CopperSkinType useSkin = getDefaultSkin();
		CopperPipelineType.initTable();
		CopperPipelineType usePipeline = getDefaultPipeline();
		String logFile = null;
		String dumpFile = "";
		String runtimeQuietLevel = "ERROR";
		String packageDecl = null;
		String parserName = null;
		String output = null;
		int i;
		for(i = 0;i < args.length;i++)
		{
			if(args[i].charAt(0) != '-') break;
			else if(args[i].equals("-o"))
			{
				if(++i == args.length) usageMessageError();
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
			else if(args[i].equals("-pipeline"))
			{
				if(++i == args.length || !CopperPipelineType.contains(args[i])) usageMessageError();
				else usePipeline = CopperPipelineType.fromString(args[i]);
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
					dumpFormat = CopperDumpType.fromString(args[i]);
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
		argTable.setUsePipeline(usePipeline);
		argTable.setDumpFormat(dumpFormat);
		argTable.setRuntimeQuietLevel(runtimeQuietLevel);
		argTable.setPackageDecl(packageDecl);
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
			argTable.setDumpType(argTable.getLogType());
			argTable.setDumpStream(argTable.getLogStream());
			argTable.setDumpFile(argTable.getLogFile());
		}
		else if(dumpFile.equals("-"))
		{
			argTable.setDumpType(CopperIOType.STREAM);
			argTable.setDumpStream(System.err);			
		}
		else
		{
			argTable.setDumpType(CopperIOType.FILE);
			argTable.setDumpFile(new File(dumpFile));			
		}

				
		int errorlevel = 1;
		
		try
		{
			errorlevel = compile(argTable);
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

