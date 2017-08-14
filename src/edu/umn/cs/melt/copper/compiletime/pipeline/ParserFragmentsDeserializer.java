package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.compiletime.builders.ExtensionFragmentData;
import edu.umn.cs.melt.copper.compiletime.builders.HostFragmentData;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLevel;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.messages.GenericMessage;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * @author Kevin Viratyosin
 */
public class ParserFragmentsDeserializer extends ZeroSwitcher implements SpecParser<ParserFragments> {
    public ParserFragmentsDeserializer(ParserCompilerParameters args) {
    }

    @Override
    public ParserFragments parseSpec(SpecParserParameters args) throws IOException, CopperException {
        CompilerLogger logger = AuxiliaryMethods.getOrMakeLogger(args);
        ArrayList<Pair<String, Object>> inputs = args.getInputs();

        ParserFragments fragments = new ParserFragments();

        if (inputs.size() == 0) {
            logger.logError(new GenericMessage(CompilerLevel.QUIET, "No input fragments given", true, true));
            return null;
        }

        String hostFilename = inputs.get(0).first();
        try {
            fragments.hostFragment = (HostFragmentData) deserialize(hostFilename, logger);
        } catch (ClassCastException e) {
            if (logger.isLoggable(CompilerLevel.VERY_VERBOSE)) {
                e.printStackTrace(System.err);
            }
            logger.logError(new GenericMessage(CompilerLevel.QUIET, "The first input file must be a host fragment. File \"" + hostFilename + "\" is not a host fragment: " + e.getMessage(), true, true));
            return null;
        }

        for (int i = 1; i < inputs.size(); i++) {
            String filename = inputs.get(i).first();
            try {
                fragments.extensionFragments.add((ExtensionFragmentData) deserialize(filename, logger));
            } catch (ClassCastException e) {
                if (logger.isLoggable(CompilerLevel.VERY_VERBOSE)) {
                    e.printStackTrace(System.err);
                }
                logger.logError(new GenericMessage(CompilerLevel.QUIET, "The first input file must be an extension fragment. File \"" + filename + "\" is not a extension fragment: " + e.getMessage(), true, true));
                return null;
            }
        }

        return fragments;
    }

    private Object deserialize(String filename, CompilerLogger logger) throws CopperException {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            Object object = in.readObject();
            in.close();
            logger.log(new GenericMessage(CompilerLevel.REGULAR, "Successfully deserialized fragment in \"" + filename + "\""));
            return object;
        } catch (IOException e) {
            if (logger.isLoggable(CompilerLevel.VERY_VERBOSE)) {
                e.printStackTrace(System.err);
            }
            logger.logError(new GenericMessage(CompilerLevel.QUIET, "I/O error in deserialization fragment in \"" + filename + "\": " + e.getMessage(), true, true));
            return null;
        } catch (Exception e) {
            if (logger.isLoggable(CompilerLevel.VERY_VERBOSE)) {
                e.printStackTrace(System.err);
            }
            logger.logError(new GenericMessage(CompilerLevel.QUIET, "Error in deserialization fragment in \"" + filename + "\": " + e.getMessage(), true, true));
            return null;
        }
    }
}
