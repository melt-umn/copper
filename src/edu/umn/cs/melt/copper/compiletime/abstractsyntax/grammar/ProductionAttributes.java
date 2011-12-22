package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar;

import java.util.HashSet;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.runtime.io.Location;


/* (non-Javadoc)
 * Holds information about productions: layout and precedence class.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ProductionAttributes
{
	private GrammarName belongsTo;
	private Location declaredAt;
	private int precedence;
	private HashSet<Terminal> layout;
	private TerminalClass precedenceClass;
	private String actionCode;
	private LinkedList<String> vars;
	
	public ProductionAttributes(GrammarName belongsTo,
			                    Location declaredAt,
			                    int precedence,
			                    LinkedList<String> vars,
			                    HashSet<Terminal> layout,
			                    TerminalClass precedenceClass,
			                    String actionCode)
	{
		this.belongsTo = belongsTo;
		this.declaredAt = declaredAt;
		this.precedence = precedence;
		this.vars = vars;
		this.layout = new HashSet<Terminal>(layout);
		this.precedenceClass = precedenceClass;
		this.actionCode = actionCode;
	}
	
	
	public GrammarName getBelongsTo()
	{
		return belongsTo;
	}
	
	public Location getDeclaredAt()
	{
		return declaredAt;
	}

	/* (non-Javadoc)
	 * @return Returns the precedence.
	 */
	public int getPrecedence()
	{
		return precedence;
	}
	
	/* (non-Javadoc)
	 * @return Returns the layout.
	 */
	public HashSet<Terminal> getLayout()
	{
		return layout;
	}
	/* (non-Javadoc)
	 * @return Returns the precedenceClass.
	 */
	public TerminalClass getPrecedenceClass()
	{
		return precedenceClass;
	}
	
	/* (non-Javadoc)
	 * @return Returns the actionCode.
	 */
	public String getActionCode()
	{
		return actionCode;
	}
	
	public LinkedList<String> getVars()
	{
		return vars;
	}

	public boolean equals(Object rhs)
	{
		if(rhs instanceof ProductionAttributes)
		{
			return layout.equals(((ProductionAttributes) rhs).layout) &&
				   precedenceClass.equals(((ProductionAttributes) rhs).precedenceClass);
		}
		else return false;
	}
	
	public String toString()
	{
		return "Belongs to grammar " + belongsTo + "; precedence " + precedence + "; layout " + layout + "; class " + precedenceClass + "; semantic action code = { " + actionCode + " }";
	}
}
