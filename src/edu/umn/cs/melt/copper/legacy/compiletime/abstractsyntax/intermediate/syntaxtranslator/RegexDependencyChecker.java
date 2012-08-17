package edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.syntaxtranslator;

import java.util.Hashtable;
import java.util.LinkedList;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.PrecedenceRelationGraph;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.IntermediateSymbolNode;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.IntermediateSymbolSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class RegexDependencyChecker
{
	public GrammarSource grammar;
	public Hashtable< IntermediateSymbolSort,Hashtable<Symbol,IntermediateSymbolNode> > sortedNodes;
	public PrecedenceRelationGraph regexDependencies;

	public RegexDependencyChecker(GrammarSource grammar,Hashtable< IntermediateSymbolSort,Hashtable<Symbol,IntermediateSymbolNode> > sortedNodes)
	{
		this.grammar = grammar;
		this.sortedNodes = sortedNodes;
		regexDependencies = new PrecedenceRelationGraph(grammar.getT());
	}
	
	@SuppressWarnings("unchecked")
	public void regexDepCheck(CompilerLogger logger,Hashtable<Symbol,IntermediateSymbolNode> intermediateRep)
	throws CopperException
	{
		Hashtable<Symbol,IntermediateSymbolNode> terminals = sortedNodes.get(IntermediateSymbolSort.TERMINAL); 
		for(Symbol termS : terminals.keySet())
		{
			IntermediateSymbolNode term = terminals.get(termS);
			LinkedList<String> regexDeps = new LinkedList<String>();
			if(term.attributes.containsKey("regexDeps"))
			{
				regexDeps = (LinkedList<String>) term.attributes.get("regexDeps").second();
			}
			LinkedList<String> dominates = new LinkedList<String>();
			if(term.attributes.containsKey("dominates"))
			{
				dominates = (LinkedList<String>) term.attributes.get("dominates").second();
			}
			LinkedList<String> submits = new LinkedList<String>();
			if(term.attributes.containsKey("submits"))
			{
				submits = (LinkedList<String>) term.attributes.get("submits").second();
			}
			
			for(String regexDepStr : regexDeps)
			{
				Symbol regexDepSym = Symbol.symbol(regexDepStr);
				if(!intermediateRep.containsKey(regexDepSym) ||
				   intermediateRep.get(regexDepSym).sort != IntermediateSymbolSort.TERMINAL)
				{
					if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,term.attributes.get("regex").first(),"Symbol '" + regexDepSym + "' specified as a regex macro is not a terminal");
					continue;
				}
				regexDependencies.addEdge(new Terminal(termS),new Terminal(regexDepSym));
			}
			
			for(String dominatesStr : dominates)
			{
				Symbol dominatesSym = Symbol.symbol(dominatesStr);
				if(!intermediateRep.containsKey(dominatesSym) ||
				   (intermediateRep.get(dominatesSym).sort != IntermediateSymbolSort.TERMINAL &&
				    intermediateRep.get(dominatesSym).sort != IntermediateSymbolSort.TERMINAL_CLASS))
				{
					InputPosition location;
					if(term.attributes.containsKey("dominates")) location = term.attributes.get("dominates").first();
					else location = term.attributes.get("location").first();
					if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,location,"Symbol '" + dominatesSym + "' specified in a precedence relation is not a terminal or terminal class");
					continue;
				}
				if(intermediateRep.get(dominatesSym).sort == IntermediateSymbolSort.TERMINAL)
				{
					grammar.addStaticPrecedenceRelation(new Terminal(dominatesSym),new Terminal(termS));
				}
				else /* if(intermediateRep.get(dominatesSym).sort == IntermediateSymbolSort.TERMINAL_CLASS) */
				{
					for(Terminal bottom : grammar.getTClassMembers(new TerminalClass(dominatesSym)))
					{
						grammar.addStaticPrecedenceRelation(bottom,new Terminal(termS));
					}
				}
			}

			for(String submitsStr : submits)
			{
				Symbol submitsSym = Symbol.symbol(submitsStr);
				if(!intermediateRep.containsKey(submitsSym) ||
				   (intermediateRep.get(submitsSym).sort != IntermediateSymbolSort.TERMINAL &&
				    intermediateRep.get(submitsSym).sort != IntermediateSymbolSort.TERMINAL_CLASS))
				{
					InputPosition location;
					if(term.attributes.containsKey("submits")) location = term.attributes.get("submits").first();
					else location = term.attributes.get("location").first();
					if(logger.isLoggable(CompilerLogMessageSort.ERROR)) logger.logErrorMessage(CompilerLogMessageSort.ERROR,location,"Symbol '" + submitsSym + "' specified in a precedence relation is not a terminal or terminal class");
					continue;
				}
				if(intermediateRep.get(submitsSym).sort == IntermediateSymbolSort.TERMINAL)
				{
					grammar.addStaticPrecedenceRelation(new Terminal(termS),new Terminal(submitsSym));
				}
				else /* if(intermediateRep.get(dominatesSym).sort == IntermediateSymbolSort.TERMINAL_CLASS) */
				{
					for(Terminal top : grammar.getTClassMembers(new TerminalClass(submitsSym)))
					{
						grammar.addStaticPrecedenceRelation(new Terminal(termS),top);
					}
				}
			}
}
	}

}
