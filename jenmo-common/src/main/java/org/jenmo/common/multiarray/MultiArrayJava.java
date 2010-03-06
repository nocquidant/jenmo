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
 * From http://www.ssec.wisc.edu/~billh/visad.html
 * Modifications to genuine file: 
 *    - renamed MultiArrayImpl to MultiArrayJava
 */
package org.jenmo.common.multiarray;

import java.lang.reflect.Array;

/**
 * {@link IMultiArray} implementation which is an adapter for java language arrays. If you have a
 * java array and want to wrap it in a {@link IMultiArray} interface, use this class. Rank of these
 * is always > 0 (scalars as {@link IMultiArray} are not supported).
 * 
 * @author Nicolas Ocquidant (thanks to Bill Hibbard)
 * @since 1.0
 */
public class MultiArrayJava implements IMultiArray {

   /**
    * The java language array which this adapts.
    */
   public final Object jla;

   /**
    * The rank is the number of indices.
    */
   private final int rank;

   /**
    * Class of the primitives or objects to be contained.
    */
   private final Class<?> componentType;

   /**
    * Package private constructor which avoids some of the protections of the public constructor
    * below.
    */
   MultiArrayJava(Object aro, int theRank, Class<?> componentType) {
      jla = aro;
      rank = theRank;
      this.componentType = componentType;
   }

   /**
    * Given a java Object, typically an array of primitive (or an array of array of primitive ...),
    * Provide a IMultiArray interface to the provided object. The wrapper provided does not copy the
    * object, so it remains accessible via the language [] notation and the
    * {@link java.lang.reflect.Array} methods. Synchronization not provided by this interface.
    * 
    * @param storage
    *           a (multi-dimensional) array of primitives.
    */
   public MultiArrayJava(Object storage) {
      int rank_ = 0;
      Class<?> componentType_ = storage.getClass();
      while (componentType_.isArray()) {
         rank_++;
         componentType_ = componentType_.getComponentType();
      }
      if (rank_ == 0)
         throw new IllegalArgumentException();
      jla = storage;
      rank = rank_;
      componentType = componentType_;
   }

   /**
    * Create a new IMultiArray of the given componentType and shape. Storage for the values is
    * allocated and owned by this with default initialization.
    * 
    * @param componentType
    *           Class of the primitives or objects to be contained.
    * @param dimensions
    *           the shape of the MultiArray. dimensions.length determines the rank of the new
    *           MultiArray.
    */
   public MultiArrayJava(Class<?> componentType, int[] dimensions) {
      rank = dimensions.length;
      if (rank == 0)
         throw new IllegalArgumentException();
      this.componentType = componentType;
      jla = Array.newInstance(componentType, dimensions);
   }

   /* Begin IMultiArray Inquiry methods from IMultiArrayInfo */

   /**
    * Returns the Class object representing the component type of the wrapped array. If the rank is
    * greater than 1, this will be the component type of the leaf (rightmost) nested array.
    * 
    * @see IMultiArrayInfo#getComponentType
    * @return Class the component type
    */
   @Override
   public Class<?> getComponentType() {
      return componentType;
   }

   /**
    * @see IMultiArrayInfo#getRank
    * @return int number of dimensions of the array
    */
   @Override
   public int getRank() {
      return rank;
   }

   /**
    * As if {@link java.lang.reflect.Array#getLength(Object)} were called recursively on the wrapped
    * object, return the dimension lengths.
    * 
    * @see IMultiArrayInfo#getLengths
    * 
    * @return int array whose length is the rank of this IMultiArray and whose elements represent
    *         the length of each of it's dimensions
    */
   @Override
   public int[] getLengths() {
      int[] lengths = new int[rank];
      Object oo = jla;
      for (int ii = 0; ii < rank; ii++) {
         lengths[ii] = Array.getLength(oo);
         oo = Array.get(oo, 0);
      }
      return lengths;
   }

   /**
    * Returns <code>true</code> if and only if the effective dimension lengths can change. Always
    * returns <code>false</code> for this class.
    * 
    * @see IMultiArrayInfo#isUnlimited
    * @return boolean <code>false</code>
    */
   @Override
   public boolean isUnlimited() {
      return false;
   }

   /**
    * Always returns false for this class.
    * 
    * @see IMultiArrayInfo#isScalar
    * @return false
    */
   @Override
   public boolean isScalar() {
      return rank == 0;
   }

