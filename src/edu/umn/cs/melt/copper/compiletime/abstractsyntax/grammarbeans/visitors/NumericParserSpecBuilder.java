package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.DisambiguationFunctionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtensionGrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.OperatorAssociativity;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserAttributeBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ProductionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.OperatorClassBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalClassBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;

/**
 * This visitor builds a {@link edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec}
 * out of a spec represented by a ParserBean object.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class NumericParserSpecBuilder implements CopperASTBeanVisitor<Boolean, RuntimeException>
{
	private boolean metadataInitialized;
	private ParserBean currentParser;
	private GrammarBean currentGrammar;
	private PSSymbolTable symbolTable;
	private ParserSpec newSpec;
	
	public static ParserSpec build(ParserBean spec,PSSymbolTable symbolTable)
	{
		NumericParserSpecBuilder builder = new NumericParserSpecBuilder(symbolTable);
		spec.acceptVisitor(builder);
		return builder.newSpec;
	}

	public NumericParserSpecBuilder(PSSymbolTable symbolTable)
	{
		metadataInitialized = false;
		this.currentParser = null;
		this.currentGrammar = null;
		this.symbolTable = symbolTable;
	}

	@Override
	public Boolean visitDisambiguationFunctionBean(DisambiguationFunctionBean bean)
	throws RuntimeException
	{
		int beanId = symbolTable.get(bean);
		if(!metadataInitialized)
		{
			newSpec.disambiguationFunctions.set(beanId);
		}
		else
		{
			newSpec.owners[beanId] = symbolTable.get(currentGrammar);
			if(bean.getDisambiguateTo() != null) newSpec.df.setDisambiguateTo(beanId,dereference(bean.getDisambiguateTo()));
			else newSpec.df.setDisambiguateTo(beanId,-1);
			
			for(CopperElementReference ref : bean.getMembers()) newSpec.df.getMembers(beanId).set(dereference(ref));
		}
		return false;
	}

	@Override
	public Boolean visitGrammarBean(GrammarBean bean)
	throws RuntimeException
	{
		boolean hasError = false;
		int beanId = symbolTable.get(bean);
		currentGrammar = bean;
		if(!metadataInitialized)
		{
			newSpec.grammars.set(beanId);
		}
		else
		{
			if(bean.getGrammarLayout() != null)
			{
				for(CopperElementReference ref : bean.getGrammarLayout())
				{
					newSpec.g.getLayouts(beanId).set(dereference(ref));
				}
			}
		}
		for(CopperElementName n : bean.getGrammarElements())
		{
			hasError |= bean.getGrammarElement(n).acceptVisitor(this);
		}
		currentGrammar = null;
		return hasError;			
	}

	@Override
	public Boolean visitExtensionGrammarBean(ExtensionGrammarBean bean)
	throws RuntimeException
	{
		boolean hasError = false;
		int beanId = symbolTable.get(bean);
		currentGrammar = bean;
		if(!metadataInitialized)
		{
			newSpec.grammars.set(beanId);
		}
		else
		{
			if(bean.getGrammarLayout() != null)
			{
				for(CopperElementReference ref : bean.getGrammarLayout())
				{
					newSpec.g.getLayouts(beanId).set(dereference(ref));
				}
			}
		}
		hasError |= bean.getMarkingTerminal().acceptVisitor(this);
		hasError |= bean.getStartProduction().acceptVisitor(this);
		for(CopperElementName n : bean.getGrammarElements())
		{
			hasError |= bean.getGrammarElement(n).acceptVisitor(this);
		}
		currentGrammar = null;
		return hasError;			
	}

	@Override
	public Boolean visitNonTerminalBean(NonTerminalBean bean)
	throws RuntimeException
	{
		int beanId = symbolTable.get(bean);
		if(!metadataInitialized)
		{
			newSpec.nonterminals.set(beanId);
		}
		else
		{
			newSpec.owners[beanId] = symbolTable.get(currentGrammar);
		}
		return false;
	}

	@Override
	public Boolean visitParserAttributeBean(ParserAttributeBean bean)
	throws RuntimeException
	{
		int beanId = symbolTable.get(bean);
		if(!metadataInitialized)
		{
			newSpec.parserAttributes.set(beanId);
		}
		else
		{
			newSpec.owners[beanId] = symbolTable.get(currentGrammar);			
		}
		return false;
	}

	@Override
	public Boolean visitParserBean(ParserBean bean)
	throws RuntimeException
	{
		newSpec = new ParserSpec(symbolTable);
		int beanId = symbolTable.get(bean);
		
		newSpec.parser = beanId;
		int eofN = symbolTable.get(PlaceholderBean.EOF);
		newSpec.terminals.set(eofN);
		int startPrimeN = symbolTable.get(PlaceholderBean.STARTPRIME);
		newSpec.nonterminals.set(startPrimeN);
		int startProdN = symbolTable.get(PlaceholderBean.STARTPROD);
		newSpec.productions.set(startProdN);

		currentParser = bean;
		boolean hasError = false;
		for(CopperElementName n : bean.getGrammars())
		{
			hasError |= bean.getGrammar(n).acceptVisitor(this);
		}
		newSpec.initAttributes(symbolTable);
		metadataInitialized = true;
		
		newSpec.pr.setLHS(startProdN,startPrimeN);
		newSpec.pr.setRHSLength(startProdN,2);
		newSpec.nt.getProductions(startPrimeN).set(startProdN);
		newSpec.pr.setRHSSym(startProdN,0,dereference(bean.getStartSymbol()));
		newSpec.pr.setRHSSym(startProdN,1,eofN);
		
		for(CopperElementName n : bean.getGrammars())
		{
			hasError |= bean.getGrammar(n).acceptVisitor(this);
		}
		
		if(bean.getStartLayout() != null)
		{
			for(CopperElementReference layout : bean.getStartLayout())
			{
				newSpec.p.getLayout().set(dereference(layout));
			}
		}
		newSpec.pr.getLayouts(startProdN).or(newSpec.p.getLayout());

		currentParser = null;
		return hasError;
	}

	@Override
	public Boolean visitExtendedParserBean(ExtendedParserBean bean)
	throws RuntimeException
	{
		return visitParserBean(bean);
	}

	@Override
	public Boolean visitProductionBean(ProductionBean bean)
	throws RuntimeException
	{
		int beanId = symbolTable.get(bean);
		if(!metadataInitialized)
		{
			newSpec.productions.set(beanId);
		}
		else
		{
			newSpec.owners[beanId] = symbolTable.get(currentGrammar);

			int lhs = dereference(bean.getLhs());
			newSpec.nt.getProductions(lhs).set(beanId);
			newSpec.pr.setLHS(beanId,lhs);
			newSpec.pr.setRHSLength(beanId,bean.getRhs().size());
			if(bean.getOperator() != null) newSpec.pr.setOperator(beanId,dereference(bean.getOperator()));
			
			int lastTerminal = -1;

			for(int i = 0;i < bean.getRhs().size();i++)
			{
				int symI = dereference(bean.getRhs().get(i));
				newSpec.pr.setRHSSym(beanId,i,symI);
				if(bean.getOperator() == null && newSpec.terminals.get(symI)) lastTerminal = symI;
			}
			
			if(bean.getOperator() == null) newSpec.pr.setOperator(beanId,lastTerminal);

			newSpec.pr.setPrecedence(beanId, bean.getPrecedence() == null ? -1 : bean.getPrecedence());
			newSpec.pr.setPrecedenceClass(beanId, bean.getPrecedenceClass() == null ? -1 : dereference(bean.getPrecedenceClass()));

			if(bean.getLayout() != null)
			{
				newSpec.pr.setHasLayout(beanId,true);
				for(CopperElementReference layout : bean.getLayout())
				{
					newSpec.pr.getLayouts(beanId).set(dereference(layout));
				}
			}
			else
			{
				newSpec.pr.setHasLayout(beanId,false);
				if(currentGrammar.getGrammarLayout() != null)
				{
					for(CopperElementReference layout : currentGrammar.getGrammarLayout())
					{
						newSpec.pr.getLayouts(beanId).set(dereference(layout));
					}
				}
			}
		}
		return false;
	}

	@Override
	public Boolean visitTerminalBean(TerminalBean bean)
	throws RuntimeException
	{
		int beanId = symbolTable.get(bean);
		if(!metadataInitialized)
		{
			newSpec.terminals.set(beanId);
		}
		else
		{
			newSpec.owners[beanId] = symbolTable.get(currentGrammar);

			newSpec.t.setRegex(beanId,bean.getRegex());
			for(CopperElementReference tc : bean.getTerminalClasses())
			{
				newSpec.t.getTerminalClasses(beanId).set(dereference(tc));
			}
			if(bean.getPrefix() == null) newSpec.t.setTransparentPrefix(beanId,-1);
			else newSpec.t.setTransparentPrefix(beanId,dereference(bean.getPrefix()));
			if(bean.getOperatorClass() == null) newSpec.t.setOperatorClass(beanId,-1);
			else newSpec.t.setOperatorClass(beanId,dereference(bean.getOperatorClass()));
			if(bean.getOperatorPrecedence() == null) newSpec.t.setOperatorPrecedence(beanId,-1);
			else newSpec.t.setOperatorPrecedence(beanId,bean.getOperatorPrecedence());
			if(bean.getOperatorAssociativity() == null) newSpec.t.setOperatorAssociativity(beanId,OperatorAssociativity.NONE);
			else newSpec.t.setOperatorAssociativity(beanId,bean.getOperatorAssociativity());
			
			for(CopperElementReference ref : bean.getSubmitList())
			{
				newSpec.t.precedences.addEdge(beanId,dereference(ref));
			}
			for(CopperElementReference ref : bean.getDominateList())
			{
				newSpec.t.precedences.addEdge(dereference(ref),beanId);
			}
		}
		return false;
	}

	@Override
	public Boolean visitTerminalClassBean(TerminalClassBean bean)
	throws RuntimeException
	{
		int beanId = symbolTable.get(bean);
		if(!metadataInitialized)
		{
			newSpec.terminalClasses.set(beanId);
		}
		else
		{
			newSpec.owners[beanId] = symbolTable.get(currentGrammar);

			for(CopperElementReference ref : bean.getMembers()) newSpec.tc.getMembers(beanId).set(dereference(ref));			
		}
		return false;
	}

	@Override
	public Boolean visitOperatorClassBean(OperatorClassBean bean)
	throws RuntimeException
	{
		int beanId = symbolTable.get(bean);
		if(!metadataInitialized)
		{
			newSpec.operatorClasses.set(beanId);
		}
		else
		{
			newSpec.owners[beanId] = symbolTable.get(currentGrammar);			
		}
		return false;
	}
	
	// Assumes that nameIsDefined(symbol) == true,
	// and that all referenced symbols are defined in the symbol table.
	private int dereference(CopperElementReference symbol)
	{
		if(!symbol.isFQ()) return symbolTable.get(currentGrammar.getGrammarElement(symbol.getName()));
		else return symbolTable.get(currentParser.getGrammar(symbol.getGrammarName()).getGrammarElement(symbol.getName()));
	}
}
