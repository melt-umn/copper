package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

/**
 * The possible types of grammar objects.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
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
