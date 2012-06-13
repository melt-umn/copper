package edu.umn.cs.melt.copper.compiletime.auxiliary.xml;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A barebones implementation of {@link SAXWriter} that produces pretty-printed XML.
 * Should work for any output that does not contain namespace prefixes.
 * 
 * This code is &copy; 2012 August Schwerdfeger and licensed under the
 * terms of the GNU Lesser General Public License, version 3 or later.
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class SAXWriterImpl implements SAXWriter
{
	private class ElementStackElement
	{
		public String element;
		public int childCount;
		
		public ElementStackElement(String element)
		{
			this.element = element;
			childCount = 0;
		}
	}
	
	private Hashtable<String,String> prefixes = new Hashtable<String,String>();
	private Hashtable<String,Stack<String>> reversePrefixes = new Hashtable<String,Stack<String>>();
	private Stack<ElementStackElement> elementStack = new Stack<ElementStackElement>();
	private boolean prettyPrint = false;
	private boolean indentAttributes = false;
	private String elementIndentation = "";
	private PrintStream out;
	
	public SAXWriterImpl(PrintStream out)
	{
		this.out = out;
	}
	
	public SAXWriterImpl(PrintStream out,String elementIndentation,boolean indentAttributes)
	{
		this.out = out;
		prettyPrint = true;
		this.elementIndentation = elementIndentation;
		this.indentAttributes = indentAttributes;
	}
	
	private static Attributes buildAttributes(String[] atts,boolean attrNamesFullyQualified)
	{
		AttributesImpl attrs = new AttributesImpl();
		if(attrNamesFullyQualified)
		{
			for(int i = 0;i < (atts.length - 3);i += 4)
			{
				attrs.addAttribute(atts[i],atts[i+1],atts[i+2],"xs:string",atts[i+3]);
			}
		}
		else
		{
			for(int i = 0;i < (atts.length - 1);i += 2)
			{
				attrs.addAttribute(null,atts[i],atts[i],"xs:string",atts[i+1]);
			}
		}
		return attrs;
	}

	@Override
	public void writeFullElement(String uri, String localName, String qName, boolean attrNamesFullyQualified,String... atts)
	throws SAXException
	{
		writeFullElement(uri,localName,qName,buildAttributes(atts,attrNamesFullyQualified));
	}

	@Override
	public void startElement(String uri, String localName, String qName, boolean attrNamesFullyQualified,String... atts)
	throws SAXException
	{
		startElement(uri,localName,qName,buildAttributes(atts,attrNamesFullyQualified));
	}

	@Override
	public void writeFullElement(String uri, String localName, String qName, Attributes atts)
	throws SAXException
	{
		writeElement(uri,localName,qName,atts,true);
	}


	@Override
	public void string(String s)
	throws SAXException
	{
		characters(s.toCharArray(),0,s.length());
	}
	
	@Override
	public void verbatimString(String s) throws SAXException
	{
		verbatimCharacters(s.toCharArray(),0,s.length());
	}
	
	
	
	
	
	@Override
	public void setDocumentLocator(Locator locator)
	{
		// Intentionally blank
	}

	@Override
	public void startDocument()
	throws SAXException
	{
		if(elementStack != null && !elementStack.isEmpty())
		{
			throw new SAXException("startDocument() received in the middle of outputting a document");
		}
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		elementStack.clear();
		prefixes.clear();
	}

	@Override
	public void endDocument()
	throws SAXException
	{
		if(elementStack == null || !elementStack.isEmpty())
		{
			throw new SAXException("endDocument() received prematurely");
		}
		prefixes.clear();
		if(prettyPrint) out.print("\n");
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
	throws SAXException
	{
		System.err.println("Prefix mappings are not properly supported");
		prefixes.put(uri,prefix);
		if(!reversePrefixes.containsKey(prefix)) reversePrefixes.put(prefix,new Stack<String>());
		reversePrefixes.get(prefix).push(uri);
	}

	@Override
	public void endPrefixMapping(String prefix)
	throws SAXException
	{
		System.err.println("Prefix mappings are not properly supported");
		if(!reversePrefixes.containsKey(prefix) || reversePrefixes.get(prefix).isEmpty())
		{
			throw new SAXException("endPrefixMapping() received for a prefix that was never mapped");
		}
		String uri = reversePrefixes.get(prefix).pop();
		prefixes.remove(uri);
	}
	
	private String getElementName(String uri,String localName,String qName)
	throws SAXException
	{
		if(qName != null) return qName;
		else if(uri != null)
		{
			if(!prefixes.containsKey(uri))
			{
				throw new SAXException("URI '" + uri + "' has no prefix");
			}
			return prefixes.get(uri) + ":" + localName;
		}
		else return localName;
	}
	
	private void writeElement(String uri, String localName, String qName, Attributes atts, boolean close)
	throws SAXException
	{
		String elementName = getElementName(uri,localName,qName);
		if(!elementStack.isEmpty()) elementStack.peek().childCount++;
		elementStack.push(new ElementStackElement(elementName));
		if(prettyPrint)
		{
			out.print("\n");
			for(int i = 0;i < elementStack.size() - 1;i++) out.print(elementIndentation);
		}
		out.print("<" + elementName);
		if(atts != null)
		{
			for(int i = 0;i < atts.getLength();i++)
			{
				if(atts.getValue(i) == null || atts.getValue(i).isEmpty()) continue;
				String name = atts.getQName(i);
				if(name.isEmpty()) name = atts.getLocalName(i);
				if(indentAttributes && i > 0)
				{
					out.print("\n");
					for(int j = 0;j < elementStack.size() - 1;j++) out.print(elementIndentation);
					for(int j = 0;j < elementName.length() + 1;j++) out.print(" ");
				}
				out.print(" ");
				out.print(name + "=\"");
				PrintWriter outPW = new PrintWriter(out);
				try { Entities.XML.escape(outPW,atts.getValue(i)); }
				catch(IOException ex) { throw new SAXException(ex); }
				outPW.flush();
				out.print("\"");
			}
		}
		if(close)
		{
			elementStack.pop();
			out.print("/");
		}
		out.print(">");		
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts)
	throws SAXException
	{
		writeElement(uri,localName,qName,atts,false);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
	throws SAXException
	{
		String elementName = getElementName(uri,localName,qName);
		if(!elementStack.peek().element.equals(elementName))
		{
			throw new SAXException("Misplaced end to element " + elementName);
		}
		ElementStackElement endedElement = elementStack.pop();
		if(prettyPrint && endedElement.childCount > 0)
		{
			out.print("\n");
			for(int i = 0;i < elementStack.size();i++) out.print(elementIndentation);
		}
		out.print("</" + elementName + ">");
	}

	@Override
	public void characters(char[] ch, int start, int length)
	throws SAXException
	{
		PrintWriter outPW = new PrintWriter(out);
		try { Entities.XML.escape(outPW,new String(ch,start,length)); }
		catch(IOException ex) { throw new SAXException(ex); }
		outPW.flush();
	}

	@Override
	public void verbatimCharacters(char[] ch, int start, int length)
	throws SAXException
	{
		out.print("<![CDATA[");
		out.print(new String(ch,start,length));
		out.print("]]>");		
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
	throws SAXException
	{
		// Ignorable whitespace is ignorable.
	}

	@Override
	public void processingInstruction(String target, String data)
	throws SAXException
	{
	}

	@Override
	public void skippedEntity(String name)
	throws SAXException
	{
	}
}
