package edu.umn.cs.melt.copper.compiletime.parsetable;

import java.util.BitSet;

/**
 * A subclass of {@link LRParseTable} with no mutator methods.
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 *
 */
public class FixedLRParseTable extends LRParseTable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8623403485857573167L;
	public FixedLRParseTable(BitSet[] validLA, byte[][] actionType,	int[][] actionParameters, LRParseTableConflict[] conflicts)
	{
		super(validLA, actionType, actionParameters, conflicts);
	}

	
	public int getConflictCount() { return (conflicts == null) ? 0 : conflicts.length; }
	public LRParseTableConflict getConflict(int index) { return conflicts[index]; }
}
