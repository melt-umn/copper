package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.util.HashSet;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents a terminal.
 * 
 * The field <code>regex</code> must be set to a non-null value
 * before a terminal is passed to the compiler.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class TerminalBean extends GrammarSymbolBean
{
	/** The regex represented by this terminal. */
	protected RegexBean regex;
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
	
	public TerminalBean()
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
	 * @see TerminalBean#regex
	 */
	public RegexBean getRegex()
	{
		return regex;
	}

	/**
	 * @see TerminalBean#regex
	 */
	public void setRegex(RegexBean regex)
	{
		this.regex = regex;
	}

	/**
	 * @see TerminalBean#operatorClass
	 */
	public CopperElementReference getOperatorClass()
	{
		return operatorClass;
	}

	/**
	 * @see TerminalBean#operatorClass
	 */
	public void setOperatorClass(CopperElementReference operatorClass)
	{
		this.operatorClass = operatorClass;
	}

	/**
	 * @see TerminalBean#operatorPrecedence
	 */
	public Integer getOperatorPrecedence()
	{
		return operatorPrecedence;
	}

	/**
	 * @see TerminalBean#operatorPrecedence
	 */
	public void setOperatorPrecedence(Integer operatorPrecedence)
	{
		this.operatorPrecedence = operatorPrecedence;
	}

	/**
	 * @see TerminalBean#operatorAssociativity
	 */
	public OperatorAssociativity getOperatorAssociativity()
	{
		return operatorAssociativity;
	}

	/**
	 * @see TerminalBean#operatorAssociativity
	 */
	public void setOperatorAssociativity(OperatorAssociativity operatorAssociativity)
	{
		this.operatorAssociativity = operatorAssociativity;
	}

	/**
	 * @see TerminalBean#code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @see TerminalBean#code
	 */
	public void setCode(String code)
	{
		this.code = code;
	}

	/**
	 * @see TerminalBean#terminalClasses
	 */
	public Set<CopperElementReference> getTerminalClasses()
	{
		return terminalClasses;
	}
	
	/**
	 * @see TerminalBean#terminalClasses
	 */
	public boolean addTerminalClass(CopperElementReference terminalClass)
	{
		return terminalClasses.add(terminalClass);
	}

	/**
	 * @see TerminalBean#terminalClasses
	 */
	public void setTerminalClasses(Set<CopperElementReference> terminalClasses)
	{
		this.terminalClasses = terminalClasses;
	}

	/**
	 * @see TerminalBean#submitList
	 */
	public Set<CopperElementReference> getSubmitList()
	{
		return submitList;
	}

	/**
	 * @see TerminalBean#submitList
	 */
	public boolean addSubmitsTo(CopperElementReference t)
	{
		return submitList.add(t);
	}

	/**
	 * @see TerminalBean#submitList
	 */
	public void setSubmitList(Set<CopperElementReference> submitList)
	{
		this.submitList = submitList;
	}

	/**
	 * @see TerminalBean#dominateList
	 */
	public Set<CopperElementReference> getDominateList()
	{
		return dominateList;
	}

	/**
	 * @see TerminalBean#dominateList
	 */
	public boolean addDominates(CopperElementReference t)
	{
		return dominateList.add(t);
	}

	/**
	 * @see TerminalBean#dominateList
	 */
	public void setDominateList(Set<CopperElementReference> dominateList)
	{
		this.dominateList = dominateList;
	}
	
	/**
	 * @see TerminalBean#prefix
	 */
	public CopperElementReference getPrefix()
	{
		return prefix;
	}

	/**
	 * @see TerminalBean#prefix
	 */
	public void setPrefix(CopperElementReference prefix)
	{
		this.prefix = prefix;
	}

	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitTerminalBean(this);
	}
}
