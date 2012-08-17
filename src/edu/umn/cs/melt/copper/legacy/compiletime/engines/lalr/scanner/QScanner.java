package edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.legacy.compiletime.auxiliary.DynHashSet;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.ParserState;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.QScannerStateInfo;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ReadOnlyParseTable;
import edu.umn.cs.melt.copper.legacy.compiletime.statistics.ScannerStatistics;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;



/* (non-Javadoc)
 * A superclass of context-aware scanners or QScanners.
 * A QScanner (extending class) is intended for a lexical
 * analyzer built on top of a DFA constructed from the union of
 * all regexes matchable by the scanner.
 * Given an input string and a set of possible token matches, it
 * should return a set of actual token matches and the strings
 * that match them.
 * It should, before <CODE>runScan()</CODE> is run on it,
 * set up the protected fields of this class so that:
 * <UL>
 * <LI><CODE>startState</CODE> represents the start state of
 * the underlying DFA.
 * <LI><CODE>buffer</CODE> represents an input buffer. At no time
 * should the buffer be advanced past any position passed as a
 * parameter to <CODE>runScan()</CODE>.
 * <LI><CODE>logger</CODE> represents a logger that could be invoked
 * in the event of a lexical syntax error.
 *   
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class QScanner
{
	/* (non-Javadoc) The DFA start state. */
	protected int startState;
	/* (non-Javadoc) The buffer of input characters. */
	protected ScannerBuffer buffer;
	/* (non-Javadoc) For the reporting of errors. */
	protected CompilerLogger logger;
	
	private ScannerStatistics statistics;
	private boolean gatherStatistics;
	
	protected HashSet<Terminal> lastShiftable;
	protected InputPosition lastPosition;
	protected HashSet<QScannerMatchData> lastMatched;
	protected Hashtable<Terminal,Symbol> precClassMap;
	
	public ScannerStatistics retrieveStatistics()
	{
		return statistics;
	}
	
	protected QScanner(boolean gatherStatistics)
	{
		if(gatherStatistics) statistics = new ScannerStatistics();
		else statistics = null;
		this.gatherStatistics = gatherStatistics;
		lastShiftable = new HashSet<Terminal>();
		lastPosition = null;
		lastMatched = null;
	}
	
	/* (non-Javadoc)
	 * Looks up a state in the transition table.
	 * @param state The table row index (current state).
	 * @param symbol The table column index (symbol).
	 * @return The state at table(state,symbol).
	 */
	protected abstract int transition(int state,char symbol);
	
	/* (non-Javadoc)
	 * Looks up information about a state.
	 * @param state The state for which information is sought.
	 * @return A <CODE>QScannerStateInfo</CODE> object containing state information.
	 */
	protected abstract QScannerStateInfo getStateInfo(int state);

	// Front-ends for shielded access to static fields.
	
	protected abstract QScannerMatch getMatch(Terminal t,InputPosition positionPreceding,InputPosition positionFollowing,ArrayList<QScannerMatchData> layouts);
	
	/* (non-Javadoc)
	 * Runs semantic actions.
	 * @param token The token on which to run the actions.
	 * @return A new lexeme rising from the action, or <CODE>null</CODE> if this token is semantically invalid (context free determination).
	 * @throws CopperException If there is a fatal semantic error.
	 */
	protected abstract String runSemanticAction(Terminal token)
	throws CopperException;

	public HashSet<QScannerMatchData> runLayoutScan(ParserState state,
			                                        ReadOnlyParseTable table)
	throws CopperException,IOException
	{
		return runLayoutScanInternal(state,table,false);
	}
	
	public HashSet<QScannerMatchData> runDisjointScan(ParserState state,
			                                          ReadOnlyParseTable table)
	throws CopperException,IOException
	{
		return runLayoutScanInternal(state,table,true);
	}
	
	/* (non-Javadoc)
	 * Runs the QScanner with layout throwaway.
	 * @param state The present parse state.
	 * @param table The parse table.
	 * @return Any acceptable matches for any shiftable tokens at <CODE>state</CODE>.
	 * @throws IOException If the input gives I/O errors.
	 * @throws CopperException If the implementation makes use of the error reporter.
	 */
	private HashSet<QScannerMatchData> runLayoutScanInternal(ParserState state,
			                                                 ReadOnlyParseTable table,
			                                                 boolean runDisjoint)
	throws CopperException,IOException
	{
		int statenum = state.getStatenum();
		InputPosition whence = state.getPos();
		HashSet<Terminal> shiftable = new HashSet<Terminal>();
		for(Terminal t : table.getShiftable(statenum)) shiftable.add(t);
		
		// DEBUG-X-BEGIN
		//if(lastMatched != null) System.err.println(lastMatched);
		//if(lastShiftable != null) System.err.println(lastShiftable);
		//System.err.println(shiftable);
		// DEBUG-X-END

		//   This is a simple test to avoid repeat scans in chains of reductions.
		// It is based on the fact that in such a case, owing to the manner
		// in which lookahead sets are constructed, each successive shiftable
		// set will be a (not necessarily proper) subset of its predecessor.
		//   All scans taking place at the same position as the previous
		// will be such reduction-chain scans, EXCEPT for the cases where the
		// previous scan matched an empty string and the token matched was
		// not the end of file. This means that instead of checking shiftable
		// sets, one may simply perform an EOF check and a check for an empty
		// lexeme.
		//   Now in such a reduction chain, one must test in each instance to
		// see if all the old matches are still in the shiftable set. If they
		// are not, clearly, a re-scan is mandatory; but if they are, it is
		// impossible that the scanner result should be different from the last
		// time around. 
		//   On syntactically valid inputs, except for cases when terminals are
		// disambiguated by group, the old matches are always in the new
		// shiftable set.
		if(!runDisjoint && whence.equals(lastPosition))
		{
			if(lastMatched.iterator().next().getToken().equals(FringeSymbols.EOF) ||
			   !lastMatched.iterator().next().getPositionPreceding().equals(lastMatched.iterator().next().getPositionFollowing())) 
			{
				boolean partiallyDisjoint = false;
				for(QScannerMatchData m : lastMatched)
				{
					if(!shiftable.contains(m.getToken()))
					{
						partiallyDisjoint = true;
						break;
					}
				}
				if(!partiallyDisjoint)
				{
					lastShiftable = shiftable;
					return lastMatched;
				}
			}
		}

		if(gatherStatistics)
		{
			if(!whence.equals(lastPosition))
			{
				statistics.tsapSummation += statistics.timesScannedAtPos;
				statistics.tokensSought++;
				statistics.timesScannedAtPos = 1;
			}
			else
			{
				if(!shiftable.equals(lastShiftable))
				{
					statistics.shiftableChanged++;
				}
				if(lastShiftable.containsAll(shiftable))
				{
					statistics.shiftableWasSubsetOnRepeat++;
					if(shiftable.contains(lastMatched)) statistics.lastMatchedTokenInSubset++;
				}
				statistics.repeatScans++;
				statistics.timesScannedAtPos++;
				if(statistics.timesScannedAtPos > statistics.maxScansPerToken) statistics.maxScansPerToken = statistics.timesScannedAtPos;
			}
		}
		
		lastPosition = whence;
		lastShiftable = shiftable;

		
		if(!table.hasLayout(statenum))
		{
			ArrayList<QScannerMatchData> layouts = new ArrayList<QScannerMatchData>();
			return runPrefixScan(state,table,whence,shiftable,layouts,runDisjoint);
		}
		else
		{
			ArrayList<QScannerMatchData> layouts = new ArrayList<QScannerMatchData>();
			for(int i = 0;;i++)
			{
				Collection<Terminal> shiftableLayout = table.getLayout(statenum);
				HashSet<QScannerMatchData> matchingLayout = runScan(whence,shiftableLayout,layouts);
				// DEBUG-X-BEGIN
				//System.err.println("State: (" + statenum + "," + shiftable + "), layout scan " + i + " -- " + shiftableLayout + "," + matchingLayout);
				// DEBUG-X-END
				if(layouts.isEmpty() && matchingLayout.isEmpty() && table.hasShiftableAfterLayout(statenum,FringeSymbols.EMPTY))
				{
					matchingLayout.add(new QScannerMatchData(FringeSymbols.EMPTY,whence,whence,new ArrayList<QScannerMatchData>()));
					shiftable.retainAll(table.getShiftableFollowingLayout(statenum,FringeSymbols.EMPTY));
					break;
				}
				if(matchingLayout.size() == 1)
				{
					QScannerMatchData layout = matchingLayout.iterator().next();
					Terminal layoutT = layout.getToken();
					whence = InputPosition.advance(whence,layoutT.getLexeme().length(),layoutT.getLexeme());
					shiftable.retainAll(table.getShiftableFollowingLayout(statenum,layoutT));
					if(layoutT.getLexeme().length() == 0)
					{
						if(layouts.isEmpty()) layouts.add(layout);
						break;
					}
					else
					{
						layouts.add(layout);
						continue;					
					}
				}
				else if(matchingLayout.size() > 1)
				{
					return matchingLayout;
				}
				else if(layouts.isEmpty() && matchingLayout.isEmpty())
				{
					if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,whence,"Expected layout of types " + shiftableLayout + " at layout scan " + i);
					break;
				}
				else if(matchingLayout.isEmpty()) break;
			}
			HashSet<QScannerMatchData> finalMatches;
			finalMatches = runPrefixScan(state,table,whence,shiftable,layouts,runDisjoint);
			if(!finalMatches.isEmpty()) lastMatched = finalMatches;
			// DEBUG-X-BEGIN
			//System.err.println(finalMatches);
			// DEBUG-X-END
			return finalMatches;
		}
	}
	
	/* (non-Javadoc)
	 * Runs the QScanner, first scanning for any transparent prefixes that may be present.
	 * @param state The parser state.
	 * @param table The parse table.
	 * @param whence The position from whence to start the scan.
	 * @param shiftable The collection of shiftable tokens (not prefixes; these are gleaned from the parse table).
	 * @param layouts The collection of layouts already scanned.
	 * @return Any acceptable matches for any shiftable tokens at <CODE>state</CODE>.
	 * @throws IOException If the input gives I/O errors.
	 * @throws CopperException If the implementation makes use of the error reporter.
	 */
	public HashSet<QScannerMatchData> runPrefixScan(ParserState state,
			                                        ReadOnlyParseTable table,
			                                        InputPosition whence,
			                                        Collection<Terminal> shiftable,
			                                        ArrayList<QScannerMatchData> layouts,
			                                        boolean runDisjoint)
	throws IOException,CopperException
	{
		if(!table.hasPrefixes(state.getStatenum()))
		{
			if(!runDisjoint) return runScan(whence,shiftable,layouts);
			else return runScan(whence,table.getShiftableUnion(),layouts);
		}
		Collection<Terminal> shiftablePrefixes = table.getPrefixes(state.getStatenum()); 
		HashSet<QScannerMatchData> matchingPrefixes = runScan(whence,shiftablePrefixes,layouts);
		if(matchingPrefixes.isEmpty())
		{
			if(!runDisjoint) return runScan(whence,shiftable,layouts);
			else return runScan(whence,table.getShiftableUnion(),layouts);
		}
		if(matchingPrefixes.size() > 1)
		{
			return matchingPrefixes;
		}
		QScannerMatchData prefixD = matchingPrefixes.iterator().next(); 
		Terminal prefix = prefixD.getToken();
		layouts.add(prefixD);
		shiftable = table.getShiftableFollowingPrefix(state.getStatenum(),prefix);
		whence = InputPosition.advance(whence,prefix.getLexeme().length(),prefix.getLexeme());
		if(!runDisjoint) return runScan(whence,shiftable,layouts);
		else return runScan(whence,table.getShiftableUnion(),layouts);
	}
	
	/* (non-Javadoc)
	 * Runs the QScanner.
	 * @param whence The position in the input from whence to start the scan.
	 * @param shiftable The tokens that can be matched in this context.
	 * @return Any acceptable matches for any tokens in <CODE>shiftableS</CODE>.
	 * @throws IOException If the input gives I/O errors.
	 * @throws CopperException If the implementation makes use of the error reporter.
	 */
	public HashSet<QScannerMatchData> runScan(InputPosition whence,
			                          Iterable<Terminal> shiftable,
			                          ArrayList<QScannerMatchData> layouts)
	throws IOException,CopperException
	{
		DynHashSet<QScannerMatch> present = runSingleScan(whence,shiftable,layouts);
		// Put all matches into one HashSet and return it
		HashSet<QScannerMatchData> finalMatches = new HashSet<QScannerMatchData>();
		if(present == null) finalMatches.add(
				              new QScannerMatchData(
				               FringeSymbols.EOF,
				               whence,
				               whence,
				               layouts));
		else finalMatches = consolidateMatches(present);
		return finalMatches;
	}
	
	private HashSet<QScannerMatchData> consolidateMatches(DynHashSet<QScannerMatch> present)
	throws IOException,CopperException
	{
		HashSet<QScannerMatchData> finalMatches = new HashSet<QScannerMatchData>();
		for(QScannerMatch match : present)
		{
			for(QScannerMatchData lexeme : match.getLexemes())
			{
				String newLex = runSemanticAction(lexeme.getToken());
				if(newLex != null) finalMatches.add(
						             new QScannerMatchData(
						              lexeme.getToken().newLexeme(newLex),
						              lexeme.getPositionPreceding(),
						              lexeme.getPositionFollowing(),
						              lexeme.getLayouts()));
			}
		}
		// DEBUG-X-BEGIN
		//System.err.println("Matched: " + finalMatches);
		// DEBUG-X-END
		return finalMatches;		
	}
	
	private DynHashSet<QScannerMatch> runSingleScan(InputPosition whence,
										             Iterable<Terminal> shiftable,
										             ArrayList<QScannerMatchData> layouts)
	throws IOException,CopperException
	{
		int currentState = startState;
		char symbol = '\0';
		InputPosition p;
		HashSet<Terminal> shiftableS = new HashSet<Terminal>();
		// DEBUG-X-BEGIN
		//System.err.print("Starting QScan with shiftable set: ");
		//System.err.println(shiftable);
		// DEBUG-X-END
		for(Terminal t : shiftable)
		{
			if(!t.equals(FringeSymbols.EMPTY)) shiftableS.add(t);
		}
		// The present set of matching tokens.
		DynHashSet<QScannerMatch> present = new DynHashSet<QScannerMatch>();
		// For each input position starting at whence
		for(p = InputPosition.copy(whence);;
		    p = InputPosition.advance(p,symbol))
		{
			// DEBUG-X-BEGIN
			//System.err.println(present);
			// DEBUG-X-END
			// If there are no possible symbols for this state, terminate
			HashSet<Terminal> tP = new HashSet<Terminal>(getStateInfo(currentState).getPossibleSyms());
			tP.retainAll(shiftableS);
			if(p.equals(whence) && shiftableS.contains(FringeSymbols.EOF)) tP.add(FringeSymbols.EOF);
			if(tP.isEmpty()) break;
			shiftableS.retainAll(tP);
			// Intersect the set of possible tokens with the set of
			// tokens matching at this input position
			HashSet<Terminal> tA = new HashSet<Terminal>(getStateInfo(currentState).getAcceptingSyms());
			tA.retainAll(shiftableS);
			// If there are matches of this length, purge the present set,
			// in accordance with the "maximal munch" mode of scanning.
			// Make the "maximal munch" optional, perhaps with a special
			// "maximal munch" matchtype.
			if(!tA.isEmpty())
			{
				present.clear();
			}
			else
			{
				HashSet<Terminal> tR = new HashSet<Terminal>(getStateInfo(currentState).getRejectingSyms());
				tR.retainAll(shiftableS);
				if(!tR.isEmpty())
				{
					present.clear();
				}
			}
			// Union the result with the present set
			for(Terminal t : tA)
			{
				Terminal token = t.newLexeme(buffer.readStringFromBuffer(whence.getPos(),p.getPos()));
				// DEBUG-X-BEGIN
				//System.err.println("Token " + token + "; precedence " + precedence);
				// DEBUG-X-END
				QScannerMatch m = getMatch(token,InputPosition.copy(whence),InputPosition.copy(p),layouts);
				present.put(m);
			}
			// Get the next character in the buffer
			symbol = buffer.charAt(p.getPos());
			// If end of file were reached, break
			if(symbol == ScannerBuffer.EOFIndicator)
			{
				break;
			}
			// Else transition to the next state
			currentState = transition(currentState,symbol);
		}
		if(symbol == ScannerBuffer.EOFIndicator &&
				   p.equals(whence) &&
				   shiftableS.contains(FringeSymbols.EOF)) return null;
		else return present;
	}
	
}
