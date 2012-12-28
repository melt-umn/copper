package edu.umn.cs.melt.copper.runtime.engines.semantics;

import java.io.IOException;

import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.Location;

/**
 * A container in which a parser's semantic actions are executed.
 * @author schwerdf
 */
public interface SemanticActionContainer<MATCHDATA,EXCEPT extends Exception>
{
	public Location getStartRealLocation();
	public Location getEndRealLocation();

	public VirtualLocation getVirtualLocation();

	public void setLatchLocation(boolean latchLocation);

	public SpecialParserAttributes getSpecialAttributes();

	public void runDefaultTermAction() throws IOException, EXCEPT;

	public void runDefaultProdAction() throws IOException, EXCEPT;

	public void error(InputPosition pos, String message) throws EXCEPT;

	/**
	 * Runs the initialization code for each parser attribute. 
	 * @throws IOException When the code requires.
	 * @throws EXCEPT When the code requires.
	 */
	public void runInit() throws IOException, EXCEPT;

	/**
	 * Runs the semantic action code for a reduce action.
	 * @param _pos The input position at the reduction.
	 * @param _children The parse tree produced by the reduction.
	 * @param _prod The production being reduced.
	 * @throws IOException When the code requires.
	 * @throws EXCEPT When the code requires.
	 */
	public Object runSemanticAction(InputPosition _pos,
			Object[] _children, int _prod) throws IOException,EXCEPT;

	/**
	 * Runs the semantic action code for a shift action.
	 * @param _pos The input position before the shift.
	 * @param _terminal The terminal being shifted.
	 * @throws IOException When the code requires.
	 * @throws EXCEPT When the code requires.
	 */
	public Object runSemanticAction(InputPosition _pos,
			MATCHDATA _terminal) throws IOException,EXCEPT;

	/**
	 * Runs the semantic action code for a disambiguation by group.
	 * @param _pos The input position at the ambiguous scan.
	 * @param matches The matches returned by the scanner.
	 * @return The disambiguated match, or <CODE>null</CODE> if no disambiguation was available.
	 * @throws IOException When the code requires.
	 * @throws EXCEPT When the code requires.
	 */
	public int runDisambiguationAction(InputPosition _pos,
			MATCHDATA matches) throws IOException, EXCEPT;

}