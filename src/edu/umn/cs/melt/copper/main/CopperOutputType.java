package edu.umn.cs.melt.copper.main;

import java.io.PrintStream;

/**
 * Represents the targets available to which to output the source code of a generated parser.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public enum CopperOutputType
{
	/** A file, identified by name. */
	FILE,
	/** A {@link PrintStream} of any kind. */
	STREAM
}
