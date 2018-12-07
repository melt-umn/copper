package edu.umn.cs.melt.copper.compiletime.lrdfa;

import java.io.Serializable;
import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

/**
 * Holds maps of valid transparent prefixes for each state in a parse table. 
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 * @author Kevin Viratyosin
 *
 * Modified to allowing construction without a spec or parsetable, serialization
 */
public class TransparentPrefixes implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 253855265720270297L;
	protected BitSet[] prefixSets;
	protected BitSet[][] prefixMaps;

	// Modified by Kevin Viratyosin
	public TransparentPrefixes(ParserSpec spec,LRParseTable parseTable)
	{
		this(spec.terminals.length(), parseTable.size());
	}

	// Added by Kevin Viratyosin
	public TransparentPrefixes(int terminalsLength, int parseTableSize) {
		prefixSets = new BitSet[parseTableSize];
		prefixMaps = new BitSet[parseTableSize][terminalsLength];
		for(int i = 0; i < parseTableSize; i++) {
			prefixSets[i] = new BitSet();
		}
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
