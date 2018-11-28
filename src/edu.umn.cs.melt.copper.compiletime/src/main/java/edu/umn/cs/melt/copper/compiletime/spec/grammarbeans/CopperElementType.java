package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans;

/**
 * The possible types of grammar objects.
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 *
 */
public enum CopperElementType
{
	TERMINAL,
	NON_TERMINAL,
	PRODUCTION,
	DISAMBIGUATION_FUNCTION,
	TERMINAL_CLASS,
	OPERATOR_CLASS,
	PARSER_ATTRIBUTE,
	GRAMMAR,
	EXTENSION_GRAMMAR,
	PARSER,
	EXTENDED_PARSER,
	SPECIAL
}
