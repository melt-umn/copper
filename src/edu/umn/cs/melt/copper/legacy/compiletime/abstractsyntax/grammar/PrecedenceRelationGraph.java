package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Queue;
import java.util.Stack;

import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * A graph to specify precedence relations among terminals.
 * Also used to specify macro dependencies among regexes.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class PrecedenceRelationGraph
{
	private Hashtable<Terminal,Integer> vertices;
	private boolean[][] adjacencyMatrix;
	private int[] inDegrees;
	
	public PrecedenceRelationGraph(Iterable<Terminal> vertices)
	{
		this.vertices = new Hashtable<Terminal,Integer>();
		int i = 0;
		for(Terminal vertex : vertices) this.vertices.put(vertex,i++);
		this.adjacencyMatrix = new boolean[i][i];
		inDegrees = new int[i];
	}
	
	private int getVertexNumber(Terminal vertex)
	{
		if(!hasVertex(vertex)) return -1;
		else return vertices.get(vertex);
	}
	
	public boolean hasVertex(Terminal vertex)
	{
		return vertices.containsKey(vertex);
	}
	
	public void removeVertex(Terminal vertex)
	{
		if(!hasVertex(vertex)) return;
		int vertexNum = getVertexNumber(vertex);
		inDegrees[vertexNum] = 0;
		for(int i = 0;i < adjacencyMatrix.length;i++)
		{
			adjacencyMatrix[vertexNum][i] = false;
			if(adjacencyMatrix[i][vertexNum] == true) inDegrees[i]--;
			adjacencyMatrix[i][vertexNum] = false;
		}
	}
	
	/**
	 * Adds an edge to the graph. N.B.: <CODE>bottom</CODE> and
	 * <CODE>top</CODE> are destination and source, respectively.
	 * @param bottom The terminal of lower precedence.
	 * @param top The terminal of higher precedence.
	 * @return <code>true</code> iff the edge was already in the graph.
	 */
	public boolean hasEdge(Terminal bottom,Terminal top)
	{
		if(!hasVertex(top) || !hasVertex(bottom)) return false;
		int topNumber = getVertexNumber(top);
		int bottomNumber = getVertexNumber(bottom);
		return (adjacencyMatrix[topNumber][bottomNumber] == true);
	}
	
	public void addEdge(Terminal bottom,Terminal top)
	{
		if(!hasVertex(top) || !hasVertex(bottom)) return;
		int topNumber = getVertexNumber(top);
		int bottomNumber = getVertexNumber(bottom);
		if(adjacencyMatrix[topNumber][bottomNumber] == false) inDegrees[bottomNumber]++;
		adjacencyMatrix[topNumber][bottomNumber] = true;
	}
	
	public int getInDegree(Terminal vertex)
	{
		if(!hasVertex(vertex)) return 0;
		else return inDegrees[getVertexNumber(vertex)];
	}

	public boolean detect2VertexCycle()
	{
		for(Terminal t : vertices.keySet())
		{
			for(Terminal u : vertices.keySet())
			{
				if(!t.equals(u) &&
				   adjacencyMatrix[getVertexNumber(t)][getVertexNumber(u)] == true &&
				   adjacencyMatrix[getVertexNumber(u)][getVertexNumber(t)] == true)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public void detectCycles(CompilerLogger logger,String location,Queue<Terminal> topologicallySortedVertices)
	throws CopperException
	{
		int[] colors = new int[adjacencyMatrix.length];
		for(Terminal u : vertices.keySet())
		{
			colors[getVertexNumber(u)] = FringeSymbols.WHITE;
		}
		for(Terminal u : vertices.keySet())
		{
			if(colors[getVertexNumber(u)] == FringeSymbols.WHITE) detectCyclesVisit(u,new Stack<Terminal>(),colors,logger,location,topologicallySortedVertices);
		}
	}
		
	private void detectCyclesVisit(Terminal u,
			                     Stack<Terminal> stackU,
			                     int[] colors,
			                     CompilerLogger logger,
			                     String location,
			                     Queue<Terminal> topologicallySortedVertices)
	throws CopperException
	{
		stackU.push(u);
		colors[getVertexNumber(u)] = FringeSymbols.GRAY;
		for(Terminal v : vertices.keySet())
		{
			if(!hasEdge(u,v)) continue;
			if(colors[getVertexNumber(v)] == FringeSymbols.WHITE) detectCyclesVisit(v,stackU,colors,logger,location,topologicallySortedVertices);
			else if(colors[getVertexNumber(v)] == FringeSymbols.GRAY)
			{
				if(logger.isLoggable(CompilerLogMessageSort.ERROR))
				{
					ArrayList<String> cycleMembers = new ArrayList<String>();
					int startIndex = stackU.lastIndexOf(v);
					if(startIndex != -1)
					{
						for(int i = startIndex;i < stackU.size();i++) cycleMembers.add(stackU.get(i).toString());
					}
					logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,"In " + location + ":\nCyclic precedence relation involving terminals\n" + PrettyPrinter.iterablePrettyPrint(cycleMembers,"  ",1) + " on graph\n" + this);
				}
			}
		}
		colors[getVertexNumber(u)] = FringeSymbols.BLACK;
		if(topologicallySortedVertices != null) topologicallySortedVertices.offer(u);
		stackU.pop();
	}

	public PrecedenceRelationGraph makeCut(Iterable<Terminal> vertices)
	{
		PrecedenceRelationGraph rv = new PrecedenceRelationGraph(vertices);
		int[] vertexNumbers = new int[rv.vertices.size()];
		for(Terminal t : vertices) vertexNumbers[rv.getVertexNumber(t)] = getVertexNumber(t);
		for(int i = 0;i < vertexNumbers.length;i++)
		{
			for(int j = 0;j < vertexNumbers.length;j++)
			{
				rv.adjacencyMatrix[i][j] = adjacencyMatrix[vertexNumbers[i]][vertexNumbers[j]];
				if(rv.adjacencyMatrix[i][j] == true) rv.inDegrees[j]++;
			}
		}
		return rv;
	}
	/**
	 * Partitions the "accept set" represented by this graph into accept and reject halves.
	 * @param logger The logger to which to pipe errors.
	 * @param location The location to which to attribute those errors.
	 * @return The partition, in the form of the reject set (terminals to be excluded).
	 * @throws DataFormatException When a semantic error such as a circular dependency occurs.
	 */
	public HashSet<Terminal> partitionAcceptSet(CompilerLogger logger,String location)
	throws CopperException
	{
		// DEBUG-X-BEGIN
		//System.err.println("Before partition, graph is: " + this);
		//System.err.print("In-degree set is this: [");
		//for(int i : inDegrees) System.err.print(" " + i);
		//System.err.println(" ]");
		// DEBUG-X-END
		detectCycles(logger,location,null);
		HashSet<Terminal> rej = new HashSet<Terminal>();
		HashSet<Terminal> sources = new HashSet<Terminal>();
		HashSet<Terminal> newSources;
		HashSet<Terminal> culled = new HashSet<Terminal>();
		for(Terminal t : vertices.keySet())
		{
			if(getInDegree(t) == 0) sources.add(t);
		}
		while(!sources.isEmpty())
		{
			newSources = new HashSet<Terminal>();
			culled.clear();
			for(Terminal t : sources)
			{
				for(Terminal u : vertices.keySet())
				{
					if(hasEdge(u,t)) culled.add(u);
				}
			}
			rej.addAll(culled);
			for(Terminal c : culled)
			{
				for(Terminal d : vertices.keySet())
				{
					if(hasEdge(d,c)) newSources.add(d);
				}
				removeVertex(c);
			}
			for(Iterator<Terminal> it = newSources.iterator();it.hasNext();)
			{
				Terminal t = it.next();
				if(getInDegree(t) != 0) it.remove();
			}
			sources = newSources;
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
	public HashSet<Terminal> getClosure(HashSet<Terminal> seed)
	{
		HashSet<Terminal> rv = new HashSet<Terminal>(seed);
		for(Terminal t : seed)
		{
			for(Terminal u : vertices.keySet())
			{
				if(hasEdge(t,u))
				{
					rv.add(u);
				}
			}
		}
		return rv;
	}
	
	public String toString()
	{
		String rv = "";
		ArrayList<String> vertexStrings = new ArrayList<String>(vertices.keySet().size());
		for(int i = 0;i < vertices.keySet().size();i++) vertexStrings.add(i,null);
		for(Terminal t : vertices.keySet()) vertexStrings.set(vertices.get(t),t.toString());
		for(int i = 0;i < vertices.keySet().size();i++) vertexStrings.set(i,i + ": " + vertexStrings.get(i));
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
