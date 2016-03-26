package edu.umn.cs.melt.copper.runtime.engines.single;

import edu.umn.cs.melt.copper.runtime.engines.single.scanner.SingleDFAMatchData;
import edu.umn.cs.melt.copper.runtime.io.InputPosition;
import edu.umn.cs.melt.copper.runtime.io.ScannerBuffer;

import java.io.IOException;
import java.io.Reader;
import java.util.BitSet;
import java.util.LinkedList;

/**
 * @author Kevin Viratyosin
 */
public abstract class ParserFragmentEngine<ROOT, EXCEPT extends Exception> extends SingleDFAEngine<ROOT, EXCEPT> {

    private static class ScannerParams {
        public int fragmentId;
        public BitSet[] shiftableSets;
        public BitSet shiftableUnion;
        public BitSet[] prefixSets;
        public BitSet[][] prefixMaps;
        public int eofSymNum;
        public int startState;
        public int terminalCount;
        public BitSet[] possibleSets;
        public BitSet[] acceptSets;
        public BitSet[] rejectSets;
        public int[][] transitionTable;
    }

    protected ScannerParams[] fragmentScanners;
    protected ScannerParams markingTerminalScanner;

    // Function from SingleDFAEngine that still need to be implemented
    public abstract int[][] getParseTable();
    protected abstract void reportSyntaxError() throws EXCEPT;
    public abstract int[] getProductionLHSs();
    public abstract BitSet[] getDisambiguationGroups();
    protected abstract Object runSemanticAction(InputPosition _pos, Object[] _children, int _prod) throws IOException,EXCEPT;

    // New abstract functions
    protected abstract int getFragmentCount();
    protected abstract int stateToFragmentId(int state);
    protected abstract int[] getProductionLengths();
    protected abstract int runFragmentDisambiguationAction(int fragmentId, InputPosition _pos,SingleDFAMatchData match) throws IOException,EXCEPT;
    protected abstract Object runFragmentSemanticAction(int fragmentId, InputPosition _pos, SingleDFAMatchData _terminal) throws IOException,EXCEPT;

    // New abstract functions (scanner getters)
    protected abstract int[][] getFragmentTransitionTable(int fragmentId);
    protected abstract BitSet[] getFragmentRejectSets(int fragmentId);
    protected abstract BitSet[] getFragmentAcceptSets(int fragmentId);
    protected abstract BitSet[] getFragmentPossibleSets(int fragmentId);
    protected abstract int getFragmentTerminalCount(int fragmentId);
    protected abstract int getFragmentStartState(int fragmentId);
    protected abstract int getFragmentEOFSymNum(int fragmentId);
    protected abstract BitSet[][] getFragmentPrefixMaps(int fragmentId);
    protected abstract BitSet[] getFragmentLayoutSets(int fragmentId);
    protected abstract BitSet[] getFragmentPrefixSets(int fragmentId);
    protected abstract int getFragmentTerminalUses(int fragmentId, int t);
    protected abstract BitSet getFragmentShiftableUnion(int fragmentId);
    protected abstract BitSet[] getFragmentShiftableSets(int fragmentId);

    @Override
    public int getTERMINAL_COUNT() { throw new UnsupportedOperationException(); }
    @Override
    public int getEOF_SYMNUM() { throw new UnsupportedOperationException(); }
    @Override
    public int[] getSymbolNumbers() { throw new UnsupportedOperationException(); }
    @Override
    public BitSet[] getShiftableSets() { throw new UnsupportedOperationException(); }
    @Override
    public BitSet[] getLayoutSets() { throw new UnsupportedOperationException(); }
    @Override
    public BitSet[] getPrefixSets() { throw new UnsupportedOperationException(); }
    @Override
    public int[] getTerminalUses() { throw new UnsupportedOperationException(); }
    @Override
    public BitSet[][] getLayoutMaps() { throw new UnsupportedOperationException(); }
    @Override
    public BitSet[][] getPrefixMaps() { throw new UnsupportedOperationException(); }
    @Override
    public BitSet getShiftableUnion() { throw new UnsupportedOperationException(); }
    @Override
    public BitSet[] getAcceptSets() { throw new UnsupportedOperationException(); }
    @Override
    public BitSet[] getRejectSets() { throw new UnsupportedOperationException(); }
    @Override
    public BitSet[] getPossibleSets() { throw new UnsupportedOperationException(); }
    @Override
    protected int transition(int state, char ch) { throw new UnsupportedOperationException(); }
    @Override
    protected int runDisambiguationAction(InputPosition _pos,SingleDFAMatchData match) throws IOException,EXCEPT {
        throw new UnsupportedOperationException();
    }
    @Override
    protected Object runSemanticAction(InputPosition _pos, SingleDFAMatchData _terminal) throws IOException,EXCEPT {
        throw new UnsupportedOperationException();
    }

