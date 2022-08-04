package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0ItemSet;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadSets;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ContextSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import java.util.*;

//TODO redo this javadodc comment
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

    ArrayList<StateItem> shortestLookaheadSensitivePath;
    //The set of all the states in the shortest context-sensitive path
    private BitSet shortestLookaheadSensitiveSet = new BitSet();
    private BitSet reduceProductionSet = new BitSet();

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

    public Counterexample getNonUnifyingCounterexample(){
        return counterexampleFromShortestPath(shortestLookaheadSensitivePath);
    }

    /**
     * Simulates 2 parsers on the same input in order to generate two derivations of the same tokens.
     * If this fails, 2 derivations that are the same up to the point of error (but potentially different afterwards)
     * are generated.
     * @return A unified or non-unified counterexample
     */
    public Counterexample getExample(){
        UnifiedSearchState initial = new UnifiedSearchState(conflictItem1,conflictItem2);

        // The search uses a priority queue on the complexity of search states.
        PriorityQueue<UnifiedSearchState> pq = new PriorityQueue<>();
        HashMap<LinkedList<StateItem>, HashSet<LinkedList<StateItem>>> visited = new HashMap<>();

        add(pq,visited,initial);
        //timer
        long start = System.nanoTime();
        UnifiedSearchState stage3result = null;
        while(!pq.isEmpty()){
            UnifiedSearchState ss = pq.remove();
            StateItem si1src = ss.states1.get(0);
            StateItem si2src = ss.states2.get(0);
            visited(visited, ss);
            if (ss.reduceDepth < 0 && ss.shiftDepth < 0) {
                // We have completed the reduce and shift conflict items.
                // Stage 3
                if(spec.pr.getLHS(si1src.getProduction()) == spec.pr.getLHS(si2src.getProduction())
                        && hasCommonPrefex(si1src,si2src)){
                    //both paths begin with the same prefix
                    if (ss.derivs1.size() == 1 && ss.derivs2.size() == 1
                            && ss.derivs1.getFirst().symbol.equals(ss.derivs2.getFirst().symbol)) {
                        // each path is only one (identical) symbol.
                        // This by itself is a counterexample
                        return new Counterexample(ss.derivs1.getFirst(), ss.derivs2.getFirst(),true);
                    }
                    //we've found the unifying non-terminal which we can use to construct the full example
                    //we can use this to construct a compact non-unifying counter-example
                    if (stage3result == null) {
                        stage3result = ss;
                    }
                }
            }
            //TODO check the timer here (and print a message saying that it's still working if need be)
            System.out.println("here! This is where we will check the timer.");

            //Compute the successor configurations
            StateItem si1 = ss.states1.getLast();
            StateItem si2 = ss.states2.getLast();

            boolean si1reduce = spec.pr.getRHSLength(si1.getProduction()) == si1.getDotPosition();
            boolean si2reduce = spec.pr.getRHSLength(si2.getProduction()) == si2.getDotPosition();

            Integer si1sym = si1reduce ? null : spec.pr.getRHSSym(si1.getProduction(), si1.getDotPosition());
            Integer si2sym = si2reduce ? null : spec.pr.getRHSSym(si2.getProduction(), si2.getDotPosition());

            if(!si1reduce && !si2reduce){
                //neither path ends in a reduce item, so it is possible to search forwards as normal
                // Two actions are possible:
                // - Make a transition on the next symbol of the items, if they are the same.
                // - Take a production step, avoiding duplicates as necessary.
                if(si1sym == si2sym){
                    // in this case, find the sequence of possible transitions on nullable symbols (the nullable closure)
                    //and add all subsequences of those to the search queue
                    StateItem nextSI1 = transitionTables.getTransition(si1,si1sym);
                    StateItem nextSI2 = transitionTables.getTransition(si2,si2sym);
                    LinkedList<Derivation> derivs1 = new LinkedList<>();
                    LinkedList<Derivation> derivs2 = new LinkedList<>();
                    LinkedList<StateItem> states1 = new LinkedList<>();
                    LinkedList<StateItem> states2 = new LinkedList<>();

                    derivs1.add(new Derivation(symbolTable.getSymbolString(si1sym)));
                    states1.add(nextSI1);
                    derivs2.add(new Derivation(symbolTable.getSymbolString(si2sym)));
                    states2.add(nextSI2);

                    nullableClosure(si1.getProduction(),
                            si1.getDotPosition() + 1,
                            nextSI1,
                            states1,
                            derivs1);
                    nullableClosure(si2.getProduction(),
                            si2.getDotPosition() + 1,
                            nextSI2,
                            states2,
                            derivs2);

                    for (int i = 1, size1 = derivs1.size(); i <= size1; i++) {
                        List<Derivation> subderivs1 = new ArrayList<>(derivs1.subList(0, i));
                        List<StateItem> substates1 = new ArrayList<>(states1.subList(0, i));
                        for (int j = 1, size2 = derivs2.size(); j <= size2; j++) {
                            List<Derivation> subderivs2 = new ArrayList<>(derivs2.subList(0, j));
                            List<StateItem> substates2 = new ArrayList<>(states2.subList(0, j));
                            UnifiedSearchState copy = ss.copy();
                            copy.derivs1.addAll(subderivs1);
                            copy.states1.addAll(substates1);
                            copy.derivs2.addAll(subderivs2);
                            copy.states2.addAll(substates2);
                            copy.complexity += 2 * SHIFT_COST;
                            add(pq, visited, copy);
                        }
                    }

                }
                //have the first parser take a production step if possible
                if(spec.nonterminals.get(si1sym) && productionStepTables.prodTable.containsKey(si1)){
                    BitSet prodSteps = productionStepTables.prodTable.get(si1);

                    //contains all possible productions for the state, we will take all production steps allowed by
                    // the production step table and loop over each, adding the search states where we have taken the
                    // production steps to the queue
                    LR0ItemSet possibleItems = dfa.getItemSet(si1.getState());
                    for(int i = prodSteps.nextSetBit(0); i >= 0; i = prodSteps.nextSetBit(i+1) ){
                        if(spec.pr.getRHSLength(si1.getProduction()) != si1.getDotPosition() ||
                                !compatible(spec.pr.getRHSSym(si1.getProduction(),si1.getDotPosition()),si2sym)){
                            continue;
                        }
                        if(!productionAllowed(si1.getProduction(),possibleItems.getProduction(i))){
                            continue;
                        }
                        //create a new state item from the newly selected production
                        StateItem next = new StateItem(si1.getState(),possibleItems.getProduction(i),
                                possibleItems.getPosition(i),lookaheadSets.getLookahead(si1.getState(),i));
                        LinkedList<Derivation> derivs1 = new LinkedList<>();
                        LinkedList<StateItem> states1 = new LinkedList<>();
                        states1.add(next);

                        //add the nullable closure (and all possible subsequences) of the new production item
                        nullableClosure(possibleItems.getProduction(i), 0, next, states1, derivs1);
                        for (int j = 0, size1 =
                             derivs1.size(); j <= size1; j++) {
                            LinkedList<Derivation> subderivs1 =
                                    new LinkedList<>(derivs1.subList(0, j));
                            List<StateItem> substates1 =
                                    new LinkedList<>(states1.subList(0, j + 1));
                            UnifiedSearchState copy = ss.copy();
                            copy.derivs1.addAll(subderivs1);
                            copy.states1.addAll(substates1);
                            copy.complexity += PRODUCTION_COST;
                            add(pq, visited, copy);
                        }

                    }
                }
                //have the second parser take a production step if possible
                if(spec.nonterminals.get(si2sym) && productionStepTables.prodTable.containsKey(si2)){
                    BitSet prodSteps = productionStepTables.prodTable.get(si2);

                    //contains all possible productions for the state, we will take all production steps allowed by
                    // the production step table and loop over each, adding the search states where we have taken the
                    // production steps to the queue
                    LR0ItemSet possibleItems = dfa.getItemSet(si2.getState());
                    for(int i = prodSteps.nextSetBit(0); i >= 0; i = prodSteps.nextSetBit(i+1) ){
                        if(spec.pr.getRHSLength(si2.getProduction()) != si2.getDotPosition() ||
                                !compatible(spec.pr.getRHSSym(si2.getProduction(),si2.getDotPosition()),si1sym)){
                            continue;
                        }
                        if(!productionAllowed(si2.getProduction(),possibleItems.getProduction(i))){
                            continue;
                        }

                        //create a new state item from the newly selected production
                        StateItem next = new StateItem(si2.getState(),possibleItems.getProduction(i),
                                possibleItems.getPosition(i),lookaheadSets.getLookahead(si2.getState(),i));
                        LinkedList<Derivation> derivs2 = new LinkedList<>();
                        LinkedList<StateItem> states2 = new LinkedList<>();
                        states2.add(next);

                        //add the nullable closure (and all possible subsequences) of the new production item
                        nullableClosure(possibleItems.getProduction(i), 0, next, states2, derivs2);
                        for (int j = 0, size1 =
                             derivs2.size(); j <= size1; j++) {
                            LinkedList<Derivation> subderivs2 =
                                    new LinkedList<>(derivs2.subList(0, j));
                            List<StateItem> substates2 =
                                    new LinkedList<>(states2.subList(0, j + 1));
                            UnifiedSearchState copy = ss.copy();
                            copy.derivs2.addAll(subderivs2);
                            copy.states2.addAll(substates2);
                            copy.complexity += PRODUCTION_COST;
                            add(pq, visited, copy);
                        }
                    }
                }
            //at least one path requires a reduction
            } else {
                int len1 = spec.pr.getRHSLength(si1.getProduction());
                int len2 = spec.pr.getRHSLength(si2.getProduction());
                boolean ready1 = si1reduce && ss.states1.size() > len1;
                boolean ready2 = si2reduce && ss.states2.size() > len2;

                //self documenting: if the reductions are ready, add all possible reduction steps to the queue
                if(ready1) {
                    LinkedList<UnifiedSearchState> reduced1 = ss.reduce(si2sym,true);
                    if (ready2) {
                        reduced1.add(ss);
                        for (UnifiedSearchState red1 : reduced1) {
                            for (UnifiedSearchState candidate : red1.reduce(si1sym,false))
                                add(pq, visited, candidate);
                            if (red1 != ss) // avoid duplicates
                                add(pq, visited, red1);
                        }
                    } else {
                        for (UnifiedSearchState candidate : reduced1) {
                            add(pq, visited, candidate);
                        }
                    }
                // NOTE this used to have `ss.derivs2.size() > len2` as a req, should be fine, but noting in case it becomes a failure point later.
                } else if (ready2) {
                    LinkedList<UnifiedSearchState> reduced2 = ss.reduce(si1sym,false);
                    for (UnifiedSearchState candidate : reduced2){
                        add(pq, visited, candidate);
                    }
                }
                //neither are ready, but both are reduction items
                //we have to prepend to parsers in order to prepare a reduction
                else {
                    int sym;
                    if (si1reduce){
                        sym = spec.pr.getRHSSym(si1.getProduction(),len1-ss.states1.size());
                    } else {
                        sym = spec.pr.getRHSSym(si2.getProduction(),len2-ss.states2.size());
                    }
                    for(UnifiedSearchState prepended : ss.prepend(sym,
                            ss.reduceDepth >= 0 ? reduceProductionSet : shortestLookaheadSensitiveSet)){
                        add(pq,visited,prepended);
                    }
                }
            }
        }
        System.out.println("Failed to find unifying counterexample, attempting non-unified");
        //TODO re-use the derivations generated while attempting to construct the unified counterexample here
        return counterexampleFromShortestPath(shortestLookaheadSensitivePath);
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
     * @param lookahead a guide to restrict the output StateItems to only those that
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
                if(!prevLookahead.intersects(lookahead)){
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
     * marks the given search state as visited in the given map.
     * @param visited the map representing visited search states
     * @param ss the search state to mark as visited
     */
    private void visited(HashMap<LinkedList<StateItem>, HashSet<LinkedList<StateItem>>> visited, UnifiedSearchState ss) {
        HashSet<LinkedList<StateItem>> visited1 = visited.get(ss.states1);
        if (visited1 == null) {
            visited1 = new HashSet<>();
            visited.put(ss.states1, visited1);
        }
        visited1.add(ss.states2);
    }

    /**
     * Adds a search state to the priority queue if it has not already been visited
     * @param pq the priority queue of search states to be searched
     * @param visited A map representing the visited search states
     * @param ss the search state to add
     */
    private void add(PriorityQueue<UnifiedSearchState> pq, HashMap<LinkedList<StateItem>,
            HashSet<LinkedList<StateItem>>> visited, UnifiedSearchState ss) {
        HashSet<LinkedList<StateItem>> visited1 = visited.get(ss.states1);
        if (visited1 != null && visited1.contains(ss.states2)) {
            return;
        }
        pq.add(ss);

    }

    //TODO logic comments
    //TODO clean up

    /**
     * finds the shortest path to a target state item that respects the lookahead sets of each state in the path.
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

    //TODO javadoc comment
    //TODO comment logic
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

    private Counterexample counterexampleFromShortestPath(ArrayList<StateItem> shortestPath){
        return counterexampleFromShortestPath(shortestPath,null,null);
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

    //TODO javadoc comment
    //TODO review logic comments
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

            //TODO if the head stateItem is in the shortest path, we should finish and re-use the shortest path from there.
            //TODO fix the stateItem equality function, this is dumb
            if(head.getStateItem().getState() == startVertex.getState() &&
                    head.getStateItem().getProduction() == startVertex.getProduction() &&
                    head.getStateItem().getDotPosition() == startVertex.getDotPosition() ||
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

    /**
     * A search state representing two simulated parsers.
     * This is the "configuration" described in Isradisaikul & Myers.
     */
    protected class UnifiedSearchState implements Comparable<UnifiedSearchState> {
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

        //TODO comment logic
        /**
         * prepends a symbol to the current configuration if possible
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

            if (transitionTables.prevSymbol[si1src.getState()] != sym || transitionTables.prevSymbol[si2src.getState()] != sym) {
                return result;
            }

            LinkedList<StateItem> prev1ext = reverseTransition(si1src, si1Lookahead, guide);
            LinkedList<StateItem> prev2ext = reverseTransition(si2src, si2Lookahead, guide);

            for (StateItem prevSI1 : prev1ext) {
                for (StateItem prevSI2 : prev2ext) {
                    boolean prev1IsSrc = prevSI1.equals(si1src);
                    boolean prev2IsSrc = prevSI2.equals(si2src);
                    if (prev1IsSrc && prev2IsSrc) {
                        continue;
                    }
                    if (prevSI1.getState() != prevSI2.getState()) {
                        continue;
                    }
                    UnifiedSearchState copy = this.copy();

                    copy.states1.addFirst(prevSI1);
                    copy.states2.addFirst(prevSI2);

                    if (copy.states1.get(0).getDotPosition() + 1 ==
                        copy.states1.get(1).getDotPosition()) {
                        if (copy.states2.get(0).getDotPosition() + 1 ==
                            copy.states2.get(1).getDotPosition()) {
                            Derivation deriv = new Derivation(symbolTable.getSymbolString(sym));
                            copy.derivs1.addFirst(deriv);
                            copy.derivs2.addFirst(deriv);

                        } else {
                            continue;
                        }
                    } else if (copy.states2.get(0).getDotPosition() + 1 ==
                        copy.states2.get(1).getDotPosition()) {
                        continue;
                    }
                    int prependSize = (prev1IsSrc ? 0 : 1) + (prev2IsSrc ? 0 : 1);
                    //the number of production steps taken from prevNSI to siNsrc
                    int productionSteps =
                            (prevSI1.getState() == si1src.getState() ? 1 : 0) +
                                    (prevSI2.getState() == si2src.getState() ? 1 : 0);
                    copy.complexity +=
                            UNSHIFT_COST * (prependSize - productionSteps) + PRODUCTION_COST * productionSteps;
                    result.add(copy);
                }
            }
            return result;
        }

        //TODO comment logic
        /**
         * Reduces the search state for one of the simulated parsers.
         * @param sym symbol that follows the production item for the relevant parser
         * @param isOne true if the first parser is being reduced, false for the second parser
         * @return A list of all possible reductions for the parser
         */
        protected LinkedList<UnifiedSearchState> reduce(Integer sym, boolean isOne) {
            LinkedList<StateItem> states = isOne ? states1 : states2;
            LinkedList<Derivation> derivs = isOne ? derivs1 : derivs2;
            //If not a reduce item
            StateItem lastItem = states.getLast();
            if (lastItem.getDotPosition() != spec.pr.getRHSLength(lastItem.getProduction())) {
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
            int derivSize = derivs.size();
            Derivation deriv = new Derivation(symbolTable.getSymbolString(lhs),
                    new LinkedList<>(derivs.subList(derivs.size() - len, derivs.size())));

            if(isOne){
                if (reduceDepth == 0) {
                    // We are reducing the reduce-conflict item.
                    // Add a dot for visual inspection of the resulting counterexample.
                    deriv.derivations.add(conflictItem1.getDotPosition(), Derivation.dot);
                }
            } else {
                if (shiftDepth == 0) {
                    deriv.derivations.add(conflictItem2.getDotPosition(),Derivation.dot);
                }
            }

            derivs = new LinkedList<>(derivs.subList(0, derivs.size() - len));
            derivs.add(deriv);
            if (states.size() == len + 1) {
                //was not null before, should probably be that again
                LinkedList<StateItem> prev = reverseProduction(states.getFirst(),symbolSet);
                for (StateItem prevV : prev) {
                    UnifiedSearchState copy = copy();
                    if(isOne){
                        copy.derivs1 = derivs;
                    } else {
                        copy.derivs2 = derivs;
                    }
                    LinkedList<StateItem> copyStates = new LinkedList<>(states.subList(0, states.size() - len - 1));
                    copyStates.addFirst(prevV);
                    copyStates.add(transitionTables.trans.get(copyStates.getLast())[lhs]);

                    int statesSize = copyStates.size();
                    int productionSteps = productionSteps(copyStates);

                    copy.complexity +=
                            UNSHIFT_COST * (statesSize - productionSteps) + PRODUCTION_COST * productionSteps;

                    if(isOne){
                        copy.states1 = copyStates;
                        if (copy.reduceDepth == 0) {
                            copy.reduceDepth--;
                        }
                    } else {
                        copy.states2 = copyStates;
                        if (copy.shiftDepth >= 0){
                            copy.shiftDepth--;
                        }
                    }

                    result.add(copy);
                }
            } else {
                UnifiedSearchState copy = copy();
                if(isOne){
                    copy.states1 = new LinkedList<>(states1.subList(0, states1.size() - len - 1));
                    copy.states1.add(transitionTables.trans.get(copy.states1.getLast())[lhs]);
                } else {
                    copy.states2 = new LinkedList<>(states2.subList(0, states2.size() - len - 1));
                    copy.states2.add(transitionTables.trans.get(copy.states2.getLast())[lhs]);
                }

                copy.complexity += REDUCE_COST;

                if(isOne){
                    if (copy.reduceDepth == 0) {
                        copy.reduceDepth--;
                    }
                } else {
                    if(copy.shiftDepth >= 0){
                        copy.shiftDepth--;
                    }
                }
                result.add(copy);
            }
            // transition on nullable symbols
            LinkedList<UnifiedSearchState> finalizedResult = new LinkedList<>();
            for(UnifiedSearchState ss : result){
                StateItem next;
                if(isOne){
                    next = ss.states1.getLast();
                } else {
                    next = ss.states2.getLast();
                }
                List<Derivation> derivsNew = new LinkedList<>();
                List<StateItem> statesNew = new LinkedList<>();
                nullableClosure(next.getProduction(),
                        next.getDotPosition(),
                        next,
                        statesNew,
                        derivsNew);
                finalizedResult.add(ss);
                for (int i = 1, size1 = derivsNew.size(); i <= size1; i++) {
                    List<Derivation> subderivs =
                            new ArrayList<>(derivsNew.subList(0, i));
                    List<StateItem> substates =
                            new ArrayList<>(statesNew.subList(0, i));
                    UnifiedSearchState copy = ss.copy();
                    if(isOne){
                        copy.derivs1.addAll(subderivs);
                        copy.states1.addAll(substates);
                    } else {
                        copy.derivs2.addAll(subderivs);
                        copy.states2.addAll(substates);
                    }
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

        //for use the priority queue used in the search process
        @Override
        public int compareTo(UnifiedSearchState toCompare) {
            return this.complexity - toCompare.complexity;
        }
    }

    /**
     * Determine if two symbols are compatible for the purposes of taking a production step.
     * @param sym1
     * @param sym2
     * @return If the symbols are compatible
     */
    protected boolean compatible(int sym1, int sym2){
        if(spec.terminals.get(sym1)) {
            if (spec.terminals.get(sym2)) {
                return sym1 == sym2;
            } else {
                return contextSets.getFirst(sym2).get(sym1);
            }
        } else {
            if(spec.terminals.get(sym2)){
                return contextSets.getFirst(sym1).get(sym2);
            } else {
                return sym1 == sym2 || contextSets.getFirst(sym1).intersects(contextSets.getFirst(sym2));
            }
        }
    }

    /**
     * Computes the list of StateItems that are possible for the given production item
     * to reach from taking a transition on a nullable symbol.
     * @param production the number of the production
     * @param dotPosition the position of the dot within the production
     * @param lastSI the starting StateItem
     * @param states the list of StateItems the output closure is placed in
     * @param derivs the list of derivations the output closure derivations are placed in
     */
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
            derivs.add(new Derivation(symbolTable.getSymbolString(sp), new LinkedList<Derivation>()));
            states.add(lastSI);
        }
    }

    /**
     * counts the number of production steps taken by seeing how many times there is no transition of state.
     * @param stateItems the state sequence to count
     * @return the number of production steps taken
     */
    protected static int productionSteps(LinkedList<StateItem> stateItems){
        int count = 0;
        int lastState = stateItems.getFirst().getState();
        for(StateItem si : stateItems){
            if (si.getState() == lastState) {
                count++;
            }
            lastState = si.getState();
        }
        return count;
    }

    /**
     * determines if a production step can be taken from the first argument to the second,
     * based on precedence.
     * @param p the starting production
     * @param nextP the desired destination production.
     * @return if a production step from p to nextP is possible
     */
    protected boolean productionAllowed(int p, int nextP){
        int prodPred = spec.pr.getPrecedence(p);
        int nextProdPred = spec.pr.getPrecedence(nextP);
        int prodPredClass = spec.pr.getPrecedenceClass(p);
        int nextProdPredClass = spec.pr.getPrecedenceClass(nextP);
        if (prodPred >= 0 && nextProdPred >= 0 && prodPredClass == nextProdPredClass) {
            // Do not expand if lower precedence.
            if (prodPred > nextProdPred) {
                return false;
            }
            if (prodPred == nextProdPred) {
                //TODO check for associativity?
                return true;
            }
        }
        return true;
    }
}