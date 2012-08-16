package edu.umn.cs.melt.copper.compiletime.builders;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;

/**
 * Builds maps of valid transparent prefixes for a parse table. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class TransparentPrefixSetBuilder
{
	private ParserSpec spec;
	private LRParseTable parseTable;
	
	private TransparentPrefixSetBuilder(ParserSpec spec,LRParseTable parseTable)
	{
		this.spec = spec;
		this.parseTable = parseTable;
	}

	public static TransparentPrefixes build(ParserSpec spec,LRParseTable parseTable)
	{
		return new TransparentPrefixSetBuilder(spec,parseTable).buildPrefixSets();
	}
	
	private TransparentPrefixes buildPrefixSets()
	{
		TransparentPrefixes prefixes = new TransparentPrefixes(spec,parseTable);
		
		for(int state = 0;state < parseTable.size();state++)
		{
			for(int t = parseTable.getValidLA(state).nextSetBit(0);t >= 0;t = parseTable.getValidLA(state).nextSetBit(t+1))
			{
				if(!spec.terminals.get(t) || t == spec.getEOFTerminal()) continue;
				int prefix = spec.t.getTransparentPrefix(t); 
				if(prefix != -1)
				{
					prefixes.getPrefixes(state).set(prefix);
					prefixes.initializePrefixMap(state,prefix);
					prefixes.getFollowingTerminals(state,prefix).set(t);
				}
			}
		}
		
		return prefixes;
	}
	
}
