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

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jenmo.core.constant.JenmoConstant;
import org.jenmo.core.domain.Node.ModCounter;

/**
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public abstract class AbstractAdapter<G> implements IAdapter<G> {
   private ModCounter modCounter;

   /**
    * The modCount value that the iterator believes that the backing List should have. If this
    * expectation is violated, the iterator has detected concurrent modification.
    */
   private int expectedModCount;

   @Override
   public void adapt(Set<G> target, ModCounter modCounter) {
      if (modCounter != null) {
         this.modCounter = modCounter;
         expectedModCount = modCounter.modCount();
      }
      adapt(target);
   }

   // modCount management
   protected final void incModCount() {
      if (modCounter != null) {
         expectedModCount = modCounter.incModCount();
      }
   }

   // modCount management
   protected final int getModCount() {
      if (modCounter != null) {
         return modCounter.modCount();
      }
      return 0;
   }

   // modCount management
   protected final void checkForComodification() {
      if (modCounter != null) {
         if (modCounter.modCount() != expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }
   }

   @Override
   public void postNewlyCreated(JenmoConstant key, Object value) {
      Map<JenmoConstant, Object> map = new HashMap<JenmoConstant, Object>();
      map.put(key, value);
      postNewlyCreated(map);
   }

   /**
    * This method can only be used after asking for an "appliable view" to {@link Adapters} class.
    * Until then, throw {@link UnsupportedOperationException}.
    */
   public void apply() {
      throw new UnsupportedOperationException(
            "Call Adapters.applyable() in order to use this method");
   }

   /**
    * To be used internally in {@link Adapters} class.
    */
   protected abstract Set<G> asSet();

   /**
    * To be used internally in {@link Adapters} class.
    */
   protected abstract Object asImpl();

   /**
    * Converts a {@link Set} of G objects into a {@link List} of T.
    * <p>
    * <b>Warning:</b> the elements T are wrapped by an inner class before being inserted into the
    * list. Thus, you cannot modify them directly outside of the scope of this adapter. Also, keep
    * in mind that some operations could be slow as the conversion operation costs.
    * 
    * @param <T>
    *           Wanted type by the client code
    * @param <G>
    *           Genuine type the domain object really handle
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   // Don't have an interface here as we want all these methods to stay protected
   public static abstract class AdapterToList<T, G> extends AbstractAdapter<G> implements
         IAdapter<G>, List<T> {
      /**
       * To be used internally in {@link Adapters} class.
       */
      protected abstract IAdapterCmd<Integer, T, G> getCommand();

      /**
       * To be used internally in {@link Adapters} class.
       */
      protected abstract void setCommand(IAdapterCmd<Integer, T, G> cmd);

      /**
       * To be used internally in {@link Adapters} class.
       */
      protected abstract void shiftInc(int index, int inc);

      /**
       * To be used internally in {@link Adapters} class.
       */
      protected abstract void shiftDec(int index, int dec);

      /**
       * To be used internally in {@link Adapters} class.
       */
      protected abstract List<CompoundElmt<T, G>> getCompounds();

      @Override
      protected AdapterToListImpl<T, G> asImpl() {
         if (this instanceof AdapterToListImpl<?, ?>) {
            return (AdapterToListImpl<T, G>) this;
         }
         return null;
      }
   }

   /**
    * Converts a {@link Set} of G objects into a {@link Map} of (K,V).
    * 
    * @param <K>
    *           Wanted type for key, by the client code
    * @param <V>
    *           Wanted type for value, by the client code
    * @param <G>
    *           Genuine type the domain object really handle
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   // Don't have an interface here as we want all these methods to stay protected
   public static abstract class AdapterToMap<K, V, G> extends AbstractAdapter<G> implements
         IAdapter<G>, Map<K, V> {
      /**
       * To be used internally in {@link Adapters} class.
       */
      protected abstract IAdapterCmd<K, V, G> getCommand();

      /**
       * To be used internally in {@link Adapters} class.
       */
      protected abstract void setCommand(IAdapterCmd<K, V, G> cmd);

      @Override
      protected AdapterToMapImpl<K, V, G> asImpl() {
         if (this instanceof AdapterToMapImpl<?, ?, ?>) {
            return (AdapterToMapImpl<K, V, G>) this;
         }
         return null;
      }
   }

   /**
    * Converts a {@link Set} of G objects into a {@link Set} of T.
    * 
    * @param <T>
    *           Wanted type by the client code
    * @param <G>
    *           Genuine type the domain object really handle
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   // Don't have an interface here as we want all these methods to stay protected
   public static abstract class AdapterToSet<T, G> extends AbstractAdapter<G> implements
         IAdapter<G>, Set<T> {
      /**
       * A shortcut method calling {@link AdapterToSetImpl#add(Object)} then
       * {@link AdapterToSetImpl#postNewlyCreated(Map)}
       */
      public abstract boolean add(T e, JenmoConstant argKey, Object argValue);

      /**
       * To be used internally in {@link Adapters} class.
       */
      protected abstract IAdapterCmd<?, T, G> getCommand();

      /**
       * To be used internally in {@link Adapters} class.
       */
      protected abstract void setCommand(IAdapterCmd<?, T, G> cmd);

      @Override
      protected AdapterToSetImpl<T, G> asImpl() {
         if (this instanceof AdapterToSetImpl<?, ?>) {
            return (AdapterToSetImpl<T, G>) this;
         }
         return null;
      }
   }

   /**
    * Converts a {@link Set} of G objects into a single object T.
    * 
    * @param <T>
    *           Wanted type by the client code
    * @param <G>
    *           Genuine type the domain object really handle
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   // Don't have an interface here as we want all these methods to stay protected
   public static abstract class AdapterToSingle<T, G> extends AbstractAdapter<G> implements
         IAdapter<G>, Iterable<T>, IReference<T> {
      @Override
      public abstract T get();

      @Override
      public abstract boolean set(T value);

      @Override
      public abstract void clear();

      /**
       * To be used internally in {@link Adapters} class.
       */
      protected abstract IAdapterCmd<?, T, G> getCommand();

      /**
       * To be used internally in {@link Adapters} class.
       */
      protected abstract void setCommand(IAdapterCmd<?, T, G> cmd);

      @Override
      protected AdapterToSingleImpl<T, G> asImpl() {
         if (this instanceof AdapterToSingleImpl<?, ?>) {
            return (AdapterToSingleImpl<T, G>) this;
         }
         return null;
      }
   }
}
