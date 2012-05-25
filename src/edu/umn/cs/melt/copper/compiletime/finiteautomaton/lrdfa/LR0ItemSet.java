package edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa;

import java.util.Arrays;

/**
 * Holds a single LR item set (without lookahead). 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LR0ItemSet
{
	/**
	 * Constructs an item set.
	 * @param items Should consist of integer pairs, even-indexed
	 *              elements holding production numbers, odd-indexed
	 *              elements holding the position of the item's bullet
	 *              point.
	 */
	public LR0ItemSet(int[] items)
	{
		this.items = items;
		hash = Arrays.hashCode(items);
		size = items.length / 2;
	}
	
	public void copyItems(int[] newItems)
	{
		System.arraycopy(items,0,newItems,0,items.length);
	}
	
	private int[] items;
	private int hash;
	private int size;
	
	/** Returns the number of the production pertaining to the {@code i}th item in the item set. */ 
	public int getProduction(int i) { return items[2*i]; }
	/** Returns the bullet-point position of the {@code i}th item in the item set. */
	public int getPosition(int i) { return items[2*i+1]; }
	
	public int hashCode()
	{
		return hash;
	}
	
	public int size()
	{
		return size;
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof LR0ItemSet)) return false;
		LR0ItemSet l = (LR0ItemSet) o;
		if(hash != l.hash) return false;
		else return Arrays.equals(items,l.items);
	}
	
	public String toString()
	{
		String rv = "[";
		for(int i = 0;i < size;i++)
		{
			if(i > 0) rv += ",";
			rv += "(" + items[2*i] + "," + items[2*i+1] + ")";
		}
		rv += "]";
		return rv;
	}
}