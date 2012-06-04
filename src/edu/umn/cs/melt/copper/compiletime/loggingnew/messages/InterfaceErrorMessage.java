package edu.umn.cs.melt.copper.compiletime.loggingnew.messages;

import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessageType;

public class InterfaceErrorMessage extends GenericMessage
{
	public InterfaceErrorMessage(String message)
	{
		super(CompilerLevel.QUIET,message,true,false);
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.INTERFACE_ERROR;
	}
}
