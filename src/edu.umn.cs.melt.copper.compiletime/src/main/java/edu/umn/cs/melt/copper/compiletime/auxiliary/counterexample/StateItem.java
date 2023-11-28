package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import java.util.BitSet;

/**
 * A representation of the state of an (LA)LR(1) parser mid-parse.
 * Contains the state number and lookahead, as well as the particular production item being considered.
 * The basic abstraction used in generating counterexamples.
 * @author Kelton OBrien
 */
public class StateItem {
    private int state;
    private int production;
    private int dotPosition;
    private BitSet lookahead;

    public StateItem(int state, int production, int dotPosition, BitSet lookahead)
    {
        this.state = state;
        this.production = production;
        this.dotPosition = dotPosition;
        this.lookahead = lookahead;
    }


    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof StateItem))
            return false;

        StateItem stateItem = (StateItem) o;

        if (state != stateItem.state)
            return false;
        if (production != stateItem.production)
            return false;
        return dotPosition == stateItem.dotPosition;
    }

    @Override public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(State: ");
        sb.append(state);
        sb.append(", Production: ");
        sb.append(production);
        sb.append(", DotPosition: ");
        sb.append(dotPosition);
        sb.append(')');
        return sb.toString();
    }
    public String prettyPrint(PSSymbolTable symbolTable, ParserSpec spec)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < dotPosition; i++){
            sb.append(symbolTable.get(spec.pr.getRHSSym(production,i)).getDisplayName());
            sb.append(" ");
        }
        sb.append("• ");
        for(int i = dotPosition; i < spec.pr.getRHSLength(production); i++){
            sb.append(symbolTable.get(spec.pr.getRHSSym(production,i)).getDisplayName());
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public int hashCode()
    {
        int result = state;
        result = 31 * result + production;
        result = 31 * result + dotPosition;
        return result;
    }

    public int getState()
    {
        return state;
    }

    public int getProduction()
    {
        return production;
    }

    public int getDotPosition()
    {
        return dotPosition;
    }

    public BitSet getLookahead()
    {
        return lookahead;
    }
}
