package edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner;


import java.util.ArrayList;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;




/* (non-Javadoc)
 * Map to hold information about match types for terminals (use longest, shortest, all, etc.)
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class QScannerMatchMap implements QScannerMatchGenerator
{
	private Hashtable<Terminal,QScannerMatchGenerator> matches;
	
	/* (non-Javadoc)
	 * Creates a new empty QScannerMatchMap.
	 *
	 */
	public QScannerMatchMap()
	{
		matches = new Hashtable<Terminal,QScannerMatchGenerator>();
	}
	
	/* (non-Javadoc)
	 * Gets a match object for a given terminal/lexeme.
	 * @param t The token for which to get the match.
	 * @param positionPreceding The position preceding the lexeme.
	 * @param positionFollowing The position following the lexeme.
	 * @param layouts The layout tokens preceding the lexeme. 
	 * @return A match appertaining to <CODE>t</CODE> with the match type indicated by <CODE>t</CODE>'s symbol.
	 */
	public QScannerMatch getMatch(Terminal t,InputPosition positionPreceding,InputPosition positionFollowing,ArrayList<QScannerMatchData> layouts) { return matches.get(t).getMatch(t,positionPreceding,positionFollowing,layouts); }
	public QScannerMatch getMatch(Terminal t,InputPosition positionPreceding,InputPosition positionFollowing,QScannerMatchData layout)           { return matches.get(t).getMatch(t,positionPreceding,positionFollowing,layout); }
	
	/* (non-Javadoc)
	 * Puts a generator of matches for a particular terminal into the map.
	 * @param t A terminal symbol.
	 * @param g A generator for the type of match appertaining to <CODE>t</CODE>.
	 */
	public void putGenerator(Terminal t,QScannerMatchGenerator g) { matches.put(t,g); } 
	
	/* (non-Javadoc)
	 * Puts a generator of matches for a particular terminal into the map.
	 * @param t A terminal symbol.
	 * @param g A string label specifying the type of generator.
	 */
	public void putGenerator(Terminal t,int g)
	{
		switch(g)
		{
		case 1:
		default:
			matches.put(t,new QScannerMatchLongest.Generator());
			break;
		}
	}
}
