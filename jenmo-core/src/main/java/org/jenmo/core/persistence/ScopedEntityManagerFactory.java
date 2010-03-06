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
 * Modifications to genuine file: none
 */
package org.jenmo.core.persistence;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * The factory of LazyCloseEntityManager instances. This is the only class using the ThreadLocal
 * class. Note it is notified when each LazyCloseEntityManager instance is really closed.
 * 
 * @author Nicolas Ocquidant (thanks to puncherico)
 * @since 1.0
 */
public class ScopedEntityManagerFactory extends EntityManagerFactoryProxy implements
      LazyCloseListener {

   private final ThreadLocal<LazyCloseEntityManager> threadLocal;

   protected ScopedEntityManagerFactory(EntityManagerFactory emf) {
      super(emf);
      this.threadLocal = new ThreadLocal<LazyCloseEntityManager>();
   }

   @SuppressWarnings("unchecked")
   public EntityManager createEntityManager(Map map) {
      LazyCloseEntityManager em = threadLocal.get();
      if (em == null) {
         em = new LazyCloseEntityManager(super.createEntityManager(map));
         createEntityManager(em);
      }
      return em;
   }

   public EntityManager createEntityManager() {
      LazyCloseEntityManager em = threadLocal.get();
      if (em == null) {
         em = new LazyCloseEntityManager(super.createEntityManager());
         createEntityManager(em);
      }
      return em;
   }

   private void createEntityManager(LazyCloseEntityManager em) {
      threadLocal.set(em);
      em.setLazyCloseListener(this);
   }

   protected LazyCloseEntityManager getEntityManager() {
      return threadLocal.get();
   }

   public void lazilyClosed() {
      threadLocal.set(null);
   }
}