package edu.umn.cs.melt.copper.compiletime.auxiliary;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0ItemSet;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ContextSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import java.util.*;

//TODO comment this whole file thoroughly. like spend a few hours on it.

/**
 * A digraph that is searched in the creation of non-unifying counterexamples to
 * non-lalr parsers. Does not use {@link edu.umn.cs.melt.copper.compiletime.spec.numeric.Digraph},
 * as the number of vertices is not initially known.
 * Wrapper around {@link LookaheadSensitiveGraphVertex}
 */
public class LookaheadSensitiveGraph {
    private LookaheadSensitiveGraphVertex startVertex;

    private int conflictState;

    private StateItem conflictItem1;
    private StateItem conflictItem2;

    private int conflictTerminal;
    private boolean isShiftReduce;

    private ParserSpec spec;
    private PSSymbolTable symbolTable;
    private ContextSets contextSets;
    private LR0DFA dfa;

    private TransitionFunctionTables transitionTables;
    private ProductionStepTables productionStepTables;

    //TODO the structure of where the counterExampleMessage begins and this ends is all sorts of wrong. Reassess once more code is written.
    public LookaheadSensitiveGraph(LRParseTableConflict conflict,
                                   ParserSpec spec, PSSymbolTable symbolTable,
                                   ContextSets contextSets, LR0DFA dfa) {

        conflictState = conflict.getState();
        this.conflictTerminal = conflict.getSymbol();
        this.spec = spec;
        this.symbolTable = symbolTable;
        this.contextSets = contextSets;
        this.dfa = dfa;

        //shift/reduce
        int conflictItem1Production = -1;
        int conflictItem1Position = -1;
        int conflictItem2Production = -1;
        int conflictItem2Position = -1;

        isShiftReduce = conflict.shift != -1;
        LR0ItemSet conflictStateItems = dfa.getItemSet(conflictState);
        if(isShiftReduce){
            conflictItem1Production = conflict.reduce.nextSetBit(0);

            //find dot position for the reduce production
            for(int i = 0; i<conflictStateItems.size(); i++){
                if(conflictStateItems.getProduction(i) == conflictItem1Production){
                    conflictItem1Position = conflictStateItems.getPosition(i);
                    break;
                }
            }
            if(conflictItem1Position == -1){
                System.out.println("conflictItem1Production: " + conflictItem1Production);
                throw new Error("Failed to find reduce conflict item");
            }

            //get shift conflict item from shift state and conflict terminal
            for(int i = 0; i < conflictStateItems.size(); i++ ){
                int prod = conflictStateItems.getProduction(i);
                int pos = conflictStateItems.getPosition(i);

                if(pos == 0){
                    continue;
                }

                //If the dot is directly after the conflict terminal, it is the shift conflict item
                if(spec.pr.getRHSSym(prod,pos) == conflictTerminal){
                    conflictItem2Position = pos;
                    conflictItem2Production = prod;
                    break;
                }
            }
            if(conflictItem2Position == -1 || conflictItem2Production == -1){
                throw new Error("Failed to find shift conflict item");

            }

        }
        //reduce/reduce
        else {
            conflictItem1Production= conflict.reduce.nextSetBit(0);

            //find dot position for the first  reduce production
            for(int i = 0; i<conflictStateItems.size(); i++){
                if(conflictStateItems.getProduction(i) == conflictItem1Production){
                    conflictItem1Position = conflictStateItems.getPosition(i);
                    break;
                }
            }
            if(conflictItem1Position == -1){
                System.out.println("conflictItem1Production: " + conflictItem1Production);
                throw new Error("Failed to find first reduce conflict item");
            }

            conflictItem2Production = conflict.reduce.nextSetBit(conflictItem1Production + 1);

            //find dot position for the second reduce production
            for(int i = 0; i<conflictStateItems.size(); i++){
                if(conflictStateItems.getProduction(i) == conflictItem2Production){
                    conflictItem2Position = conflictStateItems.getPosition(i);
                    break;
                }
            }
            if(conflictItem2Position == -1){
                System.out.println("conflictItem2Production: " + conflictItem2Production);
                throw new Error("Failed to find reduce conflict item");
            }
        }

        conflictItem1 = new StateItem(conflictState,
                conflictItem1Production,
                conflictItem1Position);

        conflictItem2 = new StateItem(conflictState,
                conflictItem2Production,
                conflictItem2Position);

        System.out.println("conflict items:");
        System.out.println(conflictItem1);
        System.out.println(conflictItem2);


        BitSet initLookaheadSet = new BitSet();
        initLookaheadSet.set(spec.getEOFTerminal());
        this.startVertex = new LookaheadSensitiveGraphVertex(1,spec.getStartProduction(),0,initLookaheadSet);

        this.transitionTables = new TransitionFunctionTables(dfa,spec);
        this.productionStepTables = new ProductionStepTables(dfa,spec);
    }

