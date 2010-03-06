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

import java.util.List;

import javax.persistence.EntityManager;

import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.IDefaultDao;
import org.jenmo.core.testutil.JpaSpiActions4Test;
import org.jenmo.core.testutil.TypeConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbDeleteSimple extends AbstractTestDbPopu {

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
   public void testDeleteSimple1() throws Exception {
      IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      // Populate data

      NodeType wellType = NodeType.newInstance(TypeConstants.WELL.toString());
      NodeType boreType = NodeType.newInstance(TypeConstants.BORE.toString());
      NodeType logType = NodeType.newInstance(TypeConstants.LOG.toString());

      Node well = Node.newRoot(wellType, "well#1");
      Node bore = Node.newOutput(well, boreType, "bore#1");
      Node.newOutput(bore, logType, "log#1");

      nodeDao.persist(well);
      txCommit(em);

      // Do delete bore

      nodeDao.delete(bore.getId());

      // well.getOutputs().remove(bore);
      Assert.assertEquals(1, well.getOutputs().size());

      txCommit(em);

      JpaSpiActions4Test.getInstance().refresh(em, well);
      Assert.assertEquals(0, well.getOutputs().size());

      List<Node> list = nodeDao.find("name", "well#1");
      Assert.assertEquals(list.size(), 1);

      list = nodeDao.find("name", "bore#1");
      Assert.assertEquals(list.size(), 0);

      list = nodeDao.find("name", "log#1");
      Assert.assertEquals(list.size(), 0);
   }

   @Test
   public void testDeleteSimple2() throws Exception {
      IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      // Populate data (cascaded = false)

      NodeType wellType = NodeType.newInstance(TypeConstants.WELL.toString());
      NodeType boreType = NodeType.newInstance(TypeConstants.BORE.toString());
      NodeType logType = NodeType.newInstance(TypeConstants.LOG.toString());

      Node well = Node.newRoot(wellType, "well#1");
      Node bore = Node.newOutput(well, boreType, "bore#1");
      Node log = Node.newOutput(bore, logType, "log#1", false);

      nodeDao.persist(well);
      txCommit(em);

      // Do delete bore

      // well.getOutputs().remove(bore);
      nodeDao.delete(bore.getId());

      txCommit(em);

      JpaSpiActions4Test.getInstance().refresh(em, well);
      Assert.assertEquals(0, well.getOutputs().size());

      JpaSpiActions4Test.getInstance().refresh(em, log);
      Assert.assertEquals(0, log.getInputs().size());

      List<Node> list = nodeDao.find("name", "well#1");
      Assert.assertEquals(list.size(), 1);

      list = nodeDao.find("name", "bore#1");
      Assert.assertEquals(list.size(), 0);

      list = nodeDao.find("name", "log#1");
      Assert.assertEquals(list.size(), 1);
   }
}
