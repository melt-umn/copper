package edu.umn.cs.melt.copper.runtime.auxiliary;

/** Parametrically polymorphic class for pairs of all kinds. */
public class Pair<X,Y>
{
    private X _first;
    private Y _second;

    /** Creates a new instance of Pair.
     * @param first The first element of this Pair.
     * @param second The second element of this Pair.
     */    
    public Pair(X first,Y second)
    {
    	_first = first;
    	_second = second;
    }

    /** Gets the first element of this Pair.
     * @return The first element of this Pair.
     */    
    public X first() { return _first; }
    /** Gets the second element of this Pair.
     * @return The second element of this Pair.
     */    
    public Y second() { return _second; }

    public boolean equals(Object rhs)
    {
    	if(rhs instanceof Pair<?,?>)
    	{
    		Pair<?,?> prhs = (Pair<?,?>) rhs;
    		return (first().equals(prhs.first()) &&
		    second().equals(prhs.second()));
    	}
    	else return false;
    }

    public int hashCode()
    {
    	return toString().hashCode();
    }

    public String toString()
    {
    	return "(" + _first + "," + _second + ")";
    }
    
    public static <X,Y> Pair<X,Y> cons(X first,Y second)
    {
    	return new Pair<X,Y>(first,second);
    }
}
