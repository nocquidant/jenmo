/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jenmo.core.domain.perf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.jenmo.common.util.IProcedure.ProcedureException;
import org.jenmo.core.domain.AbstractTestDbPopu;
import org.jenmo.core.domain.IPopulator;
import org.jenmo.core.domain.Node;
import org.jenmo.core.orm.JpaSpiActions;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.IDefaultDao;
import org.jenmo.core.testutil.DbUseCase;
import org.jenmo.core.testutil.MyTimer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbHeavyNode extends AbstractTestDbPopu {

   private static EntityManager em;

   @BeforeClass
   public static void setupClass() throws Exception {
      em = initEm();
      // cleanUpTables(em);
      // runPopulator(new PopuForNodeRawForce(em));
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
   public void testNavigate() throws Exception {
      boolean pureJava = true;

      if (pureJava) {
         MyTimer t = new MyTimer();

         IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

         Map<String, Object> map = new HashMap<String, Object>();
         map.put("isRoot", true);
         List<Node> roots = nodeDao.find(map);

         int count = 0;
         for (Node root : roots) {
            count = retrieveAll(root, ++count);
         }
         LOGGER.info("*** count=" + count);

         t.end("End retrieving all Nodes using Java navigation");
      } else {
         MyTimer t = new MyTimer();

         IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);
         List<Node> nodes = nodeDao.find(new HashMap<String, Object>());
         LOGGER.info("*** count=" + nodes.size());

         t.end("End retrieving all Nodes using Jpaql");
      }
   }

   private int retrieveAll(Node parent, int count) {
      Set<Node> adapter = parent.getOutputs();
      for (Node child : adapter) {
         count = retrieveAll(child, ++count);
      }
      return count;
   }

   public static void main(String[] args) {
      EntityManager em = AbstractTestDbPopu.initEm();
      cleanUpTables(em);

      MyTimer timer = new MyTimer();
      DbUseCase dbUc = new DbUseCase() {
         @Override
         protected void reallyExecute(EntityManager em) throws DbUseCaseException {
            try {
               PopuForNodeRawForce popu = new PopuForNodeRawForce(em);
               popu.execute();
            } catch (ProcedureException e) {
               throw new DbUseCaseException(e);
            }
         }
      };
      dbUc.execute(em, true);
      timer.end("End populating");
   }

   protected static class PopuForNodeRawForce implements IPopulator {

      // 10
      private static final int NUM_ROOT = 10;

      // 10
      private static final int POPULATION = 10;

      private EntityManager currentEm;

      public PopuForNodeRawForce(EntityManager currentEm) {
         this.currentEm = currentEm;
      }

      public EntityManager getEm() {
         return currentEm;
      }

      @Override
      public boolean execute() throws ProcedureException {
         ArrayList<Node> gen1 = new ArrayList<Node>();
         ArrayList<Node> gen2 = new ArrayList<Node>(100);
         ArrayList<Node> gen3 = new ArrayList<Node>(1000);
         ArrayList<Node> gen4 = new ArrayList<Node>(10000);
         ArrayList<Node> gen5 = new ArrayList<Node>(100000);

         // Root
         MyTimer t = new MyTimer();
         Node superRoot = Node.newRoot("super_root");

         MyTimer tp = new MyTimer();
         currentEm.persist(superRoot);
         tp.end("End persist");

         MyTimer tc = new MyTimer();
         currentEm.getTransaction().commit();
         tc.end("End commit");

         t.end("** End root");

         // Gen1
         MyTimer t1 = new MyTimer();
         currentEm.getTransaction().begin();
         for (int i = 0; i < NUM_ROOT; i++) {
            gen1.add(Node.newOutput(superRoot, "roots#" + i));
         }

         MyTimer tp1 = new MyTimer();
         JpaSpiActions.getInstance().persistAll(currentEm, gen1);
         tp1.end("End persist");

         MyTimer tc1 = new MyTimer();
         currentEm.getTransaction().commit();
         tc1.end("End commit");

         t1.end("** End 1st gen (" + gen1.size() + ")");

         // Gen2

         MyTimer t2 = new MyTimer();
         currentEm.getTransaction().begin();
         for (Node node : gen1) {
            for (int i = 0; i < POPULATION; i++) {
               gen2.add(Node.newOutput(node, "sub_root#" + i));
            }
         }

         MyTimer tp2 = new MyTimer();
         JpaSpiActions.getInstance().persistAll(currentEm, gen2);
         tp2.end("End persist");

         MyTimer tc2 = new MyTimer();
         currentEm.getTransaction().commit();
         tc2.end("End commit");

         t2.end("** End 2nd gen (" + gen2.size() + ")");

         // Gen3

         MyTimer t3 = new MyTimer();
         currentEm.getTransaction().begin();
         for (Node node : gen2) {
            for (int i = 0; i < POPULATION; i++) {
               gen3.add(Node.newOutput(node, "sub_sub_root#" + i));
            }
         }

         MyTimer tp3 = new MyTimer();
         JpaSpiActions.getInstance().persistAll(currentEm, gen3);
         tp3.end("End persist");

         MyTimer tc3 = new MyTimer();
         currentEm.getTransaction().commit();
         tc3.end("End commit");

         t3.end("** End 3rd gen (" + gen3.size() + ")");

         // Gen4

         MyTimer t4 = new MyTimer();
         currentEm.getTransaction().begin();
         for (Node node : gen3) {
            for (int i = 0; i < POPULATION; i++) {
               gen4.add(Node.newOutput(node, "sub_sub_sub_root#" + i));
            }
         }

         MyTimer tp4 = new MyTimer();
         JpaSpiActions.getInstance().persistAll(currentEm, gen4);
         tp4.end("End persist");

         MyTimer tc4 = new MyTimer();
         currentEm.getTransaction().commit();
         tc4.end("End commit");

         t4.end("** End 4th gen (" + gen4.size() + ")");

         // Gen5

         MyTimer t5 = new MyTimer();
         currentEm.getTransaction().begin();
         for (Node node : gen4) {
            for (int i = 0; i < POPULATION; i++) {
               gen5.add(Node.newOutput(node, "sub_sub_sub_sub_root#" + i));
            }
         }

         MyTimer tp5 = new MyTimer();
         JpaSpiActions.getInstance().persistAll(currentEm, gen5);
         tp5.end("End persist");

         MyTimer tc5 = new MyTimer();
         currentEm.getTransaction().commit();
         tc5.end("End commit");

         t5.end("** End 5th gen (" + gen5.size() + ")");

         return true;
      }
   }
}