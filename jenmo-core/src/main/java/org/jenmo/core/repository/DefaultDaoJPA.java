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
package org.jenmo.core.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jenmo.common.localizer.Localizer;
import org.jenmo.common.repository.BaseDaoJpa;

/**
 * {@link IDefaultDao} implementation using a JPA {@link EntityManager}.
 * 
 * @param <T>
 *           Type of persistent entity
 * @param <PK>
 *           Type of primary key
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class DefaultDaoJPA<T, PK extends Serializable> extends BaseDaoJpa<T, PK> implements
      IDefaultDao<T, PK> {
   private static final Localizer LOC = Localizer.forPackage(DefaultDaoJPA.class);

   private EntityManager entityManager;

   public DefaultDaoJPA(final Class<T> aType) {
      super(aType);
   }

   public DefaultDaoJPA(final Class<T> aType, EntityManager entityManager) {
      super(aType);
      this.entityManager = entityManager;
   }

   @Override
   public EntityManager getEntityManager() {
      return entityManager;
   }

   // For DefaultDeleteAction to avoid loop cause by polymorphism
   protected void defaultDelete(PK id) {
      super.delete(id);
   }

   @Override
   public void setEntityManager(Object entityManager) {
      if ((entityManager instanceof EntityManager) == false) {
         throw new IllegalArgumentException(LOC.get("ILLEGAL_EM_FOR_DAO_$2", entityManager, this)
               .getMessage());
      }
      EntityManager em = (EntityManager) entityManager;
      LOGGER.debug("setting entityManager - open: " + em.isOpen());
      this.entityManager = em;
   }

   @Override
   public void delete(PK id) {
      DeleteActionFactory.createDeleteAction(this).delete(id);
   }
}
