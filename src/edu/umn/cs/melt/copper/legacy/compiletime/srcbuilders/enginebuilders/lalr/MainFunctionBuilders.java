package edu.umn.cs.melt.copper.legacy.compiletime.srcbuilders.enginebuilders.lalr;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.IntermediateConsNode;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.IntermediateNode;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.syntaxtranslator.AttributeConsolidator;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.LALREngine;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.LALRParseTreePrettyPrinter;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.StringBasedCompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetree.plain.ParseTree;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class MainFunctionBuilders
{
	public static String buildGeneralParserAncillaries(String packageDecl,
            String parserName,
            boolean gatherStatistics,
            boolean isPretty,
            String runtimeQuietLevel)
	{
		String ancillaries = "";
		ancillaries += "    public static void main(String[] args)\n";
		ancillaries += "    {\n";
		ancillaries += "        " + CompilerLogger.class.getName() + " logger = new " + StringBasedCompilerLogger.class.getName() + "();\n";
		ancillaries += "        logger.setLevel(" + CompilerLogMessageSort.class.getName() + ".ERROR);\n";
		ancillaries += "        logger.setOut(System.err);\n";
		ancillaries += "        boolean useFile = false;\n";
		ancillaries += "        String filename = \"<stdin>\";\n";
		ancillaries += "        " + Reader.class.getName() + " reader = null;\n";
		ancillaries += "        try\n";
		ancillaries += "        {\n";
		ancillaries += "	        int i;\n";
		ancillaries += "    	    for(i = 0;i < args.length;i++)\n";
		ancillaries += "        	{\n";
		ancillaries += "        		if(args[i].charAt(0) != '-') break;\n";
		ancillaries += "	        	else if(args[i].equals(\"-f\"))\n";
		ancillaries += "    	    	{\n";
		ancillaries += "        			i++;\n";
		ancillaries += "            	    if(i >= args.length) logger.logErrorMessage(" + CompilerLogMessageSort.class.getName() + ".ERROR,null,\"A filename must be provided with switch '-f'\");\n";
		ancillaries += "                    useFile = true;\n";
		ancillaries += "                	filename = args[i];\n";
		ancillaries += " 	               continue;\n";
		ancillaries += "    	        }\n";
		ancillaries += "        	}\n";
		ancillaries += "            if(!useFile) reader = new " + InputStreamReader.class.getName() + "(System.in);\n";
		ancillaries += "            else\n";
		ancillaries += "    		{\n";
		ancillaries += "    	        try\n";
		ancillaries += "        	    {\n";
		ancillaries += "                	reader = new " + FileReader.class.getName() + "(filename);\n";
		ancillaries += "            	}\n";
		ancillaries += "            	catch(" + FileNotFoundException.class.getName() + " ex)\n";
		ancillaries += "            	{\n";
		ancillaries += "              	  logger.logErrorMessage(" + CompilerLogMessageSort.class.getName() + ".ERROR,null,\"File not found: '\" + filename + \"'\");\n";
		ancillaries += "            	}\n";
		ancillaries += "        	}\n";
		ancillaries += "            " + LALREngine.class.getName() + " engine = new " + ((packageDecl == null || packageDecl.equals("") || packageDecl.equals("NONE")) ? "" : (packageDecl + ".")) + parserName + "(reader,logger);\n";
		ancillaries += "            engine.startEngine(" + InputPosition.class.getName() + ".initialPos(filename));\n";
		ancillaries += "            Object parseTree = engine.runEngine();\n";
		if(!gatherStatistics) ancillaries += "            if(parseTree instanceof " + ParseTree.class.getName() + ") " + LALRParseTreePrettyPrinter.class.getName() + ".run(" + isPretty + ",(" + ParseTree.class.getName() + ") parseTree,logger,System.out);\n";
		ancillaries += "            if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".TICK))\n";
		ancillaries += "            {\n";
		ancillaries += "                logger.logTick(1,engine.retrieveStatistics().toString());\n";
		ancillaries += "            }\n";
		ancillaries += "        }\n";
		ancillaries += "        catch(" + Exception.class.getName() + " ex)\n";
		ancillaries += "        {\n";
		ancillaries += "            if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".DEBUG)) ex.printStackTrace(System.err);\n";
		ancillaries += "            else System.err.println(ex.getMessage());\n";
		ancillaries += "            System.exit(1);\n";
		ancillaries += "        }\n";
		ancillaries += "    }\n";
		return ancillaries;
	}

	public static String buildGrammarParserAncillaries(String qualifiedParserName)
	{
		String ancillaries = "";
		ancillaries += "    public static " + GrammarSource.class.getName() + " parseGrammar(" + ArrayList.class.getName()  + "< " + Pair.class.getName() + "<String," + Reader.class.getName() + "> > files," + CompilerLogger.class.getName() + " logger)\n";
		ancillaries += "    throws " + IOException.class.getName() + "," + CopperException.class.getName() + "\n";
		ancillaries += "    {\n";
		ancillaries += "        " + IntermediateNode.class.getName() + " node = null;\n";
		ancillaries += "        for(" + Pair.class.getName() + "<String," + Reader.class.getName() + "> file : files)\n";
		ancillaries += "        {\n";
		ancillaries += "            " + LALREngine.class.getName() + " engine = new " + qualifiedParserName + "(file.second(),logger);\n";
		ancillaries += "            engine.startEngine(new " + InputPosition.class.getName() + "(" + InputPosition.class.getName() + ".initialPos(),file.first()));\n";
		ancillaries += "            Object parseTree = null;\n"; 
		ancillaries += "            try { parseTree = engine.runEngine(); }\n";
		ancillaries += "            catch(" + CopperException.class.getName() + " ex)\n";
		ancillaries += "            {\n";
		ancillaries += "                throw ex;\n";
		ancillaries += "            }\n";
        ancillaries += "            if(parseTree != null)\n";
        ancillaries += "            {\n";
        ancillaries += "            	node = " + IntermediateConsNode.class.getName() + ".cons(parseTree,node);\n";
        ancillaries += "            }\n";
		ancillaries += "        }\n";
		ancillaries += "        if(logger.isLoggable(" + CompilerLogMessageSort.class.getName() + ".TICK)) logger.logTick(1,\"\\nBuilding grammar AST\");\n";
        ancillaries += "        " + AttributeConsolidator.class.getName() + " consolidator = new " + AttributeConsolidator.class.getName() + "(logger);\n";
        ancillaries += "        node.acceptVisitor(consolidator,null);\n";
        ancillaries += "        return " + MasterController.class.getName() + ".buildAST(logger,consolidator.consolidatedNodes);\n";
		ancillaries += "    }\n";
		return ancillaries;
	}
	
}
