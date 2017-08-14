package edu.umn.cs.melt.copper.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;

/**
 * FlagParser takes in 
 */
public class FlagParser
{

    private Map<String,String> flagMap;
    public List<Pair<String,String>> customFlags;
    public ArrayList<Pair<String,Object>> inputs;

    public FlagParser(Map<String,String> map)
    {
        this.flagMap = map;
        this.customFlags = new ArrayList<Pair<String,String>>();
        this.inputs = new ArrayList<Pair<String,Object>>();
    }

    public FlagParser(List<String> list) 
    {
        this.flagMap = new HashMap<String,String>();
        for (String flag : list) {
            this.flagMap.put(flag, null);
        }
        this.customFlags = new ArrayList<Pair<String,String>>();
        this.inputs = new ArrayList<Pair<String,Object>>();
    }

    /**
     * isFlag returns whether the input key is part of the expected flags for
     * this parser.
     * @param key the flag
     * @return whether the input is an expected flag for this parser
     */
    public boolean isFlag(String key)
    {
        return flagMap.containsKey(key);
    }

    /**
     * parse processes input strings as flags and inputs. Inputs are expected
     * to follow flags, and flags are all expected to begin with '-'.
     * @param args The arguments to parse
     * @return boolean success of the parse 
     */
    public boolean parse(String[] args) {
        int i = 0;
        
        while (i < args.length) {
            // At this point, if we see something that is not a flag, we're at inputs
            if (args[i].charAt(0) != '-') {
                break;
            }
            String key = args[i];
            String val = "<set>";
            // Flags can either be -flag -flag2 or -flag <val> -flag2 <val>
            // We check if the value after this is another flag, or a value.
            i++;
            if (i < args.length && args[i].charAt(0) != '-') {
                val = args[i+1];
                i++;
            }
            // We have known and custom flags. If we know about this flag 
            // (this parser was initialized with the flag), then we set the
            // known flag, else we set it as a custom flag.
            if (isFlag(key)) {
                flagMap.put(key, val);
            } else {
                customFlags.add(Pair.cons(key,val));
            }
        }

        while (i < args.length) {
            inputs.add(Pair.cons(args[i], (Object) args[i]));
            i++;
        }

        return true;
    }

    /**
     * get returns whether this parser has seen the input in its
     * known flags. 
     * @param key The flag to check
     * @return Pair<IsSet,value>. If the value sent back is null, IsSet will be false
     */
    public Pair<Boolean,String> get(String key) 
    {
        String val = flagMap.get(key);
        return Pair.cons(val != null,val);
    }

}