package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.compiletime.builders.*;
import edu.umn.cs.melt.copper.compiletime.checkers.*;
import edu.umn.cs.melt.copper.compiletime.dumpers.*;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericLocatedMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.TimingMessage;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LR0DFA;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRDFAPrinter;
import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.mda.MDAResults;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTablePrinter;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.LexicalAmbiguities;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.CopperElementType;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ExtendedParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.NumericParserSpecBuilder;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.SymbolTableBuilder;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ContextSets;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;
import edu.umn.cs.melt.copper.main.CopperDumpControl;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

/**
 * Constructs the parser, runs checks, and logs messages. The core of the parser generator.
 * Extended and used by both {@link StandardSpecCompiler} and  {@link FragmentGenerator}
 * @author Kevin Viratyosin
 *
 * Extracted from StandardSpecCompiler
 */
public abstract class ParserBeanCompiler<RETURNDATA> implements SpecCompiler<ParserBean,RETURNDATA> {
    protected boolean succeeded;
    protected int errorlevel;
    protected PSSymbolTable symbolTable;
    protected ParserSpec fullSpec;
    protected String packageDecl;
    protected String parserName;
    protected LRLookaheadAndLayoutSets lookaheadSets;
    protected LRParseTable parseTable;
    protected GrammarStatistics stats;
    protected TransparentPrefixes prefixes;
    protected GeneralizedDFA scannerDFA;
    protected SingleScannerDFAAnnotations scannerDFAAnnotations;

    protected ParserSpec hostSpec;
    protected LR0DFA fullDFA;
    protected MDAResults mdaResults;

