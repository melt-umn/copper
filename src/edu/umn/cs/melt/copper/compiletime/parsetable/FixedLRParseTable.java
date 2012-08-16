package edu.umn.cs.melt.copper.compiletime.parsetable;

import java.util.BitSet;

/**
 * A subclass of {@link LRParseTable} with no mutator methods.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class FixedLRParseTable extends LRParseTable
{
	public FixedLRParseTable(BitSet[] validLA, byte[][] actionType,	int[][] actionParameters, LRParseTableConflict[] conflicts)
	{
		super(validLA, actionType, actionParameters, conflicts);
	}

	
	public int getConflictCount() { return (conflicts == null) ? 0 : conflicts.length; }
	public LRParseTableConflict getConflict(int index) { return conflicts[index]; }
}
