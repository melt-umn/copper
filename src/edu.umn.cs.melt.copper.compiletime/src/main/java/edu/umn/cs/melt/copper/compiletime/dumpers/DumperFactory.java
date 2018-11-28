package edu.umn.cs.melt.copper.compiletime.dumpers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.compiletime.pipeline.SpecCompilerParameters;
import edu.umn.cs.melt.copper.main.CopperDumpControl;

public class DumperFactory
{
	public static PrintStream getDumpStream(SpecCompilerParameters args)
	throws FileNotFoundException
	{
		PrintStream dumpStream = null;
		if(args.getDump() == CopperDumpControl.OFF || args.getDumpOutputType() == null) dumpStream = args.getLogStream();
		else switch(args.getDumpOutputType())
		{
		case FILE:
			if(args.getDumpFile().equals(args.getLogFile())) dumpStream = args.getLogStream();
			else dumpStream = new PrintStream(new FileOutputStream(args.getDumpFile()));
			break;
		case STREAM:
			dumpStream = args.getDumpStream();
			break;
		}
		return dumpStream;
	}
}
