package edu.umn.cs.melt.copper.runtime.io;

import java.io.Serializable;

/**
 * This interface represents parsing locations (can be real or virtual).
 * @author August Schwerdfeger &lt;<a href="mailto:schw0709@umn.edu">schw0709@umn.edu</a>&gt;
 * @author Kevin Viratyosin
 *
 * Modified by Kevin to allow serialization
 */
public interface Location extends Comparable<Location>, Serializable
{
	/**
	 * The name of the file being parsed.
	 */
	public String getFileName();
	/**
	 * The line the parser has reached.
	 */
	public int getLine();
	/**
	 * The column the parser has reached.
	 */
	public int getColumn();
	/**
	 * The position in the file (makes sense only for real locations -- virtual locations should return a dummy value). 
	 */
	public long getPos();
}
