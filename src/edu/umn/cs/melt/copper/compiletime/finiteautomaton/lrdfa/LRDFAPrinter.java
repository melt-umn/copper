package edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;

/**
 * Contains methods to convert LR DFAs to strings.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class LRDFAPrinter
{
	public static String toString(SymbolTable<CopperASTBean> symbolTable,ParserSpec spec,LR0DFA dfa)
	{
		return toString(symbolTable,spec,dfa,null);
	}
	
	public static String toString(SymbolTable<CopperASTBean> symbolTable,ParserSpec spec,LR0DFA dfa,LRLookaheadSets lookaheadSets)
	{
		StringBuffer rv = new StringBuffer();
		for(int i = 0;i < dfa.size();i++)
		{
			rv.append("State ").append(i).append(":\n");
			for(int j = 0;j < dfa.getItemSet(i).size();j++)
			{
				rv.append("    ");
				rv.append(symbolTable.get(spec.pr.getLHS(dfa.getItemSet(i).getProduction(j))).getDisplayName());
				rv.append(" ::=");
				for(int k = 0;k < spec.pr.getRHSLength(dfa.getItemSet(i).getProduction(j));k++)
				{
					if(k == dfa.getItemSet(i).getPosition(j)) rv.append(" (*)");
					rv.append(" ").append(symbolTable.get(spec.pr.getRHSSym(dfa.getItemSet(i).getProduction(j),k)).getDisplayName());
				}
				if(dfa.getItemSet(i).getPosition(j) == spec.pr.getRHSLength(dfa.getItemSet(i).getProduction(j))) rv.append(" (*)");
				if(lookaheadSets != null)
				{
					rv.append("   [");
					boolean first = true;
					for(int k = lookaheadSets.getLookahead(i,j).nextSetBit(0);k >= 0;k = lookaheadSets.getLookahead(i,j).nextSetBit(k+1))
					{
						if(first) first = false;
						else rv.append(",");
						rv.append(symbolTable.get(k).getDisplayName());
					}
					rv.append("]");
				}
				rv.append("\n");
			}
			for(int j = dfa.getTransitionLabels(i).nextSetBit(0);j >= 0;j = dfa.getTransitionLabels(i).nextSetBit(j+1))
			{
				rv.append("  Transition on symbol ").append(symbolTable.get(j).getDisplayName()).append(" to state ").append(dfa.getTransition(i,j)).append("\n");
			}
		}
		return rv.toString();
	}
}
