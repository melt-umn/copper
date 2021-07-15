package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

public class Counterexample {
    private Derivation derivation1;
    private Derivation derivation2;
    private boolean isShiftReduce;

    public Counterexample(Derivation derivation1, Derivation derivation2, boolean isShiftReduce) {
        this.derivation1 = derivation1;
        this.derivation2 = derivation2;
        this.isShiftReduce = isShiftReduce;
    }

    //TODO
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Example:\n");
        sb.append(isShiftReduce? "shift derivation" : "first reduce derivation:\n");
        sb.append(derivation1.toString());
        sb.append('\n');
        sb.append(isShiftReduce? "reduce derivation" : "second reduce derivation:\n");
        sb.append(derivation2.toString());
        sb.append("\n\n");
        return sb.toString();
    }
}
