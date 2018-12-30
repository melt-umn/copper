package edu.umn.cs.melt.copper.test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Test;

import edu.umn.cs.melt.copper.main.CopperIOType;
import edu.umn.cs.melt.copper.main.CopperSkinType;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

/**
 * Copper tests involving the compilation of a Copper parser.
 * @author August Schwerdfeger
 */
public class CopperCompilerTests {
	
	public static void runCopperCompilerAPI(boolean shouldFail, CopperSkinType skin, File outputFile, String... specs) {
		ParserCompilerParameters args = new ParserCompilerParameters();
		args.setUseSkin(skin);
		if(outputFile == null) {
			args.setOutputType(CopperIOType.STREAM);
			args.setOutputStream(new PrintStream(new ByteArrayOutputStream()));
		} else {
			args.setOutputType(CopperIOType.FILE);
			args.setOutputFile(outputFile);
		}
		ArrayList<Pair<String,Object>> inputs = new ArrayList<Pair<String,Object>>();
		for(String spec : specs) {
			InputStream str = CopperCompilerTests.class.getClassLoader().getResourceAsStream("grammars/" + spec);
			assertNotNull(str);
			InputStreamReader strR = new InputStreamReader(str);
			inputs.add(Pair.cons(spec, (Object) strR));
		}
		args.setInputs(inputs);
		try {
			int result = ParserCompiler.compile(args);
			if(shouldFail) {
				assertNotEquals(0, result);
			} else {
				assertEquals(0, result);
			}
		} catch(CopperException ex) {
			fail(ex.getMessage());
		} catch(IOException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testWrongSkin1() {
		runCopperCompilerAPI(true, CopperSkinType.XML, null, "BlazonGrammar.x");
	}
	
	@Test
	public void testWrongSkin2() {
		runCopperCompilerAPI(true, CopperSkinType.CUP, null, "Grammar3p23XML.xml");
	}
	
	@Test
	public void testBlazonGrammar() {
		runCopperCompilerAPI(false, CopperSkinType.CUP, null, "BlazonGrammar.x");
	}

	@Test
	public void testCLangGrammar() {
		runCopperCompilerAPI(false, CopperSkinType.XML, null, "CLangGrammar.xml");
	}

	@Test
	public void testGrammar3p23() {
		runCopperCompilerAPI(false, CopperSkinType.CUP, null, "Grammar3p23.x");
	}

	@Test
	public void testGrammar3p23XML() {
		runCopperCompilerAPI(false, CopperSkinType.XML, null, "Grammar3p23XML.xml");
	}

	@Test
	public void testGrammar3p26() {
		runCopperCompilerAPI(false, CopperSkinType.CUP, null, "Grammar3p26.x");
	}

	@Test
	public void testJava14AllTheFruit() {
		runCopperCompilerAPI(false, CopperSkinType.XML, null, "Java14AllTheFruit.xml");
	}

	@Test
	public void testJava14GrammarHost() {
		runCopperCompilerAPI(false, CopperSkinType.XML, null, "Java14GrammarHost.xml");
	}

	@Test
	public void testMathGrammar() {
		runCopperCompilerAPI(false, CopperSkinType.CUP, null, "MathGrammar.x");
	}

	@Test
	public void testMathGrammarXML() {
		runCopperCompilerAPI(false, CopperSkinType.XML, null, "MathGrammar.xml");
	}

	@Test
	public void testMiniJavaGrammar() {
		runCopperCompilerAPI(false, CopperSkinType.CUP, null, "MiniJavaGrammar.x");
	}

	@Test
	public void testSilverGrammar() {
		runCopperCompilerAPI(false, CopperSkinType.XML, null, "SilverGrammar.xml");
	}

	@Test
	public void testDisambiguationFunctionsValid() {
		runCopperCompilerAPI(false, CopperSkinType.XML, null, "DisambiguateSubset.xml");
	}

	@Test
	public void testDisambiguationFunctionsInvalid() {
		runCopperCompilerAPI(true, CopperSkinType.XML, null, "DisambiguateErrors.xml");
	}

	@Test
	public void testSchemaVersion0p7() {
		runCopperCompilerAPI(false, CopperSkinType.XML, null, "SchemaVersions/Namespace0.7_valid.xml");
	}

	@Test
	public void testSchemaVersion0p8Valid() {
		runCopperCompilerAPI(false, CopperSkinType.XML, null, "SchemaVersions/Namespace0.8_valid.xml");
	}

	@Test
	public void testSchemaVersion0p8Invalid() {
		runCopperCompilerAPI(true, CopperSkinType.XML, null, "SchemaVersions/Namespace0.8_invalid.xml");
	}
}
