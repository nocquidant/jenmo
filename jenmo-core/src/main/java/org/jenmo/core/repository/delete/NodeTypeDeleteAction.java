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

import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeType;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.DefaultDeleteAction;

/**
 * Delete operation for {@link NodeType} domain object.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class NodeTypeDeleteAction extends DefaultDeleteAction<NodeType, Long> {
   private static final String ENTITY_NODE = Node.class.getSimpleName();
   private static final String ENTITY_NODETYPE = NodeType.class.getSimpleName();

   private static final String QUERY_NODE = "SELECT n FROM " + ENTITY_NODE
         + " n JOIN n.nodeType nt WHERE nt.id = :theId ";
   private static final String QUERY_NODETYPE = "SELECT t FROM " + ENTITY_NODETYPE
         + " t WHERE t.id = :theId ";

   public NodeTypeDeleteAction(DefaultDaoJPA<NodeType, Long> caller) {
      super(caller);
   }

   private EntityManager getEntityManager() {
      return getCaller().getEntityManager();
   }

   @Override
   public void delete(Long id) {
      // Test whether or not a Node still references the Property
      // If yes reject throwing an IllegalStateException
      String queryString = QUERY_NODE;
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(queryString);
      }
      Query query = getEntityManager().createQuery(queryString);
      query.setParameter("theId", id);
      query.setMaxResults(1);
      if (query.getResultList().isEmpty() == false) {
         throw new IllegalDeleteException("Please, delete all Nodes using the NodeType first "
               + "or set another NodeType to them");
      }

      // Decision by the JPA expert group to require that relationships must be explicitly
      // managed by the user, regardless of whether the other side has even been
      // brought into the EntityManager's context.
      // => Entry in PROPERTY_NODETYPE will be deleted in database but stale
      // Property objects could stay in the EntityManager
      // You may have to explicitly call evict on those Property objects:
      // OpenJPAPersistence.cast(getEntityManager()).evict(property)

      // Delete NodeType
      queryString = QUERY_NODETYPE;
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(queryString);
      }
      query = getEntityManager().createQuery(queryString);
      query.setParameter("theId", id);
      NodeType current = (NodeType) query.getSingleResult();
      getEntityManager().remove(current);
   }
}
