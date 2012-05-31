package edu.umn.cs.melt.copper.compiletime.builders;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LR0ItemSet;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LRLookaheadSets;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.MutableLRParseTable;

/**
 * Builds an LR parse table from an LR DFA and lookahead sets. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LRParseTableBuilder
{
	private ParserSpec spec;
	private LR0DFA dfa;
	private LRLookaheadSets lookaheadSets;
	
	private int transitionArraySize;
	private BitSet reduceActions;
	
	private LRParseTableBuilder(ParserSpec spec,LR0DFA dfa,LRLookaheadSets lookaheadSets)
	{
		this.spec = spec;
		this.dfa = dfa;
		this.lookaheadSets = lookaheadSets;
		this.transitionArraySize = Math.max(spec.terminals.length(),spec.nonterminals.length());
		this.reduceActions = new BitSet(); 
	}
	
	public static LRParseTable build(ParserSpec spec,LR0DFA dfa,LRLookaheadSets lookaheadSets)
	{
		return new LRParseTableBuilder(spec,dfa,lookaheadSets).buildParseTable();
	}
	
	public LRParseTable buildParseTable()
	{
		MutableLRParseTable parseTable = new MutableLRParseTable(dfa.size(),transitionArraySize);
		
		// For each LR DFA state:
		for(int state = 0;state < dfa.size();state++)
		{
			// For each item in the state:
			LR0ItemSet I = dfa.getItemSet(state);
			for(int item = 0;item < I.size();item++)
			{
				// If the item is reducible (A ::= a (*)):
				if(I.getPosition(item) == spec.pr.getRHSLength(I.getProduction(item)))
				{
					// Add reduce actions to the state as indicated by the lookahead.
					for(int LA = lookaheadSets.getLookahead(state,item).nextSetBit(0);LA >= 0;LA = lookaheadSets.getLookahead(state,item).nextSetBit(LA+1))
					{
						addAction(parseTable,state,LA,LRParseTable.REDUCE,I.getProduction(item));
					}
				}
				// If the item is ^ ::= S (*) $:
				if(I.getProduction(item) == spec.getStartProduction() && I.getPosition(item) == 1)
				{
					// Add an accept action to the cell.
					addAction(parseTable,state,spec.getEOFTerminal(),LRParseTable.ACCEPT,0);
				}
			}
			// For each transition out of the state:
			for(int X = dfa.getTransitionLabels(state).nextSetBit(0);X >= 0;X = dfa.getTransitionLabels(state).nextSetBit(X+1))
			{
				// Add a shift action to the corresponding cell.
				// Assumes that LRParseTable.GOTO and LRParseTable.SHIFT are equal.
				addAction(parseTable,state,X,LRParseTable.SHIFT,dfa.getTransition(state,X));
			}
		}
		
		for(int i = 0;i < parseTable.getConflictCount();i++)
		{
			LRParseTableConflict conflict = parseTable.getConflict(i);
			
			int shiftAction = conflict.shift;
						
			if(conflict.reduce.cardinality() > 1)
			{
				int encounteredOperatorClass = Integer.MIN_VALUE;
				int maxPrecedence = Integer.MIN_VALUE;
				boolean allOneClass = true;
				for(int p = conflict.reduce.nextSetBit(0);allOneClass && p >= 0;p = conflict.reduce.nextSetBit(p+1))
				{
					if(spec.pr.getPrecedence(p) > maxPrecedence)
					{
						maxPrecedence = spec.pr.getPrecedence(p);
						reduceActions.clear();
					}
					
					if(spec.pr.getPrecedence(p) == maxPrecedence) reduceActions.set(p);

					int opClass = spec.pr.getPrecedenceClass(p);
					if(encounteredOperatorClass == Integer.MIN_VALUE) encounteredOperatorClass = opClass;
					else allOneClass &= (opClass == encounteredOperatorClass);
				}
				if(!allOneClass || reduceActions.cardinality() > 1) continue;
			}
			
			if(shiftAction != -1 && reduceActions.cardinality() == 1)
			{
				int shiftOperator = conflict.getSymbol();
				int reduceOperator = spec.pr.getOperator(reduceActions.nextSetBit(0));
				
				if(shiftOperator != reduceOperator)
				{
					if(spec.t.getOperatorClass(shiftOperator) == spec.t.getOperatorClass(reduceOperator))
					{
						if(spec.t.getOperatorPrecedence(shiftOperator) > spec.t.getOperatorPrecedence(reduceOperator)) reduceActions.clear();
						else if(spec.t.getOperatorPrecedence(shiftOperator) > spec.t.getOperatorPrecedence(reduceOperator)) shiftAction = -1;
					}
				}
				else /*if(shiftOperator == reduceOperator)*/
				{
					switch(spec.t.getOperatorAssociativity(shiftOperator))
					{
					case LEFT:
						shiftAction = -1;
						break;
					case RIGHT:
						reduceActions.clear();
						break;
					case NONASSOC:
						shiftAction = -1;
						reduceActions.clear();
						break;
					case NONE:
					default:
						// No action
					}
				}
			}

			if(shiftAction == -1 && reduceActions.isEmpty())
			{
				parseTable.setActionType(conflict.getState(),conflict.getSymbol(),LRParseTable.ERROR);
				parseTable.setActionParameter(conflict.getState(),conflict.getSymbol(),0);
			}
			else if(shiftAction != -1 && reduceActions.isEmpty())
			{
				parseTable.setActionType(conflict.getState(),conflict.getSymbol(),LRParseTable.SHIFT);
				parseTable.setActionParameter(conflict.getState(),conflict.getSymbol(),shiftAction);				
			}
			else if(shiftAction == -1 && reduceActions.cardinality() == 1)
			{
				parseTable.setActionType(conflict.getState(),conflict.getSymbol(),LRParseTable.REDUCE);
				parseTable.setActionParameter(conflict.getState(),conflict.getSymbol(),reduceActions.nextSetBit(0));
			}
		}
		return parseTable;
	}
	
	private void addAction(MutableLRParseTable parseTable,int state,int symbol,byte type,int parameter)
	{
		// If there is already an action for this symbol:
		if(parseTable.getValidLA(state).get(symbol))
		{
			LRParseTableConflict conflict;
			// If there is already a conflict in the state, retrieve the object with information
			// about it.
			if(parseTable.getActionType(state,symbol) == LRParseTable.CONFLICT)
			{
				conflict = parseTable.getConflict(parseTable.getActionParameter(state, symbol));
			}
			// Otherwise, make a new conflict object and put the information about it in the
			// parse table.
			else
			{
				conflict = new LRParseTableConflict(state,symbol);
				switch(parseTable.getActionType(state,symbol))
				{
				case LRParseTable.SHIFT:
					conflict.shift = parseTable.getActionParameter(state,symbol);
					break;
				case LRParseTable.REDUCE:
					conflict.reduce.set(parseTable.getActionParameter(state,symbol));
					break;
				}
				parseTable.setActionType(state,symbol,LRParseTable.CONFLICT);
				parseTable.setActionParameter(state,symbol,parseTable.addConflict(conflict));
			}
			// Add the new action to the conflict.
			switch(type)
			{
			case LRParseTable.SHIFT:
				conflict.shift = parameter;
				break;
			case LRParseTable.REDUCE:
				conflict.reduce.set(parameter);
				break;
			}
		}
		// If there is not yet an action for the symbol, put the information.
		else
		{
			parseTable.getValidLA(state).set(symbol);
			parseTable.setActionType(state,symbol,type);
			parseTable.setActionParameter(state,symbol,parameter);
		}
	}
}