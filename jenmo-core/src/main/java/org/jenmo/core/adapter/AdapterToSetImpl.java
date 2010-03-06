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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jenmo.core.adapter.AbstractAdapter.AdapterToSet;
import org.jenmo.core.constant.JenmoConstant;

/**
 * Package access level class. Implementation of {@link AdapterToSet}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
class AdapterToSetImpl<T, G> extends AdapterToSet<T, G> {

   /** The command in order to manipulate genuine elements */
   private IAdapterCmd<?, T, G> cmd;

   /** The Set to adapt */
   private Set<G> target;

   /** A Map to filter genuine elements and to optimize access */
   private Map<T, G> buff = new HashMap<T, G>();

   public AdapterToSetImpl(IAdapterCmd<?, T, G> cmd) {
      this.cmd = cmd;
   }

   @Override
   public void adapt(Set<G> target) {
      this.target = target;
      for (G each : target) {
         if (cmd.accept(each)) {
            T key = cmd.getValue(each);
            buff.put(key, each);
         }
      }
   }

   @Override
   public void postNewlyCreated(Map<JenmoConstant, Object> args) {
      cmd.postNewlyCreated(args);
   }

   @Override
   public boolean add(T e) {
      checkForComodification();
      incModCount();
      G obj = cmd.instantiateAndAdd(null, e, target);
      return (buff.put(e, obj) == null);
   }

   @Override
   public boolean add(T e, JenmoConstant argKey, Object argValue) {
      boolean out = add(e);
      if (out && (argKey != null && argValue != null)) {
         Map<JenmoConstant, Object> args = new HashMap<JenmoConstant, Object>();
         args.put(argKey, argValue);
         postNewlyCreated(args);
      }
      return out;
   }

   @Override
   public boolean addAll(Collection<? extends T> c) {
      boolean out = false;
      for (T each : c) {
         out |= add(each);
      }
      return out;
   }

   @Override
   public void clear() {
      checkForComodification();
      incModCount();
      for (Map.Entry<T, G> each : buff.entrySet()) {
         target.remove(each.getValue());
      }
      buff.clear();
   }

   @Override
   public boolean contains(Object o) {
      checkForComodification();
      return buff.containsKey(o);
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      checkForComodification();
      for (Object each : c) {
         if (buff.containsKey(each) == false) {
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean isEmpty() {
      checkForComodification();
      return buff.isEmpty();
   }

   @Override
   public Iterator<T> iterator() {
      checkForComodification();
      return new AdptItr();
   }

   @Override
   public boolean remove(Object o) {
      checkForComodification();
      incModCount();
      G obj = buff.remove(o);
      boolean out = false;
      if (obj != null) {
         out = target.remove(obj);
      }
      return out;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      boolean out = false;
      for (Object each : c) {
         out |= remove(each);
      }
      return out;
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      boolean modified = false;
      Iterator<T> e = iterator();
      while (e.hasNext()) {
         if (!c.contains(e.next())) {
            e.remove();
            modified = true;
         }
      }
      return modified;
   }

   @Override
   public int size() {
      checkForComodification();
      return buff.size();
   }

   @Override
   public Object[] toArray() {
      checkForComodification();
      return toArray(new Object[buff.size()]);
   }

   @SuppressWarnings( { "unchecked", "hiding" })
   @Override
   public <T> T[] toArray(T[] a) {
      checkForComodification();
      int idx = 0;
      for (Object each : buff.keySet()) {
         a[idx++] = (T) each;
      }
      return a;
   }

   @Override
   protected IAdapterCmd<?, T, G> getCommand() {
      return cmd;
   }

   @Override
   protected void setCommand(IAdapterCmd<?, T, G> cmd) {
      this.cmd = cmd;
   }

   @Override
   protected Set<G> asSet() {
      return target;
   }

   // Just for unit tests (keep it protected)!
   protected boolean checkConsistency() {
      if (target.size() != buff.size()) {
         return false;
      }
      for (G each : buff.values()) {
         if (target.contains(each) == false) {
            return false;
         }
      }
      return true;
   }

   protected class AdptItr implements Iterator<T> {
      Iterator<T> keyItr;

      T current;

      AdptItr() {
         this.keyItr = AdapterToSetImpl.this.buff.keySet().iterator();
      }

      @Override
      public final boolean hasNext() {
         checkForComodification();
         boolean out = keyItr.hasNext();
         if (out == false) {
            current = null;
         }
         return out;
      }

      @Override
      public final T next() {
         checkForComodification();
         current = keyItr.next();
         return current;
      }

      @Override
      public void remove() {
         checkForComodification();
         incModCount();
         G obj = AdapterToSetImpl.this.buff.get(current);
         if (obj != null) {
            keyItr.remove();
            target.remove(obj);
            current = null;
         }
      }
   }
}
