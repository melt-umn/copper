package edu.umn.cs.melt.copper.compiletime.semantics.lalr1;

import java.util.HashSet;
import java.util.TreeSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.compiletime.engines.lalr.QScannerStateInfo;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageSort;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.LexicalConflictResolution;
import edu.umn.cs.melt.copper.compiletime.parsetable.old.ParseTable;
import edu.umn.cs.melt.copper.runtime.auxiliary.internal.PrettyPrinter;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public abstract class LexicalAmbiguityChecker
{
	public CompilerLogger logger;
	
	public LexicalAmbiguityChecker(CompilerLogger logger)
	{
		this.logger = logger;
	}
	
	protected HashSet<Terminal> disambiguateState(QScannerStateInfo info,
			  GrammarSource grammarData,
			  Iterable<Terminal> shiftable)
	{
		// Calculate the intersection of the accept set and the shiftable set.
		HashSet<Terminal> finalAcceptSet;
		finalAcceptSet = new HashSet<Terminal>();
		for(Terminal t : shiftable) if(info.getAcceptingSyms().contains(t)) finalAcceptSet.add(t);
		return finalAcceptSet;
	}
	
	protected void reportAmbiguity(GrammarSource grammar,HashSet<Terminal> ambiguity,TreeSet<Integer> places,LexicalConflictResolution resolution)
	throws CopperException
	{
		CompilerLogMessageSort severity = (resolution != null) ? CompilerLogMessageSort.LEXICAL_CONFLICT : CompilerLogMessageSort.UNRESOLVED_LEXICAL_CONFLICT;
		HashSet<String> displayNamedAmbiguity = new HashSet<String>();
		for(Terminal t : ambiguity) displayNamedAmbiguity.add(grammar.getDisplayName(t.getId()));
		String ambigHash = PrettyPrinter.iterablePrettyPrint(displayNamedAmbiguity,"   ",PrettyPrinter.getOptimalItemsPerLine(displayNamedAmbiguity,80));
		logger.logLexicalConflict(severity,resolution,places,ambiguity," " + (ambiguity.size() == 2 ? "between" : "among") + " terminals:\n" + ambigHash + (resolution != null ? "\nResolved by " + resolution + "." : ""));		
	}

	public abstract void checkLexicalAmbiguities(GrammarSource grammarData,QScannerStateInfo[] stateInfo,ParseTable parseTable) throws CopperException;
}