    public Counterexample getNonUnifyingCounterExample(){
        ArrayList<StateItem> shortestContextSensitivePath = findShortestContextSensitivePath(conflictItem1);
        return counterexampleFromShortestPath(shortestContextSensitivePath);
    }

    //todo comment
    public ArrayList<StateItem> findShortestContextSensitivePath(StateItem target){
        Set<StateItem> possibleStateItems = eligibleStateItems(target);
        System.out.println("Possible stateItems:");
        System.out.println(possibleStateItems);

        Queue<LinkedList<LookaheadSensitiveGraphVertex>> queue = new LinkedList<>();
        Set<LookaheadSensitiveGraphVertex> visited = new HashSet<>();

        LinkedList<LookaheadSensitiveGraphVertex> init = new LinkedList<>();
        init.add(startVertex);
        queue.add(init);

        System.out.println(transitionTables.revTrans);

        System.out.println(transitionTables.revTrans.get(conflictItem1));
        System.out.println(transitionTables.trans.get(startVertex.stateItem));


        //unguided breadth-first search
        while(!queue.isEmpty()){
            System.out.println("top of main loop");
            System.out.println("queue:");
            System.out.println(queue);
            LinkedList<LookaheadSensitiveGraphVertex> path = queue.remove();
            LookaheadSensitiveGraphVertex last = path.getLast();
            System.out.println("path at start of queue:");
            System.out.println(path);
            if (visited.contains(last)) {
                continue;
            }
            visited.add(last);
            if(target.equals(last.stateItem) && last.lookaheadSet.get(conflictTerminal)){
                //TODO print process info and such
                //success, copy to ArrayList for efficient access
                ArrayList<StateItem> shortestConflictPath = new ArrayList<>(path.size());
                for (LookaheadSensitiveGraphVertex v : path){
                    shortestConflictPath.add(v.stateItem);
                }
                return shortestConflictPath;
            } else {
                System.out.println("Did not finish.");
                System.out.println(target + " != " + last.stateItem);
                System.out.println("and " + conflictTerminal + " is not in " + last.lookaheadSet);
            }
            //Add all transitions to the search queue
            if(transitionTables.trans.get(last.stateItem) != null){
                System.out.println("Some transition items for" + last.stateItem);
                for(StateItem tranDst : transitionTables.trans.get(last.stateItem)){
                    if(tranDst == null){
                        continue;
                    }
                    //TODO maybe using an array here isn't great if it's almost always null...
                    System.out.println("testing transition item " + tranDst);
//                    if(!possibleStateItems.contains(tranDst)){
//                        continue;
//                    }
                    LookaheadSensitiveGraphVertex next = new LookaheadSensitiveGraphVertex(tranDst,last.lookaheadSet);
                    LinkedList<LookaheadSensitiveGraphVertex> nextPath = new LinkedList<>(path);
                    nextPath.add(next);
                    queue.add(nextPath);
                }
            } else {
                System.out.println("No transition items for" + last.stateItem);
            }
            if(productionStepTables.getProductionSteps(last.stateItem) != null){
                int len = spec.pr.getRHSLength(last.getProduction());
                BitSet newLookahead = followL(last.getProduction(),last.getDotPosition(),last.lookaheadSet);
                BitSet productionSteps = productionStepTables.getProductionSteps(last.stateItem);

                //for each possible item reached via a production step
                LR0ItemSet stateItems = dfa.getItemSet(last.getState());
                for(int i = productionSteps.nextSetBit(0); i != -1; i = productionSteps.nextSetBit(i)) {
                    //TODO refactor to use a memoized lookup table if this uses too much memory
                    StateItem pStateItem = new StateItem(last.getState(),stateItems.getProduction(i),stateItems.getPosition(i));
                    //TODO fix possibleStateItems and re-add this check
//                    if(!possibleStateItems.contains(pStateItem)){
//                        continue;
//                    }
                    LookaheadSensitiveGraphVertex next = new LookaheadSensitiveGraphVertex(pStateItem,newLookahead);
                    LinkedList<LookaheadSensitiveGraphVertex> nextPath = new LinkedList<>(path);
                    nextPath.add(next);
                    queue.add(nextPath);
                }

            } else {
                System.out.println("No production step items for" + last.stateItem);
            }
        }
        throw new Error("Cannot find shortest path along lookahead sensitive graph");
    }

