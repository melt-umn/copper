package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

public class ShiftConflictSearchNode {
    private final int  possibleTransitionState;
    private final StateItem stateItem;

    public ShiftConflictSearchNode(int possibleTransitionState, StateItem stateItem) {
        this.possibleTransitionState = possibleTransitionState;
        this.stateItem = stateItem;
    }

    public int getValidStateIndex() {
        return possibleTransitionState;
    }

    public boolean isProductionItem() {
        return stateItem.getDotPosition() == 0;
    }

    public StateItem getStateItem() {
        return stateItem;
    }

    @Override
    public String toString() {
        return "ShiftConflictSearchNode{" +
                "possibleTransitionState=" + possibleTransitionState +
                ", stateItem=" + stateItem +
                '}';
    }
}
