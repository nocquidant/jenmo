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
 *    - renaming from Value to ConfValue
 *    - a lot (keep it as simple as possible for my needs)
 */
package org.jenmo.common.config;

import java.util.Map;

/**
 * A configuration value.
 * 
 * @author Nicolas Ocquidant (thanks to Marc Prud'hommeaux & Pinaki Poddar)
 * @since 1.0
 */
public class ConfValue<T> {

   private String prop;

   private String def;

   private T value;

   /**
    * Default constructor.
    */
   public ConfValue() {
   }

   /**
    * Constructor. Supply the property name and default value as String.
    */
   public ConfValue(String prop, String def) {
      this.prop = prop;
      this.def = def;
   }

   /**
    * Constructor. Supply the property name, default value as String and value.
    */
   public ConfValue(String prop, String def, T value) {
      this.prop = prop;
      this.def = def;
      set(value);
   }

   /**
    * The property name that will be used when setting or getting this value in a {@link Map}.
    */
   public String getProperty() {
      return prop;
   }

   /**
    * Returns the type of the property that this Value represents.
    */
   @SuppressWarnings("unchecked")
   public Class<T> getValueType() {
      if (value == null) {
         return null;
      }
      return (Class<T>) value.getClass();
   }

   /**
    * The default value for the property as a string.
    */
   public String getDefault() {
      return def;
   }

   /**
    * Subclasses should call this method when their internal value changes.
    */
   public void valueChanged() {
      // To be implemented if needed
   }

   /**
    * Asserts if this receiver can be changed. Subclasses <em>must</em> invoke this method before
    * changing its internal state.
    */
   protected void assertChangeable() {
      // To be implemented if needed
   }

   /**
    * The internal value.
    */
   public T get() {
      return value;
   }

   /**
    * The internal value.
    */
   public void set(T value) {
      assertChangeable();
      T oldValue = this.value;
      this.value = value;
      if (equals(value, oldValue))
         valueChanged();
   }

   private boolean equals(Object o1, Object o2) {
      return o1 == null ? o2 == null : o1.equals(o2);
   }

   @Override
   public boolean equals(Object obj) {
      if ((obj instanceof ConfValue<?>) == false) {
         return false;
      }
      if (obj == this) {
         return true;
      }
      ConfValue<?> that = (ConfValue<?>) obj;
      return this.def.equals(that.def);
   }

   @Override
   public int hashCode() {
      String aUid = "b27b2ef7-38d9-4caf-aef8-7870fef15204";
      int result = (int) aUid.hashCode();
      final int prime = 31;
      return prime * result + def.hashCode();
   }

   /**
    * Accessor for the specified property as a boolean. If the specified property isn't found
    * returns false.
    * 
    * @return Boolean value for the property
    * @throws IllegalStateException
    *            thrown when the property is not available as this type
    */
   public boolean getBoolean() {
      if (value != null) {
         if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
         } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
         }
      } else {
         return false;
      }
      throw new IllegalStateException();
   }

   /**
    * Accessor for the specified property as a String. If the specified property isn't found returns
    * null.
    * 
    * @return String value for the property
    * @throws IllegalStateException
    *            thrown when the property is not available as this type
    */
   public String getString() {
      if (value != null) {
         if (value instanceof String) {
            return (String) value;
         }
      } else {
         return null;
      }
      throw new IllegalStateException();
   }

   /**
    * Accessor for the specified property as an int. If the specified property isn't found returns
    * 0.
    * 
    * @return Int value for the property
    * @throws IllegalStateException
    *            thrown when the property is not available as this type
    */
   public int getInt() {
      if (value != null) {
         if (value instanceof Integer) {
            return ((Integer) value).intValue();
         } else if (value instanceof String) {
            return Integer.parseInt((String) value);
         }
      } else {
         return 0;
      }
      throw new IllegalStateException();
   }
}
