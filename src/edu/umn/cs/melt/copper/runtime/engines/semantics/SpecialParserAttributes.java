package edu.umn.cs.melt.copper.runtime.engines.semantics;


/**
 * Holds "special parser attributes" reset before performing each
 * semantic action, meant for carrying information back to the core parser
 * for use.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class SpecialParserAttributes
{
	/** The "virtual location" of a certain token, an attribute on parse-tree leaf nodes. */ 
	public VirtualLocation virtualLocation;
	/** Whether or not to "latch" the location on a shift action,
	 *  i.e., re-scan the same token on the next scan. */
	public boolean latchLocation;
	
	public SpecialParserAttributes(VirtualLocation oldVirtualLocation)
	{
		virtualLocation = oldVirtualLocation;
		latchLocation = false;
	}
	
	/** Resets per-terminal attributes. */
	public void resetSpecialAttributes()
	{
		virtualLocation = null;
		latchLocation = false;
	}

	/**
	 * Determines if a parser attribute name can be validly used.
	 * @param attrName The proposed name.
	 * @return <CODE>true</CODE> iff the name may be validly used.
	 */
	public static boolean isValidAttrName(String attrName)
	{
		return !attrName.equals("_pos") &&
		       !attrName.equals("_parseTree") &&
		       !attrName.equals("_prod") &&
		       !attrName.equals("_reporter") &&
		       !attrName.equals("_positionFollowing") &&
		       !attrName.equals("_terminal") &&
		       !attrName.equals("_layouts") &&
		       !attrName.equals("_specialAttributes") &&
		       !attrName.equals("lexeme") &&
		       !attrName.matches("sym_[0-9]+") &&
		       !attrName.matches("group_[0-9]+");
	}
}
