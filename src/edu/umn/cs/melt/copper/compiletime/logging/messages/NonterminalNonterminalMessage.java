package edu.umn.cs.melt.copper.compiletime.logging.messages;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLocatedLogMessage;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.NonTerminal;
import edu.umn.cs.melt.copper.runtime.io.Location;

public class NonterminalNonterminalMessage implements CompilerLocatedLogMessage<Location>
{
	private NonTerminal nt;
	
	public NonterminalNonterminalMessage(NonTerminal nt)
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
