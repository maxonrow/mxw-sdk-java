package com.mxw.utils;

import java.math.BigDecimal;

public final class Convert {

    public static BigDecimal fromCIN(String number, Unit unit){
        return fromCIN(new BigDecimal(number), unit);
    }

    public static BigDecimal fromCIN(BigDecimal number, Unit unit){
        return number.divide(unit.factor);
    }

    public static BigDecimal toCIN(String number, Unit unit) {
        return toCIN(new BigDecimal(number), unit);
    }

    public static BigDecimal toCIN(BigDecimal number, Unit unit) {
        return number.multiply(unit.getFactor());
    }

    public enum Unit {
        CIN("cin",0),
        KCIN("kcin", 3),
        MCIN("Mcin", 6),
        GCIN("Gcin", 9),
        TCIN("Tcin", 12),
        JCIN("Jcin", 15),
        MXW("mxw", 18);

        private String name;
        private BigDecimal factor;

        Unit(String name, int factor) {
            this.name = name;
            this.factor = BigDecimal.TEN.pow(factor);
        }

        public BigDecimal getFactor() {
            return factor;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Unit fromString(String name) {
            if (name != null) {
                for (Unit unit : Unit.values()) {
                    if (name.equalsIgnoreCase(unit.name)) {
                        return unit;
                    }
                }
            }
            return Unit.valueOf(name);
        }
    }
}
