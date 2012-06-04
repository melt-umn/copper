package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Queue;
import java.util.Stack;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;

/**
 * A generic digraph implementation, used to represent precedence relations.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class Digraph
{
	private static final int WHITE = 0, GRAY = 1, BLACK = 2;
	
	protected int vertexCount;
	protected boolean[][] adjacencyMatrix;
	protected int[] inDegrees;
	
	public Digraph(int vertexCount)
	{
		this.vertexCount = vertexCount;
		this.adjacencyMatrix = new boolean[vertexCount][vertexCount];
		this.inDegrees = new int[vertexCount];
	}

	/**
	 * Checks for the presence of an edge in the graph.
	 * @param bottom The terminal of lower precedence.
	 * @param top The terminal of higher precedence.
	 * @return <code>true</code> iff the graph contains the given edge.
	 */
	public boolean hasEdge(int bottom,int top)
	{
		if(top < 0 || top >= vertexCount || bottom < 0 || bottom >= vertexCount) return false;
		return (adjacencyMatrix[top][bottom] == true);
	}
	
	/**
	 * Adds an edge to the graph. N.B.: <CODE>bottom</CODE> and
	 * <CODE>top</CODE> are destination and source, respectively.
	 * @param bottom The terminal of lower precedence.
	 * @param top The terminal of higher precedence.
	 * @return <code>true</code> iff the edge was already in the graph.
	 */
	public boolean addEdge(int bottom,int top)
	{
		if(top < 0 || top >= vertexCount || bottom < 0 || bottom >= vertexCount) return true;
		if(adjacencyMatrix[top][bottom] == false) inDegrees[bottom]++;
		boolean rv = adjacencyMatrix[top][bottom];
		adjacencyMatrix[top][bottom] = true;
		return rv;
	}
	
	protected Digraph cut(BitSet vertices)
	{
		if(vertices.length() > vertexCount) return null;
		Digraph rv = new Digraph(vertexCount);
		for(int i = vertices.nextSetBit(0);i >= 0;i = vertices.nextSetBit(i+1))
		{
			for(int j = vertices.nextSetBit(0);j >= 0;j = vertices.nextSetBit(i+1))
			{
				rv.adjacencyMatrix[i][j] = adjacencyMatrix[i][j];
				if(rv.adjacencyMatrix[i][j] == true) rv.inDegrees[j]++;
			}
		}
		return rv;
	}
	
	protected boolean detectCycles(BitSet vertices,Queue<BitSet> detectedCycles,Queue<Integer> topologicallySortedVertices)
	{
		int[] colors = new int[vertexCount];
		Stack<Integer> searchStack = new Stack<Integer>();
		for(int i = vertices.nextSetBit(0);i >= 0;i = vertices.nextSetBit(i+1))
		{
			if(colors[i] == WHITE)
			{
				searchStack.push(i);
				detectCyclesVisit(vertices,detectedCycles,topologicallySortedVertices,colors,searchStack);
				searchStack.pop();
			}
		}
		return !detectedCycles.isEmpty();
	}
		
	private void detectCyclesVisit(BitSet vertices,
			   					   Collection<BitSet> detectedCycles,
            					   Queue<Integer> topologicallySortedVertices,
			                       int[] colors,
			                       Stack<Integer> searchStack)
	{
		int u = searchStack.peek();
		colors[u] = GRAY;
		for(int v = vertices.nextSetBit(0);v >= 0;v = vertices.nextSetBit(v+1))
		{
			if(!hasEdge(u,v)) continue;
			if(colors[v] == WHITE)
			{
				searchStack.push(v);
				detectCyclesVisit(vertices,detectedCycles,topologicallySortedVertices,colors,searchStack);
				searchStack.pop();
			}
			else if(colors[v] == GRAY)
			{
				BitSet cycleMembers = new BitSet(vertexCount);
				cycleMembers.set(v);
				for(int i = searchStack.size() - 1;i >= 0 && searchStack.get(i) != v;i--) cycleMembers.set(i);
				detectedCycles.add(cycleMembers);
			}
		}
		colors[u] = BLACK;
		if(topologicallySortedVertices != null) topologicallySortedVertices.offer(u);
	}
		
	public <T> String toString(SymbolTable<T> symbolTable)
	{
		String rv = "";
		ArrayList<String> vertexStrings = new ArrayList<String>(vertexCount);
		for(int i = 0;i < vertexCount;i++) vertexStrings.set(i,i + ": " + symbolTable.get(i));
		rv += "Vertices: [\n" + PrettyPrinter.iterablePrettyPrint(vertexStrings,"  ",1) + "];\nAdjacency matrix\n(read as column < row): \n";
		for(int i = 0;i < adjacencyMatrix.length;i++)
		{
			rv += "   ";
			for(int j = 0;j < adjacencyMatrix.length;j++)
			{
				if(adjacencyMatrix[i][j] == true) rv += "1 ";
				else rv += "0 ";
			}
			rv += "\n";
		}
		return rv;
	}

}
