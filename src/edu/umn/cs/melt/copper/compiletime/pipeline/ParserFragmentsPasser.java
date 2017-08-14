package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * @author Kevin Viratyosin
 */
public class ParserFragmentsPasser extends ZeroSwitcher implements SpecCompiler<ParserFragments, ParserFragments> {
    public ParserFragmentsPasser(ParserCompilerParameters args) {
        // intentionally left blank; meant to keep consistency with the use of the StandardPipeline
    }

    @Override
    public ParserFragments compileParser(ParserFragments spec, SpecCompilerParameters args) throws CopperException {
        return spec;
    }
}
