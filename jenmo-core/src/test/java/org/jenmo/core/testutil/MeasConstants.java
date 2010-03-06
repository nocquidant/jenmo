package org.jenmo.core.testutil;

public enum MeasConstants {
   GEOMETRY("Geometry"), PROROSITY("Porosity");

   private final String type;

   MeasConstants(String type) {
      this.type = type;
   }

   @Override
   public String toString() {
      return type;
   }

   // Implementing a fromString method
   private static final StringToEnum<MeasConstants> stringToEnum = new StringToEnum<MeasConstants>(
         values());

   public static MeasConstants fromString(String property) {
      return stringToEnum.fromString(property);
   }
}
