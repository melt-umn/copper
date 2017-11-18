package edu.umn.cs.melt.copper.compiletime.dumpers;

import java.io.IOException;
import java.io.PrintStream;

import edu.umn.cs.melt.copper.main.CopperDumpType;

/**
 * An interface for classes that produce a "dump," or human-readable description,
 * of a grammar and/or parser. It is deliberately agnostic as to the parameters
 * passed in, to allow different implementing classes to produce different sorts
 * of dumps.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface Dumper
{
	/**
	 * Produce a dump.
	 * @param type The type of the dump.
	 * @param out The stream to which to output the dump.
	 * @throws IOException If the dumper encounters an I/O error.
	 * @throws UnsupportedOperationException If the given dump type is not supported by the implementing class.
	 */
	public void dump(CopperDumpType type,PrintStream out)
	throws IOException,UnsupportedOperationException;
}
