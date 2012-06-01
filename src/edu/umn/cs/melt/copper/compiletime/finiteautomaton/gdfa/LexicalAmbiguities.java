package edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa;

import java.util.BitSet;

/**
 * Holds information about lexical ambiguities.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LexicalAmbiguities
{
	// The indices of ambiguities that are unresolved.
	protected BitSet unresolved;
	// The set of ambiguous terminals.
	protected BitSet[] ambiguities;
	// The set of parser states where the ambiguities occur.
	protected BitSet[] locations;
	// -1 if resolved by context; the disambiguation function number if resolved
	// by disambiguation function.
	protected int[] resolutions;
	
	public LexicalAmbiguities(BitSet unresolved,BitSet[] ambiguities,BitSet[] locations,int[] resolutions)
	{
		this.unresolved = unresolved;
		this.ambiguities = ambiguities;
		this.locations = locations;
		this.resolutions = resolutions;
	}
	
	public int size() { return ambiguities.length; }
	
	public boolean isUnresolved(int i) { return unresolved.get(i); }
	public BitSet getAmbiguity(int i) { return ambiguities[i]; }
	public BitSet getLocations(int i) { return locations[i]; }
	public int getResolution(int i) { return resolutions[i]; } 
}
