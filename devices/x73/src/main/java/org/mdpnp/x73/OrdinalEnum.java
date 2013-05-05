package org.mdpnp.x73;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for managing enumerations wherein each element is associated with an ordinal value.
 * 
 * @author jplourde
 *
 */
public class OrdinalEnum {
    public interface ShortType {
        short asShort();
    }
    
    public static final <T extends ShortType> Map<Short, T> buildShort(Class<T> cls) {
        Map<Short, T> map = new HashMap<Short, T>();
        for(T t : cls.getEnumConstants()) {
            if(map.containsKey(t.asShort())) {
                throw new IllegalStateException(""+t.asShort()+" is ordinal for " + t + " and " + map.get(t.asShort()));
            }
            map.put(t.asShort(), t);
        }
        return map;
    }
    
    public interface IntType {
        int asInt();
    }
    
    public static final <T extends IntType> Map<Integer, T> buildInt(Class<T> cls) {
        Map<Integer, T> map = new HashMap<Integer, T>();
        for(T t : cls.getEnumConstants()) {
            if(map.containsKey(t.asInt())) {
                throw new IllegalStateException(""+t.asInt()+" is ordinal for " + t + " and " + map.get(t.asInt()));
            }
            map.put(t.asInt(), t);
        }
        return map;
    }
    
    public interface LongType {
        long asLong();
    }
    
    public static final <T extends LongType> Map<Long, T> buildLong(Class<T> cls) {
        Map<Long, T> map = new HashMap<Long, T>();
        for(T t : cls.getEnumConstants()) {
            if(map.containsKey(t.asLong())) {
                throw new IllegalStateException(""+t.asLong()+" is ordinal for " + t + " and " + map.get(t.asLong()));
            }
            map.put(t.asLong(), t);
        }
        return map;
    }
}
