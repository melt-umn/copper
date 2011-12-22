package edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.lalr1;

import java.util.HashSet;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;


/**
 * Tables carrying all of an item's lookahead information: lookahead sets, beginning
 * layout, lookahead layout, and lookahead info (precedence classes "causing" a lookahead token).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LALR1LookaheadTables
{
	private HashSet<Terminal> lookahead;
	private HashSet<Terminal> beginningLayout;
	private Hashtable< Terminal,HashSet<Terminal> > lookaheadLayout;
	
	public LALR1LookaheadTables()
	{
		lookahead = new HashSet<Terminal>();
		beginningLayout = new HashSet<Terminal>();
		lookaheadLayout = new Hashtable< Terminal,HashSet<Terminal> >();
	}

	public boolean hasLookahead(Terminal lookaheadToken)
	{
		return lookahead.contains(lookaheadToken);
	}
	
	public HashSet<Terminal> getLookahead()
	{
		return lookahead;
	}
	
	public boolean addLookahead(Terminal newLookahead)
	{
		return lookahead.add(newLookahead);
	}
	
	public boolean addLookahead(HashSet<Terminal> newLookahead)
	{
		boolean rv = false;
		rv = lookahead.addAll(newLookahead) || rv;
		return rv;
	}
	
	public boolean hasBeginningLayout(Terminal layoutToken)
	{
		return beginningLayout.contains(layoutToken);
	}
	
	public boolean addBeginningLayout(Terminal newLayout)
	{
		return beginningLayout.add(newLayout);
	}
	
	public boolean addBeginningLayout(Iterable<Terminal> newLayout)
	{
		boolean rv = false;
		for(Terminal layoutToken : newLayout) rv = addBeginningLayout(layoutToken) || rv;
		return rv;
	}

	public HashSet<Terminal> getBeginningLayout()
	{
		return beginningLayout;
	}
	
	public Hashtable< Terminal,HashSet<Terminal> > getLookaheadLayout()
	{
		return lookaheadLayout;
	}

	public Iterable<Terminal> getLookaheadLayoutKeys()
	{
		return lookaheadLayout.keySet();
	}
	
	public boolean hasLookaheadLayout(Terminal layoutToken)
	{
		return lookaheadLayout.containsKey(layoutToken);
	}
	
	public boolean hasLookaheadLayout(Terminal layoutToken,Terminal lookaheadToken)
	{
		if(!hasLookaheadLayout(layoutToken)) return false;
		else return lookaheadLayout.get(layoutToken).contains(lookaheadToken);
	}
	
	public HashSet<Terminal> getLookaheadLayout(Terminal layoutToken)
	{
		if(lookaheadLayout.containsKey(layoutToken)) return lookaheadLayout.get(layoutToken);
		else return new HashSet<Terminal>();
	}
	
	public boolean addLookaheadLayout(Terminal layout,Terminal lookaheadToken)
	{
		HashSet<Terminal> lookaheadS = new HashSet<Terminal>();
		lookaheadS.add(lookaheadToken);
		return addLookaheadLayout(layout,lookaheadS);
	}
	
	public boolean addLookaheadLayout(Terminal layout,Iterable<Terminal> lookaheadTokens)
	{
		if(!lookaheadLayout.containsKey(layout)) lookaheadLayout.put(layout,new HashSet<Terminal>());
		boolean rv = false;
		for(Terminal l : lookaheadTokens) rv = lookaheadLayout.get(layout).add(l) || rv;
		return rv;
	}
	
	public LALR1LookaheadTables getCopy()
	{
		LALR1LookaheadTables rv = new LALR1LookaheadTables();
		rv.lookahead.addAll(lookahead);
		rv.beginningLayout.addAll(beginningLayout);
		for(Terminal t : lookaheadLayout.keySet()) rv.lookaheadLayout.put(t,new HashSet<Terminal>(lookaheadLayout.get(t)));
		return rv;
	}
	
	public boolean union(LALR1LookaheadTables rhs)
	{
		boolean rv = false;
		rv = lookahead.addAll(rhs.lookahead) || rv;
		rv = beginningLayout.addAll(rhs.beginningLayout) || rv;
		for(Terminal token : rhs.lookaheadLayout.keySet())
		{
			if(!lookaheadLayout.containsKey(token)) lookaheadLayout.put(token,new HashSet<Terminal>());
			rv = lookaheadLayout.get(token).addAll(rhs.lookaheadLayout.get(token)) || rv;
		}
		return rv;
	}
	
	public boolean equals(Object rhs)
	{
		if(rhs != null && rhs instanceof LALR1LookaheadTables)
		{
			LALR1LookaheadTables lrhs = (LALR1LookaheadTables) rhs;
			return lookahead.equals(lrhs.lookahead) &&
				   beginningLayout.equals(lrhs.beginningLayout) &&
				   lookaheadLayout.equals(lrhs.lookaheadLayout);
		}
		else return false;
	}

	
	public String toString()
	{
		String rv;
		rv = "[\n";
		rv += "  LOOKAHEAD = " + lookahead + "\n";
		rv += "  BEGINNING LAYOUT = " + beginningLayout + "\n";
		rv += "  LOOKAHEAD LAYOUT = [\n";
		for(Terminal t : lookaheadLayout.keySet())
		{
			rv += "    " + t + ": " + lookaheadLayout.get(t) + "\n";
		}
		rv += "  ]\n";
		return rv;
	}
}
