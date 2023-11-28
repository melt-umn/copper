package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample.ColoredStringBuilder.*;

/**
 * A derivation is a parse tree, represented by a symbol (which could a terminal or a non-terminal),
 * and optionally a list of children derivations in the case of a non-terminal
 * @author Kelton OBrien
 */
public class Derivation {
    public String symbol;
    List<Derivation> derivations;

    //Not multithreaded, so there should be no contention here
    private static int dotIdCount = 0;

    private int dotId;

    public static final Derivation dot = new Derivation("•", null);


    public Derivation(String symbol)
    {
        //massage the symbol into something more readable when printed
        if(symbol.contains("'")){
            this.symbol = symbol.substring(0,symbol.lastIndexOf("'")+1);
        } else {
            this.symbol = symbol.substring(symbol.lastIndexOf(':')+1);
        }
        this.derivations = null;
    }
    public Derivation(String symbol, List<Derivation> derivations)
    {
        if(symbol.contains("'")){
            this.symbol = symbol.substring(0,symbol.lastIndexOf("'")+1);
        } else {
            this.symbol = symbol.substring(symbol.lastIndexOf(':')+1);
        }
        this.derivations = derivations;
    }

    public String toDot()
    {
        dotId = ++dotIdCount;
        StringBuilder sb = new StringBuilder();
        sb.append(dotId);
        sb.append("[label=\"");
        sb.append(symbol);
        sb.append("\"];\n");
        if(derivations != null){
            for (Derivation d : derivations) {
                sb.append(d.toDot());
                sb.append(dotId);
                sb.append(" -> ");
                sb.append(d.dotId);
                sb.append(";\n");
            }
        }
        return sb.toString();
    }

    //returns value the new indent
    public int prettyPrint(ArrayList<ColoredStringBuilder> sbs, int index, int indent, boolean color)
    {
        //print LHS/terminal
        ColoredStringBuilder sb = sbs.get(index);
        if(derivations != null && color){
            incrementCurrentColor();
        }
        sb.append(" ");
        if(color){
            sb.appendColored(symbol);
        } else {
            sb.append(symbol);
        }

        indent += symbol.length() + 1;
        if(derivations == null){
            return indent;
        }

        //If there is a RHS to print, prepare the next line
        try {
            sbs.get(index+1);
        } catch(IndexOutOfBoundsException e) {
           sbs.add(new ColoredStringBuilder());
        }

        ColoredStringBuilder nextsb = sbs.get(index+1);
        for (int i = nextsb.length(); i < indent-1; i++) {
            nextsb.append(" ");
        }
        if(color){
            nextsb.appendColored("↳");
        } else {
            nextsb.append("↳");
        }

        //print rhs
        for(Derivation d : derivations){
           indent = d.prettyPrint(sbs,index+1,indent,color);
        }

        //account for newly required whitespace
        for (int i = sb.length(); i < indent; i++) {
            sb.append(" ");
        }
        if(color){
            decrementCurrentColor();
        }

        return indent;
    }

    //ugly print used in the reference implementation, not used anymore.
    @Override
    public String toString()
    {
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
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Derivation that = (Derivation) o;

        if (!symbol.equals(that.symbol)) return false;
        return Objects.equals(derivations, that.derivations);
    }

    @Override
    public int hashCode()
    {
        int result = symbol.hashCode();
        result = 31 * result + (derivations != null ? derivations.hashCode() : 0);
        return result;
    }

}
