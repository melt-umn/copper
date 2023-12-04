package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

import java.util.ArrayList;

public class Counterexample {
    public Derivation derivation1;
    public Derivation derivation2;
    private boolean isShiftReduce;
    private ArrayList<ColoredStringBuilder> sb1 = new ArrayList<>();
    private ArrayList<ColoredStringBuilder> sb2 = new ArrayList<>();

    public Counterexample(Derivation derivation1, Derivation derivation2, boolean isShiftReduce)
    {
        this.derivation1 = derivation1;
        this.derivation2 = derivation2;
        this.isShiftReduce = isShiftReduce;
        sb1.add(new ColoredStringBuilder());
        sb2.add(new ColoredStringBuilder());
    }


    public String toDot()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        sb.append("subgraph cluster_0 {\n");
        sb.append("label=\"");
        sb.append(isShiftReduce? "reduce derivation\";\n" : "first reduce derivation\";\n");
        sb.append(derivation1.toDot());
        sb.append("}\n");
        sb.append("subgraph cluster_1 {\n");
        sb.append("label=\"");
        sb.append(isShiftReduce? "shift derivation\";\n" : "second reduce derivation\";\n");
        sb.append(derivation2.toDot());
        sb.append("}\n");
        sb.append("}");
        return sb.toString();

    }

    public String prettyPrint(boolean color)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Counterexample (2 parse trees that are the same until the conflict point, and may differ after):\n");
        sb.append(isShiftReduce? "reduce derivation:\n" : "first reduce derivation:\n");
        derivation1.prettyPrint(sb1,0,0,color);
        for(ColoredStringBuilder s : sb1){
            sb.append(s);
            sb.append('\n');
        }
        sb.append(isShiftReduce? "shift derivation:\n" : "second reduce derivation:\n");
        derivation2.prettyPrint(sb2,0,0,color);
        for(ColoredStringBuilder s : sb2) {
            sb.append(s);
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public String toString()
    {
        return prettyPrint(true);
    }
}
