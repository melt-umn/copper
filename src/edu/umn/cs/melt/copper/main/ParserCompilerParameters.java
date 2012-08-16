package edu.umn.cs.melt.copper.main;

import java.io.File;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;

import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogger;
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
	private ArrayList< Pair<String,Reader> > files;
	private boolean isPretty,isComposition,gatherStatistics,dumpReport,dumpOnlyOnError;
	private String runtimeQuietLevel,packageDecl,parserName;
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
	
	private boolean isWarnUselessNTs;
	
	private String singleFileName;
	private Reader singleFileStream;
	
	public ParserCompilerParameters()
	{
		files = null;
		isPretty = false;
		isComposition = false;
		gatherStatistics = false;
		dumpReport = false;
		dumpOnlyOnError = false;
		runtimeQuietLevel = "ERROR";
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

	public boolean isPretty()
	{
		return isPretty;
	}

	public void setPretty(boolean isPretty)
	{
		this.isPretty = isPretty;
	}

	public boolean isComposition()
	{
		return isComposition;
	}

	public void setComposition(boolean isComposition)
	{
		this.isComposition = isComposition;
	}

	public boolean isGatherStatistics()
	{
		return gatherStatistics;
	}

	public void setGatherStatistics(boolean gatherStatistics)
	{
		this.gatherStatistics = gatherStatistics;
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

	public String getRuntimeQuietLevel()
	{
		return runtimeQuietLevel;
	}

	public void setRuntimeQuietLevel(String runtimeQuietLevel)
	{
		this.runtimeQuietLevel = runtimeQuietLevel;
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

}
