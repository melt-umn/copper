package edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner;


import java.util.Hashtable;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;


/* (non-Javadoc)
 * Stores information about terminal tokens.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class QScannerTokenInfo
{
	private class TerminalInfo
	{
		public int dynamicPrec;
		public int bridgingPrec;
		public Symbol precClass;
		
		public TerminalInfo(int dynamicPrec,int bridgingPrec,Symbol precClass)
		{
			this.dynamicPrec = dynamicPrec;
			this.bridgingPrec = bridgingPrec;
			this.precClass = precClass;
		}
		
		public String toString()
		{
			return "(C:'" + precClass + "',D:" + dynamicPrec + ",B:" + bridgingPrec + ")";
		}
	}
	
	private Hashtable<Terminal,TerminalInfo> terminalInfo;
	
	public QScannerTokenInfo()
	{
		terminalInfo = new Hashtable<Terminal,TerminalInfo>();
	}
	
	public void addAllAttrs(Terminal spec,int dynamicPrec,int bridgingPrec,Symbol precClass)
	{
		terminalInfo.put(spec,new TerminalInfo(dynamicPrec,bridgingPrec,precClass));
	}

	public int getDynamicPrecedence(Terminal spec)
	{
		if(terminalInfo.containsKey(spec)) return terminalInfo.get(spec).dynamicPrec;
		else return FringeSymbols.PRECEDENCE_NONE;
	}
	
	public int getBridgingPrecedence(Terminal spec)
	{
		if(terminalInfo.containsKey(spec)) return terminalInfo.get(spec).bridgingPrec;
		else return FringeSymbols.PRECEDENCE_NONE;
	}

	public Symbol getPrecClass(Terminal spec)
	{
		if(terminalInfo.containsKey(spec)) return terminalInfo.get(spec).precClass;
		else return FringeSymbols.EMPTY.getId();
	}

	public String toString()
	{
		return terminalInfo.toString();
	}
}
