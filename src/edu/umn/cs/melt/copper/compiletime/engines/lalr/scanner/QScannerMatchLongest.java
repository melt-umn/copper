package edu.umn.cs.melt.copper.compiletime.engines.lalr.scanner;


import java.util.ArrayList;
import java.util.HashSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;



/* (non-Javadoc)
 * A type of QScannerMatch retaining a record of only the
 * longest match encountered.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class QScannerMatchLongest extends QScannerMatch
{
	private QScannerMatchData lexeme;
	
	/* (non-Javadoc)
	 * Creates a new instance of QScannerMatchLongest.
	 * @param matchingSymbol The initial token or symbol.
	 */
	public QScannerMatchLongest(Terminal matchingSymbol,InputPosition positionPreceding,InputPosition positionFollowing,ArrayList<QScannerMatchData> layouts)
	{
		super(matchingSymbol);
		if(matchingSymbol.isBareSym()) lexeme = null;
		else lexeme = new QScannerMatchData(matchingSymbol,positionPreceding,positionFollowing,layouts);
	}
	
	/* (non-Javadoc)
	 * Creates a new instance of QScannerMatchLongest.
	 * @param matchingSymbol The initial token or symbol.
	 */
	public QScannerMatchLongest(Terminal matchingSymbol,InputPosition positionPreceding,InputPosition positionFollowing,QScannerMatchData layout)
	{
		super(matchingSymbol);
		if(matchingSymbol.isBareSym()) lexeme = null;
		else lexeme = new QScannerMatchData(matchingSymbol,positionPreceding,positionFollowing,layout);
	}
	
	public Iterable<QScannerMatchData> getLexemes()
	{
		HashSet<QScannerMatchData> lexemes = new HashSet<QScannerMatchData>();
		if(lexeme != null) lexemes.add(lexeme);
		return lexemes;
	}
	
	public boolean containsLexeme(Terminal lexeme)
	{
		if(this.lexeme == null) return false;
		else return (this.lexeme.getToken().equals(lexeme));
	}
	
	public boolean containsFinalLexeme() { return false; }
	
	public boolean union(QScannerMatch rhs)
	{
		if(equals(rhs))
		{
			QScannerMatchData max = null;
			for(QScannerMatchData t : rhs.getLexemes())
			{
				if(max == null || t.getToken().getLexeme().length() > max.getToken().getLexeme().length()) max = t;
			}
			if(lexeme == null)
			{
				if(max != null)
				{
					lexeme = max;
					return true;
				}
			}
			else if(max != null)
			{
				if(max.getToken().getLexeme().length() > lexeme.getToken().getLexeme().length())
				{
					lexeme = max;
					return true;
				}
			}
		}
		return false;
	}

	public boolean intersect(QScannerMatch rhs)
	{
		union(rhs);
		return false;
	}
	
	public String toString()
	{
		return "{Longest match: " + lexeme.toString() + "}";
	}

	/* (non-Javadoc)
	 * Class to generate instances of QScannerMatchLongest.
	 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
	 *
	 */
	public static class Generator implements QScannerMatchGenerator
	{
		public QScannerMatch getMatch(Terminal matchingSymbol,InputPosition positionPreceding,InputPosition positionFollowing,ArrayList<QScannerMatchData> layouts) { return new QScannerMatchLongest(matchingSymbol,positionPreceding,positionFollowing,layouts); }
		public QScannerMatch getMatch(Terminal matchingSymbol,InputPosition positionPreceding,InputPosition positionFollowing,QScannerMatchData layout)           { return new QScannerMatchLongest(matchingSymbol,positionPreceding,positionFollowing,layout);  }
	}

}
