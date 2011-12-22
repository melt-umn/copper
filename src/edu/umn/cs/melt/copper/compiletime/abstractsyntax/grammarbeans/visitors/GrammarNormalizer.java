package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors;

import java.util.HashSet;
import java.util.Iterator;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementType;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.DisambiguationFunctionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtensionGrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarElementBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserAttributeBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ProductionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalClassBean;

/**
 * This visitor "normalizes" a parser or grammar by performing the following actions:
 * 1. Adds to the members list of each terminal class all terminals that have declared themselves in that class.
 * 2. Adds to all terminals in the members list of each terminal class a declaration of membership in that class.
 * 3. Replaces all references to terminal classes in submit lists, dominate lists, and disambiguation functions
 *    with references to the individual terminals comprising the class.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
class GrammarNormalizer implements CopperASTBeanVisitor<Boolean,RuntimeException>
{
	private TerminalClassFiller filler;
	private PrecedenceListNormalizer normalizer;
	private ParserBean currentParser;
	private GrammarBean currentGrammar;

	public GrammarNormalizer()
	{
		filler = new TerminalClassFiller();
		normalizer = new PrecedenceListNormalizer();
	}
	
	private class TerminalClassFiller implements CopperASTBeanVisitor<Boolean,RuntimeException>
	{
		@Override
		public Boolean visitDisambiguationFunctionBean(DisambiguationFunctionBean bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public Boolean visitGrammarBean(GrammarBean bean)
		throws RuntimeException
		{
			currentGrammar = bean;
			boolean hasError = false;
			for(CopperElementName n : bean.getGrammarElements())
			{
				hasError |= bean.getGrammarElement(n).acceptVisitor(this);
			}
			return hasError;			
		}

		@Override
		public Boolean visitExtensionGrammarBean(ExtensionGrammarBean bean)
		throws RuntimeException
		{
			currentGrammar = bean;
			boolean hasError = false;
			for(CopperElementName n : bean.getGrammarElements())
			{
				hasError |= bean.getGrammarElement(n).acceptVisitor(this);
			}
			hasError |= bean.getMarkingTerminal().acceptVisitor(this);
			return hasError;			
		}

		@Override
		public Boolean visitNonTerminalBean(NonTerminalBean bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public Boolean visitParserAttributeBean(ParserAttributeBean bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public Boolean visitParserBean(ParserBean bean)
		throws RuntimeException
		{
			currentParser = bean;
			boolean hasError = false;
			for(CopperElementName n : bean.getGrammars())
			{
				hasError |= bean.getGrammar(n).acceptVisitor(this);
			}
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
			return false;
		}

		@Override
		public Boolean visitTerminalBean(TerminalBean bean)
		throws RuntimeException
		{
			for(CopperElementReference n : bean.getTerminalClasses())
			{
				TerminalClassBean terminalClass = (TerminalClassBean) dereference(n);
				if(!n.isFQ()) terminalClass.addMember(CopperElementReference.ref(bean.getName(),bean.getLocation()));
				else terminalClass.addMember(CopperElementReference.ref(currentGrammar.getName(),bean.getName(),bean.getLocation()));
			}
			return false;
		}

		@Override
		public Boolean visitTerminalClassBean(TerminalClassBean bean)
		throws RuntimeException
		{
			for(CopperElementReference n : bean.getMembers())
			{
				TerminalBean terminal = (TerminalBean) dereference(n);
				if(!n.isFQ()) terminal.addTerminalClass(CopperElementReference.ref(bean.getName(),bean.getLocation()));
				else terminal.addTerminalClass(CopperElementReference.ref(currentGrammar.getName(),bean.getName(),bean.getLocation()));
			}
			return false;
		}
	}
	
	private class PrecedenceListNormalizer implements CopperASTBeanVisitor<Boolean,RuntimeException>
	{
		@Override
		public Boolean visitDisambiguationFunctionBean(DisambiguationFunctionBean bean)
		throws RuntimeException
		{
			HashSet<CopperElementReference> newMembers = new HashSet<CopperElementReference>();
			for(Iterator<CopperElementReference> it = bean.getMembers().iterator();it.hasNext();)
			{
				CopperElementReference n = it.next();
				GrammarElementBean e = dereference(n);
				if(e.getType() == CopperElementType.TERMINAL_CLASS)
				{
					it.remove();
					newMembers.addAll(((TerminalClassBean) e).getMembers());
				}
			}
			bean.getMembers().addAll(newMembers);
			return false;
		}

		@Override
		public Boolean visitGrammarBean(GrammarBean bean)
		throws RuntimeException
		{
			currentGrammar = bean;
			boolean hasError = false;
			for(CopperElementName n : bean.getGrammarElements())
			{
				hasError |= bean.getGrammarElement(n).acceptVisitor(this);
			}
			return hasError;			
		}

		@Override
		public Boolean visitExtensionGrammarBean(ExtensionGrammarBean bean)
		throws RuntimeException
		{
			currentGrammar = bean;
			boolean hasError = false;
			for(CopperElementName n : bean.getGrammarElements())
			{
				hasError |= bean.getGrammarElement(n).acceptVisitor(this);
			}
			hasError |= bean.getMarkingTerminal().acceptVisitor(this);
			return hasError;			
		}

		@Override
		public Boolean visitNonTerminalBean(NonTerminalBean bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public Boolean visitParserAttributeBean(ParserAttributeBean bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public Boolean visitParserBean(ParserBean bean)
		throws RuntimeException
		{
			currentParser = bean;
			boolean hasError = false;
			for(CopperElementName n : bean.getGrammars())
			{
				hasError |= bean.getGrammar(n).acceptVisitor(this);
			}
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
			return false;
		}

		@Override
		public Boolean visitTerminalBean(TerminalBean bean)
		throws RuntimeException
		{
			HashSet<CopperElementReference> newMembers = new HashSet<CopperElementReference>();
			for(Iterator<CopperElementReference> it = bean.getSubmitList().iterator();it.hasNext();)
			{
				CopperElementReference n = it.next();
				GrammarElementBean e = dereference(n);
				if(e.getType() == CopperElementType.TERMINAL_CLASS)
				{
					it.remove();
					newMembers.addAll(((TerminalClassBean) e).getMembers());
				}
			}
			bean.getSubmitList().addAll(newMembers);
			newMembers.clear();
			for(Iterator<CopperElementReference> it = bean.getDominateList().iterator();it.hasNext();)
			{
				CopperElementReference n = it.next();
				GrammarElementBean e = dereference(n);
				if(e.getType() == CopperElementType.TERMINAL_CLASS)
				{
					it.remove();
					newMembers.addAll(((TerminalClassBean) e).getMembers());
				}
			}
			bean.getDominateList().addAll(newMembers);
			return false;
		}

		@Override
		public Boolean visitTerminalClassBean(TerminalClassBean bean)
		throws RuntimeException
		{
			return false;
		}
		
	}

	@Override
	public Boolean visitDisambiguationFunctionBean(DisambiguationFunctionBean bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitGrammarBean(GrammarBean bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitExtensionGrammarBean(ExtensionGrammarBean bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitNonTerminalBean(NonTerminalBean bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitParserAttributeBean(ParserAttributeBean bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitParserBean(ParserBean bean)
	throws RuntimeException
	{
		return bean.acceptVisitor(filler) || bean.acceptVisitor(normalizer);
	}

	@Override
	public Boolean visitExtendedParserBean(ExtendedParserBean bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitProductionBean(ProductionBean bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitTerminalBean(TerminalBean bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitTerminalClassBean(TerminalClassBean bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}


	// Assumes that nameIsDefined(symbol) == true.
	private GrammarElementBean dereference(CopperElementReference symbol)
	{
		if(!symbol.isFQ()) return currentGrammar.getGrammarElement(symbol.getName());
		else return currentParser.getGrammar(symbol.getGrammarName()).getGrammarElement(symbol.getName());
	}
}
