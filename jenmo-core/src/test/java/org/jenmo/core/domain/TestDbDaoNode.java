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
package org.jenmo.core.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jenmo.common.util.IProcedure.ProcedureException;
import org.jenmo.core.orm.JpaSpiActions;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.IDefaultDao;
import org.jenmo.core.testutil.JpaSpiActions4Test;
import org.jenmo.core.testutil.MyTimer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbDaoNode extends AbstractTestDbPopu {

   private static EntityManager em;

   @BeforeClass
   public static void setupClass() throws Exception {
      em = initEm();
      cleanUpTables(em);
      runPopulator(new PopuForNodeDao(em));
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
   public void testGetAll() throws Exception {
      LOGGER.debug("testNavigate");

      MyTimer t = new MyTimer();

      IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);
      Map<String, Object> map = new HashMap<String, Object>();
      List<Node> roots = nodeDao.find(map);
      int count = roots.size();
      LOGGER.info("*** count=" + count);

      t.end("End testGetAll");
   }

   protected static class PopuForNodeDao implements IPopulator {
      // 10
      private static final int NUM_ROOT = 10;

      // 10000
      private static final int NUM_NODE = 100;

      // private static WeakHashMap<Node, Object> checkGCMap = new WeakHashMap<Node,
      // Object>(NUM_NODE);

      private EntityManager currentEm;

      public PopuForNodeDao(EntityManager currentEm) {
         this.currentEm = currentEm;
      }

      public EntityManager getEm() {
         return currentEm;
      }

      @Override
      public boolean execute() throws ProcedureException {
         MyTimer t = new MyTimer();

         Node superRoot = Node.newRoot("super_root");
         currentEm.persist(superRoot);
         currentEm.getTransaction().commit();

         ArrayList<Node> gen1 = new ArrayList<Node>(NUM_ROOT);
         currentEm.getTransaction().begin();
         for (int i = 0; i < NUM_ROOT; i++) {
            gen1.add(Node.newOutput(superRoot, "roots#" + i));
         }
         JpaSpiActions.getInstance().persistAll(currentEm, gen1);
         currentEm.getTransaction().commit();

         for (int i = 0; i < NUM_ROOT; i++) {
            Node node = gen1.get(0);

            ArrayList<Node> gen2 = new ArrayList<Node>(NUM_NODE);
            ArrayList<Edge> buff = new ArrayList<Edge>(NUM_NODE);

            currentEm.getTransaction().begin();
            for (int j = 0; j < NUM_NODE; j++) {
               gen2.add(Node.newOutput(node, "sub_root#" + j));
               // checkGCMap.put(gen2.get(j), null);
            }
            buff.addAll(node.getEdges(true));
            JpaSpiActions.getInstance().persistAll(currentEm, gen2);
            currentEm.getTransaction().commit();

            // System.out.println("Releasing objects #" + (current++));
            // System.out.println("Managed objects (before): "
            // + JpaSpiActions4Test.getInstance().getManagedObjects(currentEm).size());

            gen1.remove(0);
            JpaSpiActions4Test.getInstance().evict(currentEm, node);

            JpaSpiActions4Test.getInstance().releaseAll(currentEm, buff);
            JpaSpiActions4Test.getInstance().releaseAll(currentEm, gen2);

            System.gc();
            // System.out.println("Objects in WeakMap:" + checkGCMap.size());

            // System.out.println("Managed objects (after): "
            // + JpaSpiActions4Test.getInstance().getManagedObjects(currentEm).size());
         }

         t.end("End gen");
         return true;
      }
   }
}
