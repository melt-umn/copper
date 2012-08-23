package edu.umn.cs.melt.copper.compiletime.builders;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0ItemSet;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.mda.MDAResults;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ContextSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

public class ModularDeterminismAnalyzer
{
	public static MDAResults build(boolean checkILSubsets,ParserSpec hostSpec,ParserSpec fullSpec,ContextSets hostContextSets,ContextSets fullContextSets,LR0DFA hostDFA,LR0DFA fullDFA,LRLookaheadAndLayoutSets hostLookaheadSets,LRLookaheadAndLayoutSets fullLookaheadSets)
	{
		return new ModularDeterminismAnalyzer(checkILSubsets, hostSpec, fullSpec, hostContextSets, fullContextSets, hostDFA, fullDFA, hostLookaheadSets, fullLookaheadSets).analyze();
	}
	
	// Set to 'false' for the relaxed version of the MDA. 
	private boolean checkILSubsets;
	private ParserSpec hostSpec;
	private ParserSpec fullSpec;
	private ContextSets hostContextSets;
	private ContextSets fullContextSets;
	private LR0DFA hostDFA;
	private LR0DFA fullDFA;
	private LRLookaheadAndLayoutSets hostLookaheadSets;
	private LRLookaheadAndLayoutSets fullLookaheadSets;
	
	private ModularDeterminismAnalyzer(boolean checkILSubsets, ParserSpec hostSpec,
			ParserSpec fullSpec, ContextSets hostContextSets,
			ContextSets fullContextSets, LR0DFA hostDFA, LR0DFA fullDFA,
			LRLookaheadAndLayoutSets hostLookaheadSets,
			LRLookaheadAndLayoutSets fullLookaheadSets)
	{
		this.checkILSubsets = checkILSubsets;
		this.hostSpec = hostSpec;
		this.fullSpec = fullSpec;
		this.hostContextSets = hostContextSets;
		this.fullContextSets = fullContextSets;
		this.hostDFA = hostDFA;
		this.fullDFA = fullDFA;
		this.hostLookaheadSets = hostLookaheadSets;
		this.fullLookaheadSets = fullLookaheadSets;
	}
	
