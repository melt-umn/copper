package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CharacterSetRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ChoiceRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ConcatenationRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementType;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.DisambiguationFunction;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.EmptyStringRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ExtensionGrammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Grammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.GrammarElement;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.KleeneStarRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.MacroHoleRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.OperatorClass;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Production;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Regex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Terminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.TerminalClass;
import edu.umn.cs.melt.copper.runtime.io.Location;

/**
 * This visitor performs the following checks on a parser or grammar:
 * 1. That all required fields in each of its elements have been defined.
 * 2. That all references in the parser or grammar are defined.
 * 3. That all references are to elements of the correct type (e.g., nonterminals on the
 *    left-hand side of a production, terminals or terminal classes in a submit list).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
class GrammarConsistencyChecker implements CopperASTBeanVisitor<Boolean, RuntimeException>, RegexBeanVisitor<Boolean, RuntimeException>
{
	/**
	 * Errors logged by the visitor.
	 */
	protected SortedSet<GrammarError> errors;
	
	GrammarConsistencyChecker()
	{
		errors = new TreeSet<GrammarError>();
		currentParser = null;
		currentGrammar = null;
	}

	/**
	 * @see GrammarConsistencyChecker#errors
	 */
	public SortedSet<GrammarError> getErrors()
	{
		return errors;
	}
	
	private ParserBean currentParser;
	private Grammar currentGrammar;
	private Terminal currentTerminal;
	private boolean isBridgeProduction;
	
	@Override
	public Boolean visitDisambiguationFunction(DisambiguationFunction bean)
	{
		boolean hasError = false;
		// Check that none of the disambiguation function's required elements are missing.
		if(!bean.isComplete())
		{
			Set<String> whatIsMissing = bean.whatIsMissing();
			errors.add(new GrammarError(bean.getLocation(),currentParser,null,"Disambiguation function " + bean.getDisplayName() + " is missing the following required attributes: " + whatIsMissing));
			hasError = true;
		}
		else
		{
			for(CopperElementReference n : bean.getMembers())
			{
				// Check that all the names in the disambiguation function's member set are defined,
				boolean isDefined = nameIsDefined(n) && nameIsHost(n);
				hasError |= !isDefined;
				// and refer to terminals or terminal classes.
				if(isDefined &&
				   dereference(n).getType() != CopperElementType.TERMINAL &&
				   dereference(n).getType() != CopperElementType.TERMINAL_CLASS)
				{
					reportError(n.getLocation(),getDisplayName(n) + ", member of disambiguation function " + bean.getDisplayName() + ", is not a terminal or a terminal class");
					hasError = true;					
				}
			}
			// Check that the "disambiguateTo" property, if provided, is a member of the disambiguation function.
			if(bean.getDisambiguateTo() != null &&
			   !bean.getMembers().contains(bean.getDisambiguateTo()))
			{
				reportError(bean.getLocation(),getDisplayName(bean.getDisambiguateTo()) + ", target of disambiguation function " + bean.getDisplayName() + ", is not a member of the disambiguation function");
				hasError = true;
			}
			// Check that "disambiguateTo" and "code" are not both provided.
			if(bean.getDisambiguateTo() != null && bean.getCode() != null)
			{
				reportError(bean.getLocation(),"A disambiguation function cannot specify both a declarative target and a code block");
				hasError = true;
			}
		}
		return hasError;
	}
	
	private boolean visitCompleteGrammarBean(Grammar bean,boolean isExtensionGrammar)
	{
		ExtensionGrammar extBean;
		if(isExtensionGrammar) extBean = (ExtensionGrammar) bean;
		else extBean = null;
		boolean hasError = false;
		boolean isDefined;
		// Check that the grammar's grammar-layout symbols are all defined and are all terminals.
		if(bean.getGrammarLayout() != null)
		{
			for(CopperElementReference n : bean.getGrammarLayout())
			{
				isDefined = nameIsDefined(n) && nameIsHost(n);
				hasError |= !isDefined;
				if(isDefined &&
						dereference(n).getType() != CopperElementType.TERMINAL)
				{
					reportError(n.getLocation(),getDisplayName(n) + ", designated as a grammar layout symbol of grammar " + bean.getDisplayName() + ", is not a terminal");
					hasError = true;					
				}
			}
		}
		// Check all the grammar's elements for errors.
		for(CopperElementName n : bean.getGrammarElements())
		{
			if(isExtensionGrammar && extBean.getBridgeProductions().contains(n)) isBridgeProduction = true;
			hasError |= bean.getGrammarElement(n).acceptVisitor(this);
			isBridgeProduction = false;
		}
		return hasError;
	}

	@Override
	public Boolean visitGrammar(Grammar bean)
	{
		currentGrammar = bean;
		boolean hasError = false;
		// Check that the bean is not a "placeholder" with none of its required elements.
		if(bean.isPlaceholder())
		{
			reportError(currentParser.getLocation(),currentParser,currentGrammar,"Grammar is listed in parser but not specified");
			hasError = true;
		}
		// Check that none of the grammar's required elements are missing.
		else if(!bean.isComplete())
		{
			Set<String> whatIsMissing = bean.whatIsMissing();
			reportError(bean.getLocation(),"Grammar is missing the following required attributes: " + whatIsMissing);
			hasError = true;
		}
		else
		{
			hasError |= visitCompleteGrammarBean(bean,false);
		}
		currentGrammar = null;
		return hasError;
	}

	@Override
	public Boolean visitExtensionGrammar(ExtensionGrammar bean)
	{
		currentGrammar = bean;
		boolean hasError = false;
		// Check that none of the grammar's required elements are missing.
		if(!bean.isComplete())
		{
			Set<String> whatIsMissing = bean.whatIsMissing();
			reportError(bean.getLocation(),"Extension grammar is missing the following required attributes: " + whatIsMissing);
			hasError = true;
		}
		else
		{
			hasError |= visitCompleteGrammarBean(bean,true);
			
			Hashtable<CopperElementName,CopperElementName> markingToLHS = new Hashtable<CopperElementName, CopperElementName>();
			
			for(CopperElementName p : bean.getBridgeProductions())
			{
				CopperElementName marking = bean.getBridgeProduction(p).getRhs().get(0).getName();
				if(markingToLHS.containsKey(marking) && !markingToLHS.equals(bean.getBridgeProduction(p).getLhs().getName()))
				{
					reportError(bean.getLocation(),"Marking terminal " + marking + " used on bridge productions with different left-hand sides");
				}
				else markingToLHS.put(marking, bean.getBridgeProduction(p).getLhs().getName());
			}
		}
		return hasError;
	}

	@Override
	public Boolean visitNonTerminal(NonTerminal bean)
	{
		boolean hasError = false;
		// Check that none of the nonterminal's required elements are missing.
		if(!bean.isComplete())
		{
			Set<String> whatIsMissing = bean.whatIsMissing();
			errors.add(new GrammarError(bean.getLocation(),currentParser,null,"Nonterminal " + bean.getDisplayName() + " is missing the following required attributes: " + whatIsMissing));
			hasError = true;
		}
		return hasError;
	}

	@Override
	public Boolean visitParserAttribute(ParserAttribute bean)
	{
		boolean hasError = false;
		// Check that none of the parser attribute's required elements are missing.
		if(!bean.isComplete())
		{
			Set<String> whatIsMissing = bean.whatIsMissing();
			errors.add(new GrammarError(bean.getLocation(),currentParser,null,"Parser attribute " + bean.getDisplayName() + " is missing the following required attributes: " + whatIsMissing));
			hasError = true;
		}
		return hasError;
	}

	@Override
	public Boolean visitParserBean(ParserBean bean)
	{
		currentParser = bean;
		boolean hasError = false;
		// Check that none of the parser's required elements are missing.
		if(bean == null)
		{
			errors.add(new GrammarError(null,null,null,"No parser specified"));
			hasError = true;
		}
		else if(!bean.isComplete())
		{
			Set<String> whatIsMissing = bean.whatIsMissing();
			errors.add(new GrammarError(bean.getLocation(),currentParser,null,"Parser is missing the following required attributes: " + whatIsMissing));
			hasError = true;
		}
		else
		{
			// Check that the grammar's class name, if defined, is not a fully qualified name.
			if(bean.getClassName() != null && bean.getClassName().indexOf('.') != -1)
			{
				errors.add(new GrammarError(bean.getLocation(),currentParser,null,"Class name " + bean.getClassName() + " includes package name"));
				hasError = true;
			}
			// Check that, if this is a unitary parser, it does not have more than one grammar.
			if(bean.isUnitary() && bean.getGrammars().size() > 1)
			{
				errors.add(new GrammarError(bean.getLocation(),currentParser,null,"Parser " + bean.getDisplayName() + " has been declared as unitary but contains more than one grammar"));
				hasError = true;
			}
			// Check that none of the parser's constituent grammars have an error.
			for(CopperElementName n : bean.getGrammars())
			{
				if(bean.getGrammar(n).getType() == CopperElementType.EXTENSION_GRAMMAR &&
				   bean.getType() != CopperElementType.EXTENDED_PARSER)
				{
					errors.add(new GrammarError(bean.getLocation(),currentParser,bean.getGrammar(n),"Extension grammars may only appear inside extended parsers"));
					hasError = true;
				}
				hasError |= bean.getGrammar(n).acceptVisitor(this);
			}
			// Check that the parser's designated start symbol is defined in the parser,
			boolean isDefined = nameIsDefined(bean.getStartSymbol());
			hasError |= !isDefined;
			// is a nonterminal,
			if(isDefined &&
			   dereference(bean.getStartSymbol()).getType() != CopperElementType.NON_TERMINAL)
			{
				errors.add(new GrammarError(bean.getStartSymbol().getLocation(),currentParser,null,getDisplayName(bean.getStartSymbol()) + ", designated as praser's start symbol, is not a nonterminal"));
				hasError = true;
			}
			// and is not in an extension grammar.
			else if(isDefined &&
					bean.getGrammar(bean.getStartSymbol().getGrammarName()).getType() == CopperElementType.EXTENSION_GRAMMAR)
			{
				errors.add(new GrammarError(bean.getStartSymbol().getLocation(),currentParser,null,"Designated start symbol " + getDisplayName(bean.getStartSymbol()) + " is in an extension grammar (" + bean.getGrammar(bean.getStartSymbol().getGrammarName()).getDisplayName() + ")"));
				hasError = true;
			}
			// Check that the parser's start-layout symbols are all defined.
			if(bean.getStartLayout() != null)
			{
				for(CopperElementReference n : bean.getStartLayout())
				{
					isDefined = nameIsDefined(n);
					hasError |= !isDefined;
					if(isDefined &&
							dereference(n).getType() != CopperElementType.TERMINAL)
					{
						reportError(n.getLocation(),getDisplayName(n) + ", designated as a start layout symbol of parser " + bean.getDisplayName() + ", is not a terminal");
						hasError = true;					
					}
				}
			}
		}
		return hasError;
	}
	
	@Override
	public Boolean visitExtendedParserBean(ExtendedParserBean bean)
	{
		boolean hasError = visitParserBean(bean);
		if(hasError) return hasError;
		if(bean.getHostGrammar() != null && !bean.getGrammars().contains(bean.getHostGrammar()))
		{
			reportError(bean.getLocation(),bean.getHostGrammar() + ", designated as the host grammar of parser " + bean.getDisplayName() + " is not designated as one of the parser's grammars");
			hasError = true;
		}
		if(bean.getGrammar(bean.getHostGrammar()).getType() == CopperElementType.EXTENSION_GRAMMAR)
		{
			reportError(bean.getLocation(),bean.getGrammar(bean.getHostGrammar()).getDisplayName() + ", designated as the host grammar of parser " + bean.getDisplayName() + " is an extension grammar");
			hasError = true;			
		}
		if(bean.getGrammars().size() != 2)
		{
			reportError(bean.getLocation(),"Extended parser " + bean.getDisplayName() + " must have exactly two grammars, one host and one extension");
			hasError = true;
		}
		else for(CopperElementName grammar : bean.getGrammars())
		{
			if(!grammar.equals(bean.getHostGrammar()) && bean.getGrammar(grammar).getType() != CopperElementType.EXTENSION_GRAMMAR)
			{
				reportError(bean.getLocation(),bean.getGrammar(bean.getHostGrammar()).getDisplayName() + ", designated as an extension grammar in parser " + bean.getDisplayName() + ", is not an extension grammar");
				hasError = true;
			}
		}
		if(hasError) return hasError;
		if(!bean.getStartSymbol().isFQ() || !bean.getStartSymbol().getGrammarName().equals(bean.getHostGrammar()))
		{
			reportError(bean.getStartSymbol().getLocation(),getDisplayName(bean.getStartSymbol()) + ", designated as the start symbol of parser " + bean.getDisplayName() + ", does not belong to the host grammar");
			hasError = true;
		}
		if(bean.getStartLayout() != null)
		{
			for(CopperElementReference layout : bean.getStartLayout())
			{
				if(!layout.isFQ() || !layout.getGrammarName().equals(bean.getHostGrammar()))
				{
					reportError(bean.getStartSymbol().getLocation(),getDisplayName(bean.getStartSymbol()) + ", designated as a start layout symbol of parser " + bean.getDisplayName() + ", does not belong to the host grammar");
					hasError = true;
				}			
			}
		}
		
		return hasError;
	}

	@Override
	public Boolean visitProduction(Production bean)
	{
		// FIXME: Check that all production signatures in the parser spec are unique.
		boolean hasError = false;
		// Check that none of the production's required elements are missing.
		if(!bean.isComplete())
		{
			Set<String> whatIsMissing = bean.whatIsMissing();
			reportError(bean.getLocation(),"Production " + bean.getDisplayName() + " is missing the following required attributes: " + whatIsMissing);
			hasError = true;
		}
		else
		{
			// Check that the left-hand side is defined,
			boolean isDefined = nameIsDefined(bean.getLhs());
			hasError |= !isDefined;
			// and is a nonterminal.
			if(isDefined &&
			   dereference(bean.getLhs()).getType() != CopperElementType.NON_TERMINAL)
			{
				reportError(bean.getLhs().getLocation(),getDisplayName(bean.getLhs()) + ", designated as production " + bean.getDisplayName() + "'s left-hand side, is not a nonterminal");
				hasError = true;					
			}
			int i = 0;
			if(isBridgeProduction)
			{
				if(bean.getRhs().size() < 1 ||
				   (bean.getRhs().get(0).isFQ() && !bean.getRhs().get(0).getGrammarName().equals(currentGrammar.getName())) ||
				   !((ExtensionGrammar) currentGrammar).getMarkingTerminals().contains(bean.getRhs().get(0).getName()))
				{
					reportError(bean.getLocation(),bean.getDisplayName() + ", designated as a bridge production of grammar " + currentGrammar.getDisplayName() + ", must have a marking terminal as its first right-hand side symbol");
					hasError = true;
				}
				if(!bean.getLhs().isFQ() ||
				   !bean.getLhs().getGrammarName().equals(((ExtendedParserBean) currentParser).getHostGrammar()))
				{
					reportError(bean.getLhs().getLocation(),getDisplayName(bean.getLhs()) + ", designated as the left-hand side of a bridge production, was not declared in the host grammar");
					hasError = true;
				}
				i++;
			}
			else if(currentParser.getType() == CopperElementType.EXTENDED_PARSER)
			{
				if(bean.getLhs().isFQ() &&
				   !bean.getLhs().getGrammarName().equals(currentGrammar.getName()))
				{
					reportError(bean.getLhs().getLocation(),getDisplayName(bean.getLhs()) + ", designated as the left-hand side of a production in an extended parser, was not declared in the same grammar as the production");
					hasError = true;
				}
			}
			for(;i < bean.getRhs().size();i++)
			{
				CopperElementReference n = bean.getRhs().get(i);
				// Check that all the names on the right-hand side are defined,
				isDefined = nameIsDefined(n) && nameIsHost(n);
				hasError |= !isDefined;
				// refer to terminals or nonterminals,
				if(isDefined &&
						dereference(n).getType() != CopperElementType.TERMINAL &&
						dereference(n).getType() != CopperElementType.NON_TERMINAL)
				{
					reportError(n.getLocation(),getDisplayName(n) + " is not a terminal or a nonterminal");
					hasError = true;					
				}
				// and are not marking terminals.
				if(isDefined &&
				   currentParser.getType() == CopperElementType.EXTENDED_PARSER &&
				   currentGrammar.getType() == CopperElementType.EXTENSION_GRAMMAR &&
				   (!n.isFQ() || n.getGrammarName().equals(currentGrammar.getName())) &&
				   ((ExtensionGrammar) currentGrammar).getMarkingTerminals().contains(dereference(n).getName()))
				{
					reportError(n.getLocation(),getDisplayName(n) + ", designated as a marking terminal, must be referenced only as the first right-hand-side element in a bridge production");
					hasError = true;
				}
			}
			// Check that the list of variable names is of equal length to the right-hand side.
			if(bean.getRhsVarNames() != null && bean.getRhsVarNames().size() != bean.getRhs().size())
			{
				reportError(bean.getLocation(),"In production " + bean.getDisplayName() + ": Size mismatch between right-hand side symbols (" + bean.getRhs().size() + ") and variable names (" + bean.getRhs().size() + " symbols on its right-hand side but specifies " + bean.getRhsVarNames() + " variable names");
				hasError = true;
			}
			// Check that the list of variable names contains no duplicates.
			if(bean.getRhsVarNames() != null)
			{
				HashSet<String> varNames = new HashSet<String>();
				for(String n : bean.getRhsVarNames())
				{
					if(n != null && !varNames.add(n))
					{
						reportError(bean.getLocation(),"In production " + bean.getDisplayName() + ": Duplicate variable name: " + n);
						hasError = true;
					}
				}
			}
			// Check that the terminal's prefix is either not specified,
			if(bean.getOperator() != null)
			{
				// or is defined
				CopperElementReference n = bean.getOperator();
				isDefined = nameIsDefined(n) && nameIsHost(n);
				hasError |= !isDefined;
				// and is a terminal.
				if(isDefined &&
						dereference(n).getType() != CopperElementType.TERMINAL)
				{
					reportError(n.getLocation(),getDisplayName(n) + ", designated as production " + bean.getDisplayName() + "'s operator symbol, is not a terminal");
					hasError = true;					
				}
			}
			// Check that the production's operator class, if specified, refers to an operator class.
			if(bean.getPrecedenceClass() != null)
			{
				CopperElementReference n = bean.getPrecedenceClass();
				isDefined = nameIsDefined(n) && nameIsHost(n);
				hasError |= !isDefined;
				// and is a terminal.
				if(isDefined &&
						dereference(n).getType() != CopperElementType.OPERATOR_CLASS)
				{
					reportError(n.getLocation(),getDisplayName(n) + ", designated as production " + bean.getDisplayName() + "'s operator class, is not an operator class");
					hasError = true;					
				}
			}
			// Check that the production's precedence is not a negative number.
			if(bean.getPrecedence() != null && bean.getPrecedence() < 0)
			{
				reportError(bean.getLocation(),"Production " + bean.getDisplayName() + "'s precedence must be a non-negative integer; instead is " + bean.getPrecedence());
				hasError = true;
			}
			// Check that the production's layout, if defined, consists only of terminals.
			if(bean.getLayout() != null)
			{
				for(CopperElementReference n : bean.getLayout())
				{
					isDefined = nameIsDefined(n) && nameIsHost(n);
					hasError |= !isDefined;
					if(isDefined &&
							dereference(n).getType() != CopperElementType.TERMINAL)
					{
						reportError(n.getLocation(),getDisplayName(n) + ", specified as layout to production " + bean.getDisplayName() + ", is not a terminal");
						hasError = true;					
					}				
				}
			}
		}
		return hasError;
	}

	@Override
	public Boolean visitTerminal(Terminal bean)
	{
		currentTerminal = bean;
		boolean hasError = false;
		// Check that none of the terminal's required elements are missing.
		if(!bean.isComplete())
		{
			Set<String> whatIsMissing = bean.whatIsMissing();
			reportError(bean.getLocation(),"Terminal " + bean.getDisplayName() + " is missing the following required attributes: " + whatIsMissing);
			hasError = true;
		}
		else
		{
			// Check that the production's operator class, if specified, refers to an operator class.
			if(bean.getOperatorClass() != null)
			{
				CopperElementReference n = bean.getOperatorClass();
				boolean isDefined = nameIsDefined(n) && nameIsHost(n);
				hasError |= !isDefined;
				// and is a terminal.
				if(isDefined &&
						dereference(n).getType() != CopperElementType.OPERATOR_CLASS)
				{
					reportError(n.getLocation(),getDisplayName(n) + ", designated as terminal " + bean.getDisplayName() + "'s operator class, is not an operator class");
					hasError = true;					
				}
			}
			// Check that the terminal's operator precedence is not a negative number.
			if(bean.getOperatorPrecedence() != null && bean.getOperatorPrecedence() < 0)
			{
				reportError(bean.getLocation(),"Terminal " + bean.getDisplayName() + "'s operator precedence must be a non-negative integer; instead is " + bean.getOperatorPrecedence());
				hasError = true;
			}
			// Check that the terminal's prefix is either not specified or refers to a terminal.
			if(bean.getPrefix() != null)
			{
				CopperElementReference n = bean.getPrefix();
				boolean isDefined = nameIsDefined(n) && nameIsHost(n);
				hasError |= !isDefined;
				if(isDefined &&
					dereference(n).getType() != CopperElementType.TERMINAL)
				{
					reportError(n.getLocation(),getDisplayName(n) + ", designated as terminal " + bean.getDisplayName() + "'s transparent prefix, is not a terminal");
					hasError = true;					
				}
			}
			// Check that all the names in the terminal class list are defined and refer to terminal classes.
			for(CopperElementReference n : bean.getTerminalClasses())
			{
				boolean isDefined = nameIsDefined(n) && nameIsHost(n);
				hasError |= !isDefined;
				if(isDefined &&
				   dereference(n).getType() != CopperElementType.TERMINAL_CLASS)
				{
					reportError(n.getLocation(),"Non-terminal class " + getDisplayName(n) + " in the class list of terminal " + bean.getDisplayName());
					hasError = true;					
				}
			}
			for(CopperElementReference n : bean.getSubmitList())
			{
				// Check that all the names in the submit list are defined,
				boolean isDefined = nameIsDefined(n) && nameIsHost(n);
				hasError |= !isDefined;
				// and refer to terminals or terminal classes.
				if(isDefined &&
						dereference(n).getType() != CopperElementType.TERMINAL &&
						dereference(n).getType() != CopperElementType.TERMINAL_CLASS)
				{
					reportError(n.getLocation(),getDisplayName(n) + ", in the submit list of terminal " + bean.getDisplayName() + ", is not a terminal or a terminal class");
					hasError = true;					
				}
			}
			for(CopperElementReference n : bean.getDominateList())
			{
				// Check that all the names in the dominate list are defined,
				boolean isDefined = nameIsDefined(n) && nameIsHost(n);
				hasError |= !isDefined;
				// and refer to terminals or terminal classes.
				if(isDefined &&
						dereference(n).getType() != CopperElementType.TERMINAL &&
						dereference(n).getType() != CopperElementType.TERMINAL_CLASS)
				{
					reportError(n.getLocation(),getDisplayName(n) + ", in the dominate list of terminal " + bean.getDisplayName() + ", is not a terminal or a terminal class");
					hasError = true;					
				}
			}
		}
		return hasError;
	}
	
	@Override
	public Boolean visitTerminalClass(TerminalClass bean)
	{
		boolean hasError = false;
		// Check that none of the terminal class's required elements are missing.
		if(!bean.isComplete())
		{
			Set<String> whatIsMissing = bean.whatIsMissing();
			reportError(bean.getLocation(),"Terminal class " + bean.getDisplayName() + " is missing the following required attributes: " + whatIsMissing);
			hasError = true;
		}
		return hasError;
	}


	@Override
	public Boolean visitOperatorClass(OperatorClass bean)
	throws RuntimeException
	{
		boolean hasError = false;
		// Check that none of the production class's required elements are missing.
		if(!bean.isComplete())
		{
			Set<String> whatIsMissing = bean.whatIsMissing();
			reportError(bean.getLocation(),"Production class " + bean.getDisplayName() + " is missing the following required attributes: " + whatIsMissing);
			hasError = true;
		}
		return hasError;
	}
	
	
	
	
	private boolean checkRegexCompleteness(Regex bean)
	{
		boolean hasError = false;
		// Check that none of the regex's required elements are missing.
		if(!bean.isComplete())
		{
			Set<String> whatIsMissing = bean.whatIsMissing();
			reportError(currentTerminal.getLocation(),"Regex is missing the following required attributes: " + whatIsMissing);
			hasError = true;
		}
		return hasError;
	}
	
	@Override
	public Boolean visitChoiceRegex(ChoiceRegex bean)
	throws RuntimeException
	{
		boolean hasError = checkRegexCompleteness(bean);
		for(Regex constit : bean.getSubexps()) hasError |= constit.acceptVisitor(this);
		return hasError;
	}

	@Override
	public Boolean visitConcatenationRegex(ConcatenationRegex bean)
	throws RuntimeException
	{
		boolean hasError = checkRegexCompleteness(bean);
		for(Regex constit : bean.getSubexps()) hasError |= constit.acceptVisitor(this);
		return hasError;
	}

	@Override
	public Boolean visitKleeneStarRegex(KleeneStarRegex bean)
	throws RuntimeException
	{
		boolean hasError = checkRegexCompleteness(bean);
		hasError |= bean.getSubexp().acceptVisitor(this);
		return hasError;
	}

	@Override
	public Boolean visitEmptyStringRegex(EmptyStringRegex bean)
	throws RuntimeException
	{
		return checkRegexCompleteness(bean);
	}

	@Override
	public Boolean visitCharacterSetRegex(CharacterSetRegex bean,SetOfCharsSyntax chars)
	throws RuntimeException
	{
		return checkRegexCompleteness(bean);
	}

	@Override
	public Boolean visitMacroHoleRegex(MacroHoleRegex bean)
	throws RuntimeException
	{
		boolean hasError = checkRegexCompleteness(bean);
		boolean isDefined = nameIsDefined(bean.getMacroName()) && nameIsHost(bean.getMacroName());
		hasError |= !isDefined;
		if(isDefined &&
				dereference(bean.getMacroName()).getType() != CopperElementType.TERMINAL)
		{
			reportError(bean.getMacroName().getLocation(),getDisplayName(bean.getMacroName()) + " does not reference a valid regex");
			hasError = true;					
		}
		return hasError;
	}

	
	
	
	
	
	
	private boolean nameIsDefined(CopperElementReference symbol)
	{
		if(!symbol.isFQ())
		{
			if(currentGrammar == null || !currentGrammar.getGrammarElements().contains(symbol.getName()))
			{
				reportError(symbol.getLocation(),"Undefined reference to " + symbol.getName());
				return false;
			}
			else return true;
		}
		else
		{
			if(!currentParser.getGrammars().contains(symbol.getGrammarName()))
			{
				reportError(symbol.getLocation(),"Undefined reference to grammar " + symbol.getGrammarName());
				return false;
			}
			else if(!currentParser.getGrammar(symbol.getGrammarName()).getGrammarElements().contains(symbol.getName()))
			{
				reportError(symbol.getLocation(),"Undefined reference to " + (currentParser.isUnitary() ? symbol.getName() : symbol));
				return false;
			}
			else return true;
		}
	}
	
	// Assumes that nameIsDefined(symbol) == true.
	private boolean nameIsHost(CopperElementReference symbol)
	{
		if(currentParser.getType() != CopperElementType.EXTENDED_PARSER) return true;
		ExtendedParserBean currentExtendedParser = (ExtendedParserBean) currentParser;
		
		if(symbol.isFQ() &&
		   !symbol.getGrammarName().equals(currentGrammar.getName()) &&
		   !symbol.getGrammarName().equals(currentExtendedParser.getHostGrammar()))
		{
			
			if(currentGrammar.getType() == CopperElementType.EXTENSION_GRAMMAR)
			{
				reportError(symbol.getLocation(),"Extension grammar " + currentGrammar.getDisplayName() + " may not contain a reference to symbol " + getDisplayName(symbol) + ", which is defined neither in " + currentGrammar.getDisplayName() + ", nor in the parser's host grammar");
				return false;				
			}
			else
			{
				reportError(symbol.getLocation(),"The host grammar of an extended parser may not contain a reference to external symbol " + getDisplayName(symbol));
				return false;
			}
		}
		return true;
	}
	
	
	
	
	
	

	// Assumes that nameIsDefined(symbol) == true.
	private GrammarElement dereference(CopperElementReference symbol)
	{
		if(!symbol.isFQ()) return currentGrammar.getGrammarElement(symbol.getName());
		else return currentParser.getGrammar(symbol.getGrammarName()).getGrammarElement(symbol.getName());
	}
	
	private String getDisplayName(CopperElementReference symbol)
	{
		GrammarElement dereferencedSymbol = dereference(symbol);
		if(dereferencedSymbol == null) return symbol.toString();
		else return dereferencedSymbol.getDisplayName();
	}
	
	private void reportError(Location location,ParserBean parser,Grammar grammar,String message)
	{
		errors.add(new GrammarError(location,parser,grammar,message));		
	}
	
	private void reportError(Location location,String message)
	{
		reportError(location,currentParser,currentGrammar,message);
	}
}
