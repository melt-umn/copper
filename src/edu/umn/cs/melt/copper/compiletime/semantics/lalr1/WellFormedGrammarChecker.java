package edu.umn.cs.melt.copper.compiletime.semantics.lalr1;

import java.util.HashSet;
import java.util.Iterator;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class WellFormedGrammarChecker
{
	public CompilerLogger logger;
	
	public WellFormedGrammarChecker(CompilerLogger logger)
	{
		this.logger = logger;
	}
	
	public boolean checkWellFormedness(GrammarSource grammar,boolean reportUselessWarnings)
	throws CopperException
	{
		boolean passed = true;
		HashSet<NonTerminal> encountered = new HashSet<NonTerminal>();
		HashSet<NonTerminal> fringe = new HashSet<NonTerminal>();
		HashSet<NonTerminal> newFringe = new HashSet<NonTerminal>();
		HashSet<NonTerminal> uselessNTs = new HashSet<NonTerminal>();
		// Detect the set of nonterminals accessible by derivation from the start symbol
		// (i.e., { A : (S =>* a A b) } where a and b are grammar symbol sequences),
		// and remove from it all nonterminals with no productions.
		// Store it in the set "encountered."
		fringe.add(grammar.getStartSym());
		while(!fringe.isEmpty())
		{
			for(NonTerminal fringeSym : fringe)
			{
				logger.logTick(MasterController.DOT_WINDOW,".");
				if(grammar.getP(fringeSym) == null)
				{
					uselessNTs.add(fringeSym);
					continue;
				}
				else if(grammar.getP(fringeSym).iterator().hasNext())
				{
					encountered.add(fringeSym);
				}
				for(Production p : grammar.getP(fringeSym))
				{
					for(GrammarSymbol rhsSym : p.getRight())
					{
						if(rhsSym instanceof NonTerminal &&
						   !encountered.contains(rhsSym)) newFringe.add((NonTerminal) rhsSym); 
					}
				}
			}
			fringe = newFringe;
			newFringe = new HashSet<NonTerminal>();
		}
		// Useless nonterminals are those not in "encountered."
		// This loop further classifies them into nonterminals with no productions
		// (causes a warning) and with productions (causes an error).  
		for(NonTerminal nt : grammar.getNT())
		{
			if(!encountered.contains(nt))
			{
				uselessNTs.add(nt);
				CompilerLogMessageSort sort = CompilerLogMessageSort.WARNING;
				// Comment out this conditional to make all useless-nonterminal messages into warnings.
				/*if(grammar.getP(nt) != null &&
				   grammar.getP(nt).iterator().hasNext())
				{
					passed = false;
					sort = CompilerLogMessageSort.ERROR;
				}
				else*/ if(!reportUselessWarnings) continue;
				
				if(logger.isLoggable(sort)) logger.logMessage(sort,null,"Useless nonterminal '" + nt + "'");
				grammar.setUselessNonterminalCount(grammar.getUselessNonterminalCount() + 1);
			}
		}
		if(!passed) return false;
		boolean setChanged = true;
		fringe.clear();
		for(NonTerminal nt : grammar.getNT()) fringe.add(nt);
		// Check for any nonterminals that cannot derive a terminal string.
		// This is done by progressively "validating" the nonterminals, starting
		// with those that have productions with only terminals on the RHS,
		// then progressing to those with productions having only terminals and
		// "validated" nonterminals.
		while(setChanged)
		{
			setChanged = false;
			for(Iterator<NonTerminal> it = fringe.iterator();it.hasNext();)
			{
				NonTerminal nt = it.next();
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
				if(uselessNTs.contains(nt))
				{
					it.remove();
					continue;
				}
				boolean hasTerminal = false;
				for(Production p : grammar.getP(nt))
				{
					boolean isTerminal = true;
					for(GrammarSymbol rhsSym : p.getRight())
					{
						if(!(rhsSym instanceof Terminal) &&
						   !uselessNTs.contains(rhsSym) && fringe.contains(rhsSym))
						{
							isTerminal = false;
							break;
						}
					}
					hasTerminal |= isTerminal;
					if(hasTerminal) break;
				}
				if(hasTerminal)
				{
					it.remove();
					setChanged = true;
				}
			}
		}
		// If any "encountered" nonterminal derives a nonterminal
		// with no productions, put it in the set of nonterminals with
		// no terminal derivation.
		for(NonTerminal nt : encountered)
		{
			if(grammar.getP(nt) != null)
			for(Production p : grammar.getP(nt))
			{
				for(GrammarSymbol rhsSym : p.getRight())
				{
					if(rhsSym instanceof NonTerminal &&
					   grammar.getP(rhsSym) == null)
					{
						fringe.add(nt);
					}
				}
			}
		}
		for(NonTerminal nt : fringe)
		{
			passed = false;
			if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logMessage(CompilerLogMessageSort.ERROR,null,"Nonterminal '" + nt + "' has no terminal derivations");
		}
		if(!passed) return false;
		
		return passed;
	}
}
