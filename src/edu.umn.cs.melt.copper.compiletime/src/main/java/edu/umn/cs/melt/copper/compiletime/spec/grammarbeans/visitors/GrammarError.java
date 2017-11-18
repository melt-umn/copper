package edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors;

import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Grammar;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.runtime.io.Location;

class GrammarError implements Comparable<GrammarError>
{
	private Location location;
	private ParserBean currentParser;
	private Grammar currentGrammar;
	private String message;
	
	public GrammarError(Location location,ParserBean currentParser,Grammar currentGrammar,String message)
	{
		this.location = location;
		this.currentParser = currentParser;
		this.currentGrammar = currentGrammar;
		this.message = message;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	public ParserBean getCurrentParser()
	{
		return currentParser;
	}

	public void setCurrentParser(ParserBean currentParser)
	{
		this.currentParser = currentParser;
	}

	public Grammar getCurrentGrammar()
	{
		return currentGrammar;
	}

	public void setCurrentGrammar(Grammar currentGrammar)
	{
		this.currentGrammar = currentGrammar;
	}

	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public boolean equals(Object o)
	{
		return (o instanceof GrammarError) && location.equals(((GrammarError) o).location) && message.equals(((GrammarError) o).message);
	}
	
	public int compareTo(GrammarError e)
	{
		int locationCompare,parserCompare,grammarCompare;
		if(location != null && e.location != null) locationCompare = location.compareTo(e.location);
		else if(location == null && e.location != null) locationCompare = -1;
		else if(location != null && e.location == null) locationCompare = 1;
		else /*if(location == null && e.location == null)*/ locationCompare = 0;
		if(locationCompare != 0) return locationCompare;
		
		if(currentParser != null && e.currentParser != null) parserCompare = currentParser.getName().compareTo(e.currentParser.getName()); 
		else if(currentParser == null && e.currentParser != null) parserCompare = -1;
		else if(currentParser != null && e.currentParser == null) parserCompare = 1;
		else /*if(currentParser == null && e.currentParser == null)*/ parserCompare = 0;
		if(parserCompare != 0) return parserCompare;

		if(currentGrammar != null && e.currentGrammar != null) grammarCompare = currentGrammar.getName().compareTo(e.currentGrammar.getName()); 
		else if(currentGrammar == null && e.currentGrammar != null) grammarCompare = -1;
		else if(currentGrammar != null && e.currentGrammar == null) grammarCompare = 1;
		else /*if(currentGrammar == null && e.currentGrammar == null)*/ grammarCompare = 0;
		if(grammarCompare != 0) return grammarCompare;

		return message.compareTo(e.message);
	}
	
	public String toString()
	{
		boolean displayParser = (currentParser != null);
		boolean displayGrammar = (currentGrammar != null && (currentParser == null || !currentParser.isUnitary()));
		
		StringBuffer rv = new StringBuffer();
		if(location != null)        rv.append(location);
		if(location != null && (displayParser || displayGrammar)) rv.append(" ");
		if(displayParser)   rv.append("[parser ").append(currentParser.getName()).append("]");
		if((location != null || displayParser) && displayGrammar) rv.append(" ");
		if(displayGrammar)  rv.append("[grammar ").append(currentGrammar.getName()).append("]");
		if(location != null || displayParser || displayGrammar) rv.append(": ");
		rv.append(message);
		return rv.toString();
	}
	
	public String toErrorMessage()
	{
		boolean displayParser = (currentParser != null);
		boolean displayGrammar = (currentGrammar != null && (currentParser == null || !currentParser.isUnitary()));

		StringBuffer rv = new StringBuffer();
		if(displayParser)   rv.append("[parser ").append(currentParser.getName()).append("]");
		if(displayParser && displayGrammar) rv.append(" ");
		if(displayGrammar)  rv.append("[grammar ").append(currentGrammar.getName()).append("]");
		if(displayParser || displayGrammar) rv.append(": ");
		rv.append(message);
		return rv.toString();
	}
}