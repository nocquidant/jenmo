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
package org.jenmo.core.repository;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * A default delete operation implementation i.e. with disabled <code>isDeletable(PK)</code> and
 * <code>deleteWithoutTest(OK)</code> operations.
 * 
 * @param <T>
 *           Type of persistent entity
 * @param <PK>
 *           Type of primary key
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class DefaultDeleteAction<T, PK extends Serializable> implements IDeleteAction<T, PK> {
   /** The logger. */
   protected static final Logger LOGGER = Logger.getLogger(DefaultDeleteAction.class);

   private DefaultDaoJPA<T, PK> caller;

   public DefaultDeleteAction(DefaultDaoJPA<T, PK> caller) {
      this.caller = caller;
   }

   protected DefaultDaoJPA<T, PK> getCaller() {
      return caller;
   }

   @Override
   public void delete(PK id) {
      caller.defaultDelete(id);
   }
}
