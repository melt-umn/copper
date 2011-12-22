/**
 * 
 */
package edu.umn.cs.melt.copper.compiletime.abstractsyntax.regex;

import java.util.BitSet;
import java.util.HashSet;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.GrammarSource;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Symbol;
import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammar.Terminal;
import edu.umn.cs.melt.copper.compiletime.auxiliary.SetOfCharsSyntax;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.gdfa.GeneralizedNFA;
import edu.umn.cs.melt.copper.compiletime.finiteautomaton.oldnfa.NFA;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * Represents a "macro hole" -- a reference to another regex that will be substituted for it.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class MacroHole extends ParsedRegex
{
	Terminal terminalName;
	
	public MacroHole(Terminal terminalName)
	{
		this.terminalName = terminalName;
	}

	public NFA generateAutomaton(Symbol forRegex)
	{
		throw new UnsupportedOperationException("Undefined macro '" + terminalName + "'");
	}
	
	@Override
	public Pair<Integer, BitSet> generateAutomaton(GeneralizedNFA nfa)
	{
		throw new UnsupportedOperationException("Undefined macro '" + terminalName + "'");
	}

	public ParsedRegex fillMacroHoles(GrammarSource fillers)
	{
		if(fillers.hasRegex(terminalName)) return fillers.getRegex(terminalName).clone();
		else throw new UnsupportedOperationException("Undefined macro '" + terminalName + "'");
	}
	
	public HashSet<SetOfCharsSyntax> getTransitionLabels()
	{
		throw new UnsupportedOperationException("Undefined macro '" + terminalName + "'");		
	}

	public MacroHole clone()
	{
		return new MacroHole(terminalName);
	}
	
	public <SYNTYPE,INHTYPE,E extends Exception> SYNTYPE
        acceptVisitor(ParsedRegexVisitor<SYNTYPE,INHTYPE,E> visitor,INHTYPE inheritance)
	throws E
	{
		return visitor.visitMacroHole(this,inheritance);
	}
}
