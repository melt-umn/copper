package edu.umn.cs.melt.copper.compiletime.parsetable;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * A subclass of {@link LRParseTable} with mutator methods to change action types and parameters
 * and add new objects to represent parse table conflicts.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class MutableLRParseTable extends LRParseTable
{
	private ArrayList<LRParseTableConflict> buildingConflicts;
	
	private static BitSet[] newValidLA(int stateCount)
	{
		BitSet[] newValidLA = new BitSet[stateCount];
		for(int i = 0;i < stateCount;i++) newValidLA[i] = new BitSet();
		return newValidLA;
	}
	
	public MutableLRParseTable(int stateCount,int symbolCount)
	{
		super(newValidLA(stateCount),new byte[stateCount][symbolCount],new int[stateCount][symbolCount],null);
		buildingConflicts = new ArrayList<LRParseTableConflict>();
	}
	
	public void setActionType(int state,int symbol,byte type) { actionType[state][symbol] = type; }
	public void setActionParameter(int state,int symbol,int parameter) { actionParameters[state][symbol] = parameter; }
	
	public int getConflictCount() { return (buildingConflicts == null) ? 0 : buildingConflicts.size(); }
	public LRParseTableConflict getConflict(int index) { return buildingConflicts.get(index); }
	public int addConflict(LRParseTableConflict conflict)
	{
		buildingConflicts.add(conflict);
		return buildingConflicts.size() - 1;
	}
	
	/*public FixedLRParseTable fixedCopy()
	{
		return new FixedLRParseTable(validLA,actionType,actionParameters,(LRParseTableConflict[]) buildingConflicts.toArray());
	}*/
}
