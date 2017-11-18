package edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.semantics;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner.QScannerMatchData;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetree.plain.ParseTreeProdNode;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetree.plain.ParseTreeTermNode;
import edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.Location;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;


/* (non-Javadoc)
 * Functions as a container for semantic actions --- modifications to
 * parser attributes and such.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class SemanticActionContainer implements edu.umn.cs.melt.copper.runtime.engines.semantics.SemanticActionContainer<QScannerMatchData,CopperException>
{
	protected InputPosition _pos;
	protected Object[] _children;
	protected QScannerMatchData _terminal;
	protected SpecialParserAttributes _specialAttributes;
	protected CompilerLogger _logger;

	
	public Location getStartRealLocation()
	{
		return _terminal.getPositionPreceding();
	}
	
	public Location getEndRealLocation()
	{
		return _terminal.getPositionFollowing();
	}

	/* (non-Javadoc)
	 * Concatenates two strings.
	 * @param x The left string.
	 * @param y The right string.
	 * @return <CODE>x + y</CODE>.
	 */
	public static String strcat(String x,String y) { return x + y; }
	
	/* (non-Javadoc)
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
	
	/* (non-Javadoc)
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
	
	/* (non-Javadoc)
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
	
	public ParseTreeTermNode getChildT(int num)
	throws CopperException
	{
		ParseTreeTermNode rv = null;
		try { rv = (ParseTreeTermNode) _children[num]; }
		catch(ArrayIndexOutOfBoundsException ex) { error(_pos,"Attempt to access a nonexistent child node"); }
		catch(ClassCastException ex) { error(_pos,"Attempt to access a nonterminal node as a terminal"); }
		return rv; 
	}
	
	public ParseTreeProdNode getChildNT(int num)
	throws CopperException
	{
		ParseTreeProdNode rv = null;
		try { rv = (ParseTreeProdNode) _children[num]; }
		catch(ArrayIndexOutOfBoundsException ex) { error(_pos,"Attempt to access a nonexistent child node"); }
		catch(ClassCastException ex) { error(_pos,"Attempt to access a terminal node as a nonterminal"); }
		return rv; 
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
	
	public void runDefaultTermAction()
	throws IOException, CopperException
	{
		_logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,null,"Class " + this.getClass().getName() + " did not provide a necessary runDefaultTermAction() method");
	}
	
	public void runDefaultProdAction()
	throws IOException,CopperException
	{
		_logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,null,"Class " + this.getClass().getName() + " did not provide a necessary runDefaultProdAction() method");
	}

	/* (non-Javadoc)
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
	throws CopperException;
	
	/* (non-Javadoc)
	 * Runs the initialization code for each parser attribute. 
	 * @throws IOException When the code requires.
	 * @throws CopperException When the code requires.
	 */
	public abstract void runInit()
	throws IOException,CopperException;
	
	/* (non-Javadoc)
	 * Runs the semantic action code for a reduce action.
	 * @param _pos The input position at the reduction.
	 * @param children The parse tree produced by the reduction.
	 * @param _prod The production being reduced.
	 * @throws IOException When the code requires.
	 * @throws CopperException When the code requires.
	 */
	public abstract Object runSemanticAction(InputPosition _pos,Object[] children,Production _prod)
	throws IOException,CopperException;
	
	/* (non-Javadoc)
	 * Runs the semantic action code for a shift action.
	 * @param _pos The input position before the shift.
	 * @param _terminal The terminal being shifted.
	 * @throws IOException When the code requires.
	 * @throws CopperException When the code requires.
	 */
	public abstract Object runSemanticAction(InputPosition _pos,QScannerMatchData _terminal)
	throws IOException,CopperException;
	
	/* (non-Javadoc)
	 * Runs the semantic action code for a disambiguation by group.
	 * @param _pos The input position at the ambiguous scan.
	 * @param matches The matches returned by the scanner.
	 * @return The disambiguated match, or <CODE>null</CODE> if no disambiguation was available.
	 * @throws IOException When the code requires.
	 * @throws CopperException When the code requires.
	 */
	public abstract QScannerMatchData runDisambiguationAction(InputPosition _pos,HashSet<QScannerMatchData> matches)
	throws IOException,CopperException;
	
	public Object runSemanticAction(InputPosition _pos, Object[] _children, int _prod) throws IOException, CopperException
	{
		_logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,null,"Class " + this.getClass().getName() + " did not provide a necessary runSemanticAction() method");
		return null;
	}

	public Object runSemanticAction(InputPosition _pos, ParseTreeProdNode _parseTree, int _prod) throws IOException, CopperException
	{
		_logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,null,"Class " + this.getClass().getName() + " did not provide a necessary runSemanticAction() method");
		return null;
	}

	public int runDisambiguationAction(InputPosition _pos, QScannerMatchData matches) throws IOException, CopperException
	{
		_logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,null,"Class " + this.getClass().getName() + " did not provide a necessary runDisambiguationAction() method");
		return -1;
	}

}
