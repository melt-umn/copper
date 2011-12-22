package edu.umn.cs.melt.copper.runtime.engines.semantics;

import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.io.Location;


/**
 * Holds a "virtual" location -- a location that may not represent
 * a "real" location in an input file (e.g., a location specified in
 * C preprocessor tags).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class VirtualLocation implements Location
{
	private boolean isModified;
	private String hash;
	
	/** The character width of a tab in this context; defaults to 8. */
	protected int tabStop;
	/** The line number pertaining to the location. */
	protected int line;
	/** The column number pertaining to the location. */
	protected int column;
	/** The filename pertaining to the location. */
	protected String fileName;
	
	/**
	 * Creates a new virtual location.
	 * @param fileName The filename pertaining to the location.
	 * @param line The line number pertaining to the location.
	 * @param column The column number pertaining to the location.
	 */
	public VirtualLocation(String fileName,int line,int column)
	{
		isModified = false;
		tabStop = 8;
		this.fileName = fileName;
		this.line = line;
		this.column = column;
		generateHash();
	}
	
	public VirtualLocation(VirtualLocation original)
	{
		isModified = false;
		tabStop = original.tabStop;
		line = original.line;
		column = original.column;
		fileName = original.fileName;
		generateHash();
	}
	
	/**
	 * @see VirtualLocation#column
	 */
	public int getColumn()
	{
		return column;
	}

	/**
	 * @see VirtualLocation#column
	 */
	public void setColumn(int column)
	{
		if(this.column != column) isModified = true;
		this.column = column;
	}

	/**
	 * @see VirtualLocation#fileName
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * @see VirtualLocation#fileName
	 */
	public void setFileName(String fileName)
	{
		if(!this.fileName.equals(fileName)) isModified = true;
		this.fileName = fileName;
	}

	/**
	 * @see VirtualLocation#line
	 */
	public int getLine()
	{
		return line;
	}

	/**
	 * @see VirtualLocation#line
	 */
	public void setLine(int line)
	{
		if(this.line != line) isModified = true;
		this.line = line;
	}
	
	public long getPos()
	{
		// Virtual locations have no "position."
		return -1;
	}

	/**
	 * @see VirtualLocation#tabStop
	 */
	public int getTabStop()
	{
		return tabStop;
	}

	/**
	 * @see VirtualLocation#tabStop
	 */
	public void setTabStop(int tabStop)
	{
		this.tabStop = tabStop;
	}

	/**
	 * Generates and returns a string representation of the location.
	 * @return The string representation of the location, in the format <code>[filename]:[line].[column]</code>.
	 */
	public String getHash()
	{
		if(isModified) generateHash();
		return hash;
	}

	/**
	 * Determines whether this virtual location has been modified
	 * since its construction or its last "default update."
	 * @see VirtualLocation#defaultUpdate(String)
	 */
	public boolean isModified()
	{
		return isModified;
	}
	
	private void defaultUpdate(String lexeme,boolean isAutomatic)
	{
		// DEBUG-X-BEGIN
		//System.err.println("Default-updating on " + fileName + ":" + line + "." + column + ", formerly " + hash + ", lexeme \"" + lexeme + "\": is " + (isModified ? "" : "NOT ") + "modified");
		// DEBUG-X-END
		if(!isAutomatic) isModified = true;
		else if(isModified)
		{
			isModified = false;
			return;
		}
		Pair<Integer,Integer> nlCount = countNewlines(column,lexeme);
    	line += nlCount.first();
    	if(nlCount.second() == -1) column += lexeme.length();
    	else column = nlCount.second();
    	generateHash();
    	// DEBUG-X-BEGIN
    	//System.err.println("Modification occurred");
    	// DEBUG-X-END		
	}
	
	/**
	 * This function is meant to be called automatically from
	 * the parser.
	 */
	public void defaultUpdateAutomatic(String lexeme)
	{
		defaultUpdate(lexeme,true);
	}

	/**
	 * Performs a "default update" -- i.e., moves this virtual location
	 * past a given lexeme. For example, a virtual location starting at
	 * line 4, column 5, called on the lexeme "abc\nde", will be reset to
	 * line 5, column 2.
	 * @param lexeme The lexeme to move past.
	 */
	public void defaultUpdate(String lexeme)
	{
		defaultUpdate(lexeme,false);
	}

	private void generateHash()
	{
		hash = fileName + ":" + line + "." + column;
	}
	
	public String toString()
	{
		return getHash();
	}
	
	@Override
	public int compareTo(Location l)
	{
		int fileNameCompare = fileName.compareTo(l.getFileName());
		if(fileNameCompare != 0) return fileNameCompare;
		int lineCompare = Integer.signum(line - l.getLine());
		if(lineCompare != 0) return lineCompare;
		return Integer.signum(column - l.getColumn());
	}
	
	/**
	 * Counts the number of newlines in a given lexeme, and the column number at the end of the lexeme.
	 * @param column The column number of the location at the beginning of the lexeme.
	 * @param lexeme The lexeme.
	 * @return A pair of integers, of which the first is the count of newlines in the
	 * lexeme and the second is the column number at the end of the lexeme. 
	 */
	public Pair<Integer,Integer> countNewlines(int column,String lexeme)
	{
    	if(lexeme.equals("")) return Pair.cons(0,-1);
		int startIndex = -1,nlCount = -1;
		while(startIndex != 0)
		{
			nlCount++;
			startIndex = lexeme.indexOf('\n',startIndex) + 1;
		}
		int lastStartIndex = lexeme.lastIndexOf('\n') + 1;
		int columnCount = lastStartIndex == 0 ? column : 0;
		for(startIndex = lexeme.indexOf('\t',lastStartIndex);startIndex != -1;startIndex = lexeme.indexOf('\t',lastStartIndex))
		{
			columnCount += startIndex - lastStartIndex;
			columnCount += tabStop - (columnCount % tabStop);
			lastStartIndex = startIndex + 1;
		}
		columnCount += lexeme.length() - lastStartIndex;
		
		//int pastNewline = ((lexeme.lastIndexOf('\n') == -1) ? column + lexeme.length() : (lexeme.length() - (lexeme.lastIndexOf('\n') + 1)));
		// DEBUG-X-BEGIN
		//System.err.println("Lexeme '" + lexeme + "' --- Newline count: " + nlCount + "; last newline: " + pastNewline);
		// DEBUG-X-END
		return Pair.cons(nlCount,columnCount);
	}
}