	public MDAResults analyze()
	{
		ArrayList<Byte> errorTypes = new ArrayList<Byte>();
		ArrayList<BitSet> hostSets = new ArrayList<BitSet>();
		ArrayList<BitSet> fullSets = new ArrayList<BitSet>();
		ArrayList<Integer> hostStates = new ArrayList<Integer>();
		ArrayList<Integer> fullStates = new ArrayList<Integer>();
		ArrayList<BitSet> locations = new ArrayList<BitSet>();
		
		TreeMap<Integer,Integer> hostStatePartition = new TreeMap<Integer,Integer>();
		BitSet extensionStatePartition = new BitSet();
		BitSet newHostStatePartition = new BitSet();
		
		BitSet spillage = new BitSet();
		
		for(int nt = hostSpec.nonterminals.nextSetBit(0);nt >= 0;nt = hostSpec.nonterminals.nextSetBit(nt+1))
		{
			spillage.clear();
			spillage.or(fullContextSets.getFollow(nt));
			spillage.andNot(hostContextSets.getFollow(nt));
			if(!spillage.isEmpty())
			{
				errorTypes.add(MDAResults.FOLLOW_SPILLAGE);
				hostSets.add(hostContextSets.getFollow(nt));
				fullSets.add(fullContextSets.getFollow(nt));
				hostStates.add(nt);
				fullStates.add(nt);
				locations.add(new BitSet());
			}
		}
		
		BitSet encounteredStates = new BitSet();
		Queue<Integer> fringe = new LinkedList<Integer>();
		hostStatePartition.put(1,1);
		fringe.offer(1);
		
		while(!fringe.isEmpty())
		{
			int statenum = fringe.poll();
			int hostStatenum = hostStatePartition.get(statenum);
			encounteredStates.set(statenum);
			for(int X = hostDFA.getTransitionLabels(hostStatenum).nextSetBit(0);X >= 0;X = hostDFA.getTransitionLabels(hostStatenum).nextSetBit(X+1))
			{
				int J = hostDFA.getTransition(hostStatenum,X);
				int JPrime = fullDFA.getTransition(statenum,X);
				hostStatePartition.put(JPrime,J);
				if(!encounteredStates.get(JPrime)) fringe.offer(JPrime);
			}
		}
				
		for(int statenum : hostStatePartition.keySet())
		{
			int hostStatenum = hostStatePartition.get(statenum);
			LR0ItemSet hostItems = hostDFA.getItemSet(hostStatenum);
			LR0ItemSet fullItems = fullDFA.getItemSet(statenum);
			int hostItemCounter = 0,fullItemCounter = 0;
			
			while(hostItemCounter < hostItems.size())
			{
				while(fullItemCounter < fullItems.size() &&
				      fullItems.getProduction(fullItemCounter) != hostItems.getProduction(hostItemCounter) ||
					  fullItems.getPosition(fullItemCounter) != hostItems.getPosition(hostItemCounter))
				{
					if(!fullSpec.bridgeConstructs.get(fullItems.getProduction(fullItemCounter)))
					{
						System.err.println("Non-bridge-production syntax addition in host state " + statenum + ", item " + fullItemCounter + " -- bug in MDA");
					}
					fullItemCounter++;
				}
				
				spillage.clear();
				spillage.or(fullLookaheadSets.getLookahead(statenum,fullItemCounter));
				spillage.andNot(hostLookaheadSets.getLookahead(hostStatenum,hostItemCounter));
				spillage.andNot(fullSpec.bridgeConstructs);
				
				if(!spillage.isEmpty())
				{
					errorTypes.add(MDAResults.LOOKAHEAD_SPILLAGE);
					hostSets.add(hostLookaheadSets.getLookahead(hostStatenum,hostItemCounter));
					fullSets.add(fullLookaheadSets.getLookahead(statenum,fullItemCounter));
					hostStates.add(hostStatenum);
					fullStates.add(statenum);
					locations.add(new BitSet());
					locations.get(locations.size() - 1).set(fullItemCounter);
				}
				
				hostItemCounter++;
				fullItemCounter++;
			}
			
			while(fullItemCounter < fullItems.size())
			{
				if(!fullSpec.bridgeConstructs.get(fullItems.getProduction(fullItemCounter)))
				{
					System.err.println("Non-bridge-production syntax addition in host state " + statenum + ", item " + fullItemCounter + " -- bug in MDA");
				}
				fullItemCounter++;
			}
		}
		
		BitSet extAndNewHostStates = new BitSet();
		extAndNewHostStates.set(1,fullDFA.size());
		extAndNewHostStates.andNot(encounteredStates);
		
		for(int statenum = extAndNewHostStates.nextSetBit(0);statenum >= 0;statenum = extAndNewHostStates.nextSetBit(statenum+1))
		{
			LR0ItemSet state = fullDFA.getItemSet(statenum);
			for(int item = 0;item < state.size();item++)
			{
				if(!isHostOwned(state.getProduction(item)) &&
				   (!fullSpec.bridgeConstructs.get(state.getProduction(item)) || state.getPosition(item) != 0))
				{
					extensionStatePartition.set(statenum);
					break;
				}
			}
			if(!extensionStatePartition.get(statenum)) newHostStatePartition.set(statenum);
		}
		
		if(checkILSubsets)
		{
			boolean isISubset;
			TreeMap<Integer,Integer> itemMap = new TreeMap<Integer,Integer>();
			for(int statenum = newHostStatePartition.nextSetBit(0);statenum >= 0;statenum = newHostStatePartition.nextSetBit(statenum+1))
			{
				isISubset = false;
				for(int possibleSuperset : hostStatePartition.values())
				{
					if(isISubset(statenum,possibleSuperset,itemMap))
					{
						isISubset = true;
						for(int fullItem : itemMap.keySet())
						{
							int hostItem = itemMap.get(fullItem);
							spillage.clear();
							spillage.or(hostLookaheadSets.getLookahead(possibleSuperset,hostItem));
							spillage.andNot(fullLookaheadSets.getLookahead(statenum,fullItem));
							
							if(!spillage.isEmpty())
							{
								errorTypes.add(MDAResults.I_SUBSET_ONLY);
								hostSets.add(new BitSet());
								fullSets.add(new BitSet());
								hostStates.add(-1);
								fullStates.add(statenum);
								locations.add(new BitSet());
								locations.get(locations.size() - 1).set(possibleSuperset);
								break;
							}
						}
					}
				}
				
				if(!isISubset)
				{
					errorTypes.add(MDAResults.NON_IL_SUBSET);
					hostSets.add(new BitSet());
					fullSets.add(new BitSet());
					hostStates.add(-1);
					fullStates.add(statenum);
					locations.add(new BitSet());
				}
			}
		}
		
		byte[] errorTypesA = new byte[errorTypes.size()];
		for(int i = 0;i < errorTypes.size();i++) errorTypesA[i] = errorTypes.get(i);
		BitSet[] hostSetsA = new BitSet[hostSets.size()];
		hostSets.toArray(hostSetsA);
		BitSet[] fullSetsA = new BitSet[fullSets.size()];
		fullSets.toArray(fullSetsA);
		int[] hostStatesA = new int[hostStates.size()];
		for(int i = 0;i < hostStates.size();i++) hostStatesA[i] = hostStates.get(i);
		int[] fullStatesA = new int[fullStates.size()];
		for(int i = 0;i < fullStates.size();i++) fullStatesA[i] = fullStates.get(i);
		BitSet[] locationsA = new BitSet[locations.size()];
		locations.toArray(locationsA);
		
		MDAResults rv = new MDAResults(errorTypesA, hostSetsA, fullSetsA, hostStatesA, fullStatesA, locationsA, encounteredStates, extensionStatePartition, newHostStatePartition);
		return rv;
	}
	
	private boolean isHostOwned(int element)
	{
		return fullSpec.owners[element] == fullSpec.owners[fullSpec.pr.getRHSSym(fullSpec.getStartProduction(),0)]; 
	}
	
	private boolean isISubset(int subset,int superset,TreeMap<Integer,Integer> itemMap)
	{
		LR0ItemSet hostItems = hostDFA.getItemSet(superset);
		LR0ItemSet fullItems = fullDFA.getItemSet(subset);
		int hostItemCounter = 0,fullItemCounter = 0;
		
		itemMap.clear();
		
		while(fullItemCounter < fullItems.size())
		{
			while(hostItemCounter < hostItems.size() &&
			      (hostItems.getProduction(hostItemCounter) != fullItems.getProduction(fullItemCounter) ||
				   hostItems.getPosition(hostItemCounter) != fullItems.getPosition(fullItemCounter)))
			{
				hostItemCounter++;
			}
			if(hostItemCounter == hostItems.size() ||
			   (fullItems.size() - fullItemCounter) > (hostItems.size() - hostItemCounter)) return false;
			itemMap.put(fullItemCounter,hostItemCounter);
			hostItemCounter++;
			fullItemCounter++;
		}
		
		return true;
	}
}
