package edu.umn.cs.melt.copper.runtime.io;

import java.io.IOException;
import java.io.Reader;

public class SlidingWindowScannerBuffer extends ScannerBuffer
{
	// TODO: Gather statistics on how this is used to try and make it more efficient.
	// TODO: Find out why a new string is being built at every character position in the input. 
	private static final int defaultInitialCapacity = 512;
	private Reader reader;
	private long bufferBegin,bufferEnd;
	private char[] circleBuffer;
	private int head,tail;
	private boolean eofReached;
	
	protected SlidingWindowScannerBuffer(Reader reader)
	{
		this.reader = reader;
		head = 0;
		tail = 0;
		circleBuffer = new char[defaultInitialCapacity];
		bufferBegin = 0;
		bufferEnd = 0;
		eofReached = false;
	}

	@Override
	public char charAt(long pos)
	throws IOException
	{
		// DEBUG-X-BEGIN
		//System.out.println(bufferBegin + " " + bufferEnd + " " + head + " " + tail + " " + size());
		//System.err.println("charAt(" + pos + ")");
		// DEBUG-X-END
		// None of the parse engines ever cause this exception to be thrown, so this line has been removed for efficiency.
		//if(pos.isBefore(bufferBegin)) throw new IOException("Buffer has passed position " + pos);
		if(pos >= bufferEnd) readNewChars((int)(pos - bufferEnd) + 1);
		if(pos >= bufferEnd) return EOFIndicator;
		return circleBuffer[(head + (int)(pos - bufferBegin)) % circleBuffer.length];
	}

	@Override
	public void advanceBufferTo(long newBufferBegin)
	throws IOException
	{
		// DEBUG-X-BEGIN
		//System.err.println("advanceBufferTo(" + newBufferBegin + ")");
		// DEBUG-X-END
		head = (head + (int)(newBufferBegin - bufferBegin)) % circleBuffer.length;
		bufferBegin = newBufferBegin;
		if(!eofReached && size() < (circleBuffer.length / 2))
		{
			readNewChars(circleBuffer.length - size() - 1);
		}
	}

	private void readNewChars(int additionLength)
	throws IOException
	{
		// DEBUG-X-BEGIN
		//System.err.println("readUpTo(" + additionLength + ")");
		// DEBUG-X-END
		if(additionLength == 0) return;
		int originalLength = size();
		if(originalLength + additionLength >= circleBuffer.length)
		{
			char[] newCircleBuffer = readBuffer(head,tail,Math.max(circleBuffer.length * 2,circleBuffer.length + additionLength));
			head = 0;
			tail = originalLength;
			circleBuffer = newCircleBuffer;
		}
		int readLength = 0;
		if(tail + additionLength > circleBuffer.length)
		{
			readLength = reader.read(circleBuffer,tail,circleBuffer.length - tail);
			if(readLength == circleBuffer.length - tail) readLength += reader.read(circleBuffer,0,additionLength - (circleBuffer.length - tail));
		}
		else
		{
			readLength = reader.read(circleBuffer,tail,additionLength);
		}
		if(readLength < additionLength) eofReached = true;
		if(readLength > 0)
		{
			bufferEnd += readLength;
			tail = (tail + readLength) % circleBuffer.length;
		}
	}
	
	private char[] readBuffer(int head,int tail,int length)
	{
		char[] arrayRep = new char[length];
		if(tail > head)
		{
			System.arraycopy(circleBuffer,head,arrayRep,0,tail - head);
		}
		else if(tail < head)
		{
			System.arraycopy(circleBuffer,head,arrayRep,0,circleBuffer.length - head);
			System.arraycopy(circleBuffer,0,arrayRep,circleBuffer.length - head,tail);
		}
		return arrayRep;
	}

	@Override
	public String readStringFromBuffer(long begin, long end)
	throws IOException
	{
		// DEBUG-X-BEGIN
		//System.err.println("readStringFromBuffer(" + begin + "," + end + ")");
		// DEBUG-X-END
		// None of the parse engines ever read a string outside the buffer boundary,
		// so this line has been removed for efficiency.
		//if(begin.isBefore(bufferBegin) || end.isAfter(bufferEnd)) return null;
		int length = (int)(end - begin);
		int head = (this.head + (int)(begin - bufferBegin)) % circleBuffer.length;
		int tail = (this.head + (int)(end - bufferBegin)) % circleBuffer.length;
		char[] arrayRep = readBuffer(head,tail,length);
		return new String(arrayRep);
	}
	
	private int size()
	{
		return ((tail - head) + circleBuffer.length) % circleBuffer.length;
	}

    /*private Pair<Integer, Integer> size()
	{
		return new Pair<Integer,Integer>(((tail - head) + circleBuffer.length) % circleBuffer.length,bufferEnd.diff(bufferBegin));
	}*/
}
