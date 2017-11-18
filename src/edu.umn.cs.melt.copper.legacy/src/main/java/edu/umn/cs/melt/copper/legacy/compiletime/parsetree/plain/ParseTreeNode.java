package edu.umn.cs.melt.copper.legacy.compiletime.parsetree.plain;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public abstract class ParseTreeNode
{
    protected int hashcode;

    public final boolean hashEquals(ParseTreeNode rhsNode)
    {
    	return (hashcode == rhsNode.hashcode);
    }
    public final int hashCode() { return hashcode; }

    public abstract GrammarSymbol getHeadSymbol();
    
    public abstract InputPosition getPosition();

	/**
	 * Admits a visitor class to this parse tree node.
	 * @param visitor The visitor upon which to call the method.
	 * @param inheritance What is passed down the tree within the visitor.
	 * @return What is returned by the visitor.
	 * @throws E What is thrown by the visitor.
	 */
    public abstract <SYNTYPE,INHTYPE,E extends Exception> SYNTYPE acceptVisitor(ParseTreeVisitor<SYNTYPE,INHTYPE,E> visitor,INHTYPE inheritance) throws E;
}
