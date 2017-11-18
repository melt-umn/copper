package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar;

import java.util.ArrayList;
import java.util.Hashtable;

class ProductionData
{
	private Symbol name;
	private NonTerminal leftSide;
	private ArrayList<GrammarSymbol> rightSide;
	private int precedenceIndex;
	private Terminal operator;
	
	public ProductionData(Symbol name,Terminal operator,NonTerminal leftSide,ArrayList<GrammarSymbol> rightSide)
	{
		this.name = name;
		this.leftSide = leftSide;
		this.rightSide = rightSide;
		precedenceIndex = FringeSymbols.PRECEDENCE_NONE;
		if(operator == null)
		{
			int i = rightSide.size() - 1;
			for(i = rightSide.size() - 1;i >= 0;i--)
			{
				if(rightSide.get(i) instanceof Terminal)
				{
					precedenceIndex = i;
					break;
				}
			}
		}
		this.operator = operator;
	}
	
	public int length() { return rightSide.size(); }
	
	public Symbol getName() { return name; }
	
	public NonTerminal getLeft() { return leftSide; }
	
	public Iterable<GrammarSymbol> getRight() { return rightSide; }
	
	public Terminal getPrecedenceSymbol()
	{
		if(precedenceIndex == FringeSymbols.PRECEDENCE_NONE)
		{
			if(operator == null) return FringeSymbols.EMPTY;
			else return operator;
		}
		else return (Terminal) rightSide.get(precedenceIndex);
	}
	
	public GrammarSymbol getSymbol(int index) { return rightSide.get(index); }
	
	public boolean equals(Object rhs)
	{
		if(rhs instanceof ProductionData)
		{
			return leftSide.equals(((ProductionData) rhs).leftSide) &&
					rightSide.equals(((ProductionData) rhs).rightSide);
		}
		else return false;
	}
	
	public String toString()
	{
		String rv = leftSide + " ::=";
		for(GrammarSymbol gs : rightSide)
		{
			rv += " " + gs;
		}
		return rv;
	}
	
	public int hashCode()
	{
		String rv = leftSide.toString();
		for(GrammarSymbol gs : rightSide)
		{
			rv += " " + gs;
		}
		return rv.hashCode();
	}
	
}

/**
 * A class to store productions, replacing them with integers for more efficient storage.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class Production
{
	private int tag;
	
    private static int nextTag = 0;
    private static ArrayList<ProductionData> data = new ArrayList<ProductionData>(64);
    private static Hashtable<ProductionData,Integer> tags = new Hashtable<ProductionData,Integer>();
    
    /* (non-Javadoc)
     * Creates a new instance of Production.
     * @param name The name of the new production.
     * @param operator The terminal-operator of the new production.
     * @param leftSide The left hand side of the new production.
     * @param rightSide The right hand side of the new production.
     * @return The new production.
     */
    public static Production production(Symbol name,Terminal operator,NonTerminal leftSide,ArrayList<GrammarSymbol> rightSide)
    {
    	ArrayList<GrammarSymbol> rightSideCopy = new ArrayList<GrammarSymbol>(rightSide);
    	return new Production(name,operator,leftSide,rightSideCopy);
    }
    
    public static Production production(Symbol name,NonTerminal leftSide,GrammarSymbol... rightSide)
    {
    	return production(name,null,leftSide,rightSide);
    }
    
    /* (non-Javadoc)
     * Creates a new instance of Production.
     * @param name The name of the new production.
     * @param operator The terminal-operator of the new production.
     * @param leftSide The left hand side of the new production.
     * @param rightSide The right hand side of the new production.
     * @return The new production.
     */
    public static Production production(Symbol name,Terminal operator,NonTerminal leftSide,GrammarSymbol... rightSide)
    {
    	ArrayList<GrammarSymbol> rightSideCopy = new ArrayList<GrammarSymbol>();
    	for(GrammarSymbol gs : rightSide) rightSideCopy.add(gs);
    	return new Production(name,operator,leftSide,rightSideCopy);
    }
    
    private Production(Symbol name,Terminal operator,NonTerminal leftSide,ArrayList<GrammarSymbol> rightSide)
    {
    	ProductionData newData = new ProductionData(name,operator,leftSide,rightSide);
    	if(tags.containsKey(newData)) tag = tags.get(newData);
    	else
    	{
    		tag = nextTag++;
    		data.add(newData); // kept in tandem with tag...
    		tags.put(newData,tag);
    	}
    }
    
    /* (non-Javadoc)
     * Returns the length of a production (number of symbols on the right hand side).
     * @return The length of this production.
     */
    public int length() { return data.get(tag).length(); }
    
    /* (non-Javadoc)
     * Returns the symbolic name of this production.
     * @return The symbolic name of this production.
     */
    public Symbol getName() { return data.get(tag).getName(); }
    
    /* (non-Javadoc)
     * Returns the left hand side of a production.
     * @return The left hand side of this production.
     */
    public NonTerminal getLeft() { return data.get(tag).getLeft(); }
    
    /* (non-Javadoc)
     * Returns the right hand side of a production.
     * @return An iterable collection of the symbols on the right hand side of a production.
     */
    public Iterable<GrammarSymbol> getRight() { return data.get(tag).getRight(); }
    
    /* (non-Javadoc)
     * Returns the terminal representing a production's operator precedence.
     * @return The terminal, or <CODE>FringeSymbols.EMPTY</CODE> if there is none such.
     */
    public Terminal getPrecedenceSymbol() { return data.get(tag).getPrecedenceSymbol(); }
    
    /* (non-Javadoc)
     * Gets a particular symbol on the right hand side of a production.
     * @param index The index of the symbol, the first right hand side symbol being index 0.
     * @return The symbol at <CODE>index</CODE>.
     */
    public GrammarSymbol getSymbol(int index) { return data.get(tag).getSymbol(index); }
    
    public boolean equals(Object rhs)
    {
    	if(rhs instanceof Production) return (tag == ((Production) rhs).tag);
    	else return false;
    }
    
    public String toString() { return data.get(tag).toString(); }
    
    public int hashCode() { return tag; }
}
