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
import java.util.Iterator;
import java.util.List;

import org.jenmo.core.adapter.AbstractAdapter.AdapterToSet;
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

public class TestCaseAdapterToSet extends AbstractAdapterCase {
   AdapterToSet<Double, NodeProperty> adapter;

   @Before
   public void setup() {
      Property prop = Property.newInstance("property");
      NodeType type = NodeType.newInstance("type", prop);
      Node node = Node.newRoot(type, "node");
      adapter = node.getProperties(prop, Descriptors.setForProps(Double.class));
      fillAdapters(adapter, false);
   }

   @After
   public void teardown() {
      Assert.assertTrue(adapter.asImpl().checkConsistency());
   }

   private static void fillAdapters(AdapterToSet<Double, NodeProperty> adapter, boolean logTime) {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         adapter.add(new Double(i + 1.));
      }
      Assert.assertEquals(adapter.size(), SIZE);
      if (logTime) {
         timer.end("add");
      }
   }

   protected static void doTestAdd(AdapterToSet<Double, NodeProperty> adapter) throws Exception {
      adapter.clear();
      Assert.assertTrue(adapter.isEmpty());
      Assert.assertEquals(adapter.size(), 0);

      fillAdapters(adapter, true);
      checkSum(SIZE, getExpectedSum(), adapter.iterator());
   }

   @Test
   public void testAdd() throws Exception {
      doTestAdd(adapter);
   }

   protected static void doTestAddAll(AdapterToSet<Double, NodeProperty> adapter) throws Exception {
      adapter.clear();

      MyTimer timer = new MyTimer();
      adapter.addAll(getCollForAll());
      timer.end("addAll");

      checkSum(SIZE, getExpectedSum(), adapter.iterator());
   }

   @Test
   public void testAddAll() throws Exception {
      doTestAddAll(adapter);
   }

   protected static void doTestContains(AdapterToSet<Double, NodeProperty> adapter)
         throws Exception {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         Assert.assertTrue(adapter.contains(new Double(i + 1.)));
      }
      Assert.assertFalse(adapter.contains(new Double(SIZE + 1.)));
      timer.end("contains");
   }

   @Test
   public void testContains() throws Exception {
      doTestContains(adapter);
   }

   protected static void doTestContainsAll(AdapterToSet<Double, NodeProperty> adapter)
         throws Exception {
      MyTimer timer = new MyTimer();
      Assert.assertTrue(adapter.containsAll(getCollForAll()));
      Assert.assertFalse(adapter.contains(new Double(SIZE + 1.)));
      timer.end("containsAll");
   }

   @Test
   public void testContainsAll() throws Exception {
      doTestContainsAll(adapter);
   }

   protected static void doTestIterator(AdapterToSet<Double, NodeProperty> adapter)
         throws Exception {
      MyTimer timer = new MyTimer();
      Iterator<Double> it = adapter.iterator();
      Double v = it.next();
      it.remove();
      Assert.assertEquals(adapter.size(), SIZE - 1);
      timer.end("iterator");

      checkSum(SIZE - 1, getExpectedSum() - v, adapter.iterator());
   }

   @Test
   public void testIterator() throws Exception {
      doTestIterator(adapter);
   }

   protected static void doTestToArray(AdapterToSet<Double, NodeProperty> adapter) throws Exception {
      MyTimer timer = new MyTimer();
      Double[] array = adapter.toArray(new Double[SIZE]);
      Assert.assertEquals(array.length, SIZE);
      timer.end("toArray");

      int count = 0;
      double sum = 0;
      for (Double each : array) {
         count++;
         sum += each;
      }
      Assert.assertEquals(count, SIZE);
      Assert.assertEquals(getExpectedSum(), sum, 10e-9);

      Arrays.sort(array);
      Assert.assertEquals(array[0], 1., 10e-9);
   }

   @Test
   public void testToArray() throws Exception {
      doTestToArray(adapter);
   }

   protected static void doTestRemove(AdapterToSet<Double, NodeProperty> adapter) throws Exception {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         Assert.assertTrue(adapter.remove(new Double(i + 1.)));
      }
      Assert.assertTrue(adapter.isEmpty());
      Assert.assertEquals(adapter.size(), 0);
      timer.end("remove");
   }

   @Test
   public void testRemove() throws Exception {
      doTestRemove(adapter);
   }

   protected static void doTestRemoveAll(AdapterToSet<Double, NodeProperty> adapter)
         throws Exception {
      MyTimer timer = new MyTimer();
      Assert.assertTrue(adapter.removeAll(getCollForAll()));
      Assert.assertTrue(adapter.isEmpty());
      Assert.assertEquals(adapter.size(), 0);
      timer.end("removeAll");
   }

   @Test
   public void testRemoveAll() throws Exception {
      doTestRemoveAll(adapter);
   }

   protected static void doTestRetainAll(AdapterToSet<Double, NodeProperty> adapter)
         throws Exception {
      List<Double> buff = getCollForAll();
      List<Double> buff2 = new ArrayList<Double>();
      buff2.add(buff.get(0));
      buff2.add(buff.get(buff.size() - 1));

      MyTimer timer = new MyTimer();
      Assert.assertTrue(adapter.retainAll(buff2));
      timer.end("retainAll");

      checkSum(2, buff2.get(0) + buff2.get(1), adapter.iterator());
   }

   @Test
   public void testRetainAll() throws Exception {
      doTestRetainAll(adapter);
   }
}
