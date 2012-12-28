package edu.umn.cs.melt.copper.legacy.compiletime.semantics.lalr1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.LexicalDisambiguationGroup;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.grammar.TerminalClass;
import edu.umn.cs.melt.copper.legacy.compiletime.abstractsyntax.intermediate.syntaxtranslator.MasterController;
import edu.umn.cs.melt.copper.legacy.compiletime.engines.lalr.QScannerStateInfo;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.legacy.compiletime.logging.LexicalConflictResolution;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.FullReduceAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ParseAction;
import edu.umn.cs.melt.copper.legacy.compiletime.parsetable.ParseTable;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class GenericLexicalAmbiguityChecker extends LexicalAmbiguityChecker
{
	public GenericLexicalAmbiguityChecker(CompilerLogger logger)
	{
		super(logger);
	}
	
	public void checkLexicalAmbiguities(GrammarSource grammarData,
										QScannerStateInfo[] stateInfo,
										ParseTable parseTable)
	throws CopperException
	{
		// All accept sets in the scanner with cardinality > 1.
		HashMap< HashSet<Terminal>,LexicalConflictResolution > ambiguityResolutions = new HashMap< HashSet<Terminal>,LexicalConflictResolution >();
		// The set of ambiguities, mapping specific ambiguities to sorted sets containing
		// the states where each one occurs.
		Hashtable< HashSet<Terminal>,TreeSet<Integer> > ambiguityLocations = new Hashtable< HashSet<Terminal>,TreeSet<Integer> >();
		// For every state in the parser:
		for(int statenum = 0;statenum <= parseTable.getLastState();statenum++)
		{
			// The valid layout at the parser state.
			if(!parseTable.hasLayout(statenum))
			{
				if(logger.isLoggable(CompilerLogMessageSort.WARNING)) logger.logMessage(CompilerLogMessageSort.WARNING,null,"No layout map listed for state " + statenum + ". Perhaps grammar layout was not specified.");
				continue;
			}
			if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(MasterController.DOT_WINDOW,".");
			// For every state in the scanner:
			for(int i = 0;i < stateInfo.length;i++)
			{
				QScannerStateInfo info = stateInfo[i];
				HashSet<Terminal> layoutAcceptSet = disambiguateState(info,grammarData,parseTable.getLayout(statenum));
				// The shiftable set at the parser state.
				if(!parseTable.hasShiftable(statenum))
				{
					if(logger.isLoggable(CompilerLogMessageSort.FATAL_ERROR)) logger.logErrorMessage(CompilerLogMessageSort.FATAL_ERROR,null,"No actions whatsoever listed for state " + statenum);
					continue;
				}
				HashSet<Terminal> acceptSet = disambiguateState(info,grammarData,parseTable.getShiftable(statenum));
				// If there is an ambiguity on layout, add it to the set of ambiguities. 
				if(layoutAcceptSet.size() > 1 && !grammarData.hasDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("dacheck"),layoutAcceptSet,"")))
				{
					if(!ambiguityLocations.containsKey(layoutAcceptSet) ||
							   ambiguityResolutions.get(layoutAcceptSet) != null) ambiguityLocations.put(layoutAcceptSet,new TreeSet<Integer>());
							ambiguityResolutions.put(layoutAcceptSet,null);
							ambiguityLocations.get(layoutAcceptSet).add(statenum);
				}
				// If there is an ambiguity in the shiftable set, add it to the set of ambiguities.
				else if(acceptSet.size() > 1 && !grammarData.hasDisambiguationGroup(new LexicalDisambiguationGroup(new TerminalClass("dacheck"),acceptSet,"")))
				{
					HashSet<ParseAction> actions = new HashSet<ParseAction>();
					for(Terminal t : acceptSet)
					{
						for(ParseAction a : parseTable.getParseActions(statenum,t))
						{
							actions.add(a);
							if(actions.size() > 1) break;
						}
						if(actions.size() > 1) break;
					}
					if(actions.size() != 1 ||
					!(actions.iterator().next() instanceof FullReduceAction))
					{
						if(!ambiguityLocations.containsKey(acceptSet) ||
								   ambiguityResolutions.get(acceptSet) != null) ambiguityLocations.put(acceptSet,new TreeSet<Integer>());
								ambiguityResolutions.put(acceptSet,null);
								ambiguityLocations.get(acceptSet).add(statenum);
					}
				}
				else if(info.getAcceptingSyms().size() > 1)
				{
					LexicalConflictResolution resolution = (acceptSet.size() == 1 ? LexicalConflictResolution.CONTEXT : LexicalConflictResolution.DISAMBIGUATION_FUNCTION);
					if(!ambiguityResolutions.containsKey(info.getAcceptingSyms())) ambiguityResolutions.put(info.getAcceptingSyms(),resolution);
					if(ambiguityResolutions.get(info.getAcceptingSyms()) != null)
					{
						if(!ambiguityLocations.containsKey(info.getAcceptingSyms())) ambiguityLocations.put(info.getAcceptingSyms(),new TreeSet<Integer>());
						ambiguityLocations.get(info.getAcceptingSyms()).add(i);
					}
				}
			}
		}
		if(logger.isLoggable(CompilerLogMessageSort.TICK)) logger.logTick(1,"\n");
		// Report all ambiguities.
		for(HashSet<Terminal> ambiguity : ambiguityResolutions.keySet())
		{
			LexicalConflictResolution resolution = ambiguityResolutions.get(ambiguity);
			TreeSet<Integer> places = ambiguityLocations.get(ambiguity);
			reportAmbiguity(grammarData,ambiguity,places,resolution);
		}
	}
}
