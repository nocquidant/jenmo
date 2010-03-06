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
import org.jenmo.core.domain.Node.ModCounter;

/**
 * Converts a {@link Set} of G objects into an interface expected by the client.
 * 
 * @param <G>
 *           Genuine type the domain object really handle
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public interface IAdapter<G> {

   /**
    * Converts the given target into an interface expected by the client.
    */
   void adapt(Set<G> target);

   /**
    * Converts the given target into an interface expected by the client using {@link ModCounter} to
    * check for structural modifications.
    */
   void adapt(Set<G> target, ModCounter modCounter);

   /**
    * A call to an adapter <code>add()</code> or <code>put()</code> methods must sometimes be
    * followed by additional operations to complete the creational process. A typical use is when
    * you want to specify the {@link Edge#cascaded} field for a link between {@link Node}s.
    * <p>
    * See keys in {@link JenmoConstant} for the given map.
    */
   void postNewlyCreated(Map<JenmoConstant, Object> args);

   /**
    * A shortcut to {@link #postNewlyCreated(Map)}
    */
   void postNewlyCreated(JenmoConstant key, Object value);
}
