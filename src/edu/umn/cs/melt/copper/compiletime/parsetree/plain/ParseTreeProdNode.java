package edu.umn.cs.melt.copper.compiletime.parsetree.plain;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public class ParseTreeProdNode extends ParseTreeNode
{
	private Production prod;
	private ParseTreeNode[] children;
	private InputPosition realLocation;
	private String virtualLocation;

	protected ParseTreeProdNode(Production prod,ParseTreeNode... children)
	{
		this.prod = prod;
		this.children = children;
		this.realLocation = calculatePosition();
		this.virtualLocation = (realLocation == null) ? null : realLocation.toString();
		this.hashcode = prod.hashCode() + ((realLocation == null) ? 0 : realLocation.hashCode());
	}
	
	private InputPosition calculatePosition()
	{
		for(ParseTreeNode child : children)
		{
			if(child.getPosition() != null)
			{
				return child.getPosition();
			}
		}
		return null;
	}

	public GrammarSymbol getHeadSymbol()
	{
		return prod.getLeft();
	}
	
	public InputPosition getPosition()
	{
		return realLocation;
	}
	
	public ParseTreeNode[] getChildren()
	{
		return children;
	}

	public Production getProd()
	{
		return prod;
	}

	public String getVirtualLocation()
	{
		return virtualLocation;
	}

	public void setChildren(ParseTreeNode... children)
	{
		this.children = children;
	}

	public void setProd(Production prod)
	{
		this.prod = prod;
	}

	public void setPosition(InputPosition realLocation)
	{
		this.realLocation = realLocation;
	}

	public void setVirtualLocation(String virtualLocation)
	{
		this.virtualLocation = virtualLocation;
	}

	public <SYNTYPE, INHTYPE, E extends Exception> SYNTYPE acceptVisitor(
			ParseTreeVisitor<SYNTYPE, INHTYPE, E> visitor, INHTYPE inheritance)
			throws E
	{
		return visitor.visitProdNode(this,inheritance);
	}

	public boolean equals(Object rhs)
	{
		if(rhs != null &&
		   rhs instanceof ParseTreeProdNode)
		{
			ParseTreeProdNode rhsNode = (ParseTreeProdNode) rhs;
			if(!hashEquals(rhsNode)) return false;
			else return (prod.equals(rhsNode.prod) && realLocation.equals(rhsNode.realLocation));
		}
		else return false;
	}
}
