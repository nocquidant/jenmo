package org.jenmo.core.domain;

import javax.persistence.EntityManager;

import org.jenmo.common.util.IProcedure.ProcedureException;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.IDefaultDao;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbBugOpenJpa extends AbstractTestDbPopu {

   private static EntityManager em;

   @BeforeClass
   public static void setupClass() throws Exception {
      em = initEm();
      cleanUpTables(em);
      runPopulator(new IPopulator() {
         @Override
         public EntityManager getEm() {
            return em;
         }

         @Override
         public boolean execute() throws ProcedureException {
            NodeType type = NodeType.newInstance("node-type");

            Node root = Node.newRoot(type, "root");
            Node child = Node.newOutput(root, type, "child");
            Node childchild = Node.newOutput(child, type, "childchild");
            Node.newOutput(childchild, type, "childchildchild");

            em.persist(root);
            return true;
         }

      });
   }

   @AfterClass
   public static void teardownClass() {
      closeEm(em);
   }

   @Before
   public void setupMethod() {
      txBegin(em);
   }

   @After
   public void teardownMethod() {
      txCommit(em);
   }

   @Test
   public void testDelete() throws Exception {
      IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);
      Node root = nodeDao.findSingle("name", "root");
      System.out.println("root's name: " + root.getName());

      nodeDao.delete(root.getId());
   }
}
