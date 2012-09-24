package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.syntaxtranslator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.FringeSymbols;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarName;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSymbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LexicalAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LexicalDisambiguationGroup;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminal;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.NonTerminalAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.OperatorAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.ParserAttribute;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Production;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.ProductionAttributes;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.engines.semantics.SpecialParserAttributes;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class SymGatherer
{
	public GrammarSource grammar;
	public Hashtable< IntermediateSymbolSort,Hashtable<Symbol,IntermediateSymbolNode> > sortedNodes;
	
	public SymGatherer(GrammarSource grammar)
	{
		this.grammar = grammar;
	}

	public void symGather(CompilerLogger logger,Hashtable<Symbol,IntermediateSymbolNode> intermediateRep)
	throws CopperException
	{
		sortedNodes = new Hashtable< IntermediateSymbolSort,Hashtable<Symbol,IntermediateSymbolNode> >();
		IntermediateSymbolSort.populateHashtable(sortedNodes);
		for(Symbol s : intermediateRep.keySet())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.AST_DOT_WINDOW,".");
			IntermediateSymbolNode sym = intermediateRep.get(s);
			// DEBUG-X-BEGIN
			//System.err.println(sym.name + ": " + sym.attributes);
			// DEBUG-X-END
			sortedNodes.get(sym.sort).put(sym.name,sym);
			HashSet<String> requiredAttributes = new HashSet<String>();
			switch(sym.sort)
			{
			case TERMINAL:
				grammar.addToT(new Terminal(sym.name));
				requiredAttributes.add("regex");
				break;
			case NON_TERMINAL:
				grammar.addToNT(new NonTerminal(sym.name));
				break;
			case PRODUCTION:
				requiredAttributes.add("LHS");
				requiredAttributes.add("RHS");
				break;
			case GRAMMAR_NAME:
				grammar.addContainedGrammar(new GrammarName(sym.name));
				if(sym.attributes.containsKey("parserName"))
				{
					grammar.getParserSources().setParserName((String) sym.attributes.get("parserName").second());
				}
				break;
			case DISAMBIGUATION_GROUP:
				requiredAttributes.add("members");
				requiredAttributes.add("code");
			case TERMINAL_CLASS:
				grammar.addTClass(new TerminalClass(sym.name));
				break;
			case PARSER_ATTRIBUTE:
				requiredAttributes.add("type");
				break;
			case DIRECTIVE:
				break;
			}
			
			if(!sym.attributes.keySet().containsAll(requiredAttributes))
			{
				requiredAttributes.removeAll(sym.attributes.keySet());
				if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("location").first(),"Missing the following attributes on " + sym.sort + " '" + sym.name + "': " + requiredAttributes);
				continue;
			}
		}
		
		for(Symbol s : sortedNodes.get(IntermediateSymbolSort.DIRECTIVE).keySet())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.AST_DOT_WINDOW,".");
			IntermediateSymbolNode sym = intermediateRep.get(s);
			if(sym.name.equals(Symbol.symbol(" defaultProdCode ")))
			{
				if(sym.attributes.containsKey("code"))
				{
					grammar.setDefaultProdCode((String) sym.attributes.get("code").second());
				}
			}
			else if(sym.name.equals(Symbol.symbol(" defaultTermCode ")))
			{
				if(sym.attributes.containsKey("code"))
				{
					grammar.setDefaultTCode((String) sym.attributes.get("code").second());
				}
			}
			else if(sym.name.equals(Symbol.symbol(" postParseCode ")))
			{
				if(sym.attributes.containsKey("code"))
				{
					grammar.getParserSources().setPostParseCode((String) sym.attributes.get("code").second());
				}
			}
			else if(sym.name.equals(Symbol.symbol(" grammarLayout ")))
			{
				throw new UnsupportedOperationException("DEPRECATED");
			}
			else if(sym.name.equals(Symbol.symbol(" startCode ")))
			{
				if(sym.attributes.containsKey("code"))
				{
					grammar.getParserSources().addClassFilePreambleCode((String) sym.attributes.get("code").second());
				}
			}
			else if(sym.name.equals(Symbol.symbol(" auxCode ")))
			{
				if(sym.attributes.containsKey("code"))
				{
					grammar.getParserSources().addParserClassAuxCode((String) sym.attributes.get("code").second());
				}
			}
			else if(sym.name.equals(Symbol.symbol(" initCode ")))
			{
				if(sym.attributes.containsKey("code"))
				{
					grammar.getParserSources().addParserAttrInitCode((String) sym.attributes.get("code").second());
				}
			}
		}
		
		for(Symbol s : intermediateRep.keySet())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.AST_DOT_WINDOW,".");
			IntermediateSymbolNode sym = intermediateRep.get(s);
			switch(sym.sort)
			{
			case GRAMMAR_NAME:
				if(sym.attributes.containsKey("layout"))
				{
					@SuppressWarnings("unchecked")
					LinkedList<String> layouts = (LinkedList<String>) sym.attributes.get("layout").second();
					// DEBUG-X-BEGIN
					//System.err.println("Owner is " + sym.name);
					// DEBUG-X-END
					if(!layouts.isEmpty())
					{
						for(String layout : layouts)
						{
							Symbol layoutSym = Symbol.symbol(layout);
							if(!intermediateRep.containsKey(layoutSym) ||
							   intermediateRep.get(layoutSym).sort != IntermediateSymbolSort.TERMINAL)
							{
								if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("layout").first(),"Symbol '" + layoutSym + "' specified as grammar layout is not a terminal");
								continue;
							}
							grammar.addGrammarLayout(sym.owner,new Terminal(layoutSym));
						}
					}
					else
					{
						grammar.addGrammarLayout(sym.owner,FringeSymbols.EMPTY);
						if(logger.isLoggable(CompilerLogMessageSort.WARNING)) logger.logMessage(CompilerLogMessageSort.WARNING,sym.attributes.get("layout").first(),"No grammar layout for grammar '" + sym.owner + "'");
						continue;
					}
				}
				break;
			default:
				break;
			}
		}
		
		for(Symbol s : intermediateRep.keySet())
		{
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.AST_DOT_WINDOW,".");
			IntermediateSymbolNode sym = intermediateRep.get(s);
			switch(sym.sort)
			{
			case TERMINAL:
				Terminal prefix = FringeSymbols.EMPTY;
				if(sym.attributes.containsKey("displayname"))
				{
					String displayName = (String) sym.attributes.get("displayname").second();
					grammar.setDisplayName(sym.name,displayName);
				}
				if(sym.attributes.containsKey("prefix"))
				{
					Symbol prefSym = Symbol.symbol((String) sym.attributes.get("prefix").second());
					if(!intermediateRep.containsKey(prefSym) ||
					   intermediateRep.get(prefSym).sort != IntermediateSymbolSort.TERMINAL)
					{
						if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("prefix").first(),"Symbol '" + prefSym + "' specified as a transparent prefix is not a terminal");
						continue;
					}
					prefix = new Terminal(prefSym);
				}
				String code = "";
				if(sym.attributes.containsKey("code"))
				{
					code = (String) sym.attributes.get("code").second();
				}
				String type = "Object";
				if(sym.attributes.containsKey("type"))
				{
					type = (String) sym.attributes.get("type").second();
				}
				grammar.addLexicalAttributes(new Terminal(sym.name),new LexicalAttributes(sym.owner,sym.attributes.get("location").first(),type,prefix,code));
				if(sym.attributes.containsKey("classes"))
				{
					@SuppressWarnings("unchecked") LinkedList<String> classes = (LinkedList<String>) sym.attributes.get("classes").second();
					for(String classStr : classes)
					{
						Symbol classSym = Symbol.symbol(classStr);
						if(!intermediateRep.containsKey(classSym) ||
						   intermediateRep.get(classSym).sort != IntermediateSymbolSort.TERMINAL_CLASS)
						{
							if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("classes").first(),"Symbol '" + classSym + "' is not a terminal class");
							continue;
						}
						grammar.addToTClass(new TerminalClass(classSym),new Terminal(sym.name));
					}
				}
				if(sym.attributes.containsKey("operatorClass") &&
				   sym.attributes.containsKey("operatorPrecedence"))
				{
					Symbol opClass = Symbol.symbol((String) sym.attributes.get("operatorClass").second());
					if(!intermediateRep.containsKey(opClass) ||
							   intermediateRep.get(opClass).sort != IntermediateSymbolSort.TERMINAL_CLASS)
					{
						if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("operatorClass").first(),"Symbol '" + opClass + "' is not an operator class");
						continue;
					}
					int opPrec = (Integer) sym.attributes.get("operatorPrecedence").second();
					int assocType = OperatorAttributes.ASSOC_NONE;
					if(sym.attributes.containsKey("operatorAssociativity"))
					{
						@SuppressWarnings("unchecked") Pair<InputPosition, Object> pair = ((Pair<InputPosition,Object>) sym.attributes.get("operatorAssociativity").second());
						assocType = (Integer) pair.second();
					}
					grammar.addOperatorAttributes(new Terminal(sym.name),new OperatorAttributes(assocType,opPrec,new TerminalClass(opClass)));
				}
				break;
			case NON_TERMINAL:
				String ntType = "Object";
				if(sym.attributes.containsKey("displayname"))
				{
					String displayName = (String) sym.attributes.get("displayname").second();
					grammar.setDisplayName(sym.name,displayName);
				}
				if(sym.attributes.containsKey("type"))
				{
					ntType = (String) sym.attributes.get("type").second();
				}
				if(sym.attributes.containsKey("isStart"))
				{
					if(grammar.getStartSym() != null)
					{
						if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("start").first(),"More than one start symbol specified");
						continue;
					}
					grammar.setStartSym(new NonTerminal(sym.name));
					if(sym.attributes.containsKey("startLayout"))
					{
						@SuppressWarnings("unchecked") LinkedList<String> startLayout = (LinkedList<String>) sym.attributes.get("startLayout").second();
						for(String layoutStr : startLayout)
						{
							Symbol layoutSym = Symbol.symbol(layoutStr);
							if(!intermediateRep.containsKey(layoutSym) ||
							   intermediateRep.get(layoutSym).sort != IntermediateSymbolSort.TERMINAL)
							{
								if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("start").first(),"Symbol '" + layoutSym + "' specified as start layout is not a terminal");
								continue;
							}
							grammar.addStartLayout(new Terminal(layoutSym));
						}
					}
					else if(grammar.hasGrammarLayout(sym.owner) && !grammar.getGrammarLayout(sym.owner).isEmpty())
					{
						for(Terminal t : grammar.getGrammarLayout(sym.owner))
						{
							grammar.addStartLayout(t);
						}
					}
					grammar.addNTAttributes(new NonTerminal(sym.name),new NonTerminalAttributes(sym.owner,sym.attributes.get("location").first(),ntType,true));
				}
				else grammar.addNTAttributes(new NonTerminal(sym.name),new NonTerminalAttributes(sym.owner,sym.attributes.get("location").first(),ntType,false));
				break;
			case PRODUCTION:
				if(sym.attributes.containsKey("displayname"))
				{
					String displayName = (String) sym.attributes.get("displayname").second();
					grammar.setDisplayName(sym.name,displayName);
				}
				Symbol LHS = Symbol.symbol((String) sym.attributes.get("LHS").second());
				if(!intermediateRep.containsKey(LHS) ||
				   intermediateRep.get(LHS).sort != IntermediateSymbolSort.NON_TERMINAL)
				{
					if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("LHS").first(),"LHS '" + LHS.toString() + "' of production '" + sym.name + "' is not a nonterminal");
				}
				@SuppressWarnings("unchecked") LinkedList<String> RHS = (LinkedList<String>) sym.attributes.get("RHS").second();
				LinkedList<String> RHSVars = null;
				if(sym.attributes.containsKey("RHSVars"))
				{
					@SuppressWarnings("unchecked") LinkedList<String> RHSVarsTemp = (LinkedList<String>) sym.attributes.get("RHSVars").second();
					RHSVars = RHSVarsTemp;
					for(String varName : RHSVars)
					{
						if(varName == null) continue;
						if(!SpecialParserAttributes.isValidAttrName(varName))
						{
							if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("RHSVars").first(),"Improper variable name '" + varName + "'");
							continue;
						}
					}
				}
				ArrayList<GrammarSymbol> properRHS = new ArrayList<GrammarSymbol>();
				if(RHS.size() == 1 && new Terminal(RHS.iterator().next()).equals(FringeSymbols.EMPTY)) RHS.remove();
				for(String rhsStr : RHS)
				{
					Symbol rhsSym = Symbol.symbol(rhsStr);
					if(!intermediateRep.containsKey(rhsSym) ||
					   (intermediateRep.get(rhsSym).sort != IntermediateSymbolSort.TERMINAL &&
					    intermediateRep.get(rhsSym).sort != IntermediateSymbolSort.NON_TERMINAL))
					{
						if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("RHS").first(),"Symbol '" + rhsSym + "' on production RHS is neither a terminal nor a nonterminal");
						continue;
					}
					if(rhsSym.equals(FringeSymbols.EMPTY))
					{
						if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("RHS").first(),"Symbol 'eps' is reserved for use in empty productions");
						continue;
					}
					if(intermediateRep.get(rhsSym).sort == IntermediateSymbolSort.TERMINAL)
					{
						properRHS.add(new Terminal(rhsSym));
					}
					else if(intermediateRep.get(rhsSym).sort == IntermediateSymbolSort.NON_TERMINAL)
					{
						properRHS.add(new NonTerminal(rhsSym));
					}
				}
				Terminal operator = null;
				if(sym.attributes.containsKey("operator"))
				{
					Symbol opSym = Symbol.symbol((String) sym.attributes.get("operator").second());
					if(!intermediateRep.containsKey(opSym) ||
					   intermediateRep.get(opSym).sort != IntermediateSymbolSort.TERMINAL)
					{
						if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("operator").first(),"Symbol '" + opSym + "' specified as an operator is not a terminal");
						continue;
					}
					operator = new Terminal(opSym);
				}
				
				TerminalClass newTClass = new TerminalClass(FringeSymbols.STARTPROD_SYMBOL);
				if(sym.attributes.containsKey("class"))
				{
					Symbol newTClassSym = Symbol.symbol((String) sym.attributes.get("class").second());
					if(!intermediateRep.containsKey(newTClassSym) ||
					   intermediateRep.get(newTClassSym).sort != IntermediateSymbolSort.TERMINAL_CLASS)
					{
						if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("class").first(),"Symbol '" + newTClassSym + "' is not a production class");
						continue;
					}
					newTClass = new TerminalClass(newTClassSym);
				}
				int newPrecedence = FringeSymbols.PRECEDENCE_NONE;
				if(sym.attributes.containsKey("precedence"))
				{
					newPrecedence = (Integer) sym.attributes.get("precedence").second();
				}
				HashSet<Terminal> newLayout = new HashSet<Terminal>();
				if(sym.attributes.containsKey("layout"))
				{
					@SuppressWarnings("unchecked") LinkedList<String> layouts = (LinkedList<String>) sym.attributes.get("layout").second();
					for(String layoutStr : layouts)
					{
						Symbol layoutSym = Symbol.symbol(layoutStr);
						if(!intermediateRep.containsKey(layoutSym) ||
						   intermediateRep.get(layoutSym).sort != IntermediateSymbolSort.TERMINAL)
						{
							if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("class").first(),"Symbol '" + layoutSym + "' specified as production layout is not a terminal");
							continue;
						}
						newLayout.add(new Terminal(layoutSym));
					}
				}
				else
				{
					if(grammar.hasGrammarLayout(sym.owner)) newLayout = new HashSet<Terminal>(grammar.getGrammarLayout(sym.owner));
				}
				if(newLayout.isEmpty()) newLayout.add(FringeSymbols.EMPTY);
				code = "";
				if(sym.attributes.containsKey("code"))
				{
					code = (String) sym.attributes.get("code").second();
				}
				Production newProd = Production.production(sym.name,operator,new NonTerminal(LHS),properRHS);
				if(!newProd.getName().equals(sym.name))
				{
					if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("location").first(),"Production '" + sym.name + "' has the same signature (" + newProd + ") as production '" + newProd.getName() + "'");
					continue;
				}
				
				grammar.addToP(newProd);
				grammar.addProductionAttributes(newProd,
						                        new ProductionAttributes(sym.owner,
						                                                 sym.attributes.get("LHS").first(),
						                                                 newPrecedence,
						                                                 RHSVars,
						                                                 newLayout,
						                                                 newTClass,
						                                                 code));
				break;
			case TERMINAL_CLASS:
				// All necessary operations completed.
				break;
			case DISAMBIGUATION_GROUP:
				@SuppressWarnings("unchecked") LinkedList<String> members = (LinkedList<String>) sym.attributes.get("members").second();
				HashSet<Terminal> properMembers = new HashSet<Terminal>();
				for(String member : members)
				{
					Symbol memSym = Symbol.symbol(member);
					if(!intermediateRep.containsKey(memSym) ||
					   intermediateRep.get(memSym).sort != IntermediateSymbolSort.TERMINAL)
					{
						if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("members").first(),"Symbol '" + memSym + "' specified as a disambiguation group member is not a terminal");
						continue;
					}
					properMembers.add(new Terminal(memSym));
				}
				if(properMembers.size() < 2)
				{
					if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("members").first(),"Disambiguation group '" + sym.name + "' has less than two terminals");
				}
				code = (String) sym.attributes.get("code").second();
				LexicalDisambiguationGroup newGroup = new LexicalDisambiguationGroup(new TerminalClass(sym.name),properMembers,code);
				if(grammar.hasDisambiguationGroup(newGroup))
				{
					if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("location").first(),"Disambiguation group with elements '" + properMembers + "' already declared");
				}
				grammar.addDisambiguationGroup(newGroup);
				break;
			case PARSER_ATTRIBUTE:
				if(!SpecialParserAttributes.isValidAttrName(sym.name.getName()))
				{
					if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,sym.attributes.get("location").first(),"Invalid attribute name '" + sym.name + "'");
				}
				type = (String) sym.attributes.get("type").second();
				code = "";
				if(sym.attributes.containsKey("code")) code = (String) sym.attributes.get("code").second();
				grammar.addParserAttribute(new ParserAttribute(sym.name,type,code));
				break;
			case DIRECTIVE:
				break;
			default:
				break;
			}
		}
	}
}
