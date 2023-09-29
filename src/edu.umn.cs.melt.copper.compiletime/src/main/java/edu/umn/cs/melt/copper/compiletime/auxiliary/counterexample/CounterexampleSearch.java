package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0ItemSet;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadSets;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ContextSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import java.util.*;

/**
 * The business logic for finding counter examples for parsing conflicts.
 * Reading "Finding Counterexamples from Parsing Conflicts" by Isradisaikul & Myers 
 * is highly recommended before attempting to edit this code.
 * This is a more or less direct implementation of the process discussed in that paper.
 * For the sake of complexity, this implements only non-unifying counterexamples.
 * @author Kelton OBrien
 */
public class CounterexampleSearch {

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

    ArrayList<StateItem> shortestLookaheadSensitivePath;
    //The set of all the states in the shortest context-sensitive path
    private BitSet shortestLookaheadSensitiveSet = new BitSet();
    private BitSet reduceProductionSet = new BitSet();

    public CounterexampleSearch(LRParseTableConflict conflict,
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
            System.out.println(conflict.getState());
            System.out.println(conflict.getSymbol());
            conflictItem1Production = conflict.reduce.nextSetBit(0);
            conflictItem1Position = spec.pr.getRHSLength(conflictItem1Production);

            //find the lookaheadSet for the reduce production
            for(int i = 0; i<conflictStateItems.size(); i++){
                if(conflictStateItems.getProduction(i) == conflictItem1Production &&
                    conflictStateItems.getPosition(i) == conflictItem1Position){
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
                //If the dot is directly after the conflict terminal, it is the shift conflict item
                if(spec.pr.getRHSLength(prod) > pos && spec.pr.getRHSSym(prod,pos) == conflictTerminal){
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
            conflictItem1Production = conflict.reduce.nextSetBit(0);

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
        shortestLookaheadSensitivePath = findShortestLookaheadSensitivePath(conflictItem1);

        boolean reduceProdReached = false;
        for (StateItem si : shortestLookaheadSensitivePath) {
            shortestLookaheadSensitiveSet.set(si.getState());
            reduceProdReached =
                    reduceProdReached || si.getProduction() == conflictItem1Production;
            if (reduceProdReached) {
                reduceProductionSet.set(si.getState());
            }
        }
    }

    /**
     * Simulates 2 parsers on the same input in order to generate two derivations of the same tokens.
     * If this fails, 2 derivations that are the same up to the point of error (but potentially different afterwards)
     * are generated.
     * @return A unified or non-unified counterexample
     */
    public Counterexample getExample(){
        return counterexampleFromShortestPath();
    }


    /**
     * Compute a list of StateItems that can make a transition on the given
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
    protected LinkedList<StateItem> reverseTransition(StateItem si, BitSet lookahead, BitSet guide){
        LinkedList<StateItem> result = new LinkedList<>();
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
        result.addAll(reverseProduction(si,si.getLookahead()));
        return result;
    }

    /**
     * Computes a list of possible StateItems that could take a production step to reach the given StateItem
     * @param si the StateItem the output StateItems can take a production step to reach
     * @param lookahead a guide to restrict the output StateItems to only those that si can reach
     * @return A list of StateItems that can take a production step to reach {@code si}
     */
    protected LinkedList<StateItem> reverseProduction(StateItem si, BitSet lookahead){
        LinkedList<StateItem> result = new LinkedList<>();
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

        //for every production item that can take a production step to the input StateItem
        for(int i = prevs.nextSetBit(0); i>=0; i = prevs.nextSetBit(i+1)){
            int prevProd = lr0Items.getProduction(i);
            int prevDotPos = lr0Items.getPosition(i);
            BitSet prevLookahead = lookaheadSets.getLookahead(si.getState(),i);
            //there's a check in the reference implementation here to see if prevProd can come before prod
            // but it only checks precedence, which doesn't seem relevant here.
            StateItem prevSi = new StateItem(si.getState(),prevProd,prevDotPos,prevLookahead);
            int prevLen = spec.pr.getRHSLength(prevProd);

            //reduce item
            if(prevDotPos == prevLen){
                if(lookahead != null && !prevLookahead.intersects(lookahead)){
                    continue;
                }
            }
            //shift item
            else {
                if(lookahead != null){
                    boolean applicable = false;
                    boolean nullable = true;
                    //for every symbol on the RHS of the dot in the production item that can take a production step to
                    // the input StateItem
                    for (int pos = prevDotPos; !applicable && nullable && pos < prevLen; pos++) {
                        //NOTE: this used to have a +1, but that seemed to cause errors.
                        int nextSym = spec.pr.getRHSSym(prevProd,pos);
                        //the next expected symbol is a terminal, so it is not nullable by definition
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
            }
            result.add(prevSi);
        }
        return result;
    }

    /**
     * Calculates if a terminal is either in a lookahead set, or if it can be derived by a non-terminal in the set
     * @param term the terminal to check
     * @param lookahead the lookahead set to see if the terminal is inside
     * @return if the terminal is in or derivable by something in the lookahead set
     */
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


    /**
     * Determines if two state items are the same up to the dot.
     * @return if the state items have the same symbols up to the dot.
     */
    private boolean hasCommonPrefex(StateItem si1, StateItem si2) {
        if(si1.getDotPosition() != si2.getDotPosition()){
            return false;
        }
        for(int i = 0; i < si1.getDotPosition(); i++){
            if(spec.pr.getRHSSym(si1.getProduction(),i) != spec.pr.getRHSSym(si2.getProduction(),i)){
                return false;
            }
        }
        return true;
    }

    /**
     * finds the shortest path to a target state item that respects the lookahead sets of each state in the path.
     * This step is required for both unified and non-unified counterexamples.
     * @param target the state item at the end of the path.
     * @return the shortest path to {@code target} that takes the lookahead of each state into account.
     */
    public ArrayList<StateItem> findShortestLookaheadSensitivePath(StateItem target){
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
                    if(!possibleStateItems.contains(tranDst)){
                        continue;
                    }
                    LookaheadSensitiveGraphVertex next = new LookaheadSensitiveGraphVertex(tranDst,last.lookahead);
                    LinkedList<LookaheadSensitiveGraphVertex> nextPath = new LinkedList<>(path);
                    nextPath.add(next);
                    queue.add(nextPath);
                }
            }
            //add all production steps to the search queue
            if(productionStepTables.getProdSteps(last.stateItem) != null){
                BitSet newLookahead = followL(last.getProduction(),last.getDotPosition(),last.lookahead);
                BitSet productionSteps = productionStepTables.getProdSteps(last.stateItem);

                //for each possible item reached via a production step
                LR0ItemSet stateItems = dfa.getItemSet(last.getState());
                for(int i = productionSteps.nextSetBit(0); i >= 0; i = productionSteps.nextSetBit(i+1)) {
                    BitSet l = lookaheadSets.getLookahead(last.getState(),i);
                    StateItem pStateItem = new StateItem(last.getState(),stateItems.getProduction(i),stateItems.getPosition(i),l);
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

    /**
     * computes the set of state items that may be used in a path to reach the target state.
     * works by performing a breadth-first search on reverse production steps and reverse transitions.
     * @param target the destination StateItem.
     * @return any StateItems that may be in a path to {@code target}.
     */
    private Set<StateItem> eligibleStateItems(StateItem target){
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
            //consider reverse production steps
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

    /**
     * places the followL set of a production into an arrayList
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
        //fourth case: symbol two positions to the right is a non-terminal that cannot derive the empty string
        else {
            BitSet bs  = contextSets.getFirst(sym);
            BitSet bsp = followL(production,dotPosition+1,lookaheadSet);
            bs.or(bsp);
            return bs;
        }
    }

    private Counterexample counterexampleFromShortestPath(){
        return counterexampleFromShortestPath(shortestLookaheadSensitivePath,null,null);
    }

    /**
     * generates a non-unifying example from the shortest path.
     * @param shortestPath the path to base the example upon
     * @param derivs1 A partially completed derivation of the first part of the example. May be null.
     * @param derivs2 A partially completed derivation of the second part of the example. May be null.
     * @return A non-unifying counter-example that is the same up at least until the conflict point.
     */
    private Counterexample counterexampleFromShortestPath(ArrayList<StateItem> shortestPath,
                                                          LinkedList<Derivation> derivs1, LinkedList<Derivation> derivs2){
        //for reduce/reduce errors, just find the path to the other conflict item
        if (derivs1 == null || derivs2 == null){
            derivs1 = new LinkedList<>();
            derivs2 = new LinkedList<>();
        }
        if(!isShiftReduce){
            StateItem si = new StateItem(conflictState,conflictItem2.getProduction(),conflictItem2.getDotPosition(),conflictItem2.getLookahead());
            ArrayList<StateItem> shortestPath2 = findShortestLookaheadSensitivePath(si);
            Derivation deriv1 = completeNonUnifyingExample(shortestPath,derivs1);
            Derivation deriv2 = completeNonUnifyingExample(shortestPath2,derivs2);
            return new Counterexample(deriv1,deriv2,isShiftReduce);
        }

        ArrayList<StateItem> shiftConflictPath = findShiftConflictPath(shortestPath);
        Derivation deriv1 = completeNonUnifyingExample(shortestPath,derivs1);
        Derivation deriv2 = completeNonUnifyingExample(shiftConflictPath,derivs2);
        return new Counterexample(deriv1,deriv2,isShiftReduce);
    }

    /**
     * Finds the shortest path to the shift conflict StateItem, using the path to the reduce conflict StateItem
     * to guide the search.
     * @param shortestPath the shortest lookahead-sensitive path to the reduce conflict item.
     * @return the shortest lookaheads-sensitive path to the shift conflict item.
     */
    private ArrayList<StateItem> findShiftConflictPath(ArrayList<StateItem> shortestPath) {
        //Perform a breadth first search to find a path from the shift conflict stateItem to the start stateItem
        //We use information from the shortest path to limit the search, only adding states if they are
        //(reverse) production steps, or reverse transitions to a state along the shortest path.
        //The order of the states in the path should be identical, the differences being caused by taking different production steps

        //We need to get a list of all the states in the shortest path without repetition,
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
        startPath.addFirst(new ShiftConflictSearchNode(shortestPathStates.size()-2,conflictItem2));
        queue.add(startPath);

        while(!queue.isEmpty()){
            LinkedList<ShiftConflictSearchNode> path = queue.remove();
            ShiftConflictSearchNode head = path.getFirst();

            //we're at the start state, so we have our path
            if(head.getStateItem().equals(startVertex.stateItem) ||
                    head.getStateItem().getState() == 0){
                ArrayList<StateItem> result = new ArrayList<>(path.size());

                //extract the stateItems, move to an arrayList
                for(ShiftConflictSearchNode n : path){
                    result.add(n.getStateItem());
                }

                return result;
            }

            //consider reverse production items
            if(head.isProductionItem()){
                BitSet revProd =
                        productionStepTables.getRevProdSteps(head.getStateItem().getState(),
                                                             spec.pr.getLHS(head.getStateItem().getProduction()));
                if(revProd != null){
                    LR0ItemSet itemSet = dfa.getItemSet(head.getStateItem().getState());
                    //For each production step
                    for(int i = revProd.nextSetBit(0); i >= 0; i = revProd.nextSetBit(i+1)){
                        int newState = head.getStateItem().getState();
                        BitSet l = lookaheadSets.getLookahead(newState,i);
                        StateItem newStateItem = new StateItem(newState,itemSet.getProduction(i),itemSet.getPosition(i),l);
                        LinkedList<ShiftConflictSearchNode> newPath = new LinkedList<>();
                        //add a new path with that production step item as the head to the queue
                        newPath.addAll(path);
                        newPath.addFirst(new ShiftConflictSearchNode(head.getValidStateIndex(),newStateItem));
                        queue.add(newPath);
                    }
                }
                continue;
            }

            Set<StateItem> revTran = transitionTables.revTrans.get(head.getStateItem());
            for(StateItem s : revTran){
                //only add states that are in the correct position along the shortest path
                if(s.getState() == shortestPathStates.get(head.getValidStateIndex())){
                    LinkedList<ShiftConflictSearchNode> newPath = new LinkedList<>(path);
                    newPath.addFirst(new ShiftConflictSearchNode(head.getValidStateIndex()-1,s));
                    queue.add(newPath);
                }
            }
        }
        throw new Error("failed to find shift conflict path");
    }

    /**
     * creates a full derivation from a list of parser states to the conflict state.
     * Used when constructing a non-unifying example, as no guarantees of what follows the path are made.
     * @param states the states along the shortest lookahead sensitive path
     * @param derivations any currently unfinished derivations that need to be filled out,
     *                    generated from attempting (and failing) to make a unified example
     * @return A completed derivation that begins with the shortest lookahead sensitive path
     */
    private Derivation completeNonUnifyingExample(ArrayList<StateItem> states, LinkedList<Derivation> derivations){
        LinkedList<Derivation> result = new LinkedList<>();
        //for every state in the shortest lookahead sensitive path
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
                    result.add(new Derivation(symbolTable.getSymbolString(spec.pr.getRHSSym(prod,pos))));
                    lookaheadRequired = false;
                }
            }

            //for every symbol to the right of the dot, either add it to the derivation directly,
            // or if lookahead is required, use expandFirst to generate a sequence of derivations that begins with
            // the conflict symbol
            for(int j = pos + 1; j < len; j++){
                int symbol = spec.pr.getRHSSym(prod,j);
                if(lookaheadRequired){
                    if(symbol != conflictTerminal){
                        if(spec.nonterminals.get(symbol)){
                            if(!contextSets.isNullable(symbol) || contextSets.getFirst(symbol).get(conflictTerminal)){
                                //creates a list of derivations that ends with the current stateItem and starts with the conflict terminal
                                LinkedList <Derivation> nextDerivations =
                                        expandFirst(transitionTables.getTransition(stateItem,spec.pr.getRHSSym(prod,pos)));

                                result.addAll(nextDerivations);
                                j += nextDerivations.size() - 1;
                                lookaheadRequired = false;
                            } else {
                                //can't derive  the conflict terminal, must be some other prod
                                result.add(new Derivation(symbolTable.getSymbolString(symbol)));
                            }
                        }
                    } else {
                        result.add(new Derivation(symbolTable.getSymbolString(symbol)));
                        lookaheadRequired = false;
                    }

                } else {
                    result.add(new Derivation(symbolTable.getSymbolString(symbol)));
                }

            }

            //add the symbols from the partially complete derivation to the beginning of the derivation
            Iterator<Derivation> derivationItr = derivations.descendingIterator();
            for (int j = pos - 1; j >= 0; j--) {
                if(i>0){
                    i--;
                }
                result.addFirst(derivationItr.hasNext() ? derivationItr.next() :
                        new Derivation(symbolTable.getSymbolString(spec.pr.getRHSSym(prod,j))));
            }

            //complete the derivation
            Derivation deriv = new Derivation(symbolTable.getSymbolString(spec.pr.getLHS(prod)), result);
            result = new LinkedList<>();
            result.add(deriv);
        }
        return result.getFirst();
    }

    /**
     * Repeatedly take production steps on the given StateItem so that the
     * first symbol of the derivation matches the conflict symbol.
     * @param start The StateItem to start with.
     * @return A sequence of derivations of {@code start} that ends with
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
            int symbolAfterDot = spec.pr.getRHSSym(lastSI.getProduction(), lastSI.getDotPosition());
            if(symbolAfterDot == conflictTerminal){
                //the conflict symbol has been reached, consolidate list of states into a list of derivations
                LinkedList<Derivation> result = new LinkedList<>();
                result.add(new Derivation(symbolTable.getSymbolString(conflictTerminal)));
                for(int i = states.size() - 1; i >= 0 ; i--){
                    StateItem si = states.get(i);
                    int pos = si.getDotPosition();
                    int prod = si.getProduction();
                    //if the dot position is at the start, construct the derivation with all symbols on the RHS of the production
                    //  as singleton (unexpanded) child derivations
                    if (pos == 0) {
                        int len = spec.pr.getRHSLength(prod);
                        for (int j = pos + 1; j < len; j++) {
                            result.add(new Derivation(symbolTable.getSymbolString(spec.pr.getRHSSym(prod,j))));
                        }
                        int lhs = spec.pr.getLHS(prod);
                        Derivation deriv = new Derivation(symbolTable.getSymbolString(lhs), result);
                        result = new LinkedList<>();
                        result.add(deriv);
                    }
                    //otherwise, add as singleton derivation on the symbol before the dot
                    // the rest of the derivation will be constructed in a later iteration of the loop
                    else {
                        Derivation deriv = new Derivation(symbolTable.getSymbolString(spec.pr.getRHSSym(prod,pos-1)));
                        result.addFirst(deriv);
                    }
                }
                result.removeFirst();
                return result;
            }
            //if the symbol after the dot is a non-terminal, add any valid production steps to the search
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

}