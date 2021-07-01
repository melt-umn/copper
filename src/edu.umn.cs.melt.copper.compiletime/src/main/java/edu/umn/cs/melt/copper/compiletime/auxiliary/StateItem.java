package edu.umn.cs.melt.copper.compiletime.auxiliary;

public class StateItem {
    private int state;
    private int production;
    private int dotPosition;

    //TODO: make a memoized lookup table?
    // The fact it would be a 3d array makes me feel like that's a less than ideal solution

    public StateItem(int state, int production, int dotPosition) {
        this.state = state;
        this.production = production;
        this.dotPosition = dotPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateItem stateItem = (StateItem) o;

        if (state != stateItem.state) return false;
        if (production != stateItem.production) return false;
        return dotPosition == stateItem.dotPosition;
    }


    @Override
    public int hashCode() {
        int result = state;
        result = 31 * result + production;
        result = 31 * result + dotPosition;
        return result;
    }

    public int getState() {
        return state;
    }

    public int getProduction() {
        return production;
    }

    public int getDotPosition() {
        return dotPosition;
    }

}
