package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar;

import edu.umn.cs.melt.copper.runtime.io.Location;

public class NonTerminalAttributes
{
	private GrammarName belongsTo;
	private String type;
	private Location declaredAt;
	private boolean isStartSym;
	
	public NonTerminalAttributes(GrammarName belongsTo,Location declaredAt)
	{
		this(belongsTo,declaredAt,false);
	}
	
	public NonTerminalAttributes(GrammarName belongsTo,Location declaredAt,boolean isStartSym)
	{
		this(belongsTo,declaredAt,"Object",isStartSym);
	}
	
	public NonTerminalAttributes(GrammarName belongsTo,Location declaredAt,String type,boolean isStartSym)
	{
		this.belongsTo = belongsTo;
		this.declaredAt = declaredAt;
		this.type = type;
		this.isStartSym = isStartSym;
	}

	public GrammarName getBelongsTo()
	{
		return belongsTo;
	}
	
	public Location getDeclaredAt()
	{
		return declaredAt;
	}
	
	public String getType()
	{
		return type;
	}

	public boolean isStartSym()
	{
		return isStartSym;
	}
	
	public static NonTerminalAttributes makeStartSymbol(NonTerminalAttributes attrs)
	{
		return new NonTerminalAttributes(attrs.belongsTo,attrs.declaredAt,attrs.type,true);
	}
	
	public String toString()
	{
		String rv = "";
		rv += "Belongs to grammar: " + belongsTo + "; is " + (isStartSym ? "" : "NOT ") + "start symbol";
		return rv;
	}
}
