package edu.umn.cs.melt.copper.main;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.ParserBean;
import edu.umn.cs.melt.copper.compiletime.pipeline.CompilerReturnData;
import edu.umn.cs.melt.copper.compiletime.pipeline.Pipeline;
import edu.umn.cs.melt.copper.compiletime.pipeline.StandardPipeline;
import edu.umn.cs.melt.copper.compiletime.pipeline.StandardSpecCompiler;

/**
 * Represents the parser compilation pipelines available in the Copper parser generator.
 * @see edu.umn.cs.melt.copper.compiletime.pipeline.Pipeline
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 */
public enum CopperPipelineType
{
	/**
	 * This pipeline first converts a parser specification into the class of objects in the
	 * {@link edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans} package,
	 * from which a parser is compiled.
	 */
	GRAMMARBEANS
	{
		@Override
		StandardPipeline<ParserBean,CompilerReturnData> getPipeline(ParserCompilerParameters args)
		{
			return new StandardPipeline<ParserBean, CompilerReturnData>(args.getUseSkin().getStandardSpecParser(args),new StandardSpecCompiler(),args.getUseEngine().getStandardSourceBuilder(args));
		}
	};
	
	
	abstract Pipeline getPipeline(ParserCompilerParameters args);
}
