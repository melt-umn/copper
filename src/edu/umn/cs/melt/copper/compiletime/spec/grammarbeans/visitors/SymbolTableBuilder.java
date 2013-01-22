package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.DisambiguationFunction;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ExtensionGrammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Grammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.OperatorClass;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Production;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Terminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.TerminalClass;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;

/**
 * This visitor builds a {@link edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable}
 * out of a spec represented by a ParserBean object.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class SymbolTableBuilder implements CopperASTBeanVisitor<PSSymbolTable, RuntimeException>
{
	private SortedSet<CopperASTBean> terminals;
	private SortedSet<CopperASTBean> nonterminals;
	private SortedSet<CopperASTBean> productions;
	private SortedSet<CopperASTBean> disambiguationFunctions;
	private SortedSet<CopperASTBean> terminalClasses;
	private SortedSet<CopperASTBean> operatorClasses;
	private SortedSet<CopperASTBean> parserAttributes;
	private SortedSet<CopperASTBean> grammars;
	
	public static PSSymbolTable build(ParserBean spec)
	{
		return spec.acceptVisitor(new SymbolTableBuilder());
	}

	private SymbolTableBuilder()
	{
	}
	
	@Override
	public PSSymbolTable visitDisambiguationFunction(DisambiguationFunction bean)
	throws RuntimeException
	{
		disambiguationFunctions.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitGrammar(Grammar bean)
	throws RuntimeException
	{
		for(CopperElementName n : bean.getGrammarElements())
		{
			bean.getGrammarElement(n).acceptVisitor(this);
		}
		grammars.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitExtensionGrammar(ExtensionGrammar bean)
	throws RuntimeException
	{
		visitGrammar(bean);
		for(CopperElementName n : bean.getMarkingTerminals())
		{
			bean.getMarkingTerminal(n).acceptVisitor(this);
		}
		for(CopperElementName n : bean.getBridgeProductions())
		{
			bean.getBridgeProduction(n).acceptVisitor(this);
		}
		return null;
	}

	@Override
	public PSSymbolTable visitNonTerminal(NonTerminal bean)
	throws RuntimeException
	{
		nonterminals.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitParserAttribute(ParserAttribute bean)
	throws RuntimeException
	{
		parserAttributes.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitParserBean(ParserBean bean)
	throws RuntimeException
	{
		terminals = new TreeSet<CopperASTBean>(CopperASTBeanComparator.C);
		nonterminals = new TreeSet<CopperASTBean>(CopperASTBeanComparator.C);
		productions = new TreeSet<CopperASTBean>(CopperASTBeanComparator.C);
		disambiguationFunctions = new TreeSet<CopperASTBean>(CopperASTBeanComparator.C);
		terminalClasses = new TreeSet<CopperASTBean>(CopperASTBeanComparator.C);
		operatorClasses = new TreeSet<CopperASTBean>(CopperASTBeanComparator.C);
		parserAttributes = new TreeSet<CopperASTBean>(CopperASTBeanComparator.C);
		grammars = new TreeSet<CopperASTBean>(CopperASTBeanComparator.C);
		for(CopperElementName n : bean.getGrammars())
		{
			bean.getGrammar(n).acceptVisitor(this);
		}
		ArrayList<CopperASTBean> symbolList = new ArrayList<CopperASTBean>();
		
		
		symbolList.add(PlaceholderBean.EOF);
		symbolList.addAll(terminals);
		symbolList.add(PlaceholderBean.STARTPRIME);
		symbolList.addAll(nonterminals);
		symbolList.add(PlaceholderBean.STARTPROD);
		symbolList.addAll(productions);
		symbolList.addAll(disambiguationFunctions);
		symbolList.addAll(terminalClasses);
		symbolList.addAll(operatorClasses);
		symbolList.addAll(parserAttributes);
		symbolList.addAll(grammars);
		symbolList.add(bean);
		return new PSSymbolTable(symbolList);
	}

	@Override
	public PSSymbolTable visitExtendedParserBean(ExtendedParserBean bean)
	throws RuntimeException
	{
		return visitParserBean(bean);
	}

	@Override
	public PSSymbolTable visitProduction(Production bean)
	throws RuntimeException
	{
		productions.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitTerminal(Terminal bean)
	throws RuntimeException
	{
		terminals.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitTerminalClass(TerminalClass bean)
	throws RuntimeException
	{
		terminalClasses.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitOperatorClass(OperatorClass bean)
	throws RuntimeException
	{
		operatorClasses.add(bean);
		return null;
	}

}
