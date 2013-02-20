package edu.umn.cs.melt.copper.runtime.auxiliary.internal;

import java.util.BitSet;

/**
 * Contains static methods related to producing "pretty-printed" representations of objects. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class PrettyPrinter
{
	/**
	 * Produces a string representation of an iterable object.
	 * @param coll The object to print.
	 * @param linePrefix A string with which to prefix each line in the output (e.g., indentation).
	 * @param itemsPerLine The number of objects in the iterable to place on each line of the output.
	 */
	public static <E> String iterablePrettyPrint(Iterable<E> coll,String linePrefix,int itemsPerLine)
	{
		// TODO: Let the user pass rightLimit into this function and let the number of elements on each
		// line vary, for an optimal use of space.
		String rv = linePrefix + "[";
		boolean isFirst = true;
		int currentLineItemCount = -1;
		for(E obj : coll)
		{
			if(!isFirst) rv += ",";
			currentLineItemCount = (currentLineItemCount + 1) % itemsPerLine;
			if(currentLineItemCount == 0)
			{
				if(!isFirst) rv += "\n" + linePrefix + " ";
			}
			isFirst = false;
			rv += obj.toString();
		}
		rv += /*"\n" + linePrefix +*/ "]";
		return rv;
	}
	
	public static <E> int getOptimalItemsPerLine(Iterable<E> coll,int rightLimit)
	{
		int maxLength = 0;
		for(E obj : coll)
		{
			maxLength = Math.max(maxLength,obj.toString().length());
		}
		if(maxLength == 0 || maxLength >= rightLimit) return 1;
		else return Math.max(1,rightLimit / maxLength);
	}

	/**
	 * Produces a string representation of a bit set given a string representation of each bit.
	 * @param coll The bit set.
	 * @param nameMap The string representation of each bit.
	 * @param linePrefix A string with which to prefix each line in the output (e.g., indentation).
	 * @param itemsPerLine The number of objects in the bit set to place on each line of the output.
	 */
	public static String bitSetPrettyPrint(BitSet coll,String[] nameMap,String linePrefix,int itemsPerLine)
	{
		String rv = linePrefix + "[";
		boolean isFirst = true;
		int currentLineItemCount = -1;
		for(int i = coll.nextSetBit(0);i >= 0;i = coll.nextSetBit(i + 1))
		{
			if(!isFirst) rv += ",";
			currentLineItemCount = (currentLineItemCount + 1) % itemsPerLine;
			if(currentLineItemCount == 0)
			{
				if(!isFirst) rv += "\n" + linePrefix + " ";
			}
			isFirst = false;
			rv += nameMap[i];
		}
		rv += /*"\n" + linePrefix +*/ "]";
		return rv;
	}
}
