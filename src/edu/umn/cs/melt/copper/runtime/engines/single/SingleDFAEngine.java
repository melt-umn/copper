package edu.umn.cs.melt.copper.runtime.engines.single;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Stack;

import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;
import edu.umn.cs.melt.copper.runtime.engines.CopperParser;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;

/**
 * An engine containing mechanisms of both parser and scanner, following the
 * single-DFA algorithm of context-aware scanning, built with a minimum of
 * auxiliary classes and a maximum of functionality implemented in arrays and
 * bit sets.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public abstract class SingleDFAEngine<ROOT,EXCEPT extends Exception> implements CopperParser<ROOT,EXCEPT>
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
		return PrettyPrinter.bitSetPrettyPrint(vec,getSymbolDisplayNames(),"   ",1);
	}
	public ArrayList<String> bitVecToRealStringList(BitSet vec)
	{
		ArrayList<String> stringList = new ArrayList<String>();
		for(int i = vec.nextSetBit(0);i >= 0;i = vec.nextSetBit(i+1))
		{
			stringList.add(getSymbolNames()[i]);
		}
		return stringList;
	}
	public ArrayList<String> bitVecToDisplayStringList(BitSet vec)
	{
		ArrayList<String> stringList = new ArrayList<String>();
		for(int i = vec.nextSetBit(0);i >= 0;i = vec.nextSetBit(i+1))
		{
			stringList.add(getSymbolDisplayNames()[i]);
		}
		return stringList;
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
	
	/* Identifier tags for terminals. */
	public static final int TERMINAL_VERSATILE = 0;
	public static final int TERMINAL_EXCLUSIVELY_LAYOUT = 1;
	public static final int TERMINAL_EXCLUSIVELY_PREFIX = 2;
	public static final int TERMINAL_EXCLUSIVELY_SHIFTABLE = 4;
	public static final int TERMINAL_UNUSED = 7;

	public abstract int getTERMINAL_COUNT();
	public abstract int getGRAMMAR_SYMBOL_COUNT();
	public abstract int getSYMBOL_COUNT();
	public abstract int getPARSER_STATE_COUNT();
	public abstract int getSCANNER_STATE_COUNT();
	public abstract int getDISAMBIG_GROUP_COUNT();
	public abstract int getSCANNER_START_STATENUM();
	public abstract int getPARSER_START_STATENUM();
	public abstract int getEOF_SYMNUM();
	public abstract int getEPS_SYMNUM();
	public abstract String[] getSymbolNames();
	public abstract String[] getSymbolDisplayNames();
	public abstract int[] getSymbolNumbers();
	public abstract int[] getProductionLHSs();
	public abstract int[][] getParseTable();
	public abstract BitSet[] getShiftableSets();
	public abstract BitSet[] getLayoutSets();
	public abstract BitSet[] getPrefixSets();
	public abstract int[] getTerminalUses();
	public abstract BitSet[][] getLayoutMaps();
	public abstract BitSet[][] getPrefixMaps();
	public abstract BitSet[] getDisambiguationGroups();
	public abstract BitSet getShiftableUnion();
	public abstract BitSet[] getAcceptSets();
	public abstract BitSet[] getRejectSets();
	public abstract BitSet[] getPossibleSets();
	public abstract int[][] getDelta();
	public abstract int[] getCmap();
	

	/** Functions for determining character ranges. */
    protected static boolean cheq(char input,char single)
    {
        return (input == single);
    }
    protected static boolean chin(char input,char min,char max)
    {
        return (input >= min && input <= max);
    }

	protected abstract int transition(int state,char ch);
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
    protected boolean functionalDisambiguationUsed;
    
    protected SingleDFAMatchData disjointMatch;
    
    protected abstract String formatError(String error);

    protected abstract void reportError(String error) throws EXCEPT;
    protected abstract void reportSyntaxError() throws EXCEPT;
    

	protected SingleDFAMatchData layoutScan(boolean runDisjoint)
	throws IOException,EXCEPT
	{
		BitSet shiftable = getShiftableSets()[currentState.statenum];
		InputPosition whence = currentState.pos;
		buffer.advanceBufferTo(whence.getPos());
		if(!runDisjoint && whence.equals(lastPosition))
		{
			if(lastMatched != null && !functionalDisambiguationUsed)
			{
				if(lastMatched.terms.get(getEOF_SYMNUM()) || lastMatched.isEmpty())
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
				else
				{
					return lastMatched;
				}
			}
		}

		lastPosition = whence;
		lastShiftable = shiftable;
		functionalDisambiguationUsed = false;
		
		LinkedList<SingleDFAMatchData> layouts = new LinkedList<SingleDFAMatchData>();
		SingleDFAMatchData finalMatches;

		//for(int i = 0;;i++)
		for(;;)
		{
			// DEBUG-X-BEGIN
			//System.err.println("Parse state " + currentState.statenum + "; iteration " + i + "; location " + whence + " (char " + whence.getPos() + "); runDisjoint = " + runDisjoint + "; shiftable " + bitVecToString(shiftable) + " --");
			// DEBUG-X-END
			finalMatches = maybeDisjointScan(whence,shiftable,layouts,runDisjoint);
			
			if(finalMatches.terms.cardinality() > 1)
			{
				// DEBUG-BEGIN
				//System.err.println("Ambiguity: " + bitVecToString(finalMatches.terms) + "; runDisjoint = " + runDisjoint);
				// DEBUG-END
				if(finalMatches.terms.get(getEOF_SYMNUM()) && finalMatches.lexeme.isEmpty())
				{
					finalMatches.terms.clear();
					finalMatches.terms.set(getEOF_SYMNUM());
					finalMatches.firstTerm = getEOF_SYMNUM();
				}
				else
				{
					functionalDisambiguationUsed = true;
					int disambiguatedTerm = runDisambiguationAction(currentState.pos,finalMatches);
					if(disambiguatedTerm == -1)
					{
						int firstActionIndex = finalMatches.firstTerm;
						int action = getParseTable()[currentState.statenum][firstActionIndex];
						if(actionType(action) == STATE_REDUCE) disambiguatedTerm = firstActionIndex;
						
						for(int j = finalMatches.terms.nextSetBit(firstActionIndex + 1);j >= 0;j = finalMatches.terms.nextSetBit(j + 1))
						{
							if(action != getParseTable()[currentState.statenum][j])
							{
								disambiguatedTerm = -1;
								break;
							}
						}
					}
					if(disambiguatedTerm == -1)
					{
						if(!runDisjoint)
						{
							reportError(formatError("Lexical ambiguity between tokens:\n" + bitVecToString(finalMatches.terms)));
							return finalMatches;
						}
					}
					else
					{
						finalMatches.terms.clear();
						finalMatches.terms.set(disambiguatedTerm);
						finalMatches.firstTerm = disambiguatedTerm;
					}
				}
				
				if(finalMatches.terms.cardinality() > 1) return finalMatches;
			}
			
			if(/*layouts.isEmpty() &&*/ finalMatches.terms.isEmpty())
			{
				// DEBUG-X-BEGIN
				//System.err.println("No matches");
				// DEBUG-X-END
				break;
			}
			else if(runDisjoint &&
					!shiftable.get(finalMatches.firstTerm))
			{
				break;
			}
			
			int useAs = getTerminalUses()[finalMatches.firstTerm];
			if(useAs == TERMINAL_VERSATILE)
			{
				if(getLayoutSets()[currentState.statenum].get(finalMatches.firstTerm)) useAs = TERMINAL_EXCLUSIVELY_LAYOUT;
				else if(getPrefixSets()[currentState.statenum].get(finalMatches.firstTerm)) useAs = TERMINAL_EXCLUSIVELY_PREFIX;
				else useAs = TERMINAL_EXCLUSIVELY_SHIFTABLE;
			}
			
			switch(useAs)
			{
			case TERMINAL_EXCLUSIVELY_SHIFTABLE:
				// DEBUG-X-BEGIN
				// System.err.println("Shiftable match");
				// DEBUG-X-END
				if(!finalMatches.terms.isEmpty()) lastMatched = finalMatches;
				return finalMatches;
			case TERMINAL_EXCLUSIVELY_LAYOUT:
				// DEBUG-X-BEGIN
				// System.err.println("Layout match");
				// DEBUG-X-END
				whence = InputPosition.advance(whence,finalMatches.lexeme.length(),finalMatches.lexeme);
				if(finalMatches.lexeme.length() == 0)
				{
					//System.err.println("Empty layout match");
					if(layouts.isEmpty())
					{
						layouts.add(finalMatches);
					}
					shiftable = (BitSet) shiftable.clone();
					shiftable.andNot(getLayoutSets()[currentState.statenum]);
				}
				else
				{
					layouts.add(finalMatches);
					continue;
				}
				break;
			case TERMINAL_EXCLUSIVELY_PREFIX:
				// DEBUG-X-BEGIN
				// System.err.println("Prefix match");
				// DEBUG-X-END
				layouts.add(finalMatches);
				shiftable = getPrefixMaps()[currentState.statenum][finalMatches.firstTerm];
				whence = InputPosition.advance(whence,finalMatches.lexeme.length(),finalMatches.lexeme);
				break;
			default:
				reportError(formatError("Cannot determine whether terminal is layout, prefix, or shiftable --- bug in scanner"));
			}
		}
		if(!finalMatches.terms.isEmpty()) lastMatched = finalMatches;
		return finalMatches;
	}
	
	protected SingleDFAMatchData maybeDisjointScan(InputPosition whence,
			                           BitSet shiftable,
			                           LinkedList<SingleDFAMatchData> layouts,
			                           boolean runDisjoint)
	throws IOException
	{
		//System.err.println("Run disjoint: " + runDisjoint);
		if(!runDisjoint) return simpleScan(whence,shiftable,layouts);
		else return simpleScan(whence,getShiftableUnion(),layouts);
	}
	
	protected SingleDFAMatchData simpleScan(InputPosition whence,
			                    BitSet shiftable,
			                    LinkedList<SingleDFAMatchData> layouts)
	throws IOException
	{
		//System.err.println("Simple-shiftable " + shiftable);
		int currentState = getSCANNER_START_STATENUM();
		char symbol = '\0';
		InputPosition p;
		BitSet shiftableS = (BitSet) shiftable.clone();
		BitSet present = newBitVec(getTERMINAL_COUNT());
		InputPosition presentPos = whence;
		for(p = InputPosition.copy(whence);;p = InputPosition.advance(p,symbol))
		{
			BitSet tP = (BitSet) getPossibleSets()[currentState].clone();
			tP.and(shiftableS);
			if(p.equals(whence) && shiftableS.get(getEOF_SYMNUM())) tP.set(getEOF_SYMNUM());
			if(tP.isEmpty()) break;
			shiftableS.and(tP);
			BitSet tA = (BitSet) getAcceptSets()[currentState].clone();
			tA.and(shiftableS);
			if(!tA.isEmpty())
			{
				present = tA;
				presentPos = InputPosition.copy(p);
			}
			else
			{
				BitSet tR = (BitSet) getRejectSets()[currentState].clone();
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
			currentState = transition(currentState,symbol);
		}
		if(symbol == ScannerBuffer.EOFIndicator &&
				   p.equals(whence) &&
				   shiftableS.get(getEOF_SYMNUM()))
		{
			present.set(getEOF_SYMNUM());
			return new SingleDFAMatchData(present,whence,p,"",layouts);
		}
		else return new SingleDFAMatchData(present,whence,presentPos,buffer.readStringFromBuffer(whence.getPos(),presentPos.getPos()),layouts);
	}
	
	protected void startEngine(InputPosition initialPos)
    throws IOException,EXCEPT
	{
		functionalDisambiguationUsed = false;
		parseStack = new Stack<SingleDFAParseStackNode>();
		parseStack.push(new SingleDFAParseStackNode(getPARSER_START_STATENUM(),initialPos,null));
		virtualLocation = new VirtualLocation(initialPos.getFileName(),1,0);
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
				disjointMatch = layoutScan(true);
				for(SingleDFAMatchData layout : scanResult.layouts)
				{
					runSemanticAction(layout.precedingPos,layout);
					virtualLocation.defaultUpdateAutomatic(layout.lexeme);
				}
				reportSyntaxError();
			}
			else if(scanResult.terms.cardinality() > 1)
			{
				// This should not happen.
				System.err.println("Ambiguous match");
			}
			// DEBUG-X-BEGIN
			//System.err.println(bitVecToString(scanResult.terms));
			// DEBUG-X-END
			int action = getParseTable()[currentState.statenum][scanResult.firstTerm];
			Object synthAttr;
			switch(actionType(action))
			{
			case STATE_ACCEPT:
				for(SingleDFAMatchData layout : scanResult.layouts)
				{
					runSemanticAction(layout.precedingPos,layout);
					virtualLocation.defaultUpdateAutomatic(layout.lexeme);
				}
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
				int productionLength = actionIndex(getSymbolNumbers()[production]);
				int productionLHS = actionIndex(getProductionLHSs()[production - getGRAMMAR_SYMBOL_COUNT()]);
				Object[] children = new Object[productionLength];
				for(int i = productionLength - 1;i >= 0;i--)
				{
					children[i] = parseStack.pop().synthAttr;
				}
				int gotoState = actionIndex(getParseTable()[parseStack.peek().statenum][productionLHS]);
				synthAttr = runSemanticAction(currentState.pos,children,production);
				parseStack.push(new SingleDFAParseStackNode(gotoState,currentState.pos,synthAttr));
				break;
			default:
				System.err.println(bitVecToString(scanResult.terms));
				disjointMatch = scanResult;
				reportSyntaxError();
			}
		}
	}
}
