package edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr;

import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.ParseEngine;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.ParserState;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner.QScanner;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.scanner.QScannerMatchData;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.AcceptAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.FullReduceAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ParseAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ParseActionVisitor;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ShiftAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetree.plain.ParseTree;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetree.plain.ParseTreeNode;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetree.plain.ParseTreeProdNode;
import edu.umn.cs.melt.copper.legacy.compiletime.statistics.ParserStatistics;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;
import edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes;
import edu.umn.cs.melt.copper.runtime.engines.semantics.VirtualLocation;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public abstract class LALREngine extends ParseEngine implements ParseActionVisitor<Boolean,CopperException>
{
	protected Stack< Pair<ParserState,Object> > parseStack;
	protected VirtualLocation virtualLocation;
	protected int startStatenum;
	protected QScanner scanner;
	protected CompilerLogger logger;
	
	private ParserState currentState;
	private ParserStatistics statistics;
	private boolean gatherStatistics;
	private QScannerMatchData lookahead;
	private ParseAction action;
	private ParseTree parseTree;
	private boolean accepted;
	
	public abstract Object runSemanticAction(InputPosition _pos,Object[] children,Production _prod)
	throws IOException,CopperException;
	
	public abstract Object runSemanticAction(InputPosition _pos,QScannerMatchData _terminal)
	throws IOException,CopperException;

	public abstract QScannerMatchData runDisambiguationAction(InputPosition _pos,HashSet<QScannerMatchData> matches)
	throws IOException,CopperException;
	
	public abstract SpecialParserAttributes getSpecialAttributes();
	
	public void setupEngine() {}
	
	public void startStatisticGathering()
	{
		statistics = new ParserStatistics(scanner.retrieveStatistics());
		statistics.totalParserStates = 0;
		for(int statenum = 0;statenum <= getParseTable().getLastState();statenum++) statistics.totalParserStates = Math.max(statenum,statistics.totalParserStates);
		gatherStatistics = true;
	}
		
	public void startEngine(InputPosition initialPos)
	throws IOException,CopperException
	{
		if(logger.isLoggable(CompilerLogMessageSort.DEBUG)) startStatisticGathering();
		else
		{
			statistics = new ParserStatistics(scanner.retrieveStatistics());
			gatherStatistics = false;
		}
		accepted = false;
		parseTree = new ParseTree();
		parseStack = new Stack< Pair<ParserState,Object> >();
		parseStack.push(Pair.cons(new ParserState(startStatenum,initialPos),(Object) null));
		virtualLocation = new VirtualLocation(initialPos.getFileName(),1,0);
	}
	
	private void reportError(String error)
	throws CopperException
	{
		if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logParsingErrorMessage(virtualLocation,currentState.getStatenum(),currentState.getPos().getPos(),error);
	}
	
	public ParserStatistics retrieveStatistics()
	{
		return statistics;
	}
	
	public Object runEngine()
	throws IOException,CopperException
	{
		while(true)
		{
			currentState = parseStack.peek().first();
			if(gatherStatistics)
			{
				if(!statistics.parserStatesVisited.containsKey(currentState.getStatenum()))
				{
					statistics.parserStatesVisited.put(currentState.getStatenum(),0);
				}
				statistics.parserStatesVisited.put(currentState.getStatenum(),statistics.parserStatesVisited.get(currentState.getStatenum()) + 1);
			}
			// DEBUG-X-BEGIN
			//System.err.print("State " + currentState + ", ");
			// DEBUG-X-END
			if(!getParseTable().hasShiftable(currentState.getStatenum()))
			{
				if(logger.isLoggable(CompilerLogMessageSort.FATAL_ERROR)) logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,null,"No actions whatsoever listed for state " + currentState);
				return null;
			}
			HashSet<QScannerMatchData> possibleShifts = scanner.runLayoutScan(currentState,getParseTable());
			if(possibleShifts.size() == 0)
			{
				String errorString = "Expected a token of one of the following types:\n";
				Iterable<Terminal> shiftable = getParseTable().getShiftable(currentState.getStatenum());
				errorString += PrettyPrinter.iterablePrettyPrint(shiftable,"   ",1);
				errorString += "\n  Input currently matches:\n";
				// DEBUG-X-BEGIN
				//System.err.println("Starting disjoint-scan");
				// DEBUG-X-END
				HashSet<QScannerMatchData> universalMatches = scanner.runDisjointScan(currentState,getParseTable());
				HashSet<Terminal> universalMatchesS = new HashSet<Terminal>();
				for(QScannerMatchData match : universalMatches) universalMatchesS.add(match.getToken());
				errorString += PrettyPrinter.iterablePrettyPrint(universalMatchesS,"   ",1);
				reportError(errorString);
				return null;
			}
			else if(possibleShifts.size() == 1)
			{
				lookahead = possibleShifts.iterator().next();
			}
			else // if(possibleShifts.size() > 1)
			{
				lookahead = runDisambiguationAction(currentState.getPos(),possibleShifts);
				// Disregard ambiguities that all indicate a single reduce action.
				if(lookahead == null)
				{
					HashSet<ParseAction> actions = new HashSet<ParseAction>();
					for(QScannerMatchData data : possibleShifts)
					{
						for(ParseAction a : getParseTable().getParseActions(currentState.getStatenum(),data.getToken()))
						{
							actions.add(a);
							if(actions.size() > 1) break;
						}
						if(actions.size() > 1) break;
					}
					if(actions.size() == 1 && actions.iterator().next() instanceof FullReduceAction)
					{
						lookahead = possibleShifts.iterator().next();
					}
				}
				if(lookahead == null)
				{
					if(logger.isLoggable(CompilerLogMessageSort.FATAL_ERROR)) logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,currentState.getPos(),"Lexical ambiguity between tokens: " + possibleShifts);
					return null;
				}
			}
			
			// DEBUG-X-BEGIN
			//System.err.println("token " + lookahead);
			// DEBUG-X-END
			
			// This should NEVER happen.
			if(!getParseTable().hasAction(currentState.getStatenum(),lookahead.getToken()))
			{
				if(logger.isLoggable(CompilerLogMessageSort.FATAL_ERROR)) logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,currentState.getPos(),"Unexpected token " + lookahead);
				return null;
			}
			action = getParseTable().getParseAction(currentState.getStatenum(),lookahead.getToken());
			// DEBUG-X-BEGIN
			//System.err.println("action " + action);
			// DEBUG-X-END
			action.acceptVisitor(this);
			if(accepted) break;
		}
		if(parseStack.peek().second() instanceof ParseTreeNode) return parseTree;
		else return parseStack.peek().second();
	}
	
	public Boolean visitAcceptAction(AcceptAction action)
	throws CopperException
	{
		accepted = true;
		return true;
	}
	
	public Boolean visitFullReduceAction(FullReduceAction action)
	throws CopperException
	{
		if(action.getProd().length() >= parseStack.size())
		{
			if(logger.isLoggable(CompilerLogMessageSort.FATAL_ERROR)) logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,currentState.getPos(),"Unable to perform reduction: stack too short");
			return false;
		}
		Object[] children = new Object[action.getProd().length()];
		for(int i = action.getProd().length() - 1;i >= 0;i--)
		{
			children[i] = parseStack.pop().second();
		}
		if(!getParseTable().hasGotoAction(parseStack.peek().first().getStatenum(),action.getProd().getLeft()))
		{
			if(logger.isLoggable(CompilerLogMessageSort.FATAL_ERROR)) logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,currentState.getPos(),"Unable to perform reduction: no goto action available in parse table cell (" + parseStack.peek().first().getStatenum() + "," + action.getProd().getLeft());
			return false;
		}
		ShiftAction gotoAction = getParseTable().getGotoAction(parseStack.peek().first().getStatenum(),action.getProd().getLeft());
		// DEBUG-X-BEGIN
		//System.err.println("Reducing production " + action.getProd());
		// DEBUG-X-END
		Object newNode = null;
		// Will not work with semantic actions that change the lexeme length.
		try { newNode = runSemanticAction(currentState.getPos(),children,action.getProd()); }
		catch(IOException ex)
		{
			reportError("I/O error in semantic action: " + ex.getMessage());
		}
		if(newNode != null && newNode.equals("PARSETREE"))
		{
			ParseTreeNode[] newChildren = new ParseTreeNode[children.length];
			for(int i = 0;i < children.length;i++) newChildren[i] = (ParseTreeNode) children[i];
			ParseTreeProdNode newNodePTPN = parseTree.prodNode(action.getProd(),newChildren); 
			newNode = newNodePTPN;
			parseTree.setRoot(newNodePTPN);
		}
		parseStack.push(Pair.cons(new ParserState(gotoAction.getDestState(),currentState.getPos()),newNode));
		return true;
	}
	
	public Boolean visitShiftAction(ShiftAction action)
	throws CopperException
	{
		for(QScannerMatchData layout : lookahead.getLayouts())
		{
			try
			{
				runSemanticAction(layout.getPositionPreceding(),layout);
			}
			catch(IOException ex)
			{
				reportError("I/O error in semantic action: " + ex.getMessage());
			}
			virtualLocation.defaultUpdateAutomatic(layout.getToken().getLexeme());
		}
		Object newNode = null;
		try
		{
			newNode = runSemanticAction(lookahead.getPositionPreceding(),lookahead);
		}
		catch(IOException ex)
		{
			reportError("I/O error in semantic action: " + ex.getMessage());
		}
		Terminal lookaheadTok;
		InputPosition positionPreceding,positionFollowing;
		if(getSpecialAttributes().latchLocation) lookaheadTok = lookahead.getToken().newLexeme("");
		else lookaheadTok = lookahead.getToken();
		positionPreceding = currentState.getPos();
		if(getSpecialAttributes().latchLocation)
		{
			positionFollowing = InputPosition.copy(currentState.getPos());
		}
		else positionFollowing = lookahead.getPositionFollowing();
		virtualLocation.defaultUpdateAutomatic(lookaheadTok.getLexeme());
		if(newNode != null && newNode.equals("PARSETREE"))
		{
			newNode = parseTree.termNode(lookaheadTok,positionPreceding/*,positionFollowing*/,new VirtualLocation(virtualLocation));
			parseTree.setRoot((ParseTreeNode) newNode);
		}
		parseStack.push(Pair.cons(new ParserState(action.getDestState(),positionFollowing),newNode));
		return true;
	}
}
