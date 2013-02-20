package edu.umn.cs.melt.copper.main;

import java.io.PrintStream;

/**
 * Represents the types of input/output nodes available (e.g., for outputting the source code of a generated parser).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public enum CopperIOType
{
	/** A file, identified by name. */
	FILE,
	/** A {@link PrintStream} of any kind. */
	STREAM
}
