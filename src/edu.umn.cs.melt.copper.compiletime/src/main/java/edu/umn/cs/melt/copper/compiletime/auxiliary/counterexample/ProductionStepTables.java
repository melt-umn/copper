package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0ItemSet;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import java.util.BitSet;
import java.util.Hashtable;

public class ProductionStepTables {
    /**
     * The production steps for a given StateItem
     * The bitset represents indices into the LR0ItemSet for the associated state
     */
    protected Hashtable<StateItem,BitSet> prodTable;
    /**
     * The reverse production steps indexed by the state and the non-terminal on the lefthand side of the production
     * Represented by indices into the LR0ItemSet (in the LR0DFA) for the associated state.
     */
    protected BitSet[][] revProdTable;

    public ProductionStepTables(LR0DFA dfa, ParserSpec spec,LRLookaheadSets lookaheadSets){
       revProdTable = new BitSet[dfa.size()][];
       prodTable = new Hashtable<>();
       build(dfa, spec,lookaheadSets);
    }

    /**
     * build the productionStep and reverseProductionStep tables.
     * <p>
     *     More or less directly translated from the reference implementation.
     *     <a
     *     href="https://github.com/polyglot-compiler/polyglot/blob/3ff2eb01acb952d524c7de2b7f2c1a6d875d6f6d/tools/java_cup/src/parser/StateItem.java#L365"
     *     >
     *     located here.
     *     </a>
     * </p>
     */
    private void build(LR0DFA dfa, ParserSpec spec, LRLookaheadSets lookaheads){
        //seems to be working correctly...
        //fill in the closure map
        //for each state
        for (int state = 0; state < dfa.size(); state++) {
            //mapping of a non-terminal to the (indexes into the) LR0 items that were generated by the closure on that state
            BitSet[] closures = new BitSet[dfa.size()];

            LR0ItemSet curr = dfa.getItemSet(state);
            //for each item in the state
            for (int j = 0; j < curr.size(); j++) {
                //if the current item was generated by a closure
                if(curr.getPosition(j) == 0 && curr.getProduction(j) != spec.getStartProduction()){
                    int lhs = spec.pr.getLHS(curr.getProduction(j));
                    if(closures[lhs] == null){
                        closures[lhs] = new BitSet();
                    }
                    closures[lhs].set(j);
                }
            }

            revProdTable[state] = new BitSet[spec.nonterminals.size()];

            //for each item in the state
            for (int item = 0; item < curr.size(); item++) {
                int production = curr.getProduction(item);
                int dotPosition = curr.getPosition(item);
                //do nothing on reduce items
                if (spec.pr.getRHSLength(production) == dotPosition){
                    continue;
                }
                int symbolAfterDot = spec.pr.getRHSSym(production,dotPosition);

                //ignore productions with terminals after their dot
                if(spec.terminals.get(symbolAfterDot)){
                    continue;
                }
                if(closures[symbolAfterDot] != null){
                    StateItem srcStateItem =
                            new StateItem(state,curr.getProduction(item), curr.getPosition(item),
                                    lookaheads.getLookahead(state,item));
                    if(prodTable.get(srcStateItem) == null){
                        prodTable.put(srcStateItem,new BitSet());
                    }
                    if(revProdTable[state][symbolAfterDot] == null){
                        revProdTable[state][symbolAfterDot] = new BitSet();
                    }
                    prodTable.get(srcStateItem).or(closures[symbolAfterDot]);
                    revProdTable[state][symbolAfterDot].set(item);
                }
            }
        }
    }

    /**
     * @see #prodTable
     * @param stateItem
     * @return the set of production steps for the given StateItem
     */
    public BitSet getProdSteps(StateItem stateItem) {
        return prodTable.get(stateItem);
    }

    /**
     * @see #revProdTable
     * @param state
     * @param nonTerminal
     * @return get the set of reverse production steps for the given state and nonterminal
     */
    public BitSet getRevProdSteps(int state, int nonTerminal){
        return revProdTable[state][nonTerminal];
    }
}
