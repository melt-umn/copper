package edu.umn.cs.melt.copper.compiletime.lrdfa;

import java.util.BitSet;

/**
 * Holds lookahead sets (LALR(1) or otherwise) for each item in an LR DFA, and valid layout sets
 * for each DFA state. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LRLookaheadAndLayoutSets extends LRLookaheadSets
{
	protected BitSet[] layoutSets;
	
	public LRLookaheadAndLayoutSets(LR0DFA dfa)
	{
		super(dfa);
		layoutSets = new BitSet[dfa.size()];
		
		for(int i = 0;i < dfa.size();i++)
		{
			layoutSets[i] = new BitSet();
		}
	}
	
	public BitSet getLayout(int state) { return layoutSets[state]; }

}
