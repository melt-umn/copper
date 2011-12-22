package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents an "extension grammar" &#8210; a grammar that is meant to be
 * attached to another grammar and that matches the form to be passed
 * to Copper's composability checker (modular determinism analysis).
 * 
 * Fields <code>markingTerminal</code> and <code>startProduction</code>,
 * as well as all fields required for ordinary grammars, must be set
 * to non-null values before an extension grammar is passed to the compiler.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ExtensionGrammarBean extends GrammarBean
{
	/**
	 * The grammar's marking terminal. This must appear as the first
	 * right-hand-side symbol in <code>startProduction</code>.
	 */
	protected TerminalBean markingTerminal;
	/** The start production of the grammar, which must be of the form
	 * <code>H ::= m E</code>, where:
	 * <ul><li><code>H</code> is the name of a nonterminal
	 * in the "host" language;</li><li><code>m</code> is the name of
	 * <code>markingTerminal</code>; and </li><li><code>E</code> is this
	 * grammar's start symbol.</li></ul>
	 * The start production should not be placed in the
	 * grammar's list of productions.
	 */
	protected ProductionBean startProduction;
	
	public ExtensionGrammarBean()
	{
		super(CopperElementType.EXTENSION_GRAMMAR);
		markingTerminal = null;
		startProduction = null;
	}
	
	/**
	 * Fills this extension grammar with the elements of another grammar.
	 * Meant for the straightforward construction of several different
	 * extensions from a single grammar, as when a single extension is used
	 * with several host languages.
	 *  
	 * N.B.: This will make a shallow copy &#8210; any changes to the elements
	 * of the original grammar will carry over to this grammar,
	 * and vice versa.
	 */
	public void fill(GrammarBean grammar)
	{
		grammarElements = grammar.grammarElements;
		grammarLayout = grammar.grammarLayout;		
	}
	
	@Override
	/**
	 * @see CopperASTBean#isComplete(), GrammarBean#isComplete()
	 */
	public boolean isComplete()
	{
		return super.isComplete() && (markingTerminal != null && startProduction != null);
	}
	
	@Override
	/**
	 * @see CopperASTBean#whatIsMissing()
	 */
	public Set<String> whatIsMissing()
	{
		Set<String> rv = super.whatIsMissing();
		if(markingTerminal == null) rv.add("markingTerminal");
		if(startProduction == null) rv.add("startProduction");
		return rv;
	}

	/**
	 * @see ExtensionGrammarBean#markingTerminal
	 */
	public TerminalBean getMarkingTerminal()
	{
		return markingTerminal;
	}

	/**
	 * @see ExtensionGrammarBean#markingTerminal
	 */
	public void setMarkingTerminal(TerminalBean markingTerminal)
	{
		this.markingTerminal = markingTerminal;
	}

	/**
	 * @see ExtensionGrammarBean#startProduction
	 */
	public ProductionBean getStartProduction()
	{
		return startProduction;
	}

	/**
	 * @see ExtensionGrammarBean#startProduction
	 */
	public void setStartProduction(ProductionBean startProduction)
	{
		this.startProduction = startProduction;
	}
	
	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitExtensionGrammarBean(this);
	}	
}
