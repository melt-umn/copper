package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

/**
 * Represents the associativity of an operator. Operator associativity
 * is used to resolve shift-reduce conflicts between a terminal <code>t</code>
 * and a production with <code>t</code> as an operator.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public enum OperatorAssociativity
{
	/**
	 * Operator has no associativity setting. Shift-reduce conflicts on the terminal
	 * cannot be resolved by associativity.
	 */ 
	NONE,
	/**
	 * Operator is left-associative. Shift-reduce conflicts on the terminal
	 * will be resolved in favor of reduction (e.g., <code>(1 + 2) + 3</code>).
	 */
	LEFT,
	/** 
	 * Operator is right-associative. Shift-reduce conflicts on the terminal
	 * will be resolved in favor of shifting (e.g., <code>1 + (2 + 3)</code>).
	 */
	RIGHT,
	/**
	 * Operator is non-associative. Shift-reduce conflicts on the terminal
	 * will be resolved by removing both actions, causing the parser to fail
	 * with a syntax error if the operator is used in such a way that
	 * an associativity rule must be used to disambiguate.
	 */
	NONASSOC
}
