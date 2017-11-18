package edu.umn.cs.melt.copper.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.Test;

import edu.umn.cs.melt.copper.main.CopperSkinType;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;

public class CopperMDATests {
	private void runCopperMDAAPI(boolean shouldFail, String... specs) {
		ParserCompilerParameters args = new ParserCompilerParameters();
		args.setUseSkin(CopperSkinType.XML);
		args.setRunMDA(true);
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
	public void runMDAPassing() {
		runCopperMDAAPI(false, "mda/MDAPassing.xml");
	}

	@Test
	public void runMDAFailing() {
		runCopperMDAAPI(true, "mda/MDAFailing.xml");
	}

	@Test
	public void runMDALookaheadSpillageOnly() {
		runCopperMDAAPI(true, "mda/MDALookaheadSpillageOnly.xml");
	}

	@Test
	public void runMDAFollowSpillageOnly() {
		runCopperMDAAPI(true, "mda/MDAFollowSpillageOnly.xml");
	}

	@Test
	public void runMDANonILSubsetOnly() {
		runCopperMDAAPI(true, "mda/MDANonILSubsetOnly.xml");
	}
}
