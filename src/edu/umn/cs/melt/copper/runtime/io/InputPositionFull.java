package edu.umn.cs.melt.copper.runtime.io;

import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * Holds a position in an input stream (offset from beginning).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
class InputPositionFull extends InputPosition
{
	private String fileName;
	private int line;
    private long pos;
    private int posSinceNewline;

    protected InputPositionFull()
    {
    	fileName = "";
    	pos = 0;
    	posSinceNewline = 0;
    	line = 1;
    }
    /**
     * Creates an instance of InputPosition representing an
     * advance of several characters.
     * @param ref The reference position.
     * @param offset The offset in characters from <CODE>ref</CODE>.
     * @param nlCount The number of newline characters between <CODE>ref</CODE> and the current position. 
     */
    protected InputPositionFull(InputPosition ref,int offset,Pair<Integer,Integer> nlCount)
    {
    	fileName = ref.getFileName();
    	pos = ref.getPos() + offset;
    	line = ref.getLine() + nlCount.first();
    	if(nlCount.second() == -1) posSinceNewline = ref.getColumn() + offset;
    	else posSinceNewline = nlCount.second();
    	// DEBUG-X-BEGIN
    	//System.err.println("Passing lexeme of length " + offset + "; transitioning from " + ref + " to " + this);
    	// DEBUG-X-END
    }
    
    /**
     * Creates an instance of InputPosition with a new filename.
     * @param ref The reference position.
     * @param fileName The new filename.
     */
    protected InputPositionFull(InputPosition ref,String fileName)
    {
    	this(ref);
    	this.fileName = fileName;
    }
    
    /**
     * Creates a copy of an InputPosition instance.
     * @param p The instance to copy.
     */
    protected InputPositionFull(InputPosition p)
    {
    	fileName = p.getFileName();
    	pos = p.getPos();
    	line = p.getLine();
    	posSinceNewline = p.getColumn();
    }

	/** 
	 * Tests whether this position is before another.
	 * @param rhs The position against which to test.
	 * @return <CODE>true</CODE> iff <CODE>this</CODE> fall before <CODE>rhs</CODE>.
	 */
    @Override
	public boolean isBefore(InputPosition rhs)
    {
    	return compareTo(rhs) < 0;
    }
    
	/** 
	 * Tests whether this position is after another.
	 * @param rhs The position against which to test.
	 * @return <CODE>true</CODE> iff <CODE>this</CODE> fall after <CODE>rhs</CODE>.
	 */
    @Override
	public boolean isAfter(InputPosition rhs)
    {
    	return compareTo(rhs) > 0;
    }

    /**
     * Calculates the offset from one position to another. 
     * @param ref The reference position.
     * @return The offset of this position from <CODE>ref</CODE>.
     */
    @Override
	public int diff(InputPosition ref)
    {
    	return (int)(pos - ref.getPos()); // % (((long) Integer.MAX_VALUE) + 1)
    }
    
    @Override
	public int compareTo(Location ref)
    {
    	int discriminant = (int)(pos - ref.getPos());
    	return Integer.signum(discriminant);
    }

    /* (non-Javadoc)
	 * @see edu.umn.cs.melt.copper.runtime.io.InputPosition#equals(java.lang.Object)
	 */
    @Override
	public boolean equals(Object rhs)
    {
    	if(rhs == null) return false;
    	if(rhs instanceof InputPosition) return (compareTo((InputPosition) rhs) == 0);
    	else return false;
    }
    
    /* (non-Javadoc)
	 * @see edu.umn.cs.melt.copper.runtime.io.InputPosition#getFileName()
	 */
    @Override
	public String getFileName()
    {
    	return fileName;
    }
    
    /* (non-Javadoc)
	 * @see edu.umn.cs.melt.copper.runtime.io.InputPosition#getLine()
	 */
    @Override
	public int getLine()
    {
    	return line;
    }
    
    /* (non-Javadoc)
	 * @see edu.umn.cs.melt.copper.runtime.io.InputPosition#getColumn()
	 */
    @Override
	public int getColumn()
    {
    	return posSinceNewline;
    }
    
    /* (non-Javadoc)
	 * @see edu.umn.cs.melt.copper.runtime.io.InputPosition#getPos()
	 */
    @Override
	public long getPos()
    {
    	return pos;
    }

    public String toString()
    {
    	return getFileName() + ":" + getLine() + "." + getColumn();
    	//return "file '" + getFileName() + "', line " + getLine() + ", column " + getColumn() + " (character " + String.valueOf(pos) + ")";
    	//return "character " + String.valueOf(pos) + " (line " + getLine() + ", column " + getColumn() + ")";
    }
    
    /* (non-Javadoc)
	 * @see edu.umn.cs.melt.copper.runtime.io.InputPosition#hashCode()
	 */
    @Override
	public int hashCode() { return (int)(pos % (((long) Integer.MAX_VALUE) + 1)); }
}
