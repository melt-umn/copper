package edu.umn.cs.melt.copper.compiletime.logging.messages;

import java.util.BitSet;

import edu.umn.cs.melt.copper.compiletime.auxiliary.SymbolTable;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.LexicalAmbiguities;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperASTBean;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;

public class LexicalAmbiguityMessage implements CompilerLogMessage
{
	private SymbolTable<CopperASTBean> symbolTable;
	private boolean isUnresolved;
	private BitSet ambiguity;
	private BitSet states;
	private int resolution;
	
	public LexicalAmbiguityMessage(SymbolTable<CopperASTBean> symbolTable,LexicalAmbiguities ambiguities,int ambiguity)
	{
		this.symbolTable = symbolTable;
		this.isUnresolved = ambiguities.isUnresolved(ambiguity);
		this.ambiguity = ambiguities.getAmbiguity(ambiguity);
		this.states = ambiguities.getLocations(ambiguity);
		this.resolution = ambiguities.getResolution(ambiguity);
	}
	
	@Override
	public CompilerLevel getLevel()
	{
		if(isUnresolved) return CompilerLevel.QUIET;
		else return CompilerLevel.VERBOSE;
	}

	@Override
	public int getType()
	{
		return CompilerLogMessageType.LEXICAL_AMBIGUITY;
	}

	@Override
	public boolean isError()
	{
		return isUnresolved;
	}

	@Override
	public boolean isFatalError()
	{
		return false;
	}
	
	public String toString()
	{
		String ambigHash = PSSymbolTable.bitSetPrettyPrint(ambiguity,symbolTable,"    ",80);
		StringBuffer rv = new StringBuffer();
		if(isUnresolved) rv.append("Unresolvable l");
		else rv.append("L");
		rv.append("exical ambiguity");
		if(isUnresolved)
		{
			String statesHash = PSSymbolTable.bitSetPrettyPrint(states,null,"   ",80);
			rv.append(" at parser states").append(statesHash);
		}
		rv.append(ambiguity.cardinality() == 2 ? " between" : " among").append(" terminals:\n   ").append(ambigHash);
		if(!isUnresolved) rv.append("\nResolved by ").append(resolution == -1 ? "context" : "disambiguation function");
		return rv.toString();
	}
}
