package edu.umn.cs.melt.copper.compiletime.auxiliary;

/**
 * Interface for objects that support the operations of union and intersection
 * (nominally or otherwise).
 * The contract of <CODE>Mergable</CODE> is as follows. If
 * the call <CODE>lhs.union(rhs)</CODE> or <CODE>lhs.intersect(rhs)</CODE>
 * be made, then if <CODE>!lhs.equals(rhs)</CODE>, lhs is not changed;
 * otherwise, appropriate modifications are made to <CODE>lhs</CODE>,
 * and a boolean value is returned indicating whether or not a change
 * were made in <CODE>lhs</CODE>.
 */
public interface Mergable<E>
{
    /** Merges this Mergable object with another through the operation of "union."
     * @param rhs The object with which to merge.
     * @return <CODE>true</CODE> iff the merge produced a change in <CODE>this</CODE>.
     */    
    public boolean union(E rhs);
    /** Merges this Mergable object with another through the operation of
     * "intersection."
     * @param rhs The object with which to merge.
     * @return <CODE>true</CODE> iff the merge produced a change in <CODE>this</CODE>.
     */    
    public boolean intersect(E rhs);
}
