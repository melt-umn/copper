package edu.umn.cs.melt.copper.runtime.io;

import java.io.IOException;
import java.io.Reader;

/**
 * Represents a buffer capable of holding as few input characters
 * as a context-aware scanner for a GLR parser requires, supporting constant
 * time access to a character already read in.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
class QScannerBuffer extends ScannerBuffer
{
	// The input reader.
	private Reader reader;
	// The CircleBuffer used to hold a section of input.
	private CircleBuffer buffer;
	// Markers indicating what section of the input the buffer holds.
	private long bufferBegin,bufferEnd;

	/**
	 * Creates a new instance of QScannerBuffer.
	 * @param reader The input. Scanning will start at the position
	 * the reader is in when sent to the constructor.
	 */
	protected QScannerBuffer(Reader reader)
	{
		this.reader = reader;
		buffer = new CircleBuffer();
		bufferBegin = 0;
		bufferEnd = 0;
	}

	/**
	 * Reads a character from the input without changing the buffer.
	 * @return The character read, or EOFIndicator if end of file were reached.
	 * @throws IOException If an error occurred reading input.
	 */
	private char readChar()
	throws IOException
	{
		int charRead = reader.read();
		if(charRead == -1) return EOFIndicator;
		else return (char) charRead;
	}
	
	/**
	 * Reads a character from the input and adds it to the buffer.
	 * @throws IOException If an error occurred reading input.
	 */
	private void addChar()
	throws IOException
	{
		if(buffer.isEmpty() || buffer.get(buffer.size() - 1) != EOFIndicator)
		{
			char charRead = readChar();
			buffer.offer(charRead);
			bufferEnd++;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.umn.cs.melt.copper.runtime.io.ScannerBuffer#charAt(edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition)
	 */
	@Override
	public char charAt(long pos)
	throws IOException
	{
		if(pos < bufferBegin) throw new IOException("Buffer has passed position " + pos);
		if(pos >= bufferEnd) readUpTo(pos);
		if(pos >= bufferEnd) return EOFIndicator;
		return buffer.get((int)(pos - bufferBegin));
	}

	/* (non-Javadoc)
	 * @see edu.umn.cs.melt.copper.runtime.io.ScannerBuffer#advanceBufferTo(edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition)
	 */
	@Override
	public void advanceBufferTo(long newBufferBegin)
	{
		if(bufferBegin < newBufferBegin &&
		   (newBufferBegin - bufferBegin) <= buffer.size())
		{
			buffer.poll((int)(newBufferBegin - bufferBegin));
			bufferBegin = newBufferBegin;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.umn.cs.melt.copper.runtime.io.ScannerBuffer#readUpTo(edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition)
	 */
	private void readUpTo(long newBufferEnd)
	throws IOException
	{
		while(bufferEnd <= newBufferEnd) addChar();
	}

	/* (non-Javadoc)
	 * @see edu.umn.cs.melt.copper.runtime.io.ScannerBuffer#readStringFromBuffer(edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition, edu.umn.cs.melt.copper.runtime.auxiliary.InputPosition)
	 */
	@Override
	public String readStringFromBuffer(long begin,long end)
	throws IOException
	{
		if(begin < bufferBegin || end > bufferEnd) return null;
		char[] arrayRep = new char[(int)(end - begin)];
		for(long pos = begin;pos < end;pos++)
		{
			arrayRep[(int)(pos - begin)] = charAt(pos);
		}
		return new String(arrayRep);
	}
}
