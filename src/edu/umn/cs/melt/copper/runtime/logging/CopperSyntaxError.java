package edu.umn.cs.melt.copper.runtime.logging;

import java.util.ArrayList;

import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;

/**
 * A subclass of Copper parser exception containing structured data
 * for a syntax error.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class CopperSyntaxError extends CopperParserException
{
	private static final long serialVersionUID = -7677888467140493812L;
	
	private VirtualLocation virtualLocation;
	private InputPosition realLocation;
	private int parseState;
	private ArrayList<String> expectedTerminalsRealNames;
	private ArrayList<String> expectedTerminalsDisplayNames;
	private ArrayList<String> matchedTerminalsRealNames;
	private ArrayList<String> matchedTerminalsDisplayNames;
	private String locationDescription;
	private String stringDescription;

	public CopperSyntaxError(VirtualLocation virtualLocation,
							 InputPosition realLocation,
							 int parseState,
							 ArrayList<String> expectedTerminalsRealNames,
							 ArrayList<String> expectedTerminalsDisplayNames,
							 ArrayList<String> matchedTerminalsRealNames,
							 ArrayList<String> matchedTerminalsDisplayNames)
	{
		super();
		this.virtualLocation = virtualLocation;
		this.realLocation = realLocation;
		this.parseState = parseState;
		this.expectedTerminalsRealNames = expectedTerminalsRealNames;
		this.expectedTerminalsDisplayNames = expectedTerminalsDisplayNames;
		this.matchedTerminalsRealNames = matchedTerminalsRealNames;
		this.matchedTerminalsDisplayNames = matchedTerminalsDisplayNames;
		this.locationDescription = null;
		this.stringDescription = null;
	}
	
	/**
	 * Gets the name of the file that was being parsed when this syntax error occurred.
	 */
	public String getRealFilename()
	{
		return realLocation.getFileName();
	}
	
	/**
	 * Gets the line number that the parser had reached when this syntax error occurred.
	 */
	public int getRealLine()
	{
		return realLocation.getLine();
	}
	
	/**
	 * Gets the column number that the parser had reached when this syntax error occurred.
	 */
	public int getRealColumn()
	{
		return realLocation.getColumn();
	}
	
	/**
	 * Gets the file position indicator (counting by characters from the beginning of the file)
	 * corresponding to the the point in the file where the syntax error occurred.
	 */
	public long getRealCharIndex()
	{
		return realLocation.getPos();
	}
	
	/**
	 * Gets the "virtual" filename at the point the syntax error occurred.
	 * 
	 * Virtual location data is meant for use in the parser's output, or for display to the user,
	 * rather than internal use by the parser; it may be maintained and altered by semantic actions
	 * in the parser itself.
	 * 
	 * An example is with C source files, where several files are run through the
	 * preprocessor and output as a single file; when parsing this "real" file, the original
	 * filenames and line numbers are reconstructed as "virtual" locations using the comment tags
	 * the preprocessor left behind. 
	 */
	public String getVirtualFileName()
	{
		return virtualLocation.getFileName();
	}
	
	/**
	 * Gets the "virtual" line number at the point the syntax error occurred.
	 * 
	 * Virtual location data is meant for use in the parser's output, or for display to the user,
	 * rather than internal use by the parser; it may be maintained and altered by semantic actions
	 * in the parser itself.
	 * 
	 * An example is with C source files, where several files are run through the
	 * preprocessor and output as a single file; when parsing this "real" file, the original
	 * filenames and line numbers are reconstructed as "virtual" locations using the comment tags
	 * the preprocessor left behind.
	 */
	public int getVirtualLine()
	{
		return virtualLocation.getLine();
	}
	
	/**
	 * Gets the "virtual" column number at the point the syntax error occurred.
	 * 
	 * Virtual location data is meant for use in the parser's output, or for display to the user,
	 * rather than internal use by the parser; it may be maintained and altered by semantic actions
	 * in the parser itself.
	 * 
	 * An example is with C source files, where several files are run through the
	 * preprocessor and output as a single file; when parsing this "real" file, the original
	 * filenames and line numbers are reconstructed as "virtual" locations using the comment tags
	 * the preprocessor left behind. 
	 */
	public int getVirtualColumn()
	{
		return virtualLocation.getColumn(); 
	}
	
	/**
	 * Gets the number of the state the parser was in when the syntax error occurred.
	 */
	public int getParseState()
	{
		return parseState;
	}

	/**
	 * Gets the set of expected terminals in their "real" form --
	 * the actual identifiers used internally by the parser and parser generator.
	 */
	public ArrayList<String> getExpectedTerminalsReal()
	{
		return expectedTerminalsRealNames;
	}
	
	/**
	 * Gets the set of expected terminals in their "display" form --
	 * a form meant for display to users.
	 */
	public ArrayList<String> getExpectedTerminalsDisplay()
	{
		return expectedTerminalsDisplayNames;
	}
	
	/**
	 * Gets the set of terminals that were matched in their "real" form --
	 * the actual identifiers used internally by the parser and parser generator.
	 */
	public ArrayList<String> getMatchedTerminalsReal()
	{
		return matchedTerminalsRealNames;
	}

	/**
	 * Gets the set of terminals that were matched in their "display" form --
	 * a form meant for display to users.
	 */
	public ArrayList<String> getMatchedTerminalsDisplay()
	{
		return matchedTerminalsDisplayNames;
	}

	public String getLocationDescription()
	{
		if(locationDescription == null)
		{
			StringBuffer location = new StringBuffer();
			location.append("line ").append(virtualLocation.getLine()).append(", column ").append(virtualLocation.getColumn());
			if(realLocation.getFileName().length() > 40) location.append("\n         ");
			location.append(" in file ").append(virtualLocation.getFileName());
			location.append("\n         (parser state: ").append(parseState).append("; real character index: ").append(realLocation.getPos()).append(")");
			locationDescription = location.toString();
		}
		return locationDescription;
	}
	
	@Override
	public String getMessage()
	{
		if(stringDescription == null)
		{
			StringBuffer error = new StringBuffer();
			error.append("Error at ");
			error.append(getLocationDescription());
			error.append(":\n  ");
			error.append("Expected a token of one of the following types:\n");
			error.append(PrettyPrinter.iterablePrettyPrint(expectedTerminalsDisplayNames,"   ",PrettyPrinter.getOptimalItemsPerLine(expectedTerminalsDisplayNames,80)));
			error.append("\n   Input currently matches:\n");
			error.append(PrettyPrinter.iterablePrettyPrint(matchedTerminalsDisplayNames,"   ",PrettyPrinter.getOptimalItemsPerLine(expectedTerminalsDisplayNames,80)));
			stringDescription = error.toString();
		}
		return stringDescription;
	}
	
	public String toString()
	{
		return getMessage();
	}
}
