package edu.umn.cs.melt.copper.compiletime.concretesyntax.skins.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CharacterSetRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ChoiceRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ConcatenationRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperElementReference;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.DisambiguationFunctionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.EmptyStringRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.GrammarBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.KleeneStarRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.MacroHoleRegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.NonTerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.OperatorAssociativity;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserAttributeBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ProductionBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.RegexBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.TerminalClassBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.ParserSpecProcessor;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class XMLSkinParser extends DefaultHandler 
{
	private static final String COPPER_NAMESPACE = "http://melt.cs.umn.edu/copper/xmlns";
	private static enum XMLSkinElementType
	{
		CHARACTER_RANGE_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "CharacterRange"; } },
		CHARACTER_SET_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "CharacterSet"; } },
		CHOICE_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Choice"; } },
		CLASS_AUXILIARY_CODE_ELEMENT			{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "ClassAuxiliaryCode"; } },
		CLASS_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Class"; } },
		CLASS_NAME_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "ClassName"; } },
		CODE_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Code"; } },
		CONCATENATION_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Concatenation"; } },
		COPPER_SPEC_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "CopperSpec"; } },
		DECLARATIONS_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Declarations"; } },
		DEFAULT_TERMINAL_CODE_ELEMENT			{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "DefaultTerminalCode"; } },
		DEFAULT_PRODUCTION_CODE_ELEMENT			{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "DefaultProductionCode"; } },
		DISAMBIGUATE_TO_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "DisambiguateTo"; } },
		DISAMBIGUATION_FUNCTION_ELEMENT			{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "DisambiguationFunction"; } },
		DOMINATES_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Dominates"; } },
		EMPTY_STRING_REGEX_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "EmptyString"; } },
		GRAMMARS_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Grammars"; } },
		GRAMMAR_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Grammar"; } },
		GRAMMAR_REF_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "GrammarRef"; } },
		IN_CLASSES_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "InClasses"; } },
		KLEENE_STAR_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "KleeneStar"; } },
		LAYOUT_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Layout"; } },
		LEFT_ASSOCIATIVE_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "LeftAssociative"; } },
		LHS_ELEMENT								{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "LHS"; } },
		MACRO_REF_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "MacroRef"; } },
		MEMBERS_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Members"; } },
		NON_ASSOCIATIVE_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "NonAssociative"; } },
		NONTERMINAL_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Nonterminal"; } },
		NONTERMINAL_REF_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "NonterminalRef"; } },
		OPERATOR_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Operator"; } },
		PACKAGE_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Package"; } },
		PARSER_ATTRIBUTE_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "ParserAttribute"; } },
		PARSER_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Parser"; } },
		PARSER_INIT_CODE_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "ParserInitCode"; } },
		POST_PARSE_CODE_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "PostParseCode"; } },
		PP_ELEMENT								{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "PP"; } },
		PREAMBLE_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Preamble"; } },
		PRECEDENCE_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Precedence"; } },
		PREFIX_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Prefix"; } },
		PRODUCTION_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Production"; } },
		REGEX_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Regex"; } },
		RHS_ELEMENT								{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "RHS"; } },
		RIGHT_ASSOCIATIVE_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "RightAssociative"; } },
		SEMANTIC_ACTION_AUXILIARY_CODE_ELEMENT	{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "SemanticActionAuxiliaryCode"; } },
		SINGLE_CHARACTER_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "SingleCharacter"; } },
		START_LAYOUT_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "StartLayout"; } },
		START_SYMBOL_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "StartSymbol"; } },
		SUBMITS_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Submits"; } },
		TERMINAL_CLASS_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "TerminalClass"; } },
		TERMINAL_CLASS_REF_ELEMENT				{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "TerminalClassRef"; } },
		TERMINAL_ELEMENT						{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Terminal"; } },
		TERMINAL_REF_ELEMENT					{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "TerminalRef"; } },
		TYPE_ELEMENT							{ public String getNamespace() { return COPPER_NAMESPACE; } public String getName() { return "Type"; } };
		
		public abstract String getNamespace();
		public abstract String getName();

		public String toString() { return getName(); }
	}
	
	private static class SAXStackElement
	{
		public XMLSkinElementType type;
		public InputPosition startLocation;
		public ArrayList<RegexBean> regexChildren;
		public boolean invertCharacterSet;
		
		public SAXStackElement(XMLSkinElementType type,InputPosition startLocation)
		{
			this.type = type;
			this.startLocation = startLocation;
			this.regexChildren = null;
			this.invertCharacterSet = false;
		}
	}
	
	public static Hashtable<String,XMLSkinElementType> nodeTypes;
	
	static
	{
		nodeTypes = new Hashtable<String,XMLSkinElementType>();
		for(XMLSkinElementType t : XMLSkinElementType.values()) nodeTypes.put(t.getName(),t);
	}
	
	public static void formalError(CompilerLogger logger,InputPosition pos,String message)
	throws CopperException
	{
		if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,pos,message);
	}

	private ArrayList< Pair<String,Reader> > files;
	private CompilerLogger logger;
	private SAXStackElement[] saxStack;
	private int saxStackPointer;
	
    public XMLSkinParser(ArrayList< Pair<String,Reader> > files,CompilerLogger logger)
    throws IOException,CopperException
    {
    	this.files = files;
    	this.logger = logger;
    }
    
    private void push(SAXStackElement e)
    {
    	if(saxStackPointer + 1 >= saxStack.length)
    	{
    		SAXStackElement[] newSAXStack = new SAXStackElement[saxStack.length * 2];
    		System.arraycopy(saxStack,0,newSAXStack,0,saxStack.length);
    		saxStack = newSAXStack;
    	}
    	saxStack[++saxStackPointer] = e;
    }
    
    private SAXStackElement peek()
    {
    	return saxStack[saxStackPointer];
    }
    
    private SAXStackElement pop()
    {
    	if(saxStackPointer == -1) return null;
    	return saxStack[saxStackPointer--];
    }
    
    /*private void printStack()
    {
    	for(int i = 0;i <= saxStackPointer;i++)
    	{
    		System.err.print(saxStack[i].type);
    		if(i != saxStackPointer) System.err.print(", ");
    	}
    	System.err.println();
    }*/

	public ParserBean parse()
	throws CopperException
	{
    	this.saxStack = new SAXStackElement[32];
    	this.saxStackPointer = -1;
    	
    	Schema schema = null;
    	SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		InputStream schemaFile = this.getClass().getClassLoader().getResourceAsStream("etc/XMLSkinSchema.xsd");
		if(schemaFile == null) if(logger.isLoggable(CompilerLogMessageSort.PARSING_ERROR)) logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,null,"Cannot load XML skin schema. This generally means Copper was improperly built.");
    	try
    	{
    		schema = schemaFactory.newSchema(new StreamSource(new InputStreamReader(schemaFile)));
    	}
		catch(SAXException ex)
		{
			if(logger.isLoggable(CompilerLogMessageSort.PARSING_ERROR)) logger.logMessage(CompilerLogMessageSort.PARSING_ERROR,null,"Schema parse error: " + ex.getMessage());
		}

    	SAXParserFactory factory = SAXParserFactory.newInstance();
    	factory.setNamespaceAware(true);
    	factory.setSchema(schema);
		
		SAXParser parser = null;
		
		try
		{
			parser = factory.newSAXParser();
		}
		catch(SAXException ex)
		{
			if(logger.isLoggable(CompilerLogMessageSort.PARSING_ERROR)) logger.logMessage(CompilerLogMessageSort.PARSING_ERROR,null,ex.getMessage());
		}
		catch(ParserConfigurationException ex)
		{
			if(logger.isLoggable(CompilerLogMessageSort.PARSING_ERROR)) logger.logMessage(CompilerLogMessageSort.PARSING_ERROR,null,ex.getMessage());
		}


		for(Pair<String,Reader> file : files)
		{
			try
			{
				loc = InputPosition.initialPos(file.first());
				InputSource s = new InputSource(file.second()); 
				parser.parse(s,this);
			}
			catch (IOException ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.PARSING_ERROR)) logger.logMessage(CompilerLogMessageSort.PARSING_ERROR,loc,ex.getMessage());
			}
			catch (SAXException ex)
			{
				if(logger.isLoggable(CompilerLogMessageSort.PARSING_ERROR)) logger.logMessage(CompilerLogMessageSort.PARSING_ERROR,loc,ex.getMessage());
			}
		}
		
		if(foundMoreThanOneParser)
		{
			if(logger.isLoggable(CompilerLogMessageSort.PARSING_ERROR)) logger.logMessage(CompilerLogMessageSort.PARSING_ERROR,(InputPosition) currentParser.getLocation(),"Superfluous parser " + currentParser.getDisplayName() +": spec must contain exactly one parser element");			
		}
		logger.flushMessages();
		ParserSpecProcessor.normalizeParser(currentParser,logger);
		return currentParser;
	}
	
	private InputPosition loc;
	private Locator locator;
	private Hashtable<CopperElementName,GrammarBean> grammars = new Hashtable<CopperElementName,GrammarBean>();
	private boolean foundMoreThanOneParser = false;
	private ParserBean currentParser = null;
	private GrammarBean currentGrammar = null;
	private TerminalBean currentTerminal = null;
	private TerminalClassBean currentTerminalClass = null;
	private NonTerminalBean currentNonTerminal = null;
	private ProductionBean currentProduction = null;
	private DisambiguationFunctionBean currentDisambiguationFunction = null;
	private ParserAttributeBean currentParserAttribute = null;
	private ArrayList<CopperElementReference> refList = null;
	private Set<CopperElementReference> refSet = null;
	private ArrayList<String> varNames = null;
	private String nodeText = null;
	
	@Override
	public void setDocumentLocator(Locator locator)
	{
		this.locator = locator;
	}
	
	@Override
	public void warning(SAXParseException ex)
	throws SAXParseException
	{
		loc = InputPosition.fromSAXLocator(loc,locator);
		throw ex;
	}

	@Override
	public void error(SAXParseException ex)
	throws SAXParseException
	{
		loc = InputPosition.fromSAXLocator(loc,locator);
		throw ex;
	}
	
	@Override
	public void fatalError(SAXParseException ex)
	throws SAXParseException
	{
		loc = InputPosition.fromSAXLocator(loc,locator);
		throw ex;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
	throws SAXException
	{
		try
		{
			startElementInner(uri,localName,qName,attributes);
		}
		catch(ParseException ex)
		{
			error(new SAXParseException(ex.getMessage(),locator));			
		}
		catch(CopperException ex)
		{
			error(new SAXParseException(ex.getMessage(),locator));			
		}
	}
	
	private void startElementInner(String uri,String localName,String qName,Attributes attributes)
	throws ParseException,CopperException
	{
		push(new SAXStackElement(nodeTypes.get(qName),InputPosition.fromSAXLocator(loc,locator)));
		switch(peek().type)
		{
		case CHARACTER_RANGE_ELEMENT:
			String lowerBound = attributes.getValue("lower");
			String upperBound = attributes.getValue("upper");
			((CharacterSetRegexBean) saxStack[saxStackPointer - 1].regexChildren.get(0)).addRange(lowerBound.charAt(0), upperBound.charAt(0));
			break;
		case CHARACTER_SET_ELEMENT:
			String invert = attributes.getValue("invert");
			if(invert != null && (invert.equals("true") || invert.equals("1"))) peek().invertCharacterSet = true;
			peek().regexChildren = new ArrayList<RegexBean>();
			peek().regexChildren.add(new CharacterSetRegexBean());
			break;
		case CHOICE_ELEMENT:
			peek().regexChildren = new ArrayList<RegexBean>();
			break;
		case CLASS_AUXILIARY_CODE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case CLASS_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case CLASS_NAME_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case CODE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case CONCATENATION_ELEMENT:
			peek().regexChildren = new ArrayList<RegexBean>();
			break;
		case COPPER_SPEC_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case DECLARATIONS_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case DEFAULT_PRODUCTION_CODE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case DEFAULT_TERMINAL_CODE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case DISAMBIGUATE_TO_ELEMENT:
			refList = new ArrayList<CopperElementReference>();
			break;
		case DISAMBIGUATION_FUNCTION_ELEMENT:
			CopperElementName currentDisambiguationFunctionName = CopperElementName.newName(attributes.getValue("id"));
			currentDisambiguationFunction = (DisambiguationFunctionBean) currentGrammar.getGrammarElement(currentDisambiguationFunctionName);
			if(currentDisambiguationFunction == null)
			{
				currentDisambiguationFunction = new DisambiguationFunctionBean();
				currentDisambiguationFunction.setName(currentDisambiguationFunctionName);
				currentDisambiguationFunction.setLocation(peek().startLocation);
				currentGrammar.addGrammarElement(currentDisambiguationFunction);
			}
			break;
		case DOMINATES_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case EMPTY_STRING_REGEX_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case GRAMMARS_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case GRAMMAR_ELEMENT:
			CopperElementName grammarName = CopperElementName.newName(attributes.getValue("id"));
			if(!grammars.containsKey(grammarName))
			{
				grammars.put(grammarName,new GrammarBean());
				grammars.get(grammarName).setName(grammarName);
			}
			currentGrammar = grammars.get(grammarName);
			if(currentGrammar.getLocation() == null) grammars.get(grammarName).setLocation(peek().startLocation);
			break;
		case IN_CLASSES_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case KLEENE_STAR_ELEMENT:
			peek().regexChildren = new ArrayList<RegexBean>();
			break;
		case LAYOUT_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case LEFT_ASSOCIATIVE_ELEMENT:
			currentTerminal.setOperatorAssociativity(OperatorAssociativity.LEFT);
			break;
		case LHS_ELEMENT:
			refList = new ArrayList<CopperElementReference>();
			break;
		case MEMBERS_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case NON_ASSOCIATIVE_ELEMENT:
			currentTerminal.setOperatorAssociativity(OperatorAssociativity.NONASSOC);
			break;
		case NONTERMINAL_ELEMENT:
			CopperElementName currentNonterminalName = CopperElementName.newName(attributes.getValue("id"));
			currentNonTerminal = (NonTerminalBean) currentGrammar.getGrammarElement(currentNonterminalName);
			if(currentNonTerminal == null)
			{
				currentNonTerminal = new NonTerminalBean();
				currentNonTerminal.setName(currentNonterminalName);
				currentNonTerminal.setLocation(peek().startLocation);
				currentGrammar.addGrammarElement(currentNonTerminal);
			}
			break;
		case OPERATOR_ELEMENT:
			if(saxStack[saxStackPointer - 1].type == XMLSkinElementType.PRODUCTION_ELEMENT)
			{
				refList = new ArrayList<CopperElementReference>();
			}
			break;
		case PACKAGE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case PARSER_ATTRIBUTE_ELEMENT:
			CopperElementName currentParserAttributeName = CopperElementName.newName(attributes.getValue("id"));
			currentParserAttribute = (ParserAttributeBean) currentGrammar.getGrammarElement(currentParserAttributeName);
			if(currentParserAttribute == null)
			{
				currentParserAttribute = new ParserAttributeBean();
				currentParserAttribute.setName(currentParserAttributeName);
				currentParserAttribute.setLocation(peek().startLocation);
				currentGrammar.addGrammarElement(currentParserAttribute);
			}
			break;
		case PARSER_ELEMENT:
			if(currentParser != null)
			{
				foundMoreThanOneParser = true;
			}
			currentParser = new ParserBean();
			currentParser.setName(attributes.getValue("id"));
			currentParser.setLocation(peek().startLocation);
			String isUnitary = attributes.getValue("isUnitary");
			if(isUnitary.equals("true") || isUnitary.equals("1")) currentParser.setUnitary(true);
			break;
		case PARSER_INIT_CODE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case POST_PARSE_CODE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case PP_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case PREAMBLE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case PRECEDENCE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case PREFIX_ELEMENT:
			refList = new ArrayList<CopperElementReference>();
			break;
		case PRODUCTION_ELEMENT:
			CopperElementName currentProductionName = CopperElementName.newName(attributes.getValue("id"));
			currentProduction = (ProductionBean) currentGrammar.getGrammarElement(currentProductionName);
			if(currentProduction == null)
			{
				currentProduction = new ProductionBean();
				currentProduction.setName(currentProductionName);
				currentProduction.setLocation(peek().startLocation);
				currentGrammar.addGrammarElement(currentProduction);
			}
			break;
		case REGEX_ELEMENT:
			peek().regexChildren = new ArrayList<RegexBean>();
			break;
		case RHS_ELEMENT:
			refList = new ArrayList<CopperElementReference>();
			varNames = new ArrayList<String>();
			break;
		case RIGHT_ASSOCIATIVE_ELEMENT:
			currentTerminal.setOperatorAssociativity(OperatorAssociativity.RIGHT);
			break;
		case SEMANTIC_ACTION_AUXILIARY_CODE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case SINGLE_CHARACTER_ELEMENT:
			String character = attributes.getValue("char");
			((CharacterSetRegexBean) saxStack[saxStackPointer - 1].regexChildren.get(0)).addLooseChar(character.charAt(0));
			break;
		case START_LAYOUT_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case START_SYMBOL_ELEMENT:
			refList = new ArrayList<CopperElementReference>();
			break;
		case SUBMITS_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case TERMINAL_CLASS_ELEMENT:
			CopperElementName currentTerminalClassName = CopperElementName.newName(attributes.getValue("id"));
			currentTerminalClass = (TerminalClassBean) currentGrammar.getGrammarElement(currentTerminalClassName);
			if(currentTerminalClass == null)
			{
				currentTerminalClass = new TerminalClassBean();
				currentTerminalClass.setName(currentTerminalClassName);
				currentTerminalClass.setLocation(peek().startLocation);
				currentGrammar.addGrammarElement(currentTerminalClass);
			}
			break;
		case TERMINAL_ELEMENT:
			CopperElementName currentTerminalName = CopperElementName.newName(attributes.getValue("id"));
			currentTerminal = (TerminalBean) currentGrammar.getGrammarElement(currentTerminalName);
			if(currentTerminal == null)
			{
				currentTerminal = new TerminalBean();
				currentTerminal.setName(currentTerminalName);
				currentTerminal.setLocation(peek().startLocation);
				currentGrammar.addGrammarElement(currentTerminal);
			}
			break;
		case TYPE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case MACRO_REF_ELEMENT:
			String grammar = attributes.getValue("grammar");
			if(grammar == null || grammar.equals("")) grammarName = currentGrammar.getName();
			else grammarName = CopperElementName.newName(grammar);
			peek().regexChildren = new ArrayList<RegexBean>();
			peek().regexChildren.add(new MacroHoleRegexBean(CopperElementReference.ref(grammarName,CopperElementName.newName(attributes.getValue("id")),peek().startLocation)));
			break;
		case NONTERMINAL_REF_ELEMENT:
		case TERMINAL_REF_ELEMENT:
			if(varNames != null)
			{
				String varName = attributes.getValue("name");
				if(varName == null || varName.equals("")) varNames.add(null);
				else varNames.add(varName);
			}
		case TERMINAL_CLASS_REF_ELEMENT:
			grammar = attributes.getValue("grammar");
			if(grammar == null || grammar.equals("")) grammarName = currentGrammar.getName();
			else grammarName = CopperElementName.newName(grammar);
			(refList != null ? refList : refSet).add(CopperElementReference.ref(grammarName,CopperElementName.newName(attributes.getValue("id")),peek().startLocation));
			break;
		case GRAMMAR_REF_ELEMENT:
			(refList != null ? refList : refSet).add(CopperElementReference.ref(CopperElementName.newName(attributes.getValue("id")),peek().startLocation));
			break;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
	{
		switch(peek().type)
		{
		case CODE_ELEMENT:
		case CLASS_ELEMENT:
		case CLASS_NAME_ELEMENT:
		case PACKAGE_ELEMENT:
		case PP_ELEMENT:
		case PRECEDENCE_ELEMENT:
		case TYPE_ELEMENT:
			nodeText = String.copyValueOf(ch,start,length);
			break;
		default:
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
	throws SAXException
	{
		try
		{
			endElementInner(uri,localName,qName);
		}
		catch(CopperException ex)
		{
			error(new SAXParseException(ex.getMessage(),locator));			
		}
	}
	
	private void endElementInner(String uri,String localName,String qName)
	throws CopperException
	{
		SAXStackElement element = pop();
		switch(element.type)
		{
		case CHARACTER_RANGE_ELEMENT:
			// Empty; all work is done in startElement() when the element's attributes are known.
			break;
		case CHARACTER_SET_ELEMENT:
			CharacterSetRegexBean cset = (CharacterSetRegexBean) element.regexChildren.get(0);
			if(element.invertCharacterSet) cset.invert();
			peek().regexChildren.add(cset);
			break;
		case CHOICE_ELEMENT:
			ChoiceRegexBean newChoiceBean = new ChoiceRegexBean();
			newChoiceBean.setSubexps(element.regexChildren);
			peek().regexChildren.add(newChoiceBean);
			break;
		case CLASS_AUXILIARY_CODE_ELEMENT:
			currentParser.setParserClassAuxCode(nodeText);
			break;
		case CLASS_ELEMENT:
			switch(peek().type)
			{
			case OPERATOR_ELEMENT:
				currentTerminal.setOperatorClass(nodeText);
				break;
			case PRODUCTION_ELEMENT:
				currentProduction.setProductionClass(nodeText);
				break;
			}
			break;
		case CLASS_NAME_ELEMENT:
			currentParser.setClassName(nodeText);
			break;
		case CODE_ELEMENT:
			switch(peek().type)
			{
			case DISAMBIGUATION_FUNCTION_ELEMENT:
				currentDisambiguationFunction.setCode(nodeText);
				break;
			case PARSER_ATTRIBUTE_ELEMENT:
				currentParserAttribute.setCode(nodeText);
				break;
			case PRODUCTION_ELEMENT:
				currentProduction.setCode(nodeText);
				break;
			case TERMINAL_ELEMENT:
				currentTerminal.setCode(nodeText);
				break;
			}
			break;
		case CONCATENATION_ELEMENT:
			ConcatenationRegexBean newConcatenationBean = new ConcatenationRegexBean();
			newConcatenationBean.setSubexps(element.regexChildren);
			peek().regexChildren.add(newConcatenationBean);
			break;
		case COPPER_SPEC_ELEMENT:
			break;
		case DECLARATIONS_ELEMENT:
			// Empty
			break;
		case DEFAULT_PRODUCTION_CODE_ELEMENT:
			currentParser.setDefaultProductionCode(nodeText);
			break;
		case DEFAULT_TERMINAL_CODE_ELEMENT:
			currentParser.setDefaultTerminalCode(nodeText);
			break;
		case DISAMBIGUATE_TO_ELEMENT:
			currentDisambiguationFunction.setDisambiguateTo(refList.get(0));
			refList = null;
			break;
		case DISAMBIGUATION_FUNCTION_ELEMENT:
			currentDisambiguationFunction = null;
			break;
		case DOMINATES_ELEMENT:
			currentTerminal.setDominateList(refSet);
			refSet = null;
			break;
		case EMPTY_STRING_REGEX_ELEMENT:
			peek().regexChildren.add(new EmptyStringRegexBean());
			break;
		case GRAMMARS_ELEMENT:
			for(CopperElementReference ref : refSet)
			{
				if(!grammars.containsKey(ref.getName()))
				{
					grammars.put(ref.getName(),new GrammarBean());
					grammars.get(ref.getName()).setName(ref.getName());
				}
				currentParser.addGrammar(grammars.get(ref.getName()));
			}
			refSet = null;
			break;
		case GRAMMAR_ELEMENT:
			// Empty
			break;
		case GRAMMAR_REF_ELEMENT:
			// Empty
			break;
		case IN_CLASSES_ELEMENT:
			currentTerminal.setTerminalClasses(refSet);
			refSet = null;
			break;
		case KLEENE_STAR_ELEMENT:
			peek().regexChildren.add(new KleeneStarRegexBean((RegexBean) element.regexChildren.get(0)));
			break;
		case LAYOUT_ELEMENT:
			switch(peek().type)
			{
			case GRAMMAR_ELEMENT:
				currentGrammar.setGrammarLayout(refSet);
				refSet = null;
				break;
			case PRODUCTION_ELEMENT:
				currentProduction.setLayout(refSet);
				refSet = null;
				break;
			}
			break;
		case LEFT_ASSOCIATIVE_ELEMENT:
			// Empty
			break;
		case LHS_ELEMENT:
			currentProduction.setLhs(refList.get(0));
			refList = null;
			break;
		case MACRO_REF_ELEMENT:
			peek().regexChildren.add(element.regexChildren.get(0));
			break;
		case MEMBERS_ELEMENT:
			switch(peek().type)
			{
			case DISAMBIGUATION_FUNCTION_ELEMENT:
				currentDisambiguationFunction.setMembers(refSet);
				break;
			case TERMINAL_CLASS_ELEMENT:
				currentTerminalClass.setMembers(refSet);
				break;
			}
			refSet = null;
			break;
		case NONTERMINAL_ELEMENT:
			currentNonTerminal = null;
			break;
		case NONTERMINAL_REF_ELEMENT:
			// Empty
			break;
		case NON_ASSOCIATIVE_ELEMENT:
			// Empty
			break;
		case OPERATOR_ELEMENT:
			if(peek().type == XMLSkinElementType.PRODUCTION_ELEMENT)
			{
				currentProduction.setOperator(refList.get(0));
				refSet = null;
			}
			break;
		case PACKAGE_ELEMENT:
			currentParser.setPackageDecl(nodeText);
			break;
		case PARSER_ATTRIBUTE_ELEMENT:
			currentParserAttribute = null;
			break;
		case PARSER_ELEMENT:
			// Empty
			break;
		case PARSER_INIT_CODE_ELEMENT:
			currentParser.setParserInitCode(nodeText);
			break;
		case POST_PARSE_CODE_ELEMENT:
			currentParser.setPostParseCode(nodeText);
			break;
		case PP_ELEMENT:
			switch(peek().type)
			{
			case DISAMBIGUATION_FUNCTION_ELEMENT:
				currentDisambiguationFunction.setDisplayName(nodeText);
				break;
			case GRAMMAR_ELEMENT:
				currentGrammar.setDisplayName(nodeText);
				break;
			case PARSER_ATTRIBUTE_ELEMENT:
				currentParserAttribute.setDisplayName(nodeText);
				break;
			case PARSER_ELEMENT:
				currentParser.setDisplayName(nodeText);
				break;
			case PRODUCTION_ELEMENT:
				currentProduction.setDisplayName(nodeText);
				break;
			case NONTERMINAL_ELEMENT:
				currentNonTerminal.setDisplayName(nodeText);
				break;
			case TERMINAL_ELEMENT:
				currentTerminal.setDisplayName(nodeText);
				break;
			case TERMINAL_CLASS_ELEMENT:
				currentTerminalClass.setDisplayName(nodeText);
				break;
			}
			break;
		case PREAMBLE_ELEMENT:
			currentParser.setPreambleCode(nodeText);
			break;
		case PRECEDENCE_ELEMENT:
			switch(peek().type)
			{
			case OPERATOR_ELEMENT:
				currentTerminal.setOperatorPrecedence(Integer.valueOf(nodeText));
				break;
			case PRODUCTION_ELEMENT:
				currentProduction.setPrecedence(Integer.valueOf(nodeText));
				break;
			}
			break;
		case PREFIX_ELEMENT:
			currentTerminal.setPrefix(refList.get(0));
			refList = null;
			break;
		case PRODUCTION_ELEMENT:
			currentProduction = null;
			break;
		case REGEX_ELEMENT:
			currentTerminal.setRegex(element.regexChildren.get(0));
			break;
		case RHS_ELEMENT:
			currentProduction.setRhs(refList);
			currentProduction.setRHSVarNames(varNames);
			refList = null;
			varNames = null;
			break;
		case RIGHT_ASSOCIATIVE_ELEMENT:
			// Empty
			break;
		case SEMANTIC_ACTION_AUXILIARY_CODE_ELEMENT:
			currentParser.setSemanticActionAuxCode(nodeText);
			break;
		case SINGLE_CHARACTER_ELEMENT:
			// Empty
			break;
		case START_LAYOUT_ELEMENT:
			currentParser.setStartLayout(refSet);
			refSet = null;
			break;
		case START_SYMBOL_ELEMENT:
			currentParser.setStartSymbol(refList.get(0));
			refList = null;
			break;
		case SUBMITS_ELEMENT:
			currentTerminal.setSubmitList(refSet);
			refSet = null;
			break;
		case TERMINAL_CLASS_ELEMENT:
			currentTerminalClass = null;
			break;
		case TERMINAL_CLASS_REF_ELEMENT:
			// Empty
			break;
		case TERMINAL_ELEMENT:
			currentTerminal = null;
			break;
		case TERMINAL_REF_ELEMENT:
			// Empty
			break;
		case TYPE_ELEMENT:
			switch(peek().type)
			{
			case NONTERMINAL_ELEMENT:
				currentNonTerminal.setReturnType(nodeText);
				break;
			case PARSER_ATTRIBUTE_ELEMENT:
				currentParserAttribute.setAttributeType(nodeText);
				break;
			case TERMINAL_ELEMENT:
				currentTerminal.setReturnType(nodeText);
				break;
			}
			break;
		}
	}
}
