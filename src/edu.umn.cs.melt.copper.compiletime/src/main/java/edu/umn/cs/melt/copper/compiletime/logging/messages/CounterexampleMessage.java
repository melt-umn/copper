package edu.umn.cs.melt.copper.compiletime.logging.messages;

import edu.umn.cs.melt.copper.compiletime.auxiliary.counterexample.Counterexample;
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

public class CounterexampleMessage implements CompilerLogMessage {

    private CounterexampleSearch counterexampleSearch;
    private Counterexample counterexample;

    private boolean colorExample;

    public CounterexampleMessage(PSSymbolTable symbolTable, LR0DFA dfa,
                                 LRParseTableConflict conflict,
                                 ContextSets contextSets, ParserSpec spec,
                                 LRLookaheadSets lookaheadSets, boolean colorExample) {
        this.counterexampleSearch = new CounterexampleSearch(conflict,spec,symbolTable,contextSets,lookaheadSets, dfa);
        this.counterexample = counterexampleSearch.getExample();
        this.colorExample = colorExample;
    }


    public CompilerLevel getLevel(){
        return CompilerLevel.REGULAR;
    }

    public int getType(){
        return CompilerLogMessageType.COUNTEREXAMPLE;
    }

    public boolean isError(){
        return false;
    }


    public boolean isFatalError() {
        return false;
    }


    @Override
    public String toString(){
        return counterexampleSearch.getExample().prettyPrint(colorExample);
    }

    public String toDot()
    {
        return  counterexample.toDot();
    }


}
