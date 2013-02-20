package edu.umn.cs.melt.copper.legacy.compiletime.auxiliary;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;


/** 
 * A version of the HashSet that supports:
 * (1) <code>get()</code>ting of elements;
 * (2) merging when elements are added.
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public class DynHashSet<E extends Mergable<? super E>> implements Iterable<E>,Mergable< DynHashSet<E> >
{
    private HashSet<E> elements;
    private Hashtable<E,E> accessibleElements;
    // This is presently not visible. If set to true,
    // elements put on top of already existing elements
    // will be intersected instead of union'd.
    private boolean intersectP;

    /* (non-Javadoc)
     * Creates a new empty DynHashSet.
     *
     */
    public DynHashSet()
    {
		elements = new HashSet<E>();
		accessibleElements = new Hashtable<E,E>();
		intersectP = false;
    }
    
    /* (non-Javadoc)
     * Calls <CODE>this.put()</CODE> for each element in an
     * <CODE>Iterable</CODE> object.
     * @param coll The object to union with this DynHashSet.
     * @return <CODE>true</CODE> iff this DynHashSet was changed.
     */
    public boolean addAll(Iterable<? extends E> coll)
    {
    	boolean rv = false;
    	for(E elem : coll) rv = put(elem) || rv;
    	return rv;
    }

    /* (non-Javadoc)
     * Copies an existing DynHashSet.
     * @param hs The DynHashSet to copy.
     */
    public DynHashSet(DynHashSet<E> hs)
    {
    	elements = new HashSet<E>();
    	accessibleElements = new Hashtable<E,E>();
    	intersectP = hs.intersectP;
    	for(E element : hs.elements) put(element);
    }

    /* (non-Javadoc)
     * Puts an element in the DynHashSet; if an element that
     * <CODE>equals()</CODE> it is already present, merge the
     * new with the old.
     * @param o The element to add.
     * @return <CODE>true</CODE> iff this DynHashSet was changed.
     */
    public boolean put(E o)
    {
		if(!elements.contains(o))
		{
		    elements.add(o);
		    accessibleElements.put(o,o);
		    return true;
		}
		else
		{
		    E alreadyIn = accessibleElements.get(o);
		    elements.remove(o);
		    accessibleElements.remove(o);
		    boolean rv;
		    if(intersectP) rv = alreadyIn.intersect(o);
		    else rv = alreadyIn.union(o);
		    elements.add(alreadyIn);
		    accessibleElements.put(alreadyIn,alreadyIn);
		    return rv;
		}
    }

    /* (non-Javadoc)
     * Removes an element from the DynHashSet.
     * @param o The element to remove.
     * @return <CODE>true</CODE> iff the DynHashSet was changed.
     */
    public boolean remove(E o)
    {
		if(elements.contains(o))
		{
		    elements.remove(o);
		    accessibleElements.remove(o);
		    return true;
		}
		else return false;
    }

    /* (non-Javadoc)
     * Gets an element from the DynHashSet.
     * @param o An object that <CODE>equals()</CODE> the element to get.
     * @return The element gotten, or <CODE>null</CODE> if none were present.
     */
    public E get(E o)
    {
    	return accessibleElements.get(o);
    }

    /* (non-Javadoc)
     * Determines whether the DynHashSet contains a certain element.
     * @param o The element for which to check.
     * @return <CODE>true</CODE> iff the DynHashSet contains the element. 
     */
    public boolean contains(E o) { return elements.contains(o); }
    
    /* (non-Javadoc)
     * Determines the size of this DynHashSet.
     * @return The size.
     */
    public int size() { return elements.size(); }

    private 
    /* (non-Javadoc) The iterator for a DynHashSet. Supports remove(). */    
    class DynIterator implements Iterator<E>
    {
		//public DynHashSet<E> set;
		public Iterator<E> underIterator;
		E lastReturned;
		public DynIterator(DynHashSet<E> newSet)
		{
		    //set = newSet;
		    underIterator = elements.iterator();
		    lastReturned = null;
		}
		public boolean hasNext()
		{
		    return underIterator.hasNext();
		}
		public E next()
		throws NoSuchElementException
		{
		    lastReturned = underIterator.next();
		    return lastReturned;
		}
		public void remove()
		throws IllegalStateException
		{
		    if(lastReturned == null) throw new IllegalStateException("Invalid context");
		    accessibleElements.remove(lastReturned);
		    underIterator.remove();
		    lastReturned = null;
		}
    }
    
    public void clear()
    {
    	elements.clear();
    	accessibleElements.clear();
    }

    /* (non-Javadoc) Initializes an Iterator for this DynHashSet.
     * @return An Iterator for this DynHashSet.
     */    
    public Iterator<E> iterator() { return new DynIterator(this); }

    public boolean union(DynHashSet<E> rhs)
    {
		int oldSize = size();
		for(E element : rhs.elements) put(element);
		return (oldSize != size());
    }

    public boolean intersect(DynHashSet<E> rhs)
    {
		int oldSize = size();
		HashSet<E> newElements = new HashSet<E>();
		for(E cur : elements)
		{
		    if(rhs.contains(cur)) newElements.add(cur);
		    else accessibleElements.remove(cur);
		}
		elements = newElements;
		return (oldSize != size());
    }

    /* (non-Javadoc) Determines equality: <CODE>this.equals(o)</CODE> if <CODE>o</CODE> be a
     * DynHashSet, and if they contain the same elements.
     * @param o The DynHashSet against which to check.
     * @return true if rhs is a DynHashSet and contains the same elements as this, false
     * otherwise.
     */ 
    @SuppressWarnings("unchecked")
	public boolean equals(Object o)
    {
		DynHashSet<E> drhs = null;
		try { drhs = (DynHashSet<E>) o; }
		catch(ClassCastException ex)
		{
			System.err.println("Cast Exception");
		    return false;
		}
		return safe_equals(drhs);
    }
    
    private boolean safe_equals(DynHashSet<E> rhs)
    {
		if(size() != rhs.size()) return false;
		return elements.equals(rhs.elements);
    }

    public String toString()
    {
    	return elements.toString();
    }
}
