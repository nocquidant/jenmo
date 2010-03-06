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
package org.jenmo.core.cache;

import org.jenmo.common.cache.SoftValueMap;
import org.jenmo.core.domain.Property;

/**
 * A singleton pattern to centralize data caching for Jenmo.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public final class JenmoCache {
   /** The singleton */
   private static final JenmoCache singleton = new JenmoCache();

   /** Cache string representation for a given {@link Property}. */
   private CacheObject<Property, String> cacheForProps = new CacheObject<Property, String>() {
      protected String k2v(Property k) {
         return k.getName();
      }
   };

   /** Cache string representation for a given index (i.e. {@link Integer}). */
   private CacheObject<Integer, String> cacheForIdxs = new CacheObject<Integer, String>() {
      @Override
      protected String k2v(Integer k) {
         return k.toString();
      }
   };

   private JenmoCache() {

   }

   public static JenmoCache getInstance() {
      return singleton;
   }

   public String getPropertyAsString(Property prop) {
      return cacheForProps.get(prop);
   }

   public String getIndexAsString(Integer index) {
      return cacheForIdxs.get(index);
   }

   /**
    * Cache objects using {@link SoftValueMap}. The values are stored as soft references and may
    * become garbage collected if not actively being used.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    * 
    * @param <K>
    * @param <V>
    */
   private static abstract class CacheObject<K, V> {
      /** The cache */
      private SoftValueMap<K, V> cache = new SoftValueMap<K, V>();

      /**
       * Gets cached object for the given key. Cached the value before returning if not yet in cache
       * using the {@link #k2v(Object)} method.
       */
      public V get(K key) {
         V v = cache.get(key);
         if (v == null) {
            v = k2v(key);
            cache.put(key, v);
         }
         return v;
      }

      protected abstract V k2v(K k);
   }
}
