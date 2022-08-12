package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

import java.util.ArrayList;

public class Counterexample {
    public Derivation derivation1;
    public Derivation derivation2;
    private boolean isShiftReduce;
    private ArrayList<ColoredStringBuilder> sb1 = new ArrayList<>();
    private ArrayList<ColoredStringBuilder> sb2 = new ArrayList<>();

    public Counterexample(Derivation derivation1, Derivation derivation2, boolean isShiftReduce) {
        this.derivation1 = derivation1;
        this.derivation2 = derivation2;
        this.isShiftReduce = isShiftReduce;
        sb1.add(new ColoredStringBuilder());
        sb2.add(new ColoredStringBuilder());
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Example:\n");
        sb.append(isShiftReduce? "shift derivation:\n" : "first reduce derivation:\n");
        derivation1.prettyPrint(sb1,0,0);
        for(ColoredStringBuilder s : sb1){
            sb.append(s);
            sb.append('\n');
        }
        sb.append(isShiftReduce? "reduce derivation:\n" : "second reduce derivation:\n");
        derivation2.prettyPrint(sb2,0,0);
        for(ColoredStringBuilder s : sb2){
            sb.append(s);
            sb.append('\n');
        }
        return sb.toString();
    }
}
