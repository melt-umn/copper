package edu.umn.cs.melt.copper.legacy.compiletime.logging;

import java.util.logging.Level;

public enum CompilerLogMessageSort
{
	DEBUG,
	DUMP,
	TICK,
	STATUS,
	TAGGED_STATUS,
	FINAL_REPORT,
	WARNING,
	PARSE_TABLE_CONFLICT,
	LEXICAL_CONFLICT,
	ERROR,
	UNRESOLVED_LEXICAL_CONFLICT,
	UNRESOLVED_CONFLICT,
	PARSING_ERROR,
	FATAL_ERROR;
	
	public Level getLevel()
	{
		switch(this)
		{
		case DEBUG:
			return Level.FINEST;
		case TICK:
			return Level.FINER;
		case DUMP:
			return Level.FINE;
		case STATUS:
		case TAGGED_STATUS:
		case PARSE_TABLE_CONFLICT:
		case LEXICAL_CONFLICT:
			return Level.INFO;
		case WARNING:
		case FINAL_REPORT:
			return Level.WARNING;
		case ERROR:
		case UNRESOLVED_CONFLICT:
		case UNRESOLVED_LEXICAL_CONFLICT:
		case PARSING_ERROR:
		case FATAL_ERROR:
			return Level.SEVERE;
		default:
			return Level.FINEST;
		}
	}
	
	public static CompilerLogMessageSort getQuietSort()       { return ERROR;   }
	public static CompilerLogMessageSort getDefaultSort()     { return WARNING; }
	public static CompilerLogMessageSort getVerboseSort()     { return DUMP;    }
	public static CompilerLogMessageSort getVeryVerboseSort() { return DEBUG;   } 
	
	public boolean isErrorLevel()
	{
		switch(this)
		{
		case UNRESOLVED_LEXICAL_CONFLICT:
		case UNRESOLVED_CONFLICT:
		case ERROR:
		case PARSING_ERROR:
		case FATAL_ERROR:
			return true;
		case DEBUG:
		case TICK:
		case STATUS:
		case DUMP:
		case TAGGED_STATUS:
		case PARSE_TABLE_CONFLICT:
		case LEXICAL_CONFLICT:
		case WARNING:
		case FINAL_REPORT:
		default:
			return false;
		}
	}
	
	public String toString()
	{
		switch(this)
		{
		case DEBUG: return "Debug info";
		case TICK: return "";
		case STATUS: return "";
		case DUMP: return "";
		case FINAL_REPORT: return "";
		case TAGGED_STATUS: return "Info";
		case WARNING: return "Warning";
		case PARSE_TABLE_CONFLICT: return "Parse table conflict";
		case LEXICAL_CONFLICT: return "Lexical ambiguity";
		case UNRESOLVED_LEXICAL_CONFLICT: return "Unresolvable lexical ambiguity";
		case UNRESOLVED_CONFLICT: return "Unresolvable parse table conflict";
		case ERROR: return "Error";
		case PARSING_ERROR: return "Error";
		case FATAL_ERROR: return "Fatal error";
		default: return "";
		}
	}
}
