package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.semantics.lalr1.ComposabilityChecker;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class ContextSets
{
	private HashSet<GrammarSymbol> nullable;
	private Hashtable< GrammarSymbol,HashSet<Terminal> > first,follow;

	public ContextSets()
	{
		nullable = new HashSet<GrammarSymbol>();
		first = new Hashtable< GrammarSymbol,HashSet<Terminal> >();
		follow = new Hashtable< GrammarSymbol,HashSet<Terminal> >();
	}

	public Collection<Terminal> getFirst(GrammarSymbol spec)
	{
		if(!firstContains(spec,null)) return new HashSet<Terminal>();
		else return first.get(spec);
	}
	
	public boolean firstContains(GrammarSymbol spec,Terminal sym)
	{
		if(sym == null) return first.containsKey(spec);
		else if(!first.containsKey(spec)) return false;
		else return first.get(spec).contains(sym);
	}
	
	public boolean addToFirst(GrammarSymbol spec,Terminal sym)
	{
		if(!first.containsKey(spec)) first.put(spec,new HashSet<Terminal>());
		return first.get(spec).add(sym);
	}

	public Collection<Terminal> getFollow(GrammarSymbol spec)
	{
		if(!followContains(spec,null)) return new HashSet<Terminal>();
		else return follow.get(spec);
	}
	
	public boolean followContains(GrammarSymbol spec,Terminal sym)
	{
		if(sym == null) return follow.containsKey(spec);
		else if(!follow.containsKey(spec)) return false;
		else return follow.get(spec).contains(sym);
	}
	
	public boolean addToFollow(GrammarSymbol spec,Terminal sym)
	{
		if(!follow.containsKey(spec)) follow.put(spec,new HashSet<Terminal>());
		return follow.get(spec).add(sym);
	}

	public HashSet<GrammarSymbol> getNullable()
	{
		return nullable;
	}

	public boolean nullableContains(GrammarSymbol sym)
	{
		return nullable.contains(sym);
	}
	
	public boolean addToNullable(GrammarSymbol sym)
	{
		return nullable.add(sym);
	}
	
	public String toString()
	{
		String rv = "";
		rv += " FIRST = [";
		for(GrammarSymbol sym : first.keySet())
		{
			rv += "\n          " + sym + ": " + first.get(sym); 
		}
		rv += "]\n";
		rv += " FOLLOW = [";
		for(GrammarSymbol sym : follow.keySet())
		{
			rv += "\n          " + sym + ": " + follow.get(sym); 
		}
		rv += "]\n";
		rv += " NULLABLE = " + nullable + "\n";
		rv += "]";
		return rv;
	}

	/* (non-Javadoc)
	 * Computes the context sets (first, follow and nullable) as specified
	 * in Algorithm 3.13 of Appel's "Modern Compiler Implementation in Java,"
	 * Second Edition.
	 */
	public void compute(GrammarSource grammar,HashSet<GrammarName> wantedGrammars,CompilerLogger logger)
	throws CopperException
	{
		// When this is run for the first time, first and follow will be empty
		// and nullable will contain "false" for all symbols.
		// For each terminal symbol z, first(z) := {z}
		grammar.addToT(FringeSymbols.EOF);
		grammar.addToNT(FringeSymbols.STARTPRIME);
		grammar.addToP(grammar.getStartProd());
		for(Terminal z : grammar.getT())
		{
			grammar.getContextSets().addToFirst(z,z);
		}
		// Repeat until first, follow, and nullable did not change in an iteration:
		boolean setsChanged = true;
		while(setsChanged)
		{
			// DEBUG-BEGIN
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			// DEBUG-END
		    setsChanged = false;
		    // For each production X ::= Y[1]Y[2]...Y[k] in the grammar
		    for(NonTerminal nt : grammar.getNT())
		    {
		    	if(!grammar.pContains(nt)) continue;
		    	for(Production p : grammar.getP(nt))
		    	{
					if(!ComposabilityChecker.isWanted(grammar,wantedGrammars,p))
					{
						if(!ComposabilityChecker.isBridgeProd(grammar,wantedGrammars,p)) continue;
						else
						{
							p = Production.production(p.getName(),p.getLeft(),p.getSymbol(0));
						}
					}
			    	NonTerminal x = p.getLeft();
			    	// If Y[1]...Y[k] are all nullable, nullable(x) := true.
			    	boolean isNullable = true;
			    	for(GrammarSymbol sym : p.getRight())
			    	{
			    		if(!grammar.getContextSets().nullableContains(sym)) isNullable = false;
			    	}
			    	if(isNullable) setsChanged = grammar.getContextSets().addToNullable(x) || setsChanged;
			    	// For each i from 1 to k:
			    	int frontNullableIndex = -1,rearNullableIndex = -1;
			    	// Gather nullable information. Loop will finish with
			    	// frontNullableIndex being the index of the first non-
			    	// nullable constituent in the production, rearNullableIndex
			    	// the last. Both will be equal to -1 if there is no
			    	// nullable element.
			    	for(int i = 0;i < p.length();i++)
			    	{
			    		if(!grammar.getContextSets().nullableContains(p.getSymbol(i)))
			    		{
			    			if(frontNullableIndex == -1) frontNullableIndex = i;
			    			rearNullableIndex = i;
			    		}
			    	}
			    	for(int i = 0;i < p.length();i++)
			    	{
			    		// If Y[1]...Y[i-1] are all nullable:
			    		if(frontNullableIndex == -1 || i <= frontNullableIndex)
			    		{
			    			// first(X) := union(first(X),first(Y[i]))
			    			if(grammar.getContextSets().firstContains(p.getSymbol(i),null))
			    			{
			    				for(Terminal sym : grammar.getContextSets().getFirst(p.getSymbol(i)))
			    				{
			    					setsChanged = grammar.getContextSets().addToFirst(x,sym) || setsChanged;
			    				}
			    			}
			    		}
			    		// If Y[i+1]...Y[k] are all nullable:
			    		if(rearNullableIndex == -1 || i >= rearNullableIndex)
			    		{
			    			// follow(Y[i]) := union(follow(Y[i]),follow(X))
			    			if(grammar.getContextSets().followContains(x,null) && p.getSymbol(i) instanceof NonTerminal)
			    			{
			    				for(Terminal sym : grammar.getContextSets().getFollow(x))
			    				{
			    					setsChanged = grammar.getContextSets().addToFollow(p.getSymbol(i),sym) || setsChanged;
			    				}
			    			}
			    		}
			    		// For each j from i+1 to k:
			    		for(int j = i+1;j < p.length();j++)
			    		{
			    			// If Y[i+1]...Y[j-1] are all nullable:
			    			boolean sliceNullable = true;
			    			for(int l = i+1;l < j;l++)
			    			{
			    				if(!grammar.getContextSets().nullableContains(p.getSymbol(l)))
			    				{
			    					sliceNullable = false;
			    					break;
			    				}
			    			}
		    				// follow(Y[i]) := union(follow(Y[i]),first(Y[j]))
			    			if(sliceNullable)
			    			{
			    				if(grammar.getContextSets().firstContains(p.getSymbol(j),null) && p.getSymbol(i) instanceof NonTerminal)
			    				{
			    					for(Terminal sym : grammar.getContextSets().getFirst(p.getSymbol(j)))
			    					{
			    						setsChanged = grammar.getContextSets().addToFollow(p.getSymbol(i),sym) || setsChanged;
			    					}
			    				}
			    			}
			    		}
			    	}
		    	}
		    }
		}
	}
}
