package edu.umn.cs.melt.copper.compiletime.parsetable;

import java.util.Collection;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;


/**
 * Represents a "read-only" parse table object, implementing the read methods
 * but not the construction methods. This may be used to implement static
 * tables as classes.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface ReadOnlyParseTable
{

	/**
	 * Determines whether a particular state has any parseActions assigned to it.
	 * @param statenum The state number (row).
	 * @return <CODE>true</CODE> iff an action has been inserted with this state number to it.
	 */
	public boolean hasShiftable(int statenum);

	/**
	 * Determines whether a particular state has any goto actions assigned to it.
	 * @param statenum The state number (row).
	 * @return <CODE>true</CODE> iff a goto action has been inserted with this state number to it.
	 */
	public boolean hasGotoable(int statenum);

	/**
	 * Determines whether a particular cell has any parseActions in it.
	 * @param statenum The state number (row).
	 * @param symbol The token symbol (column).
	 * @return <CODE>true</CODE> iff an action has been inserted with this state number and token symbol to it.
	 */
	public boolean hasAction(int statenum, Terminal symbol);

	/**
	 * Determines whether a particular cell has any goto actions in it.
	 * @param statenum The state number (row).
	 * @param symbol The nonterminal symbol (column).
	 * @return <CODE>true</CODE> iff an action has been inserted with this state number and token symbol to it.
	 */
	public boolean hasGotoAction(int statenum, NonTerminal symbol);

	/**
	 * Determines whether a particular state has any layout preceding any shiftable tokens.
	 * @param statenum The state number (row).
	 * @return <CODE>true</CODE> iff any layout has been inserted with this state number.
	 */
	public boolean hasLayout(int statenum);

	/**
	 * Determines whether a particular state has any shiftable tokens for a given state and layout token.
	 * @param statenum The state number (row).
	 * @param layout The layout token.
	 * @return <CODE>true</CODE> iff any following tokens have been inserted with this state number and layout token.
	 */
	public boolean hasShiftableAfterLayout(int statenum, Terminal layout);
	
	/**
	 * Determines whether a particular state has a particular shiftable token for a given state and layout token.
	 * @param statenum The state number (row).
	 * @param layout The layout token.
	 * @param afterLayout The shiftable token to be searched for.
	 * @return <CODE>true</CODE> iff the terminal <CODE>afterLayout</CODE> has been inserted under this state number and layout token.
	 */
	public boolean hasShiftableAfterLayout(int statenum, Terminal layout, Terminal afterLayout);

	/**
	 * Gets the parseActions for a certain cell.
	 * @param statenum The state number (row).
	 * @param symbol The token symbol (column).
	 * @return An Iterable collection of parseActions from the cell, or <CODE>null</CODE> if the cell contains no parseActions.
	 */
	public Collection<ParseAction> getParseActions(int statenum, Terminal symbol);
	
	/**
	 * Gets the first (or in deterministic implementations, only) parse action for a certain cell.
	 * @param statenum The state number (row).
	 * @param symbol The token symbol (column).
	 * @return The first element of the collection of parseActions from the cell, or <CODE>null</CODE> if the cell contains no parseActions.
	 */
	public ParseAction getParseAction(int statenum,Terminal symbol);

	/**
	 * Counts the parse actions in a certain cell.
	 * @param statenum The state number (row).
	 * @param symbol The token symbol (column).
	 * @return The number of parse actions in the cell.
	 */
	public int countParseActions(int statenum, Terminal symbol);

	/**
	 * Gets any terminals for which a state contain parseActions.
	 * @param statenum The state number (row).
	 * @return An Iterable collection of terminals for which this state contains parseActions, or <CODE>null</CODE> if the state have no parseActions associated with it.
	 */
	public Collection<Terminal> getShiftable(int statenum);
	
	/**
	 * Gets all terminals in the grammar.
	 * @return An Iterable collection of all terminals in the grammar.
	 */
	public Collection<Terminal> getShiftableUnion();
	
	/**
	 * Gets any nonterminals for which a state contain goto actions.
	 * @param statenum The state number (row).
	 * @return An Iterable collection of nonterminals for which this state contain gotoActions, or <CODE>null</CODE> if the state have no goto actions associated with it.
	 */
	public Collection<NonTerminal> getGotoable(int statenum);

	/**
	 * Gets any layout tokens that may be shifted in a state.
	 * @param statenum The state number (row).
	 * @return An iterable collection of layout terminals.
	 */
	public Collection<Terminal> getLayout(int statenum);

	/**
	 * Gets any shiftable tokens that appear after a given layout token.
	 * @param statenum The state number (row).
	 * @param layout The layout token.
	 * @return An iterable collection of terminals.
	 */
	public Collection<Terminal> getShiftableFollowingLayout(int statenum,
			Terminal layout);

	/**
	 * Gets the last state for which there be an entry in this parse table.
	 * @return The maximum state number to be found in the set returned by getStates() (defunct).
	 */
	public int getLastState();

	/**
	 * Gets the goto actions for a certain cell.
	 * @param statenum The state number (row).
	 * @param symbol The nonterminal symbol (column).
	 * @return The goto action in that cell.
	 */
	public ShiftAction getGotoAction(int statenum, NonTerminal symbol);

	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#hasPrefixes(int)
	 */
	public boolean hasPrefixes(int statenum);

	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getPrefixes(int)
	 */
    public Collection<Terminal> getPrefixes(int statenum);

	/**
	 * @see edu.umn.cs.melt.copper.compiletime.parsetable.ParseTable#getShiftableFollowingPrefix(int, edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal)
	 */
	public Collection<Terminal> getShiftableFollowingPrefix(int statenum,
			Terminal prefix);

	public String prettyPrint();
}