package edu.umn.cs.melt.copper.compiletime.parsetable;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Holds a parse table in the form of two matrices, one holding an action's "type" (error, shift, reduce, etc.)
 * and one holding its parameter (destination state, production to reduce on, etc.) 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 * @author Kevin Viratyosin
 *
 * Modified by Kevin to include a print function, serialization
 */
public abstract class LRParseTable implements Serializable
{
	// SHIFT and GOTO are assumed to be identical in the class LRParseTableBuilder.
	
	/** Action type code for an "error" action. The parameter will be disregarded. */
	public static final byte ERROR = 0;
	/** Action type code for a shift action. The parameter is taken to be the number of the destination state. */
	public static final byte SHIFT = 1;
	/** Action type code for a goto action. The parameter is taken to be the number of the destination state. */
	public static final byte GOTO = 1;
	/** Action type code for an accept action. The parameter will be disregarded. */
	public static final byte ACCEPT = 1;
	/** Action type code for a reduce action. The parameter is taken to be the number of the production on which to reduce. */
	public static final byte REDUCE = 2;
	/** Action type code for an unresolved conflict.
	 * The parameter is taken to be the index of the conflict.
	 * 
	 * @see #getConflict(int)
	 */
	public static final byte CONFLICT = 3;

	protected BitSet[] validLA;
	protected byte[][] actionType;
	// Parameter will be:
	//   For ERROR or ACCEPT, irrelevant.
	//   For SHIFT, a state number.
	//   For REDUCE, a production number.
	//   For CONFLICT, the index of a conflict.
	protected int[][] actionParameters;
	protected LRParseTableConflict[] conflicts;
		
	protected LRParseTable(BitSet[] validLA, byte[][] actionType,int[][] actionParameters,LRParseTableConflict[] conflicts)
	{
		this.validLA = validLA;
		this.actionType = actionType;
		this.actionParameters = actionParameters;
		this.conflicts = conflicts;
	}
	
	public final BitSet getValidLA(int state) { return validLA[state]; }
	public final byte getActionType(int state,int symbol) { return actionType[state][symbol]; }
	public final int getActionParameter(int state,int symbol) { return actionParameters[state][symbol]; }

	public abstract int getConflictCount();
	public abstract LRParseTableConflict getConflict(int index);
	
	public int size() { return validLA.length; }

	public void print() {
		int rows = actionType.length;
		int cols = actionType[0].length;

		int entryWidth = 4;

		System.out.print("State | ");
		for (int i = 0; i < cols; i++) {
			System.out.print(fixString(String.valueOf(i), entryWidth) + " ");
		}
		System.out.println();

		for (int i = 0; i < rows; i++) {
			System.out.print(fixString(String.valueOf(i), 6) + "| ");
			for (int j = 0; j < cols; j++) {
				String entry = "";
				switch (actionType[i][j]) {
					case ERROR:
						entry += 'e';
						break;
					case SHIFT:
						entry += 's';
						break;
					case REDUCE:
						entry += 'r';
						break;
					default:
						entry += 'x';
						break;
				}
				entry += actionParameters[i][j];
				System.out.print(fixString(entry, entryWidth) + " ");
			}
			System.out.println();
		}
	}

	private String fixString(String str, int size) {
		int len = str.length();
		if (len < size) {
			String ret = str;
			for (int i = 0; i < size - len; i++) {
				ret = ret + " ";
			}
			return ret;
		} else {
			return str;
		}
	}
}
