package org.jenmo.core.domain.example;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jenmo.common.util.IProcedure.ProcedureException;
import org.jenmo.core.descriptor.Descriptors;
import org.jenmo.core.domain.AbstractTestDbPopu;
import org.jenmo.core.domain.IPopulator;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeType;
import org.jenmo.core.domain.Property;
import org.jenmo.core.domain.SplitBlob;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.IDefaultDao;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExampleSimple extends AbstractTestDbPopu {
   private static enum UserConstants {
      LOGIN, FIRST_NAME, LAST_NAME, EMAIL, AGE, LIPOGRAM, CREATION_DATE;
   }

   private static enum TypeConstants {
      USER;
   }

   private static enum LipoConstants {
      LA_DISPARITION, LES_REVENENTES;
   }

   private static enum NovelConstants {
      LA_VIE_MODE_EMPLOI, LES_CHOSES;
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

   private static byte[] getNovelAsByte(String title) {
      return (title + ": blablabla...").getBytes();
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
         // All previous members are deleted from the database in super
         // class (clean up)

         // ----------------------------------------------------------------------
         // Create a user entity with fields:
         // - login
         // - first-name
         // - last-name
         // - email
         // - age
         // - creation date
         // ----------------------------------------------------------------------

         // Create Properties
         Property login = Property.newInstance(UserConstants.LOGIN.name());
         Property firstName = Property.newInstance(UserConstants.FIRST_NAME.name());
         Property lastName = Property.newInstance(UserConstants.LAST_NAME.name());
         Property email = Property.newInstance(UserConstants.EMAIL.name());
         Property age = Property.newInstance(UserConstants.AGE.name());
         Property lipogram = Property.newInstance(UserConstants.LIPOGRAM.name());
         Property creationDate = Property.newInstance(UserConstants.CREATION_DATE.name());

         // Create NodeType
         NodeType user = NodeType.newInstance(TypeConstants.USER.name(), login, firstName,
               lastName, email, age, lipogram, creationDate);

         // Create user entity
         Node perec = Node.newRoot(user, "perec");

         // Persist user entity
         em.persist(perec);
         em.getTransaction().commit();
         em.getTransaction().begin();

         // Set property values
         perec.setProperty(login, "gperec");
         perec.setProperty(firstName, "georges");
         perec.setProperty(lastName, "perec");
         perec.setProperty(email, "gperec@company.com");
         perec.setProperty(age, 46);
         perec.setProperty(creationDate, GregorianCalendar.getInstance().getTimeInMillis());

         // Flush properties to database
         em.getTransaction().commit();
         em.getTransaction().begin();

         // ----------------------------------------------------------------------
         // Add field to user entity:
         // - lipogram ages (as Map<String, Integer>)
         // ----------------------------------------------------------------------

         // Set property values using Adapter
         Map<String, Integer> lipoAges = perec.getProperties(lipogram, Descriptors.mapForProps(
               String.class, Integer.class));
         lipoAges.put(LipoConstants.LA_DISPARITION.name(), 1969);
         lipoAges.put(LipoConstants.LES_REVENENTES.name(), 1972);

         // Flush new properties to database
         em.getTransaction().commit();
         em.getTransaction().begin();

         // ----------------------------------------------------------------------
         // Add field to user entity:
         // - novels (as blob)
         // ----------------------------------------------------------------------

         byte[] lvmeNovelAsByte = getNovelAsByte("La Vie mode d'emploi");
         SplitBlob lvmeBlob = SplitBlob.newInstance(lvmeNovelAsByte);

         byte[] lcNovelAsByte = getNovelAsByte("Les Choses");
         SplitBlob lcBlob = SplitBlob.newInstance(lcNovelAsByte);

         Map<String, SplitBlob> novels = perec.getFields();
         novels.put(NovelConstants.LA_VIE_MODE_EMPLOI.name(), lvmeBlob);
         novels.put(NovelConstants.LES_CHOSES.name(), lcBlob);

         // Flush new properties to database
         em.getTransaction().commit();
         return true;
      }
   }

   @Test
   public void doAsserts() throws Exception {
      IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      // Retrieve Node named 'perec' from database
      Node perec = nodeDao.find("name", "perec").iterator().next();

      // Fetch properties from database and check their values
      Property p = perec.getNodeType().getProperty(UserConstants.LOGIN.name());
      String ps = perec.getProperty(p, String.class);
      Assert.assertEquals(ps, "gperec");

      p = perec.getNodeType().getProperty(UserConstants.FIRST_NAME.name());
      ps = perec.getProperty(p, String.class);
      Assert.assertEquals(ps, "georges");

      p = perec.getNodeType().getProperty(UserConstants.LAST_NAME.name());
      ps = perec.getProperty(p, String.class);
      Assert.assertEquals(ps, "perec");

      p = perec.getNodeType().getProperty(UserConstants.EMAIL.name());
      ps = perec.getProperty(p, String.class);
      Assert.assertEquals(ps, "gperec@company.com");

      p = perec.getNodeType().getProperty(UserConstants.AGE.name());
      Integer pi = perec.getProperty(p, Integer.class);
      Assert.assertEquals(pi, new Integer(46));

      p = perec.getNodeType().getProperty(UserConstants.CREATION_DATE.name());
      Date pd = perec.getProperty(p, Date.class);
      Date currentDate = new Date(GregorianCalendar.getInstance().getTimeInMillis());
      Assert.assertTrue(pd.compareTo(currentDate) < 0);

      p = perec.getNodeType().getProperty(UserConstants.LIPOGRAM.name());
      Map<String, Integer> lipoAges = perec.getProperties(p, Descriptors.mapForProps(String.class,
            Integer.class));

      Integer ldAge = lipoAges.get(LipoConstants.LA_DISPARITION.name());
      Integer lrAge = lipoAges.get(LipoConstants.LES_REVENENTES.name());
      Assert.assertEquals(ldAge, new Integer(1969));
      Assert.assertEquals(lrAge, new Integer(1972));

      Map<String, SplitBlob> novels = perec.getFields();
      byte[] array = novels.get(NovelConstants.LA_VIE_MODE_EMPLOI.name()).getValues(byte[].class);
      Assert.assertArrayEquals(array, getNovelAsByte("La Vie mode d'emploi"));

      array = novels.get(NovelConstants.LES_CHOSES.name()).getValues(byte[].class);
      Assert.assertArrayEquals(array, getNovelAsByte("Les Choses"));
   }
}