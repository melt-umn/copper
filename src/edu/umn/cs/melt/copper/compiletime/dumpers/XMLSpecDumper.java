package edu.umn.cs.melt.copper.compiletime.dumpers;

import java.io.IOException;
import java.io.PrintStream;

import org.xml.sax.SAXException;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.concretesyntax.skins.xml.ParserSpecXMLPrinter;
import edu.umn.cs.melt.copper.main.CopperDumpType;

public class XMLSpecDumper implements Dumper
{
	private ParserBean spec;
	
	public XMLSpecDumper(ParserBean spec)
	{
		this.spec = spec;
	}

	@Override
	public void dump(CopperDumpType type, PrintStream out)
	throws IOException,UnsupportedOperationException
	{
		if(type != CopperDumpType.XML_SPEC) throw new UnsupportedOperationException(getClass().getName() + " only supports dump type " + CopperDumpType.XML_SPEC);
		
		try
		{
			spec.acceptVisitor(new ParserSpecXMLPrinter(out));
		}
		catch(SAXException ex)
		{
			throw new IOException(ex);
		}
		
		out.flush();
	}

}
