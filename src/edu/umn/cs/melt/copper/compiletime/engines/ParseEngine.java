package edu.umn.cs.melt.copper.compiletime.engines;


import java.io.IOException;

import edu.umn.cs.melt.copper.compiletime.parsetable.ReadOnlyParseTable;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;


/* (non-Javadoc)
 * A superclass for the old-and-slow parse engine.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class ParseEngine
{
	/* (non-Javadoc)
	 * Gets a parse table. The parse table may be a statically created object that
	 * employs switch statements, etc., rather than an actual table.
	 * @return The parse table.
	 */
	protected abstract ReadOnlyParseTable getParseTable();
	
	/* (non-Javadoc)
	 * Sets up the static part of the engine: fills in the parse table, etc.
	 *
	 */
	public abstract void setupEngine()
	throws IOException,CopperException;
	/* (non-Javadoc)
	 * Sets up the dynamic part of the engine: starts the input at the beginning
	 * and sets any parse structures at beginning configuration.
	 *
	 */
	public abstract void startEngine(InputPosition initialPos)
	throws IOException,CopperException;
	
	public abstract Object runEngine()
	throws IOException,CopperException;
}
