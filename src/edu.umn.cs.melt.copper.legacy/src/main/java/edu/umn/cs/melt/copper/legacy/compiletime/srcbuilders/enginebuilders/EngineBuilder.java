package edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders;

import java.io.IOException;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public interface EngineBuilder
{
	public void buildLALREngine(PrintStream out, String packageDecl,
			String importDecls, String parserName, String scannerName,
			String parserAncillaries, String scannerAncillaries)
			throws IOException,CopperException;

	public int getScannerStateCount();
}