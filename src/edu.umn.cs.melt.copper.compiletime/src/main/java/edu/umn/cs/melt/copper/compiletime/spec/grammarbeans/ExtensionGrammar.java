package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

import java.util.HashSet;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents an "extension grammar" &ndash; a grammar that is meant to be
 * attached to another grammar and that matches the form to be passed
 * to Copper's composability checker (modular determinism analysis).
 * 
 * An extension grammar contains two additional fields, {@link #markingTerminals}
 * and {@link #bridgeProductions}, which must be non-empty before an extension grammar is
 * passed to the compiler.
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 *
 */
public class ExtensionGrammar extends Grammar
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6113945522624561641L;
	/**
	 * The grammar's marking terminals. Each production in {@code bridgeProductions}
	 * must have one of these as their first right-hand-side symbol, and they may
	 * not be used anywhere else.
	 */
	protected Set<CopperElementName> markingTerminals;
	/** The start productions of the grammar, which must be of the form
	 * <code>H ::= m E</code>, where:
	 * <ul><li><code>H</code> is the name of a nonterminal
	 * in the "host" grammar;</li><li><code>m</code> is the name of
	 * <code>markingTerminal</code>; and </li><li><code>E</code> is this
	 * grammar's start symbol.</li></ul>
	 * The start production should not be placed in the
	 * grammar's list of productions.
	 */
	protected Set<CopperElementName> bridgeProductions;
	/**
	 * Disambiguation functions that are "glue code", required to resolve lexical
	 * ambiguities between host terminals and the extension's marking terminals.
	 * These functions are not properly a part of the extension, but must be provided
	 * for the analysis to avoid lexical ambiguities.  
	 */
	protected Set<CopperElementName> glueDisambiguationFunctions;
	
	public ExtensionGrammar()
	{
		super(CopperElementType.EXTENSION_GRAMMAR);
		markingTerminals = new HashSet<CopperElementName>();
		bridgeProductions = new HashSet<CopperElementName>();
		glueDisambiguationFunctions = new HashSet<CopperElementName>();
	}
	
	/**
	 * Fills this extension grammar with the elements of another grammar.
	 * Meant for the straightforward construction of several different
	 * extensions from a single grammar, as when a single extension is used
	 * with several host languages.
	 *  
	 * N.B.: This will make a shallow copy &ndash; any changes to the elements
	 * of the original grammar will carry over to this grammar,
	 * and vice versa.
	 */
	public void fill(Grammar grammar)
	{
		grammarElements = grammar.grammarElements;
		grammarLayout = grammar.grammarLayout;		
	}
	
	
	/**
	 * @see #markingTerminals 
	 */
	public Set<CopperElementName> getMarkingTerminals()
	{
		return markingTerminals;
	}
	
	/**
	 * @see #markingTerminals 
	 */
	public Terminal getMarkingTerminal(CopperElementName name)
	{
		if(!markingTerminals.contains(name)) return null;
		return (Terminal) grammarElements.get(name);
	}

	/**
	 * @see #bridgeProductions
	 */
	public Set<CopperElementName> getBridgeProductions()
	{
		return bridgeProductions;
	}
	
	/**
	 * @see #bridgeProductions
	 */
	public Production getBridgeProduction(CopperElementName name)
	{
		if(!bridgeProductions.contains(name)) return null;
		return (Production) grammarElements.get(name);
	}

	/**
	 * @see #glueDisambiguationFunctions
	 */
	public Set<CopperElementName> getGlueDisambiguationFunctions()
	{
		return glueDisambiguationFunctions;
	}
	
	/**
	 * @see #glueDisambiguationFunctions
	 */
	public DisambiguationFunction getGlueDisambiguationFunctions(CopperElementName name)
	{
		if(!glueDisambiguationFunctions.contains(name)) return null;
		return (DisambiguationFunction) grammarElements.get(name);
	}

	/**
	 * @see #markingTerminals
	 */
	public boolean addMarkingTerminal(CopperElementName terminal)
	{
		return markingTerminals.add(terminal);
	}
	
	/**
	 * @see #bridgeProductions
	 */
	public boolean addBridgeProduction(CopperElementName production)
	{
		return bridgeProductions.add(production);
	}
	
	/**
	 * @see #glueDisambiguationFunctions
	 */
	public boolean addGlueDisambiguationFunction(CopperElementName function)
	{
		return glueDisambiguationFunctions.add(function);
	}
	
	@Override
	/**
	 * @see CopperASTBean#isComplete(), GrammarBean#isComplete()
	 */
	public boolean isComplete()
	{
		return super.isComplete() && (!markingTerminals.isEmpty() && !bridgeProductions.isEmpty());
	}
	
	@Override
	/**
	 * @see CopperASTBean#whatIsMissing()
	 */
	public Set<String> whatIsMissing()
	{
		Set<String> rv = super.whatIsMissing();
		if(markingTerminals.isEmpty()) rv.add("markingTerminals");
		if(bridgeProductions.isEmpty()) rv.add("bridgeProductions");
		return rv;
	}
	
	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitExtensionGrammar(this);
	}	
}
