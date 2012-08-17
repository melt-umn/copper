package edu.umn.cs.melt.copper.compiletime.skins.xml;

import java.io.Reader;
import java.util.ArrayList;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.pipeline.AuxiliaryMethods;
import edu.umn.cs.melt.copper.compiletime.pipeline.SpecParser;
import edu.umn.cs.melt.copper.compiletime.pipeline.SpecParserParameters;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class XMLParsingProcess implements SpecParser<ParserBean>
{
	@Override
	public ParserBean parseSpec(SpecParserParameters args)
	throws UnsupportedOperationException
	{
		ParserBean spec;
		CompilerLogger logger;
		logger = AuxiliaryMethods.getOrMakeLogger(args);
		ArrayList< Pair<String,Reader> > files = args.getFiles(); 

		try
		{
			spec = new XMLSkinParser(files,logger).parse();
		}
		catch(CopperException ex)
		{
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) ex.printStackTrace(System.err);
			return null;
		}
		catch(Exception ex)
		{
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) ex.printStackTrace(System.err);
			else System.err.println("An unexpected fatal error has occurred. Run with -vv for debug information.");
			return null;
		}
		if(args.getPackageDecl() != null) spec.setPackageDecl(args.getPackageDecl());
		if(args.getParserName() != null && !args.getParserName().equals("")) spec.setClassName(args.getParserName());
		
		return spec;
	}
}
