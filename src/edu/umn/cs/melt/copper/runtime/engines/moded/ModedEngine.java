package edu.umn.cs.melt.copper.runtime.engines.moded;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Stack;

import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;
import edu.umn.cs.melt.copper.runtime.engines.CopperParser;
import edu.umn.cs.melt.copper.runtime.engines.moded.scanner.ModedMatchData;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAParseStackNode;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;

/**
 * An engine containing mechanisms of both parser and scanner, following the
 * multiple-DFA algorithm of context-aware scanning.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class ModedEngine<ROOT,EXCEPT extends Exception> implements CopperParser<ROOT,EXCEPT>
{
	// Abstraction barriers for bit-mashing. Limit 268,435,456 symbols + productions, states.
	public static int newSymbol(int symType,int index) { return ((symType & 0x07) << 28) | (index & 0x0FFFFFFF); }
	public static int newAction(int symType,int index) { return ((symType & 0x07) << 28) | (index & 0x0FFFFFFF); }
	public static int actionIndex(int action) { return ((action & 0x08000000) != 0) ? (action | 0xF0000000) : (action & 0x0FFFFFFF); }
	public static int actionType(int action) { return (action >> 28) & 0x07; }
	
	// Abstraction barriers for bit vectors.
	public String bitVecToString(BitSet vec)
	{
		return PrettyPrinter.bitSetPrettyPrint(vec,symbolNames,"   ",1);
	}
	
	public static boolean isMelded(byte situation) { return ((situation & SITUATION_IS_MELDED) != 0); }
	public static boolean contextRequiresLayout(byte situation) { return ((situation & SITUATION_REQ_LAYOUT) != 0); }
	
	/* Identifier tags for parser states. */
	public static final int STATE_ERROR = 0;
	public static final int STATE_SHIFT = 1;
	public static final int STATE_GOTO = 1;
	public static final int STATE_REDUCE = 2;
	public static final int STATE_ACCEPT = 3;
	public static final int STATE_IGNORELAYOUT = 3;
	
	/* Identifier tags for symbols. */
	public static final int SYMBOL_TERMINAL = 0;
	public static final int SYMBOL_NONTERM = 1;
	public static final int SYMBOL_PRODUCTION = 2;
	public static final int SYMBOL_DISAMBIG_GROUP_CODE = 3;
	public static final int SYMBOL_DISAMBIG_GROUP_SCHROEDINGER = 4;
	
	/* Constants for "melded" situation. */
	public static final int MELDED_ALL_STATES = 0;
	public static final int MELDED_NO_STATES = 1;
	public static final int MELDED_SOME_STATES = 2;
	
	public static final int SITUATION_IS_MELDED = 1;
	public static final int SITUATION_REQ_LAYOUT = 2;
	
	/* Counts for building arrays statically. */
	protected static int TERMINAL_COUNT;
	protected static int GRAMMAR_SYMBOL_COUNT;
	protected static int GRAMMAR_STRUCTURE_COUNT;
	protected static int SYMBOL_COUNT;
	protected static int PARSER_STATE_COUNT;
	protected static int SCANNER_STATE_COUNT;
	protected static int DISAMBIG_GROUP_COUNT;
	
	//protected static int SCANNER_START_STATENUM;
	protected static int PARSER_START_STATENUM;
	protected static int EOF_SYMNUM;
	protected static int EPS_SYMNUM;
	
	/** Names of terminals, nonterminals, productions, etc. */
	protected static String[] symbolNames;
	/** Lengths of productions, types of symbols. */
	protected static int[] symbolNumbers;
	/** Symbols on the left-hand sides of productions. */
	protected static int[] productionLHSs;
	
	/** Parse actions. */
	protected static int[][] parseTable;
	/** Scanner states corresponding to shiftable sets. */
	protected static int[] shiftableStates;
	/** Shiftable sets. */
	protected static BitSet[] shiftableSets;
	/** Layout sets. */
	protected static int[] layoutSets;
	/** Prefix sets. */
	protected static int[] prefixSets;
	/** Maps of layout terminals. */
	//protected static int[][] layoutMaps;
	/** Maps of prefix terminals. */
	protected static int[][] prefixMaps;
	/** Whether or not the improved layout scanner is used. */
	protected static byte[] meldedSituations;
	protected static int meldedSituation;
	
	/** Shiftable union --- all terminals with a parse action
	 * (usually all non-prefix, non-layout terminals). */
	protected static int shiftableUnion;
	
	/** State sets for scanner DFA. */
	protected static int[] acceptSets;
	
	protected static int[][] delta;
	protected static int[] cmap;
	
	protected InputPosition errorPos;

	protected abstract int transition(int state,char ch);
	protected abstract int runDisambiguationAction(InputPosition _pos,ModedMatchData match)
		throws IOException,EXCEPT;
    protected abstract Object runSemanticAction(InputPosition _pos,Object[] _children,int _prod)
    	throws IOException,EXCEPT;
	protected abstract Object runSemanticAction(InputPosition _pos,ModedMatchData _terminal)
		throws IOException,EXCEPT;
	
	public void runPostParseCode(Object __root)
    throws IOException,EXCEPT
    {
	}

	/**
	 * Runs the parser on a given input, which is taken to be standard input, with default error reporting.
	 * @param input The reader from which to read the parser's input.
	 * @return The synthesized attribute of the root node of the input's derivative parse tree.
	 * @throws IOException If an I/O error occurs.
	 * @throws EXCEPT If a parse error occurs.
	 */
	public ROOT parse(Reader input)
        throws IOException,EXCEPT
    {
		return parse(input,"<stdin>");
    }

	@Override
	public abstract ROOT parse(Reader input,String inputName)
	    throws IOException,EXCEPT;
	

	@Override
	public ROOT parse(String text)
    throws IOException,EXCEPT
	{
		return parse(text,"<StringBuffer>");
	}

	@Override
	public ROOT parse(String text,String inputName)
	    throws IOException,EXCEPT
	{
		StringReader reader = new StringReader(text);
		return parse(reader,inputName);
	}
	
	protected Stack<SingleDFAParseStackNode> parseStack;
	protected VirtualLocation virtualLocation;
	
	protected SingleDFAParseStackNode currentState;
	protected ModedMatchData scanResult;

    protected ScannerBuffer buffer;
    
    protected ModedMatchData lastMatched;
    protected InputPosition lastPosition;
    protected int lastShiftable;
    
    protected abstract String formatError(String error);
    
    protected abstract void reportError(String error)
    throws EXCEPT;

	protected ModedMatchData layoutScan(boolean runDisjoint)
	throws IOException,EXCEPT
	{
		int shiftable = shiftableStates[currentState.statenum];
		InputPosition whence = currentState.pos;
		buffer.advanceBufferTo(whence.getPos());
		if(!runDisjoint && whence.equals(lastPosition))
		{
			if(lastMatched.term == EOF_SYMNUM || lastMatched.precedingPos.equals(lastMatched.followingPos))
			{
				boolean partiallyDisjoint = false;
				if(!shiftableSets[currentState.statenum].get(lastMatched.term)) partiallyDisjoint = true;
				if(!partiallyDisjoint)
				{
					lastShiftable = shiftable;
					return lastMatched;
				}
			}
		}

		lastPosition = whence;
		lastShiftable = shiftable;
		
		LinkedList<ModedMatchData> layouts = new LinkedList<ModedMatchData>();
		if(layoutSets[currentState.statenum] == -1)
		{
			return prefixScan(whence,shiftable,layouts,runDisjoint);
		}
		else
		{
			//for(int i = 0;;i++)
			for(;;)
			{
				ModedMatchData matchingLayout = simpleScan(whence,layoutSets[currentState.statenum],layouts);
				if(layouts.isEmpty() &&
				   matchingLayout.term == -1 /*&&
				   layoutMaps[currentState.statenum][EPS_SYMNUM] != 0*/)
				{
					matchingLayout.term = EPS_SYMNUM;
					//shiftable = shiftable.get(0,TERMINAL_COUNT);
					break;
				}
				if(layouts.isEmpty() && matchingLayout.term == -1)
				{
					reportError(formatError("Expected layout of the following types:\n" + bitVecToString(shiftableSets[layoutSets[currentState.statenum]])));
					break;
				}
				else if(matchingLayout.term == -1) break;
				// New feature: Layout no longer restricts shiftable set.
				//shiftable.and(layoutMaps[currentState.statenum][matchingLayout.firstTerm]);
				whence = InputPosition.advance(whence,matchingLayout.lexeme.length(),matchingLayout.lexeme);
				if(matchingLayout.lexeme.length() == 0)
				{
					if(layouts.isEmpty()) layouts.add(matchingLayout);
					break;
				}
				else
				{
					layouts.add(matchingLayout);
					continue;
				}
			}
			ModedMatchData finalMatches = prefixScan(whence,shiftable,layouts,runDisjoint);
			if(finalMatches.term != -1) lastMatched = finalMatches;
			return finalMatches;
		}
	}

	protected ModedMatchData prefixScan(InputPosition whence,
            						 int shiftable,
            						 LinkedList<ModedMatchData> layouts,
            						 boolean runDisjoint)
	throws IOException
	{
		if(prefixSets[currentState.statenum] == 0)
		{
			return maybeDisjointScan(whence,shiftable,layouts,runDisjoint);
		}
		ModedMatchData matchingPrefixes = simpleScan(whence,prefixSets[currentState.statenum],new LinkedList<ModedMatchData>());//layouts);
		// DEBUG-X-BEGIN
		//System.err.println("Shiftable " + bitVecToString(shiftable));
		//System.err.println("Matching prefixes: " + symbolNames[matchingPrefixes.firstTerm] + " " + bitVecToString(matchingPrefixes.terms));
		// DEBUG-X-END
		if(matchingPrefixes.term == -1)
		{
			return maybeDisjointScan(whence,shiftable,layouts,runDisjoint);
		}
		/*if(matchingPrefixes.terms.cardinality() > 1)
		{
			return matchingPrefixes;
		}*/
		layouts.add(matchingPrefixes);
		shiftable = prefixMaps[currentState.statenum][matchingPrefixes.term];
		whence = InputPosition.advance(whence,matchingPrefixes.lexeme.length(),matchingPrefixes.lexeme);
		// DEBUG-X-BEGIN
		//System.err.println("New shiftable: " + bitVecToString(shiftable));
		// DEBUG-X-END
		return maybeDisjointScan(whence,shiftable,layouts,runDisjoint);
	}

	protected ModedMatchData maybeDisjointScan(InputPosition whence,
            								int shiftable,
            								LinkedList<ModedMatchData> layouts,
            								boolean runDisjoint)
	throws IOException
	{
		// DEBUG-X-BEGIN
		//System.err.println("Run disjoint: " + runDisjoint);
		// DEBUG-X-END
		if(!runDisjoint) return simpleScan(whence,shiftable,layouts);
		else
		{
			errorPos = InputPosition.copy(whence);
			return simpleScan(whence,shiftableUnion,layouts);
		}
	}

	protected ModedMatchData simpleScan(InputPosition whence,
            						 int shiftable,
            						 LinkedList<ModedMatchData> layouts)
	throws IOException
	{
		// DEBUG-X-BEGIN
		//System.err.println("Simple-shiftable " + shiftable);
		// DEBUG-X-END
		int currentState = shiftable;
		char symbol = '\0';
		InputPosition p;
		int present = -1;
		InputPosition presentPos = whence;
		for(p = InputPosition.copy(whence);;p = InputPosition.advance(p,symbol))
		{
			if(currentState == 0) break;
			int tA = acceptSets[currentState];
			if(tA != -1)
			{
				present = tA;
				presentPos = InputPosition.copy(p);
			}
			symbol = buffer.charAt(p.getPos());
			if(symbol == ScannerBuffer.EOFIndicator)
			{
				break;
			}
			currentState = transition(currentState,symbol);
		}
		if(symbol == ScannerBuffer.EOFIndicator &&
		   p.equals(whence)/* &&
		   shiftableSets[this.currentState.statenum].get(EOF_SYMNUM)*/)
		{
			present = EOF_SYMNUM;
			return new ModedMatchData(present,whence,p,"",layouts);
		}
		else return new ModedMatchData(present,whence,presentPos,buffer.readStringFromBuffer(whence.getPos(),presentPos.getPos()),layouts);
	}
	
	protected void startEngine(InputPosition initialPos)
    throws IOException,EXCEPT
	{
		parseStack = new Stack<SingleDFAParseStackNode>();
		parseStack.push(new SingleDFAParseStackNode(PARSER_START_STATENUM,initialPos,null));
		virtualLocation = new VirtualLocation(initialPos.getFileName(),1,0);
	}
	
	protected Object runEngine()
	throws IOException,EXCEPT
	{
		ModedMatchData scanResult;
		boolean currentStateIsMelded;
		boolean lastLexemeWasEmpty = false,seenLayout = false;
		int nextShiftableState = shiftableStates[parseStack.peek().statenum];
		while(true)
		{
			currentState = parseStack.peek();
			currentStateIsMelded = (meldedSituation == MELDED_SOME_STATES) ? 
					                    isMelded(meldedSituations[currentState.statenum]) :
					                    (meldedSituation == MELDED_ALL_STATES); 
			if(currentStateIsMelded)
			{
				scanResult = simpleScan(currentState.pos,nextShiftableState,null);
			    if(scanResult.precedingPos.equals(scanResult.followingPos))
			    {
			        if(lastLexemeWasEmpty) scanResult.term = -1;
			    	else lastLexemeWasEmpty = true;
			    }
			}
			else
			{
				scanResult = layoutScan(false);
			}
			int action = 0;
			int maybeModifiedTerm = scanResult.term;
			if(scanResult.term != -1)
			{
				if(actionType(symbolNumbers[scanResult.term]) == SYMBOL_DISAMBIG_GROUP_SCHROEDINGER)
				{
					maybeModifiedTerm = actionIndex(symbolNumbers[scanResult.term]);
				}
				else if(actionType(symbolNumbers[scanResult.term]) == SYMBOL_DISAMBIG_GROUP_CODE)
				{
					maybeModifiedTerm = runDisambiguationAction(currentState.pos,scanResult);
				}
			}
			if(maybeModifiedTerm != -1) action = parseTable[currentState.statenum][maybeModifiedTerm];
			// DEBUG-X-BEGIN
			/*if(scanResult.term != 1 && maybeModifiedTerm != -1)
			{
				if(!currentStateIsMelded) System.err.println("Current match at " + currentState.pos + ":\n   " + scanResult.term + "(" + symbolNames[scanResult.term] + ") modified to " + maybeModifiedTerm + "(" + symbolNames[maybeModifiedTerm] + ") \"" + scanResult.lexeme + "\" -- " + scanResult.layouts.size() + " layouts preceding");
				else System.err.println("Current match at (" + currentState.statenum + "," + currentState.pos + ")" + ", scanner state " + nextShiftableState + ":\n   " + scanResult.term + "(" + symbolNames[scanResult.term] + ") modified to " + maybeModifiedTerm + "(" + symbolNames[maybeModifiedTerm] + ") \"" + scanResult.lexeme + "\"");
			}*/
			// DEBUG-X-END
			switch(actionType(action))
			{
			case STATE_ACCEPT:
				if(maybeModifiedTerm == EOF_SYMNUM)
				{
					/*if(parseStack.peek().node == null)*/ return parseStack.peek().synthAttr;
					/*else return parseTree;*/
				}
				/* case STATE_IGNORELAYOUT:*/
				else
				{
					seenLayout = true;
				    scanResult.term = maybeModifiedTerm;
  					runSemanticAction(scanResult.precedingPos,scanResult);
					virtualLocation.defaultUpdateAutomatic(scanResult.lexeme);
				    currentState.pos = scanResult.followingPos;
				    nextShiftableState = actionIndex(action);
				}  
				break;
			case STATE_SHIFT:
				if(!seenLayout && contextRequiresLayout(meldedSituations[currentState.statenum]))
				{
					reportError(formatError("Expected whitespace, matched non-whitespace"));
				}
				seenLayout = false;
				lastLexemeWasEmpty = false;
				scanResult.term = maybeModifiedTerm;
				int nextState = actionIndex(action);
				nextShiftableState = shiftableStates[nextState];
				if(!currentStateIsMelded)
				{
					for(ModedMatchData layout : scanResult.layouts)
					{
						runSemanticAction(layout.precedingPos,layout);
						virtualLocation.defaultUpdateAutomatic(layout.lexeme);
					}
				}
				Object synthAttr = runSemanticAction(scanResult.precedingPos,scanResult);
				virtualLocation.defaultUpdateAutomatic(scanResult.lexeme);
				parseStack.push(new SingleDFAParseStackNode(nextState,scanResult.followingPos,synthAttr));
				break;
			case STATE_REDUCE:
				if(!seenLayout && contextRequiresLayout(meldedSituations[currentState.statenum]))
				{
					reportError(formatError("Expected whitespace, matched non-whitespace"));
				}
				lastLexemeWasEmpty = false;
				scanResult.term = maybeModifiedTerm;
				int production = actionIndex(action);
				int productionLength = actionIndex(symbolNumbers[production]);
				int productionLHS = actionIndex(productionLHSs[production - GRAMMAR_SYMBOL_COUNT]);
				Object[] children = new Object[productionLength];
				for(int i = productionLength - 1;i >= 0;i--)
				{
					children[i] = parseStack.pop().synthAttr;
				}
				int gotoState = actionIndex(parseTable[parseStack.peek().statenum][productionLHS]);
				nextShiftableState = shiftableStates[gotoState];
				synthAttr = runSemanticAction(currentState.pos,children,production);
				parseStack.push(new SingleDFAParseStackNode(gotoState,currentState.pos,synthAttr));
				
				break;
			default:
				ModedMatchData disjointMatch = layoutScan(true);
				for(ModedMatchData layout : disjointMatch.layouts) virtualLocation.defaultUpdateAutomatic(layout.lexeme);
				String newError = "Expected a token of the following types:\n" + bitVecToString(shiftableSets[currentState.statenum]);
				if(disjointMatch.term == -1)
				{
					newError += "\n   Input [" + disjointMatch.lexeme + "] does not match any terminals";
				}
				else if(disjointMatch.term < GRAMMAR_STRUCTURE_COUNT)
				{
					 newError += "\n   Input currently matches:\n" + "   Lexeme: [" + disjointMatch.lexeme + "]\n   Terminal:\n    [" + symbolNames[disjointMatch.term] + "]";
				}
				else
				{
					maybeModifiedTerm = -1;
					if(actionType(symbolNumbers[disjointMatch.term]) == SYMBOL_DISAMBIG_GROUP_CODE) maybeModifiedTerm = runDisambiguationAction(currentState.pos,scanResult);
					newError += "\n   Input currently matches:\n" + "   Lexeme: [" + disjointMatch.lexeme + "]\n   Terminals:\n" + symbolNames[disjointMatch.term];
					if(maybeModifiedTerm != -1) newError += "\n   Disambiguating to:\n    [" + symbolNames[maybeModifiedTerm] + "]";
				}
				reportError(formatError(newError));
			}
		}
	}
}
