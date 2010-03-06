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
import javax.persistence.Query;

import org.jenmo.common.util.IProcedure.ProcedureException;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToMap;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.IDefaultDao;
import org.jenmo.core.repository.delete.IllegalDeleteException;
import org.jenmo.core.testutil.JpaSpiActions4Test;
import org.jenmo.core.testutil.MeasConstants;
import org.jenmo.core.testutil.PropConstants;
import org.jenmo.core.testutil.TypeConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbDeleteBz1 extends AbstractTestDbPopu {

   private static EntityManager em;

   @BeforeClass
   public static void setupClass() throws Exception {
      em = initEm();
      cleanUpTables(em);
      runPopulator(new MyPopu(em));
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
      // Preliminary checks

      IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      Node log1 = nodeDao.find("name", "log#1").get(0);
      Node log2 = nodeDao.find("name", "log#2").get(0);
      Assert.assertEquals(log1.getInputs().size(), 2);
      Assert.assertEquals(log2.getInputs().size(), 2);

      // Delete Metadata

      IDefaultDao<Property, Long> propDao = new DefaultDaoJPA<Property, Long>(Property.class, em);

      List<Property> props = propDao.find("name", PropConstants.PLATEFORM_NAME.toString());
      Assert.assertEquals(props.size(), 1);
      Property p = props.get(0);

      try {
         propDao.delete(p.getId());
         Assert.fail("Expected exception");
      } catch (IllegalDeleteException e) {
      }

      IDefaultDao<NodeType, Long> typeDao = new DefaultDaoJPA<NodeType, Long>(NodeType.class, em);

      List<NodeType> types = typeDao.find("ntype", TypeConstants.WELL.toString());
      Assert.assertEquals(types.size(), 1);
      NodeType t = types.get(0);

      try {
         typeDao.delete(t.getId());
         Assert.fail("Expected exception");
      } catch (IllegalDeleteException e) {
      }

      // Delete Well

      for (Node input : log1.getInputs()) {
         System.out.println("input.getName()=" + input.getName());
      }

      Node well1 = nodeDao.find("name", "well#1").get(0);
      nodeDao.delete(well1.getId());

      txCommit(em);

      Assert.assertTrue(nodeDao.find("name", "well#1").isEmpty());
      Assert.assertTrue(nodeDao.find("name", "bore#1").isEmpty());
      Assert.assertTrue(nodeDao.find("name", "log#1").isEmpty());
      Assert.assertTrue(nodeDao.find("name", "log#2").isEmpty());

      // Delete Faults

      Node fault1 = nodeDao.find("name", "fault#1").get(0);
      Node fault2 = nodeDao.find("name", "fault#2").get(0);
      
      // Why must refresh unfetched faults?
      JpaSpiActions4Test.getInstance().refresh(em, fault1);
      JpaSpiActions4Test.getInstance().refresh(em, fault2);
      
      nodeDao.delete(fault1.getId());
      nodeDao.delete(fault2.getId());

      txCommit(em);

      Assert.assertTrue(nodeDao.find("name", "fault#1").isEmpty());
      Assert.assertTrue(nodeDao.find("name", "fault#2").isEmpty());
      Assert.assertTrue(nodeDao.find("name", "log#1").isEmpty());
      Assert.assertTrue(nodeDao.find("name", "log#2").isEmpty());

      Assert.assertEquals(nodeDao.find("name", "log#1").size(), 0);
      Assert.assertEquals(nodeDao.find("name", "log#2").size(), 0);

      // Delete Metadata

      props = propDao.find("name", PropConstants.PLATEFORM_NAME.toString());
      Assert.assertEquals(props.size(), 1);
      p = props.get(0);

      try {
         propDao.delete(p.getId());
         Assert.fail("Expected exception");
      } catch (IllegalDeleteException e) {
      }

      types = typeDao.find("ntype", TypeConstants.WELL.toString());
      Assert.assertEquals(types.size(), 1);
      t = types.get(0);

      typeDao.delete(t.getId());
      types = typeDao.find("ntype", TypeConstants.WELL.toString());
      Assert.assertEquals(types.size(), 0);

      propDao.delete(p.getId());
      props = propDao.find("name", PropConstants.PLATEFORM_NAME.toString());
      Assert.assertEquals(props.size(), 0);

      // Last checks

      txCommit(em);

      String queryString = "SELECT * FROM NODE LIMIT 1";
      Query query = em.createNativeQuery(queryString);
      Assert.assertTrue(query.getResultList().isEmpty());

      queryString = "SELECT * FROM EDGE LIMIT 1";
      query = em.createNativeQuery(queryString);
      Assert.assertTrue(query.getResultList().isEmpty());
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
         // METADATA
         // --------

         Property pn = Property.newInstance(PropConstants.PLATEFORM_NAME.toString());
         Property cs = Property.newInstance(PropConstants.COORDINATE_SYSTEM.toString());

         NodeType typeWell = NodeType.newInstance(TypeConstants.WELL.toString(), pn);
         NodeType typeBore = NodeType.newInstance(TypeConstants.BORE.toString());
         NodeType typeLog = NodeType.newInstance(TypeConstants.LOG.toString(), cs);
         NodeType typeFault = NodeType.newInstance(TypeConstants.FAULT.toString());

         // WELL BRANCH (1 well, 2 bores, 2 logs)
         // -------------------------------------

         Node well = Node.newRoot(typeWell, "well#1");
         em.persist(well);

         Node bore = Node.newOutput(well, typeBore, "bore#1");

         Node log1 = Node.newOutput(bore, typeLog, "log#1");
         Node log2 = Node.newOutput(bore, typeLog, "log#2");

         // well#1 has one property
         well.setProperty(pn, "plateform#1");

         // log#1 has one property
         log1.setProperty(cs, "UTM");

         // log#1 has one blob
         SplitBlob blob = SplitBlob.newInstance(new double[][] { { 0., 1000. }, { 0.33, 0.66 } });
         AdapterToMap<String, SplitBlob, NodeField> adapter = log1.getFields();
         adapter.put(MeasConstants.PROROSITY.toString(), blob);
         
         txCommit(getEm());

         // FAULT BRANCH (2 faults)
         // -----------------------

         Node fault1 = Node.newRoot(typeFault, "fault#1");
         Node fault2 = Node.newRoot(typeFault, "fault#2");

         em.persist(fault1);
         em.persist(fault2);

         // fault#1 references log#1 & fault#2 references log#2
         fault1.getOutputs().add(log1);
         fault2.getOutputs().add(log2);

         return true;
      }
   }
}
