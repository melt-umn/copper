package edu.umn.cs.melt.copper.runtime.auxiliary.internal;

import java.util.regex.Pattern;

/**
 * Contains methods to format strings for appearance in Java source files,
 * and for the semantic analysis of concrete syntax trees for regexes.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class QuotedStringFormatter
{
	private static Pattern javaWhitespace = Pattern.compile("[ \t\n]*");
	
	public static String formatOutputLexeme(String lexeme)
	{
		String rv = lexeme;
		rv = rv.replaceAll("\\\\","\\\\\\\\");
		rv = rv.replaceAll("\\n","\\\\\\\\n");
		rv = rv.replaceAll("\\r","\\\\\\\\r");
		rv = rv.replaceAll("\\\"","\\\\\\\"");
		return rv;		
	}
	
	/**
	 * Quotes backslashes, tabs, newlines, quotes and spaces in a string.
	 * @param q The string to format.
	 * @return The formatted string.
	 */
	public static String formatQuotedString(String q)
	{
		String rv = q;
		rv = rv.replaceAll("\\\\","\\\\\\\\");
		rv = rv.replaceAll("\\t","\\\\\\\\t");
		rv = rv.replaceAll("\\n","\\\\\\\\n");
		rv = rv.replaceAll("\\r","\\\\\\\\r");
		rv = rv.replaceAll("\\\"","\\\\\\\"");
		rv = rv.replaceAll("   "," \\\\\\\\s ");
		return rv;
	}
	
	/**
	 * Gets the character represented by a quote.
	 * @param quoted The quoted representation.
	 * @return The character quoted, or <CODE>QScannerBuffer.EOFIndicator</CODE> if <CODE>quoted</CODE> be not a quoted character.
	 */
	public static char getRepresentedCharacter(String quoted)
	{
		if(quoted.equals("\\n")) return '\n';
		else if(quoted.equals("\\r")) return '\r';
		else if(quoted.equals("\\t")) return '\t';
		else if(quoted.equals("\\s")) return ' ';
		else /*if(quoted.equals("\\+") ||
				quoted.equals("\\*") ||
				quoted.equals("\\?") ||
				quoted.equals("\\|") ||
				quoted.equals("\\-") ||
				quoted.equals("\\^") ||
				quoted.equals("\\[") ||
				quoted.equals("\\]") ||
				quoted.equals("\\(") ||
				quoted.equals("\\)") ||
				quoted.equals("\\.") ||
				quoted.equals("\\:") ||
				quoted.equals("\\\"") ||
				quoted.equals("\\\\"))*/ return quoted.charAt(1);
	}
	
	/**
	 * Quotes backslash, tab, newline, and quote characters in the manner of a filter.
	 * @param q The character to format.
	 * @return The escaped version of <CODE>q</CODE> if it be a backslash, tab, newline or quote; the single character <CODE>q</CODE> otherwise.
	 */
	public static String quoteChar(char q)
	{
		switch(q)
		{
		case '\r':
			return "\\r";
		case '\n':
			return "\\n";
		case '\t':
			return "\\t";
		case '\'':
		case '\"':
		case '\\':
			return "\\" + q;
		default:
			return String.valueOf(q);		
		}
	}
	
	public static boolean isJavaWhitespace(String str)
	{
		return javaWhitespace.matcher(str).matches();
	}
}
