package edu.umn.cs.melt.copper.runtime.io;

/**
 * A "double-ended queue" that supports four operations
 * (offer, poll, indexed access, and element count)
 * each in amortized constant time.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
class CircleBuffer
{
	private static final double defaultTrimFactor = 1.1;
	private static final int defaultInitialCapacity = 20;
	private int head,tail;
	private char[] data;

	/**
	 * Creates a new instance of CircleBuffer with the default initial capacity of 20.
	 *
	 */
	public CircleBuffer()
	{
		this(defaultInitialCapacity);
	}
	
	/**
	 * Creates a new instance of CircleBuffer with a custom initial capacity.
	 * @param initialCapacity The initial capacity.
	 */
	public CircleBuffer(int initialCapacity)
	{
		data = new char[initialCapacity];
		head = 0;
		tail = 0;
	}

	/**
	 * Lops a certain number of elements off the front of the queue.
	 * @param n The number of elements to lop.
	 */
	public void poll(int n)
	{
		if(n > size()) throw new ArrayIndexOutOfBoundsException("Not " + n + " elements in this buffer");
		head = ((head + n) % data.length);
		if(size() <= (data.length / 2))
		{
			int newSize = Math.max((int)(size() * defaultTrimFactor) + 1,defaultInitialCapacity);
			char[] newData = new char[newSize];
			for(int i = 0;i < size();i++)
			{
				newData[i] = get(i);
			}
			tail = size();
			head = 0;
			data = newData;
		}
	}
	
	/**
	 * Adds an element to the back of the queue.
	 * @param c The element to add.
	 */
	public void offer(char c)
	{
		if(size() + 1 >= data.length)
		{
			char[] newData = new char[data.length * 2];
			for(int i = 0;i < size();i++)
			{
				newData[i] = get(i);
			}
			tail = size();
			head = 0;
			data = newData;
		}
		data[tail] = c;
		tail = (tail + 1) % data.length;
	}

	/**
	 * Gets an element by queue position.
	 * @param index The position of the element.
	 * @return The element at position <code>index</code>.
	 */
	public char get(int index)
	{
		if(index < 0 || index >= size())
		{
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return data[(head + index) % data.length];
	}
	
	/**
	 * 
	 * @return The number of elements in this CircleBuffer.
	 */
	public int size()
	{
		return ((tail - head) + data.length) % data.length;
		
	}
	
	public boolean isEmpty()
	{
		return (size() == 0);
	}
	
	public String toString()
	{
		String rv = "[";
		for(int i = 0;i < size();i++)
		{
			rv += get(i);
			if(i < size() - 1) rv += ",";
		}
		rv += "]";
		return rv;
	}
}
