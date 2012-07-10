package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents an extended Copper parser &#8210; i.e., a parser
 * suitable to be run through Copper's modular determinism
 * analysis.
 * 
 * An extended parser contains one additional field,
 * {@link #hostGrammar}, and is subject to several further restrictions:
 * 
 * <ul>
 * <li>It must contain exactly two grammars, one of which is {@code hostGrammar}.</li>
 * <li>The grammar referred to by {@code hostGrammar} must not be
 *     an extension grammar; the parser's other grammar must be an extension grammar
 *     (<em>i.e.</em>, an instance of {@link ExtensionGrammarBean}).</li>
 * <li>{@code startSymbol} and all terminals in {@code startLayout} must belong to
 *     the host grammar.</li>
 * <li>The host grammar may not contain any references to elements of other grammars.</li>
 * </ul>
 * @see ParserBean
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ExtendedParserBean extends ParserBean
{
	/**
	 * A reference to the extended parser's host grammar.
	 */
	private CopperElementName hostGrammar;
	
	public ExtendedParserBean()
	{
		super(CopperElementType.EXTENDED_PARSER);
		hostGrammar = null;
	}
	
	/**
	 * @see #hostGrammar
	 */
	public CopperElementName getHostGrammar()
	{
		return hostGrammar;
	}

	/**
	 * @see #hostGrammar
	 */
	public void setHostGrammar(CopperElementName hostGrammar)
	{
		this.hostGrammar = hostGrammar;
	}

	/**
	 * @see #hostGrammar
	 */
	public void setHostGrammar(GrammarBean hostGrammar)
	{
		this.hostGrammar = hostGrammar.getName();
	}

	@Override
	/**
	 * @see CopperASTBean#isComplete()
	 */
	public boolean isComplete()
	{
		return super.isComplete() && hostGrammar != null;
	}
	
	@Override
	/**
	 * @see CopperASTBean#whatIsMissing()
	 */
	public Set<String> whatIsMissing()
	{
		Set<String> rv = super.whatIsMissing();
		if(hostGrammar == null) rv.add("hostGrammar");
		return rv;
	}		

	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitExtendedParserBean(this);
	}
}
