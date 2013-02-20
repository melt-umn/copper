package edu.umn.cs.melt.copper.runtime.io;

import org.xml.sax.Locator;

import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * Represents the position of a parser in its input.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public abstract class InputPosition implements Location
{
	/** 
	 * Tests whether this position is before another.
	 * @param rhs The position against which to test.
	 * @return <CODE>true</CODE> iff <CODE>this</CODE> falls before <CODE>rhs</CODE>.
	 */
	public abstract boolean isBefore(InputPosition rhs);

	/** 
	 * Tests whether this position is after another.
	 * @param rhs The position against which to test.
	 * @return <CODE>true</CODE> iff <CODE>this</CODE> falls after <CODE>rhs</CODE>.
	 */
	public abstract boolean isAfter(InputPosition rhs);

	/**
	 * Calculates the offset from one position to another. 
	 * @param ref The reference position.
	 * @return The offset of this position from <CODE>ref</CODE>.
	 */
	public abstract int diff(InputPosition ref);

	public abstract int compareTo(Location ref);

	public abstract boolean equals(Object rhs);

	public abstract String getFileName();

	public abstract int getLine();

	public abstract int getColumn();

	public abstract long getPos();
	
	public abstract int hashCode();

    /**
     * Returns a reference position for the "initial" point in a stream.
     * @return The reference position.
     */
    public static InputPosition initialPos()
    {
    	return new InputPositionFull();
    }
    
    /**
     * Returns a reference position for the "initial" point in a file stream.
     * @param fileName The name of the file.
     * @return The reference position.
     */
    public static InputPosition initialPos(String fileName)
    {
    	return new InputPositionFull(initialPos(),fileName);
    }
    
    /**
     * Builds a position based on information from a {@link org.xml.sax.Locator} object.
     * @param pos An input position at the beginning of the file being parsed.
     * @param locator A locator passed from the SAX parser to indicate where the parser is located.
     * @return An InputPosition object set to the location indicated by the Locator.
     */
    public static InputPosition fromSAXLocator(InputPosition pos,Locator locator)
    {
    	return new InputPositionFull(pos,0,Pair.cons(locator.getLineNumber() - pos.getLine(),locator.getColumnNumber()));
    }
    
    public static InputPosition copy(InputPosition i)
    {
    	return i;//new InputPositionFull(i);
    }

    /**
     * Returns a position one character past the given.
     * @param curPos A reference position.
     * @return A position one character past <CODE>curPos</CODE>.
     */
    public static InputPosition advance(InputPosition curPos,char pastChar)
    {
    	InputPosition rv;
    	if(pastChar == '\n')
    	{
    		rv = new InputPositionFull(curPos,1,Pair.cons(1,0));
    	}
    	else
    	{
    		rv = new InputPositionFull(curPos,1,Pair.cons(0,-1));
    	}
    	// DEBUG-X-BEGIN
    	//System.err.println("Passing character '" + pastChar + "'; transitioning from " + curPos + " to " + rv);
    	// DEBUG-X-END
    	return rv;
    }
    
    public static InputPosition advance(InputPosition curPos,int offset,String stringToPass)
    {
    	return new InputPositionFull(curPos,offset,countNewlines(stringToPass));
    }

    
    public static Pair<Integer,Integer> countNewlines(String lexeme)
    {
    	if(lexeme.equals("")) return Pair.cons(0,-1);
		int startIndex = -1,nlCount = -1;
		while(startIndex != 0)
		{
			nlCount++;
			startIndex = lexeme.indexOf('\n',startIndex) + 1;
		}
		int pastNewline = ((lexeme.lastIndexOf('\n') == -1) ? -1 : (lexeme.length() - (lexeme.lastIndexOf('\n') + 1)));
		// DEBUG-X-BEGIN
		//System.err.println("Lexeme '" + lexeme + "' --- Newline count: " + nlCount + "; last newline: " + pastNewline);
		// DEBUG-X-END
		return Pair.cons(nlCount,pastNewline);
    }
}