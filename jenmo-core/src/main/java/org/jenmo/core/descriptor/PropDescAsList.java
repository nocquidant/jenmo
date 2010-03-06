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

import java.util.Set;

import org.jenmo.core.adapter.AdapterCmd;
import org.jenmo.core.adapter.AdapterFactory;
import org.jenmo.core.adapter.IAdapterCmd;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToList;
import org.jenmo.core.cache.JenmoCache;
import org.jenmo.core.descriptor.IPropertyDescriptor.IPropDescAsList;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeProperty;
import org.jenmo.core.domain.Property;
import org.jenmo.core.util.CastUtils;

/**
 * Package access level class. Implementation of {@link IPropDescAsList<T>}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
class PropDescAsList<T> implements IPropDescAsList<T> {

   private Class<T> clazz;

   public PropDescAsList(Class<T> clazz) {
      this.clazz = clazz;
   }

   @Override
   public AdapterToList<T, NodeProperty> instantiateAdapter(final Node node, final Property prop,
         final INodeCallback<NodeProperty> callbacks) {
      IAdapterCmd<Integer, T, NodeProperty> cmd = new AdapterCmd<Integer, T, NodeProperty>() {

         @Override
         public boolean accept(NodeProperty arg) {
            return (arg.getProperty() == prop);
         }

         @Override
         public Integer getIndex(NodeProperty arg) {
            return Integer.parseInt(arg.getIndex());
         }

         @Override
         public NodeProperty setIndex(Integer index, NodeProperty arg) {
            String key = JenmoCache.getInstance().getIndexAsString(index);
            callbacks.setIndex(key, arg);
            return arg;
         }

         @Override
         public T getValue(NodeProperty arg) {
            return CastUtils.fromString(clazz, arg.getValue());
         }

         @Override
         public NodeProperty instantiateAndAdd(Integer index, T value,
               Set<? super NodeProperty> target) {
            String key = String.valueOf(index);
            String val = CastUtils.toString(value);
            NodeProperty newInstance = NodeProperty.newInstance(node, prop, key, val);
            if (target != null) {
               target.add(newInstance);
            }
            return newInstance;
         }
      };
      return AdapterFactory.list(cmd);
   }
}
