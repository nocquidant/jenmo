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

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.jenmo.core.testutil.JpaSpiActions4Test;
import org.junit.Assert;

// Just to factorize some stuff
public abstract class AbstractTestDb {
   protected static final Logger LOGGER = Logger.getLogger(AbstractTestDb.class);

   // private static final int ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;
   private static final int ISOLATION_LEVEL = Connection.TRANSACTION_REPEATABLE_READ;

   protected static EntityManagerFactory emf = Persistence.createEntityManagerFactory("JenmoPU");

   protected static EntityManager initEm() {
      EntityManager em = emf.createEntityManager();
      Assert.assertTrue(em.isOpen());

      Connection conn = JpaSpiActions4Test.getInstance().getConnection(em);

      // Can't set transaction isolation in persistence.xml for eclipselink
      try {
         conn.setTransactionIsolation(ISOLATION_LEVEL);
      } catch (SQLException e) {
         throw new RuntimeException(e);
      }

      // Force autocommit to false
      try {
         conn.setAutoCommit(false);

      } catch (SQLException e) {
         throw new RuntimeException(e);
      }

      return em;
   }

   protected static void closeEm(EntityManager em) {
      if (em != null) {
         try {
            if (em.getTransaction().isActive() == true) {
               em.getTransaction().commit();
            }

            // Needed for EclipseLink?!
            Connection conn = JpaSpiActions4Test.getInstance().getConnection(em);
            try {
               conn.commit();
            } catch (SQLException e) {
               e.printStackTrace();
            }
         } finally {
            em.close();
            em = null;
         }
      }
   }

   protected static void txBegin(EntityManager em) {
      if (em.getTransaction().isActive() == true) {
         em.getTransaction().commit();
      }
      em.getTransaction().begin();
   }

   protected static void txCommit(EntityManager em) {
      if (em.getTransaction().isActive() == true) {
         em.getTransaction().commit();
      }
      em.getTransaction().begin();
   }

   protected static void cleanUpTables(EntityManager em) {
      txBegin(em);

      // Should read table names from output.sql
      em.createNativeQuery("delete from EDGE").executeUpdate();
      em.createNativeQuery("delete from NODE").executeUpdate();
      em.createNativeQuery("delete from NODEFIELD").executeUpdate();
      em.createNativeQuery("delete from NODEPROPERTY").executeUpdate();
      em.createNativeQuery("delete from NODEREVISION").executeUpdate();
      em.createNativeQuery("delete from NODETYPE").executeUpdate();
      em.createNativeQuery("delete from PROPERTY").executeUpdate();
      em.createNativeQuery("delete from PROPERTY_NODETYPE").executeUpdate();
      em.createNativeQuery("delete from SPLITBLOB").executeUpdate();
      em.createNativeQuery("delete from SPLITBLOBPART").executeUpdate();

      txCommit(em);
   }
}
