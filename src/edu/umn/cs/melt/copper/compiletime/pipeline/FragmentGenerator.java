package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.compiletime.builders.*;
import edu.umn.cs.melt.copper.compiletime.checkers.*;
import edu.umn.cs.melt.copper.compiletime.dumpers.Dumper;
import edu.umn.cs.melt.copper.compiletime.dumpers.DumperFactory;
import edu.umn.cs.melt.copper.compiletime.dumpers.PlainTextParserDumper;
import edu.umn.cs.melt.copper.compiletime.dumpers.XHTMLParserDumper;
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
import java.util.BitSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kevin Viratyosin
 *
 * Based on StandardSpecCompiler
 */
public class FragmentGenerator extends ParserBeanCompiler<FragmentGeneratorReturnData> {
    @Override
    public FragmentGeneratorReturnData compileParser(ParserBean spec, SpecCompilerParameters args) throws CopperException {
        CompilerLogger logger = AuxiliaryMethods.getOrMakeLogger(args);

        boolean beanCompilationSucceeded = compileParserBean(spec, args);

        if (beanCompilationSucceeded) {
            FragmentGeneratorReturnData fragmentGeneratorReturnData = new FragmentGeneratorReturnData();

            fragmentGeneratorReturnData.stats = this.stats;
            if (args.isRunMDA()) {
                Map<Integer, Integer> hostPartitionMap = this.mdaResults.getHostStateMap();
                BitSet extensionStatePartition = new BitSet();
                extensionStatePartition.or(this.mdaResults.getExtPartition());
                extensionStatePartition.or(this.mdaResults.getNewHostPartition());

                fragmentGeneratorReturnData.extensionFragmentData = ExtensionFragmentDataBuilder.build(
                        this.fullSpec,
                        this.fullDFA,
                        this.parseTable,
                        this.symbolTable,
                        this.hostSpec,
                        hostPartitionMap,
                        extensionStatePartition,
                        this.lookaheadSets,
                        this.prefixes,
                        logger
                );
                fragmentGeneratorReturnData.isExtensionFragmentData = true;
            } else {
                StandardSpecCompilerReturnData rv = new StandardSpecCompilerReturnData();

                rv.succeeded = this.succeeded;
                rv.errorlevel = this.succeeded ? 0 : 1;
                rv.symbolTable = this.symbolTable;
                rv.fullSpec = this.fullSpec;
                rv.packageDecl = this.packageDecl;
                rv.parserName = this.parserName;
                rv.lookaheadSets = this.lookaheadSets;
                rv.parseTable = this.parseTable;
                rv.stats = this.stats;
                rv.prefixes = this.prefixes;
                rv.scannerDFA = this.scannerDFA;
                rv.scannerDFAAnnotations = this.scannerDFAAnnotations;

                fragmentGeneratorReturnData.hostFragmentData = new HostFragmentData(rv, this.fullDFA);
                fragmentGeneratorReturnData.isExtensionFragmentData = false;
            }

            return fragmentGeneratorReturnData;
        } else {
            return null;
        }
    }
}
