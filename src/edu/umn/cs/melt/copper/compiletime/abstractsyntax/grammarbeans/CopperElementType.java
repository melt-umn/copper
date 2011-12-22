package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

/**
 * The possible types of grammar objects.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public enum CopperElementType
{
	PARSER,
	GRAMMAR,
	EXTENSION_GRAMMAR,
	TERMINAL,
	NON_TERMINAL,
	PRODUCTION,
	DISAMBIGUATION_FUNCTION,
	PARSER_ATTRIBUTE,
	TERMINAL_CLASS
}
