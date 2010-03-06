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

import java.util.Map;
import java.util.Set;

import org.jenmo.core.constant.JenmoConstant;

/**
 * An adapter command with default implementation for methods.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class AdapterCmd<I, T, G> implements IAdapterCmd<I, T, G> {

   @Override
   public boolean accept(G arg) {
      return true;
   }

   @Override
   public I getIndex(G arg) {
      return null;
   }

   @Override
   public G setIndex(I index, G arg) {
      throw new UnsupportedOperationException("You must override this method if you want to use it");
   }

   @Override
   public T getValue(G arg) {
      return null;
   }

   @Override
   public G instantiateAndAdd(I index, T value, Set<? super G> target) {
      return null;
   }

   @Override
   public void postNewlyCreated(Map<JenmoConstant, Object> args) {
   }
   
   @Override
   public boolean remove(G arg, Set<? super G> target) {
      return target.remove(arg);
   }
}
