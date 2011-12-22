package edu.umn.cs.melt.copper.runtime.engines.moded.semantics;

import java.io.IOException;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.runtime.engines.moded.scanner.ModedMatchData;
import edu.umn.cs.melt.copper.runtime.engines.semantics.SemanticActionContainer;
import edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.Location;

public abstract class ModedSemanticActionContainer<EXCEPT extends Exception> implements SemanticActionContainer<ModedMatchData,EXCEPT>
{
	protected InputPosition _pos;
	protected Object[] _children;
	protected int _prod;
	protected ModedMatchData _terminal;
	protected SpecialParserAttributes _specialAttributes;

	public Location getStartRealLocation()
	{
		return _terminal.precedingPos;
	}
	
	public Location getEndRealLocation()
	{
		return _terminal.followingPos;
	}

	/**
	 * Concatenates two strings.
	 * @param x The left string.
	 * @param y The right string.
	 * @return <CODE>x + y</CODE>.
	 */
	public static String strcat(String x,String y) { return x + y; }
	
	/**
	 * Concatenates two lists.
	 * @param <A> The element type of the lists.
	 * @param x The left list.
	 * @param y The right list.
	 * @return <CODE>x +++ y</CODE> (shallow-copied).
	 */
	public static <A> LinkedList<A> listcat(LinkedList<A> x,LinkedList<A> y)
	{
		LinkedList<A> rv = new LinkedList<A>(x);
		rv.addAll(y);
		return rv;
	}
	
	/**
	 * Creates a new list from given elements.
	 * @param <A> The type of the elements.
	 * @param elements The elements.
	 * @return The list (array shallow-copied).
	 */
	public static <A> LinkedList<A> newList(A... elements)
	{
		LinkedList<A> rv = new LinkedList<A>();
		for(A element : elements) rv.addLast(element);
		return rv;
	}
	
	/**
	 * Cons'es an element to the front of a list.
	 * @param <A> The type of the elements.
	 * @param car The element to cons.
	 * @param cdr The list to cons.
	 * @return The cons'ed list (shallow-copied).
	 */
	public static <A> LinkedList<A> listCons(A car,LinkedList<A> cdr)
	{
		LinkedList<A> rv = new LinkedList<A>(cdr);
		rv.addFirst(car);
		return rv;
	}
	
	
	public static <A> Object listCar(LinkedList<A> list)
	{
		return list.getFirst();
	}
	
	public static <A> LinkedList<A> listCdr(LinkedList<A> list)
	{
		LinkedList<A> rv = new LinkedList<A>(list);
		rv.removeFirst();
		return rv;
	}
	
	public static <A> LinkedList<A> filterOutOfList(A elem,LinkedList<A> orig)
	{
		LinkedList<A> rv = new LinkedList<A>();
		for(A a : orig)
		{
			if((elem != null && a == null) ||
			   (elem == null && a != null) ||
			   (elem != null && a != null && !a.equals(elem)))
			{
				rv.add(a);
			}
		}
		return rv;
	}
	
	public static <A> LinkedList<A> copyList(LinkedList<A> orig)
	{
		return new LinkedList<A>(orig);
	}

	public VirtualLocation getVirtualLocation()
	{
		return _specialAttributes.virtualLocation;
	}
	
	public void setLatchLocation(boolean latchLocation)
	{
		_specialAttributes.latchLocation = latchLocation;
	}
	
	public SpecialParserAttributes getSpecialAttributes()
	{
		return _specialAttributes;
	}
	
	public abstract void runDefaultTermAction()
	throws IOException,EXCEPT;
	
	public abstract void runDefaultProdAction()
	throws IOException,EXCEPT;

	/**
	 * Determines if a list contain a given element.
	 * @param <A> The type of the elements.
	 * @param element The element for which to search.
	 * @param list The list to search.
	 * @return <CODE>true</CODE> iff <CODE>element</CODE> appear in <CODE>list</CODE>.
	 */
	public static <A> boolean listContains(A element,LinkedList<A> list)
	{
		return list.contains(element);
	}
	
	public abstract void error(InputPosition pos,String message)
	throws EXCEPT;
	
	/**
	 * Runs the initialization code for each parser attribute. 
	 * @throws IOException When the code requires.
	 * @throws EXCEPT When the code requires.
	 */
	public abstract void runInit()
	throws IOException,EXCEPT;
	
	@Override
	public abstract Object runSemanticAction(InputPosition _pos,Object[] _children,int _prod)
	throws IOException,EXCEPT;
	
	/**
	 * Runs the semantic action code for a shift action.
	 * @param _pos The input position before the shift.
	 * @param _terminal The terminal being shifted.
	 * @throws IOException When the code requires.
	 * @throws EXCEPT When the code requires.
	 */
	public abstract Object runSemanticAction(InputPosition _pos,ModedMatchData _terminal)
	throws IOException,EXCEPT;
	
	/**
	 * Runs the semantic action code for a disambiguation by group.
	 * @param _pos The input position at the ambiguous scan.
	 * @param matches The matches returned by the scanner.
	 * @return The disambiguated match, or <CODE>null</CODE> if no disambiguation was available.
	 * @throws IOException When the code requires.
	 * @throws EXCEPT When the code requires.
	 */
	public abstract int runDisambiguationAction(InputPosition _pos,ModedMatchData matches)
	throws IOException,EXCEPT;
}
