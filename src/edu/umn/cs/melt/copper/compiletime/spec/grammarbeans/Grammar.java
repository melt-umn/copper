package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.CopperASTBeanVisitor;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * Represents an entire Copper grammar.
 * 
 * The field <code>startSymbol</code> must be set to a non-null
 * values before a grammar is passed to the compiler.
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class Grammar extends CopperASTBean
{
	/** The elements of the grammar. */
	protected Hashtable<CopperElementName,GrammarElement> grammarElements;
	/** The layout (e.g., whitespace, comments) that may appear between the right-hand-side symbols of productions
	 * in this grammar (unless this is overridden by layout specified on a production individually). */
	protected Set<CopperElementReference> grammarLayout;
	
	private Hashtable< CopperElementType,Set<CopperElementName> > types; 
	
	public Grammar()
	{
		this(CopperElementType.GRAMMAR);
	}
	
	protected Grammar(CopperElementType type)
	{
		super(type);
		grammarElements = new Hashtable<CopperElementName,GrammarElement>();
		grammarLayout = null;
		
		types = new Hashtable< CopperElementType,Set<CopperElementName> >();
		for(CopperElementType t : CopperElementType.values()) types.put(t,new HashSet<CopperElementName>());
	}
		
	/**
	 * Determines whether this grammar is a "placeholder" -- a grammar that
	 * has been placed into a parser object but not filled with any
	 * content.
	 */
	public boolean isPlaceholder()
	{
		return (location == null || grammarElements.isEmpty());
	}
	
	@Override
	/**
	 * @see CopperASTBean#isComplete()
	 */
	public boolean isComplete()
	{
		return super.isComplete() && (grammarElements != null);
	}
	
	@Override
	/**
	 * @see CopperASTBean#whatIsMissing()
	 */
	public Set<String> whatIsMissing()
	{
		Set<String> rv = super.whatIsMissing();
		if(grammarElements == null) rv.add("grammarElements");
		return rv;
	}

	/**
	 * @see Grammar#grammarElements
	 */
	public GrammarElement getGrammarElement(CopperElementName element)
	{
		return grammarElements.get(element);
	}
	
	/**
	 * Returns a set containing the names of all elements in the grammar.
	 * @see Grammar#grammarElements
	 */
	public Set<CopperElementName> getGrammarElements()
	{
		return grammarElements.keySet();
	}
	
	/**
	 * Returns a set containing the names of all elements in the grammar that are of the given type.
	 */
	public Set<CopperElementName> getElementsOfType(CopperElementType type)
	{
		return types.get(type);
	}
	
	/**
	 * Adds a grammar element to the grammar.
	 * @see Grammar#grammarElements
	 * @param element The element to add.
	 * @throws AlreadyBoundException If a grammar of the same name is already specified in the parser. 
	 * @return <code>true</code> <code>grammar.getName() != null</code> and an element of the same name was not yet defined in this grammar.
	 */
	public boolean addGrammarElement(GrammarElement element)
	throws CopperException
	{
		if(element.getName() == null) throw new CopperException("Attempted to add a grammar element before setting its name");
		if(grammarElements.containsKey(element.getName())) throw new CopperException("Element " + element.getName() + " already exists in grammar " + getDisplayName());
		grammarElements.put(element.getName(),element);
		types.get(element.getType()).add(element.getName());
		return true;
	}
	
	/**
	 * @see Grammar#grammarLayout
	 */
	public Set<CopperElementReference> getGrammarLayout()
	{
		return grammarLayout;
	}

	/**
	 * @see Grammar#grammarLayout
	 */
	public void setGrammarLayout(Set<CopperElementReference> grammarLayout)
	{
		this.grammarLayout = grammarLayout;
	}

	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitGrammarBean(this);
	}
}
