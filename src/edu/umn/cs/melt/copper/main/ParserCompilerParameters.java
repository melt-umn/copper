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
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * Holds all parameters that may be passed to Copper.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class ParserCompilerParameters implements SpecParserParameters,SpecCompilerParameters,SourceBuilderParameters
{
	private Hashtable<String,Object> customParameters;
	private ArrayList< Pair<String,Reader> > files;
	private boolean isComposition;
	private boolean dumpReport;
	private boolean dumpOnlyOnError;
	private String packageDecl;
	private String parserName;
	private CopperDumpType dumpFormat;
	private CopperEngineType useEngine;
	private CopperSkinType useSkin;
	private CompilerLogger logger;
	private CompilerLevel quietLevel;
	private PrintStream outputStream;
	private File outputFile;
	private CopperIOType outputType;
	private PrintStream logStream;
	private File logFile;
	private CopperIOType logType;
	private PrintStream dumpStream;
	private File dumpFile;
	private CopperIOType dumpType;
	private CopperPipelineType usePipeline;
	private Pipeline pipeline;
	
	private boolean isWarnUselessNTs;
	
	private String singleFileName;
	private Reader singleFileStream;
	
	public ParserCompilerParameters()
	{
		files = null;
		isComposition = false;
		dumpReport = false;
		dumpOnlyOnError = false;
		dumpFormat = ParserCompiler.getDefaultDumpType();
		useEngine = ParserCompiler.getDefaultEngine();
		useSkin = ParserCompiler.getDefaultSkin();
		quietLevel = ParserCompiler.getDefaultQuietLevel();
		usePipeline = ParserCompiler.getDefaultPipeline();
		packageDecl = null;
		parserName = null;
		outputStream = null;
		outputFile = null;
		outputType = null;
		logStream = null;
		logFile = null;
		logType = null;
		dumpStream = null;
		dumpFile = null;
		dumpType = null;
		
		isWarnUselessNTs = true;
		pipeline = null;
	}

	public void setSingleFileName(String singleFileName)
	{
		this.singleFileName = singleFileName;
		if(singleFileName != null && singleFileStream != null)
		{
			files = new ArrayList< Pair<String,Reader> >();
			files.add(Pair.cons(singleFileName,singleFileStream));
		}
	}

	public void setSingleFileStream(Reader singleFileStream)
	{
		this.singleFileStream = singleFileStream;
		if(singleFileName != null && singleFileStream != null)
		{
			files = new ArrayList< Pair<String,Reader> >();
			files.add(Pair.cons(singleFileName,singleFileStream));
		}
	}

	public ArrayList<Pair<String, Reader>> getFiles()
	{
		return files;
	}

	public void setFiles(ArrayList<Pair<String, Reader>> files)
	{
		this.files = files;
	}

	public boolean isComposition()
	{
		return isComposition;
	}

	public void setComposition(boolean isComposition)
	{
		this.isComposition = isComposition;
	}

	public boolean isDumpReport()
	{
		return dumpReport;
	}

	public void setDumpReport(boolean dumpReport)
	{
		this.dumpReport = dumpReport;
	}

	public boolean isDumpOnlyOnError()
	{
		return dumpOnlyOnError;
	}

	public void setDumpOnlyOnError(boolean dumpOnlyOnError)
	{
		this.dumpOnlyOnError = dumpOnlyOnError;
	}

	public String getPackageDecl()
	{
		return packageDecl;
	}

	public void setPackageDecl(String packageDecl)
	{
		this.packageDecl = packageDecl;
	}

	public String getParserName()
	{
		return parserName;
	}

	public void setParserName(String parserName)
	{
		this.parserName = parserName;
	}

	public CopperDumpType getDumpFormat()
	{
		return dumpFormat;
	}

	public void setDumpFormat(CopperDumpType dumpFormat)
	{
		this.dumpFormat = dumpFormat;
	}

	public CopperEngineType getUseEngine()
	{
		return useEngine;
	}

	public void setUseEngine(CopperEngineType useEngine)
	{
		this.useEngine = useEngine;
	}

	public CopperSkinType getUseSkin()
	{
		return useSkin;
	}

	public void setUseSkin(CopperSkinType useSkin)
	{
		this.useSkin = useSkin;
	}

	public CompilerLogger getLogger()
	{
		return logger;
	}

	public void setLogger(CompilerLogger logger)
	{
		this.logger = logger;
	}

	public CompilerLevel getQuietLevel()
	{
		return quietLevel;
	}

	public void setQuietLevel(CompilerLevel quietLevel)
	{
		this.quietLevel = quietLevel;
	}

	public PrintStream getOutputStream()
	{
		return outputStream;
	}

	public void setOutputStream(PrintStream outputStream)
	{
		this.outputStream = outputStream;
	}

	public File getOutputFile()
	{
		return outputFile;
	}

	public void setOutputFile(File outputFile)
	{
		this.outputFile = outputFile;
	}

	public CopperIOType getOutputType()
	{
		return outputType;
	}

	public void setOutputType(CopperIOType outputType)
	{
		this.outputType = outputType;
	}

	public PrintStream getLogStream()
	{
		return logStream;
	}

	public void setLogStream(PrintStream logStream)
	{
		this.logStream = logStream;
	}

	public File getLogFile()
	{
		return logFile;
	}

	public void setLogFile(File logFile)
	{
		this.logFile = logFile;
	}

	public CopperIOType getLogType()
	{
		return logType;
	}

	public void setLogType(CopperIOType logType)
	{
		this.logType = logType;
	}

	public PrintStream getDumpStream()
	{
		return dumpStream;
	}

	public void setDumpStream(PrintStream dumpStream)
	{
		this.dumpStream = dumpStream;
	}

	public File getDumpFile()
	{
		return dumpFile;
	}

	public void setDumpFile(File dumpFile)
	{
		this.dumpFile = dumpFile;
	}

	public CopperIOType getDumpType()
	{
		return dumpType;
	}

	public void setDumpType(CopperIOType dumpType)
	{
		this.dumpType = dumpType;
	}

	public CopperPipelineType getUsePipeline()
	{
		return usePipeline;
	}

	public void setUsePipeline(CopperPipelineType usePipeline)
	{
		this.usePipeline = usePipeline;
	}

	public boolean isWarnUselessNTs()
	{
		return isWarnUselessNTs;
	}

	public void setWarnUselessNTs(boolean isWarnUselessNTs)
	{
		this.isWarnUselessNTs = isWarnUselessNTs;
	}

	public String getSingleFileName()
	{
		return singleFileName;
	}

	public Reader getSingleFileStream()
	{
		return singleFileStream;
	}
	
	@Override
	public boolean hasCustomParameter(String key)
	{
		return customParameters.containsKey(key);
	}

	@Override
	public Object getCustomParameter(String key)
	{
		return customParameters.get(key);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCustomParameter(String key,Class<T> mustBeType,T defaultValue)
	{
		if(!hasCustomParameter(key)) return defaultValue;
		Object value = customParameters.get(key);
		if(!mustBeType.isAssignableFrom(value.getClass())) return defaultValue;
		return (T) value;
	}
	
	public Object setCustomParameter(String key,Object value)
	{
		return customParameters.put(key,value);
	}
	
	public Pipeline getPipeline()
	{
		if(pipeline == null && usePipeline != null) pipeline = usePipeline.getPipeline(this);
		return pipeline;
	}

	public void setPipeline(Pipeline pipeline)
	{
		this.pipeline = pipeline;
	}

}
