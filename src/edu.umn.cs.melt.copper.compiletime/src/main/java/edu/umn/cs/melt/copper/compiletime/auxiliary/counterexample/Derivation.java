package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;


import java.util.ArrayList;
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

    public static final Derivation dot = new Derivation("(*)", null);

    //TODO go through when this is used instead of calling the 2 argument one with the empty list
    public Derivation(String symbol){
        this.symbol = symbol;
        this.derivations = null;
    }
    public Derivation(String symbol, List<Derivation> derivations) {
        this.symbol = symbol;
        this.derivations = derivations;
    }


    //return value the new indent
    public int prettyPrint(ArrayList<StringBuilder> sbs,int index,int indent) {
        //print LHS/terminal
        StringBuilder sb = sbs.get(index);
        sb.append(" ");
        sb.append(symbol);
        indent += symbol.length() + 1;
        if(derivations == null){
            return indent;
        }

        //If there is a RHS to print, prepare the next line
        try {
            sbs.get(index+1);
        } catch(IndexOutOfBoundsException e) {
           sbs.add(new StringBuilder());
        }

        StringBuilder nextsb = sbs.get(index+1);
        for (int i = nextsb.length(); i < indent-1; i++) {
            nextsb.append(" ");
        }
        nextsb.append("↳");

        //print rhs
        for(Derivation d : derivations){
           indent = d.prettyPrint(sbs,index+1,indent);
        }

        //account for newly required whitespace
        for (int i = sb.length(); i < indent; i++) {
            sb.append(" ");
        }

        return indent;
    }

    //temporary ugly print used in the reference implementation
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
