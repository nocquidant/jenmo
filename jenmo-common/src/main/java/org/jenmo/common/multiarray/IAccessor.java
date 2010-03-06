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
 *    - renamed Accessor to IAccessor
 *    - removed methods: toArray & copyIn & copyOut
 */
package org.jenmo.common.multiarray;

/**
 * Interface for multidimensional array data access. Given an index (array of integers), get or set
 * the value at index.
 * <p>
 * The primitive specific get and set methods are useful only if the the componentType is primitive
 * (like {@link java.lang.Double#TYPE}).
 * 
 * @author Nicolas Ocquidant (thanks to Bill Hibbard)
 * @since 1.0
 */
public interface IAccessor {
   /**
    * Get (read) the array element at index. The returned value is wrapped in an object if it has a
    * primitive type. Length of index must be greater than or equal to the rank of this. Values of
    * index components must be less than corresponding values from getLengths().
    * 
    * @param index
    *           MultiArray index
    * @return Object value at <code>index</code>
    * @exception NullPointerException
    *               If the argument is null.
    * @exception IllegalArgumentException
    *               If the array length of index is too small
    * @exception ArrayIndexOutOfBoundsException
    *               If an index component argument is negative, or if it is greater than or equal to
    *               the corresponding dimension length.
    */
   Object get(int[] index);

   /**
    * Get the array element at index, as a boolean.
    */
   boolean getBoolean(int[] index);

   /**
    * Get the array element at index, as a char.
    */
   char getChar(int[] index);

   /**
    * Get the array element at index, as a byte.
    */
   byte getByte(int[] index);

   /**
    * Get the array element at index, as a short.
    */
   short getShort(int[] index);

   /**
    * Get the array element at index, as an int.
    */
   int getInt(int[] index);

   /**
    * Get the array element at index, as a long.
    */
   long getLong(int[] index);

   /**
    * Get the array element at index, as a float.
    */
   float getFloat(int[] index);

   /**
    * Get the array element at index, as a double.
    */
   double getDouble(int[] index);

   /**
    * Set (modify, write) the array element at index to the specified value. If the array has a
    * primitive component type, the value may be unwrapped. Values of index components must be less
    * than corresponding values from getLengths().
    * 
    * @param index
    *           MultiArray index
    * @param value
    *           the new value.
    * @exception NullPointerException
    *               If the index argument is null, or if the array has a primitive component type
    *               and the value argument is null
    * @exception IllegalArgumentException
    *               If the array length of index is too small
    * @exception ArrayIndexOutOfBoundsException
    *               If an index component argument is negative, or if it is greater than or equal to
    *               the corresponding dimension length.
    */
   void set(int[] index, Object value);

   /**
    * Set the array element at index to the specified boolean value.
    */
   void setBoolean(int[] index, boolean value);

   /**
    * Set the array element at index to the specified char value.
    */
   void setChar(int[] index, char value);

   /**
    * Set the array element at index to the specified byte value.
    */
   void setByte(int[] index, byte value);

   /**
    * Set the array element at index to the specified short value.
    */
   void setShort(int[] index, short value);

   /**
    * Set the array element at index to the specified int value.
    */
   void setInt(int[] index, int value);

   /**
    * Set the array element at index to the specified long value.
    */
   void setLong(int[] index, long value);

   /**
    * Set the array element at index to the specified float value.
    */
   void setFloat(int[] index, float value);

   /**
    * Set the array element at index to the specified double value.
    */
   void setDouble(int[] index, double value);
}
