package org.jenmo.core.testutil;

public enum TypeConstants {
   WELL("Well"), FAULT("Fault"), GEOMETRY("Geometry"), BORE("Bore"), LOG("Log");

   private final String type;

   TypeConstants(String type) {
      this.type = type;
   }

   @Override
   public String toString() {
      return type;
   }

   // Implementing a fromString method
   private static final StringToEnum<TypeConstants> stringToEnum = new StringToEnum<TypeConstants>(
         values());

   public static TypeConstants fromString(String property) {
      return stringToEnum.fromString(property);
   }
}
