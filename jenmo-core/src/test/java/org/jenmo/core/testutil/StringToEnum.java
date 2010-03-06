package org.jenmo.core.testutil;

import java.util.HashMap;
import java.util.Map;

public class StringToEnum<T extends Enum<T>> {
   private final Map<String, T> map = new HashMap<String, T>();

   public StringToEnum(T... values) {
      for (T each : values) {
         map.put(each.toString(), each);
      }
   }

   public T fromString(String str) {
      return map.get(str);
   }
}