package edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine;

public interface LALRState<ITEM>
{
	/**
	 * Adds an item to this state.
	 * @param item The item to add.
	 * @return <CODE>true</CODE> iff the item were not already present.
	 */
	public boolean addItem(ITEM item);

	/**
	 * Gets the items contained in this state.
	 * @return An iterable collection of items.
	 */
	public Iterable<ITEM> getItems();

	/**
	 * Gets the size of this state.
	 * @return The number of items in this state.
	 */
	public int size();

	/**
	 * Determines whether a particular item is contained in this state.
	 * @param item The item for which to check.
	 * @return <CODE>true</CODE> iff a version of the item is in this state.
	 */
	public boolean containsItem(ITEM item);

	@Override
	public boolean equals(Object rhs);

	/**
	 * This hashcode function gets item hashcodes, sorts them and
	 * hashes the resulting tree-set.
	 */
	public int hashCode();

	public String toString();

}