   /* End IMultiArrayInfo */

   /* Begin IMultiArray Access methods from IMultiArrayAccessor */

   private final Object getLastButOneArray(final int[] index) {
      // TODO put a lastLeafArray variable to avoid unnecessary 'Array.get(Object, int)'?
      if (index.length < rank) {
         throw new IllegalArgumentException();
      }
      final int end = rank - 1;
      Object oo = jla;
      for (int ii = 0; ii < end; ii++) {
         oo = Array.get(oo, index[ii]);
      }
      return oo;
   }

   /**
    * @see IAccessor#get
    */
   public final Object get(final int[] index) {
      final int end = rank - 1;
      return Array.get(getLastButOneArray(index), index[end]);
   }

   /**
    * @see IAccessor#getBoolean
    */
   public final boolean getBoolean(final int[] index) {
      final int end = rank - 1;
      return Array.getBoolean(getLastButOneArray(index), index[end]);
   }

   /**
    * @see IAccessor#getChar
    */
   public final char getChar(final int[] index) {
      final int end = rank - 1;
      return Array.getChar(getLastButOneArray(index), index[end]);
   }

   /**
    * @see IAccessor#getByte
    */
   public final byte getByte(final int[] index) {
      final int end = rank - 1;
      return Array.getByte(getLastButOneArray(index), index[end]);
   }

   /**
    * @see IAccessor#getShort
    */
   public final short getShort(final int[] index) {
      final int end = rank - 1;
      return Array.getShort(getLastButOneArray(index), index[end]);
   }

   /**
    * @see IAccessor#getInt
    */
   public final int getInt(final int[] index) {
      final int end = rank - 1;
      return Array.getInt(getLastButOneArray(index), index[end]);
   }

   /**
    * @see IAccessor#getLong
    */
   public final long getLong(final int[] index) {
      final int end = rank - 1;
      return Array.getLong(getLastButOneArray(index), index[end]);
   }

   /**
    * @see IAccessor#getFloat
    */
   public final float getFloat(final int[] index) {
      final int end = rank - 1;
      return Array.getFloat(getLastButOneArray(index), index[end]);
   }

   /**
    * @see IAccessor#getDouble
    */
   public final double getDouble(final int[] index) {
      final int end = rank - 1;
      return Array.getDouble(getLastButOneArray(index), index[end]);
   }

   /**
    * @see IAccessor#set
    */
   public final void set(final int[] index, Object value) {
      final int end = rank - 1;
      Array.set(getLastButOneArray(index), index[end], value);
      return;
   }

   /**
    * @see IAccessor#setBoolean
    */
   public final void setBoolean(final int[] index, boolean value) {
      final int end = rank - 1;
      Array.setBoolean(getLastButOneArray(index), index[end], value);
   }

   /**
    * @see IAccessor#setChar
    */
   public final void setChar(final int[] index, char value) {
      final int end = rank - 1;
      Array.setChar(getLastButOneArray(index), index[end], value);
   }

   /**
    * @see IAccessor#setByte
    */
   public final void setByte(final int[] index, byte value) {
      final int end = rank - 1;
      Array.setByte(getLastButOneArray(index), index[end], value);
   }

   /**
    * @see IAccessor#setShort
    */
   public final void setShort(final int[] index, short value) {
      final int end = rank - 1;
      Array.setShort(getLastButOneArray(index), index[end], value);
   }

   /**
    * @see IAccessor#setInt
    */
   public final void setInt(final int[] index, int value) {
      final int end = rank - 1;
      Array.setInt(getLastButOneArray(index), index[end], value);
   }

   /**
    * @see IAccessor#setLong
    */
   public final void setLong(final int[] index, long value) {
      final int end = rank - 1;
      Array.setLong(getLastButOneArray(index), index[end], value);
   }

   /**
    * @see IAccessor#setFloat
    */
   public final void setFloat(final int[] index, float value) {
      final int end = rank - 1;
      Array.setFloat(getLastButOneArray(index), index[end], value);
   }

   /**
    * @see IAccessor#setDouble
    */
   public final void setDouble(final int[] index, double value) {
      final int end = rank - 1;
      Array.setDouble(getLastButOneArray(index), index[end], value);
   }

   public Object getStorage() {
      return jla;
   }

   /* End IMultiArrayAccessor */
}
