package org.jenmo.core.testutil;

public enum PropConstants {
   COORDINATE_SYSTEM("CoordinateSystem"), OILFIELD_NAME("OilfieldName"), PLATEFORM_NAME(
         "PlateformName"), WELL_STATUS("WellStatus"), WELL_WATER_DEPTH("WellWaterDepth");

   private final String property;

   PropConstants(String property) {
      this.property = property;
   }

   @Override
   public String toString() {
      return property;
   }

   // Implementing a fromString method
   private static final StringToEnum<PropConstants> stringToEnum = new StringToEnum<PropConstants>(
         values());

   public static PropConstants fromString(String property) {
      return stringToEnum.fromString(property);
   }
}
