package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew;

import java.util.BitSet;
import java.util.Collection;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.DisambiguationFunctionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.OperatorClassBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserAttributeBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ProductionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalClassBean;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;

/**
 * Extends {@link SymbolTable} with methods specifically related to {@link CopperASTBean}-derived objects.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class PSSymbolTable extends SymbolTable<CopperASTBean>
{
	public PSSymbolTable(Collection<CopperASTBean> objects)
	{
		super(objects);
	}
	
	public final TerminalBean getTerminal(int i) { return (TerminalBean) get(i); }
	public final NonTerminalBean getNonTerminal(int i) { return (NonTerminalBean) get(i); }
	public final ProductionBean getProduction(int i) { return (ProductionBean) get(i); }
	public final DisambiguationFunctionBean getDisambiguationFunction(int i) { return (DisambiguationFunctionBean) get(i); }
	public final TerminalClassBean getTerminalClass(int i) { return (TerminalClassBean) get(i); }
	public final OperatorClassBean getOperatorClass(int i) { return (OperatorClassBean) get(i); }
	public final ParserAttributeBean getParserAttribute(int i) { return (ParserAttributeBean) get(i); }
	public final GrammarBean getGrammar(int i) { return (GrammarBean) get(i); }
	public final ParserBean getParser(int i) { return (ParserBean) get(i); }
	
	/**
	 * Produces a string representation of a bit set given a string representation of each bit.
	 * @param coll The bit set.
	 * @param nameMap The string representation of each bit. Set to {@code null} to print the numbers themselves.
	 * @param linePrefix A string with which to prefix each line in the output (e.g., indentation).
	 * @param itemsPerLine The number of objects in the bit set to place on each line of the output.
	 */
	public static <T> String bitSetPrettyPrint(BitSet coll,SymbolTable<T> nameMap,String linePrefix,int maxWidth)
	{
		String rv = linePrefix + "[";
		boolean first = true;
		int currentLineCharCount = 0;
		for(int i = coll.nextSetBit(0);i >= 0;i = coll.nextSetBit(i + 1))
		{
			String s;
			if(nameMap != null)
			{
				T o = nameMap.get(i);
				if(o instanceof CopperASTBean) s = ((CopperASTBean) o).getDisplayName();
				else s = o.toString();
			}
			else s = String.valueOf(i);

			if(currentLineCharCount + s.length() + 1 >= maxWidth && currentLineCharCount > linePrefix.length())
			{
				rv += ",\n" + linePrefix;
				currentLineCharCount = linePrefix.length();
			}
			else if(!first)
			{
				rv += ",";
				currentLineCharCount++;
			}
			currentLineCharCount += s.length();
			first = false;
			rv += s;
		}
		rv += /*"\n" + linePrefix +*/ "]";
		return rv;
	}
	
	public String toString()
	{
		StringBuffer rv = new StringBuffer();
		for(int i = 0;i < size();i++)
		{
			rv.append("[").append(i).append("] ").append(get(i).getType()).append(" : ").append(get(i).getDisplayName()).append("\n");
		}
		return rv.toString();
	}
}
