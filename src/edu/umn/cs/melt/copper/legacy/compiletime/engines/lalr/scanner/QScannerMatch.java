package edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.legacy.compiletime.auxiliary.Mergable;

/* (non-Javadoc)
 * Holds a set of matches for a given token.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class QScannerMatch implements Mergable<QScannerMatch>
{
	protected Terminal matchingSymbol;
	/* (non-Javadoc)
	 * Creates a new instance of QScannerMatch.
	 * @param matchingSymbol The matches' terminal symbol, or the token of a particular match.
	 */
	protected QScannerMatch(Terminal matchingSymbol)
	{
		this.matchingSymbol = matchingSymbol.bareSym();
	}

	/* (non-Javadoc)
	 * @return Returns the matches' terminal symbol.
	 */
	public Terminal getMatchingSymbol()
	{
		return matchingSymbol;
	}
	
	/* (non-Javadoc)
	 * @return Returns the matching tokens.
	 */
	public abstract Iterable<QScannerMatchData> getLexemes();
	
	/* (non-Javadoc)
	 * Determines whether the match set contains a particular token.
	 * @param lexeme The token to match.
	 * @return <CODE>true</CODE> iff the match set contains <CODE>lexeme</CODE>.
	 */
	public abstract boolean containsLexeme(Terminal lexeme);

	/* (non-Javadoc)
	 * Determines whether the match set holds a "final" lexeme.
	 * If it does, the scanner need not continue scanning for
	 * the terminal symbol appertaining to the set (e.g. with
	 * a "minimal-munch" rule).
	 * @return <CODE>true</CODE> iff any further lexemes with greater character lengths will not be added to this QScannerMatch if union'd.
	 */
	public abstract boolean containsFinalLexeme();
	
	public final boolean equals(Object rhs)
	{
		if(rhs instanceof QScannerMatch) return matchingSymbol.equals(((QScannerMatch) rhs).matchingSymbol);
		else return false;
	}

	public abstract boolean union(QScannerMatch rhs);
	
	public abstract boolean intersect(QScannerMatch rhs);
	
	public int hashCode()
	{
		return matchingSymbol.hashCode();
	}
}
