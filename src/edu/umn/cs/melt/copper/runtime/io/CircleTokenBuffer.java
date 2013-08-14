package edu.umn.cs.melt.copper.runtime.io;

public class CircleTokenBuffer<E>
{
	private static final double defaultTrimFactor = 1.1;
	private static final int defaultInitialCapacity = 20;
	private int head,tail;
	private Object[] data;

	/**
	 * Creates a new instance of CircleBuffer with the default initial capacity of 20.
	 *
	 */
	public CircleTokenBuffer()
	{
		this(defaultInitialCapacity);
	}
	
	/**
	 * Creates a new instance of CircleBuffer with a custom initial capacity.
	 * @param initialCapacity The initial capacity.
	 */
	public CircleTokenBuffer(int initialCapacity)
	{
		data = new Object[initialCapacity];
		head = 0;
		tail = 0;
	}

	/** 
	 * Lops one element off the front of the queue.
	 * @return The lopped element.
	 */
	public E poll()
	{
		if(isEmpty()) return null;
		E rv = get(0);
		poll(1);
		return rv;
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
			Object[] newData = new Object[newSize];
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
	public void offer(E c)
	{
		if(size() + 1 >= data.length)
		{
			Object[] newData = new Object[data.length * 2];
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
	@SuppressWarnings("unchecked")
	public E get(int index)
	{
		if(index < 0 || index >= size())
		{
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return (E) data[(head + index) % data.length];
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
