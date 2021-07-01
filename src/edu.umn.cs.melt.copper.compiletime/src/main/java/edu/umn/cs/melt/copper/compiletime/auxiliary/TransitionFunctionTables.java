package edu.umn.cs.melt.copper.compiletime.auxiliary;

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
    protected Hashtable<StateItem, ArrayList<StateItem>> trans;
    /**
     * same as {@link #trans}, but the result is the set of any possible
     * StateItems that could have transitioned to the input StateItem on the input symbol
     */
    protected Hashtable<StateItem, ArrayList<Set<StateItem>>> revTrans;


    public TransitionFunctionTables(LR0DFA dfa, ParserSpec spec){
        trans = new Hashtable<>();
        revTrans = new Hashtable<>();

        //TODO comment
        //Initialize the tables
        //for each state
        for(int i = 0; i < dfa.size(); i++) {
            //for each item in the (source) state
            LR0ItemSet srcItemSet = dfa.getItemSet(i);
            for(int j = 0; j < srcItemSet.size(); j++){
                int srcProduction = srcItemSet.getProduction(j);
                int expectedDotPosition = srcItemSet.getPosition(j) + 1;
                int symbolAfterDot = spec.pr.getRHSSym(srcProduction,expectedDotPosition);

                int destinationState = dfa.getTransition(i,symbolAfterDot);
                LR0ItemSet dstItemSet = dfa.getItemSet(i);

                //for each item in the (destination) state
                for(int k = 0; k < dstItemSet.size(); k++){
                    //determine if the  item is the transition destination item
                    if(srcProduction != dstItemSet.getProduction(k)
                           || expectedDotPosition != dstItemSet.getPosition(k)){
                        //if it isn't, try the next item.
                       continue;
                    }
                    StateItem srcStateItem = new StateItem(i,srcProduction,srcItemSet.getPosition(j));
                    StateItem dstStateItem = new StateItem(destinationState,dstItemSet.getProduction(k),dstItemSet.getPosition(k));

                   ArrayList<StateItem> tran = trans.get(srcStateItem);
                    if(tran == null){
                        tran = new ArrayList<>();
                        trans.put(srcStateItem,tran);
                    }
                    tran.set(symbolAfterDot,dstStateItem);

                    ArrayList<Set<StateItem>> revTran = revTrans.get(dstStateItem);
                    if(revTran == null){
                        revTran = new ArrayList<>();
                        revTrans.put(dstStateItem,revTran);
                    }
                    Set<StateItem> srcs = revTran.get(symbolAfterDot);
                    if (srcs == null) {
                        srcs = new HashSet<>();
                        revTran.set(symbolAfterDot, srcs);
                    }
                    srcs.add(srcStateItem);
                    break;
                }
            }
        }
    }
    public StateItem getTransition(StateItem stateItem, int symbol){
        return trans.get(stateItem).get(symbol);
    }
    public Set<StateItem> getReverseTransitions(StateItem stateItem, int symbol){
        return revTrans.get(stateItem).get(symbol);
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
