package edu.umn.cs.melt.copper.compiletime.checkers;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Map.Entry;

import edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample.SymbolTable;
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

		int numSubsetDisambiguationMembers = 0;
		BitSet subsetDisambiguationMembers = new BitSet();
		Hashtable<BitSet,Integer> subsetDisambiguationFunctions = new Hashtable<BitSet,Integer>();
		Hashtable<BitSet,Integer> regularDisambiguationFunctions = new Hashtable<BitSet,Integer>();
		for(int i = spec.disambiguationFunctions.nextSetBit(0);i >= 0;i = spec.disambiguationFunctions.nextSetBit(i+1))
		{
			BitSet members = spec.df.getMembers(i);
			if (spec.df.getApplicableToSubsets(i)) {
				// Optimization: track the total size of all subset disambiguation functions and the union of their members.
				// If the sizes match, we know they are all disjoint so we don't need to do any more detailed error checking.
				numSubsetDisambiguationMembers += members.cardinality();
				subsetDisambiguationMembers.or(members);
				if (subsetDisambiguationMembers.cardinality() != numSubsetDisambiguationMembers) {
					for (Entry<BitSet,Integer> df : subsetDisambiguationFunctions.entrySet()) {
						if (df.getKey().intersects(members)) {
							BitSet intersect = (BitSet)members.clone();
							intersect.and(df.getKey());
							logger.log(new OverlappingDisambiguationFunctionMessage(symbolTable,i,df.getValue(),intersect));
							passed = false;
						}
					}
				}
				subsetDisambiguationFunctions.put(members, i);
			} else {
				if (regularDisambiguationFunctions.containsKey(members)) {
					logger.log(new DuplicateDisambiguationFunctionMessage(symbolTable,i,regularDisambiguationFunctions.get(members),members));
					passed = false;
				}
				regularDisambiguationFunctions.put(members, i);
			}
		}
		
		return passed;
	}	
}
