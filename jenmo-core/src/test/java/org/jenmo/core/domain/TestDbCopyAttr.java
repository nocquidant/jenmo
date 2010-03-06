package org.jenmo.core.domain;

import java.util.List;

import javax.persistence.EntityManager;

import org.jenmo.core.descriptor.Descriptors;
import org.jenmo.core.descriptor.IPropertyDescriptor.IPropDescAsMap;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbCopyAttr extends AbstractTestDbPopu {
   private static final byte[] BLAH = "blahblahblah...".getBytes();
   
   private static EntityManager em;

   @BeforeClass
   public static void setupClass() throws Exception {
      em = initEm();
      cleanUpTables(em);
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
   public void testCopyAttribute() throws Exception {
      // Set Up simple entity
      Property p1 = Property.newInstance("p1");
      Property p2 = Property.newInstance("p2");
      NodeType type = NodeType.newInstance("type", p1, p2);

      Node root = Node.newRoot("root");
      em.persist(root);

      Node gen1c1 = Node.newOutput(root, type, "gen1c1");
      gen1c1.setProperty(p1, "vp1_for_gen1c1");

      IPropDescAsMap<String, Integer> desc = Descriptors.mapForProps(String.class, Integer.class);
      gen1c1.getProperties(p2, desc).put("vp2_for_gen1c1", 100);

      gen1c1.getFields().put("array", SplitBlob.newInstance(BLAH));

      Node gen2c1 = Node.newOutput(gen1c1, type, "gen2c1");
      gen2c1.setProperty(p1, "vp1_for_gen2c1");

      // Copy entity
      Node gen1c1Cp = Node.copy(gen1c1, null);
      gen1c1Cp.setName(gen1c1.getName() + "-cloned");
      em.persist(gen1c1Cp);

      em.getTransaction().commit();
      em.getTransaction().begin();

      // Check entity
      DefaultDaoJPA<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);
      List<Node> roots = nodeDao.find("isRoot", true);
      Assert.assertEquals(roots.size(), 1);

      root = roots.iterator().next();
      Assert.assertEquals(root.getOutputCount(), 1);

      List<Node> clones = nodeDao.find("name", "gen1c1-cloned");
      Assert.assertEquals(clones.size(), 1);

      Node clone = clones.iterator().next();
      Assert.assertEquals(clone.getInputCount(), 0);
      Assert.assertEquals(clone.getOutputCount(), 1);
      Assert.assertEquals(clone.getNodeType(), type);
      Assert.assertEquals(clone.getProperty(p1, String.class), "vp1_for_gen1c1");
      Assert.assertEquals(clone.getProperties(p2, desc).size(), 1);
      Assert.assertEquals(clone.getProperties(p2, desc).get("vp2_for_gen1c1"), new Integer(100));
      Assert.assertEquals(clone.getFields().size(), 1);
      Assert.assertArrayEquals(clone.getFields().get("array").getValues(byte[].class), BLAH);

      Node childOfClone = clone.getOutputs().iterator().next();
      Assert.assertEquals(childOfClone.getName(), "gen2c1");
      Assert.assertEquals(childOfClone.getInputCount(), 1);
      Assert.assertEquals(childOfClone.getOutputCount(), 0);
      Assert.assertEquals(childOfClone.getProperty(p1, String.class), "vp1_for_gen2c1");
   }
}
