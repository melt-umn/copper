package edu.umn.cs.melt.copper.compiletime.loggingnew.messages;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLocatedLogMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessageType;
import edu.umn.cs.melt.copper.runtime.io.Location;

public class UselessNonterminalMessage implements CompilerLocatedLogMessage<Location>
{
	private NonTerminalBean nt;
	
	public UselessNonterminalMessage(NonTerminalBean nt)
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
