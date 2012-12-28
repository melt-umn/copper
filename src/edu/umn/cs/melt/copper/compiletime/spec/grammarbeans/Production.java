package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

import java.util.ArrayList;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents a production.
 *
 * Before a production is passed to the compier, fields
 * <code>lhs</code> and <code>rhs</code> must be set to non-null values,
 * and field <code>rhsVarNames</code> must either be set to null or be equal
 * in length to <code>rhs</code>.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class Production extends GrammarElement
{
	/** The production's left-hand-side nonterminal. */
	protected CopperElementReference lhs;
	/** The production's right-hand-side symbols. */
	protected ArrayList<CopperElementReference> rhs;
	/**
	 * Variable names for the right-hand-side symbols.
	 * May be <code>null</code> or a list of the same size as <code>rhs</code>;
	 * elements are set to <code>null</code> if their corresponding symbol
	 * has no variable name. 
	 */
	protected ArrayList<String> rhsVarNames;
	/**
	 * The production's operator symbol, the precedence of which is used
	 * to resolve shift-reduce conflicts involving the production.
	 * If this is not set, the last terminal symbol on the right-hand side
	 * will be used.
	 */
	protected CopperElementReference operator;
	/**
	 * The production's precedence class. Reduce-reduce conflicts can only
	 * be resolved on productions within the same precedence class.
	 * If this is not set, the production will be placed into an anonymous
	 * "default" class.
	 * <br/>
	 * <b>N.B.:</b> This precedence class is not used when resolving <em>shift</em>-reduce conflicts.
	 * The operator class (see {@link Terminal#operatorClass}) of the production's operator
	 * (see {@link #operator}) is used for that.
	 * @see OperatorClass
	 */
	protected CopperElementReference precedenceClass;
	/** The production's precedence for resolving reduce-reduce conflicts. */
	protected Integer precedence;
	/** 
	 * The set of layout terminals (e.g., whitespace, comments) that can appear
	 * between the symbols of this production's right-hand side. If this is
	 * left as <code>null</code>, the production will inherit layout from
	 * the grammar, if grammar layout is defined.
	 * @see Grammar#grammarLayout
	 */
	protected Set<CopperElementReference> layout;
	/** Code specifying a semantic action for this production. */
	protected String code;
	
	public Production()
	{
		super(CopperElementType.PRODUCTION);
		lhs = null;
		rhs = null;
		rhsVarNames = null;
		operator = null;
		precedenceClass = null;
		precedence = null;
		layout = null;
		code = null;
	}
	
	@Override
	/**
	 * @see ProductionBean#isComplete()
	 */
	public boolean isComplete()
	{
		return super.isComplete() && (lhs != null && rhs != null && (rhsVarNames == null || rhsVarNames.size() == rhs.size()));
	}
	
	@Override
	/**
	 * @see CopperASTBean#whatIsMissing()
	 */
	public Set<String> whatIsMissing()
	{
		Set<String> rv = super.whatIsMissing();
		if(lhs == null) rv.add("lhs");
		if(rhs == null) rv.add("rhs");
		if(rhsVarNames != null && rhsVarNames.size() != rhs.size()) rv.add("rhsVarNames");
		return rv;
	}

	/**
	 * @see Production#lhs
	 */
	public CopperElementReference getLhs()
	{
		return lhs;
	}

	/**
	 * @see Production#lhs
	 */
	public void setLhs(CopperElementReference lhs)
	{
		this.lhs = lhs;
	}

	/**
	 * @see Production#rhs
	 */
	public ArrayList<CopperElementReference> getRhs()
	{
		return rhs;
	}

	/**
	 * @see Production#rhs
	 */
	public void setRhs(ArrayList<CopperElementReference> rhs)
	{
		this.rhs = rhs;
	}

	/**
	 * @see Production#rhsVarNames
	 */
	public ArrayList<String> getRhsVarNames()
	{
		return rhsVarNames;
	}

	/**
	 * @see Production#rhsVarNames
	 */
	public void setRhsVarNames(ArrayList<String> rhsVarNames)
	{
		this.rhsVarNames = rhsVarNames;
	}

	/**
	 * @see Production#operator
	 */
	public CopperElementReference getOperator()
	{
		return operator;
	}

	/**
	 * @see Production#operator
	 */
	public void setOperator(CopperElementReference operator)
	{
		this.operator = operator;
	}

	/**
	 * @see Production#precedenceClass
	 */
	public CopperElementReference getPrecedenceClass()
	{
		return precedenceClass;
	}

	/**
	 * @see Production#precedenceClass
	 */
	public void setPrecedenceClass(CopperElementReference operatorClass)
	{
		this.precedenceClass = operatorClass;
	}

	/**
	 * @see Production#precedence
	 */
	public Integer getPrecedence()
	{
		return precedence;
	}

	/**
	 * @see Production#precedence
	 */
	public void setPrecedence(Integer precedence)
	{
		this.precedence = precedence;
	}

	/**
	 * @see Production#layout
	 */
	public Set<CopperElementReference> getLayout()
	{
		return layout;
	}

	/**
	 * @see Production#layout
	 */
	public void setLayout(Set<CopperElementReference> layout)
	{
		this.layout = layout;
	}

	/**
	 * @see Production#code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @see Production#code
	 */
	public void setCode(String code)
	{
		this.code = code;
	}
	
	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitProductionBean(this);
	}
}