    //TODO comment
    private Set<StateItem> eligibleStateItems(StateItem target){
        //It's between using a hashSet and creating a flat array of all possible stateItems with arbitary positions
        //and doing bitsets of indices into that
        //I might go to that point if performance becomes too big of an issue, but this should be fine.
        Set<StateItem> result = new HashSet<>();
        Queue<StateItem> queue = new LinkedList<>();
        queue.add(target);
        while(!queue.isEmpty()){
           StateItem s = queue.remove();
           if(result.contains(s)){
               continue;
           }
           result.add(s);
           //consider reverse transitions
           if(transitionTables.revTrans.containsKey(s)){
               for(Set<StateItem> prev : transitionTables.revTrans.get(s)){
                   //TODO probably stop using arrays in transition table due to null problem.
                   if(prev != null){
                       queue.addAll(prev);
                   }
               }
           }
           if(s.getDotPosition() == 0){
               int lhs = spec.pr.getLHS(s.getProduction());
               BitSet revProd = productionStepTables.getReverseProductionSteps(s.getState(),lhs);
               if (revProd != null){
                   for(int i = revProd.nextSetBit(0); i != -1; i = revProd.nextSetBit(i)){
                       LR0ItemSet itemSet = dfa.getItemSet(s.getState());
                       queue.add(new StateItem(s.getState(),itemSet.getProduction(i),itemSet.getPosition(i)));
                   }
               }
           }
        }
        return result;
    }

    /** places the followL set of a production into an arrayList
     * where followL is defined on page 4 of "Finding Counterexamples from Parsing Conflicts" by Isradisaikul & Myers
     *
     * @param production - the production in the LR0 item.
     *                   Represented as an index into {@link #symbolTable} and the pr field of {@link #spec}
     * @param dotPosition - the position of the dot in the LR0 item. 0-indexed.
     *                     dotPosition being the same as the index of a symbol on the RHS of a production means
     *                     the dot is directly to the left of that symbol.
     * @param lookaheadSet - the "L" in followL. The starting lookahead set.
     *                     Represented as a bitset of indices in the symbolTable.
     * @return The precise lookahead set after taking a production step from the input LR0 item.
     */
    public BitSet followL(int production, int dotPosition, BitSet lookaheadSet){
        int rhsLength = spec.pr.getRHSLength(production);

        //first Case: dot is directly before the last symbol
        if(dotPosition == rhsLength - 1){
            return lookaheadSet;
        }
        //second case: symbol two positions to the right is a non-terminal
        int sym = spec.pr.getRHSSym(production,dotPosition + 1);
        if(spec.terminals.get(sym)){
            BitSet preciseLookaheadSet = new BitSet();
            preciseLookaheadSet.set(sym);
            return preciseLookaheadSet;
        }

        //third case: symbol two positions to the right is a non-terminal that can derive the empty string
        if(!contextSets.isNullable(sym)){
            return contextSets.getFirst(sym);
        }
        //fourth case: symbol two positions to the right is a non-terminal that cannot derive the empty string.
        else {
            BitSet bs  = contextSets.getFirst(sym);
            BitSet bsp = followL(production,dotPosition+1,lookaheadSet);
            bs.or(bsp);
            return bs;
        }
    }

