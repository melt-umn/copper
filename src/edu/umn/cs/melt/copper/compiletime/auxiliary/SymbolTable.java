package edu.umn.cs.melt.copper.compiletime.auxiliary;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Implements a constant-time bidirectional mapping between objects and integers.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 * @param <T>
 */
public class SymbolTable<T> implements Iterable<T>
{
	private Hashtable<T,Integer> forward;
	private Object[] reverse;
	
	@SuppressWarnings("unchecked")
	public SymbolTable(Collection<T> objects)
	{
		forward = new Hashtable<T,Integer>();
		reverse = objects.toArray();
		for(int i = 0;i < reverse.length;i++) forward.put((T) reverse[i],i);
	}
	
	@SuppressWarnings("unchecked")
	public T get(int i)
	{
		if(i < 0 || i >= size()) return null;
		return (T) reverse[i];
	}
	
	public int get(T o)
	{
		if(forward.containsKey(o)) return forward.get(o);
		else return -1;
	}
	
	public Iterator<T> iterator()
	{
		return forward.keySet().iterator();
	}
	
	public int size()
	{
		return reverse.length;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<T> cut(int offset,int count)
	{
		ArrayList<T> cut = new ArrayList<T>();
		if(offset < 0 || offset >= reverse.length || (offset+count) < 0 || (offset+count) >= reverse.length) return cut;
		for(int i = offset;i < (offset+count);i++) cut.add((T) reverse[i]);
		return cut;
	}

	@SuppressWarnings("unchecked")
	public Collection<T> cut(BitSet members)
	{
		ArrayList<T> cut = new ArrayList<T>();
		for(int i = members.nextSetBit(0);i >= 0;i = members.nextSetBit(i+1)) cut.add((T) reverse[i]);
		return cut;
	}
	
	public int convert(SymbolTable<T> otherTable,int inOtherTable)
	{
		return get(otherTable.get(inOtherTable));
	}
}