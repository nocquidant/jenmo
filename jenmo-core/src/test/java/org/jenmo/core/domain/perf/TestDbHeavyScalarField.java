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

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.jenmo.common.multiarray.IndexIterator;
import org.jenmo.common.util.IProcedure.ProcedureException;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToMap;
import org.jenmo.core.domain.AbstractTestDbPopu;
import org.jenmo.core.domain.IPopulator;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeField;
import org.jenmo.core.domain.SplitBlob;
import org.jenmo.core.domain.SplitBlobPart;
import org.jenmo.core.listener.IListener;
import org.jenmo.core.listener.SplitBlobEvent;
import org.jenmo.core.multiarray.IBlobPartAccessor;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.IDefaultDao;
import org.jenmo.core.testutil.DbUseCase;
import org.jenmo.core.testutil.JpaSpiActions4Test;
import org.jenmo.core.testutil.MeasConstants;
import org.jenmo.core.testutil.MyTimer;
import org.jenmo.core.util.SplitBlobUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDbHeavyScalarField extends AbstractTestDbPopu {

   public static int mode = 3;

   private static EntityManager em;

   private IListener<SplitBlobEvent> listener;

   @BeforeClass
   public static void setupClass() throws Exception {
      em = initEm();
      // cleanUpTables(em);
      // runPopulator(new PopuForScalarField(em));
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

   private void initListener(final EntityManager em) {
      listener = new IListener<SplitBlobEvent>() {

         private List<SplitBlobPart> previous = new ArrayList<SplitBlobPart>(100);

         @Override
         public void update(SplitBlobEvent o, Object arg) {
            // First time previousPart = null
            if (o.getOldPart() != null) {
               previous.add(o.getOldPart());
            }
            if (previous.size() == 10) {
               LOGGER.info("<Popu> OhOh, heavy blob, managing parts!");
               JpaSpiActions4Test.getInstance().evictAll(em, previous);
               previous = new ArrayList<SplitBlobPart>(100);
            }
         }
      };
   }

   @Test
   public void testGetAll() throws Exception {
      IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("name", "blobby");
      List<Node> nodes = nodeDao.find(map);
      Node node = nodes.iterator().next();

      Map<String, SplitBlob> adapter = node.getFields();
      SplitBlob sb = adapter.get(MeasConstants.PROROSITY.toString());

      initListener(em);
      double[][][] values2 = null;

      MyTimer timer = new MyTimer();
      if (mode == 1) {
         values2 = sb.getValues(double[][][].class, listener);
      } else if (mode == 2 || mode == 3) {
         int[] shape = sb.getShape();
         values2 = new double[shape[0]][shape[1]][shape[2]];
         IBlobPartAccessor reader = sb.getAccessor(listener);
         IndexIterator ii = new IndexIterator(shape);
         for (; ii.notDone(); ii.incr()) {
            int[] index = ii.value();
            double d = reader.getDouble(index);
            values2[index[0]][index[1]][index[2]] = d;
         }
         reader.close();

      }
      timer.end("End reading all data");

      Assert.assertEquals(values2.length, PopuForScalarField.NI);
      Assert.assertEquals(values2[0].length, PopuForScalarField.NJ);
      Assert.assertEquals(values2[0][0].length, PopuForScalarField.NK);

      int count = 0;
      for (int i = 0; i < values2.length; i++) {
         for (int j = 0; j < values2[0].length; j++) {
            for (int k = 0; k < values2[0][0].length; k++) {
               if (values2[i][j][k] != count) {
                  System.out.println("i=" + i + " j=" + j + " k=" + k);
                  Assert.assertEquals(values2[i][j][k], count, 10e-9);
               }
               count++;
            }
         }
      }
   }

   // @Test
   // public void testGetSub() throws Exception {
   // double[][][] values = PopuForScalarField.instantiateArray();
   //
   // int ni = PopuForScalarField.NI;
   // int nj = PopuForScalarField.NJ;
   // int nk = PopuForScalarField.NK;
   //
   // int[] fromPos = new int[] { ni / 4, nj / 4, nk / 4 };
   // int[] lengths = new int[] { ni / 10 + 1, nj / 10 + 1, nk / 10 + 1 };
   //
   // double[][][] subvalues = new double[lengths[0]][lengths[1]][lengths[2]];
   // for (int i = 0; i < lengths[0]; i++) {
   // for (int j = 0; j < lengths[1]; j++) {
   // for (int k = 0; k < lengths[2]; k++) {
   // // double v = (i + fromPos[0]) * (j + fromPos[1]) * (k +
   // // fromPos[2]);
   // double v = values[i + fromPos[0]][j + fromPos[1]][k + fromPos[2]];
   // // System.out.println("value=" + v);
   // subvalues[i][j][k] = v;
   // }
   // }
   // }
   //
   // values = null;
   //
   // EntityManagerFactory emf = pm.getEntityManagerFactory();
   // EntityManager em = emf.createEntityManager();
   // Assert.assertTrue(em.isOpen());
   //
   // IDefaultDao<Node, Long> nodeDao = new DefaultDaoJPA<Node, Long>(Node.class, em);
   //
   // Map<String, Object> map = new HashMap<String, Object>();
   // map.put("name", "blobby");
   // List<Node> nodes = nodeDao.find(map);
   // Node node = nodes.iterator().next();
   //
   // Map<String, SplitBlob> adapter = node.getFields();
   // SplitBlob sb = adapter.get(MeasConstants.PROROSITY.toString());
   //
   // MyTimer timer = new MyTimer();
   // double[][][] results = sb.getValues(double[][][].class, fromPos, lengths);
   // timer.end("End reading sub data");
   //
   // Assert.assertEquals(results.length, lengths[0]);
   // Assert.assertEquals(results[0].length, lengths[1]);
   // Assert.assertEquals(results[0][0].length, lengths[2]);
   //
   // for (int i = 0; i < subvalues.length; i++) {
   // for (int j = 0; j < subvalues[0].length; j++) {
   // for (int k = 0; k < subvalues[0][0].length; k++) {
   // // System.out.println("i=" + i + " j=" + j + " k=" + k);
   // Assert.assertEquals(results[i][j][k], subvalues[i][j][k], 10e-9);
   // }
   // }
   // }
   // }

   public static void main(String[] args) {
      EntityManager em = AbstractTestDbPopu.initEm();
      cleanUpTables(em);

      MyTimer timer = new MyTimer();
      DbUseCase dbUc = new DbUseCase() {
         @Override
         protected void reallyExecute(EntityManager em) throws DbUseCaseException {
            try {
               PopuForScalarField popu = new PopuForScalarField(em);
               popu.execute();
            } catch (ProcedureException e) {
               throw new DbUseCaseException(e);
            }
         }
      };
      dbUc.execute(em, true);
      timer.end("End populating");
   }

   private static class PopuForScalarField implements IPopulator {
      protected static Logger LOGGER = Logger.getLogger(PopuForScalarField.class);

      // 300
      public static final int NI = 300;

      // 400
      public static final int NJ = 400;

      // 500
      public static final int NK = 500;

      private EntityManager currentEm;

      private IListener<SplitBlobEvent> listener;

      public static int mode = 3;

      public PopuForScalarField(EntityManager currentEm) {
         this.currentEm = currentEm;
      }

      public EntityManager getEm() {
         return currentEm;
      }

      public static double[][][] instantiateArray() {
         double[][][] values = new double[NI][NJ][NK];
         int count = 0;
         for (int i = 0; i < NI; i++) {
            for (int j = 0; j < NJ; j++) {
               for (int k = 0; k < NK; k++) {
                  values[i][j][k] = count++;
               }
            }
         }
         return values;
      }

      private void initListener() {
         listener = new IListener<SplitBlobEvent>() {

            private List<SplitBlobPart> previous = new ArrayList<SplitBlobPart>(100);

            @Override
            public void update(SplitBlobEvent o, Object arg) {
               // First time previousPart = null
               if (o.getOldPart() != null) {
                  previous.add(o.getOldPart());
               }
               if (previous.size() == 10) {
                  LOGGER.info("<Popu> OhOh, heavy blob, managing parts!");
                  MyTimer tc = new MyTimer();
                  getEm().getTransaction().commit();
                  getEm().getTransaction().begin();
                  tc.end("End commit part");

                  MyTimer te = new MyTimer();
                  JpaSpiActions4Test.getInstance().evictAll(currentEm, previous);
                  te.end("End evict part");

                  previous = new ArrayList<SplitBlobPart>(100);
               }
            }
         };
      }

      private void doIt1() {
         Node superRoot = Node.newRoot("blobby");

         int[] shape = new int[] { NI, NJ, NK };
         final SplitBlob sblob = SplitBlob.newInstance(Double.TYPE, shape);

         AdapterToMap<String, SplitBlob, NodeField> adapter = superRoot.getFields();
         adapter.put(MeasConstants.PROROSITY.toString(), sblob);

         currentEm.persist(superRoot);
         currentEm.getTransaction().commit();
         currentEm.getTransaction().begin();

         initListener();

         double[][][] values = instantiateArray();
         sblob.setValues(values, listener);
      }

      private void doIt2() {
         Node superRoot = Node.newRoot("blobby");

         int[] shape = new int[] { NI, NJ, NK };
         final SplitBlob sblob = SplitBlob.newInstance(Double.TYPE, shape);

         AdapterToMap<String, SplitBlob, NodeField> adapter = superRoot.getFields();
         adapter.put(MeasConstants.PROROSITY.toString(), sblob);

         currentEm.persist(superRoot);
         currentEm.getTransaction().commit();
         currentEm.getTransaction().begin();

         initListener();

         double[][][] values = instantiateArray();
         IBlobPartAccessor accessor = sblob.getAccessor(listener);
         IndexIterator ii = new IndexIterator(SplitBlobUtils.getShape(values, 3));
         while (ii.notDone()) {
            int[] index = ii.value();
            accessor.setDouble(index, values[index[0]][index[1]][index[2]]);
            ii.incr();
         }
         accessor.close();
      }

      private void doIt3() {
         Node superRoot = Node.newRoot("blobby");

         int[] shape = new int[] { NI, NJ, NK };
         final SplitBlob sblob = SplitBlob.newInstance(Double.TYPE, shape);

         AdapterToMap<String, SplitBlob, NodeField> adapter = superRoot.getFields();
         adapter.put(MeasConstants.PROROSITY.toString(), sblob);

         currentEm.persist(superRoot);
         currentEm.getTransaction().commit();
         currentEm.getTransaction().begin();

         initListener();

         int count = 0;
         IBlobPartAccessor accessor = sblob.getAccessor();
         accessor.setPartListener(listener);
         IndexIterator ii = new IndexIterator(shape);
         for (; ii.notDone(); ii.incr()) {
            int[] index = ii.value();
            accessor.setDouble(index, count++);
         }
         accessor.close();
      }

      @Override
      public boolean execute() throws ProcedureException {
         if (mode == 1) {
            doIt1(); // ~ 115s
         } else if (mode == 2) {
            doIt2(); // ~ 100S
         } else if (mode == 3) {
            doIt3(); // ~ 88s
         }
         return true;
      }
   }
}