    //TODO this entire function is suspicious.
    // is there something i'm missing or is this just bad academic code?
    private Counterexample counterexampleFromShortestPath(ArrayList<StateItem> shortestPath){
        StateItem si = new StateItem(conflictState,conflictItem2.getProduction(),conflictItem2.getDotPosition());
        if(!isShiftReduce){
            ArrayList<StateItem> shortestPath2 = findShortestContextSensitivePath(si);
            Derivation deriv1 = nonUnifyingDerivFromPath(shortestPath);
            Derivation deriv2 = nonUnifyingDerivFromPath(shortestPath2);
            return new Counterexample(deriv1,deriv2,isShiftReduce);
        }
        // in the case of a shift reduce conflict, construct a second path via traversing backwards
        // along parts of the first shortestPath
        ListIterator<StateItem> itr = shortestPath.listIterator(shortestPath.size());
        //refSI is the last StateItem that has the last state in the shortest path
        StateItem refSI = itr.previous();
        StateItem prevRefSI = itr.hasPrevious() ? itr.previous() : null;
        LinkedList<StateItem> result = new LinkedList<>();
        result.add(si);
        while(refSI != null){
            LinkedList<StateItem> refsis = new LinkedList<>();
            //TODO remove this first check and see if it still works. It seems pointless.
            // that is, refSI is only used in the next if statement, and that seems to do the same thing?
            if(prevRefSI != null){
                int curPos = refSI.getDotPosition();
                int prevPos = prevRefSI.getDotPosition();
                while(prevRefSI != null && prevPos + 1 != curPos){
                    refsis.addFirst(prevRefSI);
                    curPos = prevPos;
                    if(itr.hasPrevious()){
                        prevRefSI = itr.previous();
                        prevPos = prevRefSI.getDotPosition();
                    } else {
                        prevRefSI = null;
                    }
                }
            }
            if (si == refSI || (refSI.getProduction() ==  spec.getStartProduction()&& refSI.getDotPosition() == 0) ) {
                // Reached common item; prepend to the beginning.
                refsis.remove(refsis.size() - 1);
                result.addAll(0, refsis);
                if (prevRefSI != null) result.addFirst(prevRefSI);
                while (itr.hasPrevious())
                    result.add(0, itr.previous());
                Derivation deriv1 =
                        nonUnifyingDerivFromPath(shortestPath);
                ArrayList<StateItem> path = new ArrayList<>(result);
                Derivation deriv2 = nonUnifyingDerivFromPath(path);
                return new Counterexample(deriv1,deriv2,isShiftReduce);
            }
            int pos = si.getDotPosition();
            if(pos == 0){
                // For a production item, find a sequence of items within the
                // same state that leads to this production.
                LinkedList<StateItem> init = new LinkedList<>();
                init.add(si);
                Queue<LinkedList<StateItem>> queue = new LinkedList<>();
                queue.add(init);
                while (!queue.isEmpty()) {
                    LinkedList<StateItem> sis = queue.remove();
                    StateItem siSource = sis.get(0);
                    if(siSource.getProduction() == spec.getStartProduction() && siSource.getDotPosition() == 0){
                        sis.removeLast();
                        result.addAll(0,sis);
                        si = siSource;
                        break;
                    }

                    int sourcePos = siSource.getDotPosition();
                    if(sourcePos > 0 ){
                        //make a transition if possible.
                        int symbol = spec.pr.getRHSSym(siSource.getProduction(),sourcePos - 1);
                        for(StateItem prevSI : transitionTables.getReverseTransitions(siSource,symbol)){
                            // Only look for state compatible with the shortest path.
                            if (prevSI.getState() != prevRefSI.getState()){
                                continue;
                            }
                            sis.removeLast();
                            result.addAll(0,sis);
                            result.addFirst(prevSI);
                            si = prevSI;
                            refSI = prevRefSI;
                            queue.clear();
                            break;

                        }
                    } else {
                        //take a production step if possible
                        int lhs = spec.pr.getLHS(siSource.getProduction());
                        LR0ItemSet itemSet = dfa.getItemSet(siSource.getState());
                        BitSet prodSteps = productionStepTables.getReverseProductionSteps(siSource.getState(), lhs);
                        for(int i = prodSteps.nextSetBit(0); i != -1; i = prodSteps.nextSetBit(i)){
                            StateItem prevSI = new StateItem(siSource.getState(), itemSet.getProduction(i),itemSet.getPosition(i));
                            if (sis.contains(prevSI)) continue;
                            LinkedList<StateItem> prevSIs = new LinkedList<>(sis);
                            prevSIs.add(0, prevSI);
                            queue.add(prevSIs);

                        }
                    }
                }

            } else {
                //TODO is the position correct here? may +1 or -1 of what it is
                int symbol = spec.pr.getRHSSym(si.getProduction(),pos-1);
                for(StateItem prevSI :  transitionTables.getReverseTransitions(si,symbol)){
                    if(prevSI.getState() != prevRefSI.getState()){
                        continue;
                    }
                    result.add(0, prevSI);
                    si = prevSI;
                    refSI = prevRefSI;
                    break;
                }
            }
        }
        throw new Error("Cannot find derivation to conflict state.");
    }

