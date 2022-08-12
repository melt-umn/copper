package edu.umn.cs.melt.copper.compiletime.logging.messages;

import edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample.CounterexampleSearch;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessage;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogMessageType;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadSets;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTableConflict;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ContextSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

//TODO separate out a builder
//TODO comment
public class CounterexampleMessage implements CompilerLogMessage {

    private CounterexampleSearch counterexampleSearch;

    public CounterexampleMessage(PSSymbolTable symbolTable, LR0DFA dfa,
                                 LRParseTableConflict conflict,
                                 ContextSets contextSets, ParserSpec spec,
                                 LRLookaheadSets lookaheadSets) {
        this.counterexampleSearch = new CounterexampleSearch(conflict,spec,symbolTable,contextSets,lookaheadSets, dfa);
    }


    public CompilerLevel getLevel(){
        return CompilerLevel.REGULAR;
    }

    public int getType(){
        return CompilerLogMessageType.COUNTEREXAMPLE;
    }

    //well, kinda?
    public boolean isError(){
        return false;
    }


    public boolean isFatalError() {
        return false;
    }

    //TODO add a flag to disable attemping the unified example and just do the non-unified example
    @Override
    public String toString(){
        // return counterExampleSearch.getNonUnifyingCounterexample().toString();
        return counterexampleSearch.toString();
    }


}
