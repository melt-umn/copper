package edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateConsNode;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateNodeVisitor;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;

public class LHSAssigner implements IntermediateNodeVisitor< Pair<InputPosition,Object>,Pair<InputPosition,Object>,RuntimeException >
{
	public LHSAssigner() {}
	
	public Pair<InputPosition,Object> visitIntermediateSymbolNode(IntermediateSymbolNode node,
			Pair<InputPosition,Object> inheritance)
	{
		if(node.sort == IntermediateSymbolSort.PRODUCTION)
		{
			node.attributes.put("LHS",inheritance);
		}
		return inheritance;
	}

	public Pair<InputPosition,Object> visitIntermediateConsNode(IntermediateConsNode node,
			Pair<InputPosition,Object> inheritance)
	{
		node.car.acceptVisitor(this,inheritance);
		node.cdr.acceptVisitor(this,inheritance);
		return inheritance;
	}

}
