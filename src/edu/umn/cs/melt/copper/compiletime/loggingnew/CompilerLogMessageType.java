package edu.umn.cs.melt.copper.compiletime.loggingnew;

public abstract class CompilerLogMessageType
{
	public static final int GENERIC = 0;
	public static final int INTERFACE_ERROR = 1;
	public static final int SCHEMA_ERROR = 2;
	public static final int GRAMMAR_ERROR = 3;
	public static final int PARSE_TABLE_CONFLICT = 4;
	public static final int LEXICAL_CONFLICT = 5;
	public static final int USELESS_NONTERMINAL = 6;
	public static final int NONTERMINAL_NONTERMINAL = 7;
	public static final int CYCLIC_PRECEDENCE = 8;
	public static final int LOOKAHEAD_SPILLAGE = 9;
	public static final int FOLLOW_SPILLAGE = 10;
	public static final int NON_IL_SUBSET = 11;
	public static final int FINAL_REPORT = 12;
}