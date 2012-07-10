package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.util.Hashtable;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * Represents a Copper parser to be built.
 * The field <code>primeGrammar</code> must be set to a non-null
 * value before a parser is passed to the compiler.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ParserBean extends CopperASTBean
{
	/** The grammars to include in this parser. */
	protected Hashtable<CopperElementName,GrammarBean> grammars;
	/**
	 * The parser's start symbol.
	 */
	protected CopperElementReference startSymbol;
	/**
	 * The layout (e.g., whitespace, comments) that may appear at the beginning and end of input to this parser.
	 * If not specified, defaults to the grammar layout of the grammar containing the parser's start symbol.
	 */
	protected Set<CopperElementReference> startLayout;
	/**
	 * Whether the parser is "unitary." A unitary parser is compiled with
	 * no grammar information recorded in its element names (i.e., a terminal
	 * named "x" contained in a grammar named "Grammar" will, in the compiled
	 * parser, be named "x" rather than "Grammar$x".
	 * A unitary parser must contain exactly one grammar.
	 */
	protected boolean isUnitary;
	
	/**
	 * The name of the package in which the parser class will be placed.
	 * If this is set, a package declaration line will be placed in the Java
	 * source file output by the parser generator, above the contents of
	 * <code>preambleCode</code>.
	 */
	protected String packageDecl;
	/**
	 * The name of the Java class to which this parser will be compiled.
	 * If it is not specified, the name of the parser itself will be used.
	 * N.B.: <code>className</code> must not be a fully qualified name.
	 */
	protected String className;
	/** Code to run at parser start time. */
	protected String parserInitCode;
	/** Code to run after the conclusion of a parse. */
	protected String postParseCode;
	/** Code to place at the beginning of the parser source file (e.g., package and import statements). */
	protected String preambleCode;
	/** Code to place inside the parser class (e.g., fields and auxiliary functions). */
	protected String parserClassAuxCode;
	/**
	 * Code to place inside the semantic action container.
	 * @see edu.umn.cs.melt.copper.runtime.engines.semantics.SemanticActionContainer
	 */
	protected String semanticActionAuxCode;
	/** A default semantic action for productions. */
	protected String defaultProductionCode;
	/** A default semantic action for terminals. */
	protected String defaultTerminalCode;
	
	public ParserBean()
	{
		this(CopperElementType.PARSER);
	}
	
	protected ParserBean(CopperElementType type)
	{
		super(type);
		grammars = new Hashtable<CopperElementName,GrammarBean>();
		isUnitary = false;
		startSymbol = null;
		packageDecl = null;
		className = null;
		parserInitCode = null;
		postParseCode = null;
		preambleCode = null;
		parserClassAuxCode = null;
		semanticActionAuxCode = null;
		defaultProductionCode = null;
		defaultTerminalCode = null;
	}
	
	@Override
	/**
	 * @see CopperASTBean#isComplete()
	 */
	public boolean isComplete()
	{
		return super.isComplete() && (grammars != null && startSymbol != null);
	}
	
	@Override
	/**
	 * @see CopperASTBean#whatIsMissing()
	 */
	public Set<String> whatIsMissing()
	{
		Set<String> rv = super.whatIsMissing();
		if(grammars.isEmpty()) rv.add("grammars");
		if(startSymbol == null) rv.add("startSymbol");
		return rv;
	}

	/**
	 * @see ParserBean#grammars
	 */
	public GrammarBean getGrammar(CopperElementName grammarName)
	{
		return grammars.get(grammarName);
	}
	
	/**
	 * @see ParserBean#grammars
	 */
	public Set<CopperElementName> getGrammars()
	{
		return grammars.keySet();
	}

	/**
	 * Adds a grammar to the parser, indexing it by the name specified within the grammar object.
	 * If <code>grammar.getName() == null</code>, no action is taken.
	 * @see ParserBean#grammars
	 * @param grammar The grammar to add.
	 * @throws CopperException If a grammar of the same name is already specified in the parser. 
	 * @return <code>true</code> iff <code>grammar.getName() != null</code> and a grammar of the same name was not yet defined in this parser.  
	 */
	public boolean addGrammar(GrammarBean grammar)
	throws CopperException
	{
		if(grammar.getName() == null)  throw new CopperException("Attempted to add a grammar element before setting its name");
		if(grammars.containsKey(grammar.getName())) throw new CopperException("Grammar " + grammar.getName() + " already exists in parser " + getDisplayName());
		else return (grammars.put(grammar.getName(),grammar) == null);
	}

	/**
	 * @see ParserBean#isUnitary
	 */
	public boolean isUnitary() {
		return isUnitary;
	}

	/**
	 * @see ParserBean#isUnitary
	 */
	public void setUnitary(boolean isUnitary) {
		this.isUnitary = isUnitary;
	}

	/**
	 * @see ParserBean#startSymbol
	 */
	public CopperElementReference getStartSymbol()
	{
		return startSymbol;
	}

	/**
	 * @see ParserBean#startSymbol
	 */
	public void setStartSymbol(CopperElementReference startSymbol)
	{
		this.startSymbol = startSymbol;
	}

	/**
	 * @see ParserBean#startLayout
	 */
	public Set<CopperElementReference> getStartLayout()
	{
		return startLayout;
	}

	/**
	 * @see ParserBean#startLayout
	 */
	public void setStartLayout(Set<CopperElementReference> startLayout)
	{
		this.startLayout = startLayout;
	}

	/**
	 * @see ParserBean#packageDecl
	 */
	public String getPackageDecl()
	{
		return packageDecl;
	}

	/**
	 * @see ParserBean#packageDecl
	 */
	public void setPackageDecl(String packageDecl)
	{
		this.packageDecl = packageDecl;
	}

	/**
	 * @see ParserBean#className
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * @see ParserBean#className
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}

	/**
	 * @see ParserBean#semanticActionAuxCode
	 */
	public String getSemanticActionAuxCode()
	{
		return semanticActionAuxCode;
	}

	/**
	 * @see ParserBean#semanticActionAuxCode
	 */
	public void setSemanticActionAuxCode(String semanticActionAuxCode)
	{
		this.semanticActionAuxCode = semanticActionAuxCode;
	}

	/**
	 * @see ParserBean#parserInitCode
	 */
	public String getParserInitCode()
	{
		return parserInitCode;
	}

	/**
	 * @see ParserBean#parserInitCode
	 */
	public void setParserInitCode(String parserInitCode)
	{
		this.parserInitCode = parserInitCode;
	}

	/**
	 * @see ParserBean#postParseCode
	 */
	public String getPostParseCode()
	{
		return postParseCode;
	}

	/**
	 * @see ParserBean#postParseCode
	 */
	public void setPostParseCode(String postParseCode)
	{
		this.postParseCode = postParseCode;
	}

	/**
	 * @see ParserBean#preambleCode
	 */
	public String getPreambleCode()
	{
		return preambleCode;
	}

	/**
	 * @see ParserBean#preambleCode
	 */
	public void setPreambleCode(String preambleCode)
	{
		this.preambleCode = preambleCode;
	}

	/**
	 * @see ParserBean#parserClassAuxCode
	 */
	public String getParserClassAuxCode()
	{
		return parserClassAuxCode;
	}

	/**
	 * @see ParserBean#parserClassAuxCode
	 */
	public void setParserClassAuxCode(String parserClassAuxCode)
	{
		this.parserClassAuxCode = parserClassAuxCode;
	}

	/**
	 * @see ParserBean#defaultProductionCode
	 */
	public String getDefaultProductionCode()
	{
		return defaultProductionCode;
	}

	/**
	 * @see ParserBean#defaultProductionCode
	 */
	public void setDefaultProductionCode(String defaultProductionCode)
	{
		this.defaultProductionCode = defaultProductionCode;
	}

	/**
	 * @see ParserBean#defaultTerminalCode
	 */
	public String getDefaultTerminalCode()
	{
		return defaultTerminalCode;
	}

	/**
	 * @see ParserBean#defaultTerminalCode
	 */
	public void setDefaultTerminalCode(String defaultTerminalCode)
	{
		this.defaultTerminalCode = defaultTerminalCode;
	}
	
	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitParserBean(this);
	}
}
