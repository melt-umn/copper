package edu.umn.cs.melt.copper.compiletime.builders;

import edu.umn.cs.melt.copper.compiletime.lrdfa.LRLookaheadAndLayoutSets;
import edu.umn.cs.melt.copper.compiletime.lrdfa.TransparentPrefixes;
import edu.umn.cs.melt.copper.compiletime.parsetable.LRParseTable;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.GeneralizedDFA;
import edu.umn.cs.melt.copper.compiletime.scannerdfa.SingleScannerDFAAnnotations;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.Terminal;
import edu.umn.cs.melt.copper.compiletime.spec.numeric.PSSymbolTable;

import java.io.*;
import java.util.Map;

/**
 * @author Kevin Viratyosin
 */
public class ExtensionFragmentData implements Serializable {
    public LRParseTable appendedExtensionTable; // columns are table offset symbols
    //  No PSSymbolTable since it's generated in ExtensionMappingSpec
    public LRLookaheadAndLayoutSets extensionLookaheadAndLayoutSets;
    public ExtensionMappingSpec extensionMappingSpec;
    public TransparentPrefixes transparentPrefixes; // BitSet of table offset symbols
    public GeneralizedDFA scannerDFA; // DFA for both host and extension symbols
    public SingleScannerDFAAnnotations scannerDFAAnnotations;

    // map from marking terminal extension index to lhs (host index) of bridge production
    public Map<Integer, Integer> markingTerminalLHS;
    // map from marking terminal extension index to state (extension index) to transition to on marking terminal shift
    public Map<Integer, Integer> markingTerminalStates;

    public void serialize(FileOutputStream file) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(this);
            out.close();
            System.err.println("Successfully serialized extension fragment data");
        } catch (IOException e) {
            System.err.println("Failed to serialize extension fragment data");
            e.printStackTrace();
        }
    }

    public static ExtensionFragmentData deserialize(FileInputStream file) {
        try {
            ObjectInputStream in = new ObjectInputStream(file);
            ExtensionFragmentData e = (ExtensionFragmentData) in.readObject();
            in.close();
            System.err.println("Successfully deserialized extension fragment data");
            return e;
        } catch (Exception e) {
            System.err.println("Failed to deserialize extension fragment data");
            e.printStackTrace();
        }
        return null;
    }
}
