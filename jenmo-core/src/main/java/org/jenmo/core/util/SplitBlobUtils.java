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

import org.jenmo.common.localizer.Localizer;

/**
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public abstract class SplitBlobUtils {
   private static final Localizer LOC = Localizer.forPackage(SplitBlobUtils.class);

   public static enum PType {
      PDOUBLE, PFLOAT, PLONG, PINTEGER, PSHORT, PBYTE;
   }

   /**
    * Used to figure out how storage is required for a given shape. Compute the right to left
    * multiplicative product of the argument.
    * 
    * @param dimensions
    *           the shape.
    * @return product of the dimensions.
    */
   public static int numberOfElements(final int[] dimensions) {
      int product = 1;
      for (int ii = dimensions.length - 1; ii >= 0; ii--) {
         product *= dimensions[ii];
      }
      return product;
   }

   /**
    * Used to figure out how storage is required for a given shape, retaining intermediate products.
    * Compute the right to left multiplicative product of the first argument, modifying the second
    * argument so that it contains the intermediate products.
    * 
    * @return product of the dimensions.
    * @param dimensions
    *           the shape.
    * @param products
    *           modified upon return to contain the intermediate products
    */
   public static int numberOfElements(int[] dimensions, int[] products) {
      int product = 1;
      for (int ii = dimensions.length - 1; ii >= 0; ii--) {
         final int thisDim = dimensions[ii];
         if (thisDim < 0)
            throw new NegativeArraySizeException();
         products[ii] = product;
         product *= thisDim;
      }
      return product;
   }

   /**
    * Returns the number of dimensions of the array represented by the given Class object.
    * 
    * @return number of dimensions of the array represented by the given Class object
    */
   public static int getRank(Class<?> clazz) {
      int rank = 0;
      Class<?> componentType = clazz;
      while (componentType.isArray()) {
         componentType = componentType.getComponentType();
         rank++;
      }
      return rank;
   }

   /**
    * Returns the Class object representing the component type of the array represented by the given
    * Class object.
    * 
    * @return the component type of the array represented by the given Class object
    */
   public static Class<?> getComponentType(Class<?> clazz) {
      Class<?> componentType = clazz;
      while (componentType.isArray()) {
         componentType = componentType.getComponentType();
      }
      return componentType;
   }

   /**
    * Discover the dimensions of this multidimensional array given as object.
    * 
    * @return array whose length is the rank of this multidimensional and whose elements represent
    *         the length of each of it's dimensions
    */
   public static int[] getShape(Object array, int rank) {
      int count = 0;
      int[] shape = new int[rank];
      Object jArray = array;
      Class<?> cType = jArray.getClass();
      while (cType.isArray()) {
         shape[count++] = java.lang.reflect.Array.getLength(jArray);
         jArray = java.lang.reflect.Array.get(jArray, 0);
         cType = jArray.getClass();
      }
      return shape;
   }

   /**
    * Computes size for the given shape array.
    * 
    * @return size for the given shape array
    */
   public static long computeSize(int[] shape) {
      long product = 1;
      for (int ii = shape.length - 1; ii >= 0; ii--)
         product *= shape[ii];
      return product;
   }

   /**
    * Returns size of primitive.
    */
   public static final int sizeOf(final PType type) {
      switch (type) {
      case PDOUBLE:
         return 8;
      case PFLOAT:
         return 4;
      case PLONG:
         return 8;
      case PINTEGER:
         return 4;
      case PSHORT:
         return 2;
      case PBYTE:
         return 1;
      default:
         break;
      }
      return -1;
   }

   /**
    * Returns given primitive class as {@link PType} enum.
    */
   public static PType extractType(final Class<?> primitive) {
      if (primitive == Double.TYPE) {
         return PType.PDOUBLE;
      }
      if (primitive == Float.TYPE) {
         return PType.PFLOAT;
      }
      if (primitive == Integer.TYPE) {
         return PType.PINTEGER;
      }
      if (primitive == Short.TYPE) {
         return PType.PSHORT;
      }
      if (primitive == Byte.TYPE) {
         return PType.PBYTE;
      }
      throw new IllegalArgumentException(LOC.get("ILLEGAL_CLASS_$1", primitive).getMessage());
   }

   /**
    * Returns given {@link PType} enum as primitive class.
    */
   public static final Class<?> getClassFor(final PType type) {
      switch (type) {
      case PDOUBLE:
         return Double.TYPE;
      case PFLOAT:
         return Float.TYPE;
      case PLONG:
         return Long.TYPE;
      case PINTEGER:
         return Integer.TYPE;
      case PSHORT:
         return Short.TYPE;
      case PBYTE:
         return Byte.TYPE;
      default:
         break;
      }
      throw new IllegalArgumentException(LOC.get("ILLEGAL_PTYPE_$1", type).getMessage());
   }
}