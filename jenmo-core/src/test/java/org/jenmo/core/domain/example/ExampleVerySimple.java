package org.jenmo.core.domain.example;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jenmo.common.util.IProcedure.ProcedureException;
import org.jenmo.core.descriptor.Descriptors;
import org.jenmo.core.domain.AbstractTestDbPopu;
import org.jenmo.core.domain.IPopulator;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeType;
import org.jenmo.core.domain.Property;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExampleVerySimple extends AbstractTestDbPopu {
   private static enum UserConstants {
      LOGIN, EMAIL, ACCOUNT;
   }

   private static enum TypeConstants {
      USER;
   }

   private static EntityManager em;

   @BeforeClass
   public static void setupClass() throws Exception {
      em = initEm();
      cleanUpTables(em);
      runPopulator(new MyPopu(em));
   }

   @AfterClass
   public static void teardownClass() {
      if (em != null) {
         txCommit(em);
         em.close();
         em = null;
      }
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
   public void doAsserts() throws Exception {
      // Build a DAO to get Node entities
      DefaultDaoJPA<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      // Retrieve Node named 'aUser' from database
      Node user = nodeDao.findSingle("name", "aUser");

      // Fetch properties from database and check their values
      Property p = user.getNodeType().getProperty(UserConstants.LOGIN.name());
      String login = user.getProperty(p, String.class);
      Assert.assertEquals(login, "aUserLogin");

      // Navigate to unique child
      Node child = user.getOutputs().iterator().next();

      // Fetch properties from database and check their values
      p = child.getNodeType().getProperty(UserConstants.ACCOUNT.name());
      Map<String, BigDecimal> accounts = child.getProperties(p, Descriptors.mapForProps(
            String.class, BigDecimal.class));
      Assert.assertEquals(accounts.get("bnp"), new BigDecimal(75000.98));
   }

   private static class MyPopu implements IPopulator {
      EntityManager em;

      MyPopu(EntityManager em) {
         this.em = em;
      }

      public EntityManager getEm() {
         return em;
      }

      public boolean execute() throws ProcedureException {

         // Create Metadata
         Property login = Property.newInstance(UserConstants.LOGIN.name());
         Property email = Property.newInstance(UserConstants.EMAIL.name());
         Property account = Property.newInstance(UserConstants.ACCOUNT.name());
         NodeType typeUser = NodeType.newInstance(TypeConstants.USER.name(), login, email, account);

         // Create Node user
         Node aUser = Node.newRoot(typeUser, "aUser");

         // Set property values to user
         aUser.setProperty(login, "aUserLogin");
         aUser.setProperty(email, "aUser@company.com");

         // Create Node child
         Node aUserChild = Node.newOutput(aUser, typeUser, "aUserChild");

         // Set property values to child
         aUserChild.setProperty(login, "aUserChildLogin");
         aUserChild.setProperty(email, "aUserChild@company.com");

         // Set property values to child (more complicated)
         Map<String, BigDecimal> accounts = aUserChild.getProperties(account, Descriptors
               .mapForProps(String.class, BigDecimal.class));
         accounts.put("bnp", new BigDecimal(75000.98));
         accounts.put("lpb", new BigDecimal(25022.25));

         // Persist: note that we only need to explicitly persist a single root of the
         // object graph, since we have the "cascade" annotation on all the relations
         em.persist(aUser);

         // Commit the transaction, which will cause the entity to
         // be stored in the database
         em.getTransaction().commit();

         return true;
      }
   }

}
