package edu.umn.cs.melt.copper.compiletime.skins.xml;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementName;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Grammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.ParserSpecProcessor;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class XMLSkinParser extends DefaultHandler 
{
	private ArrayList< Pair<String,Reader> > files;
	private CompilerLogger logger;
	
    public XMLSkinParser(ArrayList< Pair<String,Reader> > files,CompilerLogger logger)
    throws IOException,CopperException
    {
    	this.files = files;
    	this.logger = logger;
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
    	
    	Schema schema = null;
    	SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		URL schemaURL = this.getClass().getClassLoader().getResource("resources/edu/umn/cs/melt/copper/compiletime/XMLSkinSchema.xsd");
		if(schemaURL == null) if(logger.isLoggable(CompilerLevel.QUIET)) logger.logError(new GenericMessage(CompilerLevel.QUIET,"Cannot load XML skin schema. This generally means Copper was improperly built.",true,true));
    	try
    	{
    		schema = schemaFactory.newSchema(schemaURL);
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
		
		if(parsers.size() > 1)
		{
			for(int i = 1; i < parsers.size();i++)
			{
				if(logger.isLoggable(CompilerLevel.QUIET)) logger.log(new GenericLocatedMessage(CompilerLevel.QUIET,(InputPosition) parsers.get(i).getLocation(),"Superfluous parser " + parsers.get(i).getDisplayName() +": spec must contain exactly one parser element",true,false));
			}
		}
		logger.flush();
		boolean hasError = !parsers.isEmpty() ? ParserSpecProcessor.normalizeParser(parsers.get(0),logger) : true;
		if(hasError) return null;
		return parsers.get(0);
	}
	
	private InputPosition loc;
	private Locator locator;
	private Hashtable<CopperElementName,Grammar> grammars = new Hashtable<CopperElementName,Grammar>();
	private ArrayList<ParserBean> parsers = new ArrayList<ParserBean>();
	private VersionSpecificXMLSkinParser versionSpecificParser = null;
	
	@Override
	public void setDocumentLocator(Locator locator)
	{
		this.locator = locator;
		if(versionSpecificParser != null)
		{
			versionSpecificParser.setDocumentLocator(locator);
		}
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
		if(versionSpecificParser == null)
		{
			if(VersionSpecificXMLSkinParser.versionSpecificParsers.containsKey(uri))
			{
				try
				{
					versionSpecificParser = VersionSpecificXMLSkinParser.versionSpecificParsers.get(uri).newInstance();
					versionSpecificParser.init();
					versionSpecificParser.grammars = grammars;
					versionSpecificParser.loc = loc;
					versionSpecificParser.logger = logger;
					versionSpecificParser.setDocumentLocator(locator);
					versionSpecificParser.parsers = parsers;
				}
				catch(IllegalAccessException ex)
				{
					fatalError(new SAXParseException(ex.getMessage(), locator));
				}
				catch(InstantiationException ex)
				{
					fatalError(new SAXParseException(ex.getMessage(), locator));					
				}
			}
			else
			{
				fatalError(new SAXParseException("Unknown namespace: " + uri + ". Copper's XML skin schemas are misconfigured.", locator));
			}
		}
		versionSpecificParser.startElement(uri, localName, qName, attributes);
	}
	
	
	@Override
	public void endElement(String uri, String localName, String qName)
	throws SAXException
	{
		if(versionSpecificParser != null)
		{
			versionSpecificParser.endElement(uri, localName, qName);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
	throws SAXException
	{
		if(versionSpecificParser != null)
		{
			versionSpecificParser.characters(ch, start, length);
		}
	}
	
	@Override
	public void endDocument()
	{
		versionSpecificParser = null;
	}
}
