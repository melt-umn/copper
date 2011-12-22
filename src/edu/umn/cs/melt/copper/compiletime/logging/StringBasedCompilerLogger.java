package edu.umn.cs.melt.copper.compiletime.logging;

import java.io.PrintStream;
import java.util.Queue;
import java.util.TreeSet;
import java.util.logging.Logger;

import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class StringBasedCompilerLogger extends CompilerLogger
{
	public StringBasedCompilerLogger()
	{
		logger = Logger.getLogger("edu.umn.cs.melt.copper.StringBasedCompilerLogger");
		if(logger.getParent().getHandlers().length > 0) logger.getParent().removeHandler(logger.getParent().getHandlers()[0]);
		handler = new CompilerLogHandler();
		logger.addHandler(handler);
	}
	
	public void flushTicks()
	{
		Queue<CompilerLogMessage> ticks = handler.flushTickQueue();
		for(CompilerLogMessage msg : ticks)
		{
			printMessage(msg);
		}
	}
	
	public void flushMessages()
	throws CopperException
	{
		Queue<CompilerLogMessage> messages = handler.flushMessageQueue();
		CompilerLogMessage throwExceptionForMsg = null;
		long requiresFailure = -1;
		for(CompilerLogMessage msg : messages)
		{
			if(msg.isRequiresImmediateFailure())
			{
				throwExceptionForMsg = msg;
			}
		}
		for(CompilerLogMessage msg : messages)
		{
			printMessage(msg);
			if(msg.sort.isErrorLevel()) requiresFailure = msg.sequenceNumber;
		}
		if(throwExceptionForMsg != null)
		{
			throwException(throwExceptionForMsg);
		}
		else if(requiresFailure != -1)
		{
			throwException(new CompilerLogMessage(requiresFailure + 1,System.currentTimeMillis(),"Errors raised in compilation",CompilerLogMessageSort.ERROR,null,true));
		}
	}
	
	public void throwException(CompilerLogMessage msg)
	throws CopperException
	{
		switch(msg.sort)
		{
		case FATAL_ERROR:
			throw new FatalCompileErrorException(msg.getMessage());
		default:
			throw new CopperException(msg.getMessage());
		}
	}
	
	public void printMessage(CompilerLogMessage msg)
	{
		String location;
		PrintStream out;
		out = this.out;
		
		switch(msg.sort)
		{
		case DEBUG:
			if(msg.getLocation() == null && msg.getVirtualLocation() == null) location = "Global debug info:\n  ";
			else if(msg.getLocation() == null) location = "Debug info at " + virtualLocMessagetoString(msg) + ":\n  ";
			else location = "Debug info at " + msg.getLocation() + ":\n  ";
			break;
		case TAGGED_STATUS:
			if(msg.getLocation() == null && msg.getVirtualLocation() == null) location = "Global:\n  ";
			else if(msg.getLocation() == null) location = "At " + virtualLocMessagetoString(msg) + ":\n  ";
			else location = "At " + msg.getLocation() + ":\n  ";
			break;
		case WARNING:
			if(msg.getLocation() == null && msg.getVirtualLocation() == null) location = "Global warning:\n  ";
			else if(msg.getLocation() == null) location = "Warning at " + virtualLocMessagetoString(msg) + ":\n  ";
			else location = "Warning at " + msg.getLocation() + ":\n  ";
			break;
		case LEXICAL_CONFLICT:
			location = "Lexical ambiguity at " + ambiguityLocToString(msg);
			break;
		case PARSE_TABLE_CONFLICT:
			location = "Parse table conflict at " + tableCelltoString(msg) + ":\n  ";
			break;
		case UNRESOLVED_LEXICAL_CONFLICT:
			location = "Unresolvable lexical ambiguity at " + unresolvableAmbiguityLocToString(msg);
			break;
		case UNRESOLVED_CONFLICT:
			location = "Unresolvable parse table conflict at " + tableCelltoString(msg) + ":\n  ";
			break;
		case ERROR:
		case PARSING_ERROR:
			if(msg.getLocation() == null && msg.getVirtualLocation() == null) location = "Global error:\n  ";
			else if(msg.getLocation() == null) location = "Error at " + virtualLocMessagetoString(msg) + ":\n  ";
		    else location = "Error at " + msg.getLocation() + ":\n  ";
			break;
		case FATAL_ERROR:
			if(msg.getLocation() == null && msg.getVirtualLocation() == null) location = "Global fatal error:\n  ";
			else if(msg.getLocation() == null) location = "Fatal error at " + virtualLocMessagetoString(msg) + ":\n  ";
			else location = "Fatal error at " + msg.getLocation() + ":\n  ";
			break;
		case TICK:
		case STATUS:
		case DUMP:
		default:
			location = "";
			break;
		}
		if(!location.equals("")) location = "\n" + location;
		out.print(location + msg.getMessage() + (location.equals("") ? "" : "\n"));
	}
	
	private String ambiguityLocToString(CompilerLogMessage msg)
	{
		if(msg.getParameters().length < 4 ||
		   !(msg.getParameters()[1] instanceof TreeSet<?>)) return "<malformed>";
		String location = "";
		int size = ((TreeSet<?>) msg.getParameters()[1]).size();
		location =  size + " scanner state" + (size == 1 ? "" : "s");
		return location;
	}

	private String unresolvableAmbiguityLocToString(CompilerLogMessage msg)
	{
		String placeHash = PrettyPrinter.iterablePrettyPrint((TreeSet<?>) msg.getParameters()[1],"   ",14);
		if(msg.getParameters().length < 4 ||
		   !(msg.getParameters()[1] instanceof TreeSet<?>)) return "<malformed>";
		String location = "";
		int size = ((TreeSet<?>) msg.getParameters()[1]).size();
		location = "parser state" + (size == 1 ? "" : "s") + "\n" + placeHash;
		return location;
	}

	private String tableCelltoString(CompilerLogMessage msg)
	{
		if(msg.getParameters().length < 4 ||
		   !(msg.getParameters()[1] instanceof Integer) ||
		   !(msg.getParameters()[3] instanceof String)) return "<malformed>";
		String location = "";
		location += "parser state " + msg.getParameters()[1] + ", on terminal " + msg.getParameters()[3];
		return location;
	}
	
	private String virtualLocMessagetoString(CompilerLogMessage msg)
	{
		if(msg.getParameters().length < 5 ||
		   !(msg.getParameters()[3] instanceof Integer) ||
		   !(msg.getParameters()[4] instanceof Long)) return "<malformed>";
		String location = "";
		location += "line " + msg.getVirtualLocation().getLine() + ", column " + msg.getVirtualLocation().getColumn();
		if(msg.getVirtualLocation().getFileName().length() > 40) location += "\n         ";
		location += " in file " + msg.getVirtualLocation().getFileName();
		location += "\n         (parser state: " + ((Integer) msg.getParameters()[3]) + "; real character index: " + ((Long) msg.getParameters()[4]) + ")";
		return location;
	}
}
