package edu.umn.cs.melt.copper.ant;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.main.CopperDumpType;
import edu.umn.cs.melt.copper.main.CopperEngineType;
import edu.umn.cs.melt.copper.main.CopperIOType;
import edu.umn.cs.melt.copper.main.CopperSkinType;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * This bean allows Copper to be run via Ant.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class CopperAntTask extends Task
{
	private String inputLabel = "<stdin>";
	private String outputLabel = "<stdout>";
	private String fullClassName = "";
	private String logFile = null;
	private String dumpFile = null;
	private boolean isDumpOnlyOnError = false;
	private CopperDumpType dumpType = ParserCompiler.getDefaultDumpType();
	private CopperEngineType engine = ParserCompiler.getDefaultEngine();
	private CopperSkinType skin = ParserCompiler.getDefaultSkin();
	private Reader input = null;
	private File outputFile = null;
	private CompilerLevel quietLevel = ParserCompiler.getDefaultQuietLevel();
	private boolean isWarnUselessNTs = true;
	private boolean isRunVerbose = false;
	private boolean isDump = false;
	private ParserCompilerParameters params = null;
	
	@Override
	public void execute()
	throws BuildException
	{
		params = new ParserCompilerParameters();
		
		int i = fullClassName.lastIndexOf('.');
		if(i != -1)
		{
			params.setPackageDecl(fullClassName.substring(0,i));
			params.setParserName(fullClassName.substring(i+1));
		}
		else if(fullClassName.length() != 0)
		{
			params.setParserName(fullClassName);
		}
		else
		{
			params.setParserName("");
		}
		params.setDumpReport(isDump);
		params.setDumpOnlyOnError(isDumpOnlyOnError);
		if(logFile != null)
		{
			params.setLogFile(new File(logFile));
			if(dumpFile == null) params.setDumpFile(new File(logFile));
		}
		if(dumpType != null) params.setDumpFormat(dumpType); 
		if(dumpFile != null) params.setDumpFile(new File(dumpFile));
		params.setQuietLevel(quietLevel);
		if(isRunVerbose) params.setRuntimeQuietLevel("INFO");
		if(engine != null) params.setUseEngine(engine);
		if(!skin.equals("")) params.setUseSkin(skin);
		
		params.setWarnUselessNTs(isWarnUselessNTs);

		System.out.println("Compiling: " + inputLabel);
		
		//PrintStream oldout = System.out;
		
		ArrayList< Pair<String,Reader> > files = new ArrayList< Pair<String,Reader> >();
		files.add(Pair.cons(inputLabel,input));
		params.setFiles(files);
		
		//System.setOut(output);
		if(outputFile != null)
		{
			params.setOutputType(CopperIOType.FILE);
			params.setOutputFile(outputFile);
		}
		else
		{
			params.setOutputType(CopperIOType.STREAM);
			params.setOutputStream(System.out);
		}
		
		
		int errorlevel = 1;
		try
		{
			errorlevel = ParserCompiler.compile(params);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		//System.setOut(oldout);
		
		if(errorlevel != 0) throw new BuildException("Error " + errorlevel);
	}
	
	/**
	 * Returns the name of the file that will be given to Copper as input.
	 * @see #getInputLabel()
	 */
	public String getInputFile()
	{
		return getInputLabel();
	}

	/**
	 * Sets the name of the file that will be given to Copper as input.
	 * A combination of <code>setInput()</code> and <code>setInputLabel()</code>.
	 * @param fileName The filename.
	 * @throws IOException If there is an error opening the file.
	 */
	public void setInputFile(String fileName)
	throws IOException
	{
		setInputLabel(fileName);
		setInput(new FileReader(fileName));
	}
	
	/**
	 * Returns the name of the file where Copper will place the source code of the finished parser.
	 * @see #getOutputLabel()
	 */
	public String getOutputFile()
	{
		return getOutputLabel();
	}
	
	/**
	 * Sets the name of the file where Copper will place the source code of the finished parser.
	 * A combination of <code>setOutputFile()</code> and <code>setOutputLabel()</code>.
	 * @param fileName The filename.
	 * @throws IOException If there is an error opening the file.
	 */
	public void setOutputFile(String fileName)
	throws IOException
	{
		setOutputLabel(fileName);
		outputFile = new File(fileName);
	}
	
	/**
	 * Returns the name of the file where Copper's log messages will be placed.
	 */
	public String getLogFile()
	{
		return logFile;
	}
	
	/**
	 * Sets the name of the file where Copper's log messages will be placed.
	 * @param logFile The filename.
	 */
	public void setLogFile(String logFile)
	{
		this.logFile = logFile;
	}

	/**
	 * Returns the name of the file where the detailed parser report will be placed, if enabled.
	 * @see #isDump()
	 */
	public String getDumpFile()
	{
		return dumpFile;
	}

	/**
	 * Sets the name of the file where the detailed parser report will be placed, if enabled.
	 * @param dumpFile The filename.
	 * @see #setDump(boolean)
	 */
	public void setDumpFile(String dumpFile)
	{
		setDump(true);
		this.dumpFile = dumpFile;
	}

	/**
	 * Returns the fully qualified name of the output parser class.
	 */
	public String getFullClassName()
	{
		return fullClassName;
	}

	/**
	 * Returns the <code>Reader</code> object that will be given to Copper as input.
	 */
	public Reader getInput()
	{
		return input;
	}

	/**
	 * Returns the name of the <code>Reader</code> object (filename, stream ID, etc.) that will be given to Copper as input.
	 * @see #getInputFile()
	 */
	public String getInputLabel()
	{
		return inputLabel;
	}

	/*
	 * Returns the <code>PrintStream</code> object to which Copper will write the source code of the finished parser.
	 */
//	public PrintStream getOutput()
//	{
//		return output;
//	}

	/**
	 * Returns the name of the <code>PrintStream</code> object (filename, stream ID, etc.) to which Copper will write the source code of the finished parser.
	 * @see #getOutputLabel()
	 */
	public String getOutputLabel()
	{
		return outputLabel;
	}

	/**
	 * Returns the full set of arguments to Copper as generated by the <code>execute()</code> method. 
	 */
	protected ParserCompilerParameters getParams()
	{
		return params;
	}

	/**
	 * Returns <code>true</code> if the compiler is to print no logging information.
	 */
	public boolean isCompileQuiet()
	{
		return quietLevel.equals(CompilerLogMessageSort.getQuietSort());
	}

	/**
	 * Returns <code>true</code> if the compiler is to print extra logging information.
	 */
	public boolean isCompileVerbose()
	{
		return quietLevel.equals(CompilerLogMessageSort.getVerboseSort());
	}

	/**
	 * Returns <code>true</code> if the compiler is to print very much extra logging information.
	 */
	public boolean isCompileVeryVerbose()
	{
		return quietLevel.equals(CompilerLogMessageSort.getVeryVerboseSort());
	}

	/**
	 * Returns <code>true</code> if the generated parser is to print extra logging information.
	 */
	public boolean isRunVerbose()
	{
		return isRunVerbose;
	}

	/**
	 * Returns <code>true</code> if the compiler is to print a "parser report" describing the parser.
	 */
	public boolean isDump()
	{
		return isDump;
	}

	/**
	 * Returns the name of the parse engine around which the parser will be built.
	 */
	public CopperEngineType getEngine()
	{
		return engine;
	}
	
	/**
	 * Returns the name of the "skin" to use when interpreting the input.
	 */
	public CopperSkinType getSkin()
	{
		return skin;
	}

	/**
	 * Sets the fully qualified name of the output parser class.
	 */
	public void setFullClassName(String fullClassName)
	{
		this.fullClassName = fullClassName;
	}

	/**
	 * Sets the <code>Reader</code> object that will be given to Copper as input.
	 */
	public void setInput(Reader input)
	{
		this.input = input;
	}

	/**
	 * Sets the name of the <code>Reader</code> object (filename, stream ID, etc.) that will be given to Copper as input.
	 * @param inputLabel
	 */
	public void setInputLabel(String inputLabel)
	{
		this.inputLabel = inputLabel;
	}

	/*
	 * Sets the <code>PrintStream</code> object to which Copper will write the source code of the finished parser.
	 */
//	public void setOutput(PrintStream output)
//	{
//		this.output = output;
//	}

	/**
	 * Sets the name of the <code>PrintStream</code> object (filename, stream ID, etc.) to which Copper will write the source code of the finished parser.
	 */
	public void setOutputLabel(String outputLabel)
	{
		this.outputLabel = outputLabel;
	}

	/**
	 * Provides a full set of arguments to Copper. Will be overwritten by a call to <code>execute()</code>.
	 */
	protected void setParams(ParserCompilerParameters params)
	{
		this.params = params;
	}

	/**
	 * @param isCompileQuiet <code>true</code> if the compiler is to print no logging information.
	 */
	public void setCompileQuiet(boolean isCompileQuiet)
	{
		this.quietLevel = isCompileQuiet ? CompilerLevel.QUIET : CompilerLevel.REGULAR;
	}

	/**
	 * @param isCompileVerbose <code>true</code> if the compiler is to print extra logging information.
	 */
	public void setCompileVerbose(boolean isCompileVerbose)
	{
		this.quietLevel = isCompileVerbose ? CompilerLevel.VERBOSE : CompilerLevel.REGULAR;
	}

	/**
	 * @param isCompileVerbose <code>true</code> if the compiler is to print very much extra logging information.
	 */
	public void setCompileVeryVerbose(boolean isCompileVerbose)
	{
		this.quietLevel = isCompileVerbose ? CompilerLevel.VERY_VERBOSE : CompilerLevel.REGULAR;
	}

	/**
	 * @param isRunVerbose <code>true</code> if the generated parser is to print extra logging information.
	 */
	public void setRunVerbose(boolean isRunVerbose)
	{
		this.isRunVerbose = isRunVerbose;
	}
	
	/**
	 * @param isDump <code>true</code> if the generated parser is to print a "parser report" describing the parser.
	 */
	public void setDump(boolean isDump)
	{
		this.isDump = isDump;
	}

	/**
	 * @param engine The name of the parse engine around which the parser will be built.
	 * @see CopperEngineType
	 */
	public void setEngine(CopperEngineType engine)
	{
		this.engine = engine;
	}

	/**
	 * @param skin The name of the "skin" to use when interpreting the input.
	 * @see CopperSkinType
	 */
	public void setSkin(CopperSkinType skin)
	{
		this.skin = skin;
	}

	/**
	 * Returns <code>true</code> if the compiler is to print warnings for "useless nonterminals" --
	 * nonterminals that are specified but do not appear in the generated LR DFA.
	 */
	public boolean isWarnUselessNTs()
	{
		return isWarnUselessNTs;
	}

	/**
	 * @param isWarnUselessNTs <code>true</code> if the compiler is to print warnings for "useless nonterminals" --
	 * nonterminals that are specified but do not appear in the generated LR DFA.
	 */
	public void setWarnUselessNTs(boolean isWarnUselessNTs)
	{
		this.isWarnUselessNTs = isWarnUselessNTs;
	}

	/**
	 * Returns the type of parser report that the compiler will produce, if it produces one.
	 * @see CopperDumpType 
	 */
	public CopperDumpType getDumpType()
	{
		return dumpType;
	}

	/**
	 * Returns the type of parser report that the compiler will produce, if it produces one.
	 * @see CopperDumpType 
	 */
	public void setDumpType(CopperDumpType dumpType)
	{
		this.dumpType = dumpType;
	}

	/**
	 * Returns <code>true</code> if the parser generator is set to produce a report only in the event
	 * of a parser compilation error. N.B.: Regardless of how this parameter is set, a report
	 * will only be generated if {@link #setDump(boolean)} is called with parameter
	 * <code>true</code>.
	 */
	public boolean isDumpOnlyOnError()
	{
		return isDumpOnlyOnError;
	}

	/**
	 * @param isDumpOnlyOnError <code>true</code> if the parser generator is set to produce a report
	 * only in the event of a parser compilation error. N.B.: Regardless of how this parameter is
	 * set, a report will only be generated if {@link #setDump(boolean)} is called with parameter
	 * <code>true</code>.
	 */
	public void setDumpOnlyOnError(boolean isDumpOnlyOnError)
	{
		this.isDumpOnlyOnError = isDumpOnlyOnError;
	}

}
