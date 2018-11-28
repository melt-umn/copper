package edu.umn.cs.melt.copper.legacy.compiletime.parsetree.plain;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public class ParseTree
{
	private ParseTreeNode root;

	/**
	 * @return Returns the root.
	 */
	public ParseTreeNode getRoot()
	{
		return root;
	}

	/**
	 * @param root The root to set.
	 */
	public void setRoot(ParseTreeNode root)
	{
		this.root = root;
	}
	
	public ParseTreeProdNode prodNode(Production prod,ParseTreeNode... children)
	{
		return new ParseTreeProdNode(prod,children);
	}

	public ParseTreeTermNode termNode(Terminal token,InputPosition realLocation,VirtualLocation virtualLocation)
	{
		return new ParseTreeTermNode(token,realLocation,virtualLocation);
	}
	
	public ParseTreeTermNode termNode(Terminal token,InputPosition realLocation)
	{
		return new ParseTreeTermNode(token,realLocation);
	}
}
