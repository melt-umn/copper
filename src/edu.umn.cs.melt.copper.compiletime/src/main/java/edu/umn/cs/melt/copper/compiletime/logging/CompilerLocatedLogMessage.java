package edu.umn.cs.melt.copper.compiletime.logging;

/**
 * An extension to the log-message interface to cover messages with a location.
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 *
 * @param <L> The location type (e.g., {@link edu.umn.cs.melt.copper.runtime.io.Location}).
 */
public interface CompilerLocatedLogMessage<L> extends CompilerLogMessage
{
	public L getLocation();
}
