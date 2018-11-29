package edu.gvsu.cis.cis656.clock;

import java.util.*;

public class VectorClock implements Clock {

    public Map<String,Integer> clock = new TreeMap<>();


    @Override
    public void update(Clock other) {
        VectorClock c = (VectorClock) other;
        for(String key : c.clock.keySet()){
            if(!this.clock.containsKey(key)){
                this.clock.put(key, c.getTime(Integer.parseInt(key)));
            }
            else{
                if(this.clock.get(key) < c.getTime(Integer.parseInt(key))){
                    this.clock.put(key, c.getTime(Integer.parseInt(key)));
                }
            }
        }
    }

    @Override
    public void setClock(Clock other) {
        VectorClock in = (VectorClock) other;
        clock = in.clock;

    }

    @Override
    public void tick(Integer pid) {
        int time = clock.get(Integer.toString(pid));
        clock.put(Integer.toString(pid), ++time);
    }

    @Override
    public boolean happenedBefore(Clock other) {


        for(Map.Entry<String, Integer> e : ((VectorClock) other).clock.entrySet()){
            // First check and see if the other clock has keys that I don't have
            if(!this.clock.containsKey(e.getKey())){
                return true;
            } else {
                // Verify that all times in my clock are less than or equal to the other clock
                if(this.clock.get(e.getKey()) > e.getValue()){
                    return false;
                }
            }
        }
        for(Map.Entry<String, Integer> f : ((VectorClock) other).clock.entrySet()){
            // need to find one in our clock less than the other clock
            if(this.clock.get(f.getKey()) < f.getValue()){
                return true;
            }
        }
        return false;
    }

    public String toString() {
        String out = "{";

        int i = 0;
        Set<Map.Entry<String, Integer>> clockSet = clock.entrySet();
        for (Map.Entry<String, Integer> entry : clockSet) {
            i++;
            out += "\"" + entry.getKey() + "\"" + ":" + entry.getValue();
            if(i < clockSet.size()){
                out += ",";
            }
        }

        out += "}";
        return out;
    }

    @Override
    public void setClockFromString(String clock) {
        Map<String,Integer> clock2 = new TreeMap<>();
        String a = clock.substring(clock.indexOf("{") + 1, clock.indexOf("}"));
        String[] s = a.split(",");
        if(s.length > 0) {
            try {
                for (String str : s) {
                    String[] b = str.split(":");
                    if(b.length > 0 && b[0].contains("\"")) {
                        String key = b[0].substring(b[0].indexOf("\"") + 1, b[0].indexOf("\"", b[0].indexOf("\"") + 1));
                        clock2.put(key, new Integer(b[1]));
                    }
                }
            } catch (NumberFormatException e) {
                return;
            }
        this.clock = clock2;
        }
    }

    @Override
    public int getTime(int p) {
        return clock.getOrDefault(Integer.toString(p), -1);
    }

    @Override
    public void addProcess(int p, int c) {
        clock.put(Integer.toString(p), c);
    }
}
