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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.jenmo.core.adapter.AbstractAdapter.AdapterToList;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToSet;
import org.jenmo.core.constant.JenmoConstant;
import org.jenmo.core.domain.Node.ModCounter;

/**
 * This class consists exclusively of static methods that operate on or return adapters. It contains
 * polymorphic algorithms that operate on adapters, "wrappers", which return a new adapters backed
 * by a specified adapters, and a few other odds and ends.
 * 
 * <p>
 * The methods of this class all throw a <tt>NullPointerException</tt> if the adapters or class
 * objects provided to them are null.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class Adapters {
   private Adapters() {

   }

   /**
    * Returns an unmodifiable view of the specified adapter. This method allows modules to provide
    * users with "read-only" access to internal adapters.
    */
   public static <T, G> AdapterToSet<T, G> unmodifiable(AdapterToSet<T, G> adapter) {
      return new UnmodifiableAdapterToSet<T, G>(adapter);
   }

   /**
    * Returns an appliable view of the specified adapter. This method allows modules to provide
    * users with "apply once finished" access to internal adapters.
    */
   public static <T, G> AdapterToList<T, G> appliable(AdapterToList<T, G> adapter) {
      return new ApplyableAdapterToList<T, G>(adapter);
   }

   static class UnmodifiableAdapterToSet<T, G> extends AdapterToSet<T, G> {
      final AdapterToSet<T, G> adapter;

      UnmodifiableAdapterToSet(AdapterToSet<T, G> adapter) {
         if (adapter == null)
            throw new NullPointerException();
         this.adapter = adapter;
      }

      @Override
      public boolean equals(Object o) {
         return o == this || adapter.equals(o);
      }

      @Override
      public int hashCode() {
         return adapter.hashCode();
      }

      @Override
      public void adapt(Set<G> target) {
         adapter.adapt(target);
      }

      @Override
      public void adapt(Set<G> target, ModCounter modCounter) {
         adapter.adapt(target, modCounter);
      }

      @Override
      public void postNewlyCreated(JenmoConstant key, Object value) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void postNewlyCreated(Map<JenmoConstant, Object> args) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean add(T e) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean add(T e, JenmoConstant argKey, Object argValue) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends T> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean contains(Object o) {
         return adapter.contains(o);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return adapter.containsAll(c);
      }

      @Override
      public boolean isEmpty() {
         return adapter.isEmpty();
      }

      @Override
      public Iterator<T> iterator() {
         return new Iterator<T>() {
            Iterator<? extends T> it = adapter.iterator();

            @Override
            public boolean hasNext() {
               return it.hasNext();
            }

            @Override
            public T next() {
               return it.next();
            }

            @Override
            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }

      @Override
      public boolean remove(Object o) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      public int size() {
         return adapter.size();
      }

      @Override
      public Object[] toArray() {
         return adapter.toArray();
      }

      @SuppressWarnings( { "hiding" })
      @Override
      public <T> T[] toArray(T[] a) {
         return adapter.toArray(a);
      }

      @Override
      protected Set<G> asSet() {
         return adapter.asSet();
      }

      @Override
      protected IAdapterCmd<?, T, G> getCommand() {
         return adapter.getCommand();
      }

      @Override
      protected void setCommand(IAdapterCmd<?, T, G> cmd) {
         adapter.setCommand(cmd);
      }

      @Override
      protected AdapterToSetImpl<T, G> asImpl() {
         return adapter.asImpl();
      }
   }

   static class ApplyableAdapterToList<T, G> extends AdapterToList<T, G> {
      final AdapterToList<T, G> adapter;

      final List<G> toRemove = new ArrayList<G>();

      int initialModCount;

      ApplyableAdapterToList(final AdapterToList<T, G> adapter) {
         if (adapter == null)
            throw new NullPointerException();

         IAdapterCmd<Integer, T, G> newCmd = new IAdapterCmd<Integer, T, G>() {
            IAdapterCmd<Integer, T, G> c = adapter.getCommand();

            @Override
            public boolean accept(G arg) {
               return c.accept(arg);
            }

            @Override
            public Integer getIndex(G arg) {
               return c.getIndex(arg);
            }

            @Override
            public T getValue(G arg) {
               return c.getValue(arg);
            }

            @Override
            public G instantiateAndAdd(Integer index, T value, Set<? super G> target) {
               return c.instantiateAndAdd(index, value, null);
            }

            @Override
            public void postNewlyCreated(Map<JenmoConstant, Object> args) {
               c.postNewlyCreated(args);

            }

            @Override
            public G setIndex(Integer index, G arg) {
               return c.setIndex(index, arg);
            }

            @Override
            public boolean remove(G arg, Set<? super G> target) {
               toRemove.add(arg);
               return true;
            }
         };
         this.adapter = adapter;
         this.adapter.setCommand(newCmd);
         initialModCount = this.adapter.asImpl().getModCount();
      }

      @Override
      public boolean equals(Object o) {
         return o == this || adapter.equals(o);
      }

      @Override
      public int hashCode() {
         return adapter.hashCode();
      }

      @SuppressWarnings("unchecked")
      @Override
      public void apply() {
         // Work to do?
         if (initialModCount == adapter.asImpl().getModCount()) {
            return;
         }

         // Update modCounts
         adapter.asImpl().checkForComodification();
         adapter.asImpl().incModCount();
         initialModCount = adapter.asImpl().getModCount();

         // Adjust indexes and add missing genuine elements
         List<CompoundElmt<T, G>> compounds = adapter.getCompounds();
         Object[] array = compounds.toArray();
         Arrays.sort(array);
         int count = 0;
         for (Object each : array) {
            CompoundElmt<?, G> cpd = (CompoundElmt<?, G>) each;
            if (cpd.key != count) {
               cpd.key = count;
               adapter.getCommand().setIndex(new Integer(cpd.key), cpd.genuine);
            }
            if (adapter.asSet().contains(cpd.genuine) == false) {
               adapter.asSet().add(cpd.genuine);
            }
            count++;
         }

         // Remove deleted genuine elements
         for (G each : toRemove) {
            adapter.asSet().remove(each);
         }
      }

      @Override
      public void adapt(Set<G> target) {
         adapter.adapt(target);
      }

      @Override
      public void adapt(Set<G> target, ModCounter modCounter) {
         adapter.adapt(target, modCounter);
      }

      @Override
      public void postNewlyCreated(Map<JenmoConstant, Object> args) {
         adapter.postNewlyCreated(args);
      }

      @Override
      public void postNewlyCreated(JenmoConstant key, Object value) {
         adapter.postNewlyCreated(key, value);
      }

      @Override
      public boolean add(T e) {
         return adapter.add(e);
      }

      @Override
      public void add(int index, T element) {
         adapter.add(index, element);
      }

      @Override
      public boolean addAll(Collection<? extends T> c) {
         return adapter.addAll(c);
      }

      @Override
      public boolean addAll(int index, Collection<? extends T> c) {
         return adapter.addAll(index, c);
      }

      @Override
      public void clear() {
         adapter.clear();
      }

      @Override
      public boolean contains(Object o) {
         return adapter.contains(o);
      }

      @Override
      public boolean containsAll(Collection<?> c) {
         return adapter.containsAll(c);
      }

      @Override
      public T get(int index) {
         return adapter.get(index);
      }

      @Override
      public int indexOf(Object o) {
         return adapter.indexOf(o);
      }

      @Override
      public boolean isEmpty() {
         return adapter.isEmpty();
      }

      @Override
      public Iterator<T> iterator() {
         return adapter.iterator();
      }

      @Override
      public int lastIndexOf(Object o) {
         return adapter.lastIndexOf(o);
      }

      @Override
      public ListIterator<T> listIterator() {
         return adapter.listIterator();
      }

      @Override
      public ListIterator<T> listIterator(int index) {
         return adapter.listIterator(index);
      }

      @Override
      public boolean remove(Object o) {
         return adapter.remove(o);
      }

      @Override
      public T remove(int index) {
         return adapter.remove(index);
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         return adapter.removeAll(c);
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         return adapter.retainAll(c);
      }

      @Override
      public T set(int index, T element) {
         return adapter.set(index, element);
      }

      @Override
      public int size() {
         return adapter.size();
      }

      @Override
      public List<T> subList(int fromIndex, int toIndex) {
         return adapter.subList(fromIndex, toIndex);
      }

      @Override
      public Object[] toArray() {
         return adapter.toArray();
      }

      @SuppressWarnings("hiding")
      @Override
      public <T> T[] toArray(T[] a) {
         return adapter.toArray(a);
      }

      protected void shiftInc(int index, int inc) {
         // Do nothing
      }

      protected void shiftDec(int index, int dec) {
         // Do nothing
      }

      @Override
      protected Set<G> asSet() {
         return adapter.asSet();
      }

      @Override
      protected IAdapterCmd<Integer, T, G> getCommand() {
         return adapter.getCommand();
      }

      @Override
      protected void setCommand(IAdapterCmd<Integer, T, G> cmd) {
         adapter.setCommand(cmd);
      }

      @Override
      protected List<CompoundElmt<T, G>> getCompounds() {
         return adapter.getCompounds();
      }

      @Override
      protected AdapterToListImpl<T, G> asImpl() {
         return adapter.asImpl();
      }
   }
}
