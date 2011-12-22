package edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate;

import java.util.LinkedList;

public class IntermediateConsNode extends IntermediateNode
{
	public IntermediateNode car,cdr;
	
	public IntermediateConsNode(IntermediateNode car, IntermediateNode cdr)
	{
		this.car = car;
		this.cdr = cdr;
	}

	public <SYNTYPE, INHTYPE, E extends Exception> SYNTYPE acceptVisitor(
			IntermediateNodeVisitor<SYNTYPE, INHTYPE, E> visitor,
			INHTYPE inheritance) throws E
	{
		return visitor.visitIntermediateConsNode(this,inheritance);		
	}

	public boolean equals(Object rhs)
	{
		return (this == rhs);
	}
	
	public static IntermediateNode cons(Object... constituents)
	{
		LinkedList<IntermediateNode> newConstituents = new LinkedList<IntermediateNode>();
		for(Object constituent : constituents)
		{
			if(constituent != null && constituent instanceof IntermediateNode)
			{
				newConstituents.addFirst((IntermediateNode) constituent);
			}
		}
		if(newConstituents.isEmpty()) return null;
		else if(newConstituents.size() == 1) return newConstituents.getFirst();
		IntermediateNode first = newConstituents.poll();
		IntermediateNode second = newConstituents.poll();
		IntermediateConsNode rv = new IntermediateConsNode(second,first);
		while(!newConstituents.isEmpty())
		{
			rv = new IntermediateConsNode(newConstituents.poll(),rv);
		}
		return rv;
	}
}
