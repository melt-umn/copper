package edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate;

public abstract class IntermediateNode
{
    public abstract <SYNTYPE,INHTYPE,E extends Exception> SYNTYPE acceptVisitor(IntermediateNodeVisitor<SYNTYPE,INHTYPE,E> visitor,INHTYPE inheritance) throws E;
}
