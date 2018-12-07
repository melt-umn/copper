package edu.umn.cs.melt.copper.compiletime.scannerdfa;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Holds annotations on states in a scanner DFA: partitioned accept sets and reject sets, and possible sets,
 * as well as a list of circular precedence dependencies (e.g., if A and B are on each other's submit-lists).
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 * @author Kevin Viratyosin
 *
 * Modified to allow serialization
 */
public class SingleScannerDFAAnnotations implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8644249517473433823L;
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
