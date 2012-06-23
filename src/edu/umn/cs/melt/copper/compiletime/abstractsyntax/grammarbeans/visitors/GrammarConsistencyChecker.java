package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CharacterSetRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ChoiceRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ConcatenationRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementType;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.DisambiguationFunctionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.EmptyStringRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtensionGrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarElementBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.KleeneStarRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.MacroHoleRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserAttributeBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ProductionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.OperatorClassBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.RegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalClassBean;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
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
	
	public GrammarConsistencyChecker()
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
	private GrammarBean currentGrammar;
	private TerminalBean currentTerminal;
	private boolean isBridgeProduction;
	
	@Override
	public Boolean visitDisambiguationFunctionBean(DisambiguationFunctionBean bean)
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
				boolean isDefined = nameIsDefined(n);
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
	
	private boolean visitCompleteGrammarBean(GrammarBean bean)
	{
		boolean hasError = false;
		boolean isDefined;
		// Check that the grammar's grammar-layout symbols are all defined and are all terminals.
		if(bean.getGrammarLayout() != null)
		{
			for(CopperElementReference n : bean.getGrammarLayout())
			{
				isDefined = nameIsDefined(n);
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
			hasError |= bean.getGrammarElement(n).acceptVisitor(this);
		}
		return hasError;
	}

	@Override
	public Boolean visitGrammarBean(GrammarBean bean)
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
			hasError |= visitCompleteGrammarBean(bean);
		}
		currentGrammar = null;
		return hasError;
	}

	@Override
	public Boolean visitExtensionGrammarBean(ExtensionGrammarBean bean)
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
			hasError |= visitCompleteGrammarBean(bean);
			hasError |= bean.getMarkingTerminal().acceptVisitor(this);
			isBridgeProduction = true;
			hasError |= bean.getStartProduction().acceptVisitor(this);
		}
		if(!hasError)
		{
			// In the future, check that the grammar's bridge production is of the correct form.
			
			// Full list of conditions (conditions 3 and 5 have been dropped for practical reasons):
			
			// 1. The marking terminal has a name that is undefined in the extension grammar.
			if(bean.getGrammarElements().contains(bean.getMarkingTerminal().getName()))
			{
				reportError(bean.getMarkingTerminal().getLocation(),"Terminal " + bean.getMarkingTerminal().getDisplayName() + " already defined in this grammar");
				hasError = true;
			}
			if(
			   // 2. LHS is a host nonterminal.					
		       (bean.getStartProduction().getLhs().isFQ() &&
			    !bean.getStartProduction().getLhs().getGrammarName().equals(bean.getName())) ||
			   // 3. RHS is of length 2.
			    false ||
			   // 4. First RHS symbol is marking terminal.
			   !bean.getStartProduction().getRhs().get(0).getName().equals(bean.getMarkingTerminal().getName()) ||
			   (bean.getStartProduction().getRhs().get(0).isFQ() &&
			    !bean.getStartProduction().getRhs().get(0).getGrammarName().equals(bean.getName())))
			{
				reportError(bean.getMarkingTerminal().getLocation(),"Malformed bridge production");
				hasError = true;				
			}
		}
		return hasError;
	}

	@Override
	public Boolean visitNonTerminalBean(NonTerminalBean bean)
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
	public Boolean visitParserAttributeBean(ParserAttributeBean bean)
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
		return visitParserBean(bean);
	}

	@Override
	public Boolean visitProductionBean(ProductionBean bean)
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
			for(CopperElementReference n : bean.getRhs())
			{
				// If the production is an extension terminal's bridge production,
				// skip checks of the first symbol on the right-hand side; this is the
				// marking terminal and will not be defined in the grammar proper.
				if(isBridgeProduction)
				{
					isBridgeProduction = false;
					continue;
				}
				// Check that all the names on the right-hand side are defined,
				isDefined = nameIsDefined(n);
				hasError |= !isDefined;
				// and refer to terminals or nonterminals.
				if(isDefined &&
						dereference(n).getType() != CopperElementType.TERMINAL &&
						dereference(n).getType() != CopperElementType.NON_TERMINAL)
				{
					reportError(n.getLocation(),getDisplayName(n) + " is not a terminal or a nonterminal");
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
				isDefined = nameIsDefined(n);
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
				isDefined = nameIsDefined(n);
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
					isDefined = nameIsDefined(n);
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
	public Boolean visitTerminalBean(TerminalBean bean)
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
				boolean isDefined = nameIsDefined(n);
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
				boolean isDefined = nameIsDefined(n);
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
				boolean isDefined = nameIsDefined(n);
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
				boolean isDefined = nameIsDefined(n);
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
				boolean isDefined = nameIsDefined(n);
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
	public Boolean visitTerminalClassBean(TerminalClassBean bean)
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
	public Boolean visitOperatorClassBean(OperatorClassBean bean)
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
	
	
	
	
	private boolean checkRegexCompleteness(RegexBean bean)
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
	public Boolean visitChoiceRegex(ChoiceRegexBean bean)
	throws RuntimeException
	{
		boolean hasError = checkRegexCompleteness(bean);
		for(RegexBean constit : bean.getSubexps()) hasError |= constit.acceptVisitor(this);
		return hasError;
	}

	@Override
	public Boolean visitConcatenationRegex(ConcatenationRegexBean bean)
	throws RuntimeException
	{
		boolean hasError = checkRegexCompleteness(bean);
		for(RegexBean constit : bean.getSubexps()) hasError |= constit.acceptVisitor(this);
		return hasError;
	}

	@Override
	public Boolean visitKleeneStarRegex(KleeneStarRegexBean bean)
	throws RuntimeException
	{
		boolean hasError = checkRegexCompleteness(bean);
		hasError |= bean.getSubexp().acceptVisitor(this);
		return hasError;
	}

	@Override
	public Boolean visitEmptyStringRegex(EmptyStringRegexBean bean)
	throws RuntimeException
	{
		return checkRegexCompleteness(bean);
	}

	@Override
	public Boolean visitCharacterSetRegex(CharacterSetRegexBean bean,SetOfCharsSyntax chars)
	throws RuntimeException
	{
		return checkRegexCompleteness(bean);
	}

	@Override
	public Boolean visitMacroHoleRegex(MacroHoleRegexBean bean)
	throws RuntimeException
	{
		boolean hasError = checkRegexCompleteness(bean);
		// Check that all the names in the dominate list are defined,
		boolean isDefined = nameIsDefined(bean.getMacroName());
		hasError |= !isDefined;
		// and refer to terminals or terminal classes.
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
				reportError(symbol.getLocation(),"Undefined reference to " + symbol);
				return false;
			}
			else return true;
		}
	}
	
	
	
	
	
	

	// Assumes that nameIsDefined(symbol) == true.
	private GrammarElementBean dereference(CopperElementReference symbol)
	{
		if(!symbol.isFQ()) return currentGrammar.getGrammarElement(symbol.getName());
		else return currentParser.getGrammar(symbol.getGrammarName()).getGrammarElement(symbol.getName());
	}
	
	private String getDisplayName(CopperElementReference symbol)
	{
		return dereference(symbol).getDisplayName();
	}
	
	private void reportError(Location location,ParserBean parser,GrammarBean grammar,String message)
	{
		errors.add(new GrammarError(location,parser,grammar,message));		
	}
	
	private void reportError(Location location,String message)
	{
		reportError(location,currentParser,currentGrammar,message);
	}
}
