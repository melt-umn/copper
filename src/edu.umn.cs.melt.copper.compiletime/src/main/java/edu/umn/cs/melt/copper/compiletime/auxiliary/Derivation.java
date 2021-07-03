package edu.umn.cs.melt.copper.compiletime.auxiliary;


import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A derivation is a parse tree, represented by a symbol (which could a terminal or a non-terminal),
 * and  optionally a list of children derivations in the case of a non-terminal
 */
public class Derivation {
    String symbol;
    List<Derivation> derivations;

    //TODO do we want to have non-ascii chars required?
    public static final Derivation dot = new Derivation("(*)", new LinkedList<Derivation>());

    //TODO go through when this is used instead of calling the 2 argument one with the empty list
    public Derivation(String symbol){
        this.symbol = symbol;
        this.derivations = null;
    }
    public Derivation(String symbol, List<Derivation> derivations) {
        this.symbol = symbol;
        this.derivations = derivations;
    }


    //temporary ugly print used in the reference implementation
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(symbol);
        if (derivations != null) {
            sb.append(" ::= [");
            boolean tail = false;
            for (Derivation d : derivations) {
                if (tail)
                    sb.append(" ");
                else tail = true;
                sb.append(d);
            }
            sb.append("]");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Derivation that = (Derivation) o;

        if (!symbol.equals(that.symbol)) return false;
        return Objects.equals(derivations, that.derivations);
    }

    @Override
    public int hashCode() {
        int result = symbol.hashCode();
        result = 31 * result + (derivations != null ? derivations.hashCode() : 0);
        return result;
    }
}
