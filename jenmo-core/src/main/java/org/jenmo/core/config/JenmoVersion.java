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

import java.io.File;
import java.io.InputStream;
import java.security.AccessController;
import java.util.Properties;
import java.util.StringTokenizer;

import org.jenmo.common.util.J2DoPrivUtils;

/**
 * This class contains version information for Jenmo, accessible using the command line:
 * <code>java -jar jenmo-x.y.z.jar</code>.
 * 
 * @author Nicolas Ocquidant
 * @since 1.0
 */
public class JenmoVersion {

   public static final String VERSION_NUMBER;
   public static final String VERSION_ID;
   public static final String VENDOR_NAME = "Jenmo";
   public static final int MAJOR_RELEASE;
   public static final int MINOR_RELEASE;
   public static final int PATCH_RELEASE;
   public static final String RELEASE_STATUS;
   public static final String BUILD_NUMBER;
   public static final String SCHEMA_NUMBER;

   static {
      Properties revisionProps = new Properties();
      try {
         InputStream in = JenmoVersion.class
               .getResourceAsStream("/META-INF/jenmo-version.properties");
         if (in != null) {
            try {
               revisionProps.load(in);
            } finally {
               in.close();
            }
         }
      } catch (Exception e) {
      }

      String vers = revisionProps.getProperty("jenmo.version");
      if (vers == null || "".equals(vers.trim()))
         vers = "0.0.0";
      VERSION_NUMBER = vers;

      StringTokenizer tok = new StringTokenizer(VERSION_NUMBER, ".-");
      int major, minor, patch;
      try {
         major = tok.hasMoreTokens() ? Integer.parseInt(tok.nextToken()) : 0;
      } catch (Exception e) {
         major = 0;
      }
      try {
         minor = tok.hasMoreTokens() ? Integer.parseInt(tok.nextToken()) : 0;
      } catch (Exception e) {
         minor = 0;
      }
      try {
         patch = tok.hasMoreTokens() ? Integer.parseInt(tok.nextToken()) : 0;
      } catch (Exception e) {
         patch = 0;
      }

      String revision = revisionProps.getProperty("build.number");
      String schema = revisionProps.getProperty("schema.number");

      MAJOR_RELEASE = major;
      MINOR_RELEASE = minor;
      PATCH_RELEASE = patch;
      RELEASE_STATUS = tok.hasMoreTokens() ? tok.nextToken("!") : "";
      BUILD_NUMBER = revision;
      SCHEMA_NUMBER = schema;
      VERSION_ID = "jenmo-" + VERSION_NUMBER + "-s" + SCHEMA_NUMBER + "-b" + BUILD_NUMBER;
   }

   public static void main(String[] args) {
      System.out.println(new JenmoVersion().toString());
   }

   public String toString() {
      StringBuffer buf = new StringBuffer(80 * 30);
      appendBanner(buf);
      buf.append("\n");

      appendProperty("os.name", buf).append("\n");
      appendProperty("os.version", buf).append("\n");
      appendProperty("os.arch", buf).append("\n\n");

      appendProperty("java.version", buf).append("\n");
      appendProperty("java.vendor", buf).append("\n\n");

      buf.append("java.class.path:\n");
      StringTokenizer tok = new StringTokenizer((String) AccessController
            .doPrivileged(J2DoPrivUtils.getPropertyAction("java.class.path")), File.pathSeparator);
      while (tok.hasMoreTokens()) {
         buf.append("\t").append(tok.nextToken());
         buf.append("\n");
      }
      buf.append("\n");

      appendProperty("user.dir", buf);
      return buf.toString();
   }

   public void appendBanner(StringBuffer buf) {
      buf.append(VENDOR_NAME).append(" ");
      buf.append(VERSION_NUMBER);
      buf.append("\n");
      buf.append("Version id: ").append(VERSION_ID);
      buf.append("\n");
      buf.append("Schema revision: ").append(SCHEMA_NUMBER);
      buf.append("\n");
      buf.append("Build revision: ").append(BUILD_NUMBER);
      buf.append("\n");
   }

   private StringBuffer appendProperty(String prop, StringBuffer buf) {
      return buf.append(prop).append(": ").append(
            (String) AccessController.doPrivileged(J2DoPrivUtils.getPropertyAction(prop)));
   }
}
