package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents a "parser attribute" &ndash; a field specified within the parser class.
 * 
 * The field <code>attributeType</code> must be set to a non-null value
 * before a parser attribute is passed to the compiler. 
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 *
 */
public class ParserAttribute extends GrammarElement
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7191716890349062886L;
	/** The Java type of the parser attribute. */
	protected String attributeType;
	/** The code block executed at parser start time to initialize the parser attribute. */
	protected String code;
	
	public ParserAttribute()
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
	 * @see ParserAttribute#attributeType
	 */
	public String getAttributeType()
	{
		return attributeType;
	}

	/**
	 * @see ParserAttribute#attributeType
	 */
	public void setAttributeType(String attributeType)
	{
		this.attributeType = attributeType;
	}

	/**
	 * @see ParserAttribute#code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @see ParserAttribute#code
	 */
	public void setCode(String code)
	{
		this.code = code;
	}
	
	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitParserAttribute(this);
	}
}
