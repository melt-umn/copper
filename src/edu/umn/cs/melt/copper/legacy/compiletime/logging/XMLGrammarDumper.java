package edu.umn.cs.melt.copper.legacy.compiletime.logging;

import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarName;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LexicalDisambiguationGroup;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.lalr1.LALR1DFA;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.lalr1.LALR1State;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.lalr1.LALR1StateItem;
import edu.umn.cs.melt.copper.legacy.compiletime.finiteautomaton.lalrengine.lalr1.LALR1Transition;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.AcceptAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.FullReduceAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ParseAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ParseActionVisitor;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ReadOnlyParseTable;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ShiftAction;

public class XMLGrammarDumper extends GrammarDumper implements ParseActionVisitor<String,RuntimeException>
{
	private Document document;
	private Element dumpTop;
	private Element currentCell;
	private PrintStream finalOutputStream;
	private GrammarSource grammar;
	private LALR1DFA dfa;
	private ReadOnlyParseTable parseTable;
	
	private Hashtable<Object,Integer> numbering;


	public XMLGrammarDumper(PrintStream finalOutputStream,
			                GrammarSource grammar,
			                LALR1DFA dfa,
			                ReadOnlyParseTable parseTable)
	throws ParserConfigurationException
	{
		super();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
		dumpTop = (Element) document.appendChild(document.createElement("copper_spec"));
		this.finalOutputStream = finalOutputStream;
		this.grammar = grammar;
		this.dfa = dfa;
		this.parseTable = parseTable;

		numbering = new Hashtable<Object,Integer>();
	}
	
