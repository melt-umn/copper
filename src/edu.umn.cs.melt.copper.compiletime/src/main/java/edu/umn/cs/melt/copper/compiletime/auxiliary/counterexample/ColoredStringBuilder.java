package edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample;

/**
 * Creates colored terminal output; useful for visualizing parser derivations.
 * @author Kelton OBrien
 */
public class ColoredStringBuilder {
    private static final String ANSI_RESET = "\u001B[0m";
    //yellow, green, blue, ...
    private static final String[] ansiColors = {"\u001B[36m","\u001B[33m","\u001B[32m","\u001B[34m","\u001B[31m","\u001B[35m"};
    private StringBuilder sb;
    private int colorEscapeCount;
    private static int currentColor = 0;

    public ColoredStringBuilder()
    {
        sb = new StringBuilder();
    }


    public static void incrementCurrentColor()
    {
        if(currentColor + 1 == ansiColors.length){
            currentColor = 0;
        } else {
            currentColor++;
        }
    }
    public static void decrementCurrentColor()
    {
        if(currentColor - 1 < 0){
            currentColor = ansiColors.length - 1;
        } else {
            currentColor--;
        }
    }
    public void appendColored(String input)
    {
        colorEscapeCount++;
        sb.append(ansiColors[currentColor]);
        sb.append(input);
    }

    public void append(String input)
    {
        sb.append(input);
    }

    public int length()
    {
        return sb.length() - (colorEscapeCount *5);
    }

    @Override
    public String toString()
    {
        sb.append(ANSI_RESET);
        return sb.toString();
    }
}
