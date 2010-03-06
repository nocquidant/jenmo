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
 * --
 * From http://code.google.com/p/scoped-entitymanager/
 * Modifications to genuine file:
 *   - changed a lot
 */
package org.jenmo.core.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.jenmo.core.config.JenmoConfig;

/**
 * With this class, you can create the EntityManagerFactory instance needed to begin to work with
 * JPA (What you'll obtain from PersistenceManager won't be actually an EntityManagerFactory object
 * but a proxy subclass of this class).
 * 
 * @author Nicolas Ocquidant (thanks to puncherico)
 * @since 1.0
 */
public class PersistenceManager {
   /** The logger. */
   private static final Logger LOGGER = Logger.getLogger(PersistenceManager.class);

   private static final String DEFAULT_PERSISTENCE_UNIT = "JenmoPU";

   private String persistenceUnit = DEFAULT_PERSISTENCE_UNIT;

   private static final PersistenceManager singleton = (JenmoConfig.getInstance().getValue(
         JenmoConfig.OPTION_LAZYCLOSE_PM).getBoolean()) ? new ScopedPersistenceManager()
         : new PersistenceManager();

   private EntityManagerFactory emf;

   protected PersistenceManager() {
   }

   public static PersistenceManager getInstance() {
      return singleton;
   }

   public synchronized EntityManagerFactory getEntityManagerFactory() {
      if (emf == null) {
         emf = createEntityManagerFactory();
         LOGGER.info("Persistence Manager has been initialized");
      }
      return emf;
   }

   public synchronized void closeEntityManagerFactory() {
      if (emf != null) {
         emf.close();
         emf = null;
         LOGGER.info("Persistence Manager has been closed");
      }
   }
   
   // Overridden by ScopedPersistenceManager
   protected EntityManagerFactory createEntityManagerFactory() {
      return Persistence.createEntityManagerFactory(persistenceUnit);
   }
}