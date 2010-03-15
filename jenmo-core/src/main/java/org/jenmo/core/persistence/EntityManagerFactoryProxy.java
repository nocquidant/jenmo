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

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

/**
 * Instead of creating one class for every proxy we have chosen to create an abstract proxy class to
 * be used as a generic multi-purpose base class, and we have provided the actual proxy as a
 * concrete class extended from the the abstract proxy.
 * 
 * @author Nicolas Ocquidant (thanks to puncherico)
 * @since 1.0
 */
abstract class EntityManagerFactoryProxy implements EntityManagerFactory {

   protected final EntityManagerFactory delegate;

   protected EntityManagerFactoryProxy(EntityManagerFactory emf) {
      this.delegate = emf;
   }

   public EntityManager createEntityManager() {
      return delegate.createEntityManager();
   }

   @SuppressWarnings("rawtypes")
   public EntityManager createEntityManager(Map map) {
      return delegate.createEntityManager(map);
   }

   public boolean isOpen() {
      return delegate.isOpen();
   }

   public void close() {
      delegate.close();
   }

   @Override
   public Cache getCache() {
      return delegate.getCache();
   }

   @Override
   public Map<String, Object> getProperties() {
      return delegate.getProperties();
   }

   @Override
   public Metamodel getMetamodel() {
      return delegate.getMetamodel();
   }

   @Override
   public CriteriaBuilder getCriteriaBuilder() {
      return delegate.getCriteriaBuilder();
   }

   @Override
   public PersistenceUnitUtil getPersistenceUnitUtil() {
      return delegate.getPersistenceUnitUtil();
   }
}