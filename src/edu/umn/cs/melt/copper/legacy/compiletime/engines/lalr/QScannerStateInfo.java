package edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr;

import java.util.HashSet;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;



/**
 * Holds secondary information for a DFA state in a QScanner.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class QScannerStateInfo
{
	private HashSet<Terminal> acceptingSyms;
	private HashSet<Terminal> rejectingSyms;
	private HashSet<Terminal> possibleSyms;
		
	public QScannerStateInfo()
	{
		this.acceptingSyms = new HashSet<Terminal>();
		this.rejectingSyms = new HashSet<Terminal>();
		this.possibleSyms = new HashSet<Terminal>();
	}

	/**
	 * Adds one or more "accepting symbols" (symbols appertaining
	 * unto regexes for which this state is an accept state).
	 * @param syms The new symbols (lexeme-less terminals).
	 */
	public void addAcceptingSyms(Terminal... syms)
	{
		for(Terminal t : syms)
		{
			acceptingSyms.add(t);
		}
	}

	/**
	 * Adds one or more "possible symbols" (symbols appertaining
	 * unto regexes for which there is an accept state reachable
	 * from this state).
	 * @param syms The new symbols (lexeme-less terminals).
	 */
	public void addPossibleSyms(Terminal... syms)
	{
		for(Terminal t : syms)
		{
			possibleSyms.add(t);
		}
	}
	
	/**
	 * Removes one or more accepting symbols from this state.
	 * @param syms The symbols to be removed.
	 */
	public void addRejectingSyms(Terminal... syms)
	{
		for(Terminal t : syms)
		{
			rejectingSyms.add(t);
		}
	}
	
	/**
	 * Removes one or more "accepting symbols" (symbols appertaining
	 * unto regexes for which this state is an accept state).
	 * @param syms The symbols (lexeme-less terminals) to remove.
	 */
	public void removeAcceptingSyms(Terminal... syms)
	{
		for(Terminal t : syms)
		{
			acceptingSyms.remove(t);
		}
	}

	/**
	 * Removes one or more "possible symbols" (symbols appertaining
	 * unto regexes for which there is an accept state reachable
	 * from this state).
	 * @param syms The symbols (lexeme-less terminals) to remove.
	 */
	public void removePossibleSyms(Terminal... syms)
	{
		for(Terminal t : syms)
		{
			possibleSyms.remove(t);
		}
	}
	
	/**
	 * Removes one or more rejecting symbols from this state.
	 * @param syms The symbols to be removed.
	 */
	public void removeRejectingSyms(Terminal... syms)
	{
		for(Terminal t : syms)
		{
			rejectingSyms.remove(t);
		}
	}
	

	/**
	 * @return Returns the acceptingSyms.
	 */
	public HashSet<Terminal> getAcceptingSyms()
	{
		return acceptingSyms;
	}
	/**
	 * @return Returns the rejectingSyms.
	 */
	public HashSet<Terminal> getRejectingSyms()
	{
		return rejectingSyms;
	}
	/**
	 * @return Returns the possibleSyms.
	 */
	public HashSet<Terminal> getPossibleSyms()
	{
		return possibleSyms;
	}
}

