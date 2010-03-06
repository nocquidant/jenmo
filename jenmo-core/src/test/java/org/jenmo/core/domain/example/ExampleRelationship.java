package org.jenmo.core.domain.example;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jenmo.common.util.IProcedure.ProcedureException;
import org.jenmo.core.constant.JenmoConstant;
import org.jenmo.core.domain.AbstractTestDbPopu;
import org.jenmo.core.domain.IPopulator;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeType;
import org.jenmo.core.domain.Property;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.IDefaultDao;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExampleRelationship extends AbstractTestDbPopu {
   public static enum Gender {
      MALE, FEMALE
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

   private static Node giveBirth(String name, Node mother, Node father, NodeType type) {
      // Add new child to mother
      // Don't want cascade during deletion mother -> child
      Node baby = Node.newOutput(mother, name, false);

      // Add new child to father
      // Don't want cascade during deletion father -> child
      father.getOutputs().add(baby, JenmoConstant.POST_ARG_CASCADED, new Boolean(false));

      // Set type and return new child
      baby.setNodeType(type);
      return baby;
   }

   @Test
   public void testNavigate() throws Exception {
      IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      // Retrieve all deity Nodes
      List<Node> deities = nodeDao.find(Collections.<String, Object> emptyMap());

      System.out.println("** Found " + deities.size() + " deity entities");
      for (Node deity : deities) {
         System.out.println("Deity: " + deity.getName());
      }

      // Get child of Zeus
      String queryString = "SELECT n FROM Edge e JOIN e.to AS n WHERE" + " e.from.name = :name";
      Query query = em.createQuery(queryString);
      query.setParameter("name", "Zeus");
      List<?> children = query.getResultList();

      System.out.println("** Found " + children.size() + " children entities");
      for (Object child : children) {
         System.out.println("Child of Zeus: " + ((Node) child).getName());
      }

      // To be continued (Siblings of Rhea, Half-siblings of Apollo, Cousins
      // of Leto)
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
         // All previous members are already deleted from the database
         // (clean up in super class)

         // Create the Property and the Type for deity entity
         Property gender = Property.newInstance("Gender");
         NodeType deity = NodeType.newInstance("Deity", gender);

         // Generation 1
         Node uranus = Node.newRoot(deity, "Uranus");
         uranus.setNodeType(deity);
         uranus.setProperty(gender, Gender.MALE.name());

         Node gaea = Node.newRoot(deity, "Gaea");
         gaea.setNodeType(deity);
         gaea.setProperty(gender, Gender.FEMALE.name());

         // Generation 2
         Node cronus = giveBirth("Cronus", gaea, uranus, deity);
         cronus.setProperty(gender, Gender.MALE.name());

         Node rhea = giveBirth("Rhea", gaea, uranus, deity);
         rhea.setProperty(gender, Gender.FEMALE.name());

         Node coeus = giveBirth("Coeus", gaea, uranus, deity);
         coeus.setProperty(gender, Gender.MALE.name());

         Node phoebe = giveBirth("Phoebe", gaea, uranus, deity);
         phoebe.setProperty(gender, Gender.FEMALE.name());

         Node oceanus = giveBirth("Oceanus", gaea, uranus, deity);
         oceanus.setProperty(gender, Gender.MALE.name());

         Node tethys = giveBirth("Tethys", gaea, uranus, deity);
         tethys.setProperty(gender, Gender.FEMALE.name());

         // Generation 3
         Node leto = giveBirth("Leto", phoebe, coeus, deity);
         leto.setProperty(gender, Gender.FEMALE.name());

         Node hestia = giveBirth("Hestia", rhea, cronus, deity);
         hestia.setProperty(gender, Gender.FEMALE.name());

         Node pluto = giveBirth("Pluto", rhea, cronus, deity);
         pluto.setProperty(gender, Gender.MALE.name());

         Node poseidon = giveBirth("Poseidon", rhea, cronus, deity);
         poseidon.setProperty(gender, Gender.MALE.name());

         Node zeus = giveBirth("Zeus", rhea, cronus, deity);
         zeus.setProperty(gender, Gender.MALE.name());

         Node hera = giveBirth("Hera", rhea, cronus, deity);
         hera.setProperty(gender, Gender.FEMALE.name());

         Node demeter = giveBirth("Demeter", rhea, cronus, deity);
         demeter.setProperty(gender, Gender.FEMALE.name());

         // Generation 4

         Node iapetus = giveBirth("Iapetus", tethys, coeus, deity);
         iapetus.setProperty(gender, Gender.MALE.name());

         Node clymene = Node.newRoot(deity, "Clymene");
         uranus.setNodeType(deity);
         clymene.setProperty(gender, Gender.FEMALE.name());

         Node apollo = giveBirth("Apollo", leto, zeus, deity);
         apollo.setProperty(gender, Gender.MALE.name());

         Node artemis = giveBirth("Artemis", leto, zeus, deity);
         artemis.setProperty(gender, Gender.MALE.name());

         Node persephone = giveBirth("Persephone", demeter, zeus, deity);
         persephone.setProperty(gender, Gender.MALE.name());

         Node ares = giveBirth("Ares", hera, zeus, deity);
         ares.setProperty(gender, Gender.MALE.name());

         Node hebe = giveBirth("Hebe", hera, zeus, deity);
         hebe.setProperty(gender, Gender.FEMALE.name());

         Node hephaestus = giveBirth("Hephaestus", hera, zeus, deity);
         hephaestus.setProperty(gender, Gender.MALE.name());

         Node prometheus = giveBirth("Prometheus", clymene, iapetus, deity);
         prometheus.setProperty(gender, Gender.MALE.name());

         Node atlas = giveBirth("Atlas", clymene, iapetus, deity);
         atlas.setProperty(gender, Gender.MALE.name());

         Node epimetheus = giveBirth("Epimetheus", clymene, iapetus, deity);
         epimetheus.setProperty(gender, Gender.FEMALE.name());

         Node dione = Node.newRoot(deity, "Dione");
         uranus.setNodeType(deity);
         clymene.setProperty(gender, Gender.FEMALE.name());

         Node aphrodite = giveBirth("Aphrodite", dione, zeus, deity);
         aphrodite.setProperty(gender, Gender.FEMALE.name());

         // Persisting roots
         em.persist(uranus);
         em.persist(gaea);
         em.persist(clymene);
         em.persist(dione);

         // Commit the transaction, which will cause the entities to
         // be stored in the database
         em.getTransaction().commit();
         return true;
      }
   }
}
