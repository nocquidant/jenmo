/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jenmo.common.cache;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class TestCaseSoftValueMap {
   /** The logger. */
   protected static final Logger LOGGER = Logger.getLogger(TestCaseSoftValueMap.class);

   private static final int NUM_TEST_ENTRIES = 50;

   @Test
   public void testMemoryReclamation() {
      SoftValueMap<String, Integer> map = new SoftValueMap<String, Integer>();
      Runtime rt = Runtime.getRuntime();

      rt.gc();

      int added = 0;
      int size;
      long freeMem;

      /*
       * Fill the map with entries until some references get cleared. GC should cause references to
       * be cleared well before memory fills up.
       */
      do {
         freeMem = rt.freeMemory();
         String key = "" + added;
         Integer value = new Integer(added++);

         map.put(key, value);

         size = map.size();
      } while (size == added);

      Assert.assertTrue(size < added);

      LOGGER.info("ReferenceValueMap " + (added - size) + " entries out of " + added
            + " cleared when free memory was " + (int) (freeMem / 1024) + "KB");
   }

   @Test
   public void testBasicFunction() {
      SoftValueMap<String, Integer> map = new SoftValueMap<String, Integer>();
      String[] keyArray = new String[NUM_TEST_ENTRIES];
      Integer[] valueArray = new Integer[NUM_TEST_ENTRIES];

      for (int i = 0; i < keyArray.length; i++) {
         keyArray[i] = "" + i;
         valueArray[i] = new Integer(i);

         map.put(keyArray[i], valueArray[i]);
      }

      checkMapContents(map, keyArray, valueArray);

      Map<String, Integer> map2 = map.clone();
      map.clear();

      Assert.assertEquals(0, map.size());
      map.putAll(map2);

      checkMapContents(map, keyArray, valueArray);

      Assert.assertEquals(NUM_TEST_ENTRIES, map.size());
   }

   /**
    * Tests Map.get(), Map.containsKey(), Map.containsValue(), Map.entrySet(), Map.keySet(),
    * Map.values()
    */
   private void checkMapContents(Map<String, Integer> map, String[] keyArray, Integer[] valueArray) {
      Assert.assertEquals(map.size(), keyArray.length);

      for (int i = 0; i < keyArray.length; i++)
         Assert.assertTrue(map.containsKey(keyArray[i]));

      Assert.assertTrue(!map.containsKey("bitbucket"));

      for (int i = 0; i < valueArray.length; i++)
         Assert.assertTrue(map.containsValue(valueArray[i]));

      Assert.assertTrue(!map.containsValue("bitbucket"));

      for (int i = 0; i < keyArray.length; i++)
         Assert.assertEquals(i, ((Integer) map.get(keyArray[i])).intValue());

      Set<Entry<String, Integer>> set = map.entrySet();
      Iterator<Entry<String, Integer>> it = set.iterator();

      while (it.hasNext()) {
         Map.Entry<String, Integer> entry = it.next();
         String s = (String) entry.getKey();
         Integer i = (Integer) entry.getValue();
         Assert.assertTrue(map.containsKey(s));
         Assert.assertTrue(map.containsValue(i));
      }

      Assert.assertTrue(map.keySet().containsAll(Arrays.asList(keyArray)));
      Assert.assertTrue(map.values().containsAll(Arrays.asList(valueArray)));
   }
}
