package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents a "parser attribute" &#8210; a field specified within the parser class.
 * 
 * The field <code>attributeType</code> must be set to a non-null value
 * before a parser attribute is passed to the compiler. 
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ParserAttributeBean extends GrammarElementBean
{
	/** The Java type of the parser attribute. */
	protected String attributeType;
	/** The code block executed at parser start time to initialize the parser attribute. */
	protected String code;
	
	public ParserAttributeBean()
	{
		super(CopperElementType.PARSER_ATTRIBUTE);
	}
	
	@Override
	/**
	 * @see CopperASTBean#isComplete()
	 */
	public boolean isComplete()
	{
		return super.isComplete() && attributeType != null;
	}
	
	@Override
	/**
	 * @see CopperASTBean#whatIsMissing()
	 */
	public Set<String> whatIsMissing()
	{
		Set<String> rv = super.whatIsMissing();
		if(attributeType == null) rv.add("attributeType");
		return rv;
	}

	/**
	 * @see ParserAttributeBean#attributeType
	 */
	public String getAttributeType()
	{
		return attributeType;
	}

	/**
	 * @see ParserAttributeBean#attributeType
	 */
	public void setAttributeType(String attributeType)
	{
		this.attributeType = attributeType;
	}

	/**
	 * @see ParserAttributeBean#code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @see ParserAttributeBean#code
	 */
	public void setCode(String code)
	{
		this.code = code;
	}
	
	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitParserAttributeBean(this);
	}
}
