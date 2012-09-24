package edu.umn.cs.melt.copper.compiletime.skins.xml;

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

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericLocatedMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
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
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.KleeneStarRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.MacroHoleRegex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.OperatorAssociativity;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.OperatorClass;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserAttribute;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Production;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Regex;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Terminal;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.TerminalClass;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.ParserSpecProcessor;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class XMLSkinParser extends DefaultHandler 
{
	private static class SAXStackElement
	{
		public XMLSkinElements.Type type;
		public InputPosition startLocation;
		public ArrayList<Regex> regexChildren;
		public boolean invertCharacterSet;
		
		public SAXStackElement(XMLSkinElements.Type type,InputPosition startLocation)
		{
			this.type = type;
			this.startLocation = startLocation;
			this.regexChildren = null;
			this.invertCharacterSet = false;
		}
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
		if(schemaFile == null) if(logger.isLoggable(CompilerLevel.QUIET)) logger.logError(new GenericMessage(CompilerLevel.QUIET,"Cannot load XML skin schema. This generally means Copper was improperly built.",true,true));
    	try
    	{
    		schema = schemaFactory.newSchema(new StreamSource(new InputStreamReader(schemaFile)));
    	}
		catch(SAXException ex)
		{
			if(logger.isLoggable(CompilerLevel.QUIET)) logger.logError(new GenericMessage(CompilerLevel.QUIET,"Schema parse error: " + ex.getMessage(),true,true));
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
			if(logger.isLoggable(CompilerLevel.QUIET)) logger.log(new GenericMessage(CompilerLevel.QUIET,ex.getMessage(),true,false));
		}
		catch(ParserConfigurationException ex)
		{
			if(logger.isLoggable(CompilerLevel.QUIET)) logger.log(new GenericMessage(CompilerLevel.QUIET,ex.getMessage(),true,false));
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
				if(logger.isLoggable(CompilerLevel.QUIET)) logger.log(new GenericLocatedMessage(CompilerLevel.QUIET,loc,ex.getMessage(),true,false));
			}
			catch (SAXException ex)
			{
				if(logger.isLoggable(CompilerLevel.QUIET)) logger.log(new GenericLocatedMessage(CompilerLevel.QUIET,loc,ex.getMessage(),true,false));
			}
		}
		
		if(foundMoreThanOneParser)
		{
			if(logger.isLoggable(CompilerLevel.QUIET)) logger.log(new GenericLocatedMessage(CompilerLevel.QUIET,(InputPosition) currentParser.getLocation(),"Superfluous parser " + currentParser.getDisplayName() +": spec must contain exactly one parser element",true,false));			
		}
		logger.flush();
		boolean hasError = ParserSpecProcessor.normalizeParser(currentParser,logger);
		if(hasError) return null;
		return currentParser;
	}
	
	private InputPosition loc;
	private Locator locator;
	private Hashtable<CopperElementName,Grammar> grammars = new Hashtable<CopperElementName,Grammar>();
	private boolean foundMoreThanOneParser = false;
	private ParserBean currentParser = null;
	private ExtendedParserBean currentExtendedParser = null;
	private Grammar currentGrammar = null;
	private ExtensionGrammar currentExtensionGrammar = null;
	private Terminal currentTerminal = null;
	private TerminalClass currentTerminalClass = null;
	private OperatorClass currentOperatorClass = null;
	private NonTerminal currentNonTerminal = null;
	private Production currentProduction = null;
	private DisambiguationFunction currentDisambiguationFunction = null;
	private ParserAttribute currentParserAttribute = null;
	private ArrayList<CopperElementReference> refList = null;
	private Set<CopperElementReference> refSet = null;
	private ArrayList<String> varNames = null;
	private String nodeText = null;
	private SAXStackElement lastTextNode = null;
	
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
		SAXStackElement parent = null;
		if(saxStackPointer != -1) parent = peek();
		push(new SAXStackElement(XMLSkinElements.nodeTypes.get(qName),InputPosition.fromSAXLocator(loc,locator)));
		switch(peek().type)
		{
		case BRIDGE_PRODUCTIONS_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case CHARACTER_RANGE_ELEMENT:
			String lowerBound = attributes.getValue("lower");
			String upperBound = attributes.getValue("upper");
			((CharacterSetRegex) parent.regexChildren.get(0)).addRange(lowerBound.charAt(0), upperBound.charAt(0));
			break;
		case CHARACTER_SET_ELEMENT:
			String invert = attributes.getValue("invert");
			if(invert != null && (invert.equals("true") || invert.equals("1"))) peek().invertCharacterSet = true;
			peek().regexChildren = new ArrayList<Regex>();
			peek().regexChildren.add(new CharacterSetRegex());
			break;
		case CHOICE_ELEMENT:
			peek().regexChildren = new ArrayList<Regex>();
			break;
		case CLASS_AUXILIARY_CODE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case CLASS_ELEMENT:
			refList = new ArrayList<CopperElementReference>();
			break;
		case CLASS_NAME_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case CODE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case CONCATENATION_ELEMENT:
			peek().regexChildren = new ArrayList<Regex>();
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
			currentDisambiguationFunction = (DisambiguationFunction) currentGrammar.getGrammarElement(currentDisambiguationFunctionName);
			if(currentDisambiguationFunction == null)
			{
				currentDisambiguationFunction = new DisambiguationFunction();
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
		case EXTENSION_GRAMMARS_ELEMENT:
		case GRAMMARS_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case EXTENSION_GRAMMAR_ELEMENT:
			CopperElementName grammarName = CopperElementName.newName(attributes.getValue("id"));
			if(!grammars.containsKey(grammarName))
			{
				grammars.put(grammarName,new ExtensionGrammar());
				grammars.get(grammarName).setName(grammarName);
			}
			currentGrammar = grammars.get(grammarName);
			if(currentGrammar.getType() == CopperElementType.EXTENSION_GRAMMAR) currentExtensionGrammar = (ExtensionGrammar) currentGrammar;
			if(currentGrammar.getLocation() == null) grammars.get(grammarName).setLocation(peek().startLocation);
			break;
		case GRAMMAR_ELEMENT:
			grammarName = CopperElementName.newName(attributes.getValue("id"));
			if(!grammars.containsKey(grammarName))
			{
				grammars.put(grammarName,new Grammar());
				grammars.get(grammarName).setName(grammarName);
			}
			currentGrammar = grammars.get(grammarName);
			if(currentGrammar.getLocation() == null) grammars.get(grammarName).setLocation(peek().startLocation);
			break;
		case HOST_GRAMMAR_ELEMENT:
			refList = new ArrayList<CopperElementReference>();
			break;
		case IN_CLASSES_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case KLEENE_STAR_ELEMENT:
			peek().regexChildren = new ArrayList<Regex>();
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
		case MARKING_TERMINALS_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case MEMBERS_ELEMENT:
			refSet = new HashSet<CopperElementReference>();
			break;
		case NON_ASSOCIATIVE_ELEMENT:
			currentTerminal.setOperatorAssociativity(OperatorAssociativity.NONASSOC);
			break;
		case NONTERMINAL_ELEMENT:
			CopperElementName currentNonterminalName = CopperElementName.newName(attributes.getValue("id"));
			currentNonTerminal = (NonTerminal) currentGrammar.getGrammarElement(currentNonterminalName);
			if(currentNonTerminal == null)
			{
				currentNonTerminal = new NonTerminal();
				currentNonTerminal.setName(currentNonterminalName);
				currentNonTerminal.setLocation(peek().startLocation);
				currentGrammar.addGrammarElement(currentNonTerminal);
			}
			break;
		case OPERATOR_ELEMENT:
			if(parent.type == XMLSkinElements.Type.PRODUCTION_ELEMENT)
			{
				refList = new ArrayList<CopperElementReference>();
			}
			break;
		case OPERATOR_CLASS_ELEMENT:
			CopperElementName currentOperatorClassName = CopperElementName.newName(attributes.getValue("id"));
			currentOperatorClass = (OperatorClass) currentGrammar.getGrammarElement(currentOperatorClassName);
			if(currentOperatorClass == null)
			{
				currentOperatorClass = new OperatorClass();
				currentOperatorClass.setName(currentOperatorClassName);
				currentOperatorClass.setLocation(peek().startLocation);
				currentGrammar.addGrammarElement(currentOperatorClass);
			}
			break;
		case PACKAGE_ELEMENT:
			// Empty; all work is done in endElement() after the element's text content is known.
			break;
		case PARSER_ATTRIBUTE_ELEMENT:
			CopperElementName currentParserAttributeName = CopperElementName.newName(attributes.getValue("id"));
			currentParserAttribute = (ParserAttribute) currentGrammar.getGrammarElement(currentParserAttributeName);
			if(currentParserAttribute == null)
			{
				currentParserAttribute = new ParserAttribute();
				currentParserAttribute.setName(currentParserAttributeName);
				currentParserAttribute.setLocation(peek().startLocation);
				currentGrammar.addGrammarElement(currentParserAttribute);
			}
			break;
		case EXTENDED_PARSER_ELEMENT:
			currentExtendedParser = new ExtendedParserBean();
		case PARSER_ELEMENT:
			if(currentParser != null)
			{
				foundMoreThanOneParser = true;
			}
			if(currentExtendedParser != null) currentParser = currentExtendedParser;
			else currentParser = new ParserBean();
			currentParser.setName(attributes.getValue("id"));
			currentParser.setLocation(peek().startLocation);
			String isUnitary = attributes.getValue("isUnitary");
			if(isUnitary != null && (isUnitary.equals("true") || isUnitary.equals("1"))) currentParser.setUnitary(true);
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
			currentProduction = (Production) currentGrammar.getGrammarElement(currentProductionName);
			if(currentProduction == null)
			{
				currentProduction = new Production();
				currentProduction.setName(currentProductionName);
				currentProduction.setLocation(peek().startLocation);
				currentGrammar.addGrammarElement(currentProduction);
			}
			break;
		case REGEX_ELEMENT:
			peek().regexChildren = new ArrayList<Regex>();
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
			((CharacterSetRegex) parent.regexChildren.get(0)).addLooseChar(character.charAt(0));
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
			currentTerminalClass = (TerminalClass) currentGrammar.getGrammarElement(currentTerminalClassName);
			if(currentTerminalClass == null)
			{
				currentTerminalClass = new TerminalClass();
				currentTerminalClass.setName(currentTerminalClassName);
				currentTerminalClass.setLocation(peek().startLocation);
				currentGrammar.addGrammarElement(currentTerminalClass);
			}
			break;
		case TERMINAL_ELEMENT:
			CopperElementName currentTerminalName = CopperElementName.newName(attributes.getValue("id"));
			currentTerminal = (Terminal) currentGrammar.getGrammarElement(currentTerminalName);
			if(currentTerminal == null)
			{
				currentTerminal = new Terminal();
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
			peek().regexChildren = new ArrayList<Regex>();
			peek().regexChildren.add(new MacroHoleRegex(CopperElementReference.ref(grammarName,CopperElementName.newName(attributes.getValue("id")),peek().startLocation)));
			break;
		case NONTERMINAL_REF_ELEMENT:
		case TERMINAL_REF_ELEMENT:
			if(varNames != null)
			{
				String varName = attributes.getValue("name");
				if(varName == null || varName.equals("")) varNames.add(null);
				else varNames.add(varName);
			}
		case OPERATOR_CLASS_REF_ELEMENT:
		case TERMINAL_CLASS_REF_ELEMENT:
		case PRODUCTION_REF_ELEMENT:
			grammar = attributes.getValue("grammar");
			if(grammar == null || grammar.equals("")) grammarName = currentGrammar.getName();
			else grammarName = CopperElementName.newName(grammar);
			(refList != null ? refList : refSet).add(CopperElementReference.ref(grammarName,CopperElementName.newName(attributes.getValue("id")),peek().startLocation));
			break;
		case GRAMMAR_REF_ELEMENT:
			(refList != null ? refList : refSet).add(CopperElementReference.ref(CopperElementName.newName(attributes.getValue("id")),peek().startLocation));
			break;
		default:
			logger.logError(new GenericLocatedMessage(CompilerLevel.QUIET,peek().startLocation,"Unrecognized XML tag '" + localName + "'. There is a bug in Copper's XML schema.",true,true));
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
	{
		switch(peek().type)
		{
		case CODE_ELEMENT:
		case CLASS_NAME_ELEMENT:
		case PACKAGE_ELEMENT:
		case PP_ELEMENT:
		case PRECEDENCE_ELEMENT:
		case TYPE_ELEMENT:
			if(peek() != lastTextNode)
			{
				nodeText = "";
				lastTextNode = peek();
			}
			nodeText += String.copyValueOf(ch,start,length);
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
		case BRIDGE_PRODUCTIONS_ELEMENT:
			for(CopperElementReference ref : refSet)
			{
				if(ref.isFQ() && !ref.getGrammarName().equals(currentExtensionGrammar.getName()))
				{
					logger.logError(new GenericLocatedMessage(CompilerLevel.QUIET,element.startLocation,"Only local references are allowed in <" + element.type.getName() + "> elements",true,false));
				}
				else currentExtensionGrammar.addBridgeProduction(ref.getName());
			}
			refSet = null;
			break;
		case CHARACTER_RANGE_ELEMENT:
			// Empty; all work is done in startElement() when the element's attributes are known.
			break;
		case CHARACTER_SET_ELEMENT:
			CharacterSetRegex cset = (CharacterSetRegex) element.regexChildren.get(0);
			if(element.invertCharacterSet) cset.invert();
			peek().regexChildren.add(cset);
			break;
		case CHOICE_ELEMENT:
			ChoiceRegex newChoiceBean = new ChoiceRegex();
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
				// FIXME: Remove these conditions, and the changes to
				//        the 'PrecedenceClass' element type in the
				//        schema, when it is no longer necessary for
				//        backward compatibility with Copper 0.6.1.
				if(!refList.isEmpty())
				{
					currentTerminal.setOperatorClass(refList.get(0));
				}
				refList = null;
				break;
			case PRODUCTION_ELEMENT:
				if(!refList.isEmpty())
				{
					currentProduction.setPrecedenceClass(refList.get(0));
				}
				refList = null;
				break;
			default:
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
			default:
				break;
			}
			break;
		case CONCATENATION_ELEMENT:
			ConcatenationRegex newConcatenationBean = new ConcatenationRegex();
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
			peek().regexChildren.add(new EmptyStringRegex());
			break;
		case EXTENSION_GRAMMARS_ELEMENT:
			for(CopperElementReference ref : refSet)
			{
				if(!grammars.containsKey(ref.getName()))
				{
					grammars.put(ref.getName(),new ExtensionGrammar());
					grammars.get(ref.getName()).setName(ref.getName());
				}
				currentParser.addGrammar(grammars.get(ref.getName()));
			}
			refSet = null;
			break;
		case GRAMMARS_ELEMENT:
			for(CopperElementReference ref : refSet)
			{
				if(!grammars.containsKey(ref.getName()))
				{
					grammars.put(ref.getName(),new Grammar());
					grammars.get(ref.getName()).setName(ref.getName());
				}
				currentParser.addGrammar(grammars.get(ref.getName()));
			}
			refSet = null;
			break;
		case EXTENSION_GRAMMAR_ELEMENT:
			currentExtensionGrammar = null;
		case GRAMMAR_ELEMENT:
			currentGrammar = null;
			// Empty
			break;
		case GRAMMAR_REF_ELEMENT:
			// Empty
			break;
		case HOST_GRAMMAR_ELEMENT:
			CopperElementReference ref = refList.get(0);
			if(!grammars.containsKey(ref.getName()))
			{
				grammars.put(ref.getName(),new Grammar());
				grammars.get(ref.getName()).setName(ref.getName());
			}
			currentParser.addGrammar(grammars.get(ref.getName()));
			currentExtendedParser.setHostGrammar(ref.getName());
			refList = null;
			break;
		case IN_CLASSES_ELEMENT:
			currentTerminal.setTerminalClasses(refSet);
			refSet = null;
			break;
		case KLEENE_STAR_ELEMENT:
			peek().regexChildren.add(new KleeneStarRegex((Regex) element.regexChildren.get(0)));
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
			default:
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
		case MARKING_TERMINALS_ELEMENT:
			for(CopperElementReference ref1 : refSet)
			{
				if(ref1.isFQ() && !ref1.getGrammarName().equals(currentExtensionGrammar.getName()))
				{
					logger.logError(new GenericLocatedMessage(CompilerLevel.QUIET,element.startLocation,"Only local references are allowed in <" + element.type.getName() + "> elements",true,false));
				}
				currentExtensionGrammar.addMarkingTerminal(ref1.getName());
			}
			refSet = null;
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
			default:
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
			if(peek().type == XMLSkinElements.Type.PRODUCTION_ELEMENT)
			{
				currentProduction.setOperator(refList.get(0));
				refSet = null;
			}
			break;
		case OPERATOR_CLASS_ELEMENT:
			currentOperatorClass = null;
			break;
		case OPERATOR_CLASS_REF_ELEMENT:
			// Empty
			break;
		case PACKAGE_ELEMENT:
			currentParser.setPackageDecl(nodeText);
			break;
		case PARSER_ATTRIBUTE_ELEMENT:
			currentParserAttribute = null;
			break;
		case EXTENDED_PARSER_ELEMENT:
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
			case OPERATOR_CLASS_ELEMENT:
				currentOperatorClass.setDisplayName(nodeText);
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
			default:
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
			default:
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
		case PRODUCTION_REF_ELEMENT:
			// Empty
			break;
		case REGEX_ELEMENT:
			currentTerminal.setRegex(element.regexChildren.get(0));
			break;
		case RHS_ELEMENT:
			currentProduction.setRhs(refList);
			currentProduction.setRhsVarNames(varNames);
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
			default:
				break;
			}
			break;
		default:
			logger.logError(new GenericLocatedMessage(CompilerLevel.QUIET,peek().startLocation,"Unrecognized XML tag '" + localName + "'. There is a bug in Copper's XML schema.",true,true));
		}
	}
}
