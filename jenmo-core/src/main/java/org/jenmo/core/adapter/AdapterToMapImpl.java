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

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jenmo.core.adapter.AbstractAdapter.AdapterToMap;
import org.jenmo.core.constant.JenmoConstant;

/**
 * Package access level class. Implementation of {@link AdapterToMap}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
class AdapterToMapImpl<K, V, G> extends AdapterToMap<K, V, G> {

   /** The command in order to manipulate genuine elements */
   private IAdapterCmd<K, V, G> cmd;

   /** The Set to adapt */
   private Set<G> target;

   /** A Map to filter genuine elements and to optimize access */
   private Map<V, G> buffVG = new HashMap<V, G>();

   /** A Map to filter genuine elements and to optimize access */
   private Map<K, V> buffKV = new HashMap<K, V>();

   public AdapterToMapImpl(IAdapterCmd<K, V, G> cmd) {
      this.cmd = cmd;
   }

   @Override
   public void adapt(Set<G> target) {
      this.target = target;
      for (G each : target) {
         if (cmd.accept(each)) {
            K key = cmd.getIndex(each);
            V val = cmd.getValue(each);
            buffKV.put(key, val);
            buffVG.put(val, each);
         }
      }
   }

   @Override
   public void postNewlyCreated(Map<JenmoConstant, Object> args) {
      cmd.postNewlyCreated(args);
   }

   @Override
   public void clear() {
      checkForComodification();
      incModCount();
      for (Map.Entry<V, G> each : buffVG.entrySet()) {
         target.remove(each.getValue());
      }
      buffKV.clear();
      buffVG.clear();
   }

   @Override
   public boolean containsKey(Object key) {
      checkForComodification();
      return buffKV.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      checkForComodification();
      return buffKV.containsValue(value);
   }

   @Override
   public Set<java.util.Map.Entry<K, V>> entrySet() {
      checkForComodification();
      return new AdptEntrySet();
   }

   @Override
   public V get(Object key) {
      checkForComodification();
      return buffKV.get(key);
   }

   @Override
   public boolean isEmpty() {
      checkForComodification();
      return buffKV.isEmpty();
   }

   @Override
   public Set<K> keySet() {
      checkForComodification();
      return new AdptKeySet();
   }

   @Override
   public V put(K key, V value) {
      checkForComodification();
      incModCount();
      V oldv = buffKV.remove(key);
      if (oldv != null) {
         G oldg = buffVG.remove(oldv);
         if (oldg != null) {
           target.remove(oldg);
         }
      }
      G obj = cmd.instantiateAndAdd(key, value, target);
      V out = buffKV.put(key, value);
      buffVG.put(value, obj);
      return out;
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      for (Map.Entry<? extends K, ? extends V> each : m.entrySet()) {
         put(each.getKey(), each.getValue());
      }
   }

   @Override
   public V remove(Object key) {
      checkForComodification();
      incModCount();
      V v = buffKV.remove(key);
      if (v != null) {
         G g = buffVG.remove(v);
         if (g != null) {
            if (target.remove(g) == true) {
               return v;
            }
         }
      }
      return null;
   }

   @Override
   public int size() {
      checkForComodification();
      return buffKV.size();
   }

   @Override
   public Collection<V> values() {
      checkForComodification();
      return new AdptValueSet();
   }

   @Override
   protected IAdapterCmd<K, V, G> getCommand() {
      return cmd;
   }

   @Override
   protected void setCommand(IAdapterCmd<K, V, G> cmd) {
      this.cmd = cmd;
   }

   @Override
   protected Set<G> asSet() {
      return target;
   }

   // Just for unit tests!
   protected final boolean checkConsistency() {
      if (target.size() != buffKV.size()) {
         return false;
      }
      if (target.size() != buffVG.size()) {
         return false;
      }
      for (V each : buffKV.values()) {
         if (target.contains(buffVG.get(each)) == false) {
            return false;
         }
      }
      return true;
   }

   private class AdptEntrySet extends AbstractSet<Map.Entry<K, V>> {

      private Set<Map.Entry<K, V>> set;

      AdptEntrySet() {
         set = AdapterToMapImpl.this.buffKV.entrySet();
      }

      @Override
      public boolean remove(Object o) {
         checkForComodification();
         throw new UnsupportedOperationException();
      }

      @Override
      public Iterator<Map.Entry<K, V>> iterator() {
         checkForComodification();
         return new AdptEntryIterator(set.iterator());
      }

      @Override
      public void clear() {
         AdapterToMapImpl.this.clear();
      }

      @Override
      public int size() {
         checkForComodification();
         return set.size();
      }
   }

   private final class AdptEntryIterator implements Iterator<Map.Entry<K, V>> {
      Iterator<Map.Entry<K, V>> itr;

      Map.Entry<K, V> current;

      AdptEntryIterator(Iterator<Map.Entry<K, V>> itr) {
         this.itr = itr;
      }

      @Override
      public boolean hasNext() {
         checkForComodification();
         boolean out = itr.hasNext();
         if (out == false) {
            current = null;
         }
         return out;
      }

      @Override
      public java.util.Map.Entry<K, V> next() {
         checkForComodification();
         current = itr.next();
         return current;
      }

      @Override
      public void remove() {
         checkForComodification();
         incModCount();
         G obj = AdapterToMapImpl.this.buffVG.get(current.getValue());
         if (obj != null) {
            itr.remove(); // remove from buffKV
            buffVG.remove(current.getValue());
            target.remove(obj);
            current = null;
         }
      }
   }

   private class AdptKeySet extends AbstractSet<K> {

      private Set<K> set;

      AdptKeySet() {
         set = AdapterToMapImpl.this.buffKV.keySet();
      }

      @Override
      public boolean remove(Object o) {
         checkForComodification();
         incModCount();
         throw new UnsupportedOperationException();
      }

      @Override
      public Iterator<K> iterator() {
         checkForComodification();
         return new AdptKeyIterator(set.iterator());
      }

      @Override
      public void clear() {
         checkForComodification();
         incModCount();
         AdapterToMapImpl.this.clear();
      }

      @Override
      public int size() {
         checkForComodification();
         return set.size();
      }
   }

   private final class AdptKeyIterator implements Iterator<K> {
      Iterator<K> itr;

      K current;

      AdptKeyIterator(Iterator<K> itr) {
         this.itr = itr;
      }

      @Override
      public boolean hasNext() {
         checkForComodification();
         boolean out = itr.hasNext();
         if (out == false) {
            current = null;
         }
         return out;
      }

      @Override
      public K next() {
         checkForComodification();
         current = itr.next();
         return current;
      }

      @Override
      public void remove() {
         checkForComodification();
         incModCount();
         V v = AdapterToMapImpl.this.buffKV.get(current);
         G obj = AdapterToMapImpl.this.buffVG.get(v);
         if (obj != null) {
            itr.remove(); // remove from buffKV
            buffVG.remove(v);
            target.remove(obj);
            current = null;
         }
      }
   }

   private class AdptValueSet extends AbstractSet<V> {

      private Collection<V> set;

      AdptValueSet() {
         set = AdapterToMapImpl.this.buffKV.values();
      }

      @Override
      public boolean remove(Object o) {
         checkForComodification();
         incModCount();
         throw new UnsupportedOperationException();
      }

      @Override
      public Iterator<V> iterator() {
         checkForComodification();
         return new AdptValueIterator(set.iterator());
      }

      @Override
      public void clear() {
         checkForComodification();
         incModCount();
         AdapterToMapImpl.this.clear();
      }

      @Override
      public int size() {
         checkForComodification();
         return set.size();
      }
   }

   private final class AdptValueIterator implements Iterator<V> {
      Iterator<V> itr;

      V current;

      AdptValueIterator(Iterator<V> itr) {
         this.itr = itr;
      }

      @Override
      public boolean hasNext() {
         checkForComodification();
         boolean out = itr.hasNext();
         if (out == false) {
            current = null;
         }
         return out;
      }

      @Override
      public V next() {
         checkForComodification();
         current = itr.next();
         return current;
      }

      @Override
      public void remove() {
         checkForComodification();
         incModCount();
         G obj = AdapterToMapImpl.this.buffVG.get(current);
         if (obj != null) {
            itr.remove(); // remove from buffKV
            buffVG.remove(current);
            target.remove(obj);
            current = null;
         }
      }
   }
}