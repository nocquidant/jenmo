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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jenmo.core.persistence.PersistenceAppSupport;
import org.jenmo.core.persistence.PersistenceManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbScope extends AbstractTestDbPopu {
   private static PersistenceManager pm;
   
   private static EntityManager em;

   @BeforeClass
   public static void setupClass() throws Exception {
      pm =  PersistenceManager.getInstance();
      em = pm.getEntityManagerFactory().createEntityManager();
      cleanUpTables(em);
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
   public void testScope() {
      final EntityManager em1 = em;
      
      final EntityManagerFactory emf = pm.getEntityManagerFactory();
      final EntityManager em2 = emf.createEntityManager();
      Assert.assertSame(em1, em2);

      class BasicThread1 extends Thread {

         public void run() {
            EntityManager em3 = emf.createEntityManager();
            Assert.assertNotSame(em1, em3);
            new PersistenceAppSupport().sessionDestroyed(pm);
         }
      }
      Thread thread = new BasicThread1();
      thread.start();

      try {
         Thread.sleep(1000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}
