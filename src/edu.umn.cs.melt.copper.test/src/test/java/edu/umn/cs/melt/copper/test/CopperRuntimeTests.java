package edu.umn.cs.melt.copper.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.umn.cs.melt.copper.main.CopperSkinType;
import edu.umn.cs.melt.copper.runtime.RunParser;
import edu.umn.cs.melt.copper.runtime.engines.CopperParser;

/**
 * Copper tests involving the running of a Copper parser.
 * @author August Schwerdfeger
 */
public class CopperRuntimeTests {
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	private JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
	
	private CopperParser<?, ?> compileCopperParser(String parserClassName, CopperSkinType skin, String... specs) {
		CopperParser<?, ?> parser = null;
		
		try {
			File outputFolder = tempDir.newFolder();
			String[] parserClassPath = parserClassName.split("\\.");
			File parserOutputDir = outputFolder;
			for(int i = 0;i < parserClassPath.length - 1;i++) {
				parserOutputDir = new File(parserOutputDir, parserClassPath[i]);
			}
			Files.createDirectories(parserOutputDir.toPath());
			String parserClassSimpleName = parserClassPath[parserClassPath.length - 1];
			File parserSrc = new File(parserOutputDir, parserClassSimpleName + ".java"); 
			CopperCompilerTests.runCopperCompilerAPI(false, skin, parserSrc, specs);
			assertNotNull("No Java compiler available", javac);
			int compilerErrorlevel = javac.run(null, null, null, parserSrc.getAbsolutePath());
			assertEquals("Unexpected error running Java compiler", 0, compilerErrorlevel);
			
			@SuppressWarnings("resource")
			ClassLoader loader = new URLClassLoader(new URL[] { outputFolder.toURI().toURL() }, RunParser.class.getClassLoader());
			Class<?> clazz = loader.loadClass(parserClassName);
			parser = (CopperParser<?,?>) clazz.newInstance();
			assertNotNull(parser);
		} catch(Exception ex) {
			ex.printStackTrace();
			fail("Unexpected " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
		return parser;
	}
	
	@Test
	public void testCompileGrammar3p23XML() {
		CopperParser<?, ?> parser = compileCopperParser("parsers.Grammar3p23XMLParser", CopperSkinType.XML, "Grammar3p23XML.xml");
		try {
			String output = (String) parser.parse("x+x+x+-x");
			assertEquals("Parser output does not match expected","[x]\n[+]\n[x]\n[+]\n[x]\n[+]\n[-][x2]", output);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Unexpected " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
	}

}
