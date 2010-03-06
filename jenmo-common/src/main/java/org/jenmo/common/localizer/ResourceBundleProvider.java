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
 * --
 * From OpenJPA: http://openjpa.apache.org/
 * Modifications to genuine file: none     
 */
package org.jenmo.common.localizer;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A simple mechanism for looking up ResourceBundle instances across different potential sources.
 * 
 * @author Nicolas Ocquidant (thanks to Stephen Kim)
 * @since 1.0
 */
interface ResourceBundleProvider {

   /**
    * Find a ResourceBundle with the given name, locale, and class loader (which may be null).
    */
   public ResourceBundle findResource(String name, Locale locale, ClassLoader loader);
}
