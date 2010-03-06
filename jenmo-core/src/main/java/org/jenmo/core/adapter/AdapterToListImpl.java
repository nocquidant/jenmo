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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.jenmo.common.localizer.Localizer;
import org.jenmo.core.adapter.AbstractAdapter.AdapterToList;
import org.jenmo.core.constant.JenmoConstant;

/**
 * Package access level class. Implementation of {@link AdapterToList}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
class AdapterToListImpl<T, G> extends AdapterToList<T, G> {
   private static final Localizer LOC = Localizer.forPackage(AdapterToListImpl.class);

   /** The command in order to manipulate genuine elements */
   private IAdapterCmd<Integer, T, G> cmd;

   /** The Set to adapt */
   private Set<G> target;

   /** A List to filter genuine elements and to optimize access */
   private List<CompoundElmt<T, G>> buff = new ArrayList<CompoundElmt<T, G>>();

   public AdapterToListImpl(IAdapterCmd<Integer, T, G> cmd) {
      this.cmd = cmd;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void adapt(Set<G> target) {
      this.target = target;

      int size = 0;
      long sum = 0;
      CompoundElmt<?, ?>[] array = new CompoundElmt[target.size()];
      for (G each : target) {
         if (cmd.accept(each)) {
            int key = cmd.getIndex(each);
            array[key] = new CompoundElmt<T, G>(cmd, key, each);
            size++;
            sum += key;
         }
      }

      if (sum != (size * (size - 1) / 2)) {
         throw new IllegalStateException(LOC.get("INTERNAL_INCONSISTENCY").getMessage());
      }

      this.buff = new ArrayList<CompoundElmt<T, G>>(size);
      for (int i = 0; i < size; i++) {
         this.buff.add((CompoundElmt<T, G>) array[i]);
      }

      // TreeSet<CompoundElmt<T, G>> treeset = new TreeSet<CompoundElmt<T, G>>();
      // for (G each : target) {
      // if (cmd.accept(each)) {
      // Integer key = cmd.getIndex(each);
      // treeset.add(new CompoundElmt<T, G>(cmd, key, each));
      // }
      // }
      // this.buff = new ArrayList<CompoundElmt<T, G>>(target.size());
      // for (CompoundElmt<T, G> each : treeset) {
      // this.buff.add(each);
      // }
   }

   @Override
   public void postNewlyCreated(Map<JenmoConstant, Object> args) {
      cmd.postNewlyCreated(args);
   }

   @Override
   public boolean add(T e) {
      checkForComodification();
      incModCount();
      int index = buff.size();
      G obj = cmd.instantiateAndAdd(index, e, target);
      CompoundElmt<T, G> cpd = new CompoundElmt<T, G>(cmd, index, obj);
      return buff.add(cpd);
   }

   @Override
   public void add(int index, T element) {
      checkForComodification();
      incModCount();
      shiftInc(index, 1);
      G obj = cmd.instantiateAndAdd(index, element, target);
      CompoundElmt<T, G> cpd = new CompoundElmt<T, G>(cmd, index, obj);
      buff.add(index, cpd);
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
   public boolean addAll(int index, Collection<? extends T> c) {
      checkForComodification();
      incModCount();
      shiftInc(index, c.size());
      List<CompoundElmt<T, G>> listT = new ArrayList<CompoundElmt<T, G>>(c.size());
      int current = index;
      for (T each : c) {
         G obj = cmd.instantiateAndAdd(index, each, target);
         CompoundElmt<T, G> cpd = new CompoundElmt<T, G>(cmd, current++, obj);
         listT.add(cpd);
      }
      boolean result = false;
      result |= buff.addAll(index, listT);
      return result;
   }

   @Override
   public void clear() {
      checkForComodification();
      incModCount();
      for (CompoundElmt<T, G> each : buff) {
         cmd.remove(each.genuine, target);
      }
      buff.clear();
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean contains(Object o) {
      checkForComodification();
      return buff.contains(new CompoundElmt<T, G>((T) o));
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      checkForComodification();
      for (Object each : c) {
         if (contains(each) == false) {
            return false;
         }
      }
      return true;
   }

   @Override
   public T get(int index) {
      checkForComodification();
      return buff.get(index).underlyingValue();
   }

   @SuppressWarnings("unchecked")
   @Override
   public int indexOf(Object o) {
      checkForComodification();
      return buff.indexOf(new CompoundElmt<T, G>((T) o));
   }

   @Override
   public boolean isEmpty() {
      checkForComodification();
      return buff.isEmpty();
   }

   @Override
   public Iterator<T> iterator() {
      return new AdptItr();
   }

   @SuppressWarnings("unchecked")
   @Override
   public int lastIndexOf(Object o) {
      checkForComodification();
      return buff.lastIndexOf(new CompoundElmt<T, G>((T) o));
   }

   @Override
   public ListIterator<T> listIterator() {
      return new AdptListItr(0);
   }

   @Override
   public ListIterator<T> listIterator(int index) {
      return new AdptListItr(index);
   }

   @Override
   public boolean remove(Object o) {
      int idx = indexOf(o);
      if (idx != -1) {
         return (remove(idx) != null) ? true : false;
      }
      return false;
   }

   @Override
   public T remove(int index) {
      checkForComodification();
      incModCount();
      shiftDec(index, 1);
      CompoundElmt<T, G> out = buff.remove(index);
      G g = (G) out.genuine;
      cmd.remove(g, target);
      return out.underlyingValue();
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
   public T set(int index, T element) {
      checkForComodification();
      incModCount();
      G obj = cmd.instantiateAndAdd(index, element, target);
      CompoundElmt<T, G> cpd = new CompoundElmt<T, G>(cmd, new Integer(index), obj);
      CompoundElmt<T, G> old = buff.set(index, cpd);
      if (old.key != index) {
         throw new IllegalStateException(LOC.get("INTERNAL_INCONSISTENCY").getMessage());
      }
      cmd.remove(old.genuine, target);
      return old.underlyingValue();
   }

   @Override
   public int size() {
      checkForComodification();
      return buff.size();
   }

   @Override
   public List<T> subList(int fromIndex, int toIndex) {
      checkForComodification();
      // Should avoid System.arrayCopy getting the SubList class
      return new ArrayList<T>(this).subList(fromIndex, toIndex);
   }

   @Override
   public Object[] toArray() {
      checkForComodification();
      return toArray(new Object[buff.size()]);
   }

   @SuppressWarnings( { "hiding", "unchecked" })
   @Override
   public <T> T[] toArray(T[] a) {
      checkForComodification();
      for (int i = 0; i < a.length; i++) {
         a[i] = (T) buff.get(i).underlyingValue();
      }
      return a;
   }

   @Override
   protected IAdapterCmd<Integer, T, G> getCommand() {
      return cmd;
   }

   @Override
   protected void setCommand(IAdapterCmd<Integer, T, G> cmd) {
      this.cmd = cmd;
   }

   @Override
   protected Set<G> asSet() {
      return target;
   }

   @Override
   protected void shiftInc(int index, int inc) {
      for (int i = index; i < buff.size(); i++) {
         CompoundElmt<T, G> cpd = buff.get(i);
         cpd.key += inc;
         cmd.setIndex(index + inc, (G) cpd.genuine);
      }
   }

   @Override
   protected void shiftDec(int index, int dec) {
      for (int i = index + dec; i < buff.size(); i++) {
         CompoundElmt<T, G> cpd = buff.get(i);
         cpd.key -= dec;
         cmd.setIndex(index - dec, (G) cpd.genuine);
      }
   }

   @Override
   protected List<CompoundElmt<T, G>> getCompounds() {
      return buff;
   }

   // Just for unit tests!
   protected final boolean checkConsistency() {
      if (target.size() != buff.size()) {
         return false;
      }
      for (CompoundElmt<T, G> each : buff) {
         if (target.contains(each.genuine) == false) {
            return false;
         }
      }
      return true;
   }

   private class AdptItr implements Iterator<T> {
      ListIterator<CompoundElmt<T, G>> itr;

      CompoundElmt<T, G> current;

      AdptItr() {
         this.itr = AdapterToListImpl.this.buff.listIterator();
      }

      AdptItr(ListIterator<CompoundElmt<T, G>> itr) {
         this.itr = itr;
      }

//      protected G currentGenuine() {
//         checkForComodification();
//         if (current == null) {
//            return null;
//         }
//         return current.genuine;
//      }

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
      public T next() {
         checkForComodification();
         current = itr.next();
         return current.underlyingValue();
      }

      @Override
      public void remove() {
         checkForComodification();
         incModCount();
         int index = itr.nextIndex() - 1;
         CompoundElmt<T, G> obj = AdapterToListImpl.this.buff.get(index);
         shiftDec(index, 1);
         itr.remove();
         cmd.remove(obj.genuine, target);
         current = null;
      }
   }

   private final class AdptListItr extends AdptItr implements ListIterator<T> {
      AdptListItr(int index) {
         super(AdapterToListImpl.this.buff.listIterator(index));
      }

      @Override
      public void add(T e) {
         checkForComodification();
         incModCount();
         int index = itr.nextIndex();
         shiftInc(index, 1);
         G obj = AdapterToListImpl.this.cmd.instantiateAndAdd(index, e, target);
         CompoundElmt<T, G> cpd = new CompoundElmt<T, G>(cmd, index, obj);
         itr.add(cpd);
      }

      @Override
      public boolean hasPrevious() {
         checkForComodification();
         boolean out = itr.hasPrevious();
         if (out == false) {
            current = null;
         }
         return out;
      }

      @Override
      public int nextIndex() {
         checkForComodification();
         return itr.nextIndex();
      }

      @Override
      public T previous() {
         checkForComodification();
         current = itr.previous();
         return current.underlyingValue();
      }

      @Override
      public int previousIndex() {
         checkForComodification();
         return itr.previousIndex();
      }

      @Override
      public void set(T e) {
         checkForComodification();
         incModCount();
         int index = itr.nextIndex() - 1;
         G obj = cmd.instantiateAndAdd(index, e, target);
         CompoundElmt<T, G> cpd = new CompoundElmt<T, G>(cmd, new Integer(index), obj);
         CompoundElmt<T, G> old = current;
         if (old.key != index) {
            throw new IllegalStateException(LOC.get("INTERNAL_INCONSISTENCY").getMessage());
         }
         cmd.remove(old.genuine, target);
         itr.set(cpd);
      }
   }
}

class Shifter {

   protected void shiftInc(int index, int inc) {
   }

   protected void shiftDec(int index, int dec) {
   }
}

class CompoundElmt<T, G> implements Comparable<CompoundElmt<T, G>>, Serializable {
   private static final long serialVersionUID = -5239237751515871045L;

   protected IAdapterCmd<Integer, T, G> cmd;

   protected int key;

   protected G genuine;

   private T value;

   protected CompoundElmt(IAdapterCmd<Integer, T, G> cmd, int key, G genuine) {
      this.cmd = cmd;
      this.key = key;
      this.genuine = genuine;
   }

   public CompoundElmt(T value) {
      this.value = value;
   }

   public T underlyingValue() {
      if (value == null) {
         value = cmd.getValue(genuine);
      }
      return value;
   }

   @Override
   public int compareTo(CompoundElmt<T, G> o) {
      int thisVal = this.key;
      int anotherVal = o.key;
      return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
   }

   @Override
   public int hashCode() {
      int result = (int) serialVersionUID;
      final int prime = 31;
      return prime * result + underlyingValue().hashCode();
   }

   @Override
   public boolean equals(Object that) {
      if (this == that) {
         return true;
      }
      if (that instanceof CompoundElmt<?, ?>) {
         return this.underlyingValue().equals(((CompoundElmt<?, ?>) that).underlyingValue());
      }
      return false;
   }

   public String toString() {
      return value.toString();
   }
}