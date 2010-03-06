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
import java.util.HashMap;
import java.util.Map;

import org.jenmo.common.localizer.Localizer;
import org.jenmo.common.util.IFunction.FunctionException;
import org.jenmo.common.util.IProcedure.IProcedure0;
import org.jenmo.common.util.IProcedure.ProcedureException;
import org.jenmo.core.domain.Edge;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeField;
import org.jenmo.core.domain.NodeProperty;
import org.jenmo.core.domain.NodeRevision;
import org.jenmo.core.domain.NodeType;
import org.jenmo.core.domain.Property;
import org.jenmo.core.domain.SplitBlob;
import org.jenmo.core.domain.SplitBlobPart;
import org.jenmo.core.repository.delete.NodeDeleteAction;
import org.jenmo.core.repository.delete.NodeTypeDeleteAction;
import org.jenmo.core.repository.delete.PropertyDeleteAction;

/**
 * Factory for delete operation implementations.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
class DeleteActionFactory {
   private static final Localizer LOC = Localizer.forPackage(DeleteActionFactory.class);

   // Lazy loading...
   private static class CheckHolder {
      private static final Map<Class<?>, IProcedure0> CHECKS = new HashMap<Class<?>, IProcedure0>();
      static {
         CHECKS.put(SplitBlobPart.class, new IProcedure0() {
            public boolean execute() throws ProcedureException {
               throw new UnsupportedOperationException(LOC.get("REMOVE_FROM_PARENT_$2",
                     SplitBlobPart.class, SplitBlob.class).getMessage());
            }
         });
         CHECKS.put(SplitBlob.class, new IProcedure0() {
            public boolean execute() throws ProcedureException {
               throw new UnsupportedOperationException(LOC.get("REMOVE_FROM_PARENT_$2",
                     SplitBlob.class, NodeField.class).getMessage());
            }
         });
         CHECKS.put(NodeField.class, new IProcedure0() {
            public boolean execute() throws ProcedureException {
               throw new UnsupportedOperationException(LOC.get("REMOVE_FROM_PARENT_$2",
                     NodeField.class, Node.class).getMessage());
            }
         });
         CHECKS.put(NodeProperty.class, new IProcedure0() {
            public boolean execute() throws ProcedureException {
               throw new UnsupportedOperationException(LOC.get("REMOVE_FROM_PARENT_$2",
                     NodeProperty.class, Node.class).getMessage());
            }
         });
         CHECKS.put(Edge.class, new IProcedure0() {
            public boolean execute() throws ProcedureException {
               throw new UnsupportedOperationException(LOC.get("REMOVE_FROM_PARENT_$2", Edge.class,
                     Node.class).getMessage());
            }
         });
         CHECKS.put(NodeRevision.class, new IProcedure0() {
            public boolean execute() throws ProcedureException {
               throw new UnsupportedOperationException(LOC.get("REMOVE_FROM_PARENT_$2",
                     NodeRevision.class, Node.class).getMessage());
            }
         });
      };
   }

   // Lazy loading...
   private static class DeleteHolder {
      private static final Map<Class<?>, IDelFunction<?, ?>> DELETES = new HashMap<Class<?>, IDelFunction<?, ?>>();

      // Init maps
      static {
         DELETES.put(Property.class, new IDelFunction<Property, Long>() {
            @SuppressWarnings("unchecked")
            public IDeleteAction<Property, Long> execute(DefaultDaoJPA<?, ?> arg)
                  throws FunctionException {
               return new PropertyDeleteAction((DefaultDaoJPA<Property, Long>) arg);
            }
         });
         DELETES.put(NodeType.class, new IDelFunction<NodeType, Long>() {
            @SuppressWarnings("unchecked")
            public IDeleteAction<NodeType, Long> execute(DefaultDaoJPA<?, ?> arg)
                  throws FunctionException {
               return new NodeTypeDeleteAction((DefaultDaoJPA<NodeType, Long>) arg);
            }
         });
         DELETES.put(Node.class, new IDelFunction<Node, Long>() {
            @SuppressWarnings("unchecked")
            public IDeleteAction<Node, Long> execute(DefaultDaoJPA<?, ?> arg)
                  throws FunctionException {
               return new NodeDeleteAction((DefaultDaoJPA<Node, Long>) arg);
            }
         });
      }
   }

   protected DeleteActionFactory() {
   }

   private static void checkForDeletion(DefaultDaoJPA<?, ?> dao) {
      IProcedure0 proc = CheckHolder.CHECKS.get(dao.getType());
      if (proc != null) {
         proc.execute(); // forbidden
      }
   }

   /**
    * Creates a {@link IDeleteAction} object for the given dao.
    */
   @SuppressWarnings("unchecked")
   public static <T, PK extends Serializable> IDeleteAction<T, PK> createDeleteAction(
         DefaultDaoJPA<T, PK> dao) {
      checkForDeletion(dao);
      IDelFunction<?, ?> func = DeleteHolder.DELETES.get(dao.getType());
      return (IDeleteAction<T, PK>) func.execute(dao);
   }

   private static interface IDelFunction<U, V extends Serializable> {
      IDeleteAction<U, V> execute(DefaultDaoJPA<?, ?> arg) throws FunctionException;
   }
}