    @Override
    public ROOT parse(Reader input,String inputName) throws IOException,EXCEPT {
        this.charBuffer = ScannerBuffer.instantiate(input);
        startEngine(InputPosition.initialPos(inputName));
        ROOT parseTree = (ROOT) runEngine();
        return parseTree;
    }

    protected SingleDFAMatchData parameterizedSimpleScan(
            InputPosition whence,
            BitSet shiftable,
            LinkedList<SingleDFAMatchData> layouts,
            ScannerParams params
    ) throws IOException {
        //System.err.println("Simple-shiftable " + bitVecToDisplayStringList(shiftable) + "; token buffer size: " + tokenBuffer.size());
        if(!tokenBuffer.isEmpty())
        {
            SingleDFAMatchData tok = tokenBuffer.poll();
            tok.precedingPos = whence;
            tok.followingPos = whence;
            lastMatchFromQueue = true;
            return tok;
        }
        //else lastMatchFromQueue = false;
        int currentState = params.startState;
        char symbol = '\0';
        InputPosition p;
        BitSet shiftableS = (BitSet) shiftable.clone();
        BitSet present = newBitVec(params.terminalCount);
        InputPosition presentPos = whence;
        for(p = InputPosition.copy(whence);;p = InputPosition.advance(p,symbol))
        {
            BitSet tP = (BitSet) params.possibleSets[currentState].clone();
            tP.and(shiftableS);
            if(p.equals(whence) && shiftableS.get(params.eofSymNum)) tP.set(params.eofSymNum);
            if(tP.isEmpty()) break;
            shiftableS.and(tP);
            BitSet tA = (BitSet) params.acceptSets[currentState].clone();
            tA.and(shiftableS);
            if(!tA.isEmpty())
            {
                present = tA;
                presentPos = InputPosition.copy(p);
            }
            else
            {
                BitSet tR = (BitSet) params.rejectSets[currentState].clone();
                tR.and(shiftableS);
                if(!tR.isEmpty())
                {
                    present.clear();
                    presentPos = whence;
                }
            }
            symbol = charBuffer.charAt(p.getPos());
            if(symbol == ScannerBuffer.EOFIndicator)
            {
                break;
            }
            currentState = params.transitionTable[currentState][symbol];
        }
        if(symbol == ScannerBuffer.EOFIndicator &&
                p.equals(whence) &&
                shiftableS.get(params.eofSymNum))
        {
            present.set(params.eofSymNum);
            return new SingleDFAMatchData(present,whence,p,"",layouts);
        }
        else return new SingleDFAMatchData(present,whence,presentPos,charBuffer.readStringFromBuffer(whence.getPos(),presentPos.getPos()),layouts);
    }

    protected SingleDFAMatchData parameterizedMaybeDisjointScan(
            InputPosition whence,
            BitSet shiftable,
            LinkedList<SingleDFAMatchData> layouts,
            boolean runDisjoint,
            ScannerParams params
    ) throws IOException {
        if(!runDisjoint) return parameterizedSimpleScan(whence, shiftable, layouts, params);
        else return parameterizedSimpleScan(whence, params.shiftableUnion, layouts, params);
    }

