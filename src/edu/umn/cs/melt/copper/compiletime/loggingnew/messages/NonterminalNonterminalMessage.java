package edu.umn.cs.melt.copper.compiletime.loggingnew.messages;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLocatedLogMessage;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogMessageType;
import edu.umn.cs.melt.copper.runtime.io.Location;

public class NonterminalNonterminalMessage implements CompilerLocatedLogMessage<Location>
{
	private NonTerminalBean nt;
	
	public NonterminalNonterminalMessage(NonTerminalBean nt)
	{
		this.nt = nt;
	}

	@Override
	public CompilerLevel getLevel()
	{
		return CompilerLevel.QUIET;
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.NONTERMINAL_NONTERMINAL;
	}

	@Override
	public boolean isError()
	{
		return true;
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
		return getLocation() + ": warning: nonterminal '" + nt.getDisplayName() + "' has no terminal derivations";
	}

}
