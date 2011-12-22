package edu.umn.cs.melt.copper.runtime.io;

import java.io.IOException;
import java.io.Reader;

/**
 * A "sliding window" scanner buffer to hold the portion of the parser's
 * input presently being used (usually one token).
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public abstract class ScannerBuffer
{
	/**
	 * Constructs a new scanner buffer. 
	 * @param reader The parser's input.
	 * @return An instance of ScannerBuffer with the sliding window set at the reader's present position
	 * with a size of 0. 
	 */
	public static ScannerBuffer instantiate(Reader reader)
	{
		return new QScannerBuffer(reader);
	}

	/** The character used to indicate "end of file." */
	public static char EOFIndicator = Character.MIN_VALUE;

	/**
	 * Gets a character at a given position in the input.
	 * @param pos An input position.
	 * @return The character at that position, or EOFIndicator if end of file has been reached.
	 * @throws IOException If an error occurred reading input.
	 */
	public abstract char charAt(long pos) throws IOException;

	/**
	 * Advances the beginning of the buffer to a given position.
	 * Once the buffer be advanced past a point, a character at
	 * that point may not be obtained.
	 * @param newBufferBegin The new beginning point of the buffer.
	 */
	public abstract void advanceBufferTo(long newBufferBegin) throws IOException;

	/**
	 * Reads a string from the buffer.
	 * @param begin The index of the first character in the string.
	 * @param end The index of the first character after the string.
	 * @return The string representation of the characters between
	 * the labels.
	 * @throws IOException If an error occurred reading input.
	 */
	public abstract String readStringFromBuffer(long begin,long end) throws IOException;

}