    protected SingleDFAMatchData parameterizedLayoutScan(
            boolean runDisjoint,
            SingleDFAMatchData previousResult,
            ScannerParams params
    ) throws IOException, EXCEPT {
        BitSet shiftable = params.shiftableSets[currentState.statenum];
        InputPosition whence;
        if(runDisjoint)
        {
            if(!previousResult.layouts.isEmpty()) whence = previousResult.layouts.getLast().followingPos;
            else whence = currentState.pos;
        }
        else whence = currentState.pos;
        charBuffer.advanceBufferTo(whence.getPos());
        if(!runDisjoint && whence.equals(lastPosition) && lastAction != STATE_SHIFT)
        {
            if(lastMatchFromQueue)
            {
                lastShiftable = shiftable;
                return lastMatched;
            }
            if(lastMatched != null && !functionalDisambiguationUsed)
            {
                boolean partiallyDisjoint = false;
                BitSet diff = new BitSet();
                diff.or(lastMatched.terms); // TODO handle last matched was term from different fragment
                diff.andNot(shiftable);
                partiallyDisjoint = !diff.isEmpty();
                if(!partiallyDisjoint)
                {
                    lastShiftable = shiftable;
                    return lastMatched;
                }
            }
        }

        lastPosition = whence;
        lastShiftable = shiftable;
        functionalDisambiguationUsed = false;
        lastMatchFromQueue = false;

        LinkedList<SingleDFAMatchData> layouts = new LinkedList<SingleDFAMatchData>();
        SingleDFAMatchData finalMatches;

        //for(int i = 0;;i++)
        for(;;)
        {
            // DEBUG-X-BEGIN
            //System.err.println("Parse state " + currentState.statenum + "; iteration " + i + "; location " + whence + " (char " + whence.getPos() + "); runDisjoint = " + runDisjoint + "; shiftable " + bitVecToString(shiftable) + " --");
            // DEBUG-X-END
            finalMatches = parameterizedMaybeDisjointScan(whence, shiftable, layouts, runDisjoint, params);

            if(finalMatches.terms.cardinality() > 1)
            {
                // DEBUG-BEGIN
                //System.err.println("Ambiguity: " + bitVecToString(finalMatches.terms) + "; runDisjoint = " + runDisjoint);
                // DEBUG-END
                if(finalMatches.terms.get(params.eofSymNum) && finalMatches.lexeme.isEmpty())
                {
                    finalMatches.terms.clear();
                    finalMatches.terms.set(params.eofSymNum);
                    finalMatches.firstTerm = params.eofSymNum;
                }
                else
                {
                    functionalDisambiguationUsed = true;
                    int disambiguatedTerm = runFragmentDisambiguationAction(params.fragmentId, currentState.pos, finalMatches);
                    if(disambiguatedTerm == -1)
                    {
                        int firstActionIndex = finalMatches.firstTerm;
                        int action = getParseTable()[currentState.statenum][firstActionIndex];
                        if(actionType(action) == STATE_REDUCE) disambiguatedTerm = firstActionIndex;

                        for(int j = finalMatches.terms.nextSetBit(firstActionIndex + 1);j >= 0;j = finalMatches.terms.nextSetBit(j + 1))
                        {
                            if(action != getParseTable()[currentState.statenum][j])
                            {
                                disambiguatedTerm = -1;
                                break;
                            }
                        }
                    }
                    if(disambiguatedTerm == -1)
                    {
                        if(!runDisjoint)
                        {
                            reportError(formatError("Lexical ambiguity between tokens:\n" + bitVecToString(finalMatches.terms)));
                            return finalMatches;
                        }
                    }
                    else
                    {
                        finalMatches.terms.clear();
                        finalMatches.terms.set(disambiguatedTerm);
                        finalMatches.firstTerm = disambiguatedTerm;
                    }
                }

                if(finalMatches.terms.cardinality() > 1) return finalMatches;
            }

            if(/*layouts.isEmpty() &&*/ finalMatches.terms.isEmpty())
            {
                // DEBUG-X-BEGIN
                //System.err.println("No matches");
                // DEBUG-X-END
                break;
            }
            else if(runDisjoint &&
                    !shiftable.get(finalMatches.firstTerm))
            {
                break;
            }

            int useAs = getFragmentTerminalUses(params.fragmentId, finalMatches.firstTerm);
            if (useAs == TERMINAL_VERSATILE) {
                if(getFragmentLayoutSets(params.fragmentId)[currentState.statenum].get(finalMatches.firstTerm)) useAs = TERMINAL_EXCLUSIVELY_LAYOUT;
                else if(params.prefixSets[currentState.statenum].get(finalMatches.firstTerm)) useAs = TERMINAL_EXCLUSIVELY_PREFIX;
                else useAs = TERMINAL_EXCLUSIVELY_SHIFTABLE;
            }

            switch(useAs)
            {
                case TERMINAL_EXCLUSIVELY_SHIFTABLE:
                    // DEBUG-X-BEGIN
                    // System.err.println("Shiftable match");
                    // DEBUG-X-END
                    if(!finalMatches.terms.isEmpty()) lastMatched = finalMatches;
                    return finalMatches;
                case TERMINAL_EXCLUSIVELY_LAYOUT:
                    // DEBUG-X-BEGIN
                    // System.err.println("Layout match");
                    // DEBUG-X-END
                    whence = InputPosition.advance(whence,finalMatches.lexeme.length(),finalMatches.lexeme);
                    if(finalMatches.lexeme.length() == 0)
                    {
                        //System.err.println("Empty layout match");
                        if(layouts.isEmpty())
                        {
                            layouts.add(finalMatches);
                        }
                        shiftable = (BitSet) shiftable.clone();
                        shiftable.andNot(getFragmentLayoutSets(params.fragmentId)[currentState.statenum]);
                    }
                    else
                    {
                        layouts.add(finalMatches);
                        continue;
                    }
                    break;
                case TERMINAL_EXCLUSIVELY_PREFIX:
                    // DEBUG-X-BEGIN
                    // System.err.println("Prefix match");
                    // DEBUG-X-END
                    layouts.add(finalMatches);
                    shiftable = params.prefixMaps[currentState.statenum][finalMatches.firstTerm];
                    whence = InputPosition.advance(whence,finalMatches.lexeme.length(),finalMatches.lexeme);
                    break;
                default:
                    reportError(formatError("Cannot determine whether terminal is layout, prefix, or shiftable --- bug in scanner"));
            }
        }
        if(!finalMatches.terms.isEmpty()) lastMatched = finalMatches;
        return finalMatches;
    }

