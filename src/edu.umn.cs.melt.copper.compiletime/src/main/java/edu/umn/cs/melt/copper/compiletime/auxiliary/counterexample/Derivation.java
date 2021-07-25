package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample.ColouredStringBuilder.*;

/**
 * A derivation is a parse tree, represented by a symbol (which could a terminal or a non-terminal),
 * and  optionally a list of children derivations in the case of a non-terminal
 */
public class Derivation {
    String symbol;
    List<Derivation> derivations;

    public static final Derivation dot = new Derivation("(*)", null);


    public Derivation(String symbol){
        this.symbol = symbol;
        this.derivations = null;
    }
    public Derivation(String symbol, List<Derivation> derivations) {
        this.symbol = symbol;
        this.derivations = derivations;
    }


    //return value the new indent
    public int prettyPrint(ArrayList<ColouredStringBuilder> sbs, int index,int indent) {
        //print LHS/terminal
        ColouredStringBuilder sb = sbs.get(index);
        if(derivations != null){
            incrementCurrentColour();
        }
        sb.append(" ");
        sb.appendColoured(symbol);

        indent += symbol.length() + 1;
        if(derivations == null){
            return indent;
        }

        //If there is a RHS to print, prepare the next line
        try {
            sbs.get(index+1);
        } catch(IndexOutOfBoundsException e) {
           sbs.add(new ColouredStringBuilder());
        }

        ColouredStringBuilder nextsb = sbs.get(index+1);
        for (int i = nextsb.length(); i < indent-1; i++) {
            nextsb.append(" ");
        }
        nextsb.appendColoured("↳");

        //print rhs
        for(Derivation d : derivations){
           indent = d.prettyPrint(sbs,index+1,indent);
        }

        //account for newly required whitespace
        for (int i = sb.length(); i < indent; i++) {
            sb.append(" ");
        }
        decrementCurrentColour();

        return indent;
    }

    //ugly print used in the reference implementation, not used anymore.
    @Override
    public String toString(){
        ArrayList<StringBuilder> sbs = new ArrayList<>();
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
