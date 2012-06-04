package edu.umn.cs.melt.copper.compiletime.loggingnew;

/**
 * Represents the "level" of a message or the logger: very verbose ({@code -vv} switch),
 * verbose ({@code -v} switch), regular (no switch), quiet ({@code -q} switch), or absolutely
 * no log messages at all (no CLI equivalent).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public enum CompilerLevel
{
	VERY_VERBOSE,
	VERBOSE,
	REGULAR,
	QUIET,
	MUTE
}
