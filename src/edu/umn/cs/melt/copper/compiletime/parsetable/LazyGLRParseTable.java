package edu.umn.cs.melt.copper.compiletime.parsetable;


import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;


/**
 * Represents a [G]LR(1) parse table, with several actions allowed per cell.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class LazyGLRParseTable implements ParseTable
{
	private Hashtable< Integer,Hashtable<Terminal,TreeSet<ParseAction> > > parseActions;
	private Hashtable< Integer,Hashtable<NonTerminal,ShiftAction> > gotoActions;
	private Hashtable< Integer,Hashtable< Terminal,HashSet<Terminal> > > layouts;
	private Hashtable< Integer,Hashtable< Terminal,HashSet<Terminal> > > layoutsPerTerminal;
	private Hashtable< Integer,Hashtable< Terminal,HashSet< Terminal > > > prefixes;
	private Hashtable< Integer,Hashtable< Terminal,HashSet< Terminal > > > prefixesPerTerminal;
	protected HashSet<Terminal> shiftableUnion;
	private TreeSet<Integer> states;
	private int stateCount;
	
	protected abstract void initState(int statenum);
	
	protected abstract void initShiftableUnion();
	
	private void initStatesSet()
	{
		for(int i = 0;i < stateCount;i++) states.add(i); 
	}
	
	protected LazyGLRParseTable(int stateCount)
	{
		parseActions = new Hashtable< Integer,Hashtable< Terminal,TreeSet<ParseAction> > >();
		gotoActions = new Hashtable< Integer,Hashtable<NonTerminal,ShiftAction> >();
		layouts = new Hashtable< Integer,Hashtable<Terminal,HashSet<Terminal> > >();
		layoutsPerTerminal = new Hashtable< Integer,Hashtable<Terminal,HashSet<Terminal> > >();
		prefixes = new Hashtable< Integer,Hashtable<Terminal,HashSet<Terminal> > >();
		prefixesPerTerminal = new Hashtable< Integer,Hashtable<Terminal,HashSet<Terminal> > >();
		shiftableUnion = null;
		states = null;
		this.stateCount = stateCount;
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#addAction(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal, edu.umn.cs.melt.copper.compiletime.parsetable.ParseAction)
	 */
	public void addAction(int statenum,Terminal symbol,ParseAction action)
	{
		if(!parseActions.containsKey(statenum)) parseActions.put(statenum,new Hashtable< Terminal,TreeSet<ParseAction> >());
		if(!parseActions.get(statenum).containsKey(symbol)) parseActions.get(statenum).put(symbol,new TreeSet<ParseAction>());
		parseActions.get(statenum).get(symbol).add(action);
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#addGotoAction(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal, edu.umn.cs.melt.copper.compiletime.parsetable.ShiftAction)
	 */
	public void addGotoAction(int statenum,NonTerminal symbol,ShiftAction action)
	{
		if(!gotoActions.containsKey(statenum)) gotoActions.put(statenum,new Hashtable<NonTerminal,ShiftAction>());
		if(!gotoActions.get(statenum).containsKey(symbol)) gotoActions.get(statenum).put(symbol,action);
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#addLayout(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public void addLayout(int statenum,Terminal layout,Terminal token)
	{
		if(!layouts.containsKey(statenum)) layouts.put(statenum,new Hashtable< Terminal,HashSet<Terminal> >());
		if(!layouts.get(statenum).containsKey(layout)) layouts.get(statenum).put(layout,new HashSet<Terminal>());
		layouts.get(statenum).get(layout).add(token);
		if(!layoutsPerTerminal.containsKey(statenum)) layoutsPerTerminal.put(statenum,new Hashtable< Terminal,HashSet<Terminal> >());
		if(!layoutsPerTerminal.get(statenum).containsKey(token)) layoutsPerTerminal.get(statenum).put(token,new HashSet<Terminal>());
		layoutsPerTerminal.get(statenum).get(token).add(layout);
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#addPrefix(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public void addPrefix(int statenum,Terminal prefix,Terminal token)
	{
		if(!prefixes.containsKey(statenum)) prefixes.put(statenum,new Hashtable< Terminal,HashSet<Terminal> >());
		if(!prefixes.get(statenum).containsKey(prefix)) prefixes.get(statenum).put(prefix,new HashSet<Terminal>());
		prefixes.get(statenum).get(prefix).add(token);
		if(!prefixesPerTerminal.containsKey(statenum)) prefixesPerTerminal.put(statenum,new Hashtable< Terminal,HashSet<Terminal> >());
		if(!prefixesPerTerminal.get(statenum).containsKey(token)) prefixesPerTerminal.get(statenum).put(token,new HashSet<Terminal>());
		prefixesPerTerminal.get(statenum).get(token).add(prefix);
	}

	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#clearCell(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public void clearCell(int statenum,Terminal symbol)
	{
		if(!parseActions.containsKey(statenum) || !parseActions.get(statenum).containsKey(symbol)) return;
		parseActions.get(statenum).remove(symbol);
		if(parseActions.get(statenum).isEmpty()) parseActions.remove(statenum);
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#hasShiftable(int)
	 */
	public boolean hasShiftable(int statenum)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		return(parseActions.containsKey(statenum));
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#hasGotoable(int)
	 */
	public boolean hasGotoable(int statenum)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		return(gotoActions.containsKey(statenum));
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#hasAction(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public boolean hasAction(int statenum,Terminal symbol)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!parseActions.containsKey(statenum)) return false;
		else if(!parseActions.get(statenum).containsKey(symbol)) return false;
		else return true;
	}

	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#hasGotoAction(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal)
	 */
	public boolean hasGotoAction(int statenum,NonTerminal symbol)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!gotoActions.containsKey(statenum)) return false;
		else if(!gotoActions.get(statenum).containsKey(symbol)) return false;
		else return true;
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#hasLayout(int)
	 */
	public boolean hasLayout(int statenum)
	{
		 if(!parseActions.containsKey(statenum)) initState(statenum);
		 return layouts.containsKey(statenum);
	}

	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#hasPrefixes(int)
	 */
	public boolean hasPrefixes(int statenum)
	{
		 if(!parseActions.containsKey(statenum)) initState(statenum);
		 return prefixes.containsKey(statenum);
	}

	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#hasShiftableAfterLayout(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public boolean hasShiftableAfterLayout(int statenum,Terminal layout)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasLayout(statenum)) return false;
		else return layouts.get(statenum).containsKey(layout);
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#hasShiftableAfterLayout(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public boolean hasShiftableAfterLayout(int statenum, Terminal layout, Terminal afterLayout)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasLayout(statenum) || !layouts.get(statenum).containsKey(layout)) return false;
		else return layouts.get(statenum).get(layout).contains(afterLayout);
	}

	public boolean hasShiftableAfterPrefix(int statenum, Terminal prefix, Terminal afterPrefix)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasPrefixes(statenum) || !prefixes.get(statenum).containsKey(prefix)) return false;
		else return prefixes.get(statenum).get(prefix).contains(afterPrefix);
	}

	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getParseActions(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public Collection<ParseAction> getParseActions(int statenum,Terminal symbol)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasAction(statenum,symbol)) return null;
		else return parseActions.get(statenum).get(symbol);
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getParseAction(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public ParseAction getParseAction(int statenum, Terminal symbol)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasAction(statenum,symbol)) return null;
		else return parseActions.get(statenum).get(symbol).iterator().next();
	}

	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#countParseActions(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public int countParseActions(int statenum,Terminal symbol)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasAction(statenum,symbol)) return 0;
		else return parseActions.get(statenum).get(symbol).size();
	}

	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getShiftable(int)
	 */
	public Collection<Terminal> getShiftable(int statenum)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasShiftable(statenum)) return null;
		else return parseActions.get(statenum).keySet();
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getShiftableUnion()
	 */
	public Collection<Terminal> getShiftableUnion()
	{
		if(shiftableUnion == null) initShiftableUnion();
		return shiftableUnion;
	}

	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getGotoable(int)
	 */
	public Collection<NonTerminal> getGotoable(int statenum)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasGotoable(statenum)) return null;
		else return gotoActions.get(statenum).keySet();
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getLayout(int)
	 */
	public Collection<Terminal> getLayout(int statenum)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasLayout(statenum)) return null;
		else return layouts.get(statenum).keySet();
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getShiftableFollowingLayout(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public Collection<Terminal> getShiftableFollowingLayout(int statenum,Terminal layout)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasLayout(statenum) || !layouts.get(statenum).containsKey(layout)) return null;
		else return layouts.get(statenum).get(layout);
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getPrefixes(int)
	 */
	public Collection<Terminal> getPrefixes(int statenum)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasPrefixes(statenum)) return null;
		else return prefixes.get(statenum).keySet();
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getShiftableFollowingPrefix(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public Collection<Terminal> getShiftableFollowingPrefix(int statenum,Terminal prefix)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasPrefixes(statenum) || !prefixes.get(statenum).containsKey(prefix)) return null;
		else return prefixes.get(statenum).get(prefix);
	}

	public Collection<Integer> getStates()
	{
		if(states == null) initStatesSet();

		return states;
	}
	
	public int getLastState()
	{
		return stateCount - 1;
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getGotoAction(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal)
	 */
	public ShiftAction getGotoAction(int statenum,NonTerminal symbol)
	{
		if(!parseActions.containsKey(statenum)) initState(statenum);
		if(!hasGotoAction(statenum,symbol)) return null;
		else return gotoActions.get(statenum).get(symbol);
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#toString()
	 */
	public String toString()
	{
		return parseActions.toString() + "\n" + gotoActions.toString() + "\n" + layoutsPerTerminal.toString();
	}
	
	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#prettyPrint()
	 */
	public String prettyPrint()
	{
		HashSet<Terminal> tCols = new HashSet<Terminal>();;
		HashSet<NonTerminal> ntCols = new HashSet<NonTerminal>();
		int maxStateNum = -1;
		for(int i : parseActions.keySet())
		{
			maxStateNum = (i > maxStateNum) ? i : maxStateNum;
			tCols.addAll(parseActions.get(i).keySet());
		}
		for(int i : gotoActions.keySet())
		{
			maxStateNum = (i > maxStateNum) ? i : maxStateNum;
			ntCols.addAll(gotoActions.get(i).keySet());
		}
		String rv = "";
		for(Terminal t : tCols) rv += "\t" + t;
		for(NonTerminal nt : ntCols) rv += "\t" + nt;
		rv += "\t[LAYOUTS]";
		rv += "\n";
		for(int i = 0;i <= maxStateNum;i++)
		{
			rv += i;
			for(Terminal t : tCols)
			{
				rv += "\t";
				if(hasAction(i,t))
				{
					for(ParseAction act : getParseActions(i,t)) rv += act + " ";
				}
			}
			for(NonTerminal nt : ntCols)
			{
				rv += "\t";
				if(hasGotoAction(i,nt))	rv += getGotoAction(i,nt) + " ";
			}
			rv += "\t";
			if(hasLayout(i)) rv += layouts.get(i);
			rv += "\n";
		}
		return rv;
	}
}
