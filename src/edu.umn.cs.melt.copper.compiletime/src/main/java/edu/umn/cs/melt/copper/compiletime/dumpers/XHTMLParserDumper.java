package edu.umn.cs.melt.copper.compiletime.dumpers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.BitSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0ItemSet;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ContextSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import edu.umn.cs.melt.copper.main.CopperDumpType;

public class XHTMLParserDumper extends FullParserDumper
{
	private static final String COPPER_DUMP_NAMESPACE = "http://melt.cs.umn.edu/copper/xmlns/xmldump/0.9";
	
	public XHTMLParserDumper(PSSymbolTable symbolTable, ParserSpec spec,
			ContextSets contextSets, LR0DFA dfa, LRLookaheadAndLayoutSets lookahead,
			LRParseTable parseTable, TransparentPrefixes prefixes)
	throws ParserConfigurationException
	{
		super(symbolTable, spec, contextSets, dfa, lookahead, parseTable, prefixes);
	}

	private static interface XMLOutputWriter<EX extends Exception> {
		public void writeStartElement(String name) throws EX;
		public void writeDefaultNamespace(String namespaceURI) throws EX;		
		public void writeCharacters(String text) throws EX;
		public void writeAttribute(String key, String value) throws EX;
		public void writeEndElement() throws EX;
	}
	
	private static class XMLOutputWriterDOM implements XMLOutputWriter<RuntimeException> {

		public Document document;
		private Element currentElement;
		
		public XMLOutputWriterDOM() throws ParserConfigurationException {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
			currentElement = null;
		}

		@Override
		public void writeStartElement(String name) {
			if(currentElement == null) {
				currentElement = (Element) document.appendChild(document.createElementNS(COPPER_DUMP_NAMESPACE, name));
			} else {
				currentElement = (Element) currentElement.appendChild(document.createElementNS(COPPER_DUMP_NAMESPACE, name));				
			}
		}
		
		@Override
		public void writeDefaultNamespace(String namespaceURI) {
		}
		
		@Override
		public void writeCharacters(String text) {
			currentElement.setTextContent(text);
		}

		@Override
		public void writeAttribute(String key, String value) {
			currentElement.setAttribute(key, value);
		}

		@Override
		public void writeEndElement() {
			Node n = currentElement.getParentNode();
			if(n.getNodeType() == Node.ELEMENT_NODE) {
				currentElement = (Element) currentElement.getParentNode();
			} else {
				currentElement = null;
			}
		}
	}
	
	private static class XMLOutputWriterSAX implements XMLOutputWriter<XMLStreamException> {
		private XMLStreamWriter sw;
		
		public XMLOutputWriterSAX(Writer w) throws XMLStreamException {
			sw = XMLOutputFactory.newInstance().createXMLStreamWriter(w);
		}

		@Override
		public void writeStartElement(String name) throws XMLStreamException {
			sw.writeStartElement(name);
		}

		@Override
		public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
			sw.writeDefaultNamespace(namespaceURI);
		}

		@Override
		public void writeCharacters(String text) throws XMLStreamException {
			sw.writeCharacters(text);
		}

		@Override
		public void writeAttribute(String key, String value) throws XMLStreamException {
			sw.writeAttribute(key, value);
		}

