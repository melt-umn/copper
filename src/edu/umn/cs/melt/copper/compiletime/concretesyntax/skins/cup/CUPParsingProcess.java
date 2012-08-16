package edu.umn.cs.melt.copper.compiletime.concretesyntax.skins.cup;

import java.io.Reader;
import java.util.ArrayList;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.loggingnew.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.pipeline.AuxiliaryMethods;
import edu.umn.cs.melt.copper.compiletime.pipeline.SpecParser;
import edu.umn.cs.melt.copper.compiletime.pipeline.SpecParserParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

public class CUPParsingProcess implements SpecParser<ParserBean>
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
			spec = edu.umn.cs.melt.copper.compiletime.concretesyntax.skins.cup.CupSkinParserNew.parseGrammar(files,logger);
		}
		catch(Exception ex)
		{
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) ex.printStackTrace(System.err);
			return null;
		}
		if(args.getPackageDecl() != null) spec.setPackageDecl(args.getPackageDecl());
		if(args.getParserName() != null && !args.getParserName().equals("")) spec.setClassName(args.getParserName());
		return spec;
	}
}
