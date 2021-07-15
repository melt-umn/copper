package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

public class ShiftConflictSearchNode {
    private final int  possibleTransitionState;
    private final boolean productionItem;
    private final StateItem stateItem;

    public ShiftConflictSearchNode(int possibleTransitionState, boolean productionItem, StateItem stateItem) {
        this.possibleTransitionState = possibleTransitionState;
        this.productionItem = productionItem;
        this.stateItem = stateItem;
    }

    public int getValidStateIndex() {
        return possibleTransitionState;
    }

    public boolean isProductionItem() {
        return productionItem;
    }

    public StateItem getStateItem() {
        return stateItem;
    }

    @Override
    public String toString() {
        return "ShiftConflictSearchNode{" +
                "possibleTransitionState=" + possibleTransitionState +
                ", productionItem=" + productionItem +
                ", stateItem=" + stateItem +
                '}';
    }
}
