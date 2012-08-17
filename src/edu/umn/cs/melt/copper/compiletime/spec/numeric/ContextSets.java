package edu.umn.cs.melt.copper.compiletime.spec.numeric;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;

/**
 * Holds a grammar's "context sets," {@code first}, {@code follow}, and {@code nullable}.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ContextSets
{
	protected BitSet[] first;
	protected BitSet[] follow;
	protected boolean[] nullable;
	
	public final BitSet getFirst(int s) { return first[s]; }
	public final BitSet getFollow(int s) { return follow[s]; }
	public final boolean isNullable(int s) { return nullable[s]; }
	public final void setNullable(int s,boolean n) { nullable[s] = n; }
	
	public ContextSets(int symbolCount)
	{
		first = new BitSet[symbolCount];
		follow = new BitSet[symbolCount];
		nullable = new boolean[symbolCount];
		
		for(int i = 0;i < symbolCount;i++)
		{
			first[i] = new BitSet();
			follow[i] = new BitSet();
		}
	}
	
	public String toString(SymbolTable<CopperASTBean> symbolTable)
	{
		StringBuffer rv = new StringBuffer();
		
		for(int i = 0;i < first.length;i++)
		{
			rv.append("first(").append(symbolTable.get(i).getDisplayName()).append(") = ").append(PSSymbolTable.bitSetPrettyPrint(first[i],symbolTable, "  ",80)).append("\n");
			rv.append("follow(" + symbolTable.get(i).getDisplayName() + ") = " + PSSymbolTable.bitSetPrettyPrint(follow[i],symbolTable, "  ",80)).append("\n");
			rv.append(symbolTable.get(i).getDisplayName()).append(" is");
			if(!nullable[i]) rv.append(" NOT");
			rv.append(" nullable\n\n");
		}
		
		return rv.toString();
	}
}
