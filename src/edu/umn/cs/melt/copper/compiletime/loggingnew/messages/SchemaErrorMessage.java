package edu.umn.cs.melt.copper.compiletime.loggingnew.messages;

import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessageType;

public class SchemaErrorMessage extends GenericMessage
{
	public SchemaErrorMessage(String message,boolean isFatal)
	{
		super(CompilerLevel.QUIET,message,true,isFatal);
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.SCHEMA_ERROR;
	}
}
