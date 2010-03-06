package org.jenmo.core.adapter;

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

public class TestCaseAdapterToSetUnmodif extends AbstractAdapterCase {
   AdapterToSet<Double, NodeProperty> adapter;

   @Before
   public void setup() {
      Property prop = Property.newInstance("property");
      NodeType type = NodeType.newInstance("type", prop);
      Node node = Node.newRoot(type, "node");
      adapter = node.getProperties(prop, Descriptors.setForProps(Double.class));
      fillAdapters(adapter, false);
      // Unmodifiable!
      adapter = Adapters.unmodifiable(adapter);
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

   @Test
   public void testAdd() throws Exception {
      try {
         TestCaseAdapterToSet.doTestAdd(adapter);
         Assert.fail("Expected exception");
      } catch (UnsupportedOperationException e) {
      }
   }

   @Test
   public void testAddAll() throws Exception {
      try {
         TestCaseAdapterToSet.doTestAddAll(adapter);
         Assert.fail("Expected exception");
      } catch (UnsupportedOperationException e) {
      }
   }

   @Test
   public void testContains() throws Exception {
      TestCaseAdapterToSet.doTestContains(adapter);
   }

   @Test
   public void testContainsAll() throws Exception {
      TestCaseAdapterToSet.doTestContainsAll(adapter);
   }

   @Test
   public void testIterator() throws Exception {
      try {
         TestCaseAdapterToSet.doTestIterator(adapter);
         Assert.fail("Expected exception");
      } catch (UnsupportedOperationException e) {
      }
   }

   public void testToArray() throws Exception {
      TestCaseAdapterToSet.doTestToArray(adapter);
   }

   @Test
   public void testRemove() throws Exception {
      try {
         TestCaseAdapterToSet.doTestRemove(adapter);
         Assert.fail("Expected exception");
      } catch (UnsupportedOperationException e) {
      }
   }

   @Test
   public void testRemoveAll() throws Exception {
      try {
         TestCaseAdapterToSet.doTestRemoveAll(adapter);
         Assert.fail("Expected exception");
      } catch (UnsupportedOperationException e) {
      }
   }

   @Test
   public void testRetainAll() throws Exception {
      try {
         TestCaseAdapterToSet.doTestRetainAll(adapter);
         Assert.fail("Expected exception");
      } catch (UnsupportedOperationException e) {
      }
   }
}