    protected SingleDFAMatchData multiLayoutScan(int fragmentId) throws IOException,EXCEPT {
        // assumes fragmentId is an extension id (> 0)

        // marking terminals take precedence
        // TODO double check that this is handled by validLA -- "shiftable"?
        SingleDFAMatchData mtScanResult = parameterizedLayoutScan(false, null, markingTerminalScanner);
        if (mtScanResult.terms.cardinality() > 1) {
            throw new RuntimeException("Ambiguous marking terminal match: " + bitVecToDisplayStringList(mtScanResult.terms)); // Should not happen
        } else if (!mtScanResult.terms.isEmpty()) {
            return mtScanResult;
        }

        ScannerParams extParams = fragmentScanners[fragmentId];
        SingleDFAMatchData extScanResult = parameterizedLayoutScan(false, null, extParams);
        if (extScanResult.terms.isEmpty()) {
            disjointMatch = parameterizedLayoutScan(true, extScanResult, extParams);
            for (SingleDFAMatchData layout : extScanResult.layouts) {
                runFragmentSemanticAction(fragmentId, layout.precedingPos, layout);
                virtualLocation.defaultUpdateAutomatic(layout.lexeme);
            }
            reportSyntaxError();
        } else if (extScanResult.terms.cardinality() > 1) {
            throw new RuntimeException("Ambiguous match: " + bitVecToDisplayStringList(extScanResult.terms)); // Should not happen.
        }

        return extScanResult;
    }

