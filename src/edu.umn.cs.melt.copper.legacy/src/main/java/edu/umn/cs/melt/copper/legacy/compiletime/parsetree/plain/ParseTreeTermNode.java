package edu.umn.cs.melt.copper.legacy.compiletime.parsetree.plain;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public class ParseTreeTermNode extends ParseTreeNode
{
	private InputPosition realLocation;
	private VirtualLocation virtualLocation;
	private Terminal token;

	protected ParseTreeTermNode(Terminal token, InputPosition realLocation, VirtualLocation virtualLocation)
	{
		this.realLocation = realLocation;
		this.token = token;
		this.virtualLocation = virtualLocation;
		this.hashcode = token.hashCode() + realLocation.hashCode();
	}
	
	protected ParseTreeTermNode(Terminal token,InputPosition realLocation)
	{
		this(token,realLocation,new VirtualLocation(realLocation.getFileName(),realLocation.getLine(),realLocation.getColumn()));
	}

	public GrammarSymbol getHeadSymbol()
	{
		return token;
	}

	public InputPosition getPosition()
	{
		return realLocation;
	}
	
	public Terminal getToken()
	{
		return token;
	}

	public VirtualLocation getVirtualLocation()
	{
		return virtualLocation;
	}

	public void setPosition(InputPosition realLocation)
	{
		this.realLocation = realLocation;
	}

	public void setToken(Terminal token)
	{
		this.token = token;
	}

	public void setVirtualLocation(VirtualLocation virtualLocation)
	{
		this.virtualLocation = virtualLocation;
	}

	public <SYNTYPE, INHTYPE, E extends Exception> SYNTYPE acceptVisitor(
			ParseTreeVisitor<SYNTYPE, INHTYPE, E> visitor, INHTYPE inheritance)
			throws E
	{
		return visitor.visitTermNode(this,inheritance);
	}
	
	public boolean equals(Object rhs)
	{
		if(rhs != null &&
		   rhs instanceof ParseTreeTermNode)
		{
			ParseTreeTermNode rhsNode = (ParseTreeTermNode) rhs;
			if(!hashEquals(rhsNode)) return false;
			else return (token.equals(rhsNode.token) && realLocation.equals(rhsNode.realLocation));
		}
		else return false;
	}

}
