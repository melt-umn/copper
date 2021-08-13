package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

import java.util.LinkedList;
import java.util.Set;

/**
 * search states in the product parser for finding unified examples.
 * This is the ``configuration'' as described in the paper ``Finding Counterexamples from Parsing Conflicts'' by Isradisaikul et al.
 */
class UnifiedSearchState {
    //a list of derivations that simulates the  parse stack,
    //and a list of state items representing the state transitions the parser takes (with explicit production steps)
    //one of each for each part of the product parser.
    protected LinkedList<Derivation> derivs1, derivs2;
    protected LinkedList<StateItem> states1, states2;

    /** number of states and production steps the parser has had to encounter.*/
    protected int complexity;
    /** The number of production steps made since the reduce conflict item.
     *  If this is -1, the reduce conflict item has been completed.
     */
    protected int reduceDepth;
    /** The number of production steps made since the shift conflict item.
     *  If this is -1, the shift conflict item has been completed and
     *  reduced.
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
     * @param sym the symbol to add.
     * @param guide If not null, restricts the possible parser states to this set;
     *              otherwise, explore all possible parser states that can make the desired transition.
     * @return A List of search states that result from prepending successfully (can be empty).
     */
    protected LinkedList<UnifiedSearchState> prepend(int sym, Set<Integer> guide){
        return null;
    }
}
