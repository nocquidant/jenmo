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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.jenmo.common.util.IProcedure.ProcedureException;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToMap;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.IDefaultDao;
import org.jenmo.core.testutil.MeasConstants;
import org.jenmo.core.testutil.PropConstants;
import org.jenmo.core.testutil.TypeConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbBusiness extends AbstractTestDbPopu {
   private static EntityManager em;

   @BeforeClass
   public static void setupClass() throws Exception {
      em = initEm();
      cleanUpTables(em);
      runPopulator(new PopuForBusiness(em));
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
   public void testNavigateAll() throws Exception {
      IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("isRoot", true);
      List<Node> roots = nodeDao.find(map);

      int count = 0;
      for (Node root : roots) {
         count = retrieveAll(root, ++count);
      }
      LOGGER.info("*** count=" + count);
   }

   private int retrieveAll(Node parent, int count) {
      Set<Node> adapter = parent.getOutputs();
      for (Node child : adapter) {
         count = retrieveAll(child, ++count);
      }
      return count;
   }

   @Test
   public void testFields() {
      IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      Node well = nodeDao.findSingle("name", "w1");
      Assert.assertEquals(well.getComment(), "blahblahblah...");

      Property p = well.getNodeType().getProperty(PropConstants.OILFIELD_NAME.toString());
      Assert.assertEquals(well.getProperty(p, String.class), "myField");

      p = well.getNodeType().getProperty(PropConstants.PLATEFORM_NAME.toString());
      Assert.assertEquals(well.getProperty(p, String.class), "myPlateform");

      p = well.getNodeType().getProperty(PropConstants.WELL_STATUS.toString());
      Assert.assertEquals(well.getProperty(p, String.class), "myStatus");

      p = well.getNodeType().getProperty(PropConstants.WELL_WATER_DEPTH.toString());
      Assert.assertEquals(well.getProperty(p, Double.class), new Double(666.0));
   }

   protected static class PopuForBusiness implements IPopulator {

      private static final int NUM_BORES = 1;

      private static final int NUM_LOGS = 1;

      private EntityManager currentEm;

      public PopuForBusiness(EntityManager currentEm) {
         this.currentEm = currentEm;
      }

      public EntityManager getEm() {
         return currentEm;
      }

      @Override
      public boolean execute() throws ProcedureException {
         // WellNode definition

         Property p1 = Property.newInstance(PropConstants.OILFIELD_NAME.toString());
         Property p2 = Property.newInstance(PropConstants.PLATEFORM_NAME.toString());
         Property p3 = Property.newInstance(PropConstants.WELL_STATUS.toString());
         Property p4 = Property.newInstance(PropConstants.WELL_WATER_DEPTH.toString());
         NodeType typeWell = NodeType.newInstance(TypeConstants.WELL.toString(), p1, p2, p3, p4);

         Node well = Node.newRoot(typeWell, "w1");
         currentEm.persist(well);

         // Just a dummy comment
         well.setComment("blahblahblah...");

         // WellNode properties
         Property p = well.getNodeType().getProperty(PropConstants.OILFIELD_NAME.toString());
         well.setProperty(p, "myField");

         p = well.getNodeType().getProperty(PropConstants.PLATEFORM_NAME.toString());
         well.setProperty(p, "myPlateform");

         p = well.getNodeType().getProperty(PropConstants.WELL_STATUS.toString());
         well.setProperty(p, "myStatus");

         p = well.getNodeType().getProperty(PropConstants.WELL_WATER_DEPTH.toString());
         well.setProperty(p, 666.);

         // WellGeomNode definition
         p = Property.newInstance(PropConstants.COORDINATE_SYSTEM.toString());
         NodeType typeGeom = NodeType.newInstance(TypeConstants.GEOMETRY.toString(), p);
         Node wellGeom = Node.newOutput(well, typeGeom, well.getName() + "Geom");

         // WellGeomNode field
         SplitBlob sblob = SplitBlob.newInstance(new double[] { 36, 37, 38 });
         AdapterToMap<String, SplitBlob, NodeField> adapterMs = wellGeom.getFields();
         adapterMs.put(MeasConstants.GEOMETRY.toString(), sblob);

         // WellGeomNode properties
         p = wellGeom.getNodeType().getProperty(PropConstants.COORDINATE_SYSTEM.toString());
         well.setProperty(p, "UTM");

         NodeType typeWellbore = NodeType.newInstance(TypeConstants.BORE.toString());
         NodeType typeLog = NodeType.newInstance(TypeConstants.LOG.toString());
         for (int i = 0; i < NUM_BORES; i++) {
            Node bore = Node.newOutput(well, typeWellbore, "wb" + (i + 1));
            for (int j = 0; j < NUM_LOGS; j++) {
               Node.newOutput(bore, typeLog, "log" + (j + 1));
            }
         }
         return true;
      }
   }
}