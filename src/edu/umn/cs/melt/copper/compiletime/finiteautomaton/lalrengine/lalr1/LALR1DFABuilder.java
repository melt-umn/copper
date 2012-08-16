package edu.umn.cs.melt.copper.compiletime.finiteautomaton.lalrengine.lalr1;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarName;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.OperatorAttributes;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.ProductionAttributes;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.TerminalClass;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController;
import edu.umn.cs.melt.copper.compiletime.auxiliary.ParseActionPrettyPrinter;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.AcceptAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.FullReduceAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.GLRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.ParseAction;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.ShiftAction;
import edu.umn.cs.melt.copper.compiletime.semantics.lalr1.ComposabilityChecker;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;


/**
 * Contains code for building a LALR(1) DFA, by first building an LR(0) DFA
 * and then adding lookahead information and layout maps.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LALR1DFABuilder
{
	private GrammarSource grammar;
	private Hashtable<LALR1State,LALR1State> memoizedLR0Seeds;
	private HashSet<GrammarName> wantedGrammars;
	private LALR1DFA dfaLALR1;
	private LALR1State primeState;
	private LALR1StateItem primeItem;
	private GLRParseTable parseTable;
	
	private CompilerLogger logger;
	
	public LALR1DFABuilder(GrammarSource grammar,Iterable<GrammarName> wantedGrammars,CompilerLogger logger)
	{
		this.grammar = grammar;
		dfaLALR1 = new LALR1DFA();
		parseTable = new GLRParseTable();
		memoizedLR0Seeds = new Hashtable<LALR1State,LALR1State>();
		this.wantedGrammars = new HashSet<GrammarName>();
		for(GrammarName gn : wantedGrammars) this.wantedGrammars.add(gn);
		
		this.logger = logger;
	}
	
	private boolean isWanted(GrammarSymbol gs) { return ComposabilityChecker.isWanted(grammar,wantedGrammars,gs); }
	private boolean isWanted(Terminal t) { return ComposabilityChecker.isWanted(grammar,wantedGrammars,t); }
	private boolean isWanted(NonTerminal nt) { return ComposabilityChecker.isWanted(grammar,wantedGrammars,nt); }
	// This is only supposed to let through host-to-extension bridge productions.
	private boolean isWanted(LALR1StateItem item) { return ComposabilityChecker.isWanted(grammar,wantedGrammars,item); }
	
	private	void insertShiftActions(LALR1DFA dfa)
	{
		// For every transition in the DFA:
		for(LALR1State state : dfa.getStates()) for(LALR1Transition transition : dfa.getTransitions(state))
		{
			// If the label is a terminal, add a shift action to the table.
			if(transition.getLabel() instanceof Terminal &&
			   isWanted((Terminal) transition.getLabel()))
			{
				parseTable.addAction(dfa.getLabel(transition.getSrc()),
						             (Terminal) transition.getLabel(),
						             new ShiftAction(dfa.getLabel(transition.getDest())));
			}
			// If the label is a nonterminal, add a goto action to the table.
			else if(transition.getLabel() instanceof NonTerminal &&
				   isWanted((NonTerminal) transition.getLabel()))
			{
				parseTable.addGotoAction(dfa.getLabel(transition.getSrc()),
						                 (NonTerminal) transition.getLabel(),
						                 new ShiftAction(dfa.getLabel(transition.getDest())));
			}
		}		
	}
	
	public void buildLALR1Table()
	throws CopperException
	{
		parseTable = new GLRParseTable();
		insertShiftActions(dfaLALR1);
		// For every state in the DFA:
		for(LALR1State state : dfaLALR1.getStates())
		{
			// DEBUG-BEGIN
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			// DEBUG-END
			// For every item in that state:
			for(LALR1StateItem item : state.getItems())
			{
				// If an item is the end-item, put in an accept action for the EOF-symbol.
				if(item.isEndItem() && item.getSymbolAtPosition() instanceof Terminal)
				{
					parseTable.addAction(dfaLALR1.getLabel(state),(Terminal) item.getSymbolAtPosition(),new AcceptAction());
				}
				// Check whether all symbols after the dot in a production
				// are nullable.
				boolean isNullable = true;
				if(!item.isReducible() && !grammar.getContextSets().nullableContains(item.getSymbolAtPosition()))
				{
					isNullable = false;
				}
				else for(GrammarSymbol sym : item.getSymbolsAfterPosition())
				{
					if(!grammar.getContextSets().nullableContains(sym))
					{
						isNullable = false;
						break;
					}
				}
				// If they are all nullable, then for every layout-to-lookahead
				// mapping in the "lookahead layout" (indicating what layout
				// tokens may precede what lookahead) put it in the table.
				if(isNullable)
				{
					for(Terminal layout : dfaLALR1.getLookaheadLayout(state,item).keySet())
					{
						for(Terminal lookahead : dfaLALR1.getLookaheadLayout(state,item).get(layout))
						{
							// DEBUG-X-BEGIN
							//System.err.println("To state " + dfa.getLabel(state) + ", layout " + layout + ", for item " + item + ", adding " + lookahead);
							// DEBUG-X-END
							parseTable.addLayout(dfaLALR1.getLabel(state),layout,lookahead);
						}
					}
				}
				// If an item is reducible:
				if(item.isReducible())
				{
					// For every terminal in the item's lookahead set, put in a reduce action.
					for(Terminal t : dfaLALR1.getLookahead(state,item))
					{
						parseTable.addAction(dfaLALR1.getLabel(state),t,new FullReduceAction(item.getProd()));
					}
				}
				// Elseif an item is at the beginning with a shiftable terminal
				// at the dot:
				else if(item.isBeginning() &&
						 item.getSymbolAtPosition() instanceof Terminal)
				{
					// For each layout symbol that may appear before the beginning
					// of this production, indicate that it may precede the
					// terminal symbol at the beginning of this item.
					for(Terminal layout : dfaLALR1.getBeginningLayout(state,item))
					{
						parseTable.addLayout(
						 dfaLALR1.getLabel(state),
						 layout,
						 (Terminal) item.getSymbolAtPosition());
					}
				}
				// Elseif the position of the item is at an "encapsulated" terminal
				// (at neither beginning nor end) indicate that the layout of this
				// item's production may precede the encapsulated terminal.
				else if(item.isEncapsulated() &&
						(item.getSymbolAtPosition() instanceof Terminal))
				{
					HashSet<Terminal> prodLayouts = grammar.getTerminalLayout(item.getProd());
					for(Terminal prodLayout : prodLayouts)
					{
						parseTable.addLayout(
					     dfaLALR1.getLabel(state),
					     prodLayout,
					     (Terminal) item.getSymbolAtPosition());
					}
				}
			}
			if(!parseTable.hasShiftable(dfaLALR1.getLabel(state)))
			{
				if(logger.isLoggable(CompilerLogMessageSort.STATUS)) logger.logMessage(CompilerLogMessageSort.STATUS,null,"No actions whatsoever listed for state " + dfaLALR1.getLabel(state));
				continue;
			}
			else for(Terminal tok : parseTable.getShiftable(dfaLALR1.getLabel(state)))
			{
				Terminal prefix = grammar.getLexicalAttributes(tok).getTransparentPrefix();
				if(!prefix.equals(FringeSymbols.EMPTY))	parseTable.addPrefix(dfaLALR1.getLabel(state),prefix,tok);
			}
		}
	}
	
	public void cullConflictsLALR1()
	throws CopperException
	{
		cullConflicts();
	}
	
	public void cullConflicts()
	throws CopperException
	{
		// For all states in the parse table:
		for(int statenum : parseTable.getStates())
		{
			// DEBUG-BEGIN
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			// DEBUG-END			
			// For all shiftable tokens in the current state:
			HashSet<Terminal> shiftable = new HashSet<Terminal>();
			if(parseTable.hasShiftable(statenum)) for(Terminal t : parseTable.getShiftable(statenum)) shiftable.add(t);
			
			for(Terminal t : shiftable)
			{
				HashSet<ParseAction> origActions = null;
				if(parseTable.countParseActions(statenum,t) > 1) origActions = new HashSet<ParseAction>(parseTable.getParseActions(statenum,t));
				
				
				// If there is a conflict in the cell:
				// Cull reduce actions by weeding out reduce actions on productions of
				// lower precedence.
				if(parseTable.countParseActions(statenum,t) >= 2)
				{
					HashSet<ParseAction> nonReduceActions = new HashSet<ParseAction>();
					int bestPrecedence = -1;
					TerminalClass precClass = null;
					HashSet<ParseAction> bestPrecedenceActions = new HashSet<ParseAction>();
					for(ParseAction action : parseTable.getParseActions(statenum,t))
					{
						if(action.isShiftAction() || action.isAcceptAction())
						{
							nonReduceActions.add(action);
							continue;
						}
						Production actProd = null;
						actProd = ((FullReduceAction) action).getProd();							
						if(precClass == null) precClass = grammar.getProductionAttributes(actProd).getPrecedenceClass();
						else if(!precClass.equals(grammar.getProductionAttributes(actProd).getPrecedenceClass())) break;
						if(bestPrecedence < grammar.getProductionAttributes(actProd).getPrecedence())
						{
							bestPrecedenceActions.clear();
							bestPrecedenceActions.add(action);
							bestPrecedence = grammar.getProductionAttributes(actProd).getPrecedence();
						}
						else if(bestPrecedence == grammar.getProductionAttributes(actProd).getPrecedence())
						{
							bestPrecedenceActions.add(action);
						}
					}
					if(nonReduceActions.size() + bestPrecedenceActions.size() != parseTable.countParseActions(statenum,t))
					{
						parseTable.clearCell(statenum,t);
						for(ParseAction action : nonReduceActions) parseTable.addAction(statenum,t,action);
						for(ParseAction action : bestPrecedenceActions) parseTable.addAction(statenum,t,action);
						if(parseTable.countParseActions(statenum,t) == 1)
						{
							if(logger.isLoggable(CompilerLogMessageSort.PARSE_TABLE_CONFLICT)) logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),generateParseTableConflictMessage(origActions,parseTable.getParseAction(statenum,t)));
							else logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),"");
						}

					}
				}

				// If there is exactly two actions in the cell:
				// Cull shift/reduce conflicts through use of associativity and
				// precedence rules on operators, as defined within precedence
				// classes.
				if(parseTable.countParseActions(statenum,t) == 2)
				{
					ParseAction shiftAction,reduceAction;
					Iterator<ParseAction> actionIt = parseTable.getParseActions(statenum,t).iterator();
					shiftAction = actionIt.next();
					reduceAction = actionIt.next();
					// If one is an accept or shift action and the other a reduce
					// action:
					if(shiftAction.isAcceptAction() || shiftAction.isShiftAction())
					{
						Terminal shiftSym,reduceSym;
						Production p = null;
						if(reduceAction.isFullReduceAction())
						{
							p = ((FullReduceAction) reduceAction).getProd();
						}
						else continue;
						// Get the token defining the precedence of the production
						// in the reduce action.
						reduceSym = p.getPrecedenceSymbol();
						// If there is no precedence to the production, leave both
						// actions in the table.
						if(reduceSym.equals(FringeSymbols.EMPTY)) continue;
						shiftSym = t;
						OperatorAttributes shiftAttributes = grammar.getOperatorAttributes(shiftSym); 
						OperatorAttributes reduceAttributes = grammar.getOperatorAttributes(reduceSym);
						if(shiftAttributes == null || reduceAttributes == null) continue;
						if((shiftAttributes.getPrecClass() == null ^ reduceAttributes.getPrecClass() == null) ||
						   (shiftAttributes.getOperatorPrecedence() == null ^ reduceAttributes.getOperatorPrecedence() == null) ||
						   !shiftAttributes.getPrecClass().equals(reduceAttributes.getPrecClass())) continue;
						if(shiftAttributes.getOperatorPrecedence() != null &&
						   reduceAttributes.getOperatorPrecedence() != null &&
						   shiftAttributes.getOperatorPrecedence() > reduceAttributes.getOperatorPrecedence())
						{
							parseTable.clearCell(statenum,t);
							parseTable.addAction(statenum,t,shiftAction);
							if(logger.isLoggable(CompilerLogMessageSort.PARSE_TABLE_CONFLICT)) logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),generateParseTableConflictMessage(origActions,parseTable.getParseAction(statenum,t)));
							else logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),"");
							continue;
						}
						else if(shiftAttributes.getOperatorPrecedence() != null &&
								reduceAttributes.getOperatorPrecedence() != null &&
								reduceAttributes.getOperatorPrecedence() > shiftAttributes.getOperatorPrecedence())
						{
							parseTable.clearCell(statenum,t);
							parseTable.addAction(statenum,t,reduceAction);
							if(logger.isLoggable(CompilerLogMessageSort.PARSE_TABLE_CONFLICT)) logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),generateParseTableConflictMessage(origActions,parseTable.getParseAction(statenum,t)));
							else logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),"");
							continue;
						}
						// Elseif the precedences are equal, but the tokens share the same type of associativity,
						// dock out actions accordingly.
						else if(// implicit: tokenAttributes.getOperatorPrecedence() == productionAttributes.getOperatorPrecedence() &&
								shiftAttributes.getAssociativityType() == reduceAttributes.getAssociativityType())
						{
							if(reduceAttributes.getAssociativityType() == OperatorAttributes.ASSOC_LEFT)
							{
								parseTable.clearCell(statenum,t);
								parseTable.addAction(statenum,t,reduceAction);
								if(logger.isLoggable(CompilerLogMessageSort.PARSE_TABLE_CONFLICT)) logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),generateParseTableConflictMessage(origActions,parseTable.getParseAction(statenum,t)));
								else logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),"");
								continue;
							}
							else if(reduceAttributes.getAssociativityType() == OperatorAttributes.ASSOC_RIGHT)
							{
								parseTable.clearCell(statenum,t);
								parseTable.addAction(statenum,t,shiftAction);
								if(logger.isLoggable(CompilerLogMessageSort.PARSE_TABLE_CONFLICT)) logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),generateParseTableConflictMessage(origActions,parseTable.getParseAction(statenum,t)));
								else logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),"");
								continue;
							}
							else if(reduceAttributes.getAssociativityType() == OperatorAttributes.ASSOC_NONASSOC)
							{
								parseTable.clearCell(statenum,t);
								if(logger.isLoggable(CompilerLogMessageSort.PARSE_TABLE_CONFLICT)) logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),null);
								else logger.logParseTableConflict(CompilerLogMessageSort.PARSE_TABLE_CONFLICT,true,statenum,grammar.getDisplayName(t.getId()),"");
								continue;
							}
							else continue;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Builds lookahead sets in a completed LALR(1) DFA.
	 *
	 */
	public void LALRize()
	throws CopperException
	{
		HashSet<Terminal> primeBeginningLayout = new HashSet<Terminal>();
		for(Terminal layout : grammar.getStartLayout())
		{
			primeBeginningLayout.add(layout);
		}
		Hashtable<LALR1StateItem,LALR1LookaheadTables> primeTables = new Hashtable<LALR1StateItem,LALR1LookaheadTables>();
		primeTables.put(primeItem,new LALR1LookaheadTables());
		primeTables.get(primeItem).addBeginningLayout(primeBeginningLayout);

		Hashtable< LALR1State,Hashtable<LALR1StateItem,LALR1LookaheadTables> > fringe1 = new Hashtable< LALR1State,Hashtable<LALR1StateItem,LALR1LookaheadTables> >();
		Hashtable< LALR1State,Hashtable<LALR1StateItem,LALR1LookaheadTables> > fringe2 = new Hashtable< LALR1State,Hashtable<LALR1StateItem,LALR1LookaheadTables> >();
		
		fringe1.put(primeState,primeTables);

		boolean setsChanged = true;
		while(setsChanged)
		{
			setsChanged = false;
			// For each state I in T:
			for(LALR1State I : fringe1.keySet())
			{
				Hashtable<LALR1StateItem,LALR1LookaheadTables> seedItems = fringe1.get(I);
				// DEBUG-BEGIN
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
				// DEBUG-END
				// DEBUG-X-BEGIN
				//System.err.println("Processing state " + dfaLALR1.getLabel(I));
				// DEBUG-X-END
				// DEBUG-X-BEGIN
				//System.err.println("Seed items:");
				//System.err.println(seedItems);
				// DEBUG-X-END
				Hashtable<LALR1StateItem,LALR1LookaheadTables> closureResult = layoutClosure(seedItems);
				HashSet<GrammarSymbol> activeTransitions = new HashSet<GrammarSymbol>();
				Hashtable< GrammarSymbol,HashSet<LALR1StateItem> > activeGotoItems = new Hashtable< GrammarSymbol,HashSet<LALR1StateItem> >();
				
				// DEBUG-X-BEGIN
				//System.err.println("Closure result:");
				//System.err.println(closureResult);
				// DEBUG-X-END

				for(LALR1StateItem item : seedItems.keySet())
				{
					if(!isWanted(item)) continue;
					if(item.isShiftable())
					{
						GrammarSymbol X = item.getSymbolAtPosition();
						if(!X.equals(FringeSymbols.EOF) &&
						   isWanted(X))
						{
							if(!activeGotoItems.containsKey(X)) activeGotoItems.put(X,new HashSet<LALR1StateItem>());
							activeGotoItems.get(X).add(item);
							activeTransitions.add(X);
						}
					}
				}
				
				for(LALR1StateItem item : closureResult.keySet())
				{
					if(!isWanted(item)) continue;
					// DEBUG-X-BEGIN
					//System.err.println("State " + dfaLALR1.getLabel(I) + ", item " + item + " before union:");
					//System.err.println(dfaLALR1.getLookaheadTables(I,item));
					// DEBUG-X-END
					dfaLALR1.unionLookaheadTables(I,item,closureResult.get(item));
					// DEBUG-X-BEGIN
					//System.err.println("State " + dfaLALR1.getLabel(I) + ", item " + item + " after union:");
					//System.err.println(dfaLALR1.getLookaheadTables(I,item));
					// DEBUG-X-END
					if(item.isShiftable())
					{
						GrammarSymbol X = item.getSymbolAtPosition();
						if(!X.equals(FringeSymbols.EOF) &&
							isWanted(X))
						{
							if(!activeGotoItems.containsKey(X)) activeGotoItems.put(X,new HashSet<LALR1StateItem>());
							activeGotoItems.get(X).add(item);
							activeTransitions.add(X);
						}
					}
				}

				for(LALR1Transition transition : dfaLALR1.getTransitions(I))
				{
					if(!activeTransitions.contains(transition.getLabel())) continue;
					for(LALR1StateItem item : activeGotoItems.get(transition.getLabel()))
					{
						boolean itemChanged = false;
						LALR1StateItem advancedItem = item.advancePosition();
						// DEBUG-X-BEGIN
						//System.err.println("State " + dfaLALR1.getLabel(transition.getDest()) + ", item " + advancedItem + " before goto-union:");
						//System.err.println(dfaLALR1.getLookaheadTables(transition.getDest(),advancedItem));
						// DEBUG-X-END
						if(seedItems.containsKey(item)) itemChanged = dfaLALR1.unionLookaheadTables(transition.getDest(),advancedItem,seedItems.get(item)) || itemChanged;
						if(closureResult.containsKey(item)) itemChanged = dfaLALR1.unionLookaheadTables(transition.getDest(),advancedItem,closureResult.get(item)) || itemChanged;
						// DEBUG-X-BEGIN
						//System.err.println("State " + dfaLALR1.getLabel(transition.getDest()) + ", item " + advancedItem + " after goto-union:");
						//System.err.println(dfaLALR1.getLookaheadTables(transition.getDest(),advancedItem));
						//System.err.println("Changed? --- " + itemChanged);
						// DEBUG-X-END
						if(itemChanged)
						{
							if(!fringe2.containsKey(transition.getDest())) fringe2.put(transition.getDest(),new Hashtable<LALR1StateItem,LALR1LookaheadTables>());
							if(!fringe2.get(transition.getDest()).containsKey(advancedItem)) fringe2.get(transition.getDest()).put(advancedItem,new LALR1LookaheadTables());
							if(seedItems.containsKey(item)) fringe2.get(transition.getDest()).get(advancedItem).union(seedItems.get(item));
							if(closureResult.containsKey(item)) fringe2.get(transition.getDest()).get(advancedItem).union(closureResult.get(item));
						}
					}
				}
			}
			if(!fringe2.isEmpty())
			{
				fringe1 = fringe2;
				fringe2 = new Hashtable< LALR1State,Hashtable<LALR1StateItem,LALR1LookaheadTables> >();
				setsChanged = true;
			}
		}
	}
	
	private Hashtable<LALR1StateItem,LALR1LookaheadTables> layoutClosure(Hashtable<LALR1StateItem,LALR1LookaheadTables> inputTables)
	{
		Hashtable<LALR1StateItem,LALR1LookaheadTables> outputTables = new Hashtable<LALR1StateItem,LALR1LookaheadTables>();
		boolean iChanged = true;
		LALR1State newItems0,newItems1 = new LALR1State(inputTables.keySet());
		while(iChanged)
		{
			iChanged = false;
			newItems0 = newItems1;
			newItems1 = new LALR1State();
			for(LALR1StateItem item : newItems0.getItems())
			{
				if(!isWanted(item)) continue;
				if(item.isShiftable() && item.getSymbolAtPosition() instanceof NonTerminal)
				{
					LALR1LookaheadTables unionTables = new LALR1LookaheadTables();
					if(inputTables.containsKey(item)) unionTables.union(inputTables.get(item));
					if(outputTables.containsKey(item)) unionTables.union(outputTables.get(item));

					LALR1LookaheadTables newOutputTables = getCombinedFirst(item,unionTables);
					if(!grammar.pContains(item.getSymbolAtPosition())) continue;
					for(Production p : grammar.getP(item.getSymbolAtPosition()))
					{
						LALR1StateItem newItem1 = new LALR1StateItem(p);
						if(!outputTables.containsKey(newItem1))	outputTables.put(newItem1,new LALR1LookaheadTables());
						boolean changedTables = false;
						changedTables = outputTables.get(newItem1).union(newOutputTables);
						if(changedTables) newItems1.addItem(newItem1);
					}
				}
			}
			if(newItems1.getItems().iterator().hasNext()) iChanged = true;
		}
		return outputTables;
	}
	
	private LALR1LookaheadTables getCombinedFirst(LALR1StateItem item,LALR1LookaheadTables inputTables)
	{
		// DEBUG-X-BEGIN
		//System.err.println("Starting combined-first for " + item);
		// DEBUG-X-END
		Iterable<GrammarSymbol> symbols = item.getSymbolsAfterPosition();
		Iterable<Terminal> endings = inputTables.getLookahead();
		
		LALR1LookaheadTables tables = new LALR1LookaheadTables();
		
		// If this item is at the beginning, any layout token that may precede it
		// may precede its derivative.
		if(item.isBeginning())
		{
			for(Terminal layout : inputTables.getBeginningLayout())
			{
				tables.addBeginningLayout(layout);
			}
		}
		// Else its own layout token may precede its derivative.
		else tables.addBeginningLayout(grammar.getTerminalLayout(item.getProd()));
		
		boolean reachedEnd = true;
		// For each symbol in the item after the one after the dot:
		for(GrammarSymbol sym : symbols)
		{
			// For each terminal in the first set of that symbol:
			for(Terminal t : grammar.getContextSets().getFirst(sym))
			{
				// Add it to the lookahead.
				tables.addLookahead(t);
				// Indicate that in this context, the layout token of this
				// item's production may precede it.
				HashSet<Terminal> prodLayouts = grammar.getTerminalLayout(item.getProd());
				for(Terminal prodLayout : prodLayouts) tables.addLookaheadLayout(prodLayout,t);
			}
			// If one symbol is not nullable, break.
			if(!grammar.getContextSets().nullableContains(sym))
			{
				reachedEnd = false;
				break;
			}
		}
		// If the end of the production was reached in the last step
		// (i.e. all symbols after the position be nullable):
		if(reachedEnd)
		{
			// Add the item's lookahead to its derivative's lookahead.
			for(Terminal ending : endings)
			{
				tables.addLookahead(ending);
			}
			// Add the data about which layout tokens may precede which
			// lookahead.
			for(Terminal layout : inputTables.getLookaheadLayoutKeys())
			{
				for(Terminal ending : inputTables.getLookaheadLayout(layout))
				{
					tables.addLookaheadLayout(layout,ending);
				}
			}
		}
		// DEBUG-X-BEGIN
		//System.err.println("Layout data from " + rv);
		// DEBUG-X-END
		return tables;
	}
	
	public void buildDFA()
	throws CopperException
	{
		// Initialize E to empty.
		dfaLALR1 = new LALR1DFA();
		HashSet<LALR1State> fringe1 = new HashSet<LALR1State>();
		HashSet<LALR1State> fringe2 = new HashSet<LALR1State>();
		// Initialize T to {Closure([STARTPRIME ::= (*) startSym EOF,  t]}.
		LALR1State newState = new LALR1State();
		grammar.addProductionAttributes(
		 grammar.getStartProd(),
		 new ProductionAttributes(
		  new GrammarName(FringeSymbols.STARTPRIME.getId()),
		  InputPosition.initialPos(),
		  0,
		  null,
		  grammar.getStartLayout(),
		  new TerminalClass(FringeSymbols.STARTPRIME.getId()),
		  ""));
		primeItem = new LALR1StateItem(grammar.getStartProd());
		newState.addItem(primeItem);
		LALR1State J = itemClosure(newState);
		dfaLALR1.addState(J);
		fringe1.add(J);
		primeState = J;
		// Repeat until E and T do not change:
		boolean setsChanged = true;
		while(setsChanged)
		{
			setsChanged = false;
			// For each state I in T:
			for(LALR1State I : fringe1)
			{
				// DEBUG-BEGIN
				if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
				// DEBUG-END
				// For each item [A ::= a (*) X b,  w] in I:
				for(LALR1StateItem item : I.getItems())
				{
					if(item.isShiftable())
					{
						GrammarSymbol X = item.getSymbolAtPosition();
						if(X.equals(FringeSymbols.EOF) ||
						  !isWanted(X)) continue;
						// Let J be Goto(I,X).
						J = itemGoto(I,X);
						// T = union(T,J).
						if(dfaLALR1.addState(J)) fringe2.add(J);
						// E = union(E,I->X->J).
						dfaLALR1.addTransition(I,X,J);
					}
				}
			}
			if(!fringe2.isEmpty())
			{
				fringe1 = fringe2;
				fringe2 = new HashSet<LALR1State>();
				setsChanged = true;
			}
		}
	}

	public LALR1State itemClosure(LALR1State seed)
	{
		if(memoizedLR0Seeds.containsKey(seed)) return memoizedLR0Seeds.get(seed); 
		LALR1State rv = new LALR1State(seed.getItems());
		LALR1State newItems0,newItems1 = new LALR1State(rv.getItems());
		
		// Repeat until I does not change:
		boolean iChanged = true;
		while(iChanged)
		{
			iChanged = false;
			newItems0 = newItems1;
			newItems1 = new LALR1State();
			// For any item [A ::= a (*) X b,  z] in I
			for(LALR1StateItem item : newItems0.getItems()/*rv.second().getItems()*/)
			{
				if(!isWanted(item)) continue;
				if(item.isShiftable() && item.getSymbolAtPosition() instanceof NonTerminal)
				{
					// For any production X ::= g, for any w in first(bz):
					if(!grammar.pContains(item.getSymbolAtPosition())) continue;
					for(Production p : grammar.getP(item.getSymbolAtPosition()))
					{
						// I := union(I,[X ::= (*) g,  w])
						LALR1StateItem newItem1 = new LALR1StateItem(p);
						newItems1.addItem(newItem1);
						// DEBUG-X-BEGIN
						//System.err.println("Sent to " + newItem1);
						// DEBUG-X-END
					}
				}
				for(LALR1StateItem newItem1 : newItems1.getItems())
				{
					iChanged = rv.addItem(newItem1) || iChanged;
				}
			}
		}
		memoizedLR0Seeds.put(seed,rv);
		return rv;
	}
	
	public LALR1State itemGoto(LALR1State I,GrammarSymbol transitionLabel)
	{
		// J := emptyset
		LALR1State J = new LALR1State();
		// For any item [A ::= a (*) X b,  z] in I:
		for(LALR1StateItem item : I.getItems())
		{
			if(!isWanted(item)) continue;
			if(item.isShiftable() &&
			   item.getSymbolAtPosition().equals(transitionLabel))
			{
				// Add [A ::= a X (*) b,  z] to J.
				LALR1StateItem newItem = item.advancePosition();
				J.addItem(newItem);
			}
		}
		return itemClosure(J);
	}
	
	public GrammarSource getGrammar()
	{
		return grammar;
	}
	
	public LALR1DFA getLALR1DFA()
	{
		return dfaLALR1;
	}

	public GLRParseTable getParseTable()
	{
		return parseTable;
	}
	
	public String generateParseTableConflictMessage(HashSet<ParseAction> conflict,ParseAction resolution)
	{
		ParseActionPrettyPrinter papp = new ParseActionPrettyPrinter(grammar);
		if(resolution != null) return papp.prettyPrintConflict(conflict) + "\n  Resolved in favor of " + resolution.acceptVisitor(papp);
		else return conflict.toString() + "\n  Resolved in favor of an error action.";
	}
}
