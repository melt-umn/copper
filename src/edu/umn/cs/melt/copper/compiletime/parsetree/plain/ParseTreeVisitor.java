package edu.umn.cs.melt.copper.compiletime.parsetree.plain;

public interface ParseTreeVisitor<SYNTYPE, INHTYPE, E extends Exception>
{
	public SYNTYPE visitTermNode(ParseTreeTermNode node,INHTYPE inheritance) throws E;
	public SYNTYPE visitProdNode(ParseTreeProdNode node,INHTYPE inheritance) throws E;
}
