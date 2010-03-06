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
 * Modifications to genuine file: none
 */
package org.jenmo.common.multiarray;

/**
 * An IndexIterator is a helper class used for stepping through the index values of a
 * {@link IMultiArray}.
 * 
 * @author Nicolas Ocquidant (thanks to Bill Hibbard)
 * @since 1.0
 */
public class IndexIterator {

   private final int[] fromPos;

   private final int[] toPosExcl;

   /**
    * The counter value. Initialized to zero. The length is the same as limits.length.
    */
   private final int[] counter;

   /**
    * A "carry" indicator, the number of times the counter value has rolled over.
    */
   private int ncycles;

   /**
    * Creates a new IndexIterator whose variation is bounded by the component values of the
    * argument.
    * 
    * @param toPosExcl
    *           typically <code>ma.getLengths()</code> for some MultiArray <code>ma</code>
    */
   public IndexIterator(final int[] toPosExcl) {
      this.fromPos = new int[toPosExcl.length];
      this.toPosExcl = toPosExcl; // N.B not a copy
      counter = new int[toPosExcl.length];
      ncycles = 0;
   }

   /**
    * Creates a new IndexIterator with initial counter value, whose variation is bounded by the
    * component values of the <code>toPosExcl</code> argument.
    * 
    * @param fromPos
    *           the initial value.
    * @param toPosExcl
    *           typically <code>ma.getLengths()</code> for some MultiArray <code>ma</code>
    */
   public IndexIterator(final int[] fromPos, final int[] toPosExcl) {
      this.fromPos = fromPos; // N.B not a copy
      this.toPosExcl = toPosExcl; // N.B not a copy
      counter = fromPos.clone();
      ncycles = 0;
   }

   /**
    * If the IndexIterator has not yet "rolled over", return <code>true</code>. Useful for loop end
    * detection.
    */
   public boolean notDone() {
      if (ncycles > 0)
         return false;
      return true;
   }

   /**
    * Return the current counter value. N.B. Not a copy!
    */
   public int[] value() {
      return counter;
   }

   /**
    * Increment the counter value
    */
   public void incr() {
      int digit = counter.length - 1;
      if (digit < 0) {
         // counter is zero length array <==> scalar
         ncycles++;
         return;
      }
      while (digit >= 0) {
         counter[digit]++;
         if (counter[digit] < toPosExcl[digit]) {
            break; // normal exit
         }
         // else, carry on
         counter[digit] = fromPos[digit];
         if (digit == 0) {
            ncycles++; // rolled over
            break;
         }
         // else
         digit--;
      }
   }

   /**
    * Increment the counter value
    * 
    * @param nsteps
    *           the number of times to increment the value.
    */
   public void advance(int nsteps) {
      // TODO: make this smarter and faster;
      while (nsteps-- > 0)
         incr();
   }

   public String toString() {
      StringBuffer buf = new StringBuffer();
      final int last = counter.length - 1;
      for (int ii = 0; ii <= last; ii++) {
         buf.append(counter[ii]);
         if (ii == last)
            break; // normal loop exit
         buf.append(" ");
      }
      return buf.toString();
   }
}
