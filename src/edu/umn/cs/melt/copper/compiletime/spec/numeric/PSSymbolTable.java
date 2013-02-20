package edu.umn.cs.melt.copper.compiletime.spec.numeric;

import java.util.BitSet;
import java.util.Collection;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.DisambiguationFunction;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Grammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.OperatorClass;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Production;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Terminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.TerminalClass;

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
	
	public final Terminal getTerminal(int i) { return (Terminal) get(i); }
	public final NonTerminal getNonTerminal(int i) { return (NonTerminal) get(i); }
	public final Production getProduction(int i) { return (Production) get(i); }
	public final DisambiguationFunction getDisambiguationFunction(int i) { return (DisambiguationFunction) get(i); }
	public final TerminalClass getTerminalClass(int i) { return (TerminalClass) get(i); }
	public final OperatorClass getOperatorClass(int i) { return (OperatorClass) get(i); }
	public final ParserAttribute getParserAttribute(int i) { return (ParserAttribute) get(i); }
	public final Grammar getGrammar(int i) { return (Grammar) get(i); }
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
