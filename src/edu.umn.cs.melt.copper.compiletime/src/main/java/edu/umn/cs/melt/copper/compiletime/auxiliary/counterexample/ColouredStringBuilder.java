package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

public class ColouredStringBuilder {
    private static final String ANSI_RESET = "\u001B[0m";
    //yellow, green, blue, ...
    private static final String[] ansiColours = {"\u001B[36m","\u001B[33m","\u001B[32m","\u001B[34m","\u001B[31m","\u001B[35m"};
    private StringBuilder sb;
    private int colourEscapeCount;


    private static int currentColour = 0;

    //not used, added for completeness
    public static void resetCurrentColour(){
        currentColour = 0;
    }

    public static void incrementCurrentColour(){
        if(currentColour + 1 == ansiColours.length){
            currentColour = 0;
        } else {
            currentColour++;
        }
    }
    public static void decrementCurrentColour(){
        if(currentColour - 1 < 0){
            currentColour = ansiColours.length - 1;
        } else {
            currentColour--;
        }
    }

    public ColouredStringBuilder() {
        sb = new StringBuilder();
    }

    public void appendColoured(String input){
        colourEscapeCount++;
        sb.append(ansiColours[currentColour]+input);
    }

    public void append(String input){
        sb.append(input);
    }

    public int length(){
        return sb.length() - (colourEscapeCount*5);
    }

    @Override
    public String toString(){
        sb.append(ANSI_RESET);
        return sb.toString();
    }
}
