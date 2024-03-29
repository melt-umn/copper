package edu.umn.cs.melt.copper.compiletime.skins.cup;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.InterfaceErrorMessage;
import edu.umn.cs.melt.copper.compiletime.pipeline.AuxiliaryMethods;
import edu.umn.cs.melt.copper.compiletime.pipeline.SpecParser;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

public class CupParsingProcess implements SpecParser<ParserBean>
{
	@Override
	public ParserBean parseSpec(ParserCompilerParameters args)
	throws UnsupportedOperationException
	{
		ParserBean spec;
		CompilerLogger logger;
		logger = AuxiliaryMethods.getOrMakeLogger(args);
		ArrayList< Pair<String,Object> > inputs = args.getInputs(); 

		ArrayList< Pair<String,Reader> > files = new ArrayList< Pair<String,Reader> >(); 

		boolean failed = false;
				
		for(Pair<String,Object> i : inputs)
		{
			Reader second = null;
			if(i.second() instanceof Reader) second = (Reader) i.second();
			else
			{
				try
				{
					second = new FileReader(i.second().toString());
				}
				catch(FileNotFoundException ex)
				{
					logger.log(new InterfaceErrorMessage("Grammar file not found: '" + i.second() + "'"));
					failed = true;
				}
			}
			files.add(Pair.cons(i.first(),second));
		}
		if(failed)
		{
			return null;
		}

		try
		{
			spec = edu.umn.cs.melt.copper.compiletime.skins.cup.CupSkinParser.parseGrammar(files,logger);
		}
		catch(Exception ex)
		{
			if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) ex.printStackTrace(System.err);
			else System.err.println("An unexpected fatal error has occurred in parsing. Run with -vv for debug information.");
			return null;
		}
		if(args.getPackageName() != null) spec.setPackageDecl(args.getPackageName());
		if(args.getParserName() != null && !args.getParserName().equals("")) spec.setClassName(args.getParserName());
		return spec;
	}

	@Override
	public Set<String> getCustomSwitches()
	{
		return null;
	}

	@Override
	public String customSwitchUsage()
	{
		return "";
	}

	@Override
	public int processCustomSwitch(ParserCompilerParameters args,
			String[] cmdline, int index)
	{
		return -1;
	}
}
