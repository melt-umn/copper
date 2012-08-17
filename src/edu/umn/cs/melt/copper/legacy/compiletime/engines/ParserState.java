package edu.umn.cs.melt.copper.legacy.compiletime.engines;

import edu.umn.cs.melt.copper.runtime.io.InputPosition;

/**
 * Holds information relating to a parse state.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ParserState implements Comparable<ParserState>
{
	private int statenum;
	private InputPosition pos;
	
	public ParserState(int statenum,InputPosition pos)
	{
		this.pos = pos;
		this.statenum = statenum;
	}

	/**
	 * @return Returns the pos.
	 */
	public InputPosition getPos()
	{
		return pos;
	}

	/**
	 * @return Returns the statenum.
	 */
	public int getStatenum()
	{
		return statenum;
	}
	
	public boolean equals(Object rhs)
	{
		if(rhs instanceof ParserState)
		{
			return (statenum == ((ParserState) rhs).statenum &&
					pos.equals(((ParserState) rhs).pos));
		}
		else return false;
	}
	
	public int compareTo(ParserState rhs)
	{
		int cmp = 0;
		int posdiff = pos.diff(rhs.pos);
		cmp = Integer.signum(posdiff);
		if(cmp == 0)
		{
			cmp = Integer.signum(statenum);
		}
		return cmp;
	}
	
	public String toString()
	{
		return statenum + " (" + pos + ")";
	}
}
