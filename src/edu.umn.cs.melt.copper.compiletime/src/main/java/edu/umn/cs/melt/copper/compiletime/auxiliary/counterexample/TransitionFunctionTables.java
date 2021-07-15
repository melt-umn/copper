package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0ItemSet;
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
     * A mapping of states
     */
    int[] test;


    public TransitionFunctionTables(LR0DFA dfa, ParserSpec spec){
//        System.out.println("Begin transition function tables builder");
        trans = new Hashtable<>();
        revTrans = new Hashtable<>();

        //TODO comment
        //Initialize the tables
        //for each state
        for(int i = 0; i < dfa.size(); i++) {
            //for each item in the (source) state
            LR0ItemSet srcItemSet = dfa.getItemSet(i);
//            System.out.println("Transition src state: " + i);
            for(int j = 0; j < srcItemSet.size(); j++){
                int srcProduction = srcItemSet.getProduction(j);
                int srcDotPosition = srcItemSet.getPosition(j);
                int expectedDotPosition = srcDotPosition + 1;
//                System.out.println("Transition Function: (srcProduction: " + srcProduction + ", srcDotPosition: " + srcDotPosition +")");

                //skip reduce items
                if(srcDotPosition == spec.pr.getRHSLength(srcProduction)){
//                    System.out.println("Reduce item, skipping");
                    continue;
                }
                int symbolAfterDot = spec.pr.getRHSSym(srcProduction,srcDotPosition);
//                System.out.println("Symbol after dot: " + symbolAfterDot);

                int destinationState = dfa.getTransition(i,symbolAfterDot);
//                System.out.println("destination state: " + destinationState);

                LR0ItemSet dstItemSet = dfa.getItemSet(destinationState);

                //for each item in the (destination) state
                for(int k = 0; k < dstItemSet.size(); k++){
                    //determine if the item is the transition destination item
                    if(srcProduction != dstItemSet.getProduction(k)  || expectedDotPosition != dstItemSet.getPosition(k)){
//                        System.out.println("destination item (Production: " + dstItemSet.getProduction(k)
//                                + ", dotPosition: " + dstItemSet.getPosition(k) + ") does not appear to be a transition for "
//                                + "(Production: " + srcProduction + ", dotPosition: " + srcDotPosition + ")");
                        //if it isn't, try the next item.
                       continue;
                    }
//                    System.out.println("destination item (Production: " + dstItemSet.getProduction(k)
//                            + ", dotPosition: " + dstItemSet.getPosition(k) + ") IS a transition for "
//                            + "(Production: " + srcProduction + ", dotPosition: " + srcDotPosition + ")");
                    StateItem srcStateItem = new StateItem(i,srcProduction,srcItemSet.getPosition(j));
                    StateItem dstStateItem = new StateItem(destinationState,dstItemSet.getProduction(k),dstItemSet.getPosition(k));

                   StateItem[] tran = trans.get(srcStateItem);
                    if(tran == null){
                        tran = new StateItem[spec.terminals.size() + spec.nonterminals.size()];
                        trans.put(srcStateItem,tran);
                    }
                    tran[symbolAfterDot] = dstStateItem;

                    Set<StateItem> revTran = revTrans.get(dstStateItem);
                    if(revTran == null){
                        revTran = new HashSet<>();
                        revTrans.put(dstStateItem,revTran);
                    }
                    revTran.add(srcStateItem);
                    break;
                }
            }
        }
    }
    public StateItem getTransition(StateItem stateItem, int symbol){
        return trans.get(stateItem)[symbol];
    }
    public Set<StateItem> getReverseTransitions(StateItem stateItem){
        return revTrans.get(stateItem);
    }
//    /**
//     * The reverse transition function.
//     * @return a 2d array of BitSets such that {@code build(dfa)[state][symbol]}
//     * is the set of states that could have transitioned to {@code state} on {@code symbol}
//     */
//    //TODO may require information on the item?
//    public static BitSet[][] build(LR0DFA dfa, ParserSpec spec){
//        int maxTransitionSize = Math.max(spec.terminals.length(),spec.nonterminals.length());
//        BitSet[][] result = new BitSet[dfa.getTransitionLength()][maxTransitionSize];
//        //for each state
//        for (int i = 0; i < dfa.getTransitionLength(); i++) {
//            //for each symbol
//            for(int j = 0; j < maxTransitionSize; j++){
//                //TODO is the transition function total? that is, will this ever be null/invalid?
//                result[dfa.getTransition(i,j)][j].set(i);
//            }
//        }
//        return result;
//    }
}
