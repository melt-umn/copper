package edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.lalr1;

import java.util.ArrayList;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production;


/**
 * Represents an item in a LALR(1) DFA.
 * 
 * <p>This class is now immutable: it already was in effect, but we made frequent copies 
 * just in case it wasn't.  Now these copies can be avoided.
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;, Ted Kaminski &lt;<a href="mailto:tedinski@cs.umn.edu">tedinski@cs.umn.edu</a>&gt;
 */
public class LALR1StateItem
{
	protected final Production prod;
	protected final int position;

	/**
	 * Create an initial state item for a production.
	 * 
	 * @param prod The production at position 0.
	 */
	public LALR1StateItem(Production prod)
	{
		this(prod,0);
	}
	/**
	 * This isn't protected for any special reason except that it's not used elsewhere.
	 * 
	 * @param prod  The production for this state item
	 * @param position  The position in the production's rhs where this state item is located.
	 */
	protected LALR1StateItem(Production prod,int position)
	{
		// Normalize the position
		if(position < 0) position = 0;
		else if(position > prod.length()) position = prod.length();
		
		this.prod = prod;
		this.position = position;
	}

	/**
	 * Determines whether the dot is at the beginning of this item's production.
	 * @return <CODE>true</CODE> iff the position of the dot lies before the first symbol of the production.
	 */
	public boolean isBeginning()
	{
		return (position == 0);
	}
	
	/**
	 * Determines whether or not a derivative of this item will be "encapsulated" within
	 * the item (i.e. not beginning at this item's beginning or ending at its end).
	 * @return <CODE>true</CODE> iff the position of the dot lies between two symbols of the production.
	 */
	public boolean isEncapsulated()
	{
		return !(isBeginning() || (position == prod.length()));
	}
	
	/**
	 * Determines whether a shift or goto action may be performed on a state item.
	 * @return <CODE>true</CODE> iff the position marker is not at the end of the production. 
	 */
	public boolean isShiftable()
	{
		return (position < prod.length());
	}
	
	/**
	 * Determines whether a reduce action may be performed on a state item.
	 * @return <CODE>true</CODE> iff the position marker is at the end of the production. 
	 */
	public boolean isReducible()
	{
		return position == prod.length();
	}
	
	/**
	 * Determines whether an item is the end item ^ ::= S (*) $.
	 * @return <CODE>true</CODE> iff the production length is 2, the position is 1, the left side is ^, and the symbol at position is $.
	 */
	public boolean isEndItem()
	{
		return (prod.length() == 2 &&
				position == 1 &&
				prod.getLeft().equals(FringeSymbols.STARTPRIME) &&
				getSymbolAtPosition().equals(FringeSymbols.EOF));
	}

	/**
	 * @return Returns the position.
	 */
	public int getPosition()
	{
		return position;
	}
	
	/**
	 * Gets the grammar symbol at the dot for this item.
	 * @return The grammar symbol at the dot, or <CODE>null</CODE> if the dot is at the end.
	 */
	public GrammarSymbol getSymbolAtPosition()
	{
		if(isShiftable()) return prod.getSymbol(getPosition());
		else return null;
	}
	
	/**
	 * @return A list of the remaining symbols for this item, after (not including)
	 * the current position
	 */
	public ArrayList<GrammarSymbol> getSymbolsAfterPosition()
	{
		ArrayList<GrammarSymbol> rv = new ArrayList<GrammarSymbol>();
		for(int i = position + 1;i < prod.length();i++)
		{
			rv.add(prod.getSymbol(i));
		}
		return rv;
	}

	/**
	 * @return A state item with the position marker advanced by one symbol.
	 */
	public LALR1StateItem advancePosition()
	{
		return new LALR1StateItem(prod, position + 1);
	}
	
	/**
	 * @return Returns the prod.
	 */
	public Production getProd()
	{
		return prod;
	}
	
	public boolean equals(Object rhs)
	{
		if(rhs instanceof LALR1StateItem)
		{
			return prod.equals(((LALR1StateItem) rhs).prod) &&
		       position == ((LALR1StateItem) rhs).position;

		}
		else return false;
	}

	public String toString()
	{
		int i = 0;
		String rv = prod.getLeft() + " ::=";
		for(GrammarSymbol sym : prod.getRight())
		{
			if(i++ == position) rv += " (*)";
			rv += " " + sym;
		}
		if(i == position) rv += " (*)";
		return rv;
	}

	public int hashCode()
	{
		return prod.hashCode() ^ position;
	}	
}
