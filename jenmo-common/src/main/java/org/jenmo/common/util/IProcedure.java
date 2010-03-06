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
 * Interface for procedure.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public interface IProcedure<A> {
   /**
    * Executes this procedure. A {@code false} return value indicates that the application executing
    * this procedure should not invoke this procedure again.
    */
   boolean execute(A... args) throws ProcedureException;

   /**
    * Interface for procedure.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface IProcedure0 {
      /**
       * Executes this procedure. A false return value indicates that the application executing this
       * procedure should not invoke this procedure again.
       */
      boolean execute() throws ProcedureException;
   }

   /**
    * Interface for procedure.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface IProcedure1<A> {
      /**
       * Executes this procedure. A false return value indicates that the application executing this
       * procedure should not invoke this procedure again.
       */
      boolean execute(A arg) throws ProcedureException;
   }

   /**
    * Interface for procedure.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface IProcedure2<A1, A2> {
      /**
       * Executes this procedure. A false return value indicates that the application executing this
       * procedure should not invoke this procedure again.
       */
      boolean execute(A1 arg1, A2 arg2) throws ProcedureException;
   }

   /**
    * Interface for procedure.
    * 
    * @author Nicolas Ocquidant
    * @since 1.0
    */
   public static interface IProcedure3<A1, A2, A3> {
      /**
       * Executes this procedure. A false return value indicates that the application executing this
       * procedure should not invoke this procedure again.
       */
      boolean execute(A1 arg1, A2 arg2, A3 arg3) throws ProcedureException;
   }

   /**
    * The exception the execute operation of {@link IProcedure} can throw.
    */
   public static class ProcedureException extends RuntimeException {
      private static final long serialVersionUID = -2053040431158756604L;

      public ProcedureException() {
      }

      public ProcedureException(String message) {
         super(message);
      }

      public ProcedureException(Throwable cause) {
         super(cause);
      }

      public ProcedureException(String message, Throwable cause) {
         super(message, cause);
      }
   }
}
