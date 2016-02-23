package edu.umn.cs.melt.copper.compiletime.lrdfa;

import java.io.Serializable;
import java.util.BitSet;

/**
 * Holds lookahead sets (LALR(1) or otherwise) for each item in an LR DFA. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 * @author Kevin Viratyosin
 *
 * Modified by Kevin to include itemLASources, serializable
 */
public class LRLookaheadSets implements Serializable
{
	protected int maxItemCount;
	protected BitSet[][] lookahead;
	protected BitSet[][] itemLASources; // state -> item -> BitSet<nt>
	
	public LRLookaheadSets(LR0DFA dfa)
	{
		maxItemCount = -1;
		for(int i = 0;i < dfa.size();i++) maxItemCount = Math.max(maxItemCount,dfa.getItemSet(i).size());

		lookahead = new BitSet[dfa.size()][maxItemCount];
		for(int i = 0;i < dfa.size();i++)
		{
			for(int j = 0;j < dfa.getItemSet(i).size();j++) lookahead[i][j] = new BitSet();
		}

		itemLASources = new BitSet[dfa.size()][maxItemCount];
		for(int i = 0;i < dfa.size();i++)
		{
			for(int j = 0;j < dfa.getItemSet(i).size();j++) itemLASources[i][j] = new BitSet();
		}
	}

	// Less efficient due to allocating more BitSets than necessary
	// used to create extension specific set w/o generating a partial dfa
	public LRLookaheadSets(int dfaSize, int maxItemCount) {
		this.maxItemCount = maxItemCount;

		lookahead = new BitSet[dfaSize][maxItemCount];
		for(int i = 0;i < dfaSize;i++)
		{
			for(int j = 0;j < maxItemCount;j++) lookahead[i][j] = new BitSet();
		}

		itemLASources = new BitSet[dfaSize][maxItemCount];
		for(int i = 0;i < dfaSize;i++)
		{
			for(int j = 0;j < maxItemCount;j++) itemLASources[i][j] = new BitSet();
		}
	}
	
	public int getMaxItemCount() { return maxItemCount; }

	public BitSet getLookahead(int state,int item) { return lookahead[state][item]; }
	public BitSet getItemLASources(int state,int item) { return itemLASources[state][item]; }
}
