package com.ckmu32.utilities;

import java.util.List;
import java.util.Map;

public interface Utilities {
     public default boolean isInvalid(Object object){
        
        if(object == null)
            return true;

        if(object instanceof String s)
            return s.trim().isEmpty();
        else if(object instanceof Integer i)
            return i <= 0;
        else if(object instanceof Long l)
            return l <= 0;
        else if(object instanceof List<?> l)
            return l.isEmpty();
        else if(object instanceof Map<?, ?> m)
            return m.isEmpty();
        
        return true;
    }
}
