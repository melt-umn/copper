package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0ItemSet;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadSets;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ContextSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import sun.awt.image.ImageWatched;

import java.util.*;

//TODO comment this whole file thoroughly. like spend a few hours on it.
//TODO review the structure of the additions (incl. what is private/protected/public)
// All/Most of the logic here should probably be in a builder/checker

/**
 * A digraph that is searched in the creation of non-unifying counterexamples to
 * non-lalr parsers. Does not use {@link edu.umn.cs.melt.copper.compiletime.spec.numeric.Digraph},
 * as the number of vertices is not initially known.
 * Wrapper around {@link LookaheadSensitiveGraphVertex}
 */
public class CounterexampleSearchGraphs {

    protected static final int PRODUCTION_COST = 50;
    protected static final int REDUCE_COST = 1;
    protected static final int SHIFT_COST = 1;
    protected static final int UNSHIFT_COST = 1;

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
    private LRLookaheadSets lookaheadSets;

    private TransitionFunctionTables transitionTables;
    private ProductionStepTables productionStepTables;

    //TODO the structure of where the counterExampleMessage begins and this ends is all sorts of wrong. Reassess once more code is written.
    public CounterexampleSearchGraphs(LRParseTableConflict conflict,
                                      ParserSpec spec, PSSymbolTable symbolTable,
                                      ContextSets contextSets, LRLookaheadSets lookaheadSets,
                                      LR0DFA dfa) {

        conflictState = conflict.getState();
        this.conflictTerminal = conflict.getSymbol();
        this.spec = spec;
        this.symbolTable = symbolTable;
        this.contextSets = contextSets;
        this.dfa = dfa;
        this.lookaheadSets = lookaheadSets;

        //If the conflict is shift/reduce, then conflictItem1 is the shift item.
        int conflictItem1Production = -1;
        int conflictItem1Position = -1;
        BitSet conflictItem1Lookahead = null;

        int conflictItem2Production = -1;
        int conflictItem2Position = -1;
        BitSet conflictItem2Lookahead = null;

        isShiftReduce = conflict.shift != -1;
        LR0ItemSet conflictStateItems = dfa.getItemSet(conflictState);
        if(isShiftReduce){
            conflictItem1Production = conflict.reduce.nextSetBit(0);

            //find dot position for the reduce production
            for(int i = 0; i<conflictStateItems.size(); i++){
                if(conflictStateItems.getProduction(i) == conflictItem1Production){
                    conflictItem1Position = conflictStateItems.getPosition(i);
                    conflictItem1Lookahead = lookaheadSets.getLookahead(conflictState,i);
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
                    conflictItem2Lookahead = lookaheadSets.getLookahead(conflictState,i);
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
                    conflictItem1Lookahead = lookaheadSets.getLookahead(conflictState,i);
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
                    conflictItem2Lookahead = lookaheadSets.getLookahead(conflictState,i);
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
                conflictItem1Position,
                conflictItem1Lookahead);

        conflictItem2 = new StateItem(conflictState,
                conflictItem2Production,
                conflictItem2Position,
                conflictItem2Lookahead);

        BitSet initLookaheadSet = new BitSet();
        initLookaheadSet.set(spec.getEOFTerminal());
        this.startVertex = new LookaheadSensitiveGraphVertex(1,spec.getStartProduction(),0,initLookaheadSet);

        this.transitionTables = new TransitionFunctionTables(dfa,spec,lookaheadSets);
        this.productionStepTables = new ProductionStepTables(dfa,spec,lookaheadSets);
    }


    //TODO
    public Counterexample attemptUnifyingCounterexample(){
        return null;
    }


    public Counterexample getNonUnifyingCounterexample(){
        ArrayList<StateItem> shortestContextSensitivePath = findShortestContextSensitivePath(conflictItem1);
        return counterexampleFromShortestPath(shortestContextSensitivePath);
    }


    //TODO figure out where to put this
    /**
     * Compute a set of StateItems that can make a transition on the given
     * symbol to the given StateItem such that the resulting possible lookahead
     * symbols are as given.
     * @param si the stateItem to calculate transitions to
     * @param lookahead The expected possible lookahead symbols
     * @param guide If not null, restricts the possible parser states to this
     *          set; otherwise, explore all possible parser states that can
     *          make the desired transition.
     * @return A LinkedList of StateItems that result from making a reverse transition
     *          from this StateItem on the given symbol and lookahead set.
     */
    //TODO
    protected LinkedList<StateItem> reverseTransition(StateItem si, BitSet lookahead, BitSet guide){
        LinkedList<StateItem> result = new LinkedList<>();
        //TODO need to add null here?
        LinkedList<StateItem> init = new LinkedList<>();
        init.add(si);

        if(si.getDotPosition() > 0){
            Set<StateItem> prevs = transitionTables.revTrans.get(si);
            if(prevs == null) {
                return result;
            }
            for(StateItem prev : prevs){
                if(guide != null && !guide.get(prev.getState())){
                    continue;
                }
                if(lookahead != null && !lookahead.intersects(prev.getLookahead())){
                    continue;
                }
                result.add(prev);
            }
            return result;
        }
        for(LookaheadSensitiveGraphVertex g : reverseProduction(si,si.getLookahead())){
            result.add(g.stateItem);
        }
        return result;
    }

    protected LinkedList<LookaheadSensitiveGraphVertex> reverseProduction(StateItem si, BitSet lookahead){
        LinkedList<LookaheadSensitiveGraphVertex> result = new LinkedList<>();
        BitSet[] revProd = productionStepTables.revProdTable[si.getState()];
        LR0ItemSet lr0Items = dfa.getItemSet(si.getState());

        if(revProd == null){
            return result;
        }

        int prod = si.getProduction();
        BitSet prevs = revProd[spec.pr.getLHS(prod)];

        if(prevs == null){
            return result;
        }

        for(int i = prevs.nextSetBit(0); i>=0; i = prevs.nextSetBit(i+1)){
            int prevProd = lr0Items.getProduction(i);
            int prevDotPos = lr0Items.getPosition(i);
            BitSet prevLookahead = lookaheadSets.getLookahead(si.getState(),i);
            //there's a check in the reference implementation here to see if prevProd can come before prod
            //but it only checks precedence, which doesn't seem relevant here.
            StateItem prevSi = new StateItem(si.getState(),prevProd,prevDotPos,prevLookahead);
            BitSet nextLookahead =  new BitSet();

            int prevLen = spec.pr.getRHSLength(prevProd);

            //reduce item
            if(prevDotPos == prevLen){
                if(!prevLookahead.intersects(lookahead)){
                    continue;
                }
                nextLookahead.or(prevLookahead);
                nextLookahead.and(lookahead);
            }
            //shift item
            else {
                //TODO
                if(lookahead != null){
                    //TODO comment this logic
                    boolean applicable = false;
                    boolean nullable = true;
                    for (int pos = prevDotPos; !applicable && nullable && pos < prevLen; pos++) {
                        int nextSym = spec.pr.getRHSSym(prevProd,pos);
                        if(spec.terminals.get(nextSym)){
                            applicable = terminalIntersects(nextSym,lookahead);
                            nullable = false;
                        } else if(spec.nonterminals.get(nextSym)){
                            applicable = lookahead.intersects(contextSets.getFirst(nextSym));
                            if(!applicable){
                                nullable = contextSets.isNullable(nextSym);
                            }
                        }
                    }
                    if (!applicable && !nullable) {
                        continue;
                    }
                }
                nextLookahead = prevLookahead;
            }
            result.add(new LookaheadSensitiveGraphVertex(prevSi,nextLookahead));
        }
        return result;
    }

    //TODO comment
    private boolean terminalIntersects(int term, BitSet lookahead) {
        for(int i = lookahead.nextSetBit(0); i>=0; i = lookahead.nextSetBit(i+1)){
            if(spec.terminals.get(i) && term == i){
                return true;
            }
            if(spec.nonterminals.get(i) && contextSets.getFirst(i).get(term)){
                return true;
            }
        }
        return false;
    }

    //todo comment
    public ArrayList<StateItem> findShortestContextSensitivePath(StateItem target){
        Set<StateItem> possibleStateItems = eligibleStateItems(target);

        Queue<LinkedList<LookaheadSensitiveGraphVertex>> queue = new LinkedList<>();
        Set<LookaheadSensitiveGraphVertex> visited = new HashSet<>();

        LinkedList<LookaheadSensitiveGraphVertex> init = new LinkedList<>();
        init.add(startVertex);
        queue.add(init);

        //unguided breadth-first search
        while(!queue.isEmpty()){
            LinkedList<LookaheadSensitiveGraphVertex> path = queue.remove();
            LookaheadSensitiveGraphVertex last = path.getLast();
            if (visited.contains(last)) {
                continue;
            }
            visited.add(last);
            if(target.equals(last.stateItem) && last.lookahead.get(conflictTerminal)){
                //TODO print process info and such
                //success, copy to ArrayList for efficient access
                ArrayList<StateItem> shortestConflictPath = new ArrayList<>(path.size());
                for (LookaheadSensitiveGraphVertex v : path){
                    shortestConflictPath.add(v.stateItem);
                }
                return shortestConflictPath;
            }
            //Add all transitions to the search queue
            if(transitionTables.trans.get(last.stateItem) != null){
                for(StateItem tranDst : transitionTables.trans.get(last.stateItem)){
                    if(tranDst == null){
                        continue;
                    }
                    //TODO maybe using an array here isn't great if it's almost always null...
                    if(!possibleStateItems.contains(tranDst)){
                        continue;
                    }
                    LookaheadSensitiveGraphVertex next = new LookaheadSensitiveGraphVertex(tranDst,last.lookahead);
                    LinkedList<LookaheadSensitiveGraphVertex> nextPath = new LinkedList<>(path);
                    nextPath.add(next);
                    queue.add(nextPath);
                }
            }
            if(productionStepTables.getProdSteps(last.stateItem) != null){
                int len = spec.pr.getRHSLength(last.getProduction());
                BitSet newLookahead = followL(last.getProduction(),last.getDotPosition(),last.lookahead);
                BitSet productionSteps = productionStepTables.getProdSteps(last.stateItem);

                //for each possible item reached via a production step
                LR0ItemSet stateItems = dfa.getItemSet(last.getState());
                for(int i = productionSteps.nextSetBit(0); i >= 0; i = productionSteps.nextSetBit(i+1)) {
                    //TODO refactor to use a memoized lookup table if this uses too much memory
                    BitSet l = lookaheadSets.getLookahead(last.getState(),i);
                    StateItem pStateItem = new StateItem(last.getState(),stateItems.getProduction(i),stateItems.getPosition(i),l);
                    //TODO fix possibleStateItems and re-add this check
                    if(!possibleStateItems.contains(pStateItem)){
                        continue;
                    }
                    LookaheadSensitiveGraphVertex next = new LookaheadSensitiveGraphVertex(pStateItem,newLookahead);
                    LinkedList<LookaheadSensitiveGraphVertex> nextPath = new LinkedList<>(path);
                    nextPath.add(next);
                    queue.add(nextPath);
                }

            }
        }
        throw new Error("Cannot find shortest path along lookahead sensitive graph");
    }

    //TODO comment
    private Set<StateItem> eligibleStateItems(StateItem target){
        //It's between using a hashSet and creating a flat array of all possible stateItems with arbitrary positions
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
                queue.addAll(transitionTables.revTrans.get(s));
            }
            if(s.getDotPosition() == 0){
                int lhs = spec.pr.getLHS(s.getProduction());
                BitSet revProd = productionStepTables.getRevProdSteps(s.getState(),lhs);
                if (revProd != null){
                    for(int i = revProd.nextSetBit(0); i >= 0; i = revProd.nextSetBit(i+1)){
                        LR0ItemSet itemSet = dfa.getItemSet(s.getState());
                        BitSet l = lookaheadSets.getLookahead(s.getState(),i);
                        queue.add(new StateItem(s.getState(),itemSet.getProduction(i),itemSet.getPosition(i),l));
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

    private Counterexample counterexampleFromShortestPath(ArrayList<StateItem> shortestPath){
        //TODO modify to do unifying examples
        StateItem si = new StateItem(conflictState,conflictItem2.getProduction(),conflictItem2.getDotPosition(),conflictItem2.getLookahead());
        if(!isShiftReduce){
            ArrayList<StateItem> shortestPath2 = findShortestContextSensitivePath(si);
            Derivation deriv1 = nonUnifyingDerivFromPath(shortestPath);
            Derivation deriv2 = nonUnifyingDerivFromPath(shortestPath2);
            return new Counterexample(deriv1,deriv2,isShiftReduce);
        }
        ArrayList<StateItem> shiftConflictPath = findShiftConflictPath(shortestPath);
        Derivation deriv1 = nonUnifyingDerivFromPath(shortestPath);
        Derivation deriv2 = nonUnifyingDerivFromPath(shiftConflictPath);
        return new Counterexample(deriv1,deriv2,isShiftReduce);
    }

    private ArrayList<StateItem> findShiftConflictPath(ArrayList<StateItem> shortestPath) {
        //Perform a breadth first search to find a path from the shift conflict stateItem to the start stateItem
        //We use information from the shortest path to limit the search, only adding states if they are
        //(reverse) production steps, or reverse transitions to a state along the shortest path.
        //The order of the states in the path should be identical, the differences being caused by taking different production steps

        //We need to get a list of all of the states in the shortest path without repetition,
        //as having the shift path maintain that order is how we keep the search space reasonable
        ArrayList<Integer> shortestPathStates = new ArrayList<>();
        shortestPathStates.add(shortestPath.get(0).getState());
        for (StateItem s: shortestPath) {
            if(s.getState() != shortestPathStates.get(shortestPathStates.size()-1)){
                shortestPathStates.add(s.getState());
            }
        }

        //We get what states are valid to transition to by
        Queue<LinkedList<ShiftConflictSearchNode>> queue = new LinkedList<>();
        LinkedList<ShiftConflictSearchNode> startPath = new LinkedList<>();
        startPath.addFirst(new ShiftConflictSearchNode(shortestPathStates.size()-2,false,conflictItem2));
        queue.add(startPath);

        while(!queue.isEmpty()){
            LinkedList<ShiftConflictSearchNode> path = queue.remove();
            ShiftConflictSearchNode head = path.getFirst();

            //TODO if the head stateItem is in the shortest path, we should finish and re-use the shortest path from there.
            //TODO fix the stateItem equality function, this is dumb
            if(head.getStateItem().getState() == startVertex.getState() &&
                    head.getStateItem().getProduction() == startVertex.getProduction() &&
                    head.getStateItem().getDotPosition() == startVertex.getDotPosition()){
                ArrayList<StateItem> result = new ArrayList<>(path.size());

                //extract the stateItems, move to an arrayList
                for(ShiftConflictSearchNode n : path){
                    result.add(n.getStateItem());
                }

                return result;
            }

            //Consider production steps only if the current path doesn't start with a production step itself
            //Otherwise we would add the same production step items over and over
            //TODO i don't think this needs to be in the node, I think any production item with dotPosition == 0 is a prod. item
            if(!head.isProductionItem()){
                BitSet revProd =
                        productionStepTables.getRevProdSteps(head.getStateItem().getState(),
                                                             spec.pr.getLHS(head.getStateItem().getProduction()));
                if(revProd == null){
                } else {
                    LR0ItemSet itemSet = dfa.getItemSet(head.getStateItem().getState());
                    //For each production step
                    for(int i = revProd.nextSetBit(0); i >= 0; i = revProd.nextSetBit(i+1)){
                        int newState = head.getStateItem().getState();
                        BitSet l = lookaheadSets.getLookahead(newState,i);
                        StateItem newStateItem = new StateItem(newState,itemSet.getProduction(i),itemSet.getPosition(i),l);
                        LinkedList<ShiftConflictSearchNode> newPath = new LinkedList<>();
                        //add a new path with that production step item as the head to the queue
                        newPath.addAll(path);
                        newPath.addFirst(new ShiftConflictSearchNode(head.getValidStateIndex(),true,newStateItem));
                        queue.add(newPath);
                    }
                }
            }

            //consider reverse transition items
            //No way to transition to these, must have been a production item (at some point)
            if(head.getStateItem().getDotPosition() == 0 ){
                continue;
            }
            Set<StateItem> revTran = transitionTables.revTrans.get(head.getStateItem());
            for(StateItem s : revTran){
                //only add states that are in the correct position along the shortest path
                if(s.getState() == shortestPathStates.get(head.getValidStateIndex())){
                    LinkedList<ShiftConflictSearchNode> newPath = new LinkedList<>();
                    newPath.addAll(path);
                    newPath.addFirst(new ShiftConflictSearchNode(head.getValidStateIndex()-1,false,s));
                    queue.add(newPath);
                }
            }
        }
        throw new Error("failed to find shift conflict path");
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
    //TODO this entire function is a mess and needs a bunch of comments
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

            // I don't think this can handle reduction items (where the dot at the end) correctly.
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
                                j += nextDerivations.size() - 1;
                                lookaheadRequired = false;
                            } else {
                                //can't derive the the conflict terminal, must be some other prod
                                result.add(new Derivation(getSymbolString(symbol)));
                            }
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
                if(i>0){
                    i--;
                }
                result.addFirst(derivationItr.hasNext() ? derivationItr.next() : new Derivation(getSymbolString(prod,j)));
            }

            //complete the derivation
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
                BitSet prodSteps = productionStepTables.getProdSteps(lastSI);
                for(int i = prodSteps.nextSetBit(0); i >= 0; i = prodSteps.nextSetBit(i+1)){
                    LR0ItemSet itemSet = dfa.getItemSet(lastSI.getState());
                    BitSet lookahead = lookaheadSets.getLookahead(lastSI.getState(),i);
                    StateItem nextSI = new StateItem(lastSI.getState(),itemSet.getProduction(i),itemSet.getPosition(i),lookahead);
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

    //TODO where to put this...
    protected class UnifiedSearchState {
        //a list of derivations that simulates the  parse stack,
        //and a list of state items representing the state transitions the parser takes (with explicit production steps)
        //one of each for each part of the product parser.
        protected LinkedList<Derivation> derivs1, derivs2;
        protected LinkedList<StateItem> states1, states2;

        /**
         * number of states and production steps the parser has had to encounter.
         */
        protected int complexity;
        /**
         * The number of production steps made since the reduce conflict item.
         * If this is -1, the reduce conflict item has been completed.
         */
        protected int reduceDepth;
        /**
         * The number of production steps made since the shift conflict item.
         * If this is -1, the shift conflict item has been completed and
         * reduced.
         */
        protected int shiftDepth;

        protected UnifiedSearchState(StateItem si1, StateItem si2) {
            derivs1 = new LinkedList<>();
            derivs2 = new LinkedList<>();
            states1 = new LinkedList<>();
            states2 = new LinkedList<>();
            states1.add(si1);
            states2.add(si2);
            complexity = 0;
            reduceDepth = 0;
            shiftDepth = 0;
        }

        private UnifiedSearchState(LinkedList<Derivation> derivs1, LinkedList<Derivation> derivs2,
                                   LinkedList<StateItem> states1, LinkedList<StateItem> states2,
                                   int complexity, int reduceDepth, int shiftDepth) {
            this.states1 = new LinkedList<>(states1);
            this.derivs1 = new LinkedList<>(derivs1);

            this.states2 = new LinkedList<>(states2);
            this.derivs2 = new LinkedList<>(derivs2);

            this.complexity = complexity;
            this.reduceDepth = reduceDepth;
            this.shiftDepth = shiftDepth;
        }
        //TODO is this necessary?

        /**
         * Duplicate a search state.
         */
        protected UnifiedSearchState copy() {
            return new UnifiedSearchState(derivs1,
                    derivs2,
                    states1,
                    states2,
                    complexity,
                    reduceDepth,
                    shiftDepth);
        }

        /**
         * prepend a symbol to the current configuration if possible
         *
         * @param sym   the symbol to add.
         * @param guide If not null, restricts the possible parser states to this set;
         *              otherwise, explore all possible parser states that can make the desired transition.
         * @return A List of search states that result from prepending successfully (can be empty).
         */
        protected LinkedList<UnifiedSearchState> prepend(int sym, BitSet guide) {
            StateItem si1src = states1.getFirst();
            StateItem si2src = states2.getFirst();

            BitSet si1Lookahead = si1src.getLookahead();
            BitSet si2Lookahead = si2src.getLookahead();

            LinkedList<UnifiedSearchState> result = new LinkedList<>();

            //
            if (transitionTables.prevSymbol[si1src.getState()] != sym || transitionTables.prevSymbol[si2src.getState()] != sym) {
                return result;
            }

            LinkedList<StateItem> prev1ext = reverseTransition(si1src, si1Lookahead, guide);
            LinkedList<StateItem> prev2ext = reverseTransition(si2src, si2Lookahead, guide);

            for (StateItem prevSI1 : prev1ext) {
                for (StateItem prevSI2 : prev1ext) {
                    boolean prev1IsSrc = prevSI1 == si1src;
                    boolean prev2IsSrc = prevSI2 == si2src;
                    if (prev1IsSrc && prev2IsSrc) {
                        continue;
                    }
                    if (prevSI1.getState() == prevSI2.getState()) {
                        continue;
                    }
                    UnifiedSearchState copy = this.copy();

                    if (prev1IsSrc) {
                        copy.states1.addFirst(prevSI1);
                    }
                    if (prev2IsSrc) {
                        copy.states2.addFirst(prevSI2);
                    }
                    //TODO the placement of the ifs seems somewhat suspect
                    // just seems like a lot is done computed multiple times
                    if (!prev1IsSrc
                            && copy.states1.get(0).getDotPosition() + 1 == copy.states1.get(1).getDotPosition()) {
                        if (!prev2IsSrc
                                && copy.states2.get(0).getDotPosition() + 1 == copy.states2.get(1).getDotPosition()) {
                            Derivation deriv = new Derivation(getSymbolString(sym));
                            copy.derivs1.addFirst(deriv);
                            copy.derivs2.addFirst(deriv);

                        } else {
                            continue;
                        }
                    } else if (!prev2IsSrc
                            && copy.states2.get(0).getDotPosition() + 1 == copy.states2.get(1).getDotPosition()) {
                        continue;
                    }
                    int prependSize = (prev1IsSrc ? 0 : 1) + (prev2IsSrc ? 0 : 1);
                    //the number of production steps taken from prevNSI to siNsrc
                    int productionSteps =
                            (!prev1IsSrc && prevSI1.getState() == si1src.getState() ? 1 : 0) +
                                    (!prev2IsSrc && prevSI2.getState() == si2src.getState() ? 1 : 0);
                    copy.complexity +=
                            UNSHIFT_COST * (prependSize - productionSteps) + PRODUCTION_COST * productionSteps;
                    result.add(copy);
                }
            }
            return result;
        }

        protected LinkedList<UnifiedSearchState> reduce1(Integer sym) {
            //If not a reduce item
            StateItem lastItem = states1.getLast();
            if (lastItem.getDotPosition() == spec.pr.getRHSLength(lastItem.getProduction())) {
                throw new Error("cannot reduce non-reduce item in search state" + this);
            }

            LinkedList<UnifiedSearchState> result = new LinkedList<>();
            BitSet symbolSet;

            if (sym != null) {
                if (!lastItem.getLookahead().get(sym)) {
                    return result;
                }
                symbolSet = new BitSet();
                symbolSet.set(sym);
            } else {
                symbolSet = lastItem.getLookahead();
            }
            int prod = lastItem.getProduction();
            int lhs = spec.pr.getLHS(prod);
            int len = spec.pr.getRHSLength(prod);
//            int derivSize = derivs1.size();
            Derivation deriv = new Derivation(getSymbolString(lhs),
                    new LinkedList<Derivation>(derivs1.subList(derivs1.size() - len, derivs1.size())));

            if (reduceDepth == 0) {
                // We are reducing the reduce conflict item.
                // Add a dot for visual inspection of the resulting counterexample.
                // Add a dot for visual inspection of the resulting counterexample.
                derivs1.add(conflictItem1.getDotPosition(), Derivation.dot);
            }
            derivs1 = new LinkedList<>(derivs1.subList(0, derivs1.size() - len));
            derivs1.add(deriv);
            if (states1.size() == len + 1) {
                LinkedList<LookaheadSensitiveGraphVertex> prev = reverseProduction(states1.getFirst(), symbolSet);
                for (LookaheadSensitiveGraphVertex prevV : prev) {
                    UnifiedSearchState copy = copy();
                    copy.states1 = new LinkedList<>(states1.subList(0, states1.size() - len - 1));
                    copy.states1.addFirst(prevV.stateItem);
                    copy.states1.add(transitionTables.trans.get(copy.states1.getLast())[lhs]);
                    int statesSize = copy.states1.size();
                    int productionSteps = productionSteps(copy.states1, states1.get(0));
                    copy.complexity +=
                            UNSHIFT_COST * (statesSize - productionSteps) + PRODUCTION_COST * productionSteps;
                    if (copy.reduceDepth >= 0) {
                        copy.reduceDepth--;
                    }
                    result.add(copy);
                }
            } else {
                UnifiedSearchState copy = copy();
                copy.states1 = new LinkedList<>(states1.subList(0, states1.size() - len - 1));
                copy.states1.add(transitionTables.trans.get(copy.states1.getLast())[lhs]);
                copy.complexity += REDUCE_COST;
                if (copy.reduceDepth >= 0) {
                    copy.reduceDepth--;
                }
                result.add(copy);
            }
            // transition on nullable symbols
            LinkedList<UnifiedSearchState> finalizedResult = new LinkedList<>();
            for(UnifiedSearchState ss : result){
                StateItem next = ss.states1.getLast();
                List<Derivation> derivs1 = new LinkedList<>();
                List<StateItem> states1 = new LinkedList<>();
                //TODO
                nullableClosure(next.getProduction(),
                        next.getDotPosition(),
                        next,
                        states1,
                        derivs1);
                finalizedResult.add(ss);
                for (int i = 1, size1 = derivs1.size(); i <= size1; i++) {
                    List<Derivation> subderivs1 =
                            new ArrayList<>(derivs1.subList(0, i));
                    List<StateItem> substates1 =
                            new ArrayList<>(states1.subList(0, i));
                    UnifiedSearchState copy = ss.copy();
                    copy.derivs1.addAll(subderivs1);
                    copy.states1.addAll(substates1);
                    finalizedResult.add(copy);
                }
            }
            return finalizedResult;
        }
        //this is a really stupid hack that i'm only using because this is what they do in the reference impl.
        //TODO make this and reduce1 one function with an additional parameter
        protected LinkedList<UnifiedSearchState> reduce2(Integer sym) {
            //If not a reduce item
            StateItem lastItem = states2.getLast();
            if (lastItem.getDotPosition() == spec.pr.getRHSLength(lastItem.getProduction())) {
                throw new Error("cannot reduce non-reduce item in search state" + this);
            }

            LinkedList<UnifiedSearchState> result = new LinkedList<>();
            BitSet symbolSet;

            if (sym != null) {
                if (!lastItem.getLookahead().get(sym)) {
                    return result;
                }
                symbolSet = new BitSet();
                symbolSet.set(sym);
            } else {
                symbolSet = lastItem.getLookahead();
            }
            int prod = lastItem.getProduction();
            int lhs = spec.pr.getLHS(prod);
            int len = spec.pr.getRHSLength(prod);
//            int derivSize = derivs2.size();
            Derivation deriv = new Derivation(getSymbolString(lhs),
                    new LinkedList<Derivation>(derivs2.subList(derivs2.size() - len, derivs2.size())));

            if (reduceDepth == 0) {
                // We are reducing either the second reduce item, or the shift reduce item.
                // (depending on if this is a reduce/reduce or a shift/reduce conflict)
                // Add a dot for visual inspection of the resulting counterexample.
                derivs2.add(conflictItem2.getDotPosition(), Derivation.dot);
            }
            derivs2 = new LinkedList<>(derivs2.subList(0, derivs2.size() - len));
            derivs2.add(deriv);
            if (states2.size() == len + 1) {
                LinkedList<LookaheadSensitiveGraphVertex> prev = reverseProduction(states2.getFirst(), symbolSet);
                for (LookaheadSensitiveGraphVertex prevV : prev) {
                    UnifiedSearchState copy = copy();
                    copy.states2 = new LinkedList<>(states2.subList(0, states2.size() - len - 1));
                    copy.states2.addFirst(prevV.stateItem);
                    copy.states2.add(transitionTables.trans.get(copy.states2.getLast())[lhs]);
                    int statesSize = copy.states2.size();
                    int productionSteps = productionSteps(copy.states2, states2.get(0));
                    copy.complexity +=
                            SHIFT_COST * (statesSize - productionSteps) + PRODUCTION_COST * productionSteps;
                    if (copy.shiftDepth >= 0) {
                        copy.shiftDepth--;
                    }
                    result.add(copy);
                }
            } else {
                UnifiedSearchState copy = copy();
                copy.states2 = new LinkedList<>(states2.subList(0, states2.size() - len - 1));
                copy.states2.add(transitionTables.trans.get(copy.states2.getLast())[lhs]);
                copy.complexity += REDUCE_COST;
                if (copy.shiftDepth >= 0) {
                    copy.shiftDepth--;
                }
                result.add(copy);
            }
            // transition on nullable symbols
            LinkedList<UnifiedSearchState> finalizedResult = new LinkedList<>();
            for(UnifiedSearchState ss : result){
                StateItem next = ss.states2.getLast();
                List<Derivation> derivs2 = new LinkedList<>();
                List<StateItem> states2 = new LinkedList<>();
                //TODO
                nullableClosure(next.getProduction(),
                        next.getDotPosition(),
                        next,
                        states2,
                        derivs2);
                finalizedResult.add(ss);
                for (int i = 1, size2 = derivs2.size(); i <= size2; i++) {
                    List<Derivation> subderivs2 =
                            new ArrayList<>(derivs2.subList(0, i));
                    List<StateItem> substates2 =
                            new ArrayList<>(states2.subList(0, i));
                    UnifiedSearchState copy = ss.copy();
                    copy.derivs2.addAll(subderivs2);
                    copy.states2.addAll(substates2);
                    finalizedResult.add(copy);
                }
            }
            return finalizedResult;
        }

        @Override
        public String toString() {
            return "UnifiedSearchState{" +
                    "derivs1=" + derivs1 +
                    ", states1=" + states1 +
                    ", derivs2=" + derivs2 +
                    ", states2=" + states2 +
                    ", complexity=" + complexity +
                    ", reduceDepth=" + reduceDepth +
                    ", shiftDepth=" + shiftDepth +
                    '}';
        }
    }

    private void nullableClosure(int production, int dotPosition, StateItem lastSI,
                                 List<StateItem> states, List<Derivation> derivs){
        int len = spec.pr.getRHSLength(production);
        for(int curPos = dotPosition; curPos < len; curPos++){
            int sp = spec.pr.getRHSSym(production,curPos);
            if(!spec.nonterminals.get(sp)){
                break;
            }
            if(!contextSets.isNullable(sp)){
                break;
            }
            lastSI = transitionTables.trans.get(lastSI)[sp];
            derivs.add(new Derivation(getSymbolString(sp), new LinkedList<Derivation>()));
            states.add(lastSI);
        }
    }

    //TODO no point in having last? It seems that you could just go forward.
    protected static int productionSteps(LinkedList<StateItem> stateItems, StateItem last){
        int count = 0;
        int lastState = last.getState();
        Iterator<StateItem> itr =  stateItems.descendingIterator();
        while(itr.hasNext()){
            int state = itr.next().getState();
            if(state == lastState){
                count++;
            }
            lastState = state;
        }
        return count;
    }

}
