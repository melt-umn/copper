package edu.umn.cs.melt.copper.compiletime.skins.xml;

import java.util.ArrayList;
import java.util.Hashtable;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Grammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public abstract class VersionSpecificXMLSkinParser extends DefaultHandler {
	public static Hashtable<String, Class<? extends VersionSpecificXMLSkinParser>> versionSpecificParsers = new Hashtable<String, Class<? extends VersionSpecificXMLSkinParser>>();	

	static {
		versionSpecificParsers.put(edu.umn.cs.melt.copper.compiletime.skins.xml.v0p7.XMLSkinElements.COPPER_NAMESPACE, edu.umn.cs.melt.copper.compiletime.skins.xml.v0p7.XMLSkinParser.class);
		versionSpecificParsers.put(edu.umn.cs.melt.copper.compiletime.skins.xml.v0p8.XMLSkinElements.COPPER_NAMESPACE, edu.umn.cs.melt.copper.compiletime.skins.xml.v0p8.XMLSkinParser.class);
	}

	public CompilerLogger logger;
	public InputPosition loc;
	public Locator locator;
	public Hashtable<CopperElementName,Grammar> grammars;
	ArrayList<ParserBean> parsers;
	
	protected ParserBean getCurrentParser()
	{
		return parsers.get(parsers.size() - 1);
	}
	
	protected void addParser(ParserBean parser)
	{
		parsers.add(parser);
	}

	@Override
	public void setDocumentLocator(Locator locator)
	{
		this.locator = locator;
	}

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

	public abstract void init();
	
}
