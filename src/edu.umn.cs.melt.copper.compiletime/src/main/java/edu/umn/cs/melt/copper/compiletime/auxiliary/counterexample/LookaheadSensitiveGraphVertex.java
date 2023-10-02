package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

import java.util.BitSet;

/**
 * A vertex in a {@link CounterexampleSearch}.
 * A triplet of an int representing a state in the parse table
 * an int representing the position of an LR0 item in the itemSet for that state,
 * and the lookahead set.
 * @author Kelton OBrien
 */
class LookaheadSensitiveGraphVertex {
    protected StateItem stateItem;
    protected BitSet lookahead;

    public LookaheadSensitiveGraphVertex(int state, int production, int dotPosition, BitSet lookahead)
    {
        this.stateItem = new StateItem(state,production,dotPosition,lookahead);
        this.lookahead = lookahead;
    }
    public LookaheadSensitiveGraphVertex(StateItem stateItem, BitSet lookahead)
    {
        this.stateItem = stateItem;
        this.lookahead = lookahead;
    }

    public int getProduction()
    {
        return stateItem.getProduction();
    }

    public int getDotPosition()
    {
        return stateItem.getDotPosition();
    }

    public int getState()
    {
        return stateItem.getState();
    }


    @Override
    public String toString()
    {
        return "LookaheadSensitiveGraphVertex{" +
                "stateItem=" + stateItem +
                ", lookaheadSet=" + lookahead +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof LookaheadSensitiveGraphVertex))
            return false;

        LookaheadSensitiveGraphVertex that = (LookaheadSensitiveGraphVertex) o;

        if (!stateItem.equals(that.stateItem)) return false;
        return lookahead != null ? lookahead.equals(that.lookahead) : that.lookahead == null;
    }

    @Override
    public int hashCode()
    {
        int result = stateItem.hashCode();
        result = 31 * result + (lookahead != null ? lookahead.hashCode() : 0);
        return result;
    }

}
