package edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa;

import java.util.BitSet;

/**
 * Holds lookahead sets (LALR(1) or otherwise) for each item in an LR DFA. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LRLookaheadSets
{
	protected int maxItemCount;
	protected BitSet[][] lookahead;
	
	public LRLookaheadSets(LR0DFA dfa)
	{
		int maxItemCount = -1;
		for(int i = 0;i < dfa.size();i++) maxItemCount = Math.max(maxItemCount,dfa.getItemSet(i).size());
		
		lookahead = new BitSet[dfa.size()][maxItemCount];
		for(int i = 0;i < dfa.size();i++)
		{
			for(int j = 0;j < dfa.getItemSet(i).size();j++) lookahead[i][j] = new BitSet();
		}
	}
	
	public int getMaxItemCount() { return maxItemCount; }
	
	public BitSet getLookahead(int state,int item) { return lookahead[state][item]; }
}
