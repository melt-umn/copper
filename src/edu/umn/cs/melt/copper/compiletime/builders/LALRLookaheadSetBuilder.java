package edu.umn.cs.melt.copper.compiletime.builders;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ContextSets;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LRLookaheadSets;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LR0ItemSet;

/**
 * Annotates an existing LR(0) DFA with LALR(1) lookahead sets. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LALRLookaheadSetBuilder
{
	private ParserSpec spec;
	private ContextSets contextSets;
	private LR0DFA dfa;
	
	private LALRLookaheadSetBuilder(ParserSpec spec,ContextSets contextSets,LR0DFA dfa)
	{
		this.spec = spec;
		this.contextSets = contextSets;
		this.dfa = dfa;
	}
	
	public static LRLookaheadSets build(ParserSpec spec,ContextSets contextSets,LR0DFA dfa)
	{
		return new LALRLookaheadSetBuilder(spec,contextSets,dfa).buildLA();
	}
	
	public LRLookaheadSets buildLA()
	{
		LRLookaheadSets lookaheadSets = new LRLookaheadSets(dfa);
		
		BitSet activeTransitions = new BitSet();
		
		// States that have had changes to a lookahead set
		// in the current or previous iteration.
		BitSet[] fringeStates;
		// Items that have had changes to a lookahead set
		// in the current or previous iteration. Columns
		// are state indices.
		BitSet[][] fringeItems;
		
		fringeStates = new BitSet[2];
		fringeItems = new BitSet[2][dfa.size()];
		
		fringeStates[0] = new BitSet();
		fringeStates[1] = new BitSet();
		for(int i = 0;i < dfa.size();i++)
		{
			fringeItems[0][i] = new BitSet();
			fringeItems[1][i] = new BitSet();
		}
		int lastFringe = 1,currentFringe = 0;
		
		// Start in the DFA's start state,
		fringeStates[lastFringe].set(0);
		// with the initial item, ^ ::= S $.
		fringeItems[lastFringe][0].set(0);
		
		boolean setsChanged = true;
		while(setsChanged)
		{
			setsChanged = false;
			// For each state I in the fringe from the last iteration:
			for(int I = fringeStates[lastFringe].nextSetBit(0);I >= 0;I = fringeStates[lastFringe].nextSetBit(I+1))
			{
				// The group of items in the state in which a lookahead set was changed.
				BitSet seedItems = fringeItems[lastFringe][I];
				
				computeLookaheadClosure(lookaheadSets,I,seedItems);
				
				activeTransitions.clear();
				
				for(int item = seedItems.nextSetBit(0);item >= 0;item = seedItems.nextSetBit(item+1))
				{
					if(dfa.getItemSet(I).getPosition(item) < spec.pr.getRHSLength(dfa.getItemSet(I).getProduction(item)))
					{
						int X = spec.pr.getRHSSym(dfa.getItemSet(I).getProduction(item),dfa.getItemSet(I).getPosition(item));
						if(X != spec.getEOFTerminal())
						{
							activeTransitions.set(X);
						}
					}
				}
				for(int X = activeTransitions.nextSetBit(0);X >= 0;X = activeTransitions.nextSetBit(X+1))
				{
					int J = dfa.getTransition(I,X);
					for(int i = 0,item = dfa.getGotoItems(I,X).nextSetBit(0);item >= 0;i++,item = dfa.getGotoItems(I,X).nextSetBit(item+1))
					{
						if(seedItems.get(item))
						{
							boolean changed = ParserSpec.union(lookaheadSets.getLookahead(J,i),lookaheadSets.getLookahead(I,item));
							if(changed)
							{
								fringeStates[currentFringe].set(J);
								fringeItems[currentFringe][J].set(i);
							}
						}
					}
				}
				
				seedItems.clear();

			}
			fringeStates[lastFringe].clear();

			if(!fringeStates[currentFringe].isEmpty())
			{
				currentFringe = (currentFringe == 0) ? 1 : 0;
				lastFringe = (lastFringe == 0) ? 1 : 0;
				setsChanged = true;
			}
		}
		return lookaheadSets;
	}
	
	// Applies the X ::= a (*) b c,   d   ==> b ::= e (*) f,    first(cd) rule.
	private void computeLookaheadClosure(LRLookaheadSets lookaheadSets,int state,BitSet seedItems)
	{
		LR0ItemSet stateI = dfa.getItemSet(state);
		
		BitSet fringe1 = new BitSet(),fringe2 = new BitSet();
		BitSet combinedFirst = new BitSet();
		
		fringe1.or(seedItems);
		
		boolean setsChanged = true;
		while(setsChanged)
		{
			setsChanged = false;
			for(int item = fringe1.nextSetBit(0);item >= 0;item = fringe1.nextSetBit(item+1))
			{
				if(stateI.getPosition(item) >= spec.pr.getRHSLength(stateI.getProduction(item))) continue;
				combinedFirst.clear();
				boolean useLookahead = computeCombinedFirst(stateI.getProduction(item),stateI.getPosition(item),combinedFirst);
				// BOTTLENECK
				for(int i = 0;i < stateI.size();i++)
				{
					if(spec.pr.getLHS(stateI.getProduction(i)) == spec.pr.getRHSSym(stateI.getProduction(item),stateI.getPosition(item)))
					{
						boolean setChanged;
						setChanged = ParserSpec.union(lookaheadSets.getLookahead(state,i),combinedFirst);
						if(useLookahead) setChanged |= ParserSpec.union(lookaheadSets.getLookahead(state,i),lookaheadSets.getLookahead(state,item));
						if(setChanged)
						{
							setsChanged = true;
							fringe2.set(i);
						}
					}
				}
			}
			seedItems.or(fringe2);
			fringe1.clear();
			fringe1.or(fringe2);
		}
	}
	
	// Puts all the symbols of the "combined first" into the bit-set 'combinedFirst',
	// except for the lookahead; if all the symbols after 'position' are nullable
	// it will return 'true', indicating that the lookahead is also part of the
	// "combined first."
	private boolean computeCombinedFirst(int production,int position,BitSet combinedFirst)
	{
		boolean stillNullable = true;
		for(int i = position+1;i < spec.pr.getRHSLength(production) && stillNullable;i++)
		{
			combinedFirst.or(contextSets.getFirst(spec.pr.getRHSSym(production,i)));
			stillNullable &= contextSets.isNullable(spec.pr.getRHSSym(production,i));
		}
		return stillNullable;
	}
}
