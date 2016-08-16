package edu.umn.cs.melt.copper.compiletime.srcbuilders.fragment;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.FinalReportMessage;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.compiletime.pipeline.AuxiliaryMethods;
import edu.umn.cs.melt.copper.compiletime.pipeline.FragmentGeneratorReturnData;
import edu.umn.cs.melt.copper.compiletime.pipeline.SourceBuilder;
import edu.umn.cs.melt.copper.compiletime.pipeline.SourceBuilderParameters;
import edu.umn.cs.melt.copper.main.CopperIOType;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * @author Kevin Viratyosin
 */
public class FragmentSerializationProcess implements SourceBuilder<FragmentGeneratorReturnData> {
    @Override
    public int buildSource(FragmentGeneratorReturnData constructs, SourceBuilderParameters args) throws CopperException {
        CompilerLogger logger = AuxiliaryMethods.getOrMakeLogger(args);

        try {
            OutputStream stream = null;
            if (args.getOutputType() == CopperIOType.FILE) {
                stream = new FileOutputStream(args.getOutputFile());
            } else if (args.getOutputType() == CopperIOType.STREAM) {
                stream = args.getOutputStream();
            } else {
                return 0;
            }

            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(constructs.isExtensionFragmentData ? constructs.extensionFragmentData : constructs.hostFragmentData);
            out.close();

            logger.log(new FinalReportMessage(constructs.stats));

            String fragmentKind = constructs.isExtensionFragmentData ? "extension" : "host";
            logger.log(new GenericMessage(CompilerLevel.REGULAR, "Successfully serialized " + fragmentKind + " fragment data"));

            return 0;
        } catch (IOException e) {
            if (logger.isLoggable(CompilerLevel.VERY_VERBOSE)) {
                e.printStackTrace(System.err);
            }
            logger.logError(new GenericMessage(CompilerLevel.QUIET, "I/O error in fragment serialization: " + e.getMessage(),true,true));
            return 1;
        }
    }

    @Override
    public Set<String> getCustomSwitches() {
        return null;
    }

    @Override
    public String customSwitchUsage() {
        return "";
    }

    @Override
    public int processCustomSwitch(ParserCompilerParameters args, String[] cmdline, int index) {
        return -1;
    }
}
