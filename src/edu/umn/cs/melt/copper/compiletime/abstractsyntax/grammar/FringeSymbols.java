package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar;

public class FringeSymbols {

	public static final Terminal EMPTY = new Terminal("<empty string>");
	public static final Terminal EOF = new Terminal("$");
	public static final NonTerminal STARTPRIME = new NonTerminal("^");
	public static final Symbol STARTPROD_SYMBOL = Symbol.symbol("Capsule");

	public static final int PRECEDENCE_NONE = -1;

	public static final int WHITE = 0,GRAY = 1,BLACK = 2;
}
