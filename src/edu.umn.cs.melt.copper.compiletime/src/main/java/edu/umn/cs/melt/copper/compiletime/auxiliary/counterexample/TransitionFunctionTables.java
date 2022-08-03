package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0ItemSet;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import java.util.*;

/**
 * The transition and reverse transition tables used in counterexample generation.
 * Must take the particular item within the state into account, so we can't just reuse the
 * transition function in the dfa as is.
 */
public class TransitionFunctionTables {
    /**
     * the state item transitioned to parameterized by the start StateItem and the symbol
     * (which is an integer index into the symbol table)
     */
    protected Hashtable<StateItem, StateItem[]> trans;
    /**
     * same as {@link #trans}, but the result is the set of any possible
     * StateItems that could have transitioned to the input StateItem on the input symbol
     * Unlike the forward transition table, this is only parametrized by a stateItem.
     * This is because there is only one symbol that can lead to a given state,
     * so there would only be one valid (symbol) index for each stateItem, which is silly
     */
    protected Hashtable<StateItem, Set<StateItem>> revTrans;

    /**
     * Direct mapping from LR state to symbol.
     * Each state has exactly one symbol that it can be reached by
     * (i.e, if a state can be reached on symbol `2`, it can _only_ be reached on symbol 2).
     * This array is that mapping.
     * This relation is not bijective, so the reverse is not necessarily true
     * (multiple states can be reached on the same symbol (depending on the current state))
     */
    protected int[] prevSymbol;

    public TransitionFunctionTables(LR0DFA dfa, ParserSpec spec, LRLookaheadSets lookaheads) {
        prevSymbol = new int[dfa.size()];
        trans = new Hashtable<>();
        revTrans = new Hashtable<>();

        //TODO comment
        //Initialize the tables
        //for each state
        for (int i = 0; i < dfa.size(); i++) {
            //for each item in the (source) state
            LR0ItemSet srcItemSet = dfa.getItemSet(i);
            if(i == 5){
                System.out.println("spate 5 " +  srcItemSet);
                System.out.println("spate 5 " +  srcItemSet.size());
            }
            for (int j = 0; j < srcItemSet.size(); j++) {
                int srcProduction = srcItemSet.getProduction(j);
                int srcDotPosition = srcItemSet.getPosition(j);
                int expectedDotPosition = srcDotPosition + 1;
                if(i==5){
                    System.out.println(srcProduction);
                    System.out.println(srcDotPosition);
                }

                //skip reduce items
                if (srcDotPosition == spec.pr.getRHSLength(srcProduction)) {
                    System.out.println("skipping on state" + i);
                    continue;
                }
                int symbolAfterDot = spec.pr.getRHSSym(srcProduction, srcDotPosition);

                int dstState = dfa.getTransition(i, symbolAfterDot);
                if(i == 5){
                   System.out.println("after dot:" + symbolAfterDot);
                   System.out.println("dstState " + dstState);
                }

                //could check for null, but I think that would be equally efficient.
                prevSymbol[dstState] = symbolAfterDot;

                LR0ItemSet dstItemSet = dfa.getItemSet(dstState);
                if(i == 5)
                    System.out.println("dstItemSet " + dstItemSet);

                //for each item in the (destination) state
                for (int k = 0; k < dstItemSet.size(); k++) {
                    //determine if the item is the transition destination item
                    if (srcProduction != dstItemSet.getProduction(k) || expectedDotPosition != dstItemSet.getPosition(k)) {
                        //if it isn't, try the next item.
                        continue;
                    }
                    StateItem srcStateItem =
                            new StateItem(i, srcProduction, srcItemSet.getPosition(j), lookaheads.getLookahead(i,j));
                    StateItem dstStateItem =
                            new StateItem(dstState, dstItemSet.getProduction(k),
                                    dstItemSet.getPosition(k),lookaheads.getLookahead(dstState,k));
                    StateItem[] tran = trans.get(srcStateItem);
                    if (tran == null) {
                        tran = new StateItem[spec.terminals.size() + spec.nonterminals.size()];
                        trans.put(srcStateItem, tran);
                    }
                    tran[symbolAfterDot] = dstStateItem;

                    Set<StateItem> revTran = revTrans.get(dstStateItem);
                    if (revTran == null) {
                        revTran = new HashSet<>();
                        revTrans.put(dstStateItem, revTran);
                    }
                    revTran.add(srcStateItem);
                    break;
                }
            }
        }
        System.out.println("trans,lol");
        System.out.println(trans);
        System.out.println(trans);
    }

    public StateItem getTransition(StateItem stateItem, int symbol) {
        return trans.get(stateItem)[symbol];
    }

    public Set<StateItem> getReverseTransitions(StateItem stateItem) {
        return revTrans.get(stateItem);
    }
}