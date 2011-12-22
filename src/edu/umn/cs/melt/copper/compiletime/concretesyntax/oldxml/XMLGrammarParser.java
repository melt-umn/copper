package edu.umn.cs.melt.copper.compiletime.concretesyntax.oldxml;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNode;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.AttributeConsolidator;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.CharacterSet;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Choice;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.Concatenation;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.EmptyString;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.KleeneStar;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.MacroHole;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex.ParsedRegex;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
//import edu.umn.cs.melt.copper.compiletime.logging.StringBasedCompilerLogger;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class XMLGrammarParser
{
	private static DocumentBuilderFactory dbFactory;
	private static DocumentBuilder parser;
	private static InputPosition pos;
	private static CustomRegexParser regexParser;

	static
	{
		dbFactory = DocumentBuilderFactory.newInstance();
		parser = null;
		pos = null;
		regexParser = null; 
	}
	
	public static InputPosition getPos()
	{
		InputPosition oldPos = pos;
		pos = InputPosition.advance(pos,' ');
		return oldPos;
	}
	
	public static void formalError(CompilerLogger logger,Node n,String need,String neededName,String neededSort)
	throws CopperException
	{
		if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,pos,"'" + n.getNodeName() + "' node " + need + " '" + neededName + "' " + neededSort);
	}
	
    public static GrammarSource parseGrammar(ArrayList< Pair<String,Reader> > files,CompilerLogger logger)
    throws IOException,CopperException
    {
    	if(parser == null)
    	{
    		try
    		{
    			parser = dbFactory.newDocumentBuilder();
    		}
    		catch(ParserConfigurationException ex)
    		{
    			if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,ex.getMessage());
    			return null;
    		}
    	}
    	
    	Hashtable<String,Node> nodes = new Hashtable<String,Node>();
    	
    	for(Pair<String,Reader> file : files)
    	{
    		Document d = null;
    		try
    		{
    			d = parser.parse(new InputSource(file.second()));
    		}
    		catch(SAXException ex)
    		{
    			if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,ex.getMessage());
    			return null;
    		}
    		for(int i = 0;i < d.getChildNodes().getLength();i++)
    		{
    			if(d.getChildNodes().item(i) instanceof Element)
    			{
    				nodes.put(file.first(),d.getChildNodes().item(i));
    			}
    		}
    	}
    	
    	regexParser = new CustomRegexParser(logger);
    	
    	IntermediateNode allNodes = null;
    	
    	for(String file : nodes.keySet())
    	{
    		pos = InputPosition.initialPos(file);
    		if(allNodes == null) allNodes = visitDOMNode(nodes.get(file),null,logger);
    		else allNodes = IntermediateConsNode.cons(visitDOMNode(nodes.get(file),null,logger),allNodes);
    	}

    	AttributeConsolidator consolidator = new edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.AttributeConsolidator(logger);
        allNodes.acceptVisitor(consolidator,null);
        return edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController.buildAST(logger,consolidator.consolidatedNodes);

    }

	public static IntermediateNode visitDOMNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(n.getNodeName().equals("copperspec"))
		{
			return visitCopperSpecNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("preamble"))
		{
			return visitPreambleNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("nonterm"))
		{
			return visitNontermNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("start"))
		{
			return visitStartNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("layout"))
		{
			return visitLayoutNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("prefix"))
		{
			return visitPrefixNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("term"))
		{
			return visitTermNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("prod"))
		{
			return visitProdNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("termclass"))
		{
			return visitTermClassNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("disambig_func"))
		{
			return visitDisambigFuncNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("attribute"))
		{
			return visitAttributeNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("operator"))
		{
			return visitOperatorNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("regex"))
		{
			return visitRegexNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("pp") || n.getNodeName().equals("class") || n.getNodeName().equals("precedence"))
		{
			return null;
		}
		else if(n.getNodeName().startsWith("#"))
		{
			return null;
		}
		else
		{
			if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,"Invalid DOM node type '" + n.getNodeName() + "'");
			return null;
		}
	}
	
	// Visitor patterns

	// ASSUMES that the "copperspec" node has attributes "id" and "type".
	@SuppressWarnings("unchecked")
	public static IntermediateNode visitCopperSpecNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(!n.hasChildNodes() || n.getChildNodes().getLength() == 0) return null;
		else if(n.getChildNodes().getLength() == 1) return visitDOMNode(n.getFirstChild(),null,logger);
		else
		{
			if(n.getAttributes().getNamedItem("id") == null)
			{
				formalError(logger,n,"missing","id","attribute");
				return null;
			}
			if(n.getAttributes().getNamedItem("type") == null)
			{
				formalError(logger,n,"missing","type","attribute");
				return null;
			}
			// Must not contain spaces
			String grammarId = n.getAttributes().getNamedItem("id").getTextContent();
			String spectype = n.getAttributes().getNamedItem("type").getTextContent();
			String postParseCode = "";
		    //if(spectype.equals("LALR1-silver.haskell")) postParseCode = "printParseTree(System.out,false,(edu.umn.cs.melt.copper.runtime.parsetree.stripped.StrippedParseTreeNode) root,\"\");";
		    //else if(spectype.equals("LALR1-pretty")) postParseCode = "printParseTree(System.out,true,(edu.umn.cs.melt.copper.runtime.parsetree.stripped.StrippedParseTreeNode) root,\"\");";
		    IntermediateNode grammarNameNode = new IntermediateSymbolNode(
		    	    IntermediateSymbolSort.GRAMMAR_NAME,
       		        Symbol.symbol(grammarId),
       		        Pair.cons("location",Pair.cons(getPos(),null)),
       		        Pair.cons("spectype",Pair.cons(getPos(),(Object) spectype)));
		    
		    IntermediateNode postParseCodeNode = new IntermediateSymbolNode(
	                IntermediateSymbolSort.DIRECTIVE,
	                Symbol.symbol(" postParseCode "),
	                Pair.cons("location",Pair.cons(getPos(),null)),
	                Pair.cons("code",Pair.cons(getPos(),(Object) postParseCode))); 
			
			IntermediateNode rv = null;
			
			ArrayList<IntermediateNode> nodes = new ArrayList<IntermediateNode>();
			nodes.add(grammarNameNode);
			nodes.add(postParseCodeNode);
			
			for(int i = 0;i < n.getChildNodes().getLength();i++)
			{
				nodes.add(visitDOMNode(n.getChildNodes().item(i),null,logger));
			}			

			for(int i = nodes.size() - 1;i >= 0;i--)
			{
				if(rv == null) rv = nodes.get(i);
				else rv = IntermediateConsNode.cons(nodes.get(i),rv); 
			}
			return rv;
		}
	}
	
	// ASSUMES that the "preamble" node is the child of a "copperspec" node and has an attribute "code".
	@SuppressWarnings("unchecked")
	public static IntermediateNode visitPreambleNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(!n.getParentNode().getNodeName().equals("copperspec"))
		{
			formalError(logger,n,"missing","copperspec","parent");
			return null;
		}
		String code = "";
		if(n.getAttributes().getNamedItem("code") != null)
		{
			code = n.getAttributes().getNamedItem("code").getTextContent();
		}
		else
		{
			for(int i = 0;i < n.getChildNodes().getLength();i++)
			{
				if(n.getChildNodes().item(i).getNodeName().equals("code"))
				{
					if(code.equals("")) code = n.getChildNodes().item(i).getTextContent();
					else
					{
						formalError(logger,n,"has too many","code","children");
						return null;
					}
				}
			}
			if(code == null)
			{
				formalError(logger,n,"missing","code","attribute or child");
				return null;
			}
		}
	     return new IntermediateSymbolNode(
		            IntermediateSymbolSort.DIRECTIVE,
		            Symbol.symbol(" startCode "),
		            Pair.cons("location",Pair.cons(getPos(),null)),
		            Pair.cons("code",Pair.cons(getPos(),(Object) code)));		
	}
	
	@SuppressWarnings("unchecked")
	public static IntermediateNode visitAttributeNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(!n.getParentNode().getNodeName().equals("copperspec"))
		{
			formalError(logger,n,"missing","copperspec","parent");
			return null;
		}
		String code = "";
		if(n.getAttributes().getNamedItem("code") != null)
		{
			code = n.getAttributes().getNamedItem("code").getTextContent();
		}
		else
		{
			for(int i = 0;i < n.getChildNodes().getLength();i++)
			{
				if(n.getChildNodes().item(i).getNodeName().equals("code"))
				{
					if(code.equals("")) code = n.getChildNodes().item(i).getTextContent();
					else
					{
						formalError(logger,n,"has too many","code","children");
						return null;
					}
				}
			}
			if(code == null)
			{
				formalError(logger,n,"missing","code","attribute or child");
				return null;
			}
		}
		String type = "java.lang.Object";
		if(n.getAttributes().getNamedItem("type") != null)
		{
			type = n.getAttributes().getNamedItem("type").getTextContent();
		}
		else
		{
			for(int i = 0;i < n.getChildNodes().getLength();i++)
			{
				if(n.getChildNodes().item(i).getNodeName().equals("type"))
				{
					if(type.equals("java.lang.Object")) type = n.getChildNodes().item(i).getTextContent();
					else
					{
						formalError(logger,n,"has too many","type","children");
						return null;
					}
				}
			}
			if(type == null)
			{
				formalError(logger,n,"missing","type","attribute or child");
				return null;
			}
		}
		if(n.getAttributes().getNamedItem("id") == null)
		{
			formalError(logger,n,"missing","id","attribute");
			return null;
		}
		
		
		String id = n.getAttributes().getNamedItem("id").getTextContent();
		if(n.getAttributes().getNamedItem("type") != null) type = n.getAttributes().getNamedItem("type").getTextContent();
	    return new IntermediateSymbolNode(
	            IntermediateSymbolSort.PARSER_ATTRIBUTE,
	            Symbol.symbol(id),
	            Pair.cons("location",Pair.cons(getPos(),null)),
	            Pair.cons("type",Pair.cons(getPos(),(Object) type)),
	            Pair.cons("code",Pair.cons(getPos(),(Object) code)));


	}
	
	public static String visitCodeNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		String parentName = n.getParentNode().getNodeName();
		if(!parentName.equals("preamble") && !parentName.equals("term") &&
		   !parentName.equals("prod") && !parentName.equals("disambig_func"))
		{
			formalError(logger,n,"missing","preamble', 'term', 'prod', or 'disambig_func","parent");
			return null;
		}
		return n.getTextContent();
	}
	
	// ASSUMES that the "nonterm" node is a child of a "copperspec," "lhs," "rhs," or "start" node
	// and has an attribute "id".
	@SuppressWarnings("unchecked")
	public static IntermediateNode visitNontermNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		String parentName = n.getParentNode().getNodeName();
		if(!parentName.equals("copperspec") && !parentName.equals("lhs") &&
		   !parentName.equals("rhs") && !parentName.equals("start"))
		{
			formalError(logger,n,"missing","copperspec', 'lhs', 'rhs', or 'start","parent");
			return null;
		}
		if(n.getAttributes().getNamedItem("id") == null)
		{
			formalError(logger,n,"missing","id","attribute");
			return null;
		}
		String id = n.getAttributes().getNamedItem("id").getTextContent();
		String type = "java.lang.Object";
		if(n.getAttributes().getNamedItem("type") != null) type = n.getAttributes().getNamedItem("type").getTextContent();
		
		String displayName = null;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("pp"))
			{
				if(displayName == null) displayName = n.getChildNodes().item(i).getTextContent();
				else
				{
					formalError(logger,n,"has too many","pp","children");
					return null;
				}
			}
		}

		
		return new IntermediateSymbolNode(
                IntermediateSymbolSort.NON_TERMINAL,
                Symbol.symbol(id),
                Pair.cons("location",Pair.cons(getPos(),null)),
                Pair.cons("type",Pair.cons(getPos(),(Object) type)),
                (displayName == null) ? null : Pair.cons("displayname",Pair.cons(getPos(),(Object) displayName)));
	}

	// ASSUMES that the "term" node is the child of a "copperspec," "layout," "prefix,"
	// "lhs," "rhs," or "disambig_func" node, and has attributes "id", "regex".
	@SuppressWarnings("unchecked")
	public static IntermediateNode visitTermNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		String parentName = n.getParentNode().getNodeName();
		if(!parentName.equals("copperspec") && !parentName.equals("layout") &&
		   !parentName.equals("prefix") && !parentName.equals("lhs") &&
		   !parentName.equals("rhs") && !parentName.equals("disambig_func"))
		{
			formalError(logger,n,"missing","copperspec', 'layout', 'prefix', 'lhs', 'rhs', or 'disambig_func","parent");
			return null;
		}
		if(n.getAttributes().getNamedItem("id") == null)
		{
			formalError(logger,n,"missing","id","attribute");
			return null;
		}
		String id = n.getAttributes().getNamedItem("id").getTextContent();
		ParsedRegex regexP = null;
		if(n.getAttributes().getNamedItem("regex") != null)
		{
			String regex = n.getAttributes().getNamedItem("regex").getTextContent();
			regexParser.setToParse(regex);
			regexP = regexParser.parse();
		}
		String type = "java.lang.Object";
		if(n.getAttributes().getNamedItem("type") != null) type = n.getAttributes().getNamedItem("type").getTextContent();
		String code = "";
		if(n.getAttributes().getNamedItem("code") != null)
		{
			code = n.getAttributes().getNamedItem("code").getTextContent();
		}
		else
		{
			for(int i = 0;i < n.getChildNodes().getLength();i++)
			{
				if(n.getChildNodes().item(i).getNodeName().equals("code"))
				{
					if(code.equals("")) code = n.getChildNodes().item(i).getTextContent();
					else
					{
						formalError(logger,n,"has too many","code","children");
						return null;
					}
				}
			}
			if(code == null)
			{
				formalError(logger,n,"missing","code","attribute or child");
				return null;
			}
		}
		String displayName = null;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("pp"))
			{
				if(displayName == null) displayName = n.getChildNodes().item(i).getTextContent();
				else
				{
					formalError(logger,n,"has too many","pp","children");
					return null;
				}
			}
		}
		
		IntermediateSymbolNode termSym = new IntermediateSymbolNode(
                               IntermediateSymbolSort.TERMINAL,
                               Symbol.symbol(id),
                               Pair.cons("location",Pair.cons(getPos(),null)),
                               Pair.cons("type",Pair.cons(getPos(),(Object) type)),
                               ((regexP != null) ? Pair.cons("regex",Pair.cons(getPos(),(Object) regexP)) : null),
                               Pair.cons("code",Pair.cons(getPos(),(Object) code)),
                               (displayName == null) ? null : Pair.cons("displayname",Pair.cons(getPos(),(Object) displayName)));
		IntermediateNode rv = termSym;
		if(n.hasChildNodes())
		{
			for(int i = 0;i < n.getChildNodes().getLength();i++)
			{
				if(n.getChildNodes().item(i).getNodeName().equals("classes"))
				{
					for(int j = 0;j < n.getChildNodes().item(i).getChildNodes().getLength();j++)
					{
						rv = IntermediateConsNode.cons(visitDOMNode(n.getChildNodes().item(i).getChildNodes().item(j),id,logger),rv);
					}
				}
				else if(n.getChildNodes().item(i).getNodeName().equals("submits"))
				{
					LinkedList<String> submitList = visitTermOrClassListNode(n.getChildNodes().item(i),id,logger);
					termSym.attributes.put("submits",Pair.cons(getPos(),(Object) submitList));
				}
				else if(n.getChildNodes().item(i).getNodeName().equals("dominates"))
				{
					LinkedList<String> dominateList = visitTermOrClassListNode(n.getChildNodes().item(i),id,logger);
					termSym.attributes.put("dominates",Pair.cons(getPos(),(Object) dominateList));
				}
				else if(n.getChildNodes().item(i).getNodeName().equals("code"));
				else rv = IntermediateConsNode.cons(visitDOMNode(n.getChildNodes().item(i),id,logger),rv);
			}
		}
		return rv;
	}
	
	// ASSUMES that the start node is the child of a "copperspec" node,
	// has one "nonterm" child and no more than one "layout" child.
	public static IntermediateNode visitStartNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(!n.getParentNode().getNodeName().equals("copperspec"))
		{
			formalError(logger,n,"missing","copperspec","parent");
			return null;
		}
		
		int nontermNodeCount = 0;
		IntermediateNode ntName = null;
		LinkedList<String> layoutList = new LinkedList<String>();
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("nonterm"))
			{
				ntName = visitDOMNode(n.getChildNodes().item(i),null,logger);
				nontermNodeCount++;
			}
			else if(n.getChildNodes().item(i).getNodeName().equals("layout"))
			{
				LinkedList<String> layoutListPart = visitTermListNode(n.getChildNodes().item(i),null,logger);
				if(layoutListPart != null) layoutList.addAll(visitTermListNode(n.getChildNodes().item(i),null,logger));
			}
		}
		if(nontermNodeCount == 0)
		{
			formalError(logger,n,"missing","nonterm","child");
			return null;
		}
		else if(nontermNodeCount > 1)
		{
			formalError(logger,n,"has more than one","nonterm","child");
			return null;
		}
		
		if(layoutList != null) ((IntermediateSymbolNode) ntName).attributes.put("startLayout",Pair.cons(getPos(),(Object) layoutList));
		((IntermediateSymbolNode) ntName).attributes.put("isStart",Pair.cons(getPos(),(Object) true));
		return ntName;
	}
	
	@SuppressWarnings("unchecked")
	public static IntermediateNode visitOperatorNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(!n.getParentNode().getNodeName().equals("term"))
		{
			formalError(logger,n,"missing","term","parent");
			return null;
		}
		String operatorClass = null;
		int operatorPrecedence = -1;
		int operatorAssociativity = -1;
		
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("opclass"))
			{
				if(operatorClass != null)
				{
					formalError(logger,n,"has too many","opclass","children");
					return null;
				}
				else
				{
					if(n.getChildNodes().item(i).getAttributes().getNamedItem("id") == null)
					{
						formalError(logger,n.getChildNodes().item(i),"missing","id","attribute");
						return null;
					}
					operatorClass = n.getChildNodes().item(i).getAttributes().getNamedItem("id").getTextContent();
				}
			}
			else if(n.getChildNodes().item(i).getNodeName().equals("precedence"))
			{
				if(operatorPrecedence != -1)
				{
					formalError(logger,n,"has too many","precedence","children");
					return null;
				}
				else
				{
					try { operatorPrecedence = Integer.parseInt(n.getChildNodes().item(i).getTextContent()); }
					catch(NumberFormatException ex) { formalError(logger,n.getChildNodes().item(i),"has invalid","numerical","content"); }
				}
			}
			if(n.getChildNodes().item(i).getNodeName().equals("associativity"))
			{
				if(operatorAssociativity != -1)
				{
					formalError(logger,n,"has too many","associativity","children");
					return null;
				}
				else
				{
					String assocString = n.getChildNodes().item(i).getTextContent();
					if(assocString.equals("nonassoc")) operatorAssociativity = OperatorAttributes.ASSOC_NONASSOC;
					else if(assocString.equals("left")) operatorAssociativity = OperatorAttributes.ASSOC_LEFT;
					else if(assocString.equals("right")) operatorAssociativity = OperatorAttributes.ASSOC_RIGHT;
					else
					{
						formalError(logger,n.getChildNodes().item(i),"has","invalid","associativity");
					}
				}
			}
		}
	    IntermediateNode rv;
	    
	    rv = IntermediateConsNode.cons(
	            new IntermediateSymbolNode(
	             IntermediateSymbolSort.TERMINAL_CLASS,
	             Symbol.symbol(operatorClass),
	             Pair.cons("location",Pair.cons(getPos(),null))),
	            new IntermediateSymbolNode(
	             IntermediateSymbolSort.TERMINAL,
	             Symbol.symbol(inheritance),
	             Pair.cons("operatorClass",Pair.cons(getPos(),(Object) operatorClass)),
	             Pair.cons("operatorPrecedence",Pair.cons(getPos(),(Object) operatorPrecedence)),
	             Pair.cons("operatorAssociativity",Pair.cons(getPos(),(Object) Pair.cons(getPos(),operatorAssociativity)))));
	    
		return rv;
	}

	// ASSUMES nothing.
	public static IntermediateNode visitLayoutNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		IntermediateNode rv;
		if(!n.hasChildNodes() || n.getChildNodes().getLength() == 0)
		{
			return null;
		}
		else if(n.getChildNodes().getLength() == 1) return visitDOMNode(n.getFirstChild(),null,logger);
		else
		{
			rv = visitDOMNode(n.getFirstChild(),null,logger);
			for(int i = 1;i < n.getChildNodes().getLength();i++)
			{
				rv = new IntermediateConsNode(visitDOMNode(n.getChildNodes().item(i),null,logger),rv); 
			}
			return rv;
		}
	}
	
	// ASSUMES that the prod node is the child of a "copperspec" node,
	// has attributes "id," "class," "code," and "precedence", and children "lhs" and "rhs."
	@SuppressWarnings("unchecked")
	public static IntermediateNode visitProdNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(!n.getParentNode().getNodeName().equals("copperspec"))
		{
			formalError(logger,n,"missing","copperspec","parent");
			return null;
		}
		if(n.getAttributes().getNamedItem("id") == null)
		{
			formalError(logger,n,"missing","id","attribute");
			return null;
		}
		if(n.getAttributes().getNamedItem("class") == null && ((Element) n).getElementsByTagName("class").getLength() != 1)
		{
			if(n.getAttributes().getNamedItem("class") == null && ((Element) n).getElementsByTagName("class").getLength() == 0) formalError(logger,n,"missing","class","child");
			else formalError(logger,n,"has too many","class","children");
			return null;
		}
		if(n.getAttributes().getNamedItem("precedence") == null && ((Element) n).getElementsByTagName("precedence").getLength() != 1)
		{
			if(n.getAttributes().getNamedItem("precedence") == null && ((Element) n).getElementsByTagName("precedence").getLength() == 0) formalError(logger,n,"missing","precedence","child");
			else formalError(logger,n,"has too many","precedence","children");
			return null;
		}


		String id = n.getAttributes().getNamedItem("id").getTextContent();
		String precClass = n.getAttributes().getNamedItem("class") != null ? n.getAttributes().getNamedItem("class").getTextContent() : ((Element) n).getElementsByTagName("class").item(0).getTextContent();
		int precedence = 0;
		try
		{
			String precString = n.getAttributes().getNamedItem("precedence") != null ? n.getAttributes().getNamedItem("precedence").getTextContent() : ((Element) n).getElementsByTagName("precedence").item(0).getTextContent();
			precedence = Integer.parseInt(precString);
		}
		catch(NumberFormatException ex)
		{
			if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,pos,"Expected numeric precedence declaration on production '" + id + "'; got '" + n.getAttributes().getNamedItem("precedence").getTextContent() + "'");
		}
		String code = "";
		if(n.getAttributes().getNamedItem("code") != null)
		{
			code = n.getAttributes().getNamedItem("code").getTextContent();
		}
		else
		{
			for(int i = 0;i < n.getChildNodes().getLength();i++)
			{
				if(n.getChildNodes().item(i).getNodeName().equals("code"))
				{
					if(code.equals("")) code = n.getChildNodes().item(i).getTextContent();
					else
					{
						formalError(logger,n,"has too many","code","children");
						return null;
					}
				}
			}
			if(code == null)
			{
				formalError(logger,n,"missing","code","attribute or child");
				return null;
			}
		}
	    
		String operatorNode = null;
		LinkedList<String> layoutNode = new LinkedList<String>();
		String lhsNode = null;
		LinkedList<String> rhsNode = null;
		
		int lhsNodeCount = 0,rhsNodeCount = 0;
		
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("operator"))
			{
				operatorNode = visitProdOperatorNode(n.getChildNodes().item(i),id,logger);
			}
			else if(n.getChildNodes().item(i).getNodeName().equals("layout"))
			{
				LinkedList<String> layoutNodeP = visitTermListNode(n.getChildNodes().item(i),id,logger);
				if(layoutNodeP != null) layoutNode.addAll(layoutNodeP);
			}
			else if(n.getChildNodes().item(i).getNodeName().equals("lhs"))
			{
				lhsNode = visitLHSNode(n.getChildNodes().item(i),id,logger);
				lhsNodeCount++;
			}
			else if(n.getChildNodes().item(i).getNodeName().equals("rhs"))
			{
				rhsNode = visitRHSNode(n.getChildNodes().item(i),id,logger);
				rhsNodeCount++;
			}
		}
		if(layoutNode.isEmpty()) layoutNode = null;
		
		if(lhsNodeCount == 0)
		{
			formalError(logger,n,"missing","lhs","child");
			return null;
		}
		else if(lhsNodeCount > 1)
		{
			formalError(logger,n,"has more than one","lhs","child");
			return null;
		}
		if(rhsNodeCount == 0)
		{
			formalError(logger,n,"missing","rhs","child");
			return null;
		}
		else if(rhsNodeCount > 1)
		{
			formalError(logger,n,"has more than one","rhs","child");
			return null;
		}

		String displayName = null;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("pp"))
			{
				if(displayName == null) displayName = n.getChildNodes().item(i).getTextContent();
				else
				{
					formalError(logger,n,"has too many","pp","children");
					return null;
				}
			}
		}
		
	    IntermediateSymbolNode precClassNode = new IntermediateSymbolNode(
	             IntermediateSymbolSort.TERMINAL_CLASS,
	             Symbol.symbol(precClass),
	             Pair.cons("location",Pair.cons(getPos(),null)));
	    IntermediateSymbolNode prodNode = new IntermediateSymbolNode(
	             IntermediateSymbolSort.PRODUCTION,
	             Symbol.symbol(id),
	             Pair.cons("location",Pair.cons(getPos(),null)),
	             Pair.cons("class",Pair.cons(getPos(),(Object) precClass)),
	             Pair.cons("precedence",Pair.cons(getPos(),(Object) precedence)),
	             (operatorNode == null) ? null : Pair.cons("operator",Pair.cons(getPos(),(Object) operatorNode)),
	             (layoutNode == null) ? null : Pair.cons("layout",Pair.cons(getPos(),(Object) layoutNode)),
	             Pair.cons("code",Pair.cons(getPos(),(Object) code)),
	             Pair.cons("LHS",Pair.cons(getPos(),(Object) lhsNode)),
	             Pair.cons("RHS",Pair.cons(getPos(),(Object) rhsNode)),
	             (displayName == null) ? null : Pair.cons("displayname",Pair.cons(getPos(),(Object) displayName)));
	    
	    return new IntermediateConsNode(precClassNode,prodNode);
		
	}
	
	// ASSUMES that the operator node has exactly one child, a "term" with no other attributes.
	public static String visitProdOperatorNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		int termNodeIndex = -1;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("term"))
			{
				if(termNodeIndex == -1) termNodeIndex = i;
				else
				{
					formalError(logger,n,"has more than one","term","child");
					return null;
				}
			}
			else if(!n.getChildNodes().item(i).getNodeName().startsWith("#"))
			{
				formalError(logger,n,"has invalid",n.getChildNodes().item(i).getNodeName(),"child");
				return null;
			}
		}
		
		if(n.getChildNodes().item(termNodeIndex).getAttributes().getNamedItem("id") == null)
		{
			formalError(logger,n.getChildNodes().item(termNodeIndex),"missing","id","attribute");
			return null;
		}
		return n.getChildNodes().item(termNodeIndex).getAttributes().getNamedItem("id").getTextContent();
	}
	
	// ASSUMES that the operator node has exactly one child, a "nonterm" with no other attributes.
	public static String visitLHSNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		int nontermNodeIndex = -1;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("nonterm"))
			{
				if(nontermNodeIndex == -1) nontermNodeIndex = i;
				else
				{
					formalError(logger,n,"has more than one","nonterm","child");
					return null;
				}
			}
			else if(!n.getChildNodes().item(i).getNodeName().startsWith("#"))
			{
				formalError(logger,n,"has invalid",n.getChildNodes().item(i).getNodeName(),"child");
				return null;
			}
		}
		
		if(n.getChildNodes().item(nontermNodeIndex).getAttributes().getNamedItem("id") == null)
		{
			formalError(logger,n.getChildNodes().item(nontermNodeIndex),"missing","id","attribute");
			return null;
		}
		return n.getChildNodes().item(nontermNodeIndex).getAttributes().getNamedItem("id").getTextContent();
	}
	
	// ASSUMES that each child node has an "id" attribute.
	public static LinkedList<String> visitRHSNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		LinkedList<String> rv = new LinkedList<String>();
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("term") ||
			   n.getChildNodes().item(i).getNodeName().equals("nonterm") ||
			   n.getChildNodes().item(i).getNodeName().equals("symbol"))
			{
				if(n.getChildNodes().item(i).getAttributes().getNamedItem("id") == null)
				{
					formalError(logger,n.getChildNodes().item(i),"missing","id","attribute");
					return null;
				}
				rv.add(n.getChildNodes().item(i).getAttributes().getNamedItem("id").getTextContent());
			}
			else if(!n.getChildNodes().item(i).getNodeName().startsWith("#"))
			{
				formalError(logger,n,"has invalid",n.getChildNodes().item(i).getNodeName(),"child");
				return null;				
			}
		}
		return rv;
	}
	
	// ASSUMES that the "disambig_func" node has attributes "id" and "code",
	//         that there are two or more child nodes,
	//         and that each child node has an "id" attribute.
	@SuppressWarnings("unchecked")
	public static IntermediateNode visitDisambigFuncNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(n.getAttributes().getNamedItem("id") == null)
		{
			formalError(logger,n,"missing","id","attribute");
		}
		String id = n.getAttributes().getNamedItem("id").getTextContent();
		String code = "";
		if(n.getAttributes().getNamedItem("code") != null)
		{
			code = n.getAttributes().getNamedItem("code").getTextContent();
		}
		else
		{
			for(int i = 0;i < n.getChildNodes().getLength();i++)
			{
				if(n.getChildNodes().item(i).getNodeName().equals("code"))
				{
					if(code.equals("")) code = n.getChildNodes().item(i).getTextContent();
					else
					{
						formalError(logger,n,"has too many","code","children");
						return null;
					}
				}
			}
			if(code == null)
			{
				formalError(logger,n,"missing","code","attribute or child");
				return null;
			}
		}
		LinkedList<String> members = new LinkedList<String>();
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("term"))
			{
				if(n.getChildNodes().item(i).getAttributes().getNamedItem("id") == null)
				{
					formalError(logger,n.getChildNodes().item(i),"missing","id","attribute");
					return null;
				}
				members.add(n.getChildNodes().item(i).getAttributes().getNamedItem("id").getTextContent());
			}
			else if(!n.getChildNodes().item(i).getNodeName().equals("code") &&
				    !n.getChildNodes().item(i).getNodeName().startsWith("#"))
			{
				formalError(logger,n,"has invalid",n.getChildNodes().item(i).getNodeName(),"child");
				return null;				
			}
		}

		return new IntermediateSymbolNode(
	            IntermediateSymbolSort.DISAMBIGUATION_GROUP,
	            Symbol.symbol(id),
	            Pair.cons("location",Pair.cons(getPos(),null)),
	            Pair.cons("code",Pair.cons(getPos(),(Object) code)),
	            Pair.cons("members",Pair.cons(getPos(),(Object) members)));
		
	}
	
	// ASSUMES that each child node is a "term" node with an "id" attribute.
	public static LinkedList<String> visitTermListNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		LinkedList<String> members = new LinkedList<String>();
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("term"))
			{
				if(n.getChildNodes().item(i).getAttributes().getNamedItem("id") == null)
				{
					formalError(logger,n.getChildNodes().item(i),"missing","id","attribute");
					return null;
				}
				members.add(n.getChildNodes().item(i).getAttributes().getNamedItem("id").getTextContent());
			}
			else if(!n.getChildNodes().item(i).getNodeName().startsWith("#"))
			{
				formalError(logger,n,"has invalid",n.getChildNodes().item(i).getNodeName(),"child");
				return null;				
			}
		}
		return members;
	}
	
	// ASSUMES that each child node is a "term" node with an "id" attribute.
	public static LinkedList<String> visitTermOrClassListNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		LinkedList<String> members = new LinkedList<String>();
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("term"))
			{
				if(n.getChildNodes().item(i).getAttributes().getNamedItem("id") == null)
				{
					formalError(logger,n.getChildNodes().item(i),"missing","id","attribute");
					return null;
				}
				members.add(n.getChildNodes().item(i).getAttributes().getNamedItem("id").getTextContent());
			}
			else if(n.getChildNodes().item(i).getNodeName().equals("termclass"))
			{
				if(n.getChildNodes().item(i).getAttributes().getNamedItem("id") == null)
				{
					formalError(logger,n.getChildNodes().item(i),"missing","id","attribute");
					return null;
				}
				members.add(n.getChildNodes().item(i).getAttributes().getNamedItem("id").getTextContent());
			}
			else if(!n.getChildNodes().item(i).getNodeName().startsWith("#"))
			{
				formalError(logger,n,"has invalid",n.getChildNodes().item(i).getNodeName(),"child");
				return null;				
			}
		}
		return members;
	}

	// ASSUMES that the "termclass" node has an "id" attribute.
	@SuppressWarnings("unchecked")
	public static IntermediateNode visitTermClassNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(n.getAttributes().getNamedItem("id") == null)
		{
			formalError(logger,n,"missing","id","attribute");
			return null;
		}
		String id = n.getAttributes().getNamedItem("id").getTextContent();
		LinkedList<String> ids = new LinkedList<String>();
		ids.add(id);
        return IntermediateConsNode.cons(
              new IntermediateSymbolNode(IntermediateSymbolSort.TERMINAL_CLASS,
            		                    Symbol.symbol(id),
            		        		    Pair.cons("location",Pair.cons(getPos(),null))),
              new IntermediateSymbolNode(IntermediateSymbolSort.TERMINAL,
            		                     Symbol.symbol(inheritance),
            		        		     Pair.cons("location",Pair.cons(getPos(),null)),
            		                     Pair.cons("classes",Pair.cons(getPos(),(Object) ids))));
	}
	
	// ASSUMES that the "prefix" node has only one child, a terminal node with an "id" attribute. 
	@SuppressWarnings("unchecked")
	public static IntermediateNode visitPrefixNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		int termNodeIndex = -1;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("term"))
			{
				if(termNodeIndex == -1) termNodeIndex = i;
				else
				{
					formalError(logger,n,"has more than one","term","child");
					return null;
				}
			}
			else if(!n.getChildNodes().item(i).getNodeName().startsWith("#"))
			{
				formalError(logger,n,"has invalid",n.getChildNodes().item(i).getNodeName(),"child");
				return null;
			}
		}
		
		if(n.getChildNodes().item(termNodeIndex).getAttributes().getNamedItem("id") == null)
		{
			formalError(logger,n.getChildNodes().item(termNodeIndex),"missing","id","attribute");
			return null;
		}
		
        return new IntermediateSymbolNode(
                    IntermediateSymbolSort.TERMINAL,
	                Symbol.symbol(inheritance),
      		        Pair.cons("location",Pair.cons(getPos(),null)),
	                Pair.cons("prefix",Pair.cons(getPos(),(Object) n.getChildNodes().item(termNodeIndex).getAttributes().getNamedItem("id").getTextContent())));
	}
	
	// ASSUMES that the regex node is the child of a term node and has only one child.
	@SuppressWarnings("unchecked")
	public static IntermediateNode visitRegexNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(!n.getParentNode().getNodeName().equals("term"))
		{
			formalError(logger,n,"missing","term","parent");
			return null;
		}
		ParsedRegex pr = null,buffer;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			buffer = visitRegexInteriorNode(n.getChildNodes().item(i),inheritance,logger);
			if(buffer != null && pr != null)
			{
				formalError(logger,n,"has too many","regex","children");
				return null;
			}
			else if(buffer != null)
			{
				pr = buffer;
			}
		}
		return new IntermediateSymbolNode(
			   IntermediateSymbolSort.TERMINAL,
			   Symbol.symbol(inheritance),
		       Pair.cons("location",Pair.cons(getPos(),null)),
			   Pair.cons("regex",Pair.cons(getPos(),(Object) pr)));
	}
	
	public static ParsedRegex visitRegexInteriorNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(n.getNodeName().equals("string"))
		{
			return visitRegexStringNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("concat"))
		{
			return visitRegexConcatNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("choice"))
		{
			return visitRegexChoiceNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("opt"))
		{
			return visitRegexOptNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("kleenestar"))
		{
			return visitRegexKleeneStarNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("plus"))
		{
			return visitRegexPlusNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("emptystring"))
		{
			return visitRegexEmptyStringNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("charset"))
		{
			return visitRegexCharsetNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("macro"))
		{
			return visitRegexMacroNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("dot"))
		{
			return visitRegexWildcardNode(n,inheritance,logger);
		}
		else if(n.getNodeName().startsWith("#"))
		{
			return null;
		}
		else
		{
			if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,"Invalid regex interior node type '" + n.getNodeName() + "'");
			return null;
		}
	}
	
	// ASSUMES that the node has only one child, a "term" node with an "id" attribute.
	private static ParsedRegex visitRegexMacroNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		int termNodeIndex = -1;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("term"))
			{
				if(termNodeIndex == -1) termNodeIndex = i;
				else
				{
					formalError(logger,n,"has more than one","term","child");
					return null;
				}
			}
			else if(!n.getChildNodes().item(i).getNodeName().startsWith("#"))
			{
				formalError(logger,n,"has invalid",n.getChildNodes().item(i).getNodeName(),"child");
				return null;
			}
		}
		
		if(n.getChildNodes().item(termNodeIndex).getAttributes().getNamedItem("id") == null)
		{
			formalError(logger,n.getChildNodes().item(termNodeIndex),"missing","id","attribute");
			return null;
		}

		return new MacroHole(new Terminal(n.getChildNodes().item(termNodeIndex).getAttributes().getNamedItem("id").getTextContent()));
	}
	
	// ASSUMES that the node has only one child, which is a regex interior node.
	private static ParsedRegex visitRegexPlusNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		ParsedRegex pr = null,buffer;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			buffer = visitRegexInteriorNode(n.getChildNodes().item(i),inheritance,logger);
			if(buffer != null && pr != null)
			{
				formalError(logger,n,"has too many","regex","children");
			}
			if(buffer != null)
			{
				pr = buffer;
			}
		}
		return new Concatenation(pr,new KleeneStar(pr.clone()));
	}

	// ASSUMES that the node has only one child, which is a regex interior node.
	private static ParsedRegex visitRegexOptNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		ParsedRegex pr = null,buffer;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			buffer = visitRegexInteriorNode(n.getChildNodes().item(i),inheritance,logger);
			if(buffer != null && pr != null)
			{
				formalError(logger,n,"has too many","regex","children");
			}
			if(buffer != null)
			{
				pr = buffer;
			}
		}
		return new Choice(pr,new EmptyString());
	}

	// ASSUMES that the node has only one child, which is a regex interior node.
	private static ParsedRegex visitRegexKleeneStarNode(Node n,String inheritance, CompilerLogger logger)
	throws CopperException
	{
		ParsedRegex pr = null,buffer;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			buffer = visitRegexInteriorNode(n.getChildNodes().item(i),inheritance,logger);
			if(buffer != null && pr != null)
			{
				formalError(logger,n,"has too many","regex","children");
			}
			if(buffer != null)
			{
				pr = buffer;
			}
		}
		return new KleeneStar(pr);
	}

	// ASSUMES nothing.
	private static ParsedRegex visitRegexEmptyStringNode(Node n,String inheritance, CompilerLogger logger)
	{
		return new EmptyString();
	}

	// ASSUMES that the string node contains only text.
	private static ParsedRegex visitRegexStringNode(Node n,String inheritance,CompilerLogger logger)
	{
		return ParsedRegex.simpleStringRegex(n.getTextContent());
	}

	// ASSUMES that the concatenation node contains only other regex interior nodes for children.
	private static ParsedRegex visitRegexConcatNode(Node n, String inheritance,CompilerLogger logger)
	throws CopperException
	{
		LinkedList<ParsedRegex> subexps = new LinkedList<ParsedRegex>();
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			ParsedRegex pr = visitRegexInteriorNode(n.getChildNodes().item(i),inheritance,logger);
			if(pr != null) subexps.add(pr);
		}
		ParsedRegex[] subexpsA = new ParsedRegex[subexps.size()];
		int i = 0;
		for(ParsedRegex se : subexps) subexpsA[i++] = se; 
		return new Concatenation(subexpsA);
	}

	// ASSUMES that the choice node contains only other regex interior nodes for children.
	private static ParsedRegex visitRegexChoiceNode(Node n, String inheritance,CompilerLogger logger)
	throws CopperException
	{
		LinkedList<ParsedRegex> subexps = new LinkedList<ParsedRegex>();
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			ParsedRegex pr = visitRegexInteriorNode(n.getChildNodes().item(i),inheritance,logger);
			if(pr != null) subexps.add(pr);
		}
		ParsedRegex[] subexpsA = new ParsedRegex[subexps.size()];
		int i = 0;
		for(ParsedRegex se : subexps) subexpsA[i++] = se; 
		return new Choice(subexpsA);
	}
	

	private static ParsedRegex visitRegexCharsetNode(Node n,String inheritance, CompilerLogger logger)
	throws CopperException
	{
		LinkedList<CharacterSet> constituents = new LinkedList<CharacterSet>();
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			CharacterSet pr = visitRegexInteriorCharsetNode(n.getChildNodes().item(i),inheritance,logger);
			if(pr != null) constituents.add(pr);
		}
		CharacterSet union;
		if(constituents.size() == 0)
		{
			if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,"Empty character set");
			return null;
		}
		else if(constituents.size() == 1)
		{
			union = constituents.getFirst();
		}
		else
		{
			union = CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS);
			for(CharacterSet cs : constituents) union = CharacterSet.union(union,cs);
		}
		// DEBUG-X-BEGIN
		//System.err.println(n.hasAttributes() + " " + n.getAttributes().getNamedItem("invert"));
		// DEBUG-X-END
		if(n.hasAttributes() && n.getAttributes().getNamedItem("invert") != null)
		{
			union = union.invertSet();
		}
		return union;
	}

	private static CharacterSet visitRegexInteriorCharsetNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		if(n.getNodeName().equals("loosechar"))
		{
			return visitRegexLooseCharNode(n,inheritance,logger);
		}
		else if(n.getNodeName().equals("range"))
		{
			return visitRegexCharRangeNode(n,inheritance,logger);
		}
		else if(n.getNodeName().startsWith("#"))
		{
			return null;
		}
		else
		{
			if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,null,"Invalid character set interior node type '" + n.getNodeName() + "'");
			return null;
		}
	}
	
	// ASSUMES that the loosechar node contains only text. 
	private static CharacterSet visitRegexLooseCharNode(Node n,String inheritance,CompilerLogger logger)
	{
		return CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,n.getTextContent().toCharArray());
	}

	// ASSUMES that the char range node contains exactly one "lower" node and one "upper" node, each of
	// which contain exactly one character.
	private static CharacterSet visitRegexCharRangeNode(Node n,String inheritance,CompilerLogger logger)
	throws CopperException
	{
		char lower = Character.MIN_VALUE,upper = Character.MAX_VALUE;
		for(int i = 0;i < n.getChildNodes().getLength();i++)
		{
			if(n.getChildNodes().item(i).getNodeName().equals("lower"))
			{
				if(lower == Character.MIN_VALUE)
				{
					if(n.getChildNodes().item(i).getTextContent().length() == 0)
					{
						formalError(logger,n.getChildNodes().item(i),"missing","#text","content");
					}
					lower = n.getChildNodes().item(i).getTextContent().charAt(0);
				}
				else
				{
					formalError(logger,n,"has too many","lower","children");
					return null;
				}
			}
			if(n.getChildNodes().item(i).getNodeName().equals("upper"))
			{
				if(upper == Character.MAX_VALUE)
				{
					if(n.getChildNodes().item(i).getTextContent().length() == 0)
					{
						formalError(logger,n.getChildNodes().item(i),"missing","#text","content");
					}
					upper = n.getChildNodes().item(i).getTextContent().charAt(0);
				}
				else
				{
					formalError(logger,n,"has too many","upper","children");
					return null;
				}
			}
		}
		return CharacterSet.instantiate(CharacterSet.RANGES,'+',lower,upper);		
	}
	
	private static ParsedRegex visitRegexWildcardNode(Node n,String inheritance,CompilerLogger logger)
	{
		return CharacterSet.instantiate(CharacterSet.LOOSE_CHARACTERS,'\n').invertSet();
	}
	
	
}
