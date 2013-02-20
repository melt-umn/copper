package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

import java.util.HashSet;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents a terminal.
 * 
 * The field <code>regex</code> must be set to a non-null value
 * before a terminal is passed to the compiler.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class Terminal extends GrammarSymbol
{
	/** The regex represented by this terminal. */
	protected Regex regex;
	/**
	 * The terminal's operator precedence class. Shift-reduce conflicts can
	 * only be resolved on operators within the same precedence class.
	 * If this is not set, the terminal will be placed into an anonymous
	 * "default" class.
	 */
	protected CopperElementReference operatorClass;
	/**
	 * The terminal's operator precedence.
	 */
	protected Integer operatorPrecedence;
	/** The terminal's operator associativity. */
	protected OperatorAssociativity operatorAssociativity;
	/** Code specifying a semantic action for this terminal. */
	protected String code;
	
	/** Terminal classes to which this terminal belongs. */
	protected Set<CopperElementReference> terminalClasses;
	/** Terminals and/or terminal classes that take precedence over this terminal. */ 
	protected Set<CopperElementReference> submitList;
	/** Terminals and/or terminal classes over which this terminal takes lexical precedence. */
	protected Set<CopperElementReference> dominateList;
	
	/** The terminal's transparent prefix. */
	protected CopperElementReference prefix;
	
	public Terminal()
	{
		super(CopperElementType.TERMINAL);
		regex = null;
		operatorClass = null;
		operatorPrecedence = null;
		operatorAssociativity = null;
		code = null;
		terminalClasses = new HashSet<CopperElementReference>();
		submitList = new HashSet<CopperElementReference>();
		dominateList = new HashSet<CopperElementReference>();
		prefix = null;
	}
	
	@Override
	/**
	 * @see CopperASTBean#isComplete()
	 */
	public boolean isComplete()
	{
		return super.isComplete() && (regex != null);
	}

	@Override
	/**
	 * @see CopperASTBean#whatIsMissing()
	 */
	public Set<String> whatIsMissing()
	{
		Set<String> rv = super.whatIsMissing();
		if(regex == null) rv.add("regex");
		return rv;
	}

	/**
	 * @see Terminal#regex
	 */
	public Regex getRegex()
	{
		return regex;
	}

	/**
	 * @see Terminal#regex
	 */
	public void setRegex(Regex regex)
	{
		this.regex = regex;
	}

	/**
	 * @see Terminal#operatorClass
	 */
	public CopperElementReference getOperatorClass()
	{
		return operatorClass;
	}

	/**
	 * @see Terminal#operatorClass
	 */
	public void setOperatorClass(CopperElementReference operatorClass)
	{
		this.operatorClass = operatorClass;
	}

	/**
	 * @see Terminal#operatorPrecedence
	 */
	public Integer getOperatorPrecedence()
	{
		return operatorPrecedence;
	}

	/**
	 * @see Terminal#operatorPrecedence
	 */
	public void setOperatorPrecedence(Integer operatorPrecedence)
	{
		this.operatorPrecedence = operatorPrecedence;
	}

	/**
	 * @see Terminal#operatorAssociativity
	 */
	public OperatorAssociativity getOperatorAssociativity()
	{
		return operatorAssociativity;
	}

	/**
	 * @see Terminal#operatorAssociativity
	 */
	public void setOperatorAssociativity(OperatorAssociativity operatorAssociativity)
	{
		this.operatorAssociativity = operatorAssociativity;
	}

	/**
	 * @see Terminal#code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @see Terminal#code
	 */
	public void setCode(String code)
	{
		this.code = code;
	}

	/**
	 * @see Terminal#terminalClasses
	 */
	public Set<CopperElementReference> getTerminalClasses()
	{
		return terminalClasses;
	}
	
	/**
	 * @see Terminal#terminalClasses
	 */
	public boolean addTerminalClass(CopperElementReference terminalClass)
	{
		return terminalClasses.add(terminalClass);
	}

	/**
	 * @see Terminal#terminalClasses
	 */
	public void setTerminalClasses(Set<CopperElementReference> terminalClasses)
	{
		this.terminalClasses = terminalClasses;
	}

	/**
	 * @see Terminal#submitList
	 */
	public Set<CopperElementReference> getSubmitList()
	{
		return submitList;
	}

	/**
	 * @see Terminal#submitList
	 */
	public boolean addSubmitsTo(CopperElementReference t)
	{
		return submitList.add(t);
	}

	/**
	 * @see Terminal#submitList
	 */
	public void setSubmitList(Set<CopperElementReference> submitList)
	{
		this.submitList = submitList;
	}

	/**
	 * @see Terminal#dominateList
	 */
	public Set<CopperElementReference> getDominateList()
	{
		return dominateList;
	}

	/**
	 * @see Terminal#dominateList
	 */
	public boolean addDominates(CopperElementReference t)
	{
		return dominateList.add(t);
	}

	/**
	 * @see Terminal#dominateList
	 */
	public void setDominateList(Set<CopperElementReference> dominateList)
	{
		this.dominateList = dominateList;
	}
	
	/**
	 * @see Terminal#prefix
	 */
	public CopperElementReference getPrefix()
	{
		return prefix;
	}

	/**
	 * @see Terminal#prefix
	 */
	public void setPrefix(CopperElementReference prefix)
	{
		this.prefix = prefix;
	}

	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitTerminal(this);
	}
}
