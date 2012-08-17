package edu.umn.cs.melt.copper.compiletime.checkers;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.NonterminalNonterminalMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.UselessNonterminalMessage;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * Checks that a grammar is "well-formed;" i.e., that it contains no nonterminals with no terminal
 * derivations, and, if specified, that it contains no "useless" nonterminals.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class GrammarWellFormednessChecker
{
	public static boolean check(CompilerLogger logger,GrammarStatistics stats,PSSymbolTable symbolTable,ParserSpec spec,boolean reportUselessWarnings)
	throws CopperException
	{
		boolean passed = true;
		BitSet encountered = new BitSet();
		BitSet fringe = new BitSet();
		BitSet newFringe = new BitSet();
		BitSet uselessNTs = new BitSet();
		
		// Detect the set of nonterminals accessible by derivation from the start symbol
		// (i.e., { A : (S =>* a A b) } where a and b are grammar symbol sequences),
		// and remove from it all nonterminals with no productions.
		// Store it in the set "encountered."
		fringe.set(spec.getStartNonterminal());
		
		while(!fringe.isEmpty())
		{
			for(int fringeNT = fringe.nextSetBit(0);fringeNT >= 0;fringeNT = fringe.nextSetBit(fringeNT+1))
			{
				if(spec.nt.getProductions(fringeNT).isEmpty())
				{
					uselessNTs.set(fringeNT);
					continue;
				}
				else
				{
					encountered.set(fringeNT);
					for(int p = spec.nt.getProductions(fringeNT).nextSetBit(0);p >= 0;p = spec.nt.getProductions(fringeNT).nextSetBit(p+1))
					{
						for(int i = 0;i < spec.pr.getRHSLength(p);i++)
						{
							int rhsSym = spec.pr.getRHSSym(p,i);
							if(spec.nonterminals.get(rhsSym) &&
							   !encountered.get(rhsSym)) newFringe.set(rhsSym); 
						}
					}
				}
			}
			fringe.clear();
			fringe.or(newFringe);
			newFringe.clear();
		}
		// Useless nonterminals are those not in "encountered."
		// This loop further classifies them into nonterminals with no productions
		// (causes a warning) and with productions (causes an error).
		BitSet newUseless = new BitSet();
		newUseless.or(spec.nonterminals);
		newUseless.andNot(encountered);
		
		uselessNTs.or(newUseless);
		
		for(int nt = uselessNTs.nextSetBit(0);nt >= 0;nt = uselessNTs.nextSetBit(nt+1))
		{
			if(logger.isLoggable(CompilerLevel.REGULAR)) logger.log(new UselessNonterminalMessage(symbolTable.getNonTerminal(nt)));
		}
		
		stats.uselessNTs = uselessNTs;
		
		if(!passed) return false;
		boolean setChanged = true;
		boolean hasTerminal;
		fringe.clear();

		fringe.or(spec.nonterminals);
		fringe.andNot(uselessNTs);
		
		// Check for any nonterminals that cannot derive a terminal string.
		// This is done by progressively "validating" the nonterminals, starting
		// with those that have productions with only terminals on the RHS,
		// then progressing to those with productions having only terminals and
		// "validated" nonterminals.
		while(setChanged)
		{
			setChanged = false;
			for(int nt = fringe.nextSetBit(0);nt >= 0;nt = fringe.nextSetBit(nt+1))
			{
				hasTerminal = false;
				for(int p = spec.nt.getProductions(nt).nextSetBit(0);p >= 0;p = spec.nt.getProductions(nt).nextSetBit(p+1))
				{
					boolean isTerminal = true;
					for(int i = 0;i < spec.pr.getRHSLength(p);i++)
					{
						int rhsSym = spec.pr.getRHSSym(p,i);
						if(!spec.terminals.get(rhsSym) && !uselessNTs.get(rhsSym) && fringe.get(rhsSym))
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
					fringe.clear(nt);
					setChanged = true;
				}
			}
		}
		// If any "encountered" nonterminal derives a nonterminal
		// with no productions, put it in the set of nonterminals with
		// no terminal derivation.
		for(int nt = encountered.nextSetBit(0);nt >= 0;nt = encountered.nextSetBit(nt+1))
		{
			for(int p = spec.nt.getProductions(nt).nextSetBit(0);p >= 0;p = spec.nt.getProductions(nt).nextSetBit(p+1))
			{
				for(int i = 0;i < spec.pr.getRHSLength(p);i++)
				{
					int rhsSym = spec.pr.getRHSSym(p,i);
					if(spec.nonterminals.get(rhsSym) &&
					   spec.nt.getProductions(rhsSym).isEmpty())
					{
						fringe.set(nt);
					}
				}
			}
		}

		stats.nonTerminalNTs = fringe;
		for(int nt = fringe.nextSetBit(0);nt >= 0;nt = fringe.nextSetBit(nt+1))
		{
			passed = false;
			if(logger.isLoggable(CompilerLevel.QUIET)) logger.log(new NonterminalNonterminalMessage(symbolTable.getNonTerminal(nt)));
		}
		
		return passed;		
	}
}
