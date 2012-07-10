package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarName;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.LexicalAttributes;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.LexicalDisambiguationGroup;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminalAttributes;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.ParserSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.ProductionAttributes;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.TerminalClass;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CharacterSetRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ChoiceRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ConcatenationRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
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
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Choice;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.KleeneStar;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.MacroHole;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;

/**
 * This visitor builds a GrammarSource object out of a spec represented by a ParserBean object.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
class GrammarSourceBuilder implements CopperASTBeanVisitor<Boolean, RuntimeException>
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
	private GrammarBean currentGrammar;
	
	private class NameAdder implements CopperASTBeanVisitor<Boolean,RuntimeException>
	{
		@Override
		public Boolean visitDisambiguationFunctionBean(DisambiguationFunctionBean bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			if(bean.hasDisplayName()) grammar.setDisplayName(name,bean.getDisplayName());
			return false;
		}

		@Override
		public Boolean visitGrammarBean(GrammarBean bean)
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
		public Boolean visitExtensionGrammarBean(ExtensionGrammarBean bean)
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
		public Boolean visitNonTerminalBean(NonTerminalBean bean)
		throws RuntimeException
		{
			Symbol ntName = generateName(bean);
			grammar.addToNT(new NonTerminal(ntName));
			if(bean.hasDisplayName()) grammar.setDisplayName(ntName,bean.getDisplayName());
			return false;
		}

		@Override
		public Boolean visitParserAttributeBean(ParserAttributeBean bean)
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
		public Boolean visitProductionBean(ProductionBean bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			if(bean.hasDisplayName()) grammar.setDisplayName(name,bean.getDisplayName());
			return false;
		}

		@Override
		public Boolean visitTerminalBean(TerminalBean bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			Terminal t = new Terminal(name);
			grammar.addToT(t);
			if(bean.hasDisplayName()) grammar.setDisplayName(name,bean.getDisplayName());
			return false;
		}

		@Override
		public Boolean visitTerminalClassBean(TerminalClassBean bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			grammar.addTClass(new TerminalClass(name));
			if(bean.hasDisplayName()) grammar.setDisplayName(name,bean.getDisplayName());
			return false;
		}

		@Override
		public Boolean visitOperatorClassBean(OperatorClassBean bean)
		throws RuntimeException
		{
			return false;
		}
		
	}
	
	private class AttributeAdder implements CopperASTBeanVisitor<Boolean,RuntimeException>, RegexBeanVisitor<ParsedRegex,RuntimeException>
	{
		@Override
		public Boolean visitDisambiguationFunctionBean(DisambiguationFunctionBean bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			HashSet<Terminal> members = new HashSet<Terminal>();
			for(CopperElementReference n : bean.getMembers())
			{
				members.add(new Terminal(generateName(n)));
			}
			String code;
			if(bean.getCode() != null) code = transformCode(bean.getCode());
			else code = "return " + generateName(bean.getDisambiguateTo()) + ";";
			LexicalDisambiguationGroup group = new LexicalDisambiguationGroup(new TerminalClass(name), members, code);
			grammar.addDisambiguationGroup(group);
			
			return false;
		}
	
		@Override
		public Boolean visitGrammarBean(GrammarBean bean)
		throws RuntimeException
		{
			currentGrammar = bean;
			GrammarName grammarName = new GrammarName(generateName(bean));
			if(bean.getGrammarLayout() != null) for(CopperElementReference n : bean.getGrammarLayout()) grammar.addGrammarLayout(grammarName, new Terminal(generateName(n)));
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
			GrammarName grammarName = new GrammarName(generateName(bean));
			if(bean.getGrammarLayout() != null) for(CopperElementReference n : bean.getGrammarLayout()) grammar.addGrammarLayout(grammarName, new Terminal(generateName(n)));
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
		public Boolean visitNonTerminalBean(NonTerminalBean bean)
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
			grammar.addNTAttributes(new NonTerminal(ntName),attrs);
	
			return false;
		}
	
		@Override
		public Boolean visitParserAttributeBean(ParserAttributeBean bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			String code = transformCode(bean.getCode() == null ? "" : bean.getCode());
			grammar.addParserAttribute(new ParserAttribute(name,bean.getAttributeType(),code));
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
			grammar.setStartSym(new NonTerminal(generateName(bean.getStartSymbol())));
			if(bean.getStartLayout() != null)
			{
				for(CopperElementReference n : bean.getStartLayout()) grammar.addStartLayout(new Terminal(generateName(n)));
			}
			else if(bean.getGrammar(bean.getStartSymbol().getGrammarName()).getGrammarLayout() != null)
			{
				for(CopperElementReference n : bean.getGrammar(bean.getStartSymbol().getGrammarName()).getGrammarLayout()) grammar.addStartLayout(new Terminal(generateName(n)));
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
		public Boolean visitProductionBean(ProductionBean bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			Terminal operator = null;
			if(bean.getOperator() != null) operator = new Terminal(generateName(bean.getOperator()));
			NonTerminal lhs = new NonTerminal(generateName(bean.getLhs()));
			ArrayList<GrammarSymbol> rhs = new ArrayList<GrammarSymbol>();
			for(CopperElementReference n : bean.getRhs())
			{
				GrammarElementBean nBean = dereference(n);
				if(nBean.getType() == CopperElementType.TERMINAL) rhs.add(new Terminal(generateName(n))); 
				else /* if(nBean.getType() == CopperElementType.NON_TERMINAL) */ rhs.add(new NonTerminal(generateName(n))); 
			}
			Production prod = Production.production(name,operator,lhs,rhs);
			grammar.addToP(prod);
			
			LinkedList<String> vars = null;
			if(bean.getRhsVarNames() != null)
			{
				vars = new LinkedList<String>();
				vars.addAll(bean.getRhsVarNames());
			}
			
			HashSet<Terminal> layout = new HashSet<Terminal>();
			if(bean.getLayout() != null)
			{
				for(CopperElementReference n : bean.getLayout())
				{
					layout.add(new Terminal(generateName(n)));
				}
			}
			else if(currentGrammar.getGrammarLayout() != null)
			{
				for(CopperElementReference n : currentGrammar.getGrammarLayout())
				{
					layout.add(new Terminal(generateName(n)));
				}
			}
			
			TerminalClass productionClass;
			if(bean.getPrecedenceClass() == null) productionClass = new TerminalClass(FringeSymbols.STARTPROD_SYMBOL);
			else productionClass = new TerminalClass(generateName(bean.getPrecedenceClass()));

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
		public Boolean visitTerminalBean(TerminalBean bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			Terminal t = new Terminal(name);
			grammar.addRegex(t,bean.getRegex().acceptVisitor(this));
			String returnType = "Object";
			returnType = bean.getReturnType() == null ? "Object" : bean.getReturnType();
			Terminal prefix = FringeSymbols.EMPTY;
			String code = transformCode(bean.getCode() == null ? "" : bean.getCode());
			if(bean.getPrefix() != null) prefix = new Terminal(generateName(bean.getPrefix()));
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
				
				TerminalClass precClass;
				if(bean.getOperatorClass() == null) precClass = new TerminalClass(FringeSymbols.STARTPROD_SYMBOL);
				else precClass = new TerminalClass(generateName(bean.getOperatorClass()));
				
				OperatorAttributes operatorAttributes = new OperatorAttributes(associativityType,
						                                                       operatorPrecedence,
						                                                       precClass);
				grammar.addOperatorAttributes(t, operatorAttributes);
			}
			
			for(CopperElementReference n : bean.getSubmitList())
			{
				grammar.addStaticPrecedenceRelation(t,new Terminal(generateName(n)));
			}
			
			for(CopperElementReference n : bean.getDominateList())
			{
				grammar.addStaticPrecedenceRelation(new Terminal(generateName(n)),t);
			}
			
			return false;
		}
	
		@Override
		public Boolean visitTerminalClassBean(TerminalClassBean bean)
		throws RuntimeException
		{
			Symbol name = generateName(bean);
			TerminalClass tc = new TerminalClass(name);
			for(CopperElementReference n : bean.getMembers())
			{
				grammar.addToTClass(tc,new Terminal(generateName(n)));
			}
			return false;
		}

		@Override
		public Boolean visitOperatorClassBean(OperatorClassBean bean)
		throws RuntimeException
		{
			return false;
		}

		@Override
		public ParsedRegex visitChoiceRegex(ChoiceRegexBean bean)
		throws RuntimeException
		{
			ParsedRegex[] subexps = new ParsedRegex[bean.getSubexps().size()];
			int i = 0;
			for(RegexBean constit : bean.getSubexps()) subexps[i++] = constit.acceptVisitor(this);
			return new Choice(subexps);
		}

		@Override
		public ParsedRegex visitConcatenationRegex(ConcatenationRegexBean bean)
		throws RuntimeException
		{
			ParsedRegex[] subexps = new ParsedRegex[bean.getSubexps().size()];
			int i = 0;
			for(RegexBean constit : bean.getSubexps()) subexps[i++] = constit.acceptVisitor(this);
			return new Concatenation(subexps);
		}

		@Override
		public ParsedRegex visitKleeneStarRegex(KleeneStarRegexBean bean)
		throws RuntimeException
		{
			return new KleeneStar(bean.getSubexp().acceptVisitor(this));
		}

		@Override
		public ParsedRegex visitEmptyStringRegex(EmptyStringRegexBean bean)
		throws RuntimeException
		{
			return new EmptyString();
		}

		@Override
		public ParsedRegex visitCharacterSetRegex(CharacterSetRegexBean bean,SetOfCharsSyntax chars)
		throws RuntimeException
		{
			return CharacterSet.instantiate(chars);
		}

		@Override
		public ParsedRegex visitMacroHoleRegex(MacroHoleRegexBean bean)
		throws RuntimeException
		{
			return new MacroHole(new Terminal(generateName(bean.getMacroName())));
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

	@Override
	public Boolean visitOperatorClassBean(OperatorClassBean bean)
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

	private Symbol generateName(GrammarBean bean)
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
