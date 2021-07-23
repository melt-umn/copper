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

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String[] ansiColours = {"\u001B[36m","\u001B[33m","\u001B[32m","\u001B[34m","\u001B[31m","\u001B[35m"};
    private static int currentColour = 0;

    private static void incrementCurrentColour(){
        if(currentColour + 1 == ansiColours.length){
            currentColour = 0;
        } else {
            currentColour++;
        }
    }
    private static void decrementCurrentColour(){
        if(currentColour - 1 < 0){
            currentColour = ansiColours.length - 1;
        } else {
            currentColour--;
        }
    }

    public Derivation(String symbol){
        this.symbol = symbol;
        this.derivations = null;
    }
    public Derivation(String symbol, List<Derivation> derivations) {
        this.symbol = symbol;
        this.derivations = derivations;
    }


    //return value the new indent
    public int prettyPrint(ArrayList<StringBuilder> sbs,ArrayList<Integer> escapeCounts,int index,int indent) {
        //print LHS/terminal
        StringBuilder sb = sbs.get(index);
        Integer sbEscapes = escapeCounts.get(index);
        if(derivations != null){
            incrementCurrentColour();
        }
        sb.append(" ");
        sb.append(ansiColours[currentColour]);
        sb.append(symbol);
        sb.append(ANSI_RESET);
        escapeCounts.set(index,escapeCounts.get(index) + 2);

        indent += symbol.length() + 1;
        if(derivations == null){
            return indent;
        }

        //If there is a RHS to print, prepare the next line
        try {
            sbs.get(index+1);
        } catch(IndexOutOfBoundsException e) {
           sbs.add(new StringBuilder());
           escapeCounts.add(0);
        }

        StringBuilder nextsb = sbs.get(index+1);
        for (int i = nextsb.length()-escapeCounts.get(index+1); i < indent-1; i++) {
            nextsb.append(" ");
        }
        nextsb.append(ansiColours[currentColour] + "↳" + ANSI_RESET);
        escapeCounts.set(index+1,escapeCounts.get(index+1)+2);
//        nextsb.append("↳");

        //print rhs
        for(Derivation d : derivations){
           indent = d.prettyPrint(sbs,escapeCounts,index+1,indent);
        }

        //account for newly required whitespace
        for (int i = sb.length()-escapeCounts.get(index); i < indent; i++) {
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
