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
package org.jenmo.core.holder;

import org.jenmo.core.domain.Node;

/**
 * Instead of using {@link Node} directly, client code may choose to manipulate {@link INodeHolder}.
 * Each {@link Node} could have one and only one {@link INodeHolder}: there is a 1..1 relation.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public interface INodeHolder {
   /**
    * Gets underlying {@link Node} entity for this {@link INodeHolder}.
    */
   Node getInnerEntity();

   /**
    * Sets underlying {@link Node} entity for this {@link INodeHolder}.
    */
   void setInnerEntity(Node node);

   /**
    * Gets underlying {@link Node} entity id.
    */
   long getId();
}