    private Derivation nonUnifyingDerivFromPath(ArrayList<StateItem> states){
        return nonUnifyingDerivFromPath(states, new LinkedList<Derivation>());
    }

    /**
     * creates a full derivation from a list of parser states to the conflict state
     * @param states the states along the shortest lookahead sensitive path
     * @param derivations any currently unfinished derivations that need to be filled out
     * @return
     */
    private Derivation nonUnifyingDerivFromPath(ArrayList<StateItem> states, LinkedList<Derivation> derivations){
        LinkedList<Derivation> result = new LinkedList<>();

        for (int i = states.size() - 1; i >= 0 ; i--) {
            boolean lookaheadRequired = false;
            StateItem stateItem = states.get(i);
            int pos = stateItem.getDotPosition();
            int prod = stateItem.getProduction();
            int len = spec.pr.getRHSLength(prod);

            if(result.isEmpty()){
                if (derivations.isEmpty()) {
                    result.add(Derivation.dot);
                    lookaheadRequired = true;
                }
                if(pos != len){
                    result.add(new Derivation(getSymbolString(prod,pos)));
                    lookaheadRequired = false;

                }
            }

            for(int j = pos + 1; j < len; j++){
                int symbol = spec.pr.getRHSSym(prod,j);
                if(lookaheadRequired){
                    if(symbol != conflictTerminal){
                        if(spec.nonterminals.get(symbol)){
                            if(!contextSets.isNullable(symbol) || contextSets.getFirst(symbol).get(conflictTerminal)){
                                //TODO ???
                                LinkedList <Derivation> nextDerivations =
                                        expandFirst(transitionTables.getTransition(stateItem,spec.pr.getRHSSym(prod,pos)));

                                result.addAll(nextDerivations);
                                i += nextDerivations.size() - 1;
                                lookaheadRequired = false;
                            }
                        } else {
                            //can't derive the the conflict terminal, must be some other prod
                            result.add(new Derivation(getSymbolString(symbol)));
                        }
                    } else {
                        result.add(new Derivation(getSymbolString(symbol)));
                        lookaheadRequired = false;
                    }

                } else {
                    result.add(new Derivation(getSymbolString(symbol)));
                }

            }

            //TODO try removing the derivations argument and see if it lessens the quality of counterexamples
            //symbols before dot
            Iterator<Derivation> derivationItr = derivations.descendingIterator();
            for (int j = pos - 1; j >= 0; j--) {
                result.addFirst(derivationItr.hasNext() ? derivationItr.next() : new Derivation(getSymbolString(prod,i)));

            }
            //complete the derivation
            int lhs = spec.pr.getLHS(prod);
            Derivation deriv = new Derivation(getSymbolString(spec.pr.getLHS(prod)), result);
            result = new LinkedList<>();
            result.add(deriv);
        }
        return result.getFirst();
    }

