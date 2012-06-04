package edu.umn.cs.melt.copper.compiletime.loggingnew;

public interface CompilerLogMessage
{
	public CompilerLevel getLevel();
	public int getType();
	
	public boolean isError();
	public boolean isFatalError();
	
	public String toString();
}
