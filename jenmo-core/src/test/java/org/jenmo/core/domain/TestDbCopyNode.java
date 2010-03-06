package org.jenmo.core.domain;

import java.util.List;

import javax.persistence.EntityManager;

import org.jenmo.core.repository.DefaultDaoJPA;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbCopyNode extends AbstractTestDbPopu {
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
   public void testCopyNode1() throws Exception {
      // Set Up entities
      NodeType type = NodeType.newInstance("mytype");

      Node root = Node.newRoot(type, "root");
      Node gen1c1 = Node.newOutput(root, type, "gen1c1");
      Node gen1c2 = Node.newOutput(root, type, "gen1c2");
      Node gen2c1 = Node.newOutput(gen1c1, type, "gen2c1");
      gen1c2.getOutputs().add(gen2c1); // loop

      em.persist(root);
      em.getTransaction().commit();
      em.getTransaction().begin();

      // Copy root
      Node rootCp = Node.copy(root, null);
      rootCp.setName(root.getName() + "-cloned");
      em.persist(rootCp);

      em.getTransaction().commit();
      em.getTransaction().begin();

      // Check entities
      DefaultDaoJPA<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      List<Node> roots = nodeDao.find("isRoot", true);
      Assert.assertEquals(roots.size(), 2);

      for (Node each : roots) {
         Assert.assertEquals(each.getInputCount(), 0);
         Assert.assertEquals(each.getOutputCount(), 2);
         for (Node gen1c : each.getOutputs()) {
            Assert.assertEquals(gen1c.getInputCount(), 1);
            Assert.assertEquals(gen1c.getOutputCount(), 1);
            Assert.assertEquals(gen1c.getInputs().iterator().next(), each);
            for (Node gen2c : gen1c.getOutputs()) {
               Assert.assertEquals(gen2c.getInputCount(), 2);
               Assert.assertEquals(gen2c.getOutputCount(), 0);
               Assert.assertTrue(gen2c.getInputs().contains(gen1c));
            }
         }
      }
   }

   @Test
   public void testCopyNode2() throws Exception {
      // Set Up entities
      NodeType type = NodeType.newInstance("mytype");

      Node root1 = Node.newRoot(type, "root1");
      Node gen1c1 = Node.newOutput(root1, type, "gen1c1");
      Node gen1c2 = Node.newOutput(root1, type, "gen1c2");
      Node gen2c1 = Node.newOutput(gen1c1, type, "gen2c1");
      gen1c2.getOutputs().add(gen2c1); // loop
      Node root2 = Node.newRoot(type, "root2");
      root2.getOutputs().add(gen2c1); // loop

      em.persist(root1);
      em.persist(root2);
      em.getTransaction().commit();
      em.getTransaction().begin();

      // Copy root2
      Node rootCp = Node.copy(root2, null);
      rootCp.setName(root2.getName() + "-cloned");
      em.persist(rootCp);

      em.getTransaction().commit();
      em.getTransaction().begin();

      // Check entities
      DefaultDaoJPA<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      List<Node> roots = nodeDao.find("isRoot", true);
      Assert.assertEquals(roots.size(), 3);

      List<Node> nodes = nodeDao.find("name", root2.getName() + "-cloned");
      Assert.assertEquals(nodes.size(), 1);
      Node root2Cp = nodes.iterator().next();

      Assert.assertEquals(root2Cp.getInputCount(), 0);
      Assert.assertEquals(root2Cp.getOutputCount(), 1);

      Node child = root2Cp.getOutputs().iterator().next();
      Assert.assertEquals(child.getInputCount(), 1);
      Assert.assertEquals(child.getOutputCount(), 0);
      Assert.assertEquals(child.getInputs().iterator().next(), root2Cp);
   }

   @Test
   public void testCopyNode3() throws Exception {
      // Set Up entities
      NodeType type = NodeType.newInstance("mytype");

      Node root1 = Node.newRoot(type, "root1");
      Node gen1c1 = Node.newOutput(root1, type, "gen1c1");
      Node gen1c2 = Node.newOutput(root1, type, "gen1c2");
      Node gen2c1 = Node.newOutput(gen1c1, type, "gen2c1");
      gen1c2.getOutputs().add(gen2c1); // loop
      Node root2 = Node.newRoot(type, "root2");
      root2.getOutputs().add(gen2c1); // loop

      em.persist(root1);
      em.persist(root2);
      em.getTransaction().commit();
      em.getTransaction().begin();

      // Copy root
      Node rootCp = Node.copy(root1, null);
      rootCp.setName(root1.getName() + "-cloned");
      em.persist(rootCp);

      em.getTransaction().commit();
      em.getTransaction().begin();

      // Check entities
      DefaultDaoJPA<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      List<Node> roots = nodeDao.find("isRoot", true);
      Assert.assertEquals(roots.size(), 3);

      root1 = nodeDao.find("name", "root1").iterator().next();
      Node root1Cp = nodeDao.find("name", root1.getName() + "-cloned").iterator().next();
      root2 = nodeDao.find("name", "root2").iterator().next();

      Assert.assertEquals(root1.getInputCount(), 0);
      Assert.assertEquals(root1Cp.getInputCount(), 0);
      Assert.assertEquals(root2.getInputCount(), 0);

      Assert.assertEquals(root1.getOutputCount(), 2);
      Assert.assertEquals(root1Cp.getOutputCount(), 2);
      Assert.assertEquals(root2.getOutputCount(), 1);

      Node lastRoot1 = root1.getOutputs().iterator().next().getOutputs().iterator().next();
      Node lastRoot1Cp = root1Cp.getOutputs().iterator().next().getOutputs().iterator().next();

      Assert.assertEquals(lastRoot1.getInputCount(), 3);
      Assert.assertEquals(lastRoot1Cp.getInputCount(), 2);
      Assert.assertTrue(lastRoot1.getInputs().contains(root2));
      Assert.assertFalse(lastRoot1Cp.getInputs().contains(root2));
      Assert.assertEquals(lastRoot1.getOutputCount(), 0);
      Assert.assertEquals(lastRoot1Cp.getOutputCount(), 0);
   }
}
