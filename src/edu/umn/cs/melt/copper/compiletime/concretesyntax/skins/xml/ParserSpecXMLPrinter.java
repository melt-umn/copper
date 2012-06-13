package edu.umn.cs.melt.copper.compiletime.concretesyntax.skins.xml;

import java.io.PrintStream;
import java.util.TreeSet;

import org.xml.sax.SAXException;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CharacterSetRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ChoiceRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ConcatenationRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.DisambiguationFunctionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.EmptyStringRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ExtensionGrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarElementBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.KleeneStarRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.MacroHoleRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.OperatorAssociativity;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.OperatorClassBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserAttributeBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ProductionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.RegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalClassBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.RegexBeanVisitor;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.RegexSimplifier;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.auxiliary.xml.SAXWriter;
import edu.umn.cs.melt.copper.compiletime.auxiliary.xml.SAXWriterImpl;

/**
 * Prints out a parser spec in XML.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ParserSpecXMLPrinter implements CopperASTBeanVisitor<Boolean,SAXException>,RegexBeanVisitor<Boolean,SAXException>
{
	private ParserBean currentParser;
	private GrammarBean currentGrammar;
	private RegexSimplifier regexSimplifier;
	private SAXWriter out;
	
	public ParserSpecXMLPrinter(PrintStream out)
	{
		this(out,"\t");
	}
	
	public ParserSpecXMLPrinter(PrintStream out,String ppIndentation)
	{
		this.out = new SAXWriterImpl(out,ppIndentation,false);
		regexSimplifier = new RegexSimplifier();
		currentParser = null;
		currentGrammar = null;
	}

	@Override
	public Boolean visitDisambiguationFunctionBean(DisambiguationFunctionBean bean)
	throws SAXException
	{
		startElement(XMLSkinElements.Type.DISAMBIGUATION_FUNCTION_ELEMENT,"id",bean.getName().toString());
		if(bean.hasDisplayName())
		{
			startElement(XMLSkinElements.Type.PP_ELEMENT);
			out.string(bean.getDisplayName());
			endElement(XMLSkinElements.Type.PP_ELEMENT);
		}
		startElement(XMLSkinElements.Type.MEMBERS_ELEMENT);
		TreeSet<CopperElementReference> members = new TreeSet<CopperElementReference>(bean.getMembers());
		for(CopperElementReference member : members)
		{
			writeGrammarElementRef(member);
		}
		endElement(XMLSkinElements.Type.MEMBERS_ELEMENT);
		if(bean.getDisambiguateTo() != null)
		{
			startElement(XMLSkinElements.Type.DISAMBIGUATE_TO_ELEMENT);
			writeGrammarElementRef(bean.getDisambiguateTo());
			endElement(XMLSkinElements.Type.DISAMBIGUATE_TO_ELEMENT);
		}
		else
		{
			startElement(XMLSkinElements.Type.CODE_ELEMENT);
			out.verbatimString(bean.getCode());
			endElement(XMLSkinElements.Type.CODE_ELEMENT);
		}
		endElement(XMLSkinElements.Type.DISAMBIGUATION_FUNCTION_ELEMENT);
		return false;
	}

	@Override
	public Boolean visitGrammarBean(GrammarBean bean)
	throws SAXException
	{
		currentGrammar = bean;
		startElement(XMLSkinElements.Type.GRAMMAR_ELEMENT,"id",bean.getName().toString());
		if(bean.hasDisplayName())
		{
			startElement(XMLSkinElements.Type.PP_ELEMENT);
			out.string(bean.getDisplayName());
			endElement(XMLSkinElements.Type.PP_ELEMENT);
		}
		if(bean.getGrammarLayout() != null)
		{
			startElement(XMLSkinElements.Type.LAYOUT_ELEMENT);
			for(CopperElementReference layout : bean.getGrammarLayout())
			{
				writeGrammarElementRef(layout);
			}
			endElement(XMLSkinElements.Type.LAYOUT_ELEMENT);
		}
		startElement(XMLSkinElements.Type.DECLARATIONS_ELEMENT);
		boolean hasError = false;
		TreeSet<CopperElementName> elements = new TreeSet<CopperElementName>(bean.getGrammarElements()); 
		for(CopperElementName n : elements)
		{
			hasError |= bean.getGrammarElement(n).acceptVisitor(this);
		}
		endElement(XMLSkinElements.Type.DECLARATIONS_ELEMENT);
		endElement(XMLSkinElements.Type.GRAMMAR_ELEMENT);
		return hasError;			
	}

	@Override
	public Boolean visitExtensionGrammarBean(ExtensionGrammarBean bean)
	throws SAXException
	{
		throw new SAXException("Extension grammars not yet supported in XML");
	}

	@Override
	public Boolean visitNonTerminalBean(NonTerminalBean bean)
	throws SAXException
	{
		startElement(XMLSkinElements.Type.NONTERMINAL_ELEMENT,"id",bean.getName().toString());
		if(bean.hasDisplayName())
		{
			startElement(XMLSkinElements.Type.PP_ELEMENT);
			out.string(bean.getDisplayName());
			endElement(XMLSkinElements.Type.PP_ELEMENT);
		}
		if(bean.getReturnType() != null && !bean.getReturnType().isEmpty())
		{
			startElement(XMLSkinElements.Type.TYPE_ELEMENT);
			out.verbatimString(bean.getReturnType());
			endElement(XMLSkinElements.Type.TYPE_ELEMENT);
		}
		endElement(XMLSkinElements.Type.NONTERMINAL_ELEMENT);
		return false;
	}

	@Override
	public Boolean visitParserAttributeBean(ParserAttributeBean bean)
	throws SAXException
	{
		startElement(XMLSkinElements.Type.PARSER_ATTRIBUTE_ELEMENT,"id",bean.getName().toString());
		if(bean.hasDisplayName())
		{
			startElement(XMLSkinElements.Type.PP_ELEMENT);
			out.string(bean.getDisplayName());
			endElement(XMLSkinElements.Type.PP_ELEMENT);
		}
		startElement(XMLSkinElements.Type.TYPE_ELEMENT);
		out.verbatimString(bean.getAttributeType());
		endElement(XMLSkinElements.Type.TYPE_ELEMENT);
		startElement(XMLSkinElements.Type.CODE_ELEMENT);
		out.verbatimString(bean.getCode());
		endElement(XMLSkinElements.Type.CODE_ELEMENT);
		endElement(XMLSkinElements.Type.PARSER_ATTRIBUTE_ELEMENT);
		return false;
	}

	@Override
	public Boolean visitParserBean(ParserBean bean)
	throws SAXException
	{
		currentParser = bean;
		out.startDocument();
		startElement(XMLSkinElements.Type.COPPER_SPEC_ELEMENT,"xmlns",XMLSkinElements.COPPER_NAMESPACE);
		startElement(XMLSkinElements.Type.PARSER_ELEMENT,"id", bean.getName().toString(), "isUnitary", (bean.isUnitary() ? "true" : "false"));
		
		if(bean.hasDisplayName())
		{
			startElement(XMLSkinElements.Type.PP_ELEMENT);
			out.string(bean.getDisplayName());
			endElement(XMLSkinElements.Type.PP_ELEMENT);
		}
		
		startElement(XMLSkinElements.Type.GRAMMARS_ELEMENT);
		TreeSet<CopperElementName> grammars = new TreeSet<CopperElementName>(bean.getGrammars());
		for(CopperElementName grammar : grammars)
		{
			writeFullElement(XMLSkinElements.Type.GRAMMAR_REF_ELEMENT,"id",grammar.toString());	
		}
		endElement(XMLSkinElements.Type.GRAMMARS_ELEMENT);
		
		startElement(XMLSkinElements.Type.START_SYMBOL_ELEMENT);
		writeGrammarElementRef(bean.getStartSymbol());
		endElement(XMLSkinElements.Type.START_SYMBOL_ELEMENT);

		if(bean.getStartLayout() != null)
		{
			startElement(XMLSkinElements.Type.START_LAYOUT_ELEMENT);
			for(CopperElementReference layout : bean.getStartLayout())
			{
				writeGrammarElementRef(layout);
			}
			endElement(XMLSkinElements.Type.START_LAYOUT_ELEMENT);
		}
		
		if(bean.getPackageDecl() != null && !bean.getPackageDecl().isEmpty())
		{
			startElement(XMLSkinElements.Type.PACKAGE_ELEMENT);
			out.string(bean.getPackageDecl());
			endElement(XMLSkinElements.Type.PACKAGE_ELEMENT);
		}
		if(bean.getClassName() != null && !bean.getClassName().isEmpty())
		{
			startElement(XMLSkinElements.Type.CLASS_NAME_ELEMENT);
			out.string(bean.getClassName());
			endElement(XMLSkinElements.Type.CLASS_NAME_ELEMENT);
		}
		if(bean.getParserClassAuxCode() != null && !bean.getParserClassAuxCode().isEmpty())
		{
			startElement(XMLSkinElements.Type.CLASS_AUXILIARY_CODE_ELEMENT);
			startElement(XMLSkinElements.Type.CODE_ELEMENT);
			out.verbatimString(bean.getParserClassAuxCode());
			endElement(XMLSkinElements.Type.CODE_ELEMENT);
			endElement(XMLSkinElements.Type.CLASS_AUXILIARY_CODE_ELEMENT);
		}
		if(bean.getDefaultProductionCode() != null && !bean.getDefaultProductionCode().isEmpty())
		{
			startElement(XMLSkinElements.Type.DEFAULT_PRODUCTION_CODE_ELEMENT);
			startElement(XMLSkinElements.Type.CODE_ELEMENT);
			out.verbatimString(bean.getDefaultProductionCode());
			endElement(XMLSkinElements.Type.CODE_ELEMENT);
			endElement(XMLSkinElements.Type.DEFAULT_PRODUCTION_CODE_ELEMENT);
		}
		if(bean.getDefaultTerminalCode() != null && !bean.getDefaultTerminalCode().isEmpty())
		{
			startElement(XMLSkinElements.Type.DEFAULT_TERMINAL_CODE_ELEMENT);
			startElement(XMLSkinElements.Type.CODE_ELEMENT);
			out.verbatimString(bean.getDefaultTerminalCode());
			endElement(XMLSkinElements.Type.CODE_ELEMENT);
			endElement(XMLSkinElements.Type.DEFAULT_TERMINAL_CODE_ELEMENT);
		}
		if(bean.getParserInitCode() != null && !bean.getParserInitCode().isEmpty())
		{
			startElement(XMLSkinElements.Type.PARSER_INIT_CODE_ELEMENT);
			startElement(XMLSkinElements.Type.CODE_ELEMENT);
			out.verbatimString(bean.getParserInitCode());
			endElement(XMLSkinElements.Type.CODE_ELEMENT);
			endElement(XMLSkinElements.Type.PARSER_INIT_CODE_ELEMENT);
		}
		if(bean.getPostParseCode() != null && !bean.getPostParseCode().isEmpty())
		{
			startElement(XMLSkinElements.Type.POST_PARSE_CODE_ELEMENT);
			startElement(XMLSkinElements.Type.CODE_ELEMENT);
			out.verbatimString(bean.getPostParseCode());
			endElement(XMLSkinElements.Type.CODE_ELEMENT);
			endElement(XMLSkinElements.Type.POST_PARSE_CODE_ELEMENT);
		}
		if(bean.getPreambleCode() != null && !bean.getPreambleCode().isEmpty())
		{
			startElement(XMLSkinElements.Type.PREAMBLE_ELEMENT);
			startElement(XMLSkinElements.Type.CODE_ELEMENT);
			out.verbatimString(bean.getPreambleCode());
			endElement(XMLSkinElements.Type.CODE_ELEMENT);
			endElement(XMLSkinElements.Type.PREAMBLE_ELEMENT);
		}
		if(bean.getSemanticActionAuxCode() != null && !bean.getSemanticActionAuxCode().isEmpty())
		{
			startElement(XMLSkinElements.Type.SEMANTIC_ACTION_AUXILIARY_CODE_ELEMENT);
			startElement(XMLSkinElements.Type.CODE_ELEMENT);
			out.verbatimString(bean.getSemanticActionAuxCode());
			endElement(XMLSkinElements.Type.CODE_ELEMENT);
			endElement(XMLSkinElements.Type.SEMANTIC_ACTION_AUXILIARY_CODE_ELEMENT);
		}

		endElement(XMLSkinElements.Type.PARSER_ELEMENT);

		boolean hasError = false;
		for(CopperElementName n : grammars)
		{
			hasError |= bean.getGrammar(n).acceptVisitor(this);
		}
		endElement(XMLSkinElements.Type.COPPER_SPEC_ELEMENT);
		currentParser = null;
		return hasError;
	}

	@Override
	public Boolean visitExtendedParserBean(ExtendedParserBean bean)
	throws SAXException
	{
		return visitParserBean(bean);
	}

	@Override
	public Boolean visitProductionBean(ProductionBean bean)
	throws SAXException
	{
		startElement(XMLSkinElements.Type.PRODUCTION_ELEMENT,"id",bean.getName().toString());
		if(bean.hasDisplayName())
		{
			startElement(XMLSkinElements.Type.PP_ELEMENT);
			out.string(bean.getDisplayName());
			endElement(XMLSkinElements.Type.PP_ELEMENT);
		}
		if(bean.getPrecedenceClass() != null)
		{
			startElement(XMLSkinElements.Type.CLASS_ELEMENT);
			writeGrammarElementRef(bean.getPrecedenceClass());
			endElement(XMLSkinElements.Type.CLASS_ELEMENT);
		}
		
		if(bean.getPrecedence() != null && bean.getPrecedence() != -1)
		{
			startElement(XMLSkinElements.Type.PRECEDENCE_ELEMENT);
			out.string(String.valueOf(bean.getPrecedence()));
			endElement(XMLSkinElements.Type.PRECEDENCE_ELEMENT);			
		}
		
		if(bean.getCode() != null && !bean.getCode().isEmpty())
		{
			startElement(XMLSkinElements.Type.CODE_ELEMENT);
			out.verbatimString(bean.getCode());
			endElement(XMLSkinElements.Type.CODE_ELEMENT);						
		}
		
		startElement(XMLSkinElements.Type.LHS_ELEMENT);
		writeGrammarElementRef(bean.getLhs());
		endElement(XMLSkinElements.Type.LHS_ELEMENT);
		
		startElement(XMLSkinElements.Type.RHS_ELEMENT);
		for(int i = 0;i < bean.getRhs().size();i++)
		{
			writeGrammarElementRef(bean.getRhs().get(i),bean.getRhsVarNames().get(i),false);
		}
		endElement(XMLSkinElements.Type.RHS_ELEMENT);
		
		if(bean.getLayout() != null)
		{
			startElement(XMLSkinElements.Type.LAYOUT_ELEMENT);
			for(CopperElementReference layout : bean.getLayout())
			{
				writeGrammarElementRef(layout);
			}
			endElement(XMLSkinElements.Type.LAYOUT_ELEMENT);
		}
		
		if(bean.getOperator() != null)
		{
			startElement(XMLSkinElements.Type.OPERATOR_ELEMENT);
			writeGrammarElementRef(bean.getOperator());
			endElement(XMLSkinElements.Type.OPERATOR_ELEMENT);
		}
		endElement(XMLSkinElements.Type.PRODUCTION_ELEMENT);
		return false;
	}

	@Override
	public Boolean visitTerminalBean(TerminalBean bean)
			throws SAXException
	{
		startElement(XMLSkinElements.Type.TERMINAL_ELEMENT,"id",bean.getName().toString());
		if(bean.hasDisplayName())
		{
			startElement(XMLSkinElements.Type.PP_ELEMENT);
			out.string(bean.getDisplayName());
			endElement(XMLSkinElements.Type.PP_ELEMENT);
		}
		
		startElement(XMLSkinElements.Type.REGEX_ELEMENT);
		bean.getRegex().acceptVisitor(regexSimplifier).acceptVisitor(this);
		endElement(XMLSkinElements.Type.REGEX_ELEMENT);

		if(bean.getOperatorClass() != null ||
		   (bean.getOperatorPrecedence() != null && bean.getOperatorPrecedence() != -1) ||
		   (bean.getOperatorAssociativity() != null && bean.getOperatorAssociativity() != OperatorAssociativity.NONE))
		{
			startElement(XMLSkinElements.Type.OPERATOR_ELEMENT);
			if(bean.getOperatorClass() != null)
			{
				startElement(XMLSkinElements.Type.CLASS_ELEMENT);
				writeGrammarElementRef(bean.getOperatorClass());
				endElement(XMLSkinElements.Type.CLASS_ELEMENT);
			}
			if(bean.getOperatorPrecedence() != null && bean.getOperatorPrecedence() != -1)
			{
				startElement(XMLSkinElements.Type.PRECEDENCE_ELEMENT);
				out.string(String.valueOf(bean.getOperatorPrecedence()));
				endElement(XMLSkinElements.Type.PRECEDENCE_ELEMENT);
			}
			if(bean.getOperatorAssociativity() != null && bean.getOperatorAssociativity() != OperatorAssociativity.NONE)
			{
				switch(bean.getOperatorAssociativity())
				{
				case LEFT:
					writeFullElement(XMLSkinElements.Type.LEFT_ASSOCIATIVE_ELEMENT);
					break;
				case NONASSOC:
					writeFullElement(XMLSkinElements.Type.NON_ASSOCIATIVE_ELEMENT);
					break;
				case RIGHT:
					writeFullElement(XMLSkinElements.Type.RIGHT_ASSOCIATIVE_ELEMENT);
					break;
				}
			}
			endElement(XMLSkinElements.Type.OPERATOR_ELEMENT);
		}
		if(bean.getReturnType() != null)
		{
			startElement(XMLSkinElements.Type.TYPE_ELEMENT);
			out.string(bean.getReturnType());
			endElement(XMLSkinElements.Type.TYPE_ELEMENT);
		}

		if(bean.getCode() != null && !bean.getCode().isEmpty())
		{
			startElement(XMLSkinElements.Type.CODE_ELEMENT);
			out.verbatimString(bean.getCode());
			endElement(XMLSkinElements.Type.CODE_ELEMENT);						
		}

		if(bean.getTerminalClasses() != null)
		{
			startElement(XMLSkinElements.Type.IN_CLASSES_ELEMENT);
			TreeSet<CopperElementReference> members = new TreeSet<CopperElementReference>(bean.getTerminalClasses());
			for(CopperElementReference member : members)
			{
				writeGrammarElementRef(member);
			}
			endElement(XMLSkinElements.Type.IN_CLASSES_ELEMENT);
		}
		
		if(bean.getPrefix() != null)
		{
			startElement(XMLSkinElements.Type.PREFIX_ELEMENT);
			writeGrammarElementRef(bean.getPrefix());
			endElement(XMLSkinElements.Type.PREFIX_ELEMENT);
		}
		
		if(bean.getSubmitList() != null)
		{
			startElement(XMLSkinElements.Type.SUBMITS_ELEMENT);
			TreeSet<CopperElementReference> members = new TreeSet<CopperElementReference>(bean.getSubmitList());
			for(CopperElementReference member : members)
			{
				writeGrammarElementRef(member);
			}
			endElement(XMLSkinElements.Type.SUBMITS_ELEMENT);
		}
		
		if(bean.getDominateList() != null)
		{
			startElement(XMLSkinElements.Type.DOMINATES_ELEMENT);
			TreeSet<CopperElementReference> members = new TreeSet<CopperElementReference>(bean.getDominateList());
			for(CopperElementReference member : members)
			{
				writeGrammarElementRef(member);
			}
			endElement(XMLSkinElements.Type.DOMINATES_ELEMENT);
		}
		
		endElement(XMLSkinElements.Type.TERMINAL_ELEMENT);
		return false;
	}

	@Override
	public Boolean visitTerminalClassBean(TerminalClassBean bean)
	throws SAXException
	{
		startElement(XMLSkinElements.Type.TERMINAL_CLASS_ELEMENT,"id",bean.getName().toString());
		if(bean.hasDisplayName())
		{
			startElement(XMLSkinElements.Type.PP_ELEMENT);
			out.string(bean.getDisplayName());
			endElement(XMLSkinElements.Type.PP_ELEMENT);
		}
		if(bean.getMembers() != null && !bean.getMembers().isEmpty())
		{
			startElement(XMLSkinElements.Type.MEMBERS_ELEMENT);
			TreeSet<CopperElementReference> members = new TreeSet<CopperElementReference>(bean.getMembers());
			for(CopperElementReference member : members)
			{
				writeGrammarElementRef(member);
			}
			endElement(XMLSkinElements.Type.MEMBERS_ELEMENT);
		}
		endElement(XMLSkinElements.Type.TERMINAL_CLASS_ELEMENT);
		return false;
	}

	@Override
	public Boolean visitOperatorClassBean(OperatorClassBean bean)
	throws SAXException
	{
		startElement(XMLSkinElements.Type.OPERATOR_CLASS_ELEMENT,"id",bean.getName().toString());
		if(bean.hasDisplayName())
		{
			startElement(XMLSkinElements.Type.PP_ELEMENT);
			out.string(bean.getDisplayName());
			endElement(XMLSkinElements.Type.PP_ELEMENT);
		}
		endElement(XMLSkinElements.Type.OPERATOR_CLASS_ELEMENT);
		return false;
	}
	
	@Override
	public Boolean visitChoiceRegex(ChoiceRegexBean bean)
	throws SAXException
	{
		startElement(XMLSkinElements.Type.CHOICE_ELEMENT);
		for(RegexBean subexp : bean.getSubexps())
		{
			subexp.acceptVisitor(this);
		}
		endElement(XMLSkinElements.Type.CHOICE_ELEMENT);
		return false;
	}

	@Override
	public Boolean visitConcatenationRegex(ConcatenationRegexBean bean)
	throws SAXException
	{
		startElement(XMLSkinElements.Type.CONCATENATION_ELEMENT);
		for(RegexBean subexp : bean.getSubexps())
		{
			subexp.acceptVisitor(this);
		}
		endElement(XMLSkinElements.Type.CONCATENATION_ELEMENT);
		return false;
	}

	@Override
	public Boolean visitKleeneStarRegex(KleeneStarRegexBean bean)
	throws SAXException
	{
		startElement(XMLSkinElements.Type.KLEENE_STAR_ELEMENT);
		bean.getSubexp().acceptVisitor(this);
		endElement(XMLSkinElements.Type.KLEENE_STAR_ELEMENT);
		return false;
	}

	@Override
	public Boolean visitEmptyStringRegex(EmptyStringRegexBean bean)
	throws SAXException
	{
		writeFullElement(XMLSkinElements.Type.EMPTY_STRING_REGEX_ELEMENT);
		return false;
	}

	@Override
	public Boolean visitCharacterSetRegex(CharacterSetRegexBean bean,SetOfCharsSyntax chars)
	throws SAXException
	{
		SetOfCharsSyntax modChars;
		if(chars.getMembers()[0][0] == Character.MIN_VALUE ||
		   chars.getMembers()[chars.getMembers().length - 1][1] == Character.MAX_VALUE)
		{
			//System.err.println(chars + ", inverted: " + chars.invert());
			startElement(XMLSkinElements.Type.CHARACTER_SET_ELEMENT,"invert","true");
			modChars = chars.invert();
			//modChars = chars;
		}
		else
		{
			startElement(XMLSkinElements.Type.CHARACTER_SET_ELEMENT);
			modChars = chars;
		}
		for(int i = 0;i < modChars.getMembers().length;i++)
		{
			if(modChars.getMembers()[i][0] == modChars.getMembers()[i][1])
			{
				writeFullElement(XMLSkinElements.Type.SINGLE_CHARACTER_ELEMENT,"char",String.valueOf(modChars.getMembers()[i][0]));
			}
			else
			{
				writeFullElement(XMLSkinElements.Type.CHARACTER_RANGE_ELEMENT,
						"lower",String.valueOf(modChars.getMembers()[i][0]),
						"upper",String.valueOf(modChars.getMembers()[i][1]));
			}
		}
		endElement(XMLSkinElements.Type.CHARACTER_SET_ELEMENT);
		return false;
	}

	@Override
	public Boolean visitMacroHoleRegex(MacroHoleRegexBean bean)
	throws SAXException
	{
		writeRegexMacroRef(bean.getMacroName());
		return false;
	}

	private void startElement(XMLSkinElements.Type element,String... attrs)
	throws SAXException
	{
		out.startElement(element.getNamespace(),element.getName(),element.getName(),false,attrs);
	}
	
	private void endElement(XMLSkinElements.Type element)
	throws SAXException
	{
		out.endElement(element.getNamespace(),element.getName(),element.getName());		
	}
	
	private void writeFullElement(XMLSkinElements.Type element,String... attrs)
	throws SAXException
	{
		out.writeFullElement(element.getNamespace(),element.getName(),element.getName(),false,attrs);
	}
	
	private void writeGrammarElementRef(CopperElementReference ref)
	throws SAXException
	{
		writeGrammarElementRef(ref,null,false);
	}
	
	private void writeRegexMacroRef(CopperElementReference ref)
	throws SAXException
	{
		writeGrammarElementRef(ref,null,true);
	}
	
	private void writeGrammarElementRef(CopperElementReference ref,String name,boolean isMacro)
	throws SAXException
	{
		boolean writeGrammarAttr = currentGrammar == null || !ref.getGrammarName().equals(currentGrammar.getName());
		XMLSkinElements.Type refType;
		if(isMacro) refType = XMLSkinElements.Type.MACRO_REF_ELEMENT;
		else switch(dereference(ref).getType())
		{
		case EXTENSION_GRAMMAR:
		case GRAMMAR:
			refType = XMLSkinElements.Type.GRAMMAR_REF_ELEMENT;
			break;
		case NON_TERMINAL:
			refType = XMLSkinElements.Type.NONTERMINAL_REF_ELEMENT;
			break;
		case OPERATOR_CLASS:
			refType = XMLSkinElements.Type.OPERATOR_CLASS_REF_ELEMENT;
			break;
		case TERMINAL:
			refType = XMLSkinElements.Type.TERMINAL_REF_ELEMENT;
			break;
		case TERMINAL_CLASS:
			refType = XMLSkinElements.Type.TERMINAL_CLASS_REF_ELEMENT;
			break;
		default:
			return;
		}
		if(writeGrammarAttr)
		{
			if(name != null) writeFullElement(refType,"grammar",ref.getGrammarName().toString(),"id",ref.getName().toString(),"name",name);
			else writeFullElement(refType,"grammar",ref.getGrammarName().toString(),"id",ref.getName().toString());
		}
		else
		{
			if(name != null) writeFullElement(refType,"id",ref.getName().toString(),"name",name);
			else writeFullElement(refType,"id",ref.getName().toString());
		}
	}
	
	// Assumes that nameIsDefined(symbol) == true.
	private GrammarElementBean dereference(CopperElementReference symbol)
	{
		if(!symbol.isFQ()) return currentGrammar.getGrammarElement(symbol.getName());
		else return currentParser.getGrammar(symbol.getGrammarName()).getGrammarElement(symbol.getName());
	}
}
