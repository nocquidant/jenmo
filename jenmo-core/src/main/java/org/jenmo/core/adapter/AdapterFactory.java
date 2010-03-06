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

import org.jenmo.core.adapter.AbstractAdapter.AdapterToList;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToMap;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToSet;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToSingle;

/**
 * A simple factory class for adapters in order to keep some classes at package access level.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class AdapterFactory {
   private AdapterFactory() {

   }

   /**
    * Returns a {@link AdapterToList} for the given command.
    */
   public static <T, G> AdapterToList<T, G> list(IAdapterCmd<Integer, T, G> cmd) {
      return new AdapterToListImpl<T, G>(cmd);
   }

   /**
    * Returns a {@link AdapterToMap} for the given command.
    */
   public static <K, V, G> AdapterToMap<K, V, G> map(IAdapterCmd<K, V, G> cmd) {
      return new AdapterToMapImpl<K, V, G>(cmd);
   }

   /**
    * Returns a {@link AdapterToSet} for the given command.
    */
   public static <T, G> AdapterToSet<T, G> set(IAdapterCmd<?, T, G> cmd) {
      return new AdapterToSetImpl<T, G>(cmd);
   }

   /**
    * Returns a {@link AdapterToSingle} for the given command.
    */
   public static <T, G> AdapterToSingle<T, G> single(IAdapterCmd<?, T, G> cmd) {
      return new AdapterToSingleImpl<T, G>(cmd);
   }
}
