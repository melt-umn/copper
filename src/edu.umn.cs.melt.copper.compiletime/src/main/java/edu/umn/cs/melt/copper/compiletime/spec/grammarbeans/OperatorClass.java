package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents an operator class, used to create separate scopes of precedence when resolving
 * shift-reduce or reduce-reduce conflicts.
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 *
 */
public class OperatorClass extends GrammarElement
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8135242635858659756L;

	public OperatorClass()
	{
		super(CopperElementType.OPERATOR_CLASS);
	}
	
	@Override
	public <RT, E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT, E> visitor)
	throws E
	{
		return visitor.visitOperatorClass(this);
	}
}
