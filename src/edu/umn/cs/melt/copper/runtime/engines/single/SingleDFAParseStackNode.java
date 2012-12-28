package edu.umn.cs.melt.copper.runtime.engines.single;

import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public class SingleDFAParseStackNode
{
	public int statenum;
	public InputPosition pos;
	public Object synthAttr;
	
	public SingleDFAParseStackNode(int statenum,InputPosition pos,Object synthAttr)
	{
		this.statenum = statenum;
		this.pos = pos;
		this.synthAttr = synthAttr;
	}
}
