package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

import java.util.Set;

/**
 * @author Kevin Viratyosin
 */
public class ParserFragmentsPasser implements SpecCompiler<ParserFragments, ParserFragments> {
    public ParserFragmentsPasser(ParserCompilerParameters args) {
        // intentionally left blank; meant to keep consistency with the use of the StandardPipeline
    }

    @Override
    public ParserFragments compileParser(ParserFragments spec, SpecCompilerParameters args) throws CopperException {
        return spec;
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
