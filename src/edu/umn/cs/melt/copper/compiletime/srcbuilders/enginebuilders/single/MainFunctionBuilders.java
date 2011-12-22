package edu.umn.cs.melt.copper.compiletime.srcbuilders.enginebuilders.single;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public class MainFunctionBuilders
{
	public static String buildSingleDFAParserMainFunction(String packageDecl,
			                                             String parserName,
			                                             String rootType,
			                                             String errorType,
			                                             boolean gatherStatistics,
			                                             boolean isPretty,
			                                             String runtimeQuietLevel)
	{
		String ancillaries = "";
		ancillaries += "    public static void main(String[] args)\n";
		ancillaries += "    {\n";
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
		ancillaries += "            	    if(i >= args.length) throw new " + CopperParserException.class.getName() + "(\"A filename must be provided with switch '-f'\");\n";
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
		ancillaries += "              	  throw new " + CopperParserException.class.getName() + "(\"File not found: '\" + filename + \"'\");\n";
		ancillaries += "            	}\n";
		ancillaries += "        	}\n";
		ancillaries += "            " + SingleDFAEngine.class.getName() + "<" + rootType + "," + errorType + "> engine = new " + ((packageDecl == null || packageDecl.equals("")) ? "" : (packageDecl + ".")) + parserName + "();\n";
		ancillaries += "            Object parseTree = engine.parse(reader,filename);\n";
		ancillaries += "            engine.runPostParseCode(parseTree);\n";
		ancillaries += "        }\n";
		ancillaries += "        catch(" + Exception.class.getName() + " ex)\n";
		ancillaries += "        {\n";
		if(runtimeQuietLevel.equals("INFO")) ancillaries += "            ex.printStackTrace(System.err);\n";
		else                                 ancillaries += "            System.err.println(ex.getMessage());\n";
		ancillaries += "            System.exit(1);\n";
		ancillaries += "        }\n";
		ancillaries += "    }\n";
		return ancillaries;
	}
	
	public static String buildSingleDFAParserAncillaries(String packageDecl,
            String parserName,
            boolean gatherStatistics,
            boolean isPretty,
            String runtimeQuietLevel)
	{
		String ancillaries = "";
		ancillaries += "    public " + parserName + "() {}\n";
		ancillaries += "    \n";
		return ancillaries;
	}
}
