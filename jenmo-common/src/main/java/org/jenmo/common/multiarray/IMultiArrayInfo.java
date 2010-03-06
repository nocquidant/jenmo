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
 *    - renamed MultiArrayInfo to IMultiArrayInfo
 *    - added generics
 */
package org.jenmo.common.multiarray;

/**
 * Inquiry or introspection interface for abstract multidimensional arrays. The {@link IMultiArray}
 * interface extends this by adding data access operations.
 * 
 * @author Nicolas Ocquidant (thanks to Bill Hibbard)
 * @since 1.0
 */
public interface IMultiArrayInfo {
   /**
    * Returns the Class object representing the component type of the array.
    * 
    * @return Class the component type
    * @see java.lang.Class#getComponentType
    */
   Class<?> getComponentType();

   /**
    * Returns the number of dimensions of the array.
    * 
    * @return int number of dimensions of the array
    */
   int getRank();

   /**
    * Discover the dimensions of this MultiArray.
    * 
    * @return int array whose length is the rank of this IMultiArray and whose elements represent
    *         the length of each of it's dimensions
    */
   int[] getLengths();

   /**
    * Returns <code>true</code> if and only if the effective dimension lengths can change. For
    * example, if this were implemented by a {@link java.util.List}.
    * 
    * @return boolean <code>true</code> if this can grow
    */
   boolean isUnlimited();

   /**
    * Convenience interface; return <code>true</code> if and only if the rank is zero.
    * 
    * @return boolean <code>true</code> if rank == 0
    */
   boolean isScalar();
}
