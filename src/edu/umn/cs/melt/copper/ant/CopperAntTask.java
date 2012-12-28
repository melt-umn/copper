package edu.umn.cs.melt.copper.ant;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.resources.FileResource;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.main.CopperDumpControl;
import edu.umn.cs.melt.copper.main.CopperDumpType;
import edu.umn.cs.melt.copper.main.CopperEngineType;
import edu.umn.cs.melt.copper.main.CopperIOType;
import edu.umn.cs.melt.copper.main.CopperPipelineType;
import edu.umn.cs.melt.copper.main.CopperSkinType;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * This bean allows Copper to be run via ANT. It matches, as closely
 * as possible, the parameters used in {@link ParserCompilerParameters}.
 * {@code inputs} is represented as a nested file-set element, <em>e.g.</em>
 * <br/>
 * {@code <copper ...><inputs dir='dir' includes='specfile'/></copper>}.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class CopperAntTask extends Task
{
	/**
	 * Holds any custom/pipeline-specific switches to be passed on to the pipeline.
	 * @see #usePipeline
	 */
	private Hashtable<String,Object> customSwitches;
	/**
	 * A file to which the dump will be output.
	 * @see #dump
	 */
	protected File dumpFile;
	/**
	 * The format of any parser dump generated during this compiler run.
	 * Defaults to {@link ParserCompiler#getDefaultDumpType()}.
	 */
	protected CopperDumpType dumpFormat;
	/** 
	 * Controls generation of a parser dump: off, on, or only in the event of an error.
	 * @see CopperDumpControl
	 */
	protected CopperDumpControl dump;
	/**
	 * A stream to which any dump generated during this compiler run will be output.
	 * @see #dump
	 */
	protected PrintStream dumpStream;
	/**
	 * Controls whether to output any dump generated during this compiler run to a file or a stream.
	 * @see #dumpFile
	 * @see #dumpStream
	 */
	private CopperIOType dumpOutputType;
	
	private ArrayList<Pair<String,Object>> inputs;

	/**
	 * The set of input arguments to the parser compiler. Each entry
	 * in the list comprises a {@code String} "label" and an
	 * object of nonspecific type. Tighter type restrictions may
	 * be enforced by each pipeline.
	 */
	@SuppressWarnings("unchecked")
	public void addConfiguredInputs(FileSet files)
	{
		Iterator<FileResource> it = files.iterator();
		while(it.hasNext())
		{
			FileResource fr = it.next();
			inputs.add(Pair.cons(fr.getFile().toString(),(Object) fr.getFile()));
		}
	}
	
	/** Turns on or off warnings on useless nonterminals. Defaults to {@code true}. */
	protected boolean isWarnUselessNTs;
	/** 
	 * A file to which Copper's log output (error/warning/info messages) will be sent.
	 * It is permissible for loggers or log handlers to ignore this parameter.
	 */
	protected File logFile;
	/**
	 * A stream to which Copper's log output (error/warning/info messages) will be sent.
	 * It is permissible for loggers or log handlers to ignore this parameter,
	 * in which case it will simply be the default value for {@link #dumpStream}.
	 * Defaults to {@code System.err}.
	 */
	protected PrintStream logStream;
	/**
	 * Controls whether to send Copper's log output (error/warning/info messages) to a file or a stream.
	 * Defaults to {@link CopperIOType#STREAM}.
	 * @see #logFile
	 * @see #logStream
	 */
	private CopperIOType logType;
	/** A file to which Copper's output parser class will be sent. */
	protected File outputFile;
	/** A stream to which Copper's output parser class will be sent. */
	protected PrintStream outputStream;
	/**
	 * Controls whether to send Copper's output parser class to a file or a stream.
	 * Defaults to {@code null}, which suppresses output altogether.
	 * @see #outputFile
	 * @see #outputStream
	 */
	private CopperIOType outputType;
	/**
	 * The name of the package in which to declare the output parser.
	 * Defaults to {@code null}, indicating no package declaration
	 * (if, e.g., it is provided in a code block). 
	 */
	protected String packageName;
	/** The name of the output parser class. */
	protected String parserName;
	/**
	 * Sets the level of verbosity for the logs.
	 * Defaults to {@link ParserCompiler#getDefaultQuietLevel()}.
	 */
	protected CompilerLevel quietLevel;
	/**
	 * Controls whether the modular determinism analysis should be run
	 * on this input. Defaults to {@code false}.
	 */
	protected boolean runMDA;
	
	/**
	 * The parsing "engine" on which the output parser class will be based.
	 * Defaults to {@link ParserCompiler#getDefaultEngine()}.
	 */
	private CopperEngineType useEngine;
	/**
	 * The "pipeline" to which this input will be passed.
	 * Defaults to {@link ParserCompiler#getDefaultPipeline()}.
	 */
	private CopperPipelineType usePipeline;
	/**
	 * The "skin" with which this input will be parsed.
	 * Defaults to {@link ParserCompiler#getDefaultSkin()}.
	 */
	private CopperSkinType useSkin;
	
	public CopperAntTask()
	{
		inputs = new ArrayList<Pair<String,Object>>();
		runMDA = false;
		dump = CopperDumpControl.OFF;
		dumpFormat = ParserCompiler.getDefaultDumpType();
		useEngine = ParserCompiler.getDefaultEngine();
		useSkin = ParserCompiler.getDefaultSkin();
		quietLevel = ParserCompiler.getDefaultQuietLevel();
		usePipeline = ParserCompiler.getDefaultPipeline();
		packageName = null;
		parserName = null;
		outputStream = null;
		outputFile = null;
		outputType = null;
		logStream = null;
		logFile = null;
		logType = null;
		dumpStream = null;
		dumpFile = null;
		dumpOutputType = null;
		
		isWarnUselessNTs = true;
		
		customSwitches = new Hashtable<String,Object>();
	}
	
	public void execute()
	throws BuildException
	{
		ParserCompilerParameters params = new ParserCompilerParameters();
		
		
		params.setInputs(inputs);
		params.setRunMDA(runMDA);
		params.setDump(dump);
		params.setDumpFormat(dumpFormat);
		params.setUseEngine(useEngine);
		params.setUseSkin(useSkin);
		params.setQuietLevel(quietLevel);
		params.setUsePipeline(usePipeline);
		params.setPackageName(packageName);
		params.setParserName(parserName);
		params.setOutputStream(outputStream);
		params.setOutputFile(outputFile);
		params.setOutputType(outputType);
		params.setLogStream(logStream);
		params.setLogFile(logFile);
		params.setLogType(logType);
		params.setDumpStream(dumpStream);
		params.setDumpFile(dumpFile);
		params.setDumpOutputType(dumpOutputType);
		
		params.setWarnUselessNTs(isWarnUselessNTs);
		
		for(String customSwitch : customSwitches.keySet())
		{
			params.setCustomSwitch(customSwitch,customSwitches.get(customSwitch));
		}
		
		System.out.println("Compiling " + inputs.size() + " input file" + (inputs.size() == 1 ? "" : "s") + ":");
		for(Pair<String,Object> input : inputs) System.out.println("\t" + input.first());
		
		int errorlevel = 1;
		try
		{
			errorlevel = ParserCompiler.compile(params);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		if(errorlevel != 0) throw new BuildException("Error " + errorlevel);
	}


	public File getDumpFile()
	{
		return dumpFile;
	}

	public CopperDumpType getDumpFormat()
	{
		return dumpFormat;
	}

	public PrintStream getDumpStream()
	{
		return dumpStream;
	}

	public CopperIOType getDumpOutputType()
	{
		return dumpOutputType;
	}

	public File getLogFile()
	{
		return logFile;
	}

	public PrintStream getLogStream()
	{
		return logStream;
	}

	public CopperIOType getLogType()
	{
		return logType;
	}

	public File getOutputFile()
	{
		return outputFile;
	}

	public PrintStream getOutputStream()
	{
		return outputStream;
	}

	public CopperIOType getOutputType()
	{
		return outputType;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public String getParserName()
	{
		return parserName;
	}

	public CompilerLevel getQuietLevel()
	{
		return quietLevel;
	}

	public CopperEngineType getUseEngine()
	{
		return useEngine;
	}

	public CopperPipelineType getUsePipeline()
	{
		return usePipeline;
	}

	public CopperSkinType getUseSkin()
	{
		return useSkin;
	}

	public boolean hasCustomSwitch(String key)
	{
		return customSwitches.containsKey(key);
	}

	public boolean isRunMDA()
	{
		return runMDA;
	}

	public boolean isWarnUselessNTs()
	{
		return isWarnUselessNTs;
	}

	public void setDumpFile(File dumpFile)
	{
		if(this.dumpOutputType == null) this.dumpOutputType = CopperIOType.FILE;
		if(this.dump == null)    this.dump = CopperDumpControl.ON;
		this.dumpFile = dumpFile;
	}

	public void setDumpFormat(CopperDumpType dumpFormat)
	{
		this.dumpFormat = dumpFormat;
	}

	public void setDumpStream(PrintStream dumpStream)
	{
		if(this.dumpOutputType == null) this.dumpOutputType = CopperIOType.STREAM;
		if(this.dump == null)    this.dump = CopperDumpControl.ON;
		this.dumpStream = dumpStream;
	}

	public void setDumpOutputType(CopperIOType dumpOutputType)
	{
		this.dumpOutputType = dumpOutputType;
	}

	public void setLogFile(File logFile)
	{
		if(this.logType == null) this.logType = CopperIOType.FILE;
		this.logFile = logFile;
	}

	public void setLogStream(PrintStream logStream)
	{
		if(this.logType == null) this.logType = CopperIOType.STREAM;
		this.logStream = logStream;
	}

	public void setLogType(CopperIOType logType)
	{
		this.logType = logType;
	}

	public void setOutputFile(File outputFile)
	{
		if(this.outputType == null) this.outputType = CopperIOType.FILE;
		this.outputFile = outputFile;
	}

	public void setOutputStream(PrintStream outputStream)
	{
		if(this.outputType == null) this.outputType = CopperIOType.STREAM;
		this.outputStream = outputStream;
	}

	public void setOutputType(CopperIOType outputType)
	{
		this.outputType = outputType;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public void setParserName(String parserName)
	{
		this.parserName = parserName;
	}

	public void setQuietLevel(CompilerLevel quietLevel)
	{
		this.quietLevel = quietLevel;
	}

	public void setRunMDA(boolean isComposition)
	{
		this.runMDA = isComposition;
	}
	
	public void setUseEngine(CopperEngineType useEngine)
	{
		this.useEngine = useEngine;
	}
	
	public void setUsePipeline(CopperPipelineType usePipeline)
	{
		this.usePipeline = usePipeline;
	}
	
	public void setUseSkin(CopperSkinType useSkin)
	{
		this.useSkin = useSkin;
	}

	public void setWarnUselessNTs(boolean isWarnUselessNTs)
	{
		this.isWarnUselessNTs = isWarnUselessNTs;
	}

	public CopperDumpControl getDump()
	{
		return dump;
	}

	public void setDump(CopperDumpControl dump)
	{
		this.dump = dump;
	}
}