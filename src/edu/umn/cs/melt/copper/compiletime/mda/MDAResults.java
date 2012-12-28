package edu.umn.cs.melt.copper.compiletime.mda;

import java.util.BitSet;

public class MDAResults
{
	/** Error type code for lookahead spillage. */
	public static final byte LOOKAHEAD_SPILLAGE = 0;
	/** Error type code for follow spillage. */
	public static final byte FOLLOW_SPILLAGE = 1;
	/** Error type code for new-host states that are not I-subsets of any host state. */
	public static final byte NON_IL_SUBSET = 2;
	/** Error type code for new-host states that is an I-subset of some host state but is not an IL-subset of any. */
	public static final byte I_SUBSET_ONLY = 3;
	
	protected byte[] errorType;
	
	// For lookahead spillage, these are lookahead sets.
	// For follow spillage, these are follow sets.
	// For non-IL-subset conditions, these are unused.
	protected BitSet[] hostSets;
	protected BitSet[] fullSets;
	
	// For follow spillage, these are nonterminals.
	// For everything else, they are state numbers.
	protected int[] hostStates;
	protected int[] fullStates;
	
	// For lookahead spillage, these are item numbers.
	// For I_SUBSET_ONLY conditions, these contain one number, that of the I-superset host state.
	protected BitSet[] locations;
	
	protected BitSet hostPartition;
	protected BitSet extPartition;
	protected BitSet newHostPartition;
	
	public MDAResults(byte[] errorType, BitSet[] hostSets, BitSet[] fullSets,
			int[] hostStates, int[] fullStates, BitSet[] locations,
			BitSet hostPartition, BitSet extPartition, BitSet newHostPartition)
	{
		this.errorType = errorType;
		this.hostSets = hostSets;
		this.fullSets = fullSets;
		this.hostStates = hostStates;
		this.fullStates = fullStates;
		this.locations = locations;
		this.hostPartition = hostPartition;
		this.extPartition = extPartition;
		this.newHostPartition = newHostPartition;
	}
	
	public int size() { return errorType.length; }
	
	public byte getErrorType(int error) { return errorType[error]; }
	
	public BitSet getHostLookaheadSet(int error) { return hostSets[error]; }
	public BitSet getHostFollowSet(int error)    { return hostSets[error]; }
	public BitSet getFullLookaheadSet(int error) { return fullSets[error]; }
	public BitSet getFullFollowSet(int error)    { return fullSets[error]; }
	
	public int getHostState(int error) 		 { return hostStates[error]; }
	public int getFullState(int error) 		 { return fullStates[error]; }
	public int getNonterminal(int error) 	 { return hostStates[error]; }
	
	public BitSet getItems(int error) { return locations[error]; }
	
	public int getISuperset(int error) { return locations[error].nextSetBit(0); }
	
	public BitSet getHostPartition()    { return hostPartition; }
	public BitSet getExtPartition()     { return extPartition; }
	public BitSet getNewHostPartition() { return newHostPartition; }
}