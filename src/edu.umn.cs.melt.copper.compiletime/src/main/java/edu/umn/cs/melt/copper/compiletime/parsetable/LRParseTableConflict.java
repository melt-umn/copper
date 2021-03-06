package edu.umn.cs.melt.copper.compiletime.parsetable;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Holds information about an LR parse table conflict. 
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 * @author Kevin Viratyosin
 *
 * Modified by Kevin to allow for serialization
 */
public class LRParseTableConflict implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7814639360867425739L;
	private int state;
	private int symbol;
	
	/**
	 * If this conflict contains a shift action, this variable will contain its destination state.
	 * If the conflict consists only of reduce actions it will be equal to -1. 
	 */
	public int shift;
	/**
	 * For each reduce action in this conflict, this set will contain the number of its production.
	 */
	public BitSet reduce;

	public LRParseTableConflict(int state, int symbol)
	{
		this.state = state;
		this.symbol = symbol;
		this.shift = -1;
		this.reduce = new BitSet();
	}

	/**
	 * The state (parse table row) in which the conflict occurs. 
	 */
	public int getState()
	{
		return state;
	}

	/**
	 * The symbol (parse table column) on which the conflict occurs. 
	 */
	public int getSymbol()
	{
		return symbol;
	}
}
