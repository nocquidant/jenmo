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
package org.jenmo.core.descriptor;

import java.util.Map;

import org.jenmo.core.constant.JenmoConstant;

/**
 * Descriptors may need to call protected methods on domain objects. This interface helps doing this
 * without changing for public access methods.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 * 
 * @param <T>
 */
public interface INodeCallback<T> {
   boolean accept(T arg);

   void setIndex(String index, T arg);

   void postNew(T arg);

   void postNewAttributes(Map<JenmoConstant, Object> args);

   public static class NodeCallback<T> implements INodeCallback<T> {

      @Override
      public boolean accept(T arg) {
         return true;
      }

      @Override
      public void postNew(T arg) {

      }

      @Override
      public void setIndex(String index, T arg) {

      }

      @Override
      public void postNewAttributes(Map<JenmoConstant, Object> args) {

      }
   }
}