package edu.umn.cs.melt.copper.legacy.runtime.engines.split;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Stack;

import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;
import edu.umn.cs.melt.copper.runtime.engines.CopperParser;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAParseStackNode;
import edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;

/**
 * An experimental engine built to implement the runtime side of
 * parsers built by parse table composition.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class SplitEngine<ROOT,EXCEPT extends Exception> implements CopperParser<ROOT,EXCEPT>
{
	// Abstraction barriers for bit-mashing. Limit 536,870,912 symbols + productions, states.
	public static int newSymbol(int symType,int index) { return ((symType & 0x03) << 29) | (index & 0x1FFFFFFF); }
	public static int newAction(int symType,int index) { return ((symType & 0x03) << 29) | (index & 0x1FFFFFFF); }
	public static int actionIndex(int action) { return (action & 0x1FFFFFFF); }
	public static int actionType(int action) { return (action >> 29) & 0x03; }
	// Abstraction barriers for bit vectors.
	public static BitSet newBitVec(int totalBits,int... newBits)
	{
		BitSet rv = new BitSet(totalBits);
		setBits(rv,newBits);
		return rv;
	}
	public static void setBits(BitSet vec,int... newBits)
	{
		for(int bit : newBits) vec.set(bit);
	}
	public String bitVecToString(BitSet vec)
	{
		return PrettyPrinter.bitSetPrettyPrint(vec,symbolNames,"   ",1);
	}

	/* Identifier tags for parser states. */
	public static final int STATE_ERROR = 0;
	public static final int STATE_SHIFT = 1;
	public static final int STATE_GOTO = 1;
	public static final int STATE_REDUCE = 2;
	public static final int STATE_ACCEPT = 3;
	
	/* Identifier tags for symbols. */
	public static final int SYMBOL_TERMINAL = 0;
	public static final int SYMBOL_NONTERM = 1;
	public static final int SYMBOL_PRODUCTION = 2;

	/* Counts for building arrays statically. */
	protected static int TERMINAL_COUNT;
	protected static int GRAMMAR_SYMBOL_COUNT;
	protected static int SYMBOL_COUNT;
	protected static int PARSER_STATE_COUNT;
	protected static int[] scannerStateCounts;
	protected static int DISAMBIG_GROUP_COUNT;
	
	protected static int[] scannerStartStates;
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
	protected static int[][] parseTableHost,parseTableExts,parseTableMarking;
	/** Shiftable sets. */
	protected static BitSet[] shiftableSets;
	/** Layout sets. */
	protected static BitSet[] layoutSets;
	/** Prefix sets. */
	protected static BitSet[] prefixSets;
	/** Maps of layout terminals. */
	protected static BitSet[][] layoutMaps;
	/** Maps of prefix terminals. */
	protected static BitSet[][] prefixMaps;
	
	protected static BitSet[] disambiguationGroups;
	
	protected static BitSet[] neededScanners,neededLayoutScanners,neededPrefixScanners;
	
	public abstract int getParseTableAction(int state,int symbol);
		
	/** Shiftable union --- all terminals with a parse action
	 * (usually all non-prefix, non-layout terminals). */
	protected static BitSet shiftableUnion;
	
    /** Functions for determining character ranges. */
    protected static boolean cheq(char input,char single)
    {
        return (input == single);
    }
    protected static boolean chin(char input,char min,char max)
    {
        return (input >= min && input <= max);
    }
	
	protected abstract int transition(int scanner,int state,char ch);
	protected abstract BitSet getAcceptSet(int scanner,int state);
	protected abstract BitSet getRejectSet(int scanner,int state);
	protected abstract BitSet getPossibleSet(int scanner,int state);
	protected abstract int runDisambiguationAction(InputPosition _pos,SingleDFAMatchData match)
		throws IOException,EXCEPT;
    protected abstract Object runSemanticAction(InputPosition _pos,Object[] _children,int _prod)
    	throws IOException,EXCEPT;
	protected abstract Object runSemanticAction(InputPosition _pos,SingleDFAMatchData _terminal)
		throws IOException,EXCEPT;
	
	public void runPostParseCode(Object __root)
	throws IOException,EXCEPT
	{
	}
	
	@Override
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
	protected SingleDFAMatchData scanResult;
	
	
    protected ScannerBuffer buffer;
    
    protected BitSet lastShiftable;
    protected InputPosition lastPosition;
    protected SingleDFAMatchData lastMatched;
    
    protected abstract String formatError(String error);

    protected abstract void reportError(String error) throws EXCEPT;
    

	protected SingleDFAMatchData layoutScan(boolean runDisjoint)
	throws IOException,EXCEPT
	{
		BitSet shiftable = shiftableSets[currentState.statenum];
		InputPosition whence = currentState.pos;
		buffer.advanceBufferTo(whence.getPos());
		if(!runDisjoint && whence.equals(lastPosition))
		{
			if(lastMatched != null &&
				 (lastMatched.terms.get(EOF_SYMNUM) || lastMatched.precedingPos.equals(lastMatched.followingPos)))
			{
				boolean partiallyDisjoint = false;
				for(int i = lastMatched.terms.nextSetBit(0);i >= 0;i = lastMatched.terms.nextSetBit(i + 1))
				{
					if(!shiftable.get(i))
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

		lastPosition = whence;
		lastShiftable = shiftable;
		
		LinkedList<SingleDFAMatchData> layouts = new LinkedList<SingleDFAMatchData>();
		if(layoutSets[currentState.statenum].isEmpty())
		{
			return prefixScan(whence,shiftable,layouts,runDisjoint);
		}
		else
		{
			shiftable = shiftable.get(0,TERMINAL_COUNT);
			//for(int i = 0;;i++)
			for(;;)
			{
				SingleDFAMatchData matchingLayout = simpleScan(whence,neededLayoutScanners[currentState.statenum],layoutSets[currentState.statenum],layouts);
				if(layouts.isEmpty() &&
				   matchingLayout.terms.isEmpty() &&
				   layoutMaps[currentState.statenum][EPS_SYMNUM] != null &&
				   !layoutMaps[currentState.statenum][EPS_SYMNUM].isEmpty())
				{
					matchingLayout.terms.set(EPS_SYMNUM);
					matchingLayout.firstTerm = matchingLayout.terms.nextSetBit(0);
					//shiftable = shiftable.get(0,TERMINAL_COUNT);
					shiftable.and(layoutMaps[currentState.statenum][EPS_SYMNUM]);
					break;
				}
				if(matchingLayout.terms.cardinality() == 1)
				{
					//shiftable = shiftable.get(0,TERMINAL_COUNT);
					shiftable.and(layoutMaps[currentState.statenum][matchingLayout.firstTerm]);
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
				else if(matchingLayout.terms.cardinality() > 1)
				{
					return matchingLayout;
				}
				else if(layouts.isEmpty() && matchingLayout.terms.isEmpty())
				{
					reportError(formatError("Expected layout of the following types:\n" + bitVecToString(layoutSets[currentState.statenum])));
					break;
				}
				else if(matchingLayout.terms.isEmpty()) break;
			}
			SingleDFAMatchData finalMatches = prefixScan(whence,shiftable,layouts,runDisjoint);
			if(!finalMatches.terms.isEmpty()) lastMatched = finalMatches;
			return finalMatches;
		}
	}
	
	protected SingleDFAMatchData prefixScan(InputPosition whence,
			                    BitSet shiftable,
			                    LinkedList<SingleDFAMatchData> layouts,
			                    boolean runDisjoint)
	throws IOException
	{
		if(prefixSets[currentState.statenum].isEmpty())
		{
			return maybeDisjointScan(whence,neededScanners[currentState.statenum],shiftable,layouts,runDisjoint);
		}
		SingleDFAMatchData matchingPrefixes = simpleScan(whence,neededPrefixScanners[currentState.statenum],prefixSets[currentState.statenum],new LinkedList<SingleDFAMatchData>());//layouts);
		//System.err.println("Shiftable " + bitVecToString(shiftable));
		//System.err.println("Matching prefixes: " + symbolNames[matchingPrefixes.firstTerm] + " " + bitVecToString(matchingPrefixes.terms));
		if(matchingPrefixes.terms.isEmpty())
		{
			return maybeDisjointScan(whence,neededScanners[currentState.statenum],shiftable,layouts,runDisjoint);
		}
		if(matchingPrefixes.terms.cardinality() > 1)
		{
			return matchingPrefixes;
		}
		layouts.add(matchingPrefixes);
		shiftable = prefixMaps[currentState.statenum][matchingPrefixes.firstTerm];
		whence = InputPosition.advance(whence,matchingPrefixes.lexeme.length(),matchingPrefixes.lexeme);
		//System.err.println("New shiftable: " + bitVecToString(shiftable));
		return maybeDisjointScan(whence,neededScanners[currentState.statenum],shiftable,layouts,runDisjoint);
	}
	
	protected SingleDFAMatchData maybeDisjointScan(InputPosition whence,
									   BitSet scanners,
			                           BitSet shiftable,
			                           LinkedList<SingleDFAMatchData> layouts,
			                           boolean runDisjoint)
	throws IOException
	{
		//System.err.println("Run disjoint: " + runDisjoint);
		if(!runDisjoint) return simpleScan(whence,scanners,shiftable,layouts);
		else return simpleScan(whence,scanners,shiftableUnion,layouts);
	}
	
	protected SingleDFAMatchData simpleScan(InputPosition whence,
								BitSet scanners,
			                    BitSet shiftable,
			                    LinkedList<SingleDFAMatchData> layouts)
	throws IOException
	{
		// DEBUG-X-BEGIN
		//System.err.println("Scanner set " + scanners + ", shiftable " + bitVecToString(shiftable));
		// DEBUG-X-END
		if(scanners.cardinality() == 1) return simpleDetScan(whence,scanners,shiftable,layouts);
		else return simpleDualScan(whence,scanners,shiftable,layouts);
	}
	
	protected SingleDFAMatchData simpleDetScan(InputPosition whence,
			                                  BitSet scanner,
			                                  BitSet shiftable,
			                                  LinkedList<SingleDFAMatchData> layouts)
	throws IOException
	{
		//System.err.println("Simple-shiftable " + bitVecToString(shiftable));
		int activeScanner = scanner.nextSetBit(0);
		int currentState = scannerStartStates[activeScanner];
		char symbol = '\0';
		InputPosition p;
		BitSet shiftableS = shiftable.get(0,TERMINAL_COUNT);
		BitSet present = newBitVec(TERMINAL_COUNT);
		InputPosition presentPos = whence;
		for(p = InputPosition.copy(whence);;p = InputPosition.advance(p,symbol))
		{
			BitSet tP = getPossibleSet(activeScanner,currentState).get(0,TERMINAL_COUNT);
			tP.and(shiftableS);
			if(p.equals(whence) && shiftableS.get(EOF_SYMNUM)) tP.set(EOF_SYMNUM);
			if(tP.isEmpty()) break;
			shiftableS.and(tP);
			BitSet tA = getAcceptSet(activeScanner,currentState).get(0,TERMINAL_COUNT);
			tA.and(shiftableS);
			if(!tA.isEmpty())
			{
				present = tA;
				presentPos = InputPosition.copy(p);
			}
			else
			{
				BitSet tR = getRejectSet(activeScanner,currentState).get(0,TERMINAL_COUNT);
				tR.and(shiftableS);
				if(!tR.isEmpty())
				{
					present.clear();
					presentPos = whence;
				}
			}
			symbol = buffer.charAt(p.getPos());
			if(symbol == ScannerBuffer.EOFIndicator)
			{
				break;
			}
			currentState = transition(activeScanner,currentState,symbol);
		}
		if(symbol == ScannerBuffer.EOFIndicator &&
				   p.equals(whence) &&
				   shiftableS.get(EOF_SYMNUM))
		{
			present.set(EOF_SYMNUM);
			return new SingleDFAMatchData(present,whence,p,"",layouts);
		}
		else return new SingleDFAMatchData(present,whence,presentPos,buffer.readStringFromBuffer(whence.getPos(),presentPos.getPos()),layouts);
	}

	protected SingleDFAMatchData simpleDualScan(InputPosition whence,
            BitSet scanners,
            BitSet shiftable,
            LinkedList<SingleDFAMatchData> layouts)
	throws IOException
	{
		//System.err.println("Simple-shiftable " + shiftable);
		int activeScanner1 = scanners.nextSetBit(0);
		int activeScanner2 = scanners.nextSetBit(activeScanner1 + 1);
		int currentState1 = scannerStartStates[activeScanner1];
		int currentState2 = scannerStartStates[activeScanner2];
		char symbol = '\0';
		InputPosition p;
		BitSet shiftableS = shiftable.get(0,TERMINAL_COUNT);
		BitSet present = newBitVec(TERMINAL_COUNT);
		InputPosition presentPos = whence;
		for(p = InputPosition.copy(whence);;p = InputPosition.advance(p,symbol))
		{
			// DEBUG-X-BEGIN
			//System.err.println("States: " + currentState1 + "," + currentState2 + "; position: " + p);
			// DEBUG-X-END
			BitSet tP1 = (currentState1 == -1) ? new BitSet() : getPossibleSet(activeScanner1,currentState1).get(0,TERMINAL_COUNT);
			BitSet tP2 = (currentState2 == -1) ? new BitSet() : getPossibleSet(activeScanner2,currentState2).get(0,TERMINAL_COUNT);
			// DEBUG-X-BEGIN
			//System.err.println("Possibles: " + bitVecToString(tP1) + "," + bitVecToString(tP2));
			// DEBUG-X-END
			BitSet tPUnion = new BitSet();
			tP1.and(shiftableS);
			tP2.and(shiftableS);
			tPUnion.or(tP1);
			tPUnion.or(tP2);
			if(p.equals(whence) && shiftableS.get(EOF_SYMNUM))
			{
				tP1.set(EOF_SYMNUM);
			}
			if(tPUnion.isEmpty()) break;
			shiftableS.and(tPUnion);
			BitSet tA1 = (currentState1 == -1) ? new BitSet() : getAcceptSet(activeScanner1,currentState1).get(0,TERMINAL_COUNT);
			BitSet tA2 = (currentState2 == -1) ? new BitSet() : getAcceptSet(activeScanner2,currentState2).get(0,TERMINAL_COUNT);
			BitSet tAUnion = new BitSet();
			tA1.and(shiftableS);
			tA2.and(shiftableS);
			tAUnion.or(tA1);
			tAUnion.or(tA2);
			
			BitSet tR1 = (currentState1 == -1) ? new BitSet() : getRejectSet(activeScanner1,currentState1).get(0,TERMINAL_COUNT);
			BitSet tR2 = (currentState2 == -1) ? new BitSet() : getRejectSet(activeScanner2,currentState2).get(0,TERMINAL_COUNT);
			BitSet tRUnion = new BitSet();
			tR1.and(shiftableS);
			tR2.and(shiftableS);
			tRUnion.or(tR1);
			tRUnion.or(tR2);
			
			if(!tAUnion.isEmpty())
			{
				if(tA1.isEmpty()) present = tA2;
				else if(tA2.isEmpty()/* || tA1.equals(tA2)*/) present = tA1;
				else
				{
					// DEBUG-BEGIN
					System.err.println("Ambiguity across dual scanners: " + bitVecToString(tA1) + " vs. " + bitVecToString(tA2));
					// DEBUG-END
					present = tAUnion;
				}
				presentPos = InputPosition.copy(p);
			}
			else
			{
				if(!tRUnion.isEmpty())
				{
					present.clear();
					presentPos = whence;
				}
			}
			symbol = buffer.charAt(p.getPos());
			if(symbol == ScannerBuffer.EOFIndicator)
			{
				break;
			}
			currentState1 = tP1.isEmpty() ? -1 : transition(activeScanner1,currentState1,symbol);
			currentState2 = tP2.isEmpty() ? -1 : transition(activeScanner2,currentState2,symbol);
		}
		// DEBUG-X-BEGIN
		//System.err.println("Matching " + bitVecToString(present));
		// DEBUG-X-END
		if(symbol == ScannerBuffer.EOFIndicator &&
		   p.equals(whence) &&
		   shiftableS.get(EOF_SYMNUM))
		{
			present.set(EOF_SYMNUM);
			return new SingleDFAMatchData(present,whence,p,"",layouts);
		}
		else return new SingleDFAMatchData(present,whence,presentPos,buffer.readStringFromBuffer(whence.getPos(),presentPos.getPos()),layouts);
	}

	
	protected void startEngine(InputPosition initialPos)
    throws IOException,EXCEPT
	{
		parseStack = new Stack<SingleDFAParseStackNode>();
		parseStack.push(new SingleDFAParseStackNode(PARSER_START_STATENUM,initialPos,null));
		virtualLocation = new VirtualLocation(initialPos.getFileName(),1,0);
		currentState = null;
		lastPosition = null;
		lastMatched = null;
		lastShiftable = null;
		scanResult = null;
	}
	
	protected Object runEngine()
	throws IOException,EXCEPT
	{
		while(true)
		{
			currentState = parseStack.peek();
			SingleDFAMatchData scanResult = layoutScan(false);
			if(scanResult.terms.isEmpty())
			{
				SingleDFAMatchData disjointMatch = layoutScan(true);
				reportError(formatError("Expected a token of the following types:\n" + bitVecToString(shiftableSets[currentState.statenum]) + "\n   Input currently matches:\n" + bitVecToString(disjointMatch.terms)));
			}
			else if(scanResult.terms.cardinality() > 1)
			{
				int disambiguatedTerm = runDisambiguationAction(currentState.pos,scanResult);
				if(disambiguatedTerm == -1)
				{
					int firstActionIndex = scanResult.firstTerm;
					int action = getParseTableAction(currentState.statenum,firstActionIndex);
					if(actionType(action) == STATE_REDUCE) disambiguatedTerm = firstActionIndex;
					
					for(int i = scanResult.terms.nextSetBit(firstActionIndex + 1);i >= 0;i = scanResult.terms.nextSetBit(i + 1))
					{
						if(action != getParseTableAction(currentState.statenum,i))
						{
							disambiguatedTerm = -1;
							break;
						}
					}
				}
				if(disambiguatedTerm == -1)
				{
					reportError(formatError("Lexical ambiguity between tokens:\n" + bitVecToString(scanResult.terms)));
				}
				else
				{
					scanResult.terms = newBitVec(TERMINAL_COUNT,disambiguatedTerm);
					scanResult.firstTerm = disambiguatedTerm;
				}
			}
			int action = getParseTableAction(currentState.statenum,scanResult.firstTerm);
			Object synthAttr;
			switch(actionType(action))
			{
			case STATE_ACCEPT:
				return parseStack.peek().synthAttr;
			case STATE_SHIFT:
				int nextState = actionIndex(action);
				for(SingleDFAMatchData layout : scanResult.layouts)
				{
					runSemanticAction(layout.precedingPos,layout);
					virtualLocation.defaultUpdateAutomatic(layout.lexeme);
				}
				synthAttr = runSemanticAction(scanResult.precedingPos,scanResult);
				virtualLocation.defaultUpdateAutomatic(scanResult.lexeme);
				parseStack.push(new SingleDFAParseStackNode(nextState,scanResult.followingPos,synthAttr));
				break;
			case STATE_REDUCE:
				int production = actionIndex(action);
				int productionLength = actionIndex(symbolNumbers[production]);
				int productionLHS = actionIndex(productionLHSs[production - GRAMMAR_SYMBOL_COUNT]);
				Object[] children = new Object[productionLength];
				for(int i = productionLength - 1;i >= 0;i--)
				{
					children[i] = parseStack.pop().synthAttr;
				}
				int gotoState = actionIndex(getParseTableAction(parseStack.peek().statenum,productionLHS));
				synthAttr = runSemanticAction(currentState.pos,children,production);
				parseStack.push(new SingleDFAParseStackNode(gotoState,currentState.pos,synthAttr));
				break;
			default:
				reportError(formatError("Cannot locate an action --- bug in parser"));
			}
		}
	}
}
