package edu.umn.cs.melt.copper.main;

import java.util.ArrayList;
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
    public ArrayList<Pair<String,Object>> files;

    public FlagParser(Map<String,String> map)
    {
        this.flagMap = map;
        this.customFlags = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public boolean IsFlag(String key)
    {
        return flagMap.containsKey(key);
    }

    public boolean Parse(String[] args) {
        int i = 0;
        
        while (i < args.length) {
            // At this point, if we see something that is not a flag, we're at files
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
            if (IsFlag(key)) {
                flagMap.put(key, val);
            } else {
                customFlags.add(Pair.cons(key,val));
            }
        }

        while (i < args.length) {
            files.add(Pair.cons(args[i], (Object) args[i]));
            i++;
        }

        return true;
    }

    public Pair<Boolean,String> Get(String key) 
    {
        String val = flagMap.get(key);
        boolean isSet = val != null;
        return Pair.cons(isSet,val);
    }

}