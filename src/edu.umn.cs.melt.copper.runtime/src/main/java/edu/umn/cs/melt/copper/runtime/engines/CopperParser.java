package edu.umn.cs.melt.copper.runtime.engines;

import java.io.IOException;
import java.io.Reader;

/**
 * This is an interface for Copper parser classes, specifying the methods that
 * are called to run them.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 * @param <ROOT> The type of the root of a parse tree produced by the parser.
 * @param <EXCEPT> The type of the syntax-error exceptions thrown by the parser.
 */
public interface CopperParser<ROOT,EXCEPT extends Exception>
{
	/**
	 * Runs the parser on a given input, which is taken to be standard input.
	 * @param input The reader from which to read the parser's input.
	 * @return The synthesized attribute of the root node of the input's derivative parse tree.
	 * @throws IOException If an I/O error occurs.
	 * @throws EXCEPT If a parse error occurs.
	 */
	public ROOT parse(Reader input)
        throws IOException,EXCEPT;

	/**
	 * Runs the parser on input provided in the form of a string, with default input labeling.
	 * @param text Contains the text to parse.
	 * @return The synthesized attribute of the root node of the input's derivative parse tree.
	 * @throws IOException If an I/O error occurs.
	 * @throws EXCEPT If a parse error occurs.
	 */
	public ROOT parse(String text)
    throws IOException,EXCEPT;

	/**
	 * Runs the parser on a given input, with custom input labeling.
	 * @see #parse(java.io.Reader,java.lang.String)
	 * @param input The reader from which to read the parser's input.
	 * @param inputName The label attached to the reader's data (filename or similar).
	 * @return The synthesized attribute of the root node of the input's derivative parse tree.
	 * @throws IOException If an I/O error occurs.
	 * @throws EXCEPT If a parse error occurs.
	 */
	public ROOT parse(Reader input,String inputName)
        throws IOException,EXCEPT;

	/**
	 * Runs the parser on input provided in the form of a string, with custom input labeling.
	 * @param text Contains the text to parse.
	 * @return The synthesized attribute of the root node of the input's derivative parse tree.
	 * @throws IOException If an I/O error occurs.
	 * @throws EXCEPT If a parse error occurs.
	 */
	public ROOT parse(String text,String inputName)
	    throws IOException,EXCEPT;
}
