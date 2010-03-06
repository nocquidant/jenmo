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
import javax.persistence.Query;

import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.IDefaultDao;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbDeleteLoop extends AbstractTestDbPopu {
   
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
      txBegin(em);
   }

   @After
   public void teardownMethod() {
      txCommit(em);
   }

   @Test
   public void testDeleteLoop() throws Exception {
      IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      // Populate data

      Node root = Node.newRoot(NodeType.newInstance("tr"), "root");
      Node node1 = Node.newRoot(NodeType.newInstance("t1"), "node#1");
      Node node2 = Node.newRoot(NodeType.newInstance("t2"), "node#2");
      Node node3 = Node.newRoot(NodeType.newInstance("t3"), "node#3");

      node1.getOutputs().add(node2);
      node2.getOutputs().add(node3);
      node3.getOutputs().add(node1);
      root.getOutputs().add(node1);

      nodeDao.persist(root);
      txCommit(em);

      // Try deleting one node

      nodeDao.delete(node1.getId());

      txCommit(em);

      // Test result

      Assert.assertFalse(nodeDao.find("name", "root").isEmpty());
      Assert.assertTrue(nodeDao.find("name", "node#1").isEmpty());
      Assert.assertTrue(nodeDao.find("name", "node#2").isEmpty());
      Assert.assertTrue(nodeDao.find("name", "node#3").isEmpty());

      // Last checks

      txCommit(em);

      String queryString = "SELECT * FROM NODE";
      Query query = em.createNativeQuery(queryString);
      Assert.assertEquals(query.getResultList().size(), 1);

      queryString = "SELECT * FROM EDGE LIMIT 1";
      query = em.createNativeQuery(queryString);
      Assert.assertTrue(query.getResultList().isEmpty());
   }
}
