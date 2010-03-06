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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jenmo.core.adapter.IAdapter;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToList;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToMap;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToSet;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToSingle;
import org.jenmo.core.domain.Edge;
import org.jenmo.core.domain.Node;

/**
 * A descriptor to describe {@link Node} output relationships, and to define the corresponding
 * {@link IAdapter} form.
 * 
 * @param <A>
 *           Wanted adapter type
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public interface INodeDescriptor<A extends IAdapter<Edge>> extends IDescriptor<A, Edge> {
   /**
    * Instantiates the {@link IAdapter} object for the given {@link Node}. The {@code callbacks}
    * parameter is used to execute callback methods in {@link Node} class.
    */
   A instantiateAdapter(Node node, INodeCallback<Edge> callbacks);

   /**
    * A descriptor to describe {@link Node} output relationships as {@link List} of {@link Node}
    * elements.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface INodeDescAsList extends INodeDescriptor<AdapterToList<Node, Edge>> {
   }

   /**
    * A descriptor to describe {@link Node} output relationships as {@link Map} of (K,{@link Node})
    * elements.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface INodeDescAsMap<K> extends INodeDescriptor<AdapterToMap<K, Node, Edge>> {

   }

   /**
    * A descriptor to describe {@link Node} output relationships as {@link Set} of {@link Node}
    * elements.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface INodeDescAsSet extends INodeDescriptor<AdapterToSet<Node, Edge>> {

   }

   /**
    * A descriptor to describe {@link Node} output relationships as single {@link Node} element.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface INodeDescAsSingle extends INodeDescriptor<AdapterToSingle<Node, Edge>> {

   }
}
