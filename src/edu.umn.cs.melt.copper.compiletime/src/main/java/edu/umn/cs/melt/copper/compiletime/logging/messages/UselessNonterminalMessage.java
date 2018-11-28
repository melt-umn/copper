package edu.umn.cs.melt.copper.compiletime.logging.messages;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLocatedLogMessage;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.NonTerminal;
import edu.umn.cs.melt.copper.runtime.io.Location;

public class UselessNonterminalMessage implements CompilerLocatedLogMessage<Location>
{
	private NonTerminal nt;
	
	public UselessNonterminalMessage(NonTerminal nt)
	{
		this.nt = nt;
	}

	@Override
	public CompilerLevel getLevel()
	{
		return CompilerLevel.REGULAR;
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.USELESS_NONTERMINAL;
	}

	@Override
	public boolean isError()
	{
		return false;
	}

	@Override
	public boolean isFatalError()
	{
		return false;
	}

	@Override
	public Location getLocation()
	{
		return nt.getLocation();
	}
	
	public String toString()
	{
		return getLocation() + ": warning: useless nonterminal '" + nt.getDisplayName() + "'";
	}

}
