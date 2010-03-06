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
package org.jenmo.core.config;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jenmo.common.config.AbstractConfiguration;
import org.jenmo.common.config.ConfValue;
import org.jenmo.common.config.IConfiguration;
import org.jenmo.common.util.Loader;
import org.jenmo.common.util.IProcedure.IProcedure1;
import org.jenmo.common.util.IProcedure.ProcedureException;

/**
 * Configuration for Jenmo.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public final class JenmoConfig extends AbstractConfiguration {
   /** The logger. */
   private static final Logger LOGGER = Logger.getLogger(JenmoConfig.class);

   /**
    * Option for runtime to manage EntityManager lifecycle.
    */
   public static final String OPTION_LAZYCLOSE_PM = "jenmo.option.lazyclose.pm";

   /**
    * Option for runtime to choose a JPA vendor.
    */
   public static final String OPTION_JPA_PROVIDER = "jenmo.option.jpa.provider";

   /**
    * In order to validate options.
    */
   private static final Map<String, IProcedure1<String>> VALIDATORS = new HashMap<String, IProcedure1<String>>();

   static {
      VALIDATORS.put(OPTION_LAZYCLOSE_PM, new IProcedure1<String>() {
         public boolean execute(String arg) throws ProcedureException {
            if (!arg.toLowerCase().equals(Boolean.toString(true))
                  && !arg.toLowerCase().equals(Boolean.toString(false))) {
               throw new IllegalArgumentException("Option for " + OPTION_LAZYCLOSE_PM + " must be in ("
                     + Boolean.toString(true) + "|" + Boolean.toString(false) + ")");
            }
            return true;
         }
      });
      VALIDATORS.put(OPTION_JPA_PROVIDER, new IProcedure1<String>() {
         public boolean execute(String arg) throws ProcedureException {
            if (!arg.toLowerCase().equals("openjpa") && !arg.toLowerCase().equals("eclipselink")) {
               throw new IllegalArgumentException("Option for " + OPTION_JPA_PROVIDER
                     + " must be in (openjpa|eclipselink)");
            }
            return true;
         }
      });
   }

   /** The singleton */
   private static final IConfiguration singleton = loadDefault();

   private JenmoConfig(Map<String, String> props) {
      initialiseProperties(props);
   }

   protected void initialiseProperties(Map<String, String> props) {
      Map<String, ConfValue<?>> options = new HashMap<String, ConfValue<?>>();

      String key = OPTION_LAZYCLOSE_PM; // default to true
      String def = Boolean.toString(true);
      Boolean valueBoolean = true;
      if (props != null && props.get(key) != null) {
         String str = props.get(key).trim();
         VALIDATORS.get(key).execute(str);
         valueBoolean = Boolean.parseBoolean(str);
      }
      options.put(key, new ConfValue<Boolean>(key, def, valueBoolean));

      key = OPTION_JPA_PROVIDER; // default to OpenJpa
      def = "openjpa";
      String valueStr = "openjpa";
      if (props != null && props.get(key) != null) {
         String str = props.get(key).trim();
         VALIDATORS.get(key).execute(str);
         valueStr = str;
      }
      options.put(key, new ConfValue<String>(key, def, valueStr));

      fromProperties(options);
   }

   public static IConfiguration getInstance() {
      return singleton;
   }

   /**
    * Gets a {@link IConfiguration} object from resource name.
    */
   private static IConfiguration load(String configFile) {
      URL configURL = Loader.getResource(configFile);

      Properties props = new Properties();
      LOGGER.debug("Reading configuration from URL " + configURL);
      InputStream istream = null;
      try {
         istream = configURL.openStream();
         props.load(istream);
      } catch (Exception e) {
         LOGGER.error("Could not read configuration file from URL [" + configURL + "].", e);
         return null;
      } finally {
         if (istream != null) {
            try {
               istream.close();
            } catch (Exception ignore) {
            }
         }
      }
      Map<String, String> map = new HashMap<String, String>(props.size());
      for (Map.Entry<Object, Object> entry : props.entrySet()) {
         map.put((String) entry.getKey(), (String) entry.getValue());
      }
      return new JenmoConfig(map);
   }

   private static IConfiguration loadDefault() {
      return load("jenmo.properties");
   }
}
