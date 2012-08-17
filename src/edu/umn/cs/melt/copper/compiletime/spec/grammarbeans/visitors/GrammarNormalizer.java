package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors;

import java.util.HashSet;
import java.util.Iterator;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementType;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.DisambiguationFunction;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ExtensionGrammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Grammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.GrammarElement;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.OperatorClass;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Production;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Terminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.TerminalClass;

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
	private Grammar currentGrammar;

	GrammarNormalizer()
	{
		filler = new TerminalClassFiller();
		normalizer = new PrecedenceListNormalizer();
	}
	
	private class TerminalClassFiller implements CopperASTBeanVisitor<Boolean,RuntimeException>
	{
		@Override
		public Boolean visitDisambiguationFunctionBean(DisambiguationFunction bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public Boolean visitGrammarBean(Grammar bean)
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
		public Boolean visitExtensionGrammarBean(ExtensionGrammar bean)
		throws RuntimeException
		{
			currentGrammar = bean;
			boolean hasError = false;
			for(CopperElementName n : bean.getGrammarElements())
			{
				hasError |= bean.getGrammarElement(n).acceptVisitor(this);
			}
			for(CopperElementName n : bean.getMarkingTerminals())
			{
				hasError |= bean.getMarkingTerminal(n).acceptVisitor(this);
			}
			for(CopperElementName n : bean.getBridgeProductions())
			{
				hasError |= bean.getBridgeProduction(n).acceptVisitor(this);
			}
			return hasError;			
		}

		@Override
		public Boolean visitNonTerminalBean(NonTerminal bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public Boolean visitParserAttributeBean(ParserAttribute bean)
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
		public Boolean visitProductionBean(Production bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public Boolean visitTerminalBean(Terminal bean)
		throws RuntimeException
		{
			for(CopperElementReference n : bean.getTerminalClasses())
			{
				TerminalClass terminalClass = (TerminalClass) dereference(n);
				if(!n.isFQ()) terminalClass.addMember(CopperElementReference.ref(bean.getName(),bean.getLocation()));
				else terminalClass.addMember(CopperElementReference.ref(currentGrammar.getName(),bean.getName(),bean.getLocation()));
			}
			return false;
		}

		@Override
		public Boolean visitTerminalClassBean(TerminalClass bean)
		throws RuntimeException
		{
			for(CopperElementReference n : bean.getMembers())
			{
				Terminal terminal = (Terminal) dereference(n);
				if(!n.isFQ()) terminal.addTerminalClass(CopperElementReference.ref(bean.getName(),bean.getLocation()));
				else terminal.addTerminalClass(CopperElementReference.ref(currentGrammar.getName(),bean.getName(),bean.getLocation()));
			}
			return false;
		}

		@Override
		public Boolean visitOperatorClassBean(OperatorClass bean)
		throws RuntimeException
		{
			return false;
		}
	}
	
	private class PrecedenceListNormalizer implements CopperASTBeanVisitor<Boolean,RuntimeException>
	{
		@Override
		public Boolean visitDisambiguationFunctionBean(DisambiguationFunction bean)
		throws RuntimeException
		{
			HashSet<CopperElementReference> newMembers = new HashSet<CopperElementReference>();
			for(Iterator<CopperElementReference> it = bean.getMembers().iterator();it.hasNext();)
			{
				CopperElementReference n = it.next();
				GrammarElement e = dereference(n);
				if(e.getType() == CopperElementType.TERMINAL_CLASS)
				{
					it.remove();
					newMembers.addAll(((TerminalClass) e).getMembers());
				}
			}
			bean.getMembers().addAll(newMembers);
			return false;
		}

		@Override
		public Boolean visitGrammarBean(Grammar bean)
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
		public Boolean visitExtensionGrammarBean(ExtensionGrammar bean)
		throws RuntimeException
		{
			currentGrammar = bean;
			boolean hasError = false;
			for(CopperElementName n : bean.getGrammarElements())
			{
				hasError |= bean.getGrammarElement(n).acceptVisitor(this);
			}
			for(CopperElementName n : bean.getMarkingTerminals())
			{
				hasError |= bean.getMarkingTerminal(n).acceptVisitor(this);
			}
			for(CopperElementName n : bean.getBridgeProductions())
			{
				hasError |= bean.getBridgeProduction(n).acceptVisitor(this);
			}
			return hasError;			
		}

		@Override
		public Boolean visitNonTerminalBean(NonTerminal bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public Boolean visitParserAttributeBean(ParserAttribute bean)
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
		public Boolean visitProductionBean(Production bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public Boolean visitTerminalBean(Terminal bean)
		throws RuntimeException
		{
			HashSet<CopperElementReference> newMembers = new HashSet<CopperElementReference>();
			for(Iterator<CopperElementReference> it = bean.getSubmitList().iterator();it.hasNext();)
			{
				CopperElementReference n = it.next();
				GrammarElement e = dereference(n);
				if(e.getType() == CopperElementType.TERMINAL_CLASS)
				{
					it.remove();
					newMembers.addAll(((TerminalClass) e).getMembers());
				}
			}
			bean.getSubmitList().addAll(newMembers);
			newMembers.clear();
			for(Iterator<CopperElementReference> it = bean.getDominateList().iterator();it.hasNext();)
			{
				CopperElementReference n = it.next();
				GrammarElement e = dereference(n);
				if(e.getType() == CopperElementType.TERMINAL_CLASS)
				{
					it.remove();
					newMembers.addAll(((TerminalClass) e).getMembers());
				}
			}
			bean.getDominateList().addAll(newMembers);
			return false;
		}

		@Override
		public Boolean visitTerminalClassBean(TerminalClass bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public Boolean visitOperatorClassBean(OperatorClass bean)
		throws RuntimeException
		{
			return false;
		}
		
	}

	@Override
	public Boolean visitDisambiguationFunctionBean(DisambiguationFunction bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitGrammarBean(Grammar bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitExtensionGrammarBean(ExtensionGrammar bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitNonTerminalBean(NonTerminal bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitParserAttributeBean(ParserAttribute bean)
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
		return visitParserBean(bean);
	}

	@Override
	public Boolean visitProductionBean(Production bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitTerminalBean(Terminal bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitTerminalClassBean(TerminalClass bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitOperatorClassBean(OperatorClass bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	// Assumes that nameIsDefined(symbol) == true.
	private GrammarElement dereference(CopperElementReference symbol)
	{
		if(!symbol.isFQ()) return currentGrammar.getGrammarElement(symbol.getName());
		else return currentParser.getGrammar(symbol.getGrammarName()).getGrammarElement(symbol.getName());
	}
}
