package edu.umn.cs.melt.copper.compiletime.logging.messages;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.runtime.logging.CopperSyntaxError;

public class GrammarSyntaxError extends GenericMessage
{
	public static CompilerLevel getGrammarSyntaxErrorMessageLevel()
	{
		return CompilerLevel.QUIET;
	}

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
