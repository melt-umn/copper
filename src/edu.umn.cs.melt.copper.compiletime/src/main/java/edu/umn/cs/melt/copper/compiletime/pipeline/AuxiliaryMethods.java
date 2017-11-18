package edu.umn.cs.melt.copper.compiletime.pipeline;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.PrintCompilerLogHandler;

/**
 * Auxiliary methods commonly used in pipelines. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class AuxiliaryMethods
{
	public static CompilerLogger getOrMakeLogger(UniversalProcessParameters args)
	{
		CompilerLogger logger;
		if(args.getLogger() == null)
		{
			PrintStream log = null;
			if(args.getLogType() == null) log = System.err;
			else switch(args.getLogType())
			{
			case FILE:
				if(args.getLogFile() == null) log = System.err;
				else
				{
					try { log = new PrintStream(new FileOutputStream(args.getLogFile())); }
					catch(FileNotFoundException ex)
					{
						ex.printStackTrace();
						return null;
					}
				}
				break;
			case STREAM:
				if(args.getLogStream() == null) log = System.err;
				else log = args.getLogStream();
				break;
				
			}
			logger = new CompilerLogger(new PrintCompilerLogHandler(log));
			logger.setLevel(args.getQuietLevel());
			args.setLogger(logger);
		}
		else logger = args.getLogger();
		return logger;
	}

}
