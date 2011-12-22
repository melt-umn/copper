package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents a disambiguation function &#8210; a method for resolving a specific
 * lexical ambiguity. This is done by specifying either a "declarative target" -- one of
 * the disambiguation group's members to which the ambiguity always disambiguates --
 * or (for the more general case) a block of code that returns one of the members.
 * 
 * The field <code>members</code>, as well as either the field <code>code</code> or
 * the field <code>disambiguateTo</code>, must be set to non-null values before a
 * disambiguation function is passed to the compiler. 

 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class DisambiguationFunctionBean extends GrammarElementBean
{
	/** The disambiguation function's code. */
	protected String code;
	/**
	 * If this is set, the disambiguation function will always return
	 * the terminal specified. This is meant to handle a special case of
	 * disambiguation function and cannot be specified concurrently
	 * with {@code code}.
	 */
	protected CopperElementReference disambiguateTo;
	/** The ambiguity to be resolved by the disambiguation function. */
	protected Set<CopperElementReference> members;
	
	public DisambiguationFunctionBean()
	{
		super(CopperElementType.DISAMBIGUATION_FUNCTION);
		code = null;
		members = null;
	}

	@Override
	/**
	 * @see CopperASTBean#isComplete()
	 */
	public boolean isComplete()
	{
		return super.isComplete() && ((code != null | disambiguateTo != null) && members != null);
	}
	
	@Override
	/**
	 * @see CopperASTBean#whatIsMissing()
	 */
	public Set<String> whatIsMissing()
	{
		Set<String> rv = super.whatIsMissing();
		if(code == null && disambiguateTo == null) rv.add("code or disambiguateTo");
		if(members == null) rv.add("members");
		return rv;
	}

	/**
	 * @see DisambiguationFunctionBean#code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @see DisambiguationFunctionBean#code
	 */
	public void setCode(String code)
	{
		this.code = code;
	}

	/**
	 * @see DisambiguationFunctionBean#members
	 */
	public Set<CopperElementReference> getMembers()
	{
		return members;
	}

	/**
	 * @see DisambiguationFunctionBean#members
	 */
	public void setMembers(Set<CopperElementReference> members)
	{
		this.members = members;
	}
		
	/**
	 * @see DisambiguationFunctionBean#disambiguateTo
	 */
	public CopperElementReference getDisambiguateTo()
	{
		return disambiguateTo;
	}

	/**
	 * @see DisambiguationFunctionBean#disambiguateTo
	 */
	public void setDisambiguateTo(CopperElementReference disambiguateTo)
	{
		this.disambiguateTo = disambiguateTo;
	}

	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitDisambiguationFunctionBean(this);
	}
}
