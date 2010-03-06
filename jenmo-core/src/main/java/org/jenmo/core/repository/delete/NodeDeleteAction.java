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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jenmo.core.domain.Edge;
import org.jenmo.core.domain.Node;
import org.jenmo.core.orm.JpaSpiActions;
import org.jenmo.core.repository.DefaultDaoJPA;
import org.jenmo.core.repository.DefaultDeleteAction;

/**
 * Delete operation for {@link Node} domain object.
 * <p>
 * The deletion operation is only effective if the {@link Node} isn't references by other
 * {@link Node}s. Cascade delete children {@link Node}s if possible.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class NodeDeleteAction extends DefaultDeleteAction<Node, Long> {
   private static final String ENTITY_EDGE = Edge.class.getSimpleName();
   private static final String ENTITY_NODE = Node.class.getSimpleName();

   private static final String QUERY_NODE = "SELECT n FROM " + ENTITY_NODE
         + " n WHERE n.id = :theId";
   private static final String QUERY_OUTPUTS = "SELECT n FROM " + ENTITY_EDGE
         + " e JOIN e.to AS n WHERE e.from = :theNode AND e.cascaded = " + true;

   public NodeDeleteAction(DefaultDaoJPA<Node, Long> caller) {
      super(caller);
   }

   private EntityManager getEntityManager() {
      return getCaller().getEntityManager();
   }

   private Set<Node> fetchOutputs(Node current, Set<Node> previous) {
      if (previous.contains(current)) {
         // Already fetched
         return previous;
      }
      previous.add(current);

      // Edges (inputs and outputs) are ElementDependent in Nodes
      // Useless to delete them here

      // Decision by the JPA expert group to require that relationships must be explicitly
      // managed by the user, regardless of whether the other side has even been
      // brought into the EntityManager's context.
      // => Entry in EDGE will be deleted in database but stale
      // Node objects could stay in the EntityManager
      // You may have to explicitly call evict on those Node objects:
      // OpenJPAPersistence.cast(getEntityManager()).evict(node)

      // Get all outputs from the current Node
      String queryString = QUERY_OUTPUTS;
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(queryString);
      }
      Query query = getEntityManager().createQuery(queryString);
      query.setParameter("theNode", current);
      List<?> outputs = query.getResultList();
      for (Object output : outputs) {
         fetchOutputs((Node) output, previous);
      }

      return previous;
   }

   @Override
   public void delete(Long id) {
      // Select Node from id
      String queryString = QUERY_NODE;
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(queryString);
      }
      Query query = getEntityManager().createQuery(queryString);
      query.setParameter("theId", id);
      Node current = (Node) query.getSingleResult();
      Set<Node> toBeDeleted = fetchOutputs(current, new HashSet<Node>(1000));
      // Try a group delete
      JpaSpiActions.getInstance().removeAll(getEntityManager(), toBeDeleted);
   }
}