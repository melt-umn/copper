package edu.umn.cs.melt.copper.compiletime.pipeline;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.GrammarStatistics;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.ParserSpec;

import java.io.*;

/**
 * This is a container class for all the data returned by {@link StandardSpecCompiler}.
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 * @author Kevin Viratyosin
 *
 * Modified by Kevin to allow serialization
 */
public class StandardSpecCompilerReturnData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 36758451293844968L;
	public boolean succeeded;
	public int errorlevel;
	public PSSymbolTable symbolTable;
	public ParserSpec fullSpec;
	public String packageDecl;
	public String parserName;
	public LRLookaheadAndLayoutSets lookaheadSets;
	public LRParseTable parseTable;
	public GrammarStatistics stats;
	public TransparentPrefixes prefixes;
	public GeneralizedDFA scannerDFA;
	public SingleScannerDFAAnnotations scannerDFAAnnotations;

	public void serialize(FileOutputStream file) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(this);
			out.close();
			System.err.println("Successfully serialized spec compiler return data");
		} catch (IOException e) {
			System.err.println("Failed to serialize spec compiler return data");
			e.printStackTrace();
		}
	}

	public static StandardSpecCompilerReturnData deserialize(FileInputStream file) {
		try {
			ObjectInputStream in = new ObjectInputStream(file);
			StandardSpecCompilerReturnData e = (StandardSpecCompilerReturnData) in.readObject();
			in.close();
			System.err.println("Successfully deserialized spec compiler return data");
			return e;
		} catch (Exception e) {
			System.err.println("Failed to deserialize spec compiler return data");
			e.printStackTrace();
		}
		return null;
	}
}
