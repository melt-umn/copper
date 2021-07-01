package edu.umn.cs.melt.copper.legacy.compiletime.logging;

public abstract class GrammarDumper
{
	public void completeDump()
	{
		dumpPreamble();
		dumpTerminals();
		dumpNonTerminals();
		dumpProductions();
		dumpPrecedenceGraph();
		dumpDisambigGroups();
		dumpLALR1DFA();
		dumpParseTable();
		dumpPostamble();
	}
	
	public abstract void dumpPreamble();
	public abstract void dumpTerminals();
	public abstract void dumpPrecedenceGraph();
	public abstract void dumpDisambigGroups();
	public abstract void dumpNonTerminals();
	public abstract void dumpProductions();
	public abstract void dumpLALR1DFA();
	public abstract void dumpParseTable();
	public abstract void dumpPostamble();
	
	public void logPlain()
	{
		throw new FatalCompileErrorException("Plaintext dump not available from this dumper");
	}
	public void logHTML()
	{
		throw new FatalCompileErrorException("HTML dump not available from this dumper");
	}
	public void logXML()
	{
		throw new FatalCompileErrorException("XML dump not available from this dumper");
	}
}