    /**
     * Repeatedly take production steps on the given StateItem so that the
     * first symbol of the derivation matches the conflict symbol.
     * @param start The StateItem to start with.
     * @return A sequence of derivation of {@code start} that ends with
     *          the conflict symbol.
     */
    private LinkedList<Derivation> expandFirst(StateItem start) {
        Queue<LinkedList<StateItem>> queue = new LinkedList<>();
        LinkedList<StateItem> init = new LinkedList<>();
        init.add(start);
        queue.add(init);

        //breadth-first search
        while(!queue.isEmpty()){
            LinkedList<StateItem> states = queue.remove();
            StateItem lastSI = states.getLast();
            int symbolAfterDot = spec.pr.getRHSSym(lastSI.getProduction(), lastSI.getDotPosition()+1);
            if(symbolAfterDot == conflictTerminal){
                //we're done
                LinkedList<Derivation> result = new LinkedList<>();
                result.add(new Derivation(getSymbolString(conflictTerminal)));
                for(int i = states.size() - 1; i >= 0 ; i--){
                    StateItem si = states.get(i);
                    int pos = si.getDotPosition();
                    int prod = si.getProduction();
                    if (pos == 0) {
                        int len = spec.pr.getRHSLength(prod);
                        for (int j = pos + 1; j < len; j++) {
                            result.add(new Derivation(getSymbolString(prod,j)));
                        }
                        int lhs = spec.pr.getLHS(prod);
                        Derivation deriv = new Derivation(getSymbolString(lhs), result);
                        result = new LinkedList<>();
                        result.add(deriv);
                    } else {
                        Derivation deriv = new Derivation(getSymbolString(prod,pos-1));
                        result.addFirst(deriv);
                    }
                }
                //TODO ??? why remove the first one
                result.removeFirst();
                return result;
            }
            if(spec.nonterminals.get(symbolAfterDot)){
                BitSet prodSteps = productionStepTables.getProductionSteps(lastSI);
                for(int i = prodSteps.nextSetBit(0); prodSteps.nextSetBit(i) != -1; i = prodSteps.nextSetBit(i)){
                    LR0ItemSet itemSet = dfa.getItemSet(lastSI.getState());
                    StateItem nextSI = new StateItem(lastSI.getState(),itemSet.getProduction(i),itemSet.getPosition(i));
                    if(states.contains(nextSI)){
                        continue;
                    }
                    LinkedList<StateItem> next = new LinkedList<>(states);
                    next.add(nextSI);
                    queue.add(next);
                }
                if(contextSets.isNullable(symbolAfterDot)){

                    StateItem nextSI = transitionTables.getTransition(lastSI,symbolAfterDot);
                    LinkedList<StateItem> next = new LinkedList<>(states);
                    next.add(nextSI);
                    queue.add(next);
                }
            }
        }
        throw new Error("Invalid state reached in expandFirst");
    }

    private String getSymbolString(int prod, int pos){
        return symbolTable.get(spec.pr.getRHSSym(prod,pos)).getDisplayName();
    }
    private String getSymbolString(int sym){
        return symbolTable.get(sym).getDisplayName();
    }


}
