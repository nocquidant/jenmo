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
package org.jenmo.core.adapter;

import java.util.Map;
import java.util.Set;

import org.jenmo.core.constant.JenmoConstant;
import org.jenmo.core.domain.Edge;
import org.jenmo.core.domain.Node;

/**
 * Such a command is instantiated by descriptors (or directly by domain objects as {@link Node}) and
 * manipulated by adapters. It contains the specific part which cannot be handled by adapters.
 * 
 * @param <I>
 *           Index, may be irrelevant
 * @param <T>
 *           Wanted type by the client code
 * @param <G>
 *           Genuine type the domain object really handle
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public interface IAdapterCmd<I, T, G> {

   /**
    * To know if the adapter, which hold this command, should manage the given genuine object or
    * not.
    */
   boolean accept(G arg);

   /**
    * Gets the index for the given genuine object if it exists, <code>null</code> if irrelevant.
    */
   I getIndex(G arg);

   /**
    * Sets the given index for the given genuine object, if index is relevant.
    */
   G setIndex(I index, G arg);

   /**
    * Gets the value associated with given the genuine object, as T.
    */
   T getValue(G arg);

   /**
    * Instantiates a new genuine object regarding the given value (and index if relevant i.e. not
    * {@code null}). Then add it to the given {@link Set} target.
    */
   G instantiateAndAdd(I index, T value, Set<? super G> target);

   /**
    * A call to an adapter <code>add()</code> or <code>put()</code> methods must sometimes be
    * followed by additional operations to complete the creational process. A typical use is when
    * you want to specify the {@link Edge#cascaded} field for a link between {@link Node}s.
    * <p>
    * See keys in {@link JenmoConstant} for the given map.
    */
   void postNewlyCreated(Map<JenmoConstant, Object> args);

   boolean remove(G arg, Set<? super G> target);
}
