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
import org.jenmo.core.adapter.AbstractAdapter.AdapterToMap;
import org.jenmo.core.descriptor.IPropertyDescriptor.IPropDescAsMap;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeProperty;
import org.jenmo.core.domain.Property;
import org.jenmo.core.util.CastUtils;

/**
 * Package access level class. Implementation of {@link IPropDescAsMap<K,V>}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
class PropDescAsMap<K, V> implements IPropDescAsMap<K, V> {
   private Class<K> clazzKey;

   private Class<V> clazzVal;

   public PropDescAsMap(Class<K> clazzKey, Class<V> clazzVal) {
      this.clazzKey = clazzKey;
      this.clazzVal = clazzVal;
   }

   @Override
   public AdapterToMap<K, V, NodeProperty> instantiateAdapter(final Node node, final Property prop,
         final INodeCallback<NodeProperty> callbacks) {
      IAdapterCmd<K, V, NodeProperty> cmd = new AdapterCmd<K, V, NodeProperty>() {

         @Override
         public boolean accept(NodeProperty arg) {
            return (arg.getProperty() == prop);
         }

         @Override
         public K getIndex(NodeProperty arg) {
            return CastUtils.fromString(clazzKey, arg.getIndex());
         }

         @Override
         public V getValue(NodeProperty arg) {
            return CastUtils.fromString(clazzVal, arg.getValue());
         }

         @Override
         public NodeProperty instantiateAndAdd(K index, V value, Set<? super NodeProperty> target) {
            String key = String.valueOf(index);
            String val = CastUtils.toString(value);
            NodeProperty newInstance = NodeProperty.newInstance(node, prop, key, val);
            if (target != null) {
               target.add(newInstance);
            }
            return newInstance;
         }
      };
      return AdapterFactory.map(cmd);
   }
}
