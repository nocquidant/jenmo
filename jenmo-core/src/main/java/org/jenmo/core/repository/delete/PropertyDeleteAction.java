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
package org.jenmo.core.repository.delete;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jenmo.core.domain.NodeProperty;
import org.jenmo.core.domain.NodeType;
import org.jenmo.core.domain.Property;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.DefaultDeleteAction;

/**
 * Delete operation for {@link Property} domain object.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class PropertyDeleteAction extends DefaultDeleteAction<Property, Long> {
   private static final String ENTITY_NODEPROPERTY = NodeProperty.class.getSimpleName();
   private static final String ENTITY_NODETYPE = NodeType.class.getSimpleName();
   private static final String ENTITY_PROPERTY = Property.class.getSimpleName();

   private static final String QUERY_NODEPROPERTY = "SELECT np FROM " + ENTITY_NODEPROPERTY
         + " np JOIN np.prop p WHERE p.id = :theId ";
   private static final String QUERY_NODETYPE = "SELECT nt FROM " + ENTITY_NODETYPE
         + " nt JOIN nt.properties" + " p WHERE p.id = :theId ";
   private static final String QUERY_PROPERTY = "SELECT p FROM " + ENTITY_PROPERTY
         + " p WHERE p.id = :theId ";

   public PropertyDeleteAction(DefaultDaoJPA<Property, Long> caller) {
      super(caller);
   }

   private EntityManager getEntityManager() {
      return getCaller().getEntityManager();
   }

   @Override
   public void delete(Long id) {
      // Test whether or not a NodeProperty still references the Property
      // If yes reject throwing an IllegalStateException
      String queryString = QUERY_NODEPROPERTY;
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(queryString);
      }
      Query query = getEntityManager().createQuery(queryString);
      query.setParameter("theId", id);
      query.setMaxResults(1);
      if (query.getResultList().isEmpty() == false) {
         throw new IllegalDeleteException("Please, delete all Nodes using the Property first");
      }

      // Test whether or not a NodeType still references the Property
      // If yes reject throwing an IllegalStateException
      queryString = QUERY_NODETYPE;
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(queryString);
      }
      query = getEntityManager().createQuery(queryString);
      query.setParameter("theId", id);
      query.setMaxResults(1);
      if (query.getResultList().isEmpty() == false) {
         throw new IllegalDeleteException("Please, delete all NodeTypes using the Property first");
      }

      // Really delete Property
      queryString = QUERY_PROPERTY;
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(queryString);
      }
      query = getEntityManager().createQuery(queryString);
      query.setParameter("theId", id);
      Property current = (Property) query.getSingleResult();
      getEntityManager().remove(current);
   }
}