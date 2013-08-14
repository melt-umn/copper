package edu.umn.cs.melt.copper.runtime.engines;

public interface CopperScanner<STATE,TOKEN,EXCEPT extends Exception>
{
	public TOKEN pullToken(STATE state)
	throws EXCEPT;
}
