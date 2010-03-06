package org.jenmo.core.adapter;

import java.util.List;
import java.util.Map;

import org.jenmo.core.descriptor.Descriptors;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestCaseAdapterNode extends AbstractAdapterCase {
   private NodeType type;
   private Node root;

   @Before
   public void setup() {
      type = NodeType.newInstance("type");
      root = Node.newRoot(type, "root");
   }

   @After
   public void teardown() {
      type = null;
      root = null;
   }

   @Test
   public void testList() {
      List<Node> adapter = root.getOutputs(Descriptors.listForNode());
      adapter.add(Node.newRoot(type, "child#1"));
      adapter.add(Node.newRoot(type, "child#2"));
      adapter.add(Node.newRoot(type, "child#3"));

      Assert.assertEquals(adapter.size(), 3);
      Assert.assertEquals(adapter.get(0).getName(), "child#1");
      Assert.assertEquals(adapter.get(1).getName(), "child#2");
      Assert.assertEquals(adapter.get(2).getName(), "child#3");

      adapter.clear();
      Assert.assertEquals(adapter.size(), 0);
   }

   @Test
   public void testMap() {
      Map<String, Node> adapter = root.getOutputs(Descriptors.mapForNode(String.class));
      adapter.put("3", Node.newRoot(type, "child#1"));
      adapter.put("6", Node.newRoot(type, "child#2"));
      adapter.put("6", Node.newRoot(type, "child#3"));

      Assert.assertEquals(adapter.size(), 2);
      Assert.assertEquals(adapter.get("3").getName(), "child#1");
      Assert.assertEquals(adapter.get("6").getName(), "child#3");

      adapter.clear();
      Assert.assertEquals(adapter.size(), 0);
   }

   @Test
   public void testSingle() {
      IReference<Node> adapter = root.getOutputs(Descriptors.singleForNode());
      Assert.assertNull(adapter.get());

      adapter.set(Node.newRoot(type, "child#1"));
      Assert.assertEquals(adapter.get().getName(), "child#1");

      adapter.clear();
      Assert.assertNull(adapter.get());
   }
}
