package edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate;

public interface IntermediateNodeVisitor<SYNTYPE,INHTYPE,E extends Exception>
{
	public SYNTYPE visitIntermediateSymbolNode(IntermediateSymbolNode node,INHTYPE inheritance) throws E;
	public SYNTYPE visitIntermediateConsNode(IntermediateConsNode node,INHTYPE inheritance) throws E;
}
