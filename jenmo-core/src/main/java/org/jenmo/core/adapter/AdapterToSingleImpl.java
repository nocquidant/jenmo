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

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jenmo.core.adapter.AbstractAdapter.AdapterToSingle;
import org.jenmo.core.constant.JenmoConstant;

/**
 * Package access level class. Implementation of {@link AdapterToSingle}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
class AdapterToSingleImpl<T, G> extends AdapterToSingle<T, G> {

   /** The command in order to manipulate genuine elements */
   private IAdapterCmd<?, T, G> cmd;

   /** The Set to adapt */
   private Set<G> target;

   /** The underlying target object */
   private G genuine;

   /** The underlying value */
   private T value;

   public AdapterToSingleImpl(IAdapterCmd<?, T, G> cmd) {
      this.cmd = cmd;
   }

   @Override
   public void adapt(Set<G> target) {
      this.target = target;
      for (G each : target) {
         if (cmd.accept(each)) {
            value = cmd.getValue(each);
            genuine = each;
            break;
         }
      }
   }

   @Override
   public void postNewlyCreated(Map<JenmoConstant, Object> args) {
      cmd.postNewlyCreated(args);
   }

   @Override
   public T get() {
      checkForComodification();
      return value;
   }

   @Override
   public boolean set(T value) {
      if (value == null) {
         throw new NullPointerException("Argument cannot be null");
      }
      clear();
      genuine = cmd.instantiateAndAdd(null, value, target);
      this.value = value;
      return true;
   }

   @Override
   public void clear() {
      if (genuine != null) {
         checkForComodification();
         incModCount();
         target.remove(genuine);
         genuine = null;
         value = null;
      }
   }

   @Override
   public Iterator<T> iterator() {
      return new Iterator<T>() {
         private boolean hasNext = (value != null);

         @Override
         public boolean hasNext() {
            checkForComodification();
            return hasNext;
         }

         @Override
         public T next() {
            checkForComodification();
            if (hasNext == true) {
               hasNext = false;
               return value;
            }
            throw new NoSuchElementException();
         }

         @Override
         public void remove() {
            checkForComodification();
            incModCount();
            AdapterToSingleImpl.this.clear();
            hasNext = false;
         }
      };
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
}
