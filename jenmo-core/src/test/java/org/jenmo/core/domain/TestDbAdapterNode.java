package org.jenmo.core.domain;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jenmo.core.adapter.IReference;
import org.jenmo.core.descriptor.Descriptors;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbAdapterNode extends AbstractTestDbPopu {
   private static EntityManager em;

   @BeforeClass
   public static void setupClass() throws Exception {
      em = initEm();
   }

   @AfterClass
   public static void teardownClass() {
      closeEm(em);
   }

   @Before
   public void setupMethod() {
      cleanUpTables(em);
      txBegin(em);
   }

   @After
   public void teardownMethod() {
      txCommit(em);
   }

   @Test
   public void testList() {
      NodeType type = NodeType.newInstance("type");
      Node root = Node.newRoot(type, "root");
      em.persist(root);

      List<Node> adapter = root.getOutputs(Descriptors.listForNode());
      adapter.add(Node.newRoot(type, "child#1"));
      adapter.add(Node.newRoot(type, "child#2"));
      adapter.add(Node.newRoot(type, "child#3"));
      txCommit(em);

      Assert.assertEquals(adapter.size(), 3);
      Assert.assertEquals(adapter.get(0).getName(), "child#1");
      Assert.assertEquals(adapter.get(1).getName(), "child#2");
      Assert.assertEquals(adapter.get(2).getName(), "child#3");

      adapter.clear();
      Assert.assertEquals(adapter.size(), 0);
   }

   @Test
   public void testMap() {
      NodeType type = NodeType.newInstance("type");
      Node root = Node.newRoot(type, "root");
      em.persist(root);

      Map<String, Node> adapter = root.getOutputs(Descriptors.mapForNode(String.class));
      adapter.put("3", Node.newRoot(type, "child#1"));
      adapter.put("6", Node.newRoot(type, "child#2"));
      adapter.put("6", Node.newRoot(type, "child#3"));
      txCommit(em);

      Assert.assertEquals(adapter.size(), 2);
      Assert.assertEquals(adapter.get("3").getName(), "child#1");
      Assert.assertEquals(adapter.get("6").getName(), "child#3");

      adapter.clear();
      Assert.assertEquals(adapter.size(), 0);
   }

   @Test
   public void testSingle() {
      NodeType type = NodeType.newInstance("type");
      Node root = Node.newRoot(type, "root");
      em.persist(root);

      IReference<Node> adapter = root.getOutputs(Descriptors.singleForNode());
      Assert.assertNull(adapter.get());

      adapter.set(Node.newRoot(type, "child#1"));
      Assert.assertEquals(adapter.get().getName(), "child#1");
      txCommit(em);

      adapter.clear();
      Assert.assertNull(adapter.get());
   }
}
