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
package org.jenmo.core.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;

public class AbstractAdapterCase extends AbstractSizedCase {
   protected static void checkSum(int expectedCount, double expectedSum, Iterator<Double> it) {
      int count = 0;
      double sum = 0;
      while (it.hasNext()) {
         Double each = (Double) it.next();
         count++;
         sum += each;
      }
      Assert.assertEquals(count, expectedCount);
      Assert.assertEquals(sum, expectedSum, 10e-9);
   }

   protected static double getExpectedSum() {
      return SIZE + (SIZE * (SIZE - 1) / 2.);
   }

   protected static List<Double> getCollForAll() {
      List<Double> out = new ArrayList<Double>(SIZE);
      for (int i = 0; i < SIZE; i++) {
         out.add(new Double(i + 1.));
      }
      return out;
   }
}
