package org.jenmo.core.adapter;

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

public class TestCaseAdapterToListApply extends AbstractAdapterCase {
   private AdapterToList<Double, NodeProperty> adapter;

   @Before
   public void setup() {
      Property prop = Property.newInstance("property");
      NodeType type = NodeType.newInstance("type", prop);
      Node node = Node.newRoot(type, "node");
      adapter = node.getProperties(prop, Descriptors.listForProps(Double.class));
      fillAdapters(false);
      // Applyable!
      adapter = Adapters.appliable(adapter);
   }

   @After
   public void teardown() {
      adapter.apply();
      Assert.assertTrue(adapter.asImpl().checkConsistency());
   }

   private void fillAdapters(boolean logTime) {
      MyTimer timer = new MyTimer();
      for (int i = 0; i < SIZE; i++) {
         Assert.assertTrue(adapter.add(i + 1.));
      }
      Assert.assertEquals(adapter.size(), SIZE);
      if (logTime) {
         timer.end("add");
      }
   }

   @Test
   public void testAdd() throws Exception {
      TestCaseAdapterToList.doTestAdd(adapter);
   }

   @Test
   public void testAddAt() throws Exception {
      TestCaseAdapterToList.doTestAddAt(adapter);
   }

   @Test
   public void testAddAll() throws Exception {
      TestCaseAdapterToList.doTestAddAll(adapter);
   }

   @Test
   public void testAddAllAt() throws Exception {
      TestCaseAdapterToList.doTestAddAllAt(adapter);
   }

   @Test
   public void testContains() throws Exception {
      TestCaseAdapterToList.doTestContains(adapter);
   }

   @Test
   public void testContainsAll() throws Exception {
      TestCaseAdapterToList.doTestContainsAll(adapter);
   }

   @Test
   public void testIndexOf() throws Exception {
      TestCaseAdapterToList.doTestIndexOf(adapter);
   }

   @Test
   public void testLastIndexOf() throws Exception {
      TestCaseAdapterToList.doTestLastIndexOf(adapter);
   }

   @Test
   public void testIterator() throws Exception {
      TestCaseAdapterToList.doTestIterator(adapter);
   }

   @Test
   public void testListIterator() throws Exception {
      TestCaseAdapterToList.doTestListIterator(adapter);
   }

   @Test
   public void testRemoveAt() throws Exception {
      TestCaseAdapterToList.doTestRemoveAt(adapter);
   }

   @Test
   public void testRemoveForward() throws Exception {
      TestCaseAdapterToList.doTestRemoveForward(adapter);
   }

   @Test
   public void testRemoveBackward() throws Exception {
      TestCaseAdapterToList.doTestRemoveBackward(adapter);
   }

   @Test
   public void testRemoveAll() throws Exception {
      TestCaseAdapterToList.doTestRemoveAll(adapter);
   }

   @Test
   public void testRetainAll() throws Exception {
      TestCaseAdapterToList.doTestRetainAll(adapter);
   }

   @Test
   public void testSet() throws Exception {
      TestCaseAdapterToList.doTestSet(adapter);
   }

   @Test
   public void testToArray() throws Exception {
      TestCaseAdapterToList.doTestToArray(adapter);
   }

   @Test
   public void testSubList() throws Exception {
      TestCaseAdapterToList.doTestSubList(adapter);
   }
}
