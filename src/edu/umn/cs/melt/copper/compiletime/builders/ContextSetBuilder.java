package edu.umn.cs.melt.copper.compiletime.builders;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ContextSets;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;

/**
 * A method for computing the "context sets," {@code first}, {@code follow}, and {@code nullable},
 * using the procedure specified in Algorithm 3.13 of Appel's "Modern Compiler Implementation
 * in Java," 2nd ed.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 * @see ContextSets
 */
public class ContextSetBuilder
{
	public static ContextSets build(ParserSpec spec)
	{
		ContextSets c = new ContextSets(Math.max(spec.terminals.length(),spec.nonterminals.length()));
		
		// When this is run for the first time, first and follow will be empty
		// and nullable will contain "false" for all symbols.
		// For each terminal symbol z, first(z) := {z}
		// EOF is the first terminal; ^ is the first nonterminal; ^ ::= S $ is the first production.
		for(int t = spec.terminals.nextSetBit(0);t >= 0;t = spec.terminals.nextSetBit(t+1))
		{
			c.getFirst(t).set(t);
		}
		// Repeat until first, follow, and nullable did not change in an iteration:
		boolean setsChanged = true;
		while(setsChanged)
		{
		    setsChanged = false;
		    // For each production X ::= Y[1]Y[2]...Y[k] in the grammar
		    for(int nt = spec.nonterminals.nextSetBit(0);nt >= 0;nt = spec.nonterminals.nextSetBit(nt+1))
		    {
		    	BitSet productions = spec.nt.getProductions(nt); 
		    	if(spec.nt.getProductions(nt).isEmpty()) continue;
		    	for(int p = productions.nextSetBit(0);p >= 0;p = productions.nextSetBit(p+1))
		    	{
		    		int x = spec.pr.getLHS(p);
			    	// If Y[1]...Y[k] are all nullable, nullable(x) := true.
			    	boolean isNullable = true;
			    	for(int i = 0;i < spec.pr.getRHSLength(p);i++)
			    	{
			    		isNullable &= c.isNullable(spec.pr.getRHSSym(p,i));
			    	}
			    	if(isNullable)
			    	{
			    		setsChanged |= !c.isNullable(x);
			    		c.setNullable(x,true);
			    	}
			    	// For each i from 1 to k:
			    	int frontNullableIndex = -1,rearNullableIndex = -1;
			    	// Gather nullable information. Loop will finish with
			    	// frontNullableIndex being the index of the first non-
			    	// nullable constituent in the production, rearNullableIndex
			    	// the last. Both will be equal to -1 if there is no
			    	// nullable element.
			    	for(int i = 0;i < spec.pr.getRHSLength(p);i++)
			    	{
			    		if(!c.isNullable(spec.pr.getRHSSym(p,i)))
			    		{
			    			if(frontNullableIndex == -1) frontNullableIndex = i;
			    			rearNullableIndex = i;
			    		}
			    	}
			    	for(int i = 0;i < spec.pr.getRHSLength(p);i++)
			    	{
			    		// If Y[1]...Y[i-1] are all nullable:
			    		if(frontNullableIndex == -1 || i <= frontNullableIndex)
			    		{
			    			// first(X) := union(first(X),first(Y[i]))
			    			setsChanged |= ParserSpec.union(c.getFirst(x),c.getFirst(spec.pr.getRHSSym(p,i)));
			    		}
			    		// If Y[i+1]...Y[k] are all nullable:
			    		if(rearNullableIndex == -1 || i >= rearNullableIndex)
			    		{
			    			// follow(Y[i]) := union(follow(Y[i]),follow(X))
			    			setsChanged |= ParserSpec.union(c.getFollow(spec.pr.getRHSSym(p,i)),c.getFollow(x));
			    		}
			    		// For each j from i+1 to k:
			    		boolean nullableSoFar = true;
			    		for(int j = i+1;j < spec.pr.getRHSLength(p);j++)
			    		{
			    			// If Y[i+1]...Y[j-1] are all nullable:
			    			if(j > i+1) nullableSoFar &= c.isNullable(spec.pr.getRHSSym(p,j-1));
			    			if(nullableSoFar)
			    			{
			    				// follow(Y[i]) := union(follow(Y[i]),first(Y[j]))
			    				setsChanged |= ParserSpec.union(c.getFollow(spec.pr.getRHSSym(p,i)),c.getFirst(spec.pr.getRHSSym(p,j)));
			    			}
			    			else break;
			    		}
			    	}
		    	}
		    }
		}
		
		return c;
	}
}
