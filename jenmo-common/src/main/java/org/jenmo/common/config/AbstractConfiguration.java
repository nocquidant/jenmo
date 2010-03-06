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
package org.jenmo.common.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract configuration class.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public abstract class AbstractConfiguration implements IConfiguration {
   private final Map<String, ConfValue<?>> properties = new HashMap<String, ConfValue<?>>();

   @Override
   public void fromProperties(Map<String, ConfValue<?>> props) {
      for (Map.Entry<String, ConfValue<?>> entry : props.entrySet()) {
         addValue(entry.getValue());
      }
   }

   @Override
   public Map<String, ConfValue<?>> toProperties() {
      return new HashMap<String, ConfValue<?>>(properties);
   }

   @Override
   public ConfValue<?> addValue(ConfValue<?> val) {
      return properties.put(val.getProperty(), val);
   }

   @Override
   public ConfValue<?> getValue(String property) {
      return properties.get(property);
   }

   @Override
   public boolean removeValue(ConfValue<?> val) {
      return (properties.remove(val) != null);
   }
}