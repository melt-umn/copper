package edu.umn.cs.melt.copper.main;

import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * Holds all parameters that may be passed to the parser compiler.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class ParserCompilerParameters
{
	private ArrayList< Pair<String,Reader> > files;
	private boolean isPretty,isComposition,gatherStatistics,dumpReport,dumpOnlyOnError;
	private String runtimeQuietLevel,logFile,dumpFile,packageDecl,parserName;
	private CopperDumpType dumpType;
	private CopperEngineType useEngine;
	private CopperSkinType useSkin;
	private CompilerLogger logger;
	private CompilerLogMessageSort quietLevel;
	private PrintStream output;
	
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
		dumpType = ParserCompiler.getDefaultDumpType();
		useEngine = ParserCompiler.getDefaultEngine();
		useSkin = ParserCompiler.getDefaultSkin();
		logFile = "";
		dumpFile = "";
		quietLevel = CompilerLogMessageSort.getDefaultSort();
		packageDecl = null;
		parserName = null;
		output = System.out;
		
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

	public CompilerLogger getLogger()
	{
		return logger;
	}

	public ArrayList<Pair<String, Reader>> getFiles()
	{
		return files;
	}

	public boolean isDumpReport()
	{
		return dumpReport;
	}

	public boolean isGatherStatistics()
	{
		return gatherStatistics;
	}

	public boolean isComposition()
	{
		return isComposition;
	}

	public boolean isPretty()
	{
		return isPretty;
	}

	public String getLogFile()
	{
		return logFile;
	}

	public String getPackageDecl()
	{
		return packageDecl;
	}

	public String getParserName()
	{
		return parserName;
	}

	public CompilerLogMessageSort getQuietLevel()
	{
		return quietLevel;
	}

	public String getRuntimeQuietLevel()
	{
		return runtimeQuietLevel;
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

	public CopperSkinType getUseSkin()
	{
		return useSkin;
	}

	public void setLogger(CompilerLogger logger)
	{
		this.logger = logger;
	}

	public void setDumpReport(boolean dumpReport)
	{
		this.dumpReport = dumpReport;
	}

	public void setFiles(ArrayList<Pair<String, Reader>> files)
	{
		this.files = files;
	}

	public void setGatherStatistics(boolean gatherStatistics)
	{
		this.gatherStatistics = gatherStatistics;
	}

	public void setComposition(boolean isComposition)
	{
		this.isComposition = isComposition;
		//if(isComposition() && quietLevel == CompilerLogMessageSort.getDefaultSort()) setQuietLevel(CompilerLogMessageSort.ERROR);
	}

	public void setPretty(boolean isPretty)
	{
		this.isPretty = isPretty;
	}

	public void setLogFile(String logFile)
	{
		this.logFile = logFile;
	}

	public void setPackageDecl(String packageDecl)
	{
		this.packageDecl = packageDecl;
	}

	public void setParserName(String parserName)
	{
		this.parserName = parserName;
	}

	public void setQuietLevel(CompilerLogMessageSort quietLevel)
	{
		this.quietLevel = quietLevel;
	}

	public void setRuntimeQuietLevel(String runtimeQuietLevel)
	{
		this.runtimeQuietLevel = runtimeQuietLevel;
	}

	public void setUseEngine(CopperEngineType useEngine)
	{
		this.useEngine = useEngine;
	}

	public void setUseSkin(CopperSkinType useSkin)
	{
		this.useSkin = useSkin;
	}

	public String getDumpFile()
	{
		return dumpFile;
	}

	public void setDumpFile(String dumpFile)
	{
		this.dumpFile = dumpFile;
	}

	public CopperDumpType getDumpType()
	{
		return dumpType;
	}

	public void setDumpType(CopperDumpType dumpType)
	{
		this.dumpType = dumpType;
	}

	public boolean isWarnUselessNTs()
	{
		return isWarnUselessNTs;
	}

	public void setWarnUselessNTs(boolean isWarnUselessNTs)
	{
		this.isWarnUselessNTs = isWarnUselessNTs;
	}

	public boolean isDumpOnlyOnError()
	{
		return dumpOnlyOnError;
	}

	public void setDumpOnlyOnError(boolean dumpOnlyOnError)
	{
		this.dumpOnlyOnError = dumpOnlyOnError;
	}

	public PrintStream getOutput()
	{
		return output;
	}

	public void setOutput(PrintStream output)
	{
		this.output = output;
	}
}
