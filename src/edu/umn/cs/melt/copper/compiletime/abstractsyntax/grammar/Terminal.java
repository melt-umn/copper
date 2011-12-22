package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar;


/* (non-Javadoc)
 * A terminal token. May be used to hold a terminal symbol
 * alone (symbol), or a terminal symbol with a matching lexeme (token).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class Terminal extends GrammarSymbol
{
    /* (non-Javadoc) This token's lexeme. */    
    private String lexeme;

    /* (non-Javadoc) Creates a new instance of Terminal for use as a symbol.
     * @param nid The string representation of the terminal symbol.
     */    
    public Terminal(String nid)
    {
		id = Symbol.symbol(nid);
		lexeme = null;
    }
    /* (non-Javadoc) Creates a new instance of Terminal for use as a symbol.
     * @param nid The terminal symbol.
     */    
    public Terminal(Symbol nid)
    {
		id = nid;
		lexeme = null;
    }
    /* (non-Javadoc) Creates a new use of Terminal for use as a token.
     * @param nid The string representation of the terminal symbol.
     * @param nlexeme The new token's lexeme.
     */    
    public Terminal(String nid,String nlexeme)
    {
		id = Symbol.symbol(nid);
		lexeme = nlexeme;
    }
    /* (non-Javadoc) Creates a new use of Terminal for use as a token.
     * @param nid The terminal symbol.
     * @param nlexeme The new token's lexeme.
     */    
    public Terminal(Symbol nid,String nlexeme)
    {
		id = nid;
		lexeme = nlexeme;
    }
    
    /* (non-Javadoc)
     * Determines whether or not this Terminal carries a lexeme.
     * @return <CODE>true</CODE> iff the Terminal carries no lexeme.
     */
    public boolean isBareSym()
    {
    	return (lexeme == null);
    }

    /* (non-Javadoc)
     * Returns a symbol instance of this Terminal
     * (one for which <CODE>isBareSym() == true</CODE>).
     * @return The symbol instance of this Terminal.
     */
    public Terminal bareSym()
    {
    	return new Terminal(id);
    }
    
	/* (non-Javadoc)
	 * Returns an instance of this Terminal with a new lexeme. 
	 * @param newLexeme The new lexeme.
	 * @return An instance of this Terminal with the new lexeme.
	 */
    public Terminal newLexeme(String newLexeme)
    {
    	return new Terminal(id,newLexeme);
    }

    /* (non-Javadoc)
     * Gets this Terminal's lexeme.
     * @return The Terminal's lexeme, or <CODE>null</CODE> if it is a symbol.
     */
    public String getLexeme()
    {
    	return lexeme;
    }
    
    public boolean equals(Object rhs)
    {
		if(rhs instanceof Terminal)
		{
		    boolean rv = id.equals(((Terminal) rhs).id);
		    if(lexeme != null && ((Terminal) rhs).lexeme != null) rv = rv && lexeme.equals(((Terminal) rhs).lexeme);
		    return rv;
		}
		else return false;
    }

    public String toString()
    {
		String rv = id.toString();
		if(lexeme != null) rv += ":\"" + lexeme + "\"";
		return rv;
    }

    public int hashCode() { return id.hashCode(); }
}
