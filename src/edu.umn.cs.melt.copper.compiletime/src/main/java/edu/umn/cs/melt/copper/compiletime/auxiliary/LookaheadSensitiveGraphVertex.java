package edu.umn.cs.melt.copper.compiletime.auxiliary;

import java.util.BitSet;

/**
 * A vertex in a {@link LookaheadSensitiveGraph}.
 * A triplet of an int representing a state in the parse table,
 * an int representing the position of an LR0 item in the itemSet for that state,
 * and the lookahead set.
 */
class LookaheadSensitiveGraphVertex {
    protected StateItem stateItem;
    protected BitSet lookaheadSet;

    public LookaheadSensitiveGraphVertex(int state, int production, int dotPosition, BitSet lookaheadSet) {
        this.stateItem = new StateItem(state,production,dotPosition);
        this.lookaheadSet = lookaheadSet;
    }
    public LookaheadSensitiveGraphVertex(StateItem stateItem, BitSet lookaheadSet) {
        this.stateItem = stateItem;
        this.lookaheadSet = lookaheadSet;
    }

    public int getProduction(){
        return stateItem.getProduction();
    }

    public int getDotPosition(){
        return stateItem.getDotPosition();
    }

    public int getState(){
        return stateItem.getState();
    }


    @Override
    public String toString() {
        return "LookaheadSensitiveGraphVertex{" +
                "stateItem=" + stateItem +
                ", lookaheadSet=" + lookaheadSet +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LookaheadSensitiveGraphVertex that = (LookaheadSensitiveGraphVertex) o;

        if (!stateItem.equals(that.stateItem)) return false;
        return lookaheadSet != null ? lookaheadSet.equals(that.lookaheadSet) : that.lookaheadSet == null;
    }

    @Override
    public int hashCode() {
        int result = stateItem.hashCode();
        result = 31 * result + (lookaheadSet != null ? lookaheadSet.hashCode() : 0);
        return result;
    }
}
