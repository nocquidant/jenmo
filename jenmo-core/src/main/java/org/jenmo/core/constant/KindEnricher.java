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
package org.jenmo.core.constant;

import java.util.Map;

/**
 * In order to populate {@link KindConstants}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public abstract class KindEnricher {
   public static final String ALL_KEY = "ALL";
   public static final Integer ALL_VALUE = Integer.MIN_VALUE;

   public void enrich(KindConstants constants) {
      Map<String, Integer> kinds = getAllKinds();
      if (kinds.containsKey(ALL_KEY)) {
         throw new IllegalArgumentException("Key collision");
      }
      constants.getInternalMap().putAll(getAllKinds());
   }

   protected abstract Map<String, Integer> getAllKinds();
}
