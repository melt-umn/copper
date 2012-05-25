package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents an operator class, used to create separate scopes of precedence when resolving
 * shift-reduce or reduce-reduce conflicts.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class OperatorClassBean extends GrammarElementBean
{
	public OperatorClassBean()
	{
		super(CopperElementType.OPERATOR_CLASS);
	}
	
	@Override
	public <RT, E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT, E> visitor)
	throws E
	{
		return visitor.visitOperatorClassBean(this);
	}
}
