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

/**
 * Interface for delete operations as all domain objects couldn't be defined using the
 * <code>Dependent</code> or <code>ElementDependent</code> tag.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public interface IDeleteAction<T, PK extends Serializable> {

   /**
    * Remove the object identified by <code>id</code>, cascade deleting children.
    */
   void delete(PK id);
}
