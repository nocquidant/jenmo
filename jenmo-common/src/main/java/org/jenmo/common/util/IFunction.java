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
package org.jenmo.common.util;

/**
 * Interface for function.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public interface IFunction<A, O> {
   /**
    * Executes this function.
    */
   O execute(A... args) throws FunctionException;

   /**
    * Interface for function.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface IFunction0<O> {
      /**
       * Executes this function.
       */
      O execute() throws FunctionException;
   }

   /**
    * Interface for function.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface IFunction1<A, O> {
      /**
       * Executes this function.
       */
      O execute(A arg) throws FunctionException;
   }

   /**
    * Interface for function.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface IFunction2<A1, A2, O> {
      /**
       * Executes this function.
       */
      O execute(A1 arg1, A2 arg2) throws FunctionException;
   }

   /**
    * Interface for function.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface IFunction3<A1, A2, A3, O> {
      /**
       * Executes this function.
       */
      O execute(A1 arg1, A2 arg2, A3 arg3) throws FunctionException;
   }

   /**
    * The exception the execute operation of {@link IFunction} can throw.
    */
   public static class FunctionException extends RuntimeException {
      private static final long serialVersionUID = 6195708707525458567L;

      public FunctionException() {
      }

      public FunctionException(String message) {
         super(message);
      }

      public FunctionException(Throwable cause) {
         super(cause);
      }

      public FunctionException(String message, Throwable cause) {
         super(message, cause);
      }
   }
}
