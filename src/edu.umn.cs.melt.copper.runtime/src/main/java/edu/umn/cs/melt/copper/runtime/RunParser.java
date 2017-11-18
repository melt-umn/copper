package edu.umn.cs.melt.copper.runtime;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;

import edu.umn.cs.melt.copper.runtime.engines.CopperParser;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

/**
 * This class contains a {@code main} method that dynamically loads any {@link CopperParser} object, using its
 * default constructor, and invokes its {@link CopperParser#parse(Reader, String)} method. Full command-line syntax is
 * 
 * {@code java RunParser parser-class-name [-v] [-f input-file]}
 * 
 * If {@code -f} is specified, the parser will read from the given file instead of from standard input.
 * 
 * If {@code -v} is specified, any exceptions thrown will have their full stack trace displayed instead of just their
 * message.
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt; 
 *
 */
public class RunParser
{
    public static void main(String[] args)
    {
        boolean useFile = false;
        boolean runVerbose = false;
        String filename = "<stdin>";
        Reader reader = null;
        try
        {
            Class<?> className;
            if(args.length < 1) throw new CopperParserException("The first argument to RunParser must be a parser class name");
            try
            {
            	className = Class.forName(args[0]);
            }
            catch(ClassNotFoundException ex)
            {
            	throw new CopperParserException("No such class: " + args[0]);
            }
            
	    	int i;
    	    for(i = 1;i < args.length;i++)
        	{
        		if(args[i].charAt(0) != '-') break;
	        	else if(args[i].equals("-f"))
    	    	{
        			i++;
            	    if(i >= args.length) throw new CopperParserException("A filename must be provided with switch '-f'");
                    useFile = true;
                	filename = args[i];
 	               continue;
    	        }
	        	else if(args[i].equals("-v"))
	        	{
	        		runVerbose = true;
	        	}
        	}
            if(!useFile) reader = new InputStreamReader(System.in);
            else
    		{
    	        try
        	    {
                	reader = new FileReader(filename);
            	}
            	catch(FileNotFoundException ex)
            	{
              	  throw new CopperParserException("File not found: '" + filename + "'");
            	}
        	}
            CopperParser<?,?> engine = (CopperParser<?,?>) className.newInstance();
            Object parseTree = engine.parse(reader,filename);
            try
            {
            	Method postParseCode = className.getMethod("runPostParseCode",Object.class);
            	postParseCode.invoke(engine,parseTree);
            }
            catch(NoSuchMethodException ex) { /* Intentionally blank */ }
        }
        catch(Exception ex)
        {
            if(runVerbose) ex.printStackTrace();
            else System.err.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            System.exit(1);
        }
    }

}
