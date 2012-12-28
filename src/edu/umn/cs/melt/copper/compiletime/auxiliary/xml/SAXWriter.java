package edu.umn.cs.melt.copper.compiletime.auxiliary.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * This interface is meant to be implemented by classes that output
 * XML via a SAX-like interface.
 * 
 * This code is &copy; 2012 August Schwerdfeger and licensed under the
 * terms of the GNU Lesser General Public License, version 3 or later.
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface SAXWriter extends ContentHandler
{
	/**
	 * Writes a string to the output. Implementing classes should behave identically on calls
	 * to {@code string(s)} and {@code characters(s.toCharArray(),0,s.length()}.
	 * @param s The string to write.
	 */
	public void string(String s) throws SAXException;
	
	
	/**
	 * Writes a character array to the output verbatim, in a {@code CDATA} block.
	 * @see ContentHandler#characters(char[], int, int)
	 */
	public void verbatimCharacters(char[] ch,int start,int length) throws SAXException;
	
	/**
	 * Writes a string to the output verbatim, in a {@code CDATA} block.
	 * Implementing classes should behave identically on calls to {@code verbatimString(s)}
	 * and {@code verbatimCharacters(s.toCharArray(),0,s.length()}.
	 * @param s The string to write.
	 */
	public void verbatimString(String s) throws SAXException;
	
	/**
	 * Starts an element with attributes specified as stand-alone parameters rather than part of
	 * an {@link org.xml.sax.Attributes} object.
	 * @see #startElement(String, String, String, org.xml.sax.Attributes) 
	 * @param attrNamesFullyQualified Whether the provided
	 * @param atts The attributes. If {@code attrNamesFullyQualified} is {@code false} the attributes must be
	 *             provided as pairs of strings -- local name and value. If {@code attrNamesFullyQualified} is
	 *             {@code true} the attributes must be provided as 4-tuples -- namespace URI, local name, fully
	 *             qualified name, and value.
	 * @throws SAXException If there is an error in starting the element.
	 */
	public void startElement(String uri, String localName, String qName, boolean attrNamesFullyQualified,String... atts)
	throws SAXException;

	public void writeFullElement(String uri, String localName, String qName, Attributes atts)
	throws SAXException;
	
	public void writeFullElement(String uri, String localName, String qName, boolean attrNamesFullyQualified,String... atts)
	throws SAXException;
}
