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
package org.jenmo.core.multiarray;

import java.lang.ref.WeakReference;

import org.apache.log4j.Logger;
import org.jenmo.common.multiarray.IAccessor;
import org.jenmo.common.multiarray.IMultiArray;
import org.jenmo.common.multiarray.IMultiArrayInfo;
import org.jenmo.core.domain.SplitBlob;
import org.jenmo.core.domain.SplitBlobPart;
import org.jenmo.core.listener.IListener;
import org.jenmo.core.listener.SplitBlobEvent;
import org.jenmo.core.util.SplitBlobUtils;

/**
 * A {@link SplitBlob} implementation of the IMultiArray interface.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class MultiArrayBlobPart implements IMultiArray, IBlobPartAccessor {
   /** The logger. */
   private static final Logger LOGGER = Logger.getLogger(MultiArrayBlobPart.class);

   /**
    * Right to left products used in indexMap() to compute offset into the array. When incrementing
    * index[ii], one jumps through storage by products[ii].
    */
   private final int[] products;

   private final int[] lengths;

   private final int sizeForParts;

   /** The actual storage. */
   private final SplitBlob storage;

   /** The part index we are currently working with. */
   private transient int currentPartIdx;

   /** The part we are currently working with. */
   private transient SplitBlobPart currentPart;

   /** The listener of blob part changes. */
   private WeakReference<IListener<SplitBlobEvent>> listener;

   /**
    * Create a new MultiArrayImpl of the given shape accessing externally created storage. It is up
    * to the client to to mitigate conflicting access to the external storage.
    * 
    * @param storage
    *           array Object which is storage
    */
   public MultiArrayBlobPart(SplitBlob storage) {
      this.lengths = storage.getShape();
      this.sizeForParts = storage.getElmtCountEachPart();
      this.products = new int[lengths.length];
      final int length = SplitBlobUtils.numberOfElements(this.lengths, this.products);
      if (length > storage.getElmtCount())
         throw new IllegalArgumentException("Inadequate storage");
      this.storage = storage;
      // Some optimizations
      this.currentPartIdx = -1;
      this.currentPart = null;
   }

   /* IMultiArray Inquiry methods from IMultiArrayInfo */

   /**
    * @see IMultiArrayInfo#getComponentType
    */
   public Class<?> getComponentType() {
      return SplitBlobUtils.getClassFor(storage.getElmtType());
   }

   /**
    * @see IMultiArrayInfo#getRank
    */
   public int getRank() {
      return lengths.length;
   }

   /**
    * @see IMultiArrayInfo#getLengths
    */
   public int[] getLengths() {
      return lengths.clone();
   }

   /**
    * @see IMultiArrayInfo#isUnlimited
    */
   public boolean isUnlimited() {
      return false;
   }

   /**
    * @see IMultiArrayInfo#isScalar
    */
   public boolean isScalar() {
      return 0 == getRank();
   }

   /* End IMultiArrayInfo */

   /* MultiArray Access methods from IMultiArrayAccessor */

   private int indexMap(final int[] index) {
      int value = 0;
      for (int ii = 0; ii < lengths.length; ii++) {
         final int thisIndex = index[ii];
         if (thisIndex < 0 || thisIndex >= lengths[ii])
            throw new ArrayIndexOutOfBoundsException();
         value += thisIndex * products[ii];
      }
      return value;
   }

   private SplitBlobPart getPart(int index) {
      if (currentPartIdx != index) {
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting blob part: " + index);
         }
         SplitBlobPart part = storage.getPart(index);
         onPartChange(currentPart, part);
         currentPart = part;
         currentPartIdx = index;
      }
      return currentPart;
   }

   private void onPartChange(SplitBlobPart previousPart, SplitBlobPart newPart) {
      // Very first part?
      if (previousPart != null) {
         previousPart.closeBuffer();
      }
      // Here previousPart is null at first time
      if (listener != null) {
         listener.get().update(new SplitBlobEvent(this, previousPart, newPart), null);
      }
      // Open new part
      newPart.openBuffer();
   }

   /**
    * @see IAccessor#get
    */
   public final Object get(final int[] index) {
      throw new UnsupportedOperationException();
   }

   /**
    * @see IAccessor#getBoolean
    */
   public final boolean getBoolean(final int[] index) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 1;
      return getPart(idx1).getBoolean(idx2);
   }

   /**
    * @see IAccessor#getChar
    */
   public final char getChar(final int[] index) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 1;
      return getPart(idx1).getChar(idx2);
   }

   /**
    * @see IAccessor#getByte
    */
   public final byte getByte(final int[] index) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 1;
      return getPart(idx1).getByte(idx2);
   }

   /**
    * @see IAccessor#getShort
    */
   public final short getShort(final int[] index) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 2;
      return getPart(idx1).getShort(idx2);
   }

   /**
    * @see IAccessor#getInt
    */
   public final int getInt(final int[] index) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 4;
      return getPart(idx1).getInt(idx2);
   }

   /**
    * @see IAccessor#getLong
    */
   public final long getLong(final int[] index) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 8;
      return getPart(idx1).getLong(idx2);
   }

   /**
    * @see IAccessor#getFloat
    */
   public final float getFloat(final int[] index) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 4;
      return getPart(idx1).getFloat(idx2);
   }

   /**
    * @see IAccessor#getDouble
    */
   public final double getDouble(final int[] index) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 8;
      return getPart(idx1).getDouble(idx2);
   }

   /**
    * @see IAccessor#set
    */
   public final void set(final int[] index, final Object value) {
      throw new UnsupportedOperationException();
   }

   /**
    * @see IAccessor#setBoolean
    */
   public final void setBoolean(final int[] index, final boolean value) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 1;
      getPart(idx1).putBoolean(idx2, value);
   }

   /**
    * @see IAccessor#setChar
    */
   public final void setChar(final int[] index, final char value) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 1;
      getPart(idx1).putChar(idx2, value);
   }

   /**
    * @see IAccessor#setByte
    */
   public final void setByte(final int[] index, final byte value) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 1;
      getPart(idx1).putByte(idx2, value);
   }

   /**
    * @see IAccessor#setShort
    */
   public final void setShort(final int[] index, final short value) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 2;
      getPart(idx1).putShort(idx2, value);
   }

   /**
    * @see IAccessor#setInt
    */
   public final void setInt(final int[] index, final int value) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 4;
      getPart(idx1).putInt(idx2, value);
   }

   /**
    * @see IAccessor#setLong
    */
   public final void setLong(final int[] index, final long value) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 8;
      getPart(idx1).putLong(idx2, value);
   }

   /**
    * @see IAccessor#setFloat
    */
   public final void setFloat(final int[] index, final float value) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 4;
      getPart(idx1).putFloat(idx2, value);
   }

   /**
    * @see IAccessor#setDouble
    */
   public final void setDouble(final int[] index, final double value) {
      final int idx = indexMap(index);
      final int idx1 = idx / sizeForParts;
      final int idx2 = (idx % sizeForParts) * 8;
      getPart(idx1).putDouble(idx2, value);
   }

   /**
    * @see IMultiArray#getStorage
    */
   public SplitBlob getStorage() {
      return storage;
   }

   /* End IMultiArrayAccessor */

   public void setPartListener(IListener<SplitBlobEvent> l) {
      if (l != null) {
         listener = new WeakReference<IListener<SplitBlobEvent>>(l);
      }
   }

   public void removePartListener() {
      listener = null;
   }

   @Override
   public void close() {
      if (currentPart != null) {
         currentPart.closeBuffer();
      }
      this.listener = null;
      this.currentPartIdx = -1;
      this.currentPart = null;
   }
}
