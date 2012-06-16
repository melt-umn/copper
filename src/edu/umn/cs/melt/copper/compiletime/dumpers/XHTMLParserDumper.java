package edu.umn.cs.melt.copper.compiletime.dumpers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.BitSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LR0ItemSet;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.logging.FatalCompileErrorException;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTableConflict;
import edu.umn.cs.melt.copper.main.CopperDumpType;

public class XHTMLParserDumper extends FullParserDumper
{
	private Document document;
	private Element dumpTop;

	public XHTMLParserDumper(PSSymbolTable symbolTable, ParserSpec spec,
			LR0DFA dfa, LRLookaheadAndLayoutSets lookahead,
			LRParseTable parseTable, TransparentPrefixes prefixes)
	throws ParserConfigurationException
	{
		super(symbolTable, spec, dfa, lookahead, parseTable, prefixes);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
		dumpTop = (Element) document.appendChild(document.createElement("copper_spec"));
	}

	@Override
	public void dump(CopperDumpType type, PrintStream out)
	throws IOException,	UnsupportedOperationException
	{
		if(type != CopperDumpType.HTML && type != CopperDumpType.XML) throw new UnsupportedOperationException(getClass().getName() + " only supports dump type " + CopperDumpType.HTML + " and " + CopperDumpType.XML);
			
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

		generateXMLDump();

		DOMSource source = new DOMSource(document);

		switch(type)
		{
		case HTML:
			StreamSource xslt = new StreamSource(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("etc/dumpformat.xml")));
			TransformerFactory fact = TransformerFactory.newInstance();
			Transformer tr = null;
			try { tr = fact.newTransformer(xslt); }
			catch(TransformerException ex) { throw new FatalCompileErrorException(ex.getMessage()); }
			StringWriter wr = new StringWriter();
			StreamResult output = new StreamResult(wr);
			try { tr.transform(source,output); }
			catch(TransformerException ex) { throw new FatalCompileErrorException(ex.getMessage()); }
			out.print(wr.toString());
			break;
		case XML:
			StreamResult result = new StreamResult(new PrintWriter(out));
			try
			{
				transformer.transform(source, result);
			}
			catch (TransformerException e)
			{
				throw new FatalCompileErrorException(e);
			}		
		}

	}
	
	private void generateXMLDump()
	{
		// GRAMMARS
		for(int i = spec.grammars.nextSetBit(0);i >= 0;i = spec.grammars.nextSetBit(i+1))
		{
			CopperASTBean gn = symbolTable.get(i); 
			Element grammarElement = (Element) dumpTop.appendChild(document.createElement("grammar"));
			grammarElement.setAttribute("tag",String.valueOf(i));
			grammarElement.setAttribute("id",generateName(gn));
			if(gn.hasDisplayName())
			{
				Element displayNameElement = (Element) grammarElement.appendChild(document.createElement("displayname"));
				displayNameElement.setTextContent(gn.getDisplayName());				
			}
		}
		
		
		
		// TERMINALS
		for(int i = spec.terminals.nextSetBit(0);i >= 0;i = spec.terminals.nextSetBit(i+1))
		{
			CopperASTBean t = symbolTable.get(i);
			Element terminalElement = (Element) dumpTop.appendChild(document.createElement("terminal"));
			terminalElement.setAttribute("tag",generateTag(i));
			terminalElement.setAttribute("id",generateName(t));
			
			if(spec.owners[i] != -1 && spec.grammars.get(spec.owners[i]))
			{
				terminalElement.setAttribute("owner",String.valueOf(spec.owners[i]));
			}
			if(t.hasDisplayName())
			{
				Element displayNameElement = (Element) terminalElement.appendChild(document.createElement("displayname"));
				displayNameElement.setTextContent(t.getDisplayName());
			}
		}
		
		
		

		// NONTERMINALS
		for(int i = spec.nonterminals.nextSetBit(0);i >= 0;i = spec.nonterminals.nextSetBit(i+1))
		{
			CopperASTBean nt = symbolTable.get(i);
			Element nonTerminalElement = (Element) dumpTop.appendChild(document.createElement("nonterminal"));
			nonTerminalElement.setAttribute("tag",generateTag(i));
			nonTerminalElement.setAttribute("id",generateName(nt));
			
			if(spec.owners[i] != -1 && spec.grammars.get(spec.owners[i]))
			{
				nonTerminalElement.setAttribute("owner",String.valueOf(spec.owners[i]));
			}
			if(nt.hasDisplayName())
			{
				Element displayNameElement = (Element) nonTerminalElement.appendChild(document.createElement("displayname"));
				displayNameElement.setTextContent(nt.getDisplayName());
			}
		}
		
		
		
		
		// PRODUCTIONS
		for(int i = spec.productions.nextSetBit(0);i >= 0;i = spec.productions.nextSetBit(i+1))
		{
			CopperASTBean p = symbolTable.get(i);
			Element productionElement = (Element) dumpTop.appendChild(document.createElement("production"));
			productionElement.setAttribute("tag",generateTag(i));
			productionElement.setAttribute("id",generateName(p));

			if(spec.owners[i] != -1 && spec.grammars.get(spec.owners[i]))
			{
				productionElement.setAttribute("owner",String.valueOf(spec.owners[i]));
			}
			
			productionElement.setAttribute("name",generateName(p));
			if(p.hasDisplayName())
			{
				Element displayNameElement = (Element) productionElement.appendChild(document.createElement("displayname"));
				displayNameElement.setTextContent(p.getDisplayName());
			}
			Element lhsElement = (Element) productionElement.appendChild(document.createElement("lhs"));
			lhsElement.setTextContent(generateTag(spec.pr.getLHS(i)));
			for(int j = 0;j < spec.pr.getRHSLength(i);j++)
			{
				Element rhsElement = (Element) productionElement.appendChild(document.createElement("rhssym"));
				rhsElement.setTextContent(generateTag(spec.pr.getRHSSym(i,j)));
			}
		}
		
		// PRECEDENCE GRAPH
		Element precGraphElement = (Element) dumpTop.appendChild(document.createElement("precgraph"));
		for(int t = spec.terminals.nextSetBit(0);t >= 0;t = spec.terminals.nextSetBit(t+1))
		{
			//if(!grammar.getPrecedenceRelationsGraph().hasVertex(t)) continue;
			Element vertexElement = (Element) precGraphElement.appendChild(document.createElement("vertex"));
			vertexElement.setAttribute("tag",generateTag(t));
			for(int u = spec.terminals.nextSetBit(0);u >= 0;u = spec.terminals.nextSetBit(u+1))
			{
				if(spec.t.precedences.hasEdge(t,u))
				{
					Element edgeElement = (Element) precGraphElement.appendChild(document.createElement("edge"));
					edgeElement.setAttribute("submits",generateTag(t));
					edgeElement.setAttribute("dominates",generateTag(u));
				}
			}
		}
		
		
//		boolean success = true;
//		byte[] svg = null;
//		ProcessBuilder pb = new ProcessBuilder("dot","-Tsvg");
//		try
//		{
//			Process p = pb.start();
//			p.getOutputStream().write(spec.t.precedences.toEquivalenceClassDot("PrecedenceGraph").getBytes("UTF-8"));
//			p.getOutputStream().close();
//			int errorlevel = 0;
//			try
//			{
//				errorlevel = p.waitFor();
//			}
//			catch(InterruptedException ex)
//			{
//				errorlevel = 1;
//			}
//			if(errorlevel != 0)
//			{
//				success = false;
//			}
//			else
//			{
//				ByteArrayBuffer buffer = new ByteArrayBuffer();
//				buffer.write(p.getInputStream());
//				svg = new byte[buffer.size()];
//				System.arraycopy(buffer.getRawData(),0,svg,0,buffer.size());
//			}
//		}
//		catch(IOException ex)
//		{
//			success = false;
//		}
//		
//		if(success)
//		{
//			Element imgElement = (Element) precGraphElement.appendChild(document.createElement("img"));
//          // This is Base64 from the Apache Commons Codec package.
//			Base64 coder = new Base64();
//			imgElement.setAttribute("src","data:image/svg+xml;base64," + coder.encodeToString(svg));
//		}
		
		
		
		
		// DISAMBIGUATION FUNCTIONS
		for(int i = spec.disambiguationFunctions.nextSetBit(0);i >= 0;i = spec.disambiguationFunctions.nextSetBit(i+1))
		{
			CopperASTBean df = symbolTable.get(i);
			Element dfNode = (Element) dumpTop.appendChild(document.createElement("disambig_group"));
			dfNode.setAttribute("tag",generateTag(i));
			dfNode.setAttribute("id",df.getName().toString());
			for(int j = spec.df.getMembers(i).nextSetBit(0);j >= 0;j = spec.df.getMembers(i).nextSetBit(j+1))
			{
				Element memberNode = (Element) dfNode.appendChild(document.createElement("member"));
				memberNode.setTextContent(generateTag(j));
			}
			// TODO: Put an element in for disambiguation groups that disambiguate to a fixed terminal rather than through code.
		}
		
		
		
		
		// LALR(1) DFA
		Element lalrDFANode = (Element) dumpTop.appendChild(document.createElement("lalr_dfa"));
		for(int statenum = 1;statenum < dfa.size();statenum++)
		{
			LR0ItemSet state = dfa.getItemSet(statenum);
			Element stateNode = (Element) lalrDFANode.appendChild(document.createElement("state"));
			stateNode.setAttribute("id",String.valueOf(statenum));
			stateNode.setAttribute("tag","ds" + statenum);
			for(int item = 0;item < state.size();item++)
			{
				Element itemNode = (Element) stateNode.appendChild(document.createElement("item"));
				itemNode.setAttribute("production",generateTag(state.getProduction(item)));
				itemNode.setAttribute("marker",String.valueOf(state.getPosition(item)));
				for(int la = lookahead.getLookahead(statenum,item).nextSetBit(0);la >= 0;la = lookahead.getLookahead(statenum,item).nextSetBit(la+1))
				{
					Element lookaheadNode = (Element) itemNode.appendChild(document.createElement("lookahead"));
					lookaheadNode.setTextContent(generateTag(la));
				}
			}
			for(int X = dfa.getTransitionLabels(statenum).nextSetBit(0);X >= 0;X = dfa.getTransitionLabels(statenum).nextSetBit(X+1))
			{
				Element transitionNode = (Element) stateNode.appendChild(document.createElement("transition"));
				transitionNode.setAttribute("label",generateTag(X));
				transitionNode.setAttribute("dest","ds" + dfa.getTransition(statenum,X));
			}
		}
		
		
		
		
		// PARSE TABLE
		Element parseTableElement = (Element) dumpTop.appendChild(document.createElement("parsetable"));
		for(int statenum = 1;statenum < parseTable.size();statenum++)
		{
			Element stateElement = (Element) parseTableElement.appendChild(document.createElement("state"));
			stateElement.setAttribute("tag","tr" + statenum);
			stateElement.setAttribute("id",String.valueOf(statenum));

			// TODO Remove nonterminals from the 'validLA' sets in the parse table.
			BitSet shiftable = new BitSet();
			shiftable.or(parseTable.getValidLA(statenum));
			shiftable.andNot(spec.nonterminals);
				
			if(!shiftable.isEmpty())
			{
				for(int layout = lookahead.getLayout(statenum).nextSetBit(0);layout >= 0;layout = lookahead.getLayout(statenum).nextSetBit(layout+1))
				{
					Element layoutElement = (Element) stateElement.appendChild(document.createElement("layout"));
					layoutElement.setAttribute("tag",generateTag(layout));
					for(int t = shiftable.nextSetBit(0);t >= 0;t = shiftable.nextSetBit(t+1))
					{
						Element followElement = (Element) layoutElement.appendChild(document.createElement("follow"));
						followElement.setTextContent(generateTag(t));
					}
				}
	
				for(int prefix = prefixes.getPrefixes(statenum).nextSetBit(0);prefix >= 0;prefix = prefixes.getPrefixes(statenum).nextSetBit(prefix+1))
				{
					Element prefixElement = (Element) stateElement.appendChild(document.createElement("prefix"));
					prefixElement.setAttribute("tag",generateTag(prefix));
					for(int t = prefixes.getFollowingTerminals(statenum,prefix).nextSetBit(0);t >= 0;t = prefixes.getFollowingTerminals(statenum,prefix).nextSetBit(t+1))
					{
						Element followElement = (Element) prefixElement.appendChild(document.createElement("follow"));
						followElement.setTextContent(generateTag(t));
					}
				}
				
				for(int t = shiftable.nextSetBit(0);t >= 0;t = shiftable.nextSetBit(t+1))
				{
					Element currentCell = (Element) stateElement.appendChild(document.createElement("parse_cell"));
					currentCell.setAttribute("id",generateTag(t));
					
					switch(parseTable.getActionType(statenum, t))
					{
					case LRParseTable.SHIFT:
					case LRParseTable.REDUCE:
						addParseAction(currentCell,t,parseTable.getActionType(statenum,t),parseTable.getActionParameter(statenum,t));
						break;
					case LRParseTable.CONFLICT:
						LRParseTableConflict conflict = parseTable.getConflict(parseTable.getActionParameter(statenum,t));
						if(conflict.shift != -1) addParseAction(currentCell,t,LRParseTable.SHIFT,conflict.shift);
						for(int r = conflict.reduce.nextSetBit(0);r >= 0;r = conflict.reduce.nextSetBit(r+1))
						{
							addParseAction(currentCell,t,LRParseTable.REDUCE,r);
						}
						break;
					default:
					}
				}
			}

			// TODO Remove nonterminals from the 'validLA' sets in the parse table.
			BitSet gotoable = new BitSet();
			gotoable.or(parseTable.getValidLA(statenum));
			gotoable.andNot(spec.terminals);

			for(int nt = gotoable.nextSetBit(0);nt >= 0;nt = gotoable.nextSetBit(nt+1))
			{
				Element currentCell = (Element) stateElement.appendChild(document.createElement("goto_cell"));
				currentCell.setAttribute("id",generateTag(nt));
				
				addParseAction(currentCell,nt,LRParseTable.GOTO,parseTable.getActionParameter(statenum,nt));
			}

		}
	}

	private String generateName(CopperASTBean bean)
	{
		if(symbolTable.getParser(spec.parser).isUnitary() || !spec.grammars.get(spec.owners[symbolTable.get(bean)])) return bean.getName().toString();
		else return symbolTable.getGrammar(spec.owners[symbolTable.get(bean)]).getName() + "$" + bean.getName();
	}
	
	private String generateTag(int i)
	{
		return String.valueOf(i);
	}
	
	private void addParseAction(Element currentCell,int sym,byte type,int parameter)
	{
		if(type == LRParseTable.ACCEPT && sym == spec.getEOFTerminal())
		{
			currentCell.appendChild(document.createElement("accept"));
		}
		else if(type == LRParseTable.GOTO && spec.nonterminals.get(sym))
		{
			Element gotoElement = (Element) currentCell.appendChild(document.createElement("goto"));
			gotoElement.setAttribute("dest","tr" + parameter);			
		}
		else if(type == LRParseTable.SHIFT)
		{
			Element shiftElement = (Element) currentCell.appendChild(document.createElement("shift"));
			shiftElement.setAttribute("dest","tr" + parameter);
		}
		else /*if(type == LRParseTable.REDUCE) */
		{
			Element reduceElement = (Element) currentCell.appendChild(document.createElement("reduce"));
			reduceElement.setAttribute("prod",generateTag(parameter));
		}		
	}
}
