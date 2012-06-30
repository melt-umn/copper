package edu.umn.cs.melt.copper.compiletime.loggingnew.messages;

import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessageType;
import edu.umn.cs.melt.copper.runtime.logging.CopperSyntaxError;

public class GrammarSyntaxError extends GenericMessage
{
	private CopperSyntaxError ex;
	
	public GrammarSyntaxError(CopperSyntaxError ex)
	{
		super(CompilerLevel.QUIET,ex.getMessage(),true,false);
		this.ex = ex;
	}
	
	@Override
	public int getType()
	{
		return CompilerLogMessageType.GRAMMAR_SYNTAX_ERROR;
	}
	
	public CopperSyntaxError getSyntaxError()
	{
		return ex;
	}

	@Override
	public String toString()
	{
		return ex.getMessage();
	}
}
