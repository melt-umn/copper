package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.DisambiguationFunctionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtensionGrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserAttributeBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ProductionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.OperatorClassBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalClassBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.PSSymbolTable;

/**
 * This visitor builds a {@link edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.PSSymbolTable}
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
	public PSSymbolTable visitDisambiguationFunctionBean(DisambiguationFunctionBean bean)
	throws RuntimeException
	{
		disambiguationFunctions.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitGrammarBean(GrammarBean bean)
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
	public PSSymbolTable visitExtensionGrammarBean(ExtensionGrammarBean bean)
	throws RuntimeException
	{
		visitGrammarBean(bean);
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
	public PSSymbolTable visitNonTerminalBean(NonTerminalBean bean)
	throws RuntimeException
	{
		nonterminals.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitParserAttributeBean(ParserAttributeBean bean)
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
	public PSSymbolTable visitProductionBean(ProductionBean bean)
	throws RuntimeException
	{
		productions.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitTerminalBean(TerminalBean bean)
	throws RuntimeException
	{
		terminals.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitTerminalClassBean(TerminalClassBean bean)
	throws RuntimeException
	{
		terminalClasses.add(bean);
		return null;
	}

	@Override
	public PSSymbolTable visitOperatorClassBean(OperatorClassBean bean)
	throws RuntimeException
	{
		operatorClasses.add(bean);
		return null;
	}

}
