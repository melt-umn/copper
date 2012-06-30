package edu.umn.cs.melt.copper.compiletime.loggingnew;

/**
 * Types of log messages.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public abstract class CompilerLogMessageType
{
	/**
	 * Any errors that do not fit in another category.
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.GenericMessage
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.GenericLocatedMessage
	 */
	public static final int GENERIC = 0;
	/**
	 * Interface errors.
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.InterfaceErrorMessage
	 */
	public static final int INTERFACE_ERROR = 1;
	/**
	 * Errors reading an XML schema or validating a file according to such a schema.
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.SchemaErrorMessage
	 */
	public static final int SCHEMA_ERROR = 2;
	/**
	 * Errors parsing a grammar.
	 */
	public static final int GRAMMAR_SYNTAX_ERROR = 3;
	/**
	 * Errors raised on grammars that are not well-formed.
	 */
	public static final int GRAMMAR_SEMANTIC_ERROR = 4;
	/**
	 * Parse table conflicts.
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.ParseTableConflictMessage
	 */
	public static final int PARSE_TABLE_CONFLICT = 5;
	/**
	 * Lexical ambiguities.
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.LexicalAmbiguityMessage
	 */
	public static final int LEXICAL_AMBIGUITY = 6;
	/**
	 * "Useless nonterminal" warnings.
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.UselessNonterminalMessage
	 */
	public static final int USELESS_NONTERMINAL = 7;
	/**
	 * "Nonterminal X has no terminal derivations" errors.
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.NonterminalNonterminalMessage
	 */
	public static final int NONTERMINAL_NONTERMINAL = 8;
	/**
	 * Errors on cyclic precedence relations (e.g., A and B are on each other's submit lists).
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.CyclicPrecedenceRelationMessage
	 */
	public static final int CYCLIC_PRECEDENCE = 9;
	/**
	 * "Lookahead spillage" errors from the modular determinism analysis, raised when an
	 * extension introduces new lookahead.
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.LookaheadSpillageMessage
	 */
	public static final int LOOKAHEAD_SPILLAGE = 10;
	/**
	 * "Follow spillage" errors from the modular determinism analysis, raised when an
	 * extension introduces new members to the follow set of a host nonterminal.
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.FollowSpillageMessage
	 */
	public static final int FOLLOW_SPILLAGE = 11;
	/**
	 * "Non-IL-subset condition" errors from the modular determinism analysis, raised when
	 * an extension introduces a state that may conflict with one introduced by another extension.
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.NonILSubsetMessage
	 */
	public static final int NON_IL_SUBSET = 12;
	/**
	 * The final report message, containing grammar and parse table metrics, as well as the number
	 * of parse table conflicts and lexical ambiguities found and resolved.
	 * @see edu.umn.cs.melt.copper.compiletime.loggingnew.messages.FinalReportMessage
	 */
	public static final int FINAL_REPORT = 13;
}