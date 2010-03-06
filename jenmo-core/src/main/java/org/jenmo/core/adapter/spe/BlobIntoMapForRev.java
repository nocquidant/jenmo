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
package org.jenmo.core.adapter.spe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jenmo.core.adapter.AbstractAdapter.AdapterToMap;
import org.jenmo.core.domain.Node;
import org.jenmo.core.domain.NodeField;
import org.jenmo.core.domain.NodeRevision;
import org.jenmo.core.domain.SplitBlob;

/**
 * Implementations of {@link Map} of {@link SplitBlob} for {@link NodeRevision}.
 * <p>
 * A {@link NodeRevision} could accept new {@link SplitBlob} but cannot modify {@link SplitBlob} of
 * the revised {@link Node}. Such a behavior is managed by this class.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class BlobIntoMapForRev implements Map<String, SplitBlob> {
   /** Blobs for the revised Node: unmodifiable */
   private AdapterToMap<String, SplitBlob, NodeField> blobsForRevised;

   /** Blobs for the revision Node: modifiable */
   private AdapterToMap<String, SplitBlob, NodeField> blobsForRevision;

   public BlobIntoMapForRev() {

   }

   public void decorate(AdapterToMap<String, SplitBlob, NodeField> blobsForRevised,
         AdapterToMap<String, SplitBlob, NodeField> blobsForRevision) {
      this.blobsForRevised = blobsForRevised;
      this.blobsForRevision = blobsForRevision;
   }

   @Override
   public void clear() {
      // Clear operation only in revision
      blobsForRevision.clear();
   }

   @Override
   public boolean containsKey(Object key) {
      if (blobsForRevision.containsKey(key)) {
         return true;
      }
      return blobsForRevised.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      if (blobsForRevision.containsValue(value)) {
         return true;
      }
      return blobsForRevised.containsValue(value);
   }

   @Override
   public Set<Map.Entry<String, SplitBlob>> entrySet() {
      Set<Map.Entry<String, SplitBlob>> entries1 = blobsForRevision.entrySet();
      Set<Map.Entry<String, SplitBlob>> entries2 = blobsForRevised.entrySet();
      int s = entries1.size() + entries2.size();
      Set<Map.Entry<String, SplitBlob>> out = new HashSet<Map.Entry<String, SplitBlob>>(s);
      out.addAll(entries1);
      out.addAll(entries2);
      return out;
   }

   @Override
   public SplitBlob get(Object key) {
      SplitBlob out = blobsForRevision.get(key);
      if (out != null) {
         return null;
      }
      return blobsForRevised.get(key);
   }

   @Override
   public boolean isEmpty() {
      return (blobsForRevision.isEmpty() && blobsForRevised.isEmpty());
   }

   @Override
   public Set<String> keySet() {
      Set<String> keys1 = blobsForRevision.keySet();
      Set<String> keys2 = blobsForRevised.keySet();
      Set<String> out = new HashSet<String>(keys1.size() + keys2.size());
      out.addAll(keys1);
      out.addAll(keys2);
      return out;
   }

   @Override
   public SplitBlob put(String key, SplitBlob value) {
      if (blobsForRevised.containsKey(key)) {
         if (blobsForRevised.containsValue(value)) {
            // Forbidden
            return null;
         }
      }
      // Add operations only in revision
      return blobsForRevision.put(key, value);
   }

   @Override
   public void putAll(Map<? extends String, ? extends SplitBlob> m) {
      for (Map.Entry<? extends String, ? extends SplitBlob> each : m.entrySet()) {
         put(each.getKey(), each.getValue());
      }
   }

   @Override
   public SplitBlob remove(Object key) {
      // Remove operations only in revision
      return blobsForRevision.remove(key);
   }

   @Override
   public int size() {
      return (blobsForRevision.size() + blobsForRevised.size());
   }

   @Override
   public Collection<SplitBlob> values() {
      Collection<SplitBlob> values1 = blobsForRevision.values();
      Collection<SplitBlob> values2 = blobsForRevised.values();
      Collection<SplitBlob> out = new ArrayList<SplitBlob>(values1.size() + values2.size());
      out.addAll(values1);
      out.addAll(values2);
      return out;
   }
}
