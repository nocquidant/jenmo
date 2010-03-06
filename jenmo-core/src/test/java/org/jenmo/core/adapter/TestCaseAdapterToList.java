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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jenmo.core.adapter.AbstractAdapter.AdapterToList;
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

public class TestCaseAdapterToList extends AbstractAdapterCase {
   private AdapterToList<Double, NodeProperty> adapter;

   @Before
   public void setup() {
      Property prop = Property.newInstance("property");
      NodeType type = NodeType.newInstance("type", prop);
      Node node = Node.newRoot(type, "node");
      adapter = node.getProperties(prop, Descriptors.listForProps(Double.class));
      fillAdapters(adapter, false);
   }

   @After
   public void teardown() {
      Assert.assertTrue(adapter.asImpl().checkConsistency());
   }

   private static void fillAdapters(AdapterToList<Double, NodeProperty> adapter, boolean logTime) {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         Assert.assertTrue(adapter.add(i + 1.));
      }
      Assert.assertEquals(SIZE, adapter.size());
      if (logTime) {
         timer.end("add");
      }
   }

   protected static void doTestAdd(AdapterToList<Double, NodeProperty> adapter) throws Exception {
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

   protected static void doTestAddAt(AdapterToList<Double, NodeProperty> adapter) throws Exception {
      adapter.clear();

      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         adapter.add(i, i + 1.);
      }
      timer.end("addAt");
      checkSum(SIZE, getExpectedSum(), adapter.iterator());
   }

   @Test
   public void testAddAt() throws Exception {
      doTestAddAt(adapter);
   }

   protected static void doTestAddAll(AdapterToList<Double, NodeProperty> adapter) throws Exception {
      adapter.clear();

      MyTimer timer = new MyTimer();
      Assert.assertTrue(adapter.addAll(getCollForAll()));
      timer.end("addAll");

      checkSum(SIZE, getExpectedSum(), adapter.iterator());
   }

   @Test
   public void testAddAll() throws Exception {
      doTestAddAll(adapter);
   }

   protected static void doTestAddAllAt(AdapterToList<Double, NodeProperty> adapter)
         throws Exception {
      adapter.clear();

      MyTimer timer = new MyTimer();
      Assert.assertTrue(adapter.addAll(0, getCollForAll()));
      timer.end("addAllAt");

      checkSum(SIZE, getExpectedSum(), adapter.iterator());
   }

   @Test
   public void testAddAllAt() throws Exception {
      doTestAddAllAt(adapter);
   }

   protected static void doTestContains(AdapterToList<Double, NodeProperty> adapter)
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

   protected static void doTestContainsAll(AdapterToList<Double, NodeProperty> adapter)
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

   protected static void doTestIndexOf(AdapterToList<Double, NodeProperty> adapter)
         throws Exception {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         Assert.assertEquals(adapter.indexOf(new Double(i + 1.)), i);
      }
      Assert.assertFalse(adapter.contains(new Double(SIZE + 1.)));
      timer.end("indexOf");
   }

   @Test
   public void testIndexOf() throws Exception {
      doTestIndexOf(adapter);
   }

   protected static void doTestLastIndexOf(AdapterToList<Double, NodeProperty> adapter)
         throws Exception {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         Assert.assertEquals(adapter.lastIndexOf(new Double(i + 1.)), i);
      }
      Assert.assertFalse(adapter.contains(new Double(SIZE + 1.)));
      timer.end("lastIndexOf");
   }

   @Test
   public void testLastIndexOf() throws Exception {
      doTestLastIndexOf(adapter);
   }

   protected static void doTestIterator(AdapterToList<Double, NodeProperty> adapter)
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

   protected static void doTestListIterator(AdapterToList<Double, NodeProperty> adapter)
         throws Exception {
      ListIterator<Double> it = adapter.listIterator();

      // Next
      int count = 0;
      double sum = 0;
      double sum2 = 0;

      while (it.hasNext()) {
         Double each = (Double) it.next();
         count++;
         sum += each;
         sum2 += it.nextIndex();
      }

      Assert.assertEquals(count, SIZE);
      Assert.assertEquals(getExpectedSum(), sum, 10e-9);
      Assert.assertEquals(getExpectedSum(), sum2, 10e-9);

      // Previous
      count = 0;
      sum = 0;
      sum2 = 0;

      while (it.hasPrevious()) {
         sum2 += SIZE - it.previousIndex();
         System.out.println(sum2);
         Double each = (Double) it.previous();
         count++;
         sum += each;
      }

      Assert.assertEquals(SIZE, count);
      Assert.assertEquals(getExpectedSum(), sum, 10e-9);
      Assert.assertEquals(getExpectedSum(), sum2, 10e-9);

      // Add
      adapter.clear();
      it = adapter.listIterator();
      for (int i = 0; i < SIZE; i++) {
         it.add(new Double(i + 1.));
      }
      checkSum(SIZE, sum, adapter.iterator());

      // Remove
      it = adapter.listIterator();
      Double v = it.next();
      it.remove();
      Assert.assertEquals(adapter.size(), SIZE - 1);
      checkSum(SIZE - 1, getExpectedSum() - v, adapter.iterator());

      // Set
      it = adapter.listIterator();
      it.next();
      it.set(0.);
      Assert.assertEquals(adapter.size(), SIZE - 1);
      checkSum(SIZE - 1, getExpectedSum() - 3, adapter.iterator());
   }

   @Test
   public void testListIterator() throws Exception {
      doTestListIterator(adapter);
   }

   protected static void doTestRemoveAt(AdapterToList<Double, NodeProperty> adapter)
         throws Exception {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         Assert.assertEquals(adapter.remove(0), new Double(i + 1.), 10e-9);
      }
      Assert.assertTrue(adapter.isEmpty());
      Assert.assertEquals(0, adapter.size());
      timer.end("removeAt");
   }

   @Test
   public void testRemoveAt() throws Exception {
      doTestRemoveAt(adapter);
   }

   protected static void doTestRemoveForward(AdapterToList<Double, NodeProperty> adapter)
         throws Exception {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         Assert.assertTrue(adapter.remove(new Double(i + 1.)));
      }
      Assert.assertTrue(adapter.isEmpty());
      Assert.assertEquals(adapter.size(), 0);
      timer.end("removeForward");
   }

   @Test
   public void testRemoveForward() throws Exception {
      doTestRemoveForward(adapter);
   }

   protected static void doTestRemoveBackward(AdapterToList<Double, NodeProperty> adapter)
         throws Exception {
      MyTimer timer = new MyTimer();
      for (int i = SIZE - 1; i >= 0; i--) {
         Assert.assertTrue(adapter.remove(new Double(i + 1.)));
      }
      Assert.assertTrue(adapter.isEmpty());
      Assert.assertEquals(adapter.size(), 0);
      timer.end("removeBackward");
   }

   @Test
   public void testRemoveBackward() throws Exception {
      doTestRemoveBackward(adapter);
   }

   protected static void doTestRemoveAll(AdapterToList<Double, NodeProperty> adapter)
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

   protected static void doTestRetainAll(AdapterToList<Double, NodeProperty> adapter)
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

   protected static void doTestSet(AdapterToList<Double, NodeProperty> adapter) throws Exception {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         adapter.set(i, 0.);
      }
      timer.end("set");
      checkSum(SIZE, 0, adapter.iterator());
   }

   @Test
   public void testSet() throws Exception {
      doTestSet(adapter);
   }

   protected static void doTestToArray(AdapterToList<Double, NodeProperty> adapter)
         throws Exception {
      MyTimer timer = new MyTimer();
      Double[] array = adapter.toArray(new Double[SIZE]);
      Assert.assertEquals(array.length, SIZE);
      Assert.assertEquals(1., array[0], 10e-9);
      timer.end("toArray");
   }

   @Test
   public void testToArray() throws Exception {
      doTestToArray(adapter);
   }

   protected static void doTestSubList(AdapterToList<Double, NodeProperty> adapter)
         throws Exception {
      MyTimer timer = new MyTimer();
      adapter.subList(0, adapter.size());
      timer.end("subList");
      checkSum(SIZE, getExpectedSum(), adapter.iterator());
   }

   @Test
   public void testSubList() throws Exception {
      doTestSubList(adapter);
   }

   public static void main(String[] args) {
      TestCaseAdapterToList test = new TestCaseAdapterToList();
      int warmup = 10;
      try {
         for (int i = 0; i < warmup; i++) {
            test.setup();
            test.testContains();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
