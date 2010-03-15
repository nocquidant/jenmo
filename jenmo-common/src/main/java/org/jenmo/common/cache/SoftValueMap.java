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
 * --     
 * From http://plan-x.org/
 * Modifications to genuine file: none
 */
package org.jenmo.common.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A map implementation with soft values.
 * <p>
 * The values are stored as soft references. If map entry value object is not actively being used,
 * i.e. no other object has a strong reference to it, it may become garbage collected at the
 * discretion of the garbage collector (typically if the VM is low on memory). If this happens, the
 * entry in the <code>SoftValueMap</code> corresponding to the value object will also be removed.
 * 
 * @author Nicolas Ocquidant (thanks to Thomas Ambus)
 * @since 1.0
 */
public class SoftValueMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable {

   private HashMap<K, SoftValue<K, V>> map;

   private ReferenceQueue<V> queue = new ReferenceQueue<V>();

   public SoftValueMap() {
      map = new HashMap<K, SoftValue<K, V>>();
   }

   public SoftValueMap(int initialCapacity) {
      map = new HashMap<K, SoftValue<K, V>>(initialCapacity);
   }

   public SoftValueMap(int initialCapacity, float loadFactor) {
      map = new HashMap<K, SoftValue<K, V>>(initialCapacity, loadFactor);
   }

   @Override
   public Set<Map.Entry<K, V>> entrySet() {
      return new EntrySet();
   }

   @Override
   public boolean containsKey(Object key) {
      return map.containsKey(key);
   }

   @Override
   public V get(Object key) {
      expungeStaleEntries();
      SoftValue<K, V> val = map.get(key);
      if (val == null) {
         return null;
      }
      return val.get();
   }

   @Override
   public V put(K key, V value) {
      expungeStaleEntries();
      SoftValue<K, V> old = map.put(key, new SoftValue<K, V>(key, value, queue));
      if (old == null) {
         return null;
      }
      return old.get();
   }

   @Override
   public V remove(Object key) {
      expungeStaleEntries();
      SoftValue<K, V> old = map.remove(key);
      if (old == null) {
         return null;
      }
      return old.get();
   }

   @Override
   public void clear() {
      expungeStaleEntries();
      map.clear();
   }

   @Override
   public int size() {
      expungeStaleEntries();
      return map.size();
   }

   @Override
   public boolean isEmpty() {
      expungeStaleEntries();
      return map.isEmpty();
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private void expungeStaleEntries() {
      SoftValue<K, V> wv;
      while ((wv = (SoftValue) queue.poll()) != null) {
         map.remove(wv.getKey());
      }
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public SoftValueMap<K, V> clone() {
      expungeStaleEntries();

      SoftValueMap<K, V> svm = null;

      try {
         svm = (SoftValueMap<K, V>) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException(e);
      }

      svm.map = (HashMap) map.clone(); // to preserve initialCapacity,
      // loadFactor
      svm.map.clear();

      svm.queue = new ReferenceQueue<V>();
      for (Map.Entry<K, V> entry : entrySet()) {
         svm.put(entry.getKey(), entry.getValue());
      }

      return svm;
   }

   private static class SoftValue<E, F> extends SoftReference<F> {

      private E key;

      public SoftValue(E key, F value, ReferenceQueue<F> queue) {
         super(value, queue);
         this.key = key;
      }

      public E getKey() {
         return key;
      }
   }

   private static class Entry<E, F> implements Map.Entry<E, F> {

      private Map.Entry<E, SoftValue<E, F>> entry;

      private F value;

      Entry(Map.Entry<E, SoftValue<E, F>> entry, F value) {
         this.entry = entry;
         this.value = value;
      }

      public E getKey() {
         return entry.getKey();
      }

      public F getValue() {
         return value;
      }

      public F setValue(F value) {
         throw new UnsupportedOperationException();
      }

      @Override
      @SuppressWarnings({ "unchecked", "rawtypes" })
      public boolean equals(Object o) {
         if (!(o instanceof Map.Entry)) {
            return false;
         }
         Map.Entry<E, F> e = (Map.Entry<E, F>) o;
         return getKey().equals(e.getKey()) && getValue().equals(e.getValue());
      }

      @Override
      public int hashCode() {
         return getKey().hashCode() ^ getValue().hashCode();
      }
   }

   private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

      private Set<Map.Entry<K, SoftValue<K, V>>> eSet = map.entrySet();

      @Override
      public Iterator<Map.Entry<K, V>> iterator() {
         return new Iterator<Map.Entry<K, V>>() {

            private Iterator<Map.Entry<K, SoftValue<K, V>>> it = eSet.iterator();

            private Entry<K, V> next = null;

            public boolean hasNext() {
               while (it.hasNext()) {
                  Map.Entry<K, SoftValue<K, V>> entry = it.next();
                  SoftValue<K, V> wv = entry.getValue();
                  V value = null;
                  if ((wv != null) && ((value = wv.get()) == null)) {
                     continue;
                  }
                  next = new Entry<K, V>(entry, value);
                  return true;
               }
               return false;
            }

            public Map.Entry<K, V> next() {
               if ((next == null) && !hasNext()) {
                  throw new NoSuchElementException();
               }
               Entry<K, V> e = next;
               next = null;
               return e;
            }

            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }

      @Override
      public int size() {
         return eSet.size();
      }
   }
}