    protected boolean compileParserBean(ParserBean spec, ParserCompilerParameters args) throws CopperException {
        boolean doMDA = args.isRunMDA();
        if (spec == null) {
            return false;
        }
        boolean succeeded = true;
        CompilerLogger logger = AuxiliaryMethods.getOrMakeLogger(args);

        if (args.getDump() == CopperDumpControl.ON || (args.getDump() == CopperDumpControl.ERROR_ONLY && !succeeded)) {
            Dumper dumper = null;

            switch (args.getDumpFormat())
            {
                case XML_SPEC:
                    dumper = new XMLSpecDumper(spec);
                    break;
                default:
                    break;
            }

            dumpIfNecessary(args, succeeded, dumper);
        }

        String packageDecl =
                (args.getPackageName() != null && !args.getPackageName().equals("")) ?
                        args.getPackageName() :
                        (spec.getPackageDecl() != null && !spec.getPackageDecl().equals("") ?
                                spec.getPackageDecl() :
                                "");
        String parserName =
                (args.getParserName() != null && !args.getParserName().equals("")) ?
                        args.getParserName() :
                        (spec.getClassName() != null && !spec.getClassName().equals("") ?
                                spec.getClassName() :
                                "Parser");

        if (doMDA && spec.getType() != CopperElementType.EXTENDED_PARSER) {
            logger.log(new GenericLocatedMessage(CompilerLevel.QUIET, spec.getLocation(), "Cannot run the modular determinism analysis on a non-extended parser",true,false));
            logger.flush();
            return false;
        }

        long timeBefore;

        timeBefore = System.currentTimeMillis();

        PSSymbolTable symbolTable = SymbolTableBuilder.build(spec);

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing symbol table",System.currentTimeMillis() - timeBefore));
        if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Symbol table:\n" + symbolTable));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        ParserSpec hostSpec = null;

        if(doMDA && spec.getType() == CopperElementType.EXTENDED_PARSER) {
            hostSpec = NumericParserSpecBuilder.buildExt((ExtendedParserBean) spec,symbolTable,true);
        }
        ParserSpec fullSpec = NumericParserSpecBuilder.build(spec,symbolTable);

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing numeric grammar specification",System.currentTimeMillis() - timeBefore));
        if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Numeric spec:\n" + fullSpec.toString(symbolTable)));
        logger.flush();

        GrammarStatistics stats = new GrammarStatistics(fullSpec);
        timeBefore = System.currentTimeMillis();

        succeeded &= GrammarWellFormednessChecker.check(logger, stats, symbolTable, fullSpec, args.isWarnUselessNTs());

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Checking grammar well-formedness",System.currentTimeMillis() - timeBefore));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        succeeded &= DisambiguationFunctionConflictChecker.check(logger, symbolTable, fullSpec);

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Checking for conflicting disambiguation functions",System.currentTimeMillis() - timeBefore));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        ContextSets hostContextSets = (hostSpec != null) ? ContextSetBuilder.build(hostSpec) : null;
        ContextSets contextSets = ContextSetBuilder.build(fullSpec);

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing context sets",System.currentTimeMillis() - timeBefore));
        if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Context sets:\n" + contextSets.toString(symbolTable)));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        LR0DFA hostDFA = (hostSpec != null) ? LR0DFABuilder.build(hostSpec) : null;
        LR0DFA dfa = LR0DFABuilder.build(fullSpec);
        stats.parseStateCount = dfa.size() - 1;

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing LR(0) DFA",System.currentTimeMillis() - timeBefore));
        if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"LR(0) DFA:\n" + LRDFAPrinter.toString(symbolTable,fullSpec,dfa)));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        LRLookaheadAndLayoutSets hostLookaheadSets = (hostSpec != null) ? LALRLookaheadAndLayoutSetBuilder.build(hostSpec,hostContextSets,hostDFA) : null;
        LRLookaheadAndLayoutSets lookaheadSets = LALRLookaheadAndLayoutSetBuilder.build(fullSpec,contextSets,dfa);

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing LALR lookahead/layout sets",System.currentTimeMillis() - timeBefore));
        if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"LALR(1) DFA:\n" + LRDFAPrinter.toString(symbolTable,fullSpec,dfa,lookaheadSets)));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        MDAResults mdaResults = null;
        if(hostSpec != null)
        {
            mdaResults = ModularDeterminismAnalyzer.build(true,hostSpec,fullSpec,hostContextSets,contextSets,hostDFA,dfa,hostLookaheadSets,lookaheadSets);
            succeeded &= MDAResultChecker.check(logger, mdaResults, symbolTable, fullSpec, dfa, stats);

            if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Running modular determinism analysis",System.currentTimeMillis() - timeBefore));
            logger.flush();

            timeBefore = System.currentTimeMillis();
        }

        LRParseTable parseTable = LRParseTableBuilder.build(fullSpec,dfa,lookaheadSets);

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing parse table",System.currentTimeMillis() - timeBefore));
        if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Parse table:\n" + LRParseTablePrinter.toString(symbolTable,fullSpec,parseTable)));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        succeeded &= ParseTableConflictChecker.check(logger, args.getDotOutput(), symbolTable, fullSpec, parseTable,
                dfa, contextSets,lookaheadSets, stats, args.isColorCounterexample());

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Checking parse table conflicts",System.currentTimeMillis() - timeBefore));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        TransparentPrefixes prefixes = TransparentPrefixSetBuilder.build(fullSpec,parseTable);

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing transparent prefix sets",System.currentTimeMillis() - timeBefore));
        if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Transparent prefix sets:\n" + prefixes.toString(symbolTable)));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        GeneralizedDFA scannerDFA = SingleScannerDFABuilder.build(fullSpec);
        stats.scannerStateCount = scannerDFA.stateCount();

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing scanner DFA",System.currentTimeMillis() - timeBefore));
        if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Scanner DFA:\n" + scannerDFA));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        SingleScannerDFAAnnotations scannerDFAAnnotations = SingleScannerDFAAnnotationBuilder.build(fullSpec,scannerDFA);

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Constructing scanner DFA annotations (accept/reject/possible sets, character map)",System.currentTimeMillis() - timeBefore));
        if(logger.isLoggable(CompilerLevel.VERY_VERBOSE)) logger.log(new GenericMessage(CompilerLevel.VERY_VERBOSE,"Scanner DFA annotations:\n" + scannerDFAAnnotations));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        succeeded &= PrecedenceCycleChecker.check(logger, symbolTable, scannerDFAAnnotations);

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Checking for precedence dependency cycles",System.currentTimeMillis() - timeBefore));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        LexicalAmbiguities lexicalAmbiguities = LexicalAmbiguitySetBuilder.build(fullSpec,lookaheadSets,parseTable,prefixes,scannerDFAAnnotations);

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Checking for lexical ambiguities",System.currentTimeMillis() - timeBefore));
        logger.flush();

        timeBefore = System.currentTimeMillis();

        succeeded &= LexicalAmbiguityChecker.check(logger, symbolTable, lexicalAmbiguities, stats);

        if(logger.isLoggable(TimingMessage.TIMING_LEVEL)) logger.log(new TimingMessage("Reporting lexical ambiguities",System.currentTimeMillis() - timeBefore));
        logger.flush();

        if(args.getDump() == CopperDumpControl.ON ||
                (args.getDump() == CopperDumpControl.ERROR_ONLY && !succeeded))
        {
            Dumper dumper = null;

            switch(args.getDumpFormat())
            {
                case HTML:
                case XML:
                    try { dumper = new XHTMLParserDumper(symbolTable, fullSpec, contextSets, dfa, lookaheadSets, parseTable, prefixes); }
                    catch(ParserConfigurationException ex) { ex.printStackTrace(); }
                    break;
                case PLAIN:
                    dumper = new PlainTextParserDumper(80, symbolTable, fullSpec, contextSets, dfa, lookaheadSets, parseTable, prefixes);
                    break;
                case XML_SPEC:
                    break;
            }

            dumpIfNecessary(args, succeeded, dumper);
        }
        
        this.succeeded = succeeded;
        this.errorlevel = succeeded ? 0 : 1;
        this.symbolTable = symbolTable;
        this.fullSpec = fullSpec;
        this.packageDecl = packageDecl;
        this.parserName = parserName;
        this.lookaheadSets = lookaheadSets;
        this.parseTable = parseTable;
        this.stats = stats;
        this.prefixes = prefixes;
        this.scannerDFA = scannerDFA;
        this.scannerDFAAnnotations = scannerDFAAnnotations;

        this.hostSpec = hostSpec;
        this.fullDFA = dfa;
        this.mdaResults = mdaResults;

        return true;
    }

    private void dumpIfNecessary(ParserCompilerParameters args, boolean succeeded, Dumper dumper) {
        PrintStream dumpStream = null;
        if(dumper != null) {
            try {
                dumpStream = DumperFactory.getDumpStream(args);
            } catch(FileNotFoundException ex) {
                ex.printStackTrace();
            }

            if(dumpStream != null) {
                try {
                    dumper.dump(args.getDumpFormat(),dumpStream);
                } catch (UnsupportedOperationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Set<String> getCustomSwitches() {
        return null;
    }

    @Override
    public String customSwitchUsage() {
        return null;
    }

    @Override
    public int processCustomSwitch(ParserCompilerParameters args, String[] cmdline, int index) {
        return -1;
    }
}