	@Override
	public void logXML()
	{
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		  
		try
		{
			transformer = tFactory.newTransformer();
		}
		catch (TransformerConfigurationException e)
		{
			throw new FatalCompileErrorException(e);
		}
		
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new PrintWriter(finalOutputStream));
		try
		{
			transformer.transform(source, result);
		}
		catch (TransformerException e)
		{
			throw new FatalCompileErrorException(e);
		}
	}
	
	@Override
	public void logHTML()
	{
		StreamSource xslt = new StreamSource(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("etc/dumpformat.xml")));
		logHTML(xslt);
	}
	
	public void logHTML(StreamSource xslt)
	{
		Source xml = new DOMSource(document);
		TransformerFactory fact = TransformerFactory.newInstance();
		Transformer tr = null;
		try { tr = fact.newTransformer(xslt); }
		catch(TransformerException ex) { throw new FatalCompileErrorException(ex.getMessage()); }
		StringWriter wr = new StringWriter();
		StreamResult output = new StreamResult(wr);
		try { tr.transform(xml,output); }
		catch(TransformerException ex) { throw new FatalCompileErrorException(ex.getMessage()); }
		finalOutputStream.print(wr.toString());
	}

	@Override
	public void dumpPreamble()
	{
		int i = 0;
		for(GrammarName gn : grammar.getContainedGrammars())
		{
			Element grammarElement = (Element) dumpTop.appendChild(document.createElement("grammar"));
			grammarElement.setAttribute("tag","g" + i);
			grammarElement.setAttribute("id",gn.toString());
			if(grammar.hasDisplayName(gn.getName()))
			{
				Element displayNameElement = (Element) grammarElement.appendChild(document.createElement("displayname"));
				displayNameElement.setTextContent(grammar.getDisplayName(gn.getName()));				
			}
			numbering.put(gn,i++);
		}
	}

	@Override
	public void dumpDisambigGroups()
	{
		int i = 0;
		for(LexicalDisambiguationGroup group : grammar.getDisambiguationGroups())
		{
			Element dgNode = (Element) dumpTop.appendChild(document.createElement("disambig_group"));
			dgNode.setAttribute("tag","dg" + i);
			dgNode.setAttribute("id",group.getName().toString());
			for(Terminal t : group.getMembers())
			{
				Element memberNode = (Element) dgNode.appendChild(document.createElement("member"));
				memberNode.setTextContent("t" + numbering.get(t));
			}
			numbering.put(group,i++);
		}
	}

	@Override
	public void dumpLALR1DFA()
	{
		Element lalrDFANode = (Element) dumpTop.appendChild(document.createElement("lalr_dfa"));
		for(int statenum = 0;statenum <= parseTable.getLastState();statenum++)
		{
			LALR1State state = dfa.getState(statenum);
			Element stateNode = (Element) lalrDFANode.appendChild(document.createElement("state"));
			stateNode.setAttribute("id",String.valueOf(statenum));
			stateNode.setAttribute("tag","ds" + statenum);
			for(LALR1StateItem item : state.getItems())
			{
				Element itemNode = (Element) stateNode.appendChild(document.createElement("item"));
				itemNode.setAttribute("production","p" + numbering.get(item.getProd()));
				itemNode.setAttribute("marker",String.valueOf(item.getPosition()));
				for(Terminal lookahead : dfa.getLookahead(state,item))
				{
					Element lookaheadNode = (Element) itemNode.appendChild(document.createElement("lookahead"));
					lookaheadNode.setTextContent("t" + numbering.get(lookahead));
				}
			}
			for(LALR1Transition transition : dfa.getTransitions(state))
			{
				Element transitionNode = (Element) stateNode.appendChild(document.createElement("transition"));
				GrammarSymbol label = transition.getLabel();
				if(label instanceof Terminal) transitionNode.setAttribute("label","t" + numbering.get(label));
				else /* if(label instanceof NonTerminal) */ transitionNode.setAttribute("label","nt" + numbering.get(label));
				transitionNode.setAttribute("dest","ds" + dfa.getLabel(transition.getDest()));
			}
		}
	}

	@Override
	public void dumpNonTerminals()
	{
		int i = 0;
		for(NonTerminal nt : grammar.getNT())
		{
			Element ntElement = (Element) dumpTop.appendChild(document.createElement("nonterminal"));
			ntElement.setAttribute("tag","nt" + i);
			ntElement.setAttribute("id",nt.toString());
			if(grammar.getOwner(nt) != null && numbering.containsKey(grammar.getOwner(nt)))
			{
				ntElement.setAttribute("owner","g" + numbering.get(grammar.getOwner(nt)));
			}
			if(grammar.hasDisplayName(nt.getId()))
			{
				Element displayNameElement = (Element) ntElement.appendChild(document.createElement("displayname"));
				displayNameElement.setTextContent(grammar.getDisplayName(nt.getId()));
			}
			numbering.put(nt,i++);
		}
	}

	@Override
	public void dumpParseTable()
	{
		Element parseTableElement = (Element) dumpTop.appendChild(document.createElement("parsetable"));
		for(int statenum = 0;statenum <= parseTable.getLastState();statenum++)
		{
			Element stateElement = (Element) parseTableElement.appendChild(document.createElement("state"));
			stateElement.setAttribute("tag","tr" + statenum);
			stateElement.setAttribute("id",String.valueOf(statenum));
			
			if(parseTable.hasShiftable(statenum))
			{
				if(parseTable.hasLayout(statenum))
				{
					for(Terminal layout : parseTable.getLayout(statenum))
					{
						Element layoutElement = (Element) stateElement.appendChild(document.createElement("layout"));
						layoutElement.setAttribute("tag","t" + numbering.get(layout));
						for(Terminal t : parseTable.getShiftableFollowingLayout(statenum,layout))
						{
							Element followElement = (Element) layoutElement.appendChild(document.createElement("follow"));
							followElement.setTextContent("t" + numbering.get(t));
						}
					}
				}
	
				if(parseTable.hasPrefixes(statenum))
				{
					for(Terminal prefix : parseTable.getPrefixes(statenum))
					{
						Element prefixElement = (Element) stateElement.appendChild(document.createElement("prefix"));
						prefixElement.setAttribute("tag","t" + numbering.get(prefix));
						for(Terminal t : parseTable.getShiftableFollowingPrefix(statenum,prefix))
						{
							Element followElement = (Element) prefixElement.appendChild(document.createElement("follow"));
							followElement.setTextContent("t" + numbering.get(t));
						}
					}
				}
				
				for(Terminal sym : parseTable.getShiftable(statenum))
				{
					currentCell = (Element) stateElement.appendChild(document.createElement("parse_cell"));
					currentCell.setAttribute("id","t" + numbering.get(sym));
					for(ParseAction action : parseTable.getParseActions(statenum,sym))
					{
						action.acceptVisitor(this);
					}
				}
			}

			if(parseTable.hasGotoable(statenum))
			{
				for(NonTerminal sym : parseTable.getGotoable(statenum))
				{
					ShiftAction action = parseTable.getGotoAction(statenum,sym);

					Element cellElement = (Element) stateElement.appendChild(document.createElement("goto_cell"));
					cellElement.setAttribute("id","nt" + numbering.get(sym));
					Element gotoElement = (Element) cellElement.appendChild(document.createElement("goto"));
					gotoElement.setAttribute("dest","tr" + action.getDestState());
				}
			}

		}
	}

	@Override
	public void dumpPrecedenceGraph()
	{
		Element precGraphElement = (Element) dumpTop.appendChild(document.createElement("precgraph"));
		for(Terminal t : grammar.getT())
		{
			if(!grammar.getPrecedenceRelationsGraph().hasVertex(t)) continue;
			Element vertexElement = (Element) precGraphElement.appendChild(document.createElement("vertex"));
			vertexElement.setAttribute("tag","t" + numbering.get(t));
			for(Terminal u : grammar.getT())
			{
				if(grammar.getPrecedenceRelationsGraph().hasVertex(u) &&
				   grammar.getPrecedenceRelationsGraph().hasEdge(t,u))
				{
					Element edgeElement = (Element) precGraphElement.appendChild(document.createElement("edge"));
					edgeElement.setAttribute("submits","t" + numbering.get(t));
					edgeElement.setAttribute("dominates","t" + numbering.get(u));
				}
			}
		}
	}

	@Override
	public void dumpProductions()
	{
		int i = 0;
		for(NonTerminal nt : grammar.getNT())
		{
			if(!grammar.pContains(nt)) continue;
			for(Production p : grammar.getP(nt))
			{
				Element productionElement = (Element) dumpTop.appendChild(document.createElement("production"));
				productionElement.setAttribute("tag","p" + i);
				productionElement.setAttribute("id",p.toString());
				if(grammar.getOwner(p) != null && numbering.containsKey(grammar.getOwner(p)))
				{
					productionElement.setAttribute("owner","g" + numbering.get(grammar.getOwner(p)));
				}
				productionElement.setAttribute("name",p.getName().toString());
				if(grammar.hasDisplayName(p.getName()))
				{
					Element displayNameElement = (Element) productionElement.appendChild(document.createElement("displayname"));
					displayNameElement.setTextContent(grammar.getDisplayName(nt.getId()));
				}
				Element lhsElement = (Element) productionElement.appendChild(document.createElement("lhs"));
				lhsElement.setTextContent("nt" + numbering.get(p.getLeft()));
				for(GrammarSymbol s : p.getRight())
				{
					Element rhsElement = (Element) productionElement.appendChild(document.createElement("rhssym"));
					if(s instanceof Terminal) rhsElement.setTextContent("t" + numbering.get(s));
					else if(s instanceof NonTerminal) rhsElement.setTextContent("nt" + numbering.get(s));
				}
				numbering.put(p,i++);
			}
		}
	}

	@Override
	public void dumpTerminals()
	{
		int i = 0;
		for(Terminal t : grammar.getT())
		{
			Element terminalElement = (Element) dumpTop.appendChild(document.createElement("terminal"));
			terminalElement.setAttribute("tag","t" + i);
			terminalElement.setAttribute("id",t.toString());
			
			if(grammar.getOwner(t) != null && numbering.containsKey(grammar.getOwner(t)))
			{
				terminalElement.setAttribute("owner","g" + numbering.get(grammar.getOwner(t)));
			}
			if(grammar.hasDisplayName(t.getId()))
			{
				Element displayNameElement = (Element) terminalElement.appendChild(document.createElement("displayname"));
				displayNameElement.setTextContent(grammar.getDisplayName(t.getId()));
			}
			numbering.put(t,i++);
		}
	}

	@Override
	public void dumpPostamble()
	{
	}

	public String visitAcceptAction(AcceptAction action)
	throws RuntimeException
	{
		currentCell.appendChild(document.createElement("accept"));
		return null;
	}

	public String visitFullReduceAction(FullReduceAction action)
	throws RuntimeException
	{
		Element reduceElement = (Element) currentCell.appendChild(document.createElement("reduce"));
		reduceElement.setAttribute("prod","p" + numbering.get(action.getProd()));
		return null;
	}

	public String visitShiftAction(ShiftAction action)
	throws RuntimeException
	{
		Element shiftElement = (Element) currentCell.appendChild(document.createElement("shift"));
		shiftElement.setAttribute("dest","tr" + action.getDestState());
		return null;
	}
}
