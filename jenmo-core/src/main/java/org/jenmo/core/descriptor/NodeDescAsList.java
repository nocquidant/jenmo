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
package org.jenmo.core.descriptor;

import java.util.Map;
import java.util.Set;

import org.jenmo.core.adapter.AdapterCmd;
import org.jenmo.core.adapter.AdapterFactory;
import org.jenmo.core.adapter.IAdapterCmd;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToList;
import org.jenmo.core.cache.JenmoCache;
import org.jenmo.core.constant.JenmoConstant;
import org.jenmo.core.descriptor.INodeDescriptor.INodeDescAsList;
import org.jenmo.core.domain.Edge;
import org.jenmo.core.domain.Node;

/**
 * Package access level class. Implementation of {@link INodeDescAsList}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
class NodeDescAsList implements INodeDescAsList {

   @Override
   public AdapterToList<Node, Edge> instantiateAdapter(final Node node,
         final INodeCallback<Edge> callbacks) {
      IAdapterCmd<Integer, Node, Edge> cmd = new AdapterCmd<Integer, Node, Edge>() {
         @Override
         public boolean accept(Edge arg) {
            return callbacks.accept(arg);
         }

         @Override
         public Integer getIndex(Edge arg) {
            return Integer.parseInt(arg.getIndex());
         }

         @Override
         public Edge setIndex(Integer index, Edge arg) {
            String key = JenmoCache.getInstance().getIndexAsString(index);
            callbacks.setIndex(key, arg);
            return arg;
         }

         @Override
         public Node getValue(Edge arg) {
            return arg.getTarget();
         }

         @Override
         public Edge instantiateAndAdd(Integer index, Node output, Set<? super Edge> target) {
            String key = String.valueOf(index);
            Edge newInstance = Edge.newInstance(node, output, key);
            callbacks.postNew(newInstance);
            if (target != null) {
               target.add(newInstance);
            }
            return newInstance;
         }

         @Override
         public void postNewlyCreated(Map<JenmoConstant, Object> args) {
            callbacks.postNewAttributes(args);
         }
      };
      return AdapterFactory.list(cmd);
   }
}
