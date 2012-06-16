package edu.umn.cs.melt.copper.compiletime.dumpers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.main.ParserCompilerParameters;

public class DumperFactory
{
	public static PrintStream getDumpStream(ParserCompilerParameters args)
	throws FileNotFoundException
	{
		PrintStream dumpStream = null;
		if(!args.isDumpReport() || args.getDumpFile().equals("") || args.getDumpFile().equals(args.getLogFile())) dumpStream = args.getLogger().getOut();
		else dumpStream = new PrintStream(new FileOutputStream(args.getDumpFile()));
		return dumpStream;
	}
}
