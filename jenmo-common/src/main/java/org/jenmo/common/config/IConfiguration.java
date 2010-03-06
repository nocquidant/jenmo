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
 * From OpenJPA: http://openjpa.apache.org/
 * Modifications to genuine file:
 *    - renaming from Configuration to IConfiguration
 *    - a lot (keep it as simple as possible for my needs)
 */
package org.jenmo.common.config;

import java.util.Map;
import java.util.Properties;

/**
 * Interface for generic configuration objects. Includes the ability to write configuration to and
 * from {@link Properties} instances.
 * 
 * @author Nicolas Ocquidant (thanks to Marc Prud'hommeaux & Abe White)
 * @since 1.0
 */
public interface IConfiguration {

   /**
    * Set this IConfiguration via the given map. Any keys missing from the given map will not be
    * set. Note that changes made to this map will not be automatically reflected in this
    * IConfiguration object.
    */
   void fromProperties(Map<String, ConfValue<?>> map);

   /**
    * A properties representation of this IConfiguration. Note that changes made to this properties
    * object will not be automatically reflected in this IConfiguration object.
    */
   Map<String, ConfValue<?>> toProperties();

   /**
    * Return the value for the given property, or null if none.
    */
   ConfValue<?> getValue(String property);

   /**
    * Add the given value to the set of configuration properties. This method replaces any existing
    * value under the same property.
    */
   ConfValue<?> addValue(ConfValue<?> val);

   /**
    * Remove the given value from the set of configuration properties.
    */
   boolean removeValue(ConfValue<?> val);
}
