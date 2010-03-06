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
 * --
 * From http://code.google.com/p/scoped-entitymanager/
 * Modifications to genuine file: none
 */
package org.jenmo.core.persistence;

import javax.persistence.EntityManager;

/**
 * This class is a wrapper for the Actual EntityManager: it is created by ScopedEntityManagerFactory
 * (through a ThreadLocal) and used by the SessionHelper class (which could be used by a HTTP
 * request listener if in a web environment).
 * <p>
 * The important thing to highlight is that the real <code>close()</code> method has no effect: in
 * case a client code invoke this method, it has nothing to do. <code>lazyClose()</code> method is
 * the one who close the actual EntityManager and is invoked by the SessionHelper class.
 * 
 * @author Nicolas Ocquidant (thanks to puncherico)
 * @since 1.0
 */
public class LazyCloseEntityManager extends EntityManagerProxy {

   private LazyCloseListener listener;

   public LazyCloseEntityManager(EntityManager delegate) {

      super(delegate);
   }

   public void setLazyCloseListener(LazyCloseListener listener) {
      this.listener = listener;
   }

   public LazyCloseListener getLazyCloseListener() {
      return listener;
   }

   @Override
   public void close() {
   }

   protected void lazyClose() {
      super.close();
      if (listener != null)
         listener.lazilyClosed();
   }
}