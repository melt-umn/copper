package edu.umn.cs.melt.copper.compiletime.logging;

/**
 * Types of log messages.
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 * @author Kelton O'Brien &lt;<a href="mailto:obri0707@umn.edu">obri0707@umn.edu</a>&gt;
 */
public abstract class CompilerLogMessageType
{
	/**
	 * Any errors that do not fit in another category.
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.GenericLocatedMessage
	 */
	public static final int GENERIC = 0;
	/**
	 * Interface errors.
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.InterfaceErrorMessage
	 */
	public static final int INTERFACE_ERROR = 1;
	/**
	 * Errors reading an XML schema or validating a file according to such a schema.
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.SchemaErrorMessage
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
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.ParseTableConflictMessage
	 */
	public static final int PARSE_TABLE_CONFLICT = 5;
	/**
	 * Lexical ambiguities.
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.LexicalAmbiguityMessage
	 */
	public static final int LEXICAL_AMBIGUITY = 6;
	/**
	 * "Useless nonterminal" warnings.
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.UselessNonterminalMessage
	 */
	public static final int USELESS_NONTERMINAL = 7;
	/**
	 * "Nonterminal X has no terminal derivations" errors.
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.NonterminalNonterminalMessage
	 */
	public static final int NONTERMINAL_NONTERMINAL = 8;
	/**
	 * Malformed regex errors. 
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.MalformedRegexMessage
	 */
	public static final int MALFORMED_REGEX = 9;
	/**
	 * Errors on cyclic precedence relations (e.g., A and B are on each other's submit lists).
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.CyclicPrecedenceRelationMessage
	 */
	public static final int CYCLIC_PRECEDENCE = 10;
	/**
	 * Errors on conflicting disambiguation functions (A and B have the same members, or A and B
	 * apply to subsets and are non-disjoint).
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.DuplicateDisambiguationFunctionMessage
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.OverlappingDisambiguationFunctionMessage
	 */
	public static final int DISAMBIGUATION_FUNCTION_CONFLICT = 11;
	/**
	 * "Lookahead spillage" errors from the modular determinism analysis, raised when an
	 * extension introduces new lookahead.
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.LookaheadSpillageMessage
	 */
	public static final int LOOKAHEAD_SPILLAGE = 12;
	/**
	 * "Follow spillage" errors from the modular determinism analysis, raised when an
	 * extension introduces new members to the follow set of a host nonterminal.
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.FollowSpillageMessage
	 */
	public static final int FOLLOW_SPILLAGE = 13;
	/**
	 * "Non-IL-subset condition" errors from the modular determinism analysis, raised when
	 * an extension introduces a state that may conflict with one introduced by another extension.
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.NonILSubsetMessage
	 */
	public static final int NON_IL_SUBSET = 14;
	/**
	 * The final report message, containing grammar and parse table metrics, as well as the number
	 * of parse table conflicts and lexical ambiguities found and resolved.
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.FinalReportMessage
	 */
	public static final int FINAL_REPORT = 15;

	/**
	 * Messages to report a counterexamples for ambiguities found in the input grammar.
	 * @see edu.umn.cs.melt.copper.compiletime.logging.messages.CounterexampleMessage
	 */
	public static final int COUNTEREXAMPLE = 16;



}
