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
import org.jenmo.core.adapter.AbstractAdapter.AdapterToSet;
import org.jenmo.core.constant.JenmoConstant;
import org.jenmo.core.descriptor.INodeDescriptor.INodeDescAsSet;
import org.jenmo.core.domain.Edge;
import org.jenmo.core.domain.Node;

/**
 * Package access level class. Implementation of {@link INodeDescAsSet}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
class NodeDescAsSet implements INodeDescAsSet {

   @Override
   public AdapterToSet<Node, Edge> instantiateAdapter(final Node node,
         final INodeCallback<Edge> callbacks) {
      IAdapterCmd<Object, Node, Edge> cmd = new AdapterCmd<Object, Node, Edge>() {

         @Override
         public boolean accept(Edge arg) {
            return callbacks.accept(arg);
         }

         @Override
         public Node getValue(Edge arg) {
            return arg.getTarget();
         }

         @Override
         public Edge instantiateAndAdd(Object index, Node output, Set<? super Edge> target) {
            Edge newInstance = Edge.newInstance(node, output);
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
      return AdapterFactory.set(cmd);
   }
}