    @Override
    protected void startEngine(InputPosition initialPos) throws IOException, EXCEPT {
        super.startEngine(initialPos);

        fragmentScanners = new ScannerParams[getFragmentCount()];
        for (int fragmentId = 0; fragmentId < getFragmentCount(); fragmentId++) {
            ScannerParams fragmentScannerParams = new ScannerParams();
            fragmentScannerParams.fragmentId = fragmentId;
            fragmentScannerParams.shiftableSets = getFragmentShiftableSets(fragmentId);
            fragmentScannerParams.shiftableUnion = getFragmentShiftableUnion(fragmentId);
            fragmentScannerParams.prefixSets = getFragmentPrefixSets(fragmentId);
            fragmentScannerParams.prefixMaps = getFragmentPrefixMaps(fragmentId);
            fragmentScannerParams.eofSymNum = getFragmentEOFSymNum(fragmentId);
            fragmentScannerParams.startState = getFragmentStartState(fragmentId);
            fragmentScannerParams.terminalCount = getFragmentTerminalCount(fragmentId);
            fragmentScannerParams.possibleSets = getFragmentPossibleSets(fragmentId);
            fragmentScannerParams.acceptSets = getFragmentAcceptSets(fragmentId);
            fragmentScannerParams.rejectSets = getFragmentRejectSets(fragmentId);
            fragmentScannerParams.transitionTable = getFragmentTransitionTable(fragmentId);
            fragmentScanners[fragmentId] = fragmentScannerParams;
        }

        markingTerminalScanner = fragmentScanners[0];
    }

    @Override
    protected Object runEngine() throws IOException,EXCEPT {
        while(true)
        {
            // DEBUG-X-BEGIN
            //System.err.println(parseStack);
            // DEBUG-X-END
            currentState = parseStack.peek();
            int fragmentId = stateToFragmentId(currentState.statenum);

            SingleDFAMatchData scanResult = multiLayoutScan(fragmentId);
            // DEBUG-X-BEGIN
            //System.err.println(bitVecToString(scanResult.terms));
            // DEBUG-X-END
            int action = getParseTable()[currentState.statenum][scanResult.firstTerm];
            // TODO adjust here or in scanning method to account for case when result is a marking terminal! (offset)
            Object synthAttr;
            switch(actionType(action))
            {
                case STATE_ACCEPT:
                    for(SingleDFAMatchData layout : scanResult.layouts)
                    {
                        runFragmentSemanticAction(fragmentId, layout.precedingPos, layout);
                        virtualLocation.defaultUpdateAutomatic(layout.lexeme);
                    }
                    return parseStack.peek().synthAttr;
                case STATE_SHIFT:
                    int nextState = actionIndex(action);
                    for(SingleDFAMatchData layout : scanResult.layouts)
                    {
                        runFragmentSemanticAction(fragmentId, layout.precedingPos, layout);
                        virtualLocation.defaultUpdateAutomatic(layout.lexeme);
                    }
                    synthAttr = runFragmentSemanticAction(fragmentId, scanResult.precedingPos, scanResult);
                    virtualLocation.defaultUpdateAutomatic(scanResult.lexeme);
                    parseStack.push(new SingleDFAParseStackNode(nextState,scanResult.followingPos,synthAttr));
                    // DEBUG-X-BEGIN
                    //System.err.println("shift(" + nextState + ")");
                    // DEBUG-X-END
                    break;
                case STATE_REDUCE:
                    int production = actionIndex(action);
                    int productionLength = actionIndex(getProductionLengths()[production]);
                    int productionLHS = actionIndex(getProductionLHSs()[production]);
                    Object[] children = new Object[productionLength];
                    for(int i = productionLength - 1;i >= 0;i--)
                    {
                        children[i] = parseStack.pop().synthAttr;
                    }
                    int gotoState = actionIndex(getParseTable()[parseStack.peek().statenum][productionLHS]);
                    synthAttr = runSemanticAction(currentState.pos, children, production);
                    parseStack.push(new SingleDFAParseStackNode(gotoState,currentState.pos,synthAttr));
                    // DEBUG-X-BEGIN
                    //System.err.println("reduce(" + production + "); goto(" + gotoState + ")");
                    // DEBUG-X-END
                    break;
                default:
                    // DEBUG-X-BEGIN
                    //System.err.println(bitVecToString(scanResult.terms));
                    // DEBUG-X-END
                    disjointMatch = scanResult;
                    reportSyntaxError();
            }
            lastAction = actionType(action);
        }
    }
}