		@Override
		public void writeEndElement() throws XMLStreamException {
			sw.writeEndElement();
		}
		
	}

	@Override
	public void dump(CopperDumpType type, PrintStream out)
	throws IOException,	UnsupportedOperationException
	{
		if(type != CopperDumpType.HTML && type != CopperDumpType.XML) throw new UnsupportedOperationException(getClass().getName() + " only supports dump type " + CopperDumpType.HTML + " and " + CopperDumpType.XML);
			
		switch(type)
		{
		case HTML:
			XMLOutputWriterDOM xmloutD = null;
			try {
				xmloutD = new XMLOutputWriterDOM();
			} catch(ParserConfigurationException ex) {
				throw new IOException(ex);
			}
			generateXMLDump(xmloutD);
			DOMSource source = new DOMSource(xmloutD.document);
			StreamSource xslt = new StreamSource(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("resources/edu/umn/cs/melt/copper/compiletime/dumpformat.xslt")));
			TransformerFactory fact = TransformerFactory.newInstance();
			Transformer tr = null;
			try { tr = fact.newTransformer(xslt); }
			catch(TransformerException ex) { throw new IOException(ex); }
			StringWriter wr = new StringWriter();
			StreamResult output = new StreamResult(wr);
			try { tr.transform(source,output); }
			catch(TransformerException ex) { throw new IOException(ex); }
			out.print(wr.toString());
			break;
		case XML:
			XMLOutputWriterSAX xmloutS = null;
			try {
				xmloutS = new XMLOutputWriterSAX(new PrintWriter(out));
				xmloutS.sw.writeStartDocument();
				generateXMLDump(xmloutS);
				xmloutS.sw.writeEndDocument();
				xmloutS.sw.flush();
			} catch(XMLStreamException ex) {
				throw new IOException(ex);
			}
		default:
			break;		
		}

	}
	
	private <EX extends Exception> void generateXMLDump(XMLOutputWriter<EX> xmlout) throws EX {
		xmlout.writeStartElement("copper_spec");
		xmlout.writeDefaultNamespace(COPPER_DUMP_NAMESPACE);
		
		// GRAMMARS
		for(int i = spec.grammars.nextSetBit(0);i >= 0;i = spec.grammars.nextSetBit(i+1))
		{
			CopperASTBean gn = symbolTable.get(i);
			xmlout.writeStartElement("grammar");
			xmlout.writeAttribute("tag",String.valueOf(i));
			xmlout.writeAttribute("id",generateName(gn));
			if(gn.hasDisplayName())
			{
				xmlout.writeStartElement("displayname");
				xmlout.writeCharacters(gn.getDisplayName());
				xmlout.writeEndElement();
			}
			xmlout.writeEndElement();
		}
		
		
		
		// TERMINALS
		for(int i = spec.terminals.nextSetBit(0);i >= 0;i = spec.terminals.nextSetBit(i+1))
		{
			CopperASTBean t = symbolTable.get(i);
			xmlout.writeStartElement("terminal");
			xmlout.writeAttribute("tag",generateTag(i));
			xmlout.writeAttribute("id",generateName(t));
			
			if(spec.owners[i] != -1 && spec.grammars.get(spec.owners[i]))
			{
				xmlout.writeAttribute("owner",String.valueOf(spec.owners[i]));
			}
			if(t.hasDisplayName())
			{
				xmlout.writeStartElement("displayname");
				xmlout.writeCharacters(t.getDisplayName());
				xmlout.writeEndElement();
			}
			xmlout.writeEndElement();
		}
		
		
		

		// NONTERMINALS
		for(int i = spec.nonterminals.nextSetBit(0);i >= 0;i = spec.nonterminals.nextSetBit(i+1))
		{
			CopperASTBean nt = symbolTable.get(i);
			xmlout.writeStartElement("nonterminal");
			xmlout.writeAttribute("tag",generateTag(i));
			xmlout.writeAttribute("id",generateName(nt));
			
			if(spec.owners[i] != -1 && spec.grammars.get(spec.owners[i]))
			{
				xmlout.writeAttribute("owner",String.valueOf(spec.owners[i]));
			}
			if(nt.hasDisplayName())
			{
				xmlout.writeStartElement("displayname");
				xmlout.writeCharacters(nt.getDisplayName());
				xmlout.writeEndElement();
			}
			
			xmlout.writeEndElement();
		}
		
		
		
		
		// PRODUCTIONS
		for(int i = spec.productions.nextSetBit(0);i >= 0;i = spec.productions.nextSetBit(i+1))
		{
			CopperASTBean p = symbolTable.get(i);
			xmlout.writeStartElement("production");
			xmlout.writeAttribute("tag",generateTag(i));
			xmlout.writeAttribute("id",generateName(p));

			if(spec.owners[i] != -1 && spec.grammars.get(spec.owners[i]))
			{
				xmlout.writeAttribute("owner",String.valueOf(spec.owners[i]));
			}
			
			xmlout.writeAttribute("name",generateName(p));
			if(p.hasDisplayName())
			{
				xmlout.writeStartElement("displayname");
				xmlout.writeCharacters(p.getDisplayName());
				xmlout.writeEndElement();
			}
			xmlout.writeStartElement("lhs");
			xmlout.writeCharacters(generateTag(spec.pr.getLHS(i)));
			xmlout.writeEndElement();
			for(int j = 0;j < spec.pr.getRHSLength(i);j++)
			{
				xmlout.writeStartElement("rhssym");
				xmlout.writeCharacters(generateTag(spec.pr.getRHSSym(i,j)));
				xmlout.writeEndElement();
			}
			xmlout.writeEndElement();
		}
		
		// PRECEDENCE GRAPH
		xmlout.writeStartElement("precgraph");
		for(int t = spec.terminals.nextSetBit(0);t >= 0;t = spec.terminals.nextSetBit(t+1))
		{
			//if(!grammar.getPrecedenceRelationsGraph().hasVertex(t)) continue;
			xmlout.writeStartElement("vertex");
			xmlout.writeAttribute("tag",generateTag(t));
			xmlout.writeEndElement();
			for(int u = spec.terminals.nextSetBit(0);u >= 0;u = spec.terminals.nextSetBit(u+1))
			{
				if(spec.t.precedences.hasEdge(t,u))
				{
					xmlout.writeStartElement("edge");
					xmlout.writeAttribute("submits",generateTag(t));
					xmlout.writeAttribute("dominates",generateTag(u));
					xmlout.writeEndElement();
				}
			}
		}
		xmlout.writeEndElement();
		
		
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
//			xmlout.writeStartElement("img");
//          // This is Base64 from the Apache Commons Codec package.
//			Base64 coder = new Base64();
//			xmlout.writeAttribute("src","data:image/svg+xml;base64," + coder.encodeToString(svg));
//		}
		
		
		
		
		// DISAMBIGUATION FUNCTIONS
		for(int i = spec.disambiguationFunctions.nextSetBit(0);i >= 0;i = spec.disambiguationFunctions.nextSetBit(i+1))
		{
			CopperASTBean df = symbolTable.get(i);
			xmlout.writeStartElement("disambig_group");
			xmlout.writeAttribute("tag",generateTag(i));
			xmlout.writeAttribute("id",df.getName().toString());
			for(int j = spec.df.getMembers(i).nextSetBit(0);j >= 0;j = spec.df.getMembers(i).nextSetBit(j+1))
			{
				xmlout.writeStartElement("member");
				xmlout.writeCharacters(generateTag(j));
				xmlout.writeEndElement();
			}
			xmlout.writeEndElement();
			// TODO: Put an element in for disambiguation groups that disambiguate to a fixed terminal rather than through code.
		}
		

		
		// CONTEXT SETS
		
		xmlout.writeStartElement("context_sets");
		
		for(int i = spec.nonterminals.nextSetBit(0); i >= 0; i = spec.nonterminals.nextSetBit(i+1))
		{
			xmlout.writeStartElement("first");
			xmlout.writeAttribute("of", String.valueOf(i));
			for(int j = contextSets.getFirst(i).nextSetBit(0); j >= 0; j = contextSets.getFirst(i).nextSetBit(j+1))
			{
				xmlout.writeStartElement("member");
				xmlout.writeCharacters(generateTag(j));
				xmlout.writeEndElement();
			}
			xmlout.writeEndElement();
		}

		for(int i = spec.nonterminals.nextSetBit(0); i >= 0; i = spec.nonterminals.nextSetBit(i+1))
		{
			xmlout.writeStartElement("first_nt");
			xmlout.writeAttribute("of", String.valueOf(i));
			for(int j = contextSets.getFirstNTs(i).nextSetBit(0); j >= 0; j = contextSets.getFirstNTs(i).nextSetBit(j+1))
			{
				xmlout.writeStartElement("member");
				xmlout.writeCharacters(generateTag(j));
				xmlout.writeEndElement();
			}
			xmlout.writeEndElement();
		}

		BitSet syms = spec.terminals;
		while(syms != null)
		{
			for(int i = syms.nextSetBit(0); i >= 0; i = syms.nextSetBit(i+1))
			{
				xmlout.writeStartElement("follow");
				xmlout.writeAttribute("of", String.valueOf(i));
				for(int j = contextSets.getFollow(i).nextSetBit(0); j >= 0; j = contextSets.getFollow(i).nextSetBit(j+1))
				{
					xmlout.writeStartElement("member");
					xmlout.writeCharacters(generateTag(j));
					xmlout.writeEndElement();
				}
				xmlout.writeEndElement();
			}
			syms = (syms == spec.terminals) ? spec.nonterminals : null; 
		}

		xmlout.writeStartElement("nullable");
		for(int i = spec.nonterminals.nextSetBit(0); i >= 0; i = spec.terminals.nextSetBit(i+1))
		{
			if(contextSets.isNullable(i))
			{
				xmlout.writeStartElement("member");
				xmlout.writeCharacters(generateTag(i));
				xmlout.writeEndElement();
			}
		}
		xmlout.writeEndElement();
		xmlout.writeEndElement();
		
		// LALR(1) DFA
		xmlout.writeStartElement("lalr_dfa");
		for(int statenum = 1;statenum < dfa.size();statenum++)
		{
			LR0ItemSet state = dfa.getItemSet(statenum);
			xmlout.writeStartElement("state");
			xmlout.writeAttribute("id",String.valueOf(statenum));
			xmlout.writeAttribute("tag","ds" + statenum);
			for(int item = 0;item < state.size();item++)
			{
				xmlout.writeStartElement("item");
				xmlout.writeAttribute("production",generateTag(state.getProduction(item)));
				xmlout.writeAttribute("marker",String.valueOf(state.getPosition(item)));
				for(int la = lookahead.getLookahead(statenum,item).nextSetBit(0);la >= 0;la = lookahead.getLookahead(statenum,item).nextSetBit(la+1))
				{
					xmlout.writeStartElement("lookahead");
					xmlout.writeCharacters(generateTag(la));
					xmlout.writeEndElement();
				}
				xmlout.writeEndElement();
			}
			for(int X = dfa.getTransitionLabels(statenum).nextSetBit(0);X >= 0;X = dfa.getTransitionLabels(statenum).nextSetBit(X+1))
			{
				xmlout.writeStartElement("transition");
				xmlout.writeAttribute("label",generateTag(X));
				xmlout.writeAttribute("dest","ds" + dfa.getTransition(statenum,X));
				xmlout.writeEndElement();
			}
			xmlout.writeEndElement();
		}
		xmlout.writeEndElement();
		
		
		
		
		// PARSE TABLE
		xmlout.writeStartElement("parsetable");
		for(int statenum = 1;statenum < parseTable.size();statenum++)
		{
			xmlout.writeStartElement("state");
			xmlout.writeAttribute("tag","tr" + statenum);
			xmlout.writeAttribute("id",String.valueOf(statenum));

			// TODO Remove nonterminals from the 'validLA' sets in the parse table.
			BitSet shiftable = new BitSet();
			shiftable.or(parseTable.getValidLA(statenum));
			shiftable.andNot(spec.nonterminals);
				
			if(!shiftable.isEmpty())
			{
				for(int layout = lookahead.getLayout(statenum).nextSetBit(0);layout >= 0;layout = lookahead.getLayout(statenum).nextSetBit(layout+1))
				{
					xmlout.writeStartElement("layout");
					xmlout.writeAttribute("tag",generateTag(layout));
					for(int t = shiftable.nextSetBit(0);t >= 0;t = shiftable.nextSetBit(t+1))
					{
						xmlout.writeStartElement("follow");
						xmlout.writeCharacters(generateTag(t));
						xmlout.writeEndElement();
					}
					xmlout.writeEndElement();
				}
	
				for(int prefix = prefixes.getPrefixes(statenum).nextSetBit(0);prefix >= 0;prefix = prefixes.getPrefixes(statenum).nextSetBit(prefix+1))
				{
					xmlout.writeStartElement("prefix");
					xmlout.writeAttribute("tag",generateTag(prefix));
					for(int t = prefixes.getFollowingTerminals(statenum,prefix).nextSetBit(0);t >= 0;t = prefixes.getFollowingTerminals(statenum,prefix).nextSetBit(t+1))
					{
						xmlout.writeStartElement("follow");
						xmlout.writeCharacters(generateTag(t));
						xmlout.writeEndElement();
					}
					xmlout.writeEndElement();
				}
				
				for(int t = shiftable.nextSetBit(0);t >= 0;t = shiftable.nextSetBit(t+1))
				{
					xmlout.writeStartElement("parse_cell");
					xmlout.writeAttribute("id",generateTag(t));
					
					switch(parseTable.getActionType(statenum, t))
					{
					case LRParseTable.SHIFT:
					case LRParseTable.REDUCE:
						addParseAction(xmlout, t,parseTable.getActionType(statenum,t),parseTable.getActionParameter(statenum,t));
						break;
					case LRParseTable.CONFLICT:
						LRParseTableConflict conflict = parseTable.getConflict(parseTable.getActionParameter(statenum,t));
						if(conflict.shift != -1) addParseAction(xmlout, t,LRParseTable.SHIFT,conflict.shift);
						for(int r = conflict.reduce.nextSetBit(0);r >= 0;r = conflict.reduce.nextSetBit(r+1))
						{
							addParseAction(xmlout, t,LRParseTable.REDUCE,r);
						}
						break;
					default:
					}
					
					xmlout.writeEndElement();
				}
			}

			// TODO Remove nonterminals from the 'validLA' sets in the parse table.
			BitSet gotoable = new BitSet();
			gotoable.or(parseTable.getValidLA(statenum));
			gotoable.andNot(spec.terminals);

			for(int nt = gotoable.nextSetBit(0);nt >= 0;nt = gotoable.nextSetBit(nt+1))
			{
				xmlout.writeStartElement("goto_cell");
				xmlout.writeAttribute("id",generateTag(nt));
				
				addParseAction(xmlout, nt,LRParseTable.GOTO,parseTable.getActionParameter(statenum,nt));
				
				xmlout.writeEndElement();
			}
			xmlout.writeEndElement();
		}
		xmlout.writeEndElement();
		
		xmlout.writeEndElement();
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
	
	private <EX extends Exception> void addParseAction(XMLOutputWriter<EX> xmlout, int sym,byte type,int parameter) throws EX
	{
		if(type == LRParseTable.ACCEPT && sym == spec.getEOFTerminal())
		{
			xmlout.writeStartElement("accept");
			xmlout.writeEndElement();
		}
		else if(type == LRParseTable.GOTO && spec.nonterminals.get(sym))
		{
			xmlout.writeStartElement("goto");
			xmlout.writeAttribute("dest","tr" + parameter);
			xmlout.writeEndElement();
		}
		else if(type == LRParseTable.SHIFT)
		{
			xmlout.writeStartElement("shift");
			xmlout.writeAttribute("dest","tr" + parameter);
			xmlout.writeEndElement();
		}
		else /*if(type == LRParseTable.REDUCE) */
		{
			xmlout.writeStartElement("reduce");
			xmlout.writeAttribute("prod",generateTag(parameter));
			xmlout.writeEndElement();
		}		
	}
}
