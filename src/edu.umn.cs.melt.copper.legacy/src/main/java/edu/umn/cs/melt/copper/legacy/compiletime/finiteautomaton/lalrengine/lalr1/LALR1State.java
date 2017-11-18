package edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.lalr1;

import java.util.HashSet;

import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.LALRState;


/**
 * Represents a state in a LALR(1) DFA.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LALR1State implements LALRState<LALR1StateItem>
{
	HashSet<LALR1StateItem> items;
	
	public LALR1State()
	{
		items = new HashSet<LALR1StateItem>();
	}
	
	public LALR1State(Iterable<LALR1StateItem> items)
	{
		this();
		for(LALR1StateItem item : items)
		{
			this.items.add(item);
		}
	}
	
	/**
	 * Adds an item to this state.
	 * @param item The item to add.
	 * @return <CODE>true</CODE> iff the item were not already present.
	 */
	public boolean addItem(LALR1StateItem item)
	{
		return items.add(item);
	}
	
	/**
	 * Gets the items contained in this state.
	 * @return An iterable collection of items.
	 */
	public Iterable<LALR1StateItem> getItems()
	{
		return items;
	}
	
	/**
	 * Gets the size of this state.
	 * @return The number of items in this state.
	 */
	public int size()
	{
		return items.size();
	}

	/**
	 * Determines whether a particular item is contained in this state.
	 * @param item The item for which to check.
	 * @return <CODE>true</CODE> iff a version of the item be in this state.
	 */
	public boolean containsItem(LALR1StateItem item)
	{
		return items.contains(item);
	}
	
	/** Determines whether this state is an I-subset of another
	 * (i.e. its set of LR(0) items is a subset of <CODE>rhs</CODE>'s.
	 * @param rhs The possible I-superset of this state.
	 * @return <CODE>true</CODE> iff this is an I-subset of <CODE>rhs</CODE>.
	 */
	public boolean isISubset(LALR1State rhs)
	{
		return rhs.items.containsAll(items);
	}
	
	/**
	 * Determines the equality of two states.
	 * @param rhs The object against which to compare this state.
	 * @return <CODE>true</CODE> iff the objects be equal.
	 */
	public boolean equals(Object rhs)
	{
		if(rhs instanceof LALR1State)
		{
			return items.equals(((LALR1State) rhs).items);
		}
		else return false;
	}
	
	public int hashCode()
	{
		int hashSum = 0;
		for(LALR1StateItem item : items)
		{
			hashSum += item.hashCode();
		}
		return hashSum;
	}
	
	public String toString()
	{
		String rv = "[";
		for(LALR1StateItem item : items)
		{
			rv += "\n " + item.toString();
		}
		rv += "\n]";
		return rv;
	}
}
