package edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate;

import java.util.Hashtable;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarName;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public class IntermediateSymbolNode extends IntermediateNode
{
	public IntermediateSymbolSort sort;
	public Symbol name;
	public GrammarName owner;
	public Hashtable< String,Pair<InputPosition,Object> > attributes;
	
	public IntermediateSymbolNode(IntermediateSymbolSort sort,Symbol name,Pair< String,Pair<InputPosition,Object> >... attributes)
	{
		this.sort = sort;
		this.name = name;
		owner = null;
		this.attributes = new Hashtable< String,Pair<InputPosition,Object> >();
		for(Pair<String,Pair<InputPosition,Object> > attribute : attributes)
		{
			if(attribute == null) continue;
			this.attributes.put(attribute.first(),attribute.second());
		}
	}
	
	public <SYNTYPE, INHTYPE, E extends Exception> SYNTYPE acceptVisitor(
			IntermediateNodeVisitor<SYNTYPE, INHTYPE, E> visitor, INHTYPE inheritance)
			throws E
	{
		return visitor.visitIntermediateSymbolNode(this,inheritance);
	}
	
	public int hashCode() { return name.hashCode(); }
	
	public boolean equals(Object rhs)
	{
		if(rhs instanceof IntermediateSymbolNode)
		{
			return (name.equals(((IntermediateSymbolNode) rhs).name) &&
					sort.equals(((IntermediateSymbolNode) rhs).sort) &&
					attributes.equals(((IntermediateSymbolNode) rhs).attributes));
		}
		else return false;
	}
}
