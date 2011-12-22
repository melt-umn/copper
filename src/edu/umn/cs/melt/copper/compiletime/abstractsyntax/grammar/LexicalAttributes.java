package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar;

import edu.umn.cs.melt.copper.runtime.io.Location;


/**
 * Holds types of lexical precedence and matches for terminals.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LexicalAttributes
{
	private GrammarName belongsTo;
	private Location declaredAt;
	private String type;
	private String parserSemanticActionCode;
	private Terminal transparentPrefix;
	
	public LexicalAttributes(GrammarName belongsTo,
							 Location declaredAt,
							 String type,
			                 Terminal transparentPrefix,
			                 String parserSemanticActionCode)
	throws IndexOutOfBoundsException
	{
		this.belongsTo = belongsTo;
		this.declaredAt = declaredAt;
		this.type = type;
		this.transparentPrefix = transparentPrefix;
		this.parserSemanticActionCode = parserSemanticActionCode;
	}
	
	/**
	 * @return To what grammar the terminal belongs.
	 */
	public GrammarName getBelongsTo()
	{
		return belongsTo;
	}
	
	/**
	 * @return At what file-position the grammar was declared.
	 */
	public Location getDeclaredAt()
	{
		return declaredAt;
	}
	
	public String getType()
	{
		return type;
	}
	
	public Terminal getTransparentPrefix()
	{
		return transparentPrefix;
	}

	public String getParserSemanticActionCode()
	{
		return parserSemanticActionCode;
	}

	public String toString()
	{
		String rv = "";
		rv += "Belongs to grammar: " + belongsTo + "; Transparent prefix: " + transparentPrefix + "; ";
		rv += "Semantic action code: { " + parserSemanticActionCode + " }";
		return rv;
	}
	
}
