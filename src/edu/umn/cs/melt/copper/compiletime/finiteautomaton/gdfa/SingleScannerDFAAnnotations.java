package edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa;

import java.util.BitSet;

/**
 * Holds annotations on states in a scanner DFA: partitioned accept sets and reject sets, and possible sets,
 * as well as a list of circular precedence dependencies (e.g., if A and B are on each other's submit-lists).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class SingleScannerDFAAnnotations
{
	public BitSet[] acceptSets;
	public BitSet[] rejectSets;
	public BitSet[] possibleSets;
	public int[] charMap;
	
	public BitSet[] circularDependencies;
	
	public SingleScannerDFAAnnotations(BitSet[] acceptSets, BitSet[] rejectSets, BitSet[] possibleSets,int[] charMap,BitSet[] circularDependencies)
	{
		this.acceptSets = acceptSets;
		this.rejectSets = rejectSets;
		this.possibleSets = possibleSets;
		this.charMap = charMap;
		this.circularDependencies = circularDependencies;
	}
	
	public int size() { return acceptSets.length; }
	
	public final BitSet getAcceptSet(int state)   { return acceptSets[state]; }
	public final BitSet getRejectSet(int state)   { return rejectSets[state]; }
	public final BitSet getPossibleSet(int state) { return possibleSets[state]; }
	
}
