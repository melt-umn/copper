package edu.umn.cs.melt.copper.compiletime.auxiliary;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;

public class SetTiling<E>
{
	private ArrayList<E> tiledObjects;
	private Hashtable<Integer,BitSet> subsetMaps;
	
	public SetTiling()
	{
		tiledObjects = new ArrayList<E>();
		subsetMaps = new Hashtable<Integer,BitSet>();
	}
	
	public SetTiling(Hashtable<BitSet,E> map)
	{
		this();
		for(BitSet b : map.keySet()) addTiledObject(map.get(b),b);
	}
	
	public void addTiledObject(E obj,BitSet subsetOf)
	{
		tiledObjects.add(obj);
		for(int i = subsetOf.nextSetBit(0);i >= 0;i = subsetOf.nextSetBit(i+1))
		{
			if(!subsetMaps.containsKey(i)) subsetMaps.put(i,new BitSet(tiledObjects.size()));
			subsetMaps.get(i).set(tiledObjects.size() - 1);
		}
	}
	
	public E getTiledObject(int index)
	{
		return tiledObjects.get(index);
	}
	
	public BitSet getAllTilesCovering(int index)
	{
		return subsetMaps.get(index);
	}
}
