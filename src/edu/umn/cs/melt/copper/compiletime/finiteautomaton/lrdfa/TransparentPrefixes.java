package edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTable;

/**
 * Holds maps of valid transparent prefixes for each state in a parse table. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class TransparentPrefixes
{
	protected BitSet[] prefixSets;
	protected BitSet[][] prefixMaps;
	
	public TransparentPrefixes(ParserSpec spec,LRParseTable parseTable)
	{
		prefixSets = new BitSet[parseTable.size()];
		prefixMaps = new BitSet[parseTable.size()][spec.terminals.length()];
		for(int i = 0;i < parseTable.size();i++) prefixSets[i] = new BitSet();
	}
	
	public BitSet getPrefixes(int state) { return prefixSets[state]; }
	public BitSet getFollowingTerminals(int state,int prefix) { return prefixMaps[state][prefix]; }
	public void initializePrefixMap(int state,int prefix) { if(prefixMaps[state][prefix] == null) prefixMaps[state][prefix] = new BitSet(); }
	
	public String toString(SymbolTable<CopperASTBean> symbolTable)
	{
		StringBuffer rv = new StringBuffer();
		
		for(int state = 0;state < prefixSets.length;state++)
		{
			rv.append("State ").append(state).append(":\n");
			for(int prefix = prefixSets[state].nextSetBit(0);prefix >= 0;prefix = prefixSets[state].nextSetBit(prefix+1))
			{
				rv.append("  ").append(symbolTable.get(prefix).getDisplayName()).append(" -> ").append(PSSymbolTable.bitSetPrettyPrint(prefixMaps[state][prefix],symbolTable,"    ",80)).append("\n");
			}
		}
		
		return rv.toString();
	}
}
