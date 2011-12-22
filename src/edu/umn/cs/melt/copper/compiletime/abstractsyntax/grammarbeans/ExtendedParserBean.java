package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents an extended Copper parser &#8210; i.e., a parser
 * containing one host grammar and several extension grammars.
 * All grammars in an <code>ExtendedParserBean</code> must be
 * extension grammars, except <code>primeGrammar</code>, which
 * must not be; each extension grammar's start production must
 * have a nonterminal of <code>primeGrammar</code> for its
 * left hand side.
 * @see ParserBean
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ExtendedParserBean extends ParserBean
{
	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitExtendedParserBean(this);
	}
}
