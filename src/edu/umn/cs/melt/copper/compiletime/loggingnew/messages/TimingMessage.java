package edu.umn.cs.melt.copper.compiletime.loggingnew.messages;

import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;

public class TimingMessage extends GenericMessage
{
	public TimingMessage(String task,long duration)
	{
		super(TIMING_LEVEL,"  * " + task + " took " + duration + " ms");
	}
	
	public static final CompilerLevel TIMING_LEVEL = CompilerLevel.VERBOSE;
}
