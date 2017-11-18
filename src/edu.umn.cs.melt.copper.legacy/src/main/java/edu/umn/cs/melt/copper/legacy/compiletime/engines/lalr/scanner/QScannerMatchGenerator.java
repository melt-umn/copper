package edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner;

import java.util.ArrayList;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;


/* (non-Javadoc)
 * Interface for classes capable of returning new instances
 * of QScannerMatch. Such classes are intended to be used
 * to return one particular type of QScannerMatch used to
 * match a specific regex (e.g. QScannerMatchLongest for
 * identifier regexes).
 * @see edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner.QScannerMatchMap
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface QScannerMatchGenerator
{
	/* (non-Javadoc)
	 * Returns a new match of the given token.
	 * @param matchingSymbol The token to match.
	 * @param positionFollowing The input position following it.
	 * @param layouts The layouts preceding the match.
	 * @return A new match containing <CODE>matchingSymbol</CODE>.
	 */
	public QScannerMatch getMatch(Terminal matchingSymbol,InputPosition positionPreceding,InputPosition positionFollowing,ArrayList<QScannerMatchData> layouts);

	/* (non-Javadoc)
	 * Returns a new match of the given token.
	 * @param matchingSymbol The token to match.
	 * @param positionFollowing The input position following it.
	 * @param layout The layout preceding the match.
	 * @return A new match containing <CODE>matchingSymbol</CODE>.
	 */
	public QScannerMatch getMatch(Terminal matchingSymbol,InputPosition positionPreceding,InputPosition positionFollowing,QScannerMatchData layout);
}
