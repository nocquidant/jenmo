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
package org.jenmo.core.listener;

import org.jenmo.core.domain.SplitBlob;
import org.jenmo.core.domain.SplitBlobPart;
import org.jenmo.core.multiarray.MultiArrayBlobPart;

/**
 * An event specialized for {@link SplitBlobPart}. Usefull for instance to commit/evict parts of
 * heavy {@link SplitBlob}.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class SplitBlobEvent implements IEvent {

   private MultiArrayBlobPart source;

   private SplitBlobPart oldPart;

   private SplitBlobPart newPart;

   public SplitBlobEvent(MultiArrayBlobPart source) {
      this.source = source;
   }

   public SplitBlobEvent(MultiArrayBlobPart source, SplitBlobPart oldPart, SplitBlobPart newPart) {
      this.source = source;
      this.oldPart = oldPart;
      this.newPart = newPart;
   }

   public MultiArrayBlobPart getSource() {
      return source;
   }

   public SplitBlobPart getOldPart() {
      return oldPart;
   }

   public SplitBlobPart getNewPart() {
      return newPart;
   }
}
