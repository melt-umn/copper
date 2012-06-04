package edu.umn.cs.melt.copper.compiletime.loggingnew;

public interface CompilerLocatedLogMessage<L> extends CompilerLogMessage
{
	public L getLocation();
}
