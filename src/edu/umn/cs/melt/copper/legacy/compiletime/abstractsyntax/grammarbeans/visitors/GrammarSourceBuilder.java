package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammarbeans.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CharacterSetRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ChoiceRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ConcatenationRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
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
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.CopperASTBeanVisitor;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.RegexBeanVisitor;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarName;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LexicalAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LexicalDisambiguationGroup;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminalAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.OperatorAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.ParserSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.ProductionAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.CharacterSet;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.Choice;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.Concatenation;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.EmptyString;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.KleeneStar;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.MacroHole;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.regex.ParsedRegex;

/**
 * This visitor builds a GrammarSource object out of a spec represented by a ParserBean object.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class GrammarSourceBuilder implements CopperASTBeanVisitor<Boolean, RuntimeException>
{
	private GrammarSource grammar;
	
	private NameAdder nameAdder;
	private AttributeAdder attributeAdder;
	
	public GrammarSourceBuilder()
	{
		this.grammar = new GrammarSource();
		this.currentParser = null;
		this.currentGrammar = null;
		this.nameAdder = new NameAdder();
		this.attributeAdder = new AttributeAdder();
	}
	
	public GrammarSourceBuilder(GrammarSource grammar)
	{
		this.grammar = grammar;
	}

	public GrammarSource getGrammar()
	{
		return grammar;
	}

	private ParserBean currentParser;
	private Grammar currentGrammar;
	
	private class NameAdder implements CopperASTBeanVisitor<Boolean,RuntimeException>
	{
		@Override
		public Boolean visitDisambiguationFunction(DisambiguationFunction bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			if(bean.hasDisplayName()) grammar.setDisplayName(name,bean.getDisplayName());
			return false;
		}

		@Override
		public Boolean visitGrammar(Grammar bean)
		throws RuntimeException
		{
			currentGrammar = bean;
			GrammarName grammarName = new GrammarName(generateName(bean));
			grammar.addContainedGrammar(grammarName);
			if(bean.hasDisplayName()) grammar.setDisplayName(grammarName.getName(),bean.getDisplayName());
			boolean hasError = false;
			for(CopperElementName n : bean.getGrammarElements())
			{
				hasError |= bean.getGrammarElement(n).acceptVisitor(this);
			}
			return hasError;			
		}

		@Override
		public Boolean visitExtensionGrammar(ExtensionGrammar bean)
		throws RuntimeException
		{
			currentGrammar = bean;
			GrammarName grammarName = new GrammarName(generateName(bean));
			grammar.addContainedGrammar(grammarName);
			boolean hasError = false;
			for(CopperElementName n : bean.getMarkingTerminals())
			{
				hasError |= bean.getMarkingTerminal(n).acceptVisitor(this);
			}
			for(CopperElementName n : bean.getBridgeProductions())
			{
				hasError |= bean.getBridgeProduction(n).acceptVisitor(this);
			}
			for(CopperElementName n : bean.getGrammarElements())
			{
				hasError |= bean.getGrammarElement(n).acceptVisitor(this);
			}
	
			if(bean.hasDisplayName()) grammar.setDisplayName(grammarName.getName(),bean.getDisplayName());
			return hasError;			
		}

		@Override
		public Boolean visitNonTerminal(NonTerminal bean)
		throws RuntimeException
		{
			Symbol ntName = generateName(bean);
			grammar.addToNT(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminal(ntName));
			if(bean.hasDisplayName()) grammar.setDisplayName(ntName,bean.getDisplayName());
			return false;
		}

		@Override
		public Boolean visitParserAttribute(ParserAttribute bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			if(bean.hasDisplayName()) grammar.setDisplayName(name,bean.getDisplayName());
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
		public Boolean visitProduction(Production bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			if(bean.hasDisplayName()) grammar.setDisplayName(name,bean.getDisplayName());
			return false;
		}

		@Override
		public Boolean visitTerminal(Terminal bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal t = new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(name);
			grammar.addToT(t);
			if(bean.hasDisplayName()) grammar.setDisplayName(name,bean.getDisplayName());
			return false;
		}

		@Override
		public Boolean visitTerminalClass(TerminalClass bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			grammar.addTClass(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass(name));
			if(bean.hasDisplayName()) grammar.setDisplayName(name,bean.getDisplayName());
			return false;
		}

		@Override
		public Boolean visitOperatorClass(OperatorClass bean)
		throws RuntimeException
		{
			return false;
		}
		
	}
	
	private class AttributeAdder implements CopperASTBeanVisitor<Boolean,RuntimeException>, RegexBeanVisitor<ParsedRegex,RuntimeException>
	{
		@Override
		public Boolean visitDisambiguationFunction(DisambiguationFunction bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			HashSet<edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal> members = new HashSet<edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal>();
			for(CopperElementReference n : bean.getMembers())
			{
				members.add(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(n)));
			}
			String code;
			if(bean.getCode() != null) code = transformCode(bean.getCode());
			else code = "return " + generateName(bean.getDisambiguateTo()) + ";";
			LexicalDisambiguationGroup group = new LexicalDisambiguationGroup(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass(name), members, code);
			grammar.addDisambiguationGroup(group);
			
			return false;
		}
	
		@Override
		public Boolean visitGrammar(Grammar bean)
		throws RuntimeException
		{
			currentGrammar = bean;
			GrammarName grammarName = new GrammarName(generateName(bean));
			if(bean.getGrammarLayout() != null) for(CopperElementReference n : bean.getGrammarLayout()) grammar.addGrammarLayout(grammarName, new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(n)));
			boolean hasError = false;
			for(CopperElementName n : bean.getGrammarElements())
			{
				hasError |= bean.getGrammarElement(n).acceptVisitor(this);
			}
	
			return hasError;			
		}
	
		@Override
		public Boolean visitExtensionGrammar(ExtensionGrammar bean)
		throws RuntimeException
		{
			currentGrammar = bean;
			GrammarName grammarName = new GrammarName(generateName(bean));
			if(bean.getGrammarLayout() != null) for(CopperElementReference n : bean.getGrammarLayout()) grammar.addGrammarLayout(grammarName, new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(n)));
			boolean hasError = false;
			for(CopperElementName n : bean.getMarkingTerminals())
			{
				hasError |= bean.getMarkingTerminal(n).acceptVisitor(this);
			}
			for(CopperElementName n : bean.getBridgeProductions())
			{
				hasError |= bean.getBridgeProduction(n).acceptVisitor(this);
			}
			for(CopperElementName n : bean.getGrammarElements())
			{
				hasError |= bean.getGrammarElement(n).acceptVisitor(this);
			}
	
			return hasError;			
		}
	
		@Override
		public Boolean visitNonTerminal(NonTerminal bean)
		throws RuntimeException
		{
			Symbol ntName = generateName(bean);
			boolean isStartSym = dereference(currentParser.getStartSymbol()) == bean;
			NonTerminalAttributes attrs = null;
			if(bean.getReturnType() == null)
			{
				attrs = new NonTerminalAttributes(new GrammarName(generateName(currentGrammar)),bean.getLocation(),isStartSym);
			}
			else
			{
				attrs = new NonTerminalAttributes(new GrammarName(generateName(currentGrammar)),bean.getLocation(),bean.getReturnType(),isStartSym);
			}
			grammar.addNTAttributes(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminal(ntName),attrs);
	
			return false;
		}
	
		@Override
		public Boolean visitParserAttribute(ParserAttribute bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			String code = transformCode(bean.getCode() == null ? "" : bean.getCode());
			grammar.addParserAttribute(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.ParserAttribute(name,bean.getAttributeType(),code));
			return false;
		}
	
		@Override
		public Boolean visitParserBean(ParserBean bean)
		throws RuntimeException
		{
			currentParser = bean;
			ParserSource sources = new ParserSource();
			sources.setClassFilePreambleCode(transformCode(bean.getPreambleCode() == null ? "" : bean.getPreambleCode()));
			sources.setParserAttrInitCode(transformCode(bean.getParserInitCode() == null ? "" : bean.getParserInitCode()));
			sources.setParserClassAuxCode(transformCode(bean.getParserClassAuxCode() == null ? "" : bean.getParserClassAuxCode()));
			if(bean.getClassName() != null) sources.setParserName(bean.getClassName());
			else sources.setParserName(bean.getName().toString());
			sources.setPostParseCode(transformCode(bean.getPostParseCode() == null ? "" : bean.getPostParseCode()));
			sources.setPackageName(bean.getPackageDecl() == null ? "" : bean.getPackageDecl());
			sources.setSemanticActionAuxCode(transformCode(bean.getSemanticActionAuxCode() == null ? "" : bean.getSemanticActionAuxCode()));
			grammar.setParserSources(sources);
			grammar.setDefaultProdCode(bean.getDefaultProductionCode() == null ? "" : bean.getDefaultProductionCode());
			grammar.setDefaultTCode(bean.getDefaultTerminalCode() == null ? "" : bean.getDefaultTerminalCode());
			grammar.setStartSym(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminal(generateName(bean.getStartSymbol())));
			if(bean.getStartLayout() != null)
			{
				for(CopperElementReference n : bean.getStartLayout()) grammar.addStartLayout(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(n)));
			}
			else if(bean.getGrammar(bean.getStartSymbol().getGrammarName()).getGrammarLayout() != null)
			{
				for(CopperElementReference n : bean.getGrammar(bean.getStartSymbol().getGrammarName()).getGrammarLayout()) grammar.addStartLayout(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(n)));
			}
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
		public Boolean visitProduction(Production bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal operator = null;
			if(bean.getOperator() != null) operator = new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(bean.getOperator()));
			edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminal lhs = new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminal(generateName(bean.getLhs()));
			ArrayList<GrammarSymbol> rhs = new ArrayList<GrammarSymbol>();
			for(CopperElementReference n : bean.getRhs())
			{
				GrammarElement nBean = dereference(n);
				if(nBean.getType() == CopperElementType.TERMINAL) rhs.add(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(n))); 
				else /* if(nBean.getType() == CopperElementType.NON_TERMINAL) */ rhs.add(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminal(generateName(n))); 
			}
			edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Production prod = edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Production.production(name,operator,lhs,rhs);
			grammar.addToP(prod);
			
			LinkedList<String> vars = null;
			if(bean.getRhsVarNames() != null)
			{
				vars = new LinkedList<String>();
				vars.addAll(bean.getRhsVarNames());
			}
			
			HashSet<edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal> layout = new HashSet<edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal>();
			if(bean.getLayout() != null)
			{
				for(CopperElementReference n : bean.getLayout())
				{
					layout.add(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(n)));
				}
			}
			else if(currentGrammar.getGrammarLayout() != null)
			{
				for(CopperElementReference n : currentGrammar.getGrammarLayout())
				{
					layout.add(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(n)));
				}
			}
			
			edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass productionClass;
			if(bean.getPrecedenceClass() == null) productionClass = new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass(FringeSymbols.STARTPROD_SYMBOL);
			else productionClass = new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass(generateName(bean.getPrecedenceClass()));

			int precedence = bean.getPrecedence() == null ? FringeSymbols.PRECEDENCE_NONE : bean.getPrecedence();
			
			String code = transformCode(bean.getCode() == null ? "" : bean.getCode());
			
			ProductionAttributes attributes = new ProductionAttributes(new GrammarName(generateName(currentGrammar)),
					                                                   bean.getLocation(),
					                                                   precedence,
					                                                   vars,
					                                                   layout,
					                                                   productionClass,
					                                                   code);
			grammar.addProductionAttributes(prod, attributes);
			return false;
		}
	
		@Override
		public Boolean visitTerminal(Terminal bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal t = new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(name);
			grammar.addRegex(t,bean.getRegex().acceptVisitor(this));
			String returnType = "Object";
			returnType = bean.getReturnType() == null ? "Object" : bean.getReturnType();
			edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal prefix = FringeSymbols.EMPTY;
			String code = transformCode(bean.getCode() == null ? "" : bean.getCode());
			if(bean.getPrefix() != null) prefix = new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(bean.getPrefix()));
			LexicalAttributes lexicalAttributes = new LexicalAttributes(new GrammarName(generateName(currentGrammar)),
																		bean.getLocation(),
																		returnType,
																		prefix,
																		code);
			grammar.addLexicalAttributes(t,lexicalAttributes);
			
			if(bean.getOperatorClass() != null || bean.getOperatorAssociativity() != null || bean.getOperatorPrecedence() != null)
			{
				int associativityType;
				switch(bean.getOperatorAssociativity())
				{
				case LEFT:
					associativityType = OperatorAttributes.ASSOC_LEFT;
					break;
				case RIGHT:
					associativityType = OperatorAttributes.ASSOC_RIGHT;
					break;
				case NONASSOC:
					associativityType = OperatorAttributes.ASSOC_NONASSOC;
					break;
				case NONE:
				default:
					associativityType = OperatorAttributes.ASSOC_NONE;
					break;
				}
				
				Integer operatorPrecedence = bean.getOperatorPrecedence();
				
				edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass precClass;
				if(bean.getOperatorClass() == null) precClass = new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass(FringeSymbols.STARTPROD_SYMBOL);
				else precClass = new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass(generateName(bean.getOperatorClass()));
				
				OperatorAttributes operatorAttributes = new OperatorAttributes(associativityType,
						                                                       operatorPrecedence,
						                                                       precClass);
				grammar.addOperatorAttributes(t, operatorAttributes);
			}
			
			for(CopperElementReference n : bean.getSubmitList())
			{
				grammar.addStaticPrecedenceRelation(t,new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(n)));
			}
			
			for(CopperElementReference n : bean.getDominateList())
			{
				grammar.addStaticPrecedenceRelation(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(n)),t);
			}
			
			return false;
		}
	
		@Override
		public Boolean visitTerminalClass(TerminalClass bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass tc = new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass(name);
			for(CopperElementReference n : bean.getMembers())
			{
				grammar.addToTClass(tc,new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(n)));
			}
			return false;
		}

		@Override
		public Boolean visitOperatorClass(OperatorClass bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public ParsedRegex visitChoiceRegex(ChoiceRegex bean)
		throws RuntimeException
		{
			ParsedRegex[] subexps = new ParsedRegex[bean.getSubexps().size()];
			int i = 0;
			for(Regex constit : bean.getSubexps()) subexps[i++] = constit.acceptVisitor(this);
			return new Choice(subexps);
		}

		@Override
		public ParsedRegex visitConcatenationRegex(ConcatenationRegex bean)
		throws RuntimeException
		{
			ParsedRegex[] subexps = new ParsedRegex[bean.getSubexps().size()];
			int i = 0;
			for(Regex constit : bean.getSubexps()) subexps[i++] = constit.acceptVisitor(this);
			return new Concatenation(subexps);
		}

		@Override
		public ParsedRegex visitKleeneStarRegex(KleeneStarRegex bean)
		throws RuntimeException
		{
			return new KleeneStar(bean.getSubexp().acceptVisitor(this));
		}

		@Override
		public ParsedRegex visitEmptyStringRegex(EmptyStringRegex bean)
		throws RuntimeException
		{
			return new EmptyString();
		}

		@Override
		public ParsedRegex visitCharacterSetRegex(CharacterSetRegex bean,SetOfCharsSyntax chars)
		throws RuntimeException
		{
			return CharacterSet.instantiate(chars);
		}

		@Override
		public ParsedRegex visitMacroHoleRegex(MacroHoleRegex bean)
		throws RuntimeException
		{
			return new MacroHole(new edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal(generateName(bean.getMacroName())));
		}
	}

	
	@Override
	public Boolean visitDisambiguationFunction(DisambiguationFunction bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitGrammar(Grammar bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitExtensionGrammar(ExtensionGrammar bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitNonTerminal(NonTerminal bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitParserAttribute(ParserAttribute bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitParserBean(ParserBean bean)
	throws RuntimeException
	{
		boolean hasError = false;
		hasError |= bean.acceptVisitor(nameAdder);
		grammar.constructPrecedenceRelationsGraph();
		hasError |= bean.acceptVisitor(attributeAdder);
		return hasError;
	}

	@Override
	public Boolean visitExtendedParserBean(ExtendedParserBean bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitProduction(Production bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitTerminal(Terminal bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitTerminalClass(TerminalClass bean)
	throws RuntimeException
	{
		throw new RuntimeException("This method should not be reached");
	}

	@Override
	public Boolean visitOperatorClass(OperatorClass bean)
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

	private Symbol generateName(Grammar bean)
	{
		return Symbol.symbol(bean.getName().toString());
	}
	
	private Symbol generateName(CopperASTBean bean)
	{
		if(currentParser.isUnitary()) return Symbol.symbol(bean.getName().toString());
		else return Symbol.symbol(currentGrammar.getName() + "$" + bean.getName());
	}
	
	private Symbol generateName(CopperElementReference ref)
	{
		if(currentParser.isUnitary())
		{
			if(!ref.isFQ()) return Symbol.symbol(ref.getName().toString());
			else return Symbol.symbol(ref.getName().toString());			
		}
		else
		{
			if(!ref.isFQ()) return Symbol.symbol(currentGrammar.getName() + "$" + ref.getName());
			else return Symbol.symbol(ref.getGrammarName() + "$" + ref.getName());
		}
	}
	
	private String transformCode(String code)
	{
		return code;
	}
}
