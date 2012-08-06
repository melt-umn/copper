package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.StringBasedCompilerLogger;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.PrintCompilerLogHandler;

public class AuxiliaryMethods
{
	public static edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogger getNewStyleLogger(CompilerLogger oldStyleLogger,UniversalProcessParameters args)
	{
		edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogger logger = new edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogger(new PrintCompilerLogHandler(oldStyleLogger.getOut()));
		switch(args.getQuietLevel())
		{
		case DEBUG:
			logger.setLevel(CompilerLevel.VERY_VERBOSE);
			break;
		case DUMP:
			logger.setLevel(CompilerLevel.VERBOSE);
			break;
		case ERROR:
		case FATAL_ERROR:
			logger.setLevel(CompilerLevel.QUIET);
			break;
		case WARNING:
			break;
		}
		return logger;
	}

	public static CompilerLogger getOrMakeLogger(UniversalProcessParameters args)
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

}
