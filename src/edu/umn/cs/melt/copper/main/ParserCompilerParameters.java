package edu.umn.cs.melt.copper.main;

import java.io.File;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.pipeline.Pipeline;
import edu.umn.cs.melt.copper.compiletime.pipeline.SourceBuilderParameters;
import edu.umn.cs.melt.copper.compiletime.pipeline.SpecCompilerParameters;
import edu.umn.cs.melt.copper.compiletime.pipeline.SpecParserParameters;
import edu.umn.cs.melt.copper.compiletime.pipeline.UniversalProcessParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * Holds all parameters that may be passed to Copper.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class ParserCompilerParameters implements SpecParserParameters,SpecCompilerParameters,SourceBuilderParameters,UniversalProcessParameters
{
	/**
	 * Holds any custom/pipeline-specific switches to be passed on to the pipeline.
	 * @see #pipeline
	 */
	protected Hashtable<String,Object> customSwitches;
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
	protected CopperIOType dumpOutputType;
	/**
	 * The set of input arguments to the parser compiler. Each entry
	 * in the list comprises a {@code String} "label" and an
	 * object of nonspecific type. Tighter type restrictions may
	 * be enforced by each pipeline.
	 */
	protected ArrayList< Pair<String,Object> > inputs;
	/** Turns on or off warnings on useless nonterminals. Defaults to {@code true}. */
	protected boolean isWarnUselessNTs;
	/** 
	 * A file to which Copper's log output (error/warning/info messages) will be sent.
	 * It is permissible for loggers or log handlers to ignore this parameter.
	 */
	protected File logFile;
	/** The logging object through which Copper's log output (error/warning/info messages) will be filtered. */
	protected CompilerLogger logger;
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
	protected CopperIOType logType;
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
	protected CopperIOType outputType;
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
	private String singleFileName;
	private Reader singleFileStream;
	
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
	

	private Pipeline pipeline;
	public Pipeline getPipeline()
	{
		if(pipeline == null && usePipeline != null) pipeline = usePipeline.getPipeline(this);
		return pipeline;
	}

	@Override
	public Object getCustomSwitch(String key)
	{
		return customSwitches.get(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCustomSwitch(String key,Class<T> mustBeType,T defaultValue)
	{
		if(!hasCustomSwitch(key)) return defaultValue;
		Object value = customSwitches.get(key);
		if(!mustBeType.isAssignableFrom(value.getClass())) return defaultValue;
		return (T) value;
	}
	
	public ParserCompilerParameters()
	{
		inputs = null;
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

	@Override
	public File getDumpFile()
	{
		return dumpFile;
	}

	@Override
	public CopperDumpType getDumpFormat()
	{
		return dumpFormat;
	}

	@Override
	public PrintStream getDumpStream()
	{
		return dumpStream;
	}

	@Override
	public CopperIOType getDumpOutputType()
	{
		return dumpOutputType;
	}

	@Override
	public ArrayList<Pair<String, Object>> getInputs()
	{
		return inputs;
	}

	@Override
	public File getLogFile()
	{
		return logFile;
	}

	@Override
	public CompilerLogger getLogger()
	{
		return logger;
	}

	@Override
	public PrintStream getLogStream()
	{
		return logStream;
	}

	@Override
	public CopperIOType getLogType()
	{
		return logType;
	}

	@Override
	public File getOutputFile()
	{
		return outputFile;
	}

	@Override
	public PrintStream getOutputStream()
	{
		return outputStream;
	}

	@Override
	public CopperIOType getOutputType()
	{
		return outputType;
	}

	@Override
	public String getPackageName()
	{
		return packageName;
	}

	@Override
	public String getParserName()
	{
		return parserName;
	}

	@Override
	public CompilerLevel getQuietLevel()
	{
		return quietLevel;
	}

	public String getSingleFileName()
	{
		return singleFileName;
	}

	public Reader getSingleFileStream()
	{
		return singleFileStream;
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

	public Object setCustomSwitch(String key,Object value)
	{
		return customSwitches.put(key,value);
	}

	public void setDumpFile(File dumpFile)
	{
		this.dumpFile = dumpFile;
	}

	public void setDumpFormat(CopperDumpType dumpFormat)
	{
		this.dumpFormat = dumpFormat;
	}

	public void setDumpStream(PrintStream dumpStream)
	{
		this.dumpStream = dumpStream;
	}

	public void setDumpOutputType(CopperIOType dumpOutputType)
	{
		this.dumpOutputType = dumpOutputType;
	}

	public void setInputs(ArrayList<Pair<String, Object>> inputs)
	{
		this.inputs = inputs;
	}

	public void setLogFile(File logFile)
	{
		this.logFile = logFile;
	}

	public void setLogger(CompilerLogger logger)
	{
		this.logger = logger;
	}

	public void setLogStream(PrintStream logStream)
	{
		this.logStream = logStream;
	}

	public void setLogType(CopperIOType logType)
	{
		this.logType = logType;
	}

	public void setOutputFile(File outputFile)
	{
		this.outputFile = outputFile;
	}

	public void setOutputStream(PrintStream outputStream)
	{
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

	public void setRunMDA(boolean runMDA)
	{
		this.runMDA = runMDA;
	}
	
	public void setSingleFileName(String singleFileName)
	{
		this.singleFileName = singleFileName;
		if(singleFileName != null && singleFileStream != null)
		{
			inputs = new ArrayList< Pair<String,Object> >();
			inputs.add(Pair.cons(singleFileName,(Object) singleFileStream));
		}
	}

	public void setSingleFileStream(Reader singleFileStream)
	{
		this.singleFileStream = singleFileStream;
		if(singleFileName != null && singleFileStream != null)
		{
			inputs = new ArrayList< Pair<String,Object> >();
			inputs.add(Pair.cons(singleFileName,(Object) singleFileStream));
		}
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
