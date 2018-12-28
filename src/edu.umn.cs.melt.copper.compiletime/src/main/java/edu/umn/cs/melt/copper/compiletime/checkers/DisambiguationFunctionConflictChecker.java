package edu.umn.cs.melt.copper.compiletime.checkers;

import java.util.BitSet;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.DuplicateDisambiguationFunctionMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.OverlappingDisambiguationFunctionMessage;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

/**
 * Checks that no two disambiguation functions have the same member set, and that all disambiguation
 * functions applicable to subsets have disjoint member sets. 
 * @author Lucas Kramer &lt;<a href="mailto:krame505@umn.edu">krame505@umn.edu</a>&gt;
 *
 */
public class DisambiguationFunctionConflictChecker
{
	private CompilerLogger logger;
	private SymbolTable<CopperASTBean> symbolTable;
	private ParserSpec spec;
	
	private DisambiguationFunctionConflictChecker(CompilerLogger logger,SymbolTable<CopperASTBean> symbolTable,ParserSpec spec)
	{
		this.logger = logger;
		this.symbolTable = symbolTable;
		this.spec = spec;
	}
	
	public static boolean check(CompilerLogger logger,SymbolTable<CopperASTBean> symbolTable, ParserSpec spec)
	{
		return new DisambiguationFunctionConflictChecker(logger, symbolTable, spec).checkDisambiguationFunctionConflicts();
	}
	
	private boolean checkDisambiguationFunctionConflicts()
	{
		boolean passed = true;

		Hashtable<BitSet,Integer> subsetDisambiguationFunctions = new Hashtable<BitSet,Integer>();
		Hashtable<BitSet,Integer> regularDisambiguationFunctions = new Hashtable<BitSet,Integer>();
		for(int i = spec.disambiguationFunctions.nextSetBit(0);i >= 0;i = spec.disambiguationFunctions.nextSetBit(i+1))
		{
			BitSet members = spec.df.getMembers(i);
			if (spec.df.getApplicableToSubsets(i)) {
				for (BitSet df : subsetDisambiguationFunctions.keySet()) {
					if (df.intersects(members)) {
						BitSet intersect = (BitSet)members.clone();
						intersect.and(df);
						logger.log(new OverlappingDisambiguationFunctionMessage(symbolTable,intersect));
						passed = false;
					}
				}
				subsetDisambiguationFunctions.put(members, i);
			} else {
				if (regularDisambiguationFunctions.containsKey(members)) {
					logger.log(new DuplicateDisambiguationFunctionMessage(symbolTable,members));
					passed = false;
				}
				regularDisambiguationFunctions.put(members, i);
			}
		}
		
		return passed;
	}	
}
