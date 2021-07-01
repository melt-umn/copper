package edu.umn.cs.melt.copper.compiletime.logging.messages;

import edu.umn.cs.melt.copper.compiletime.auxiliary.Counterexample;
import edu.umn.cs.melt.copper.compiletime.auxiliary.Derivation;
import edu.umn.cs.melt.copper.compiletime.auxiliary.LookaheadSensitiveGraph;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadSets;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ContextSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import sun.jvm.hotspot.utilities.Bits;

import java.util.BitSet;

//TODO separate out a builder
//TODO comment
public class CounterexampleMessage implements CompilerLogMessage {
//    private PSSymbolTable symbolTable;
//    private LR0DFA dfa;
//    private LRLookaheadSets lookaheadSets;
//    private LRParseTableConflict conflict;
//    private ContextSets contextSets;
//    private ParserSpec spec;

    private LookaheadSensitiveGraph lookaheadSensitiveGraph;

    public CounterexampleMessage(PSSymbolTable symbolTable, LR0DFA dfa,
                                  LRParseTableConflict conflict,
                                 ContextSets contextSets, ParserSpec spec)
    {
        this.lookaheadSensitiveGraph = new LookaheadSensitiveGraph(conflict,spec,symbolTable,contextSets,dfa);
    }


    public CompilerLevel getLevel(){ return CompilerLevel.REGULAR; }

    public int getType(){ return CompilerLogMessageType.COUNTEREXAMPLE; }

    //well, kinda?
    public boolean isError(){ return false;}


    public boolean isFatalError(){ return false;}

    @Override
    public String toString(){
      return lookaheadSensitiveGraph.getNonUnifyingCounterExample().toString();
    }


}
