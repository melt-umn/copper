package edu.umn.cs.melt.copper.legacy.compiletime.parsetable;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;


/**
 * Represents a "full" parse table, with read and update methods. This is
 * meant only for use with actual (dynamic) tables, such as <CODE>GLRParseTable</CODE>.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public interface ParseTable extends ReadOnlyParseTable
{

	/**
	 * Adds a parse action to the table.
	 * @param statenum The state number (row).
	 * @param symbol The token symbol (column).
	 * @param action The action.
	 */
	public void addAction(int statenum, Terminal symbol, ParseAction action);

	/**
	 * Adds a goto action to the table.
	 * @param statenum The state number (row).
	 * @param symbol The nonterminal symbol (column).
	 * @param action The action.
	 */
	public void addGotoAction(int statenum, NonTerminal symbol,
			ShiftAction action);

	/**
	 * Adds a layout prefix relation to the table.
	 * @param statenum The state number (row).
	 * @param layout The layout symbol.
	 * @param token The token that <CODE>layout</CODE> may precede in the given state.
	 */
	public void addLayout(int statenum, Terminal layout, Terminal token);

	/**
	 * Adds a transparent prefix relation to the table.
	 * @param statenum The state number (row).
	 * @param prefix The prefix symbol.
	 * @param token The token that <CODE>prefix</CODE> may precede in the given state.
	 */
	public void addPrefix(int statenum, Terminal prefix, Terminal token);

	/**
	 * Clears a cell in the parse table.
	 * @param statenum The state number (row).
	 * @param symbol The token symbol (column).
	 */
	public void clearCell(int statenum, Terminal symbol);

	public String toString();

	public String prettyPrint();

}