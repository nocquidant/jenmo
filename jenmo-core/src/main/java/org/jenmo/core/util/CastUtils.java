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
package org.jenmo.core.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jenmo.common.localizer.Localizer;
import org.jenmo.common.util.IFunction.FunctionException;
import org.jenmo.common.util.IFunction.IFunction1;

/**
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public abstract class CastUtils {
   private static final Localizer LOC = Localizer.forPackage(CastUtils.class);

   // Lazy loading...
   private static class FromHolder {
      private static final Map<Class<?>, IFunction1<String, ?>> FROM = new HashMap<Class<?>, IFunction1<String, ?>>();
      static {
         FROM.put(Double.class, new IFunction1<String, Double>() {
            public Double execute(String arg) throws FunctionException {
               return new Double(arg);
            }
         });
         FROM.put(Float.class, new IFunction1<String, Float>() {
            public Float execute(String arg) throws FunctionException {
               return new Float(arg);
            }
         });
         FROM.put(Integer.class, new IFunction1<String, Integer>() {
            public Integer execute(String arg) throws FunctionException {
               return new Integer(arg);
            }
         });
         FROM.put(Long.class, new IFunction1<String, Long>() {
            public Long execute(String arg) throws FunctionException {
               return new Long(arg);
            }
         });
         FROM.put(Short.class, new IFunction1<String, Short>() {
            public Short execute(String arg) throws FunctionException {
               return new Short(arg);
            }
         });
         FROM.put(Byte.class, new IFunction1<String, Byte>() {
            public Byte execute(String arg) throws FunctionException {
               return new Byte(arg);
            }
         });
         FROM.put(BigDecimal.class, new IFunction1<String, BigDecimal>() {
            public BigDecimal execute(String arg) throws FunctionException {
               return new BigDecimal(arg);
            }
         });
         FROM.put(String.class, new IFunction1<String, String>() {
            public String execute(String arg) throws FunctionException {
               return arg;
            }
         });
         FROM.put(Boolean.class, new IFunction1<String, Boolean>() {
            public Boolean execute(String arg) throws FunctionException {
               return new Boolean(arg);
            }
         });
         FROM.put(Character.class, new IFunction1<String, Character>() {
            public Character execute(String arg) throws FunctionException {
               return new Character(arg.charAt(0));
            }
         });
         FROM.put(Date.class, new IFunction1<String, Date>() {
            public Date execute(String arg) throws FunctionException {
               return new Date(new Long(arg));
            }
         });
      }
   }

   // Lazy loading...
   private static class ToHolder {
      private static final Map<Class<?>, IFunction1<?, String>> TO = new HashMap<Class<?>, IFunction1<?, String>>();

      static {
         TO.put(Double.class, new IFunction1<Double, String>() {
            public String execute(Double arg) throws FunctionException {
               return arg.toString();
            }
         });
         TO.put(Float.class, new IFunction1<Float, String>() {
            public String execute(Float arg) throws FunctionException {
               return arg.toString();
            }
         });
         TO.put(Integer.class, new IFunction1<Integer, String>() {
            public String execute(Integer arg) throws FunctionException {
               return arg.toString();
            }
         });
         TO.put(Long.class, new IFunction1<Long, String>() {
            public String execute(Long arg) throws FunctionException {
               return arg.toString();
            }
         });
         TO.put(Short.class, new IFunction1<Short, String>() {
            public String execute(Short arg) throws FunctionException {
               return arg.toString();
            }
         });
         TO.put(Byte.class, new IFunction1<Byte, String>() {
            public String execute(Byte arg) throws FunctionException {
               return arg.toString();
            }
         });
         TO.put(BigDecimal.class, new IFunction1<BigDecimal, String>() {
            public String execute(BigDecimal arg) throws FunctionException {
               return arg.toString();
            }
         });
         TO.put(String.class, new IFunction1<String, String>() {
            public String execute(String arg) throws FunctionException {
               return arg;
            }
         });
         TO.put(Boolean.class, new IFunction1<Boolean, String>() {
            public String execute(Boolean arg) throws FunctionException {
               return arg.toString();
            }
         });
         TO.put(Character.class, new IFunction1<Character, String>() {
            public String execute(Character arg) throws FunctionException {
               return arg.toString();
            }
         });
         TO.put(Date.class, new IFunction1<Date, String>() {
            public String execute(Date arg) throws FunctionException {
               return Long.toString(arg.getTime());
            }
         });
      }
   }

   /**
    * Returns a <code>T</code> object holding the value represented by the argument string
    * <code>value</code>.
    */
   @SuppressWarnings("unchecked")
   public static <T> T fromString(final Class<T> clazz, final String value) {
      IFunction1<String, T> func = (IFunction1<String, T>) FromHolder.FROM.get(clazz);
      if (func == null) {
         throw new IllegalArgumentException(LOC.get("ILLEGAL_CLASS_$1", clazz).getMessage());
      }
      return func.execute(value);
   }

   /**
    * Returns a string representation of the given object.
    */
   @SuppressWarnings("unchecked")
   public static <T> String toString(final T value) {
      IFunction1<T, String> func = (IFunction1<T, String>) ToHolder.TO.get(value.getClass());
      if (func == null) {
         throw new IllegalArgumentException(LOC.get("ILLEGAL_CLASS_$1", value.getClass())
               .getMessage());
      }
      return func.execute(value);
   }
}