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
package org.jenmo.core.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jenmo.core.adapter.AbstractAdapter.AdapterToMap;
import org.jenmo.core.descriptor.Descriptors;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeProperty;
import org.jenmo.core.domain.NodeType;
import org.jenmo.core.domain.Property;
import org.jenmo.core.testutil.MyTimer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestCaseAdapterToMap extends AbstractAdapterCase {
   private AdapterToMap<String, Double, NodeProperty> adapter;

   @Before
   public void setup() {
      Property prop = Property.newInstance("property");
      NodeType type = NodeType.newInstance("type", prop);
      Node node = Node.newRoot(type, "node");
      adapter = node.getProperties(prop, Descriptors.mapForProps(String.class, Double.class));
      fillAdapters(false);
   }

   @After
   public void teardown() {
      Assert.assertTrue(adapter.asImpl().checkConsistency());
   }

   private void fillAdapters(boolean logTime) {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         adapter.put(new Double(i + 1.).toString(), new Double(i + 1.));
      }
      Assert.assertEquals(adapter.size(), SIZE);
      if (logTime) {
         timer.end("put");
      }
      displayEntries();
   }

   private void displayEntries() {
      Iterator<Map.Entry<String, Double>> itr = adapter.entrySet().iterator();
      while (itr.hasNext()) {
         Map.Entry<String, Double> each = itr.next();
         System.out.println(each.getKey() + " : " + each.getValue());
      }
   }

   @Test
   public void testPut() {
      adapter.clear();
      Assert.assertTrue(adapter.isEmpty());
      Assert.assertEquals(adapter.size(), 0);

      fillAdapters(true);
      checkSum(SIZE, getExpectedSum(), adapter.values().iterator());
   }

   @Test
   public void testPutAll() {
      adapter.clear();
      Map<String, Double> buff = new HashMap<String, Double>();
      for (int i = 0; i < SIZE; i++) {
         buff.put(new Double(i + 1.).toString(), new Double(i + 1.));
      }

      MyTimer timer = new MyTimer();
      adapter.putAll(buff);
      timer.end("put");

      checkSum(SIZE, getExpectedSum(), adapter.values().iterator());
   }

   @Test
   public void testContains() {
      MyTimer timer = new MyTimer();
      int s = SIZE;
      for (int i = 0; i < s; i++) {
         Assert.assertTrue(adapter.containsKey(new Double(i + 1.).toString()));
         Assert.assertTrue(adapter.containsValue(new Double(i + 1.)));
      }
      Assert.assertFalse(adapter.containsKey(new Double(SIZE + 1).toString()));
      Assert.assertFalse(adapter.containsValue(new Double(SIZE + 1.)));
      timer.end("contains");
   }

   @Test
   public void testEntrySet() {
      MyTimer timer = new MyTimer();
      Iterator<Map.Entry<String, Double>> it = adapter.entrySet().iterator();
      Map.Entry<String, Double> v = it.next();
      it.remove();
      Assert.assertEquals(adapter.size(), SIZE - 1);
      timer.end("entrySet");

      it = adapter.entrySet().iterator();
      List<Double> buff1 = new ArrayList<Double>(adapter.size());
      List<Double> buff2 = new ArrayList<Double>(adapter.size());
      while (it.hasNext()) {
         Map.Entry<String, Double> each = it.next();
         buff1.add(Double.parseDouble(each.getKey()));
         buff2.add(each.getValue());
      }
      checkSum(SIZE - 1, getExpectedSum() - v.getValue(), buff1.iterator());
      checkSum(SIZE - 1, getExpectedSum() - v.getValue(), buff2.iterator());
   }

   @Test
   public void testKeySet() {
      MyTimer timer = new MyTimer();
      Iterator<String> it = adapter.keySet().iterator();
      String v = it.next();
      it.remove();
      Assert.assertEquals(adapter.size(), SIZE - 1);
      timer.end("keySet");

      it = adapter.keySet().iterator();
      List<Double> buff = new ArrayList<Double>(adapter.size());
      while (it.hasNext()) {
         buff.add(Double.parseDouble(it.next()));
      }
      checkSum(SIZE - 1, getExpectedSum() - Double.parseDouble(v), buff.iterator());
   }

   @Test
   public void testValues() {
      MyTimer timer = new MyTimer();
      Iterator<Double> it = adapter.values().iterator();
      Double v = it.next();
      it.remove();
      Assert.assertEquals(adapter.size(), SIZE - 1);
      timer.end("values");

      checkSum(SIZE - 1, getExpectedSum() - v, adapter.values().iterator());
   }

   @Test
   public void testToArray() {
      MyTimer timer = new MyTimer();
      Double[] array = adapter.values().toArray(new Double[SIZE]);
      Assert.assertEquals(array.length, SIZE);
      timer.end("toArray");

      int count = 0;
      double sum = 0;
      for (Double each : array) {
         count++;
         sum += each;
      }
      Assert.assertEquals(count, SIZE);
      Assert.assertEquals(sum, getExpectedSum(), 10e-9);

      Arrays.sort(array);
      Assert.assertEquals(array[0], 1., 10e-9);
   }

   @Test
   public void testRemove() {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         Assert.assertEquals(adapter.remove(new Double(i + 1.).toString()), i + 1., 10e-9);
      }
      Assert.assertTrue(adapter.isEmpty());
      Assert.assertEquals(adapter.size(), 0);
      timer.end("removeForward");
   }
}
