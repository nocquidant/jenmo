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
package org.jenmo.core.testutil;

import javax.persistence.EntityManager;

public abstract class DbUseCase {

   protected abstract void reallyExecute(EntityManager em) throws DbUseCaseException;

   public final void execute(EntityManager em, boolean closeEm) throws DbUseCaseException {
      try {
         try {
            if (em.getTransaction().isActive() == false) {
               em.getTransaction().begin();
            }
            reallyExecute(em);
            if (em.getTransaction().isActive()) {
               em.getTransaction().commit();
            }
         } finally {
            if (em.getTransaction().isActive()) {
               em.getTransaction().rollback();
            }
         }
      } finally {
         if (closeEm) {
            em.close();
         }
      }
   }

   public static class DbUseCaseException extends RuntimeException {
      private static final long serialVersionUID = 5594925166368143205L;

      public DbUseCaseException() {
      }

      public DbUseCaseException(String message) {
         super(message);
      }

      public DbUseCaseException(Throwable cause) {
         super(cause);
      }

      public DbUseCaseException(String message, Throwable cause) {
         super(message, cause);
      }
   }
}
