package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

import java.util.HashSet;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents a terminal class.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class TerminalClass extends GrammarElement
{
	private Set<CopperElementReference> members;
	
	public TerminalClass()
	{
		super(CopperElementType.TERMINAL_CLASS);
		members = new HashSet<CopperElementReference>(); 
	}
	
	public Set<CopperElementReference> getMembers()
	{
		return members;
	}
	
	public boolean addMember(CopperElementReference member)
	{
		return members.add(member);
	}

	public void setMembers(Set<CopperElementReference> members)
	{
		this.members = members;
	}

	@Override
	public <RT, E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT, E> visitor)
	throws E
	{
		return visitor.visitTerminalClass(this);
	}

}
