package edu.umn.cs.melt.copper.compiletime.builders;

import java.util.Arrays;
import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarnew.ParserSpec;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class SingleScannerDFAAnnotationBuilder
{
	private CompilerLogger logger;
	private SymbolTable<CopperASTBean> symbolTable;
	private ParserSpec spec;
	private GeneralizedDFA dfa;
	
	private SingleScannerDFAAnnotationBuilder(CompilerLogger logger,SymbolTable<CopperASTBean> symbolTable,ParserSpec spec,GeneralizedDFA dfa)
	{
		this.logger = logger;
		this.symbolTable = symbolTable;
		this.spec = spec;
		this.dfa = dfa;
	}
	
	public static SingleScannerDFAAnnotations build(CompilerLogger logger,SymbolTable<CopperASTBean> symbolTable,ParserSpec spec,GeneralizedDFA dfa)
	throws CopperException
	{
		return new SingleScannerDFAAnnotationBuilder(logger,symbolTable,spec,dfa).buildAnnotations();
	}
	
	private SingleScannerDFAAnnotations buildAnnotations()
	throws CopperException
	{
		int i;
		// Set up holders for expanded state information.
		int SCANNER_STATE_COUNT = dfa.stateCount();
		BitSet[] acceptSets = new BitSet[SCANNER_STATE_COUNT];
		BitSet[] rejectSets = new BitSet[SCANNER_STATE_COUNT];
		BitSet[] possibleSets = new BitSet[SCANNER_STATE_COUNT];
		int[] cMap = new int[Character.MAX_VALUE + 1];

		// For each state in the DFA:
		for(int state = 0;state < dfa.stateCount();state++)
		{
			BitSet accF = dfa.getAcceptSymbols(state);
			BitSet rej = spec.t.precedences.partitionAcceptSet(logger, "static precedence disambiguator, scanner state " + state, symbolTable, accF);
			acceptSets[state] = new BitSet();
			rejectSets[state] = new BitSet();
			possibleSets[state] = new BitSet();
			
			acceptSets[state].or(accF);
			acceptSets[state].andNot(rej);
			rejectSets[state].or(rej);
		}
		
		boolean[][] transClosure = new boolean[SCANNER_STATE_COUNT][SCANNER_STATE_COUNT];
		// Compute the transitive closure of the DFA's states
		// using the Floyd-Warshall algorithm, as presented in
		// Cormen, Leiserson, Rivest and Stein's
		// "Introduction to Algorithms," Second Edition,
		// section 25.2.
		for(i = 0;i < SCANNER_STATE_COUNT;i++) transClosure[i][i] = true;
		for(int state = 0;state < dfa.stateCount();state++)
		{
			BitSet connections = dfa.getConnectedStates(state);
			for(int j = connections.nextSetBit(0);j >= 0;j = connections.nextSetBit(j+1))
			{
				transClosure[state][j] = true;
			}
		}
		for(int k = 0;k < SCANNER_STATE_COUNT;k++)
		{
			for(i = 0;i < SCANNER_STATE_COUNT;i++)
			{
				for(int j = 0;j < SCANNER_STATE_COUNT;j++)
				{
					transClosure[i][j] = transClosure[i][j] || (transClosure[i][k] && transClosure[k][j]);
				}
			}
		}
		// Use the transitive closure to compute possible sets:
		for(i = 0;i < SCANNER_STATE_COUNT;i++)
		{
			for(int j = 0;j < SCANNER_STATE_COUNT;j++)
			{
				// If states i and j are connected by transition:
				if(transClosure[i][j])
				{
					// Union the current possible set of i with
					// the accepting set of j.
					possibleSets[i].or(acceptSets[j]);
					possibleSets[i].or(rejectSets[j]);
				}
			}
		}
		
		for(int cr = 0;cr < dfa.charRangeCount();cr++)
		{
			char[][] canonicalRanges = dfa.getCharRange(cr).getMembers();
			for(int j = 0;j < canonicalRanges.length;j++)
			{
				Arrays.fill(cMap, canonicalRanges[j][0], canonicalRanges[j][1] + 1,cr);
			}
		}
		
		return new SingleScannerDFAAnnotations(acceptSets, rejectSets, possibleSets, cMap);
	}
}
