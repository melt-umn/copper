package edu.umn.cs.melt.copper.compiletime.builders;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.LexicalAmbiguities;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetablenew.LRParseTable;

/**
 * Builds an object containing information about the lexical ambiguities in a compiled parser specification. 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class LexicalAmbiguitySetBuilder
{
	private ParserSpec spec;
	private LRParseTable parseTable;
	private LRLookaheadAndLayoutSets layouts;
	private TransparentPrefixes prefixes;
	private SingleScannerDFAAnnotations scannerDFAAnnotations;
	
	
	
	private LexicalAmbiguitySetBuilder(ParserSpec spec,LRLookaheadAndLayoutSets layouts,
			LRParseTable parseTable,TransparentPrefixes prefixes,SingleScannerDFAAnnotations scannerDFAAnnotations)
	{
		this.spec = spec;
		this.layouts = layouts;
		this.parseTable = parseTable;
		this.prefixes = prefixes;
		this.scannerDFAAnnotations = scannerDFAAnnotations;
	}
	
	public static LexicalAmbiguities build(ParserSpec spec,LRLookaheadAndLayoutSets layouts,
			LRParseTable parseTable,TransparentPrefixes prefixes,SingleScannerDFAAnnotations scannerDFAAnnotations)
	{
		return new LexicalAmbiguitySetBuilder(spec,layouts,parseTable,prefixes,scannerDFAAnnotations).buildLexicalAmbiguitySet();
	}



	private LexicalAmbiguities buildLexicalAmbiguitySet()
    //throws CopperException
	{
		BitSet unresolved = new BitSet();
		Hashtable<BitSet,Integer> disambiguationFunctions = new Hashtable<BitSet,Integer>();
		Hashtable<BitSet,Integer> ambiguities = new Hashtable<BitSet,Integer>();
		ArrayList<BitSet> locations = new ArrayList<BitSet>();
		ArrayList<Integer> resolutions = new ArrayList<Integer>();
		int ambiguityCount = 0;
		
		for(int i = spec.disambiguationFunctions.nextSetBit(0);i >= 0;i = spec.disambiguationFunctions.nextSetBit(i+1))
		{
			disambiguationFunctions.put(spec.df.getMembers(i),i);
		}
		
		BitSet unitedValidLA = new BitSet();
		BitSet acceptSet = new BitSet();
		
		for(int statenum = 0;statenum < parseTable.size();statenum++)
		{
			unitedValidLA.clear();
			unitedValidLA.or(parseTable.getValidLA(statenum));
			unitedValidLA.or(layouts.getLayout(statenum));
			unitedValidLA.or(prefixes.getPrefixes(statenum));
			
			unitedValidLA.and(spec.terminals);
			
			// For every state in the scanner:
			for(int i = 0;i < scannerDFAAnnotations.size();i++)
			{
				disambiguateState(i,unitedValidLA,acceptSet);
				// If there is an ambiguity in the shiftable set, add it to the set of ambiguities.
				if(acceptSet.cardinality() > 1 && !disambiguationFunctions.containsKey(acceptSet))
				{
					// If there is an ambiguity between terminals that have the same reduce action,
					// it need not be listed as an ambiguity.
					boolean allOneReduceAction = false;
					byte type = -1;
					int parameter = -1;
					for(int t = acceptSet.nextSetBit(0);t >= 0;t = acceptSet.nextSetBit(t+1))
					{
						if(type == -1) type = parseTable.getActionType(statenum,t);
						if(parameter == -1) parameter = parseTable.getActionParameter(statenum,t);
						if(type == LRParseTable.CONFLICT ||
						   type != parseTable.getActionType(statenum,t) ||
						   parameter != parseTable.getActionParameter(statenum,t))
						{
							allOneReduceAction = false;
							break;
						}
					}
					if(allOneReduceAction)
					{
						if(!ambiguities.containsKey(scannerDFAAnnotations.getAcceptSet(i)))
						{
							ambiguities.put(scannerDFAAnnotations.getAcceptSet(i),ambiguityCount++);
							locations.add(new BitSet());
							resolutions.add(0);							
						}

						if(ambiguities.get(scannerDFAAnnotations.getAcceptSet(i)) != null)
						{
							int ambiguity = ambiguities.get(scannerDFAAnnotations.getAcceptSet(i));
							unresolved.set(ambiguity);
							locations.get(ambiguity).set(statenum);
						}
					}
				}
				else if(!acceptSet.isEmpty() && scannerDFAAnnotations.getAcceptSet(i).cardinality() > 1)
				{
					if(!ambiguities.containsKey(scannerDFAAnnotations.getAcceptSet(i)))
					{
						ambiguities.put(scannerDFAAnnotations.getAcceptSet(i),ambiguityCount++);
						locations.add(new BitSet());
						if(acceptSet.cardinality() == 1) resolutions.add(-1); // Context.
						else resolutions.add(disambiguationFunctions.get(acceptSet));
					}

					if(ambiguities.get(scannerDFAAnnotations.getAcceptSet(i)) != null)
					{
						int ambiguity = ambiguities.get(scannerDFAAnnotations.getAcceptSet(i));
						locations.get(ambiguity).set(i);
					}
				}
			}
		}
		
		BitSet[] ambiguitiesA = new BitSet[ambiguities.keySet().size()];
		ambiguities.keySet().toArray(ambiguitiesA);
		BitSet[] locationsA = new BitSet[locations.size()];
		locations.toArray(locationsA);
		int[] resolutionsA = new int[resolutions.size()];
		for(int i = 0;i < resolutions.size();i++) resolutionsA[i] = resolutions.get(i);
		
		return new LexicalAmbiguities(unresolved,ambiguitiesA,locationsA,resolutionsA);
	}
	
	private void disambiguateState(int scannerState,BitSet unitedValidLA,BitSet finalAcceptSet)
	{
		// Calculate the intersection of the accept set and the valid lookahead set.
		finalAcceptSet.clear();
		finalAcceptSet.or(unitedValidLA);
		finalAcceptSet.and(scannerDFAAnnotations.getAcceptSet(scannerState));
	}
}
