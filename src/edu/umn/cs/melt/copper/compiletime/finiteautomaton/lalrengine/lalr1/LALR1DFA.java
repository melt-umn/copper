package edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.lalr1;

import java.util.HashSet;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;


/**
 * Represents a LALR(1) DFA.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LALR1DFA
{
	private int nextLabel;
	private Hashtable<LALR1State,Integer> labels;
	private Hashtable<Integer,LALR1State> statesLabeled;
	private HashSet<LALR1State> states;
	private Hashtable< LALR1State,HashSet<LALR1Transition> > transitions;
	
	private Hashtable< LALR1State,Hashtable<LALR1StateItem,LALR1LookaheadTables> > lookaheadTables;
	
	
	public LALR1DFA()
	{
		nextLabel = 0;
		labels = new Hashtable<LALR1State,Integer>();
		statesLabeled = new Hashtable<Integer,LALR1State>();
		states = new HashSet<LALR1State>();
		transitions = new Hashtable< LALR1State,HashSet<LALR1Transition> >();
		lookaheadTables = new Hashtable< LALR1State,Hashtable<LALR1StateItem,LALR1LookaheadTables> >();
	}
	
	/**
	 * Determines whether there exist lookahead tables for a certain state.
	 * @param state The state.
	 * @return <CODE>true</CODE> if there exist tables for <CODE>state</CODE>.
	 */
	public boolean hasLookaheadTables(LALR1State state)
	{
		return lookaheadTables.containsKey(state);
	}

	/**
	 * Determines whether there exist lookahead tables for a certain item.
	 * @param state The state containing the item.
	 * @param item The item.
	 * @return <CODE>true</CODE> if there exist tables for <CODE>(state,item)</CODE>.
	 */
	public boolean hasLookaheadTables(LALR1State state,LALR1StateItem item)
	{
		return hasLookaheadTables(state) && lookaheadTables.get(state).containsKey(item);
	}
	
	/**
	 * Gets the lookahead tables for a certain state.
	 * @param state The state.
	 * @return Empty tables if <CODE>!hasLookaheadTables(state)</CODE>, the tables for <CODE>state</CODE> otherwise.
	 */
	public Hashtable<LALR1StateItem,LALR1LookaheadTables> getLookaheadTables(LALR1State state)
	{
		if(hasLookaheadTables(state)) return lookaheadTables.get(state);
		else return new Hashtable<LALR1StateItem,LALR1LookaheadTables>();
	}
	
	public static boolean isILSubset(LALR1State first,
			                         Hashtable<LALR1StateItem,LALR1LookaheadTables> firstTables,
			                         LALR1State second,
			                         Hashtable<LALR1StateItem,LALR1LookaheadTables> secondTables)
	{
		if(!first.isISubset(second)) return false;
		else
		{
			for(LALR1StateItem item : firstTables.keySet())
			{
				if(!secondTables.get(item).getLookahead().containsAll(firstTables.get(item).getLookahead())) return false;
			}
		}
		return true;
	}

	/**
	 * Gets the lookahead tables for a certain item.
	 * @param state The state containing the item.
	 * @param item The item.
	 * @return Empty tables if <CODE>!hasLookaheadTables(state,item)</CODE>, the tables for <CODE>(state,item)</CODE> otherwise.
	 */
	protected LALR1LookaheadTables getLookaheadTables(LALR1State state,LALR1StateItem item)
	{
		if(hasLookaheadTables(state,item)) return lookaheadTables.get(state).get(item);
		else return new LALR1LookaheadTables();
	}

	/**
	 * Unions given lookahead tables with those already in the DFA for a particular item.
	 * @param state The state containing the item.
	 * @param item The item.
	 * @param toUnion The new tables to union.
	 * @return <CODE>true</CODE> if the union changed the tables.
	 */
	public boolean unionLookaheadTables(LALR1State state,LALR1StateItem item,LALR1LookaheadTables toUnion)
	{
		if(!hasLookaheadTables(state)) lookaheadTables.put(state,new Hashtable<LALR1StateItem,LALR1LookaheadTables>());
		if(!hasLookaheadTables(state,item)) lookaheadTables.get(state).put(item,new LALR1LookaheadTables());
		return lookaheadTables.get(state).get(item).union(toUnion);
	}
	
	/**
	 * Adds a state to this DFA.
	 * @param state The state to add.
	 * @return <CODE>true</CODE> iff the state was not already present.
	 */
	public boolean addState(LALR1State state)
	{
		if(states.add(state))
		{
			int newLabel = nextLabel++;
			labels.put(state,newLabel);
			statesLabeled.put(newLabel,state);
			lookaheadTables.put(state,new Hashtable<LALR1StateItem,LALR1LookaheadTables>());
			for(LALR1StateItem item : state.getItems())
			{
				lookaheadTables.get(state).put(item,new LALR1LookaheadTables());
			}
			transitions.put(state,new HashSet<LALR1Transition>());
			return true;
		}
		else return false;
	}
	
	public boolean addLookahead(LALR1State state,LALR1StateItem item,HashSet<Terminal> newLookahead)
	{
		if(hasLookaheadTables(state,item)) return lookaheadTables.get(state).get(item).addLookahead(newLookahead);
		else return false;
	}
	
	public HashSet<Terminal> getLookahead(LALR1State state,LALR1StateItem item)
	{
		if(hasLookaheadTables(state,item)) return lookaheadTables.get(state).get(item).getLookahead();
		else return new HashSet<Terminal>();
	}
	
	public boolean lookaheadContains(LALR1State state,LALR1StateItem item,Terminal sym)
	{
		return hasLookaheadTables(state,item) && lookaheadTables.get(state).get(item).hasLookahead(sym);
	}
	
	public boolean addBeginningLayout(LALR1State state,LALR1StateItem item,HashSet<Terminal> newBeginningLayout)
	{
		if(hasLookaheadTables(state,item)) return lookaheadTables.get(state).get(item).addBeginningLayout(newBeginningLayout);
		else return false;
	}

	/**
	 * @return Returns the beginningLayout.
	 */
	public HashSet<Terminal> getBeginningLayout(LALR1State state,LALR1StateItem item)
	{
		if(hasLookaheadTables(state,item)) return lookaheadTables.get(state).get(item).getBeginningLayout();
		else return new HashSet<Terminal>();
	}
	
	/**
	 * @return Returns the lookaheadLayout.
	 */
	public Hashtable< Terminal,HashSet<Terminal> > getLookaheadLayout(LALR1State state,LALR1StateItem item)
	{
		return lookaheadTables.get(state).get(item).getLookaheadLayout();
	}
	
	/**
	 * Determines which of the lookahead symbols of this item may
	 * follow a particular layout token.
	 * @param layout The layout token.
	 * @return An iterable collection of lookahead symbols.
	 */
	public Iterable<Terminal> getLookaheadLayout(LALR1State state,LALR1StateItem item,Terminal layout)
	{
		return lookaheadTables.get(state).get(item).getLookaheadLayout(layout);
	}
	
	public boolean addLookaheadLayout(LALR1State state,LALR1StateItem item,Terminal layout,Terminal lookahead)
	{
		if(!hasLookaheadTables(state)) lookaheadTables.put(state,new Hashtable<LALR1StateItem,LALR1LookaheadTables>());
		if(!hasLookaheadTables(state,item)) lookaheadTables.get(state).put(item,new LALR1LookaheadTables());
		return lookaheadTables.get(state).get(item).addLookaheadLayout(layout,lookahead);
	}
	
	/**
	 * Adds a labeled state to a DFA: where <CODE>addState()</CODE>
	 * will assign a number label (table state number) automatically,
	 * this provides it manually. <B>Use with care</B>: this is meant
	 * to be used <I>only</I> when copying states from another DFA. 
	 * @param state The state to add.
	 * @param label The numerical label of the state.
	 * @return <CODE>true</CODE> iff the state were not already present.
	 */
	public boolean addLabeledState(LALR1State state,int label)
	{
		if(states.add(state))
		{
			labels.put(state,label);
			statesLabeled.put(label,state);
			lookaheadTables.put(state,new Hashtable<LALR1StateItem,LALR1LookaheadTables>());
			for(LALR1StateItem item : state.getItems())
			{
				lookaheadTables.get(state).put(item,new LALR1LookaheadTables());
			}
			return true;
		}
		else return false;
	}

	/**
	 * Adds a transition to this DFA.
	 * @param src The source state.
	 * @param label The label of the transition.
	 * @param dest The destination state.
	 * @return <CODE>true</CODE> iff the DFA contained both the src and dest states and there were not a transition already listed for these parameters.
	 */
	public boolean addTransition(LALR1State src,GrammarSymbol label,LALR1State dest)
	{
		if(!states.contains(src) || !states.contains(dest)) return false;
		return transitions.get(src).add(new LALR1Transition(src,label,dest));
	}

	/**
	 * Gets the states contained in this DFA.
	 * @return An iterable collection of states.
	 */
	public Iterable<LALR1State> getStates()
	{
		return states;
	}
	
	public boolean hasTransitions(LALR1State state)
	{
		return states.contains(state);
	}
	
	/**
	 * Gets the transitions contained in this DFA.
	 * @return An iterable collection of transitions.
	 */
	public Iterable<LALR1Transition> getTransitions(LALR1State state)
	{
		if(!states.contains(state)) return new HashSet<LALR1Transition>();
		else return transitions.get(state);
	}
	
	/**
	 * Gets the numerical label for a state.
	 * @param state The state.
	 * @return The label for this state, or -1 if the DFA does not contain it.
	 */
	public int getLabel(LALR1State state)
	{
		if(!states.contains(state)) return -1;
		else return labels.get(state);
	}

	/**
	 * Gets the state appertaining unto a particular numerical label.
	 * @param label The label.
	 * @return The state for this labe, or <CODE>null</CODE> if the DFA does not contain it.
	 */
	public LALR1State getState(int label)
	{
		if(!statesLabeled.containsKey(label)) return null;
		else return statesLabeled.get(label);
	}
	
	public int size()
	{
		return states.size();
	}

	public String toString()
	{
		String rv = "[\nSTATES = \n";
		for(LALR1State state : states)
		{
			rv += labels.get(state) + "=\n" + state.toString() + ",\n";
		}
		rv += "\nTRANSITIONS = \n";
		for(LALR1State state : states) for(LALR1Transition transition : transitions.get(state))
		{
			rv += labels.get(transition.getSrc()) + "->";
			rv += transition.getLabel() + "->";
			rv += labels.get(transition.getDest());
			rv += "\n";
		}
		rv += "\nTABLES = [\n";
		for(LALR1State state : states)
		{
			rv += "State " + getLabel(state) + ":\n";
			rv += "----------";
			for(LALR1StateItem item : state.getItems())
			{
				rv += "\n    " + item + ": " + lookaheadTables.get(state).get(item);
			}
			rv += "\n";
		}
		rv += "\n]";
		return rv;
	}
}
