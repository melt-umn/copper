package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.compiletime.builders.ExtensionFragmentDataBuilder;
import edu.umn.cs.melt.copper.compiletime.builders.HostFragmentData;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

import java.util.BitSet;
import java.util.Map;

/**
 * @author Kevin Viratyosin
 *
 * Based on StandardSpecCompiler
 */
public class FragmentGenerator extends ParserBeanCompiler<FragmentGeneratorReturnData> {
    @Override
    public FragmentGeneratorReturnData compileParser(ParserBean spec, ParserCompilerParameters args) throws CopperException {
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
