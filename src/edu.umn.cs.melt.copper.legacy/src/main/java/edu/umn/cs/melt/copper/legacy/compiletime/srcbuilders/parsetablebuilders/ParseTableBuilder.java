package edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.parsetablebuilders;

import java.io.PrintStream;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public abstract class ParseTableBuilder
{
	public abstract void outputInitFunctions(PrintStream out) throws CopperException;
	public abstract void outputInitStatements(PrintStream out) throws CopperException;
}
