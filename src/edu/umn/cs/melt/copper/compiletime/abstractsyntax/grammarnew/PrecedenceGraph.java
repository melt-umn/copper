package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew;

import java.util.BitSet;
import java.util.Queue;

import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * Extends {@link Digraph} with methods specifically related to lexical precedence
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class PrecedenceGraph extends Digraph
{
	public PrecedenceGraph(int vertexCount)
	{
		super(vertexCount);
	}

	/**
	 * Partitions the "accept set" represented by this graph into accept and reject halves.
	 * @param logger The logger to which to pipe errors.
	 * @param location The location to which to attribute those errors.
	 * @return The partition, in the form of the reject set (terminals to be excluded).
	 * @throws CopperException When an error such as a circular dependency occurs.
	 */
	public <T> BitSet partitionAcceptSet(Queue<BitSet> detectedCycles,BitSet acceptSet)
	{
		// DEBUG-X-BEGIN
		//System.err.println("Before partition, graph is: " + this);
		//System.err.print("In-degree set is this: [");
		//for(int i : inDegrees) System.err.print(" " + i);
		//System.err.println(" ]");
		// DEBUG-X-END
		detectCycles(acceptSet, detectedCycles, null);
		if(!detectedCycles.isEmpty())
		{
			return acceptSet;
		}
		BitSet acceptSetW = new BitSet(acceptSet.length());
		acceptSetW.or(acceptSet);
		BitSet rej = new BitSet(acceptSet.length());
		BitSet sources = new BitSet(acceptSet.length());
		BitSet culled = new BitSet(acceptSet.length());
		for(int i = acceptSetW.nextSetBit(0);i >= 0;i = acceptSetW.nextSetBit(i+1))
		{
			if(inDegrees[i] == 0) sources.set(i);
		}
		while(!sources.isEmpty())
		{
			culled.clear();
			
			for(int t = sources.nextSetBit(0);t >= 0;t = sources.nextSetBit(t+1))
			{
				for(int u = acceptSetW.nextSetBit(0);u >= 0;u = acceptSetW.nextSetBit(u+1))
				{
					if(hasEdge(u,t)) culled.set(u);
				}
			}
			rej.or(culled);
			sources.clear();
			for(int c = culled.nextSetBit(0);c >= 0;c = culled.nextSetBit(c+1))
			{
				for(int d = acceptSetW.nextSetBit(0);d >= 0;d = acceptSetW.nextSetBit(d+1))
				{
					if(hasEdge(d,c)) sources.set(d);
				}
				acceptSetW.clear(c);
			}
			for(int t = sources.nextSetBit(0);t >= 0;t = sources.nextSetBit(t+1))
			{
				if(inDegrees[t] != 0) sources.clear(t);
			}
		}
		// DEBUG-X-BEGIN
		//System.err.println("After partition, graph is: " + this);
		//System.err.println("Produced reject set " + rej);
		// DEBUG-X-END
		return rej;
	}
	
	/**
	 * Adds to a set of "seed" terminals all terminals of higher precedence.
	 * @param seed The set of "seed" terminals.
	 * @return The set of "seed" terminals union'd with the set of terminals dominating one or more of the same.
	 */
	public BitSet getClosure(BitSet seed)
	{
		BitSet rv = new BitSet(vertexCount);
		rv.or(seed);
		for(int t = seed.nextSetBit(0);t >= 0;t = seed.nextSetBit(t+1))
		{
			for(int u = 0;u < vertexCount;u++)
			{
				if(hasEdge(t,u))
				{
					rv.set(u);
				}
			}
		}
		return rv;
	}
	
	public String toString()
	{
		StringBuffer rv = new StringBuffer();
		rv.append("Adjacency matrix\n(read as column < row): \n");
		for(int i = 0;i < adjacencyMatrix.length;i++)
		{
			rv.append("   ");
			for(int j = 0;j < adjacencyMatrix.length;j++)
			{
				if(adjacencyMatrix[i][j] == true) rv.append("1 ");
				else rv.append("- ");
			}
			rv.append("\n");
		}
		return rv.toString();
	}
}
