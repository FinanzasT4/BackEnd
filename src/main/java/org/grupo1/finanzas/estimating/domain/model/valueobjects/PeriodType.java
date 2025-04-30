package org.grupo1.finanzas.estimating.domain.model.valueobjects;

import java.util.HashMap;
import java.util.Map;

public enum PeriodType {
    DAY,
    FORTNIGHT,
    MONTH,
    BIMONTHLY,
    QUARTER,
    FOUR_MONTHLY,
    SEMESTER,
    YEAR;

    // Tabla de conversión personalizada por tipo
    private final Map<String, Double> conversions = new HashMap<>();

    static {
        DAY.conversions.put("day", 1.0);
        DAY.conversions.put("fortnight", 1.0 / 15.0);
        DAY.conversions.put("month", 1.0 / 30.0);
        DAY.conversions.put("bimonthly", 1.0 / 60.0);
        DAY.conversions.put("quarter", 1.0 / 90.0);
        DAY.conversions.put("four_monthly", 1.0 / 120.0);
        DAY.conversions.put("semester", 1.0 / 180.0);
        DAY.conversions.put("year", 1.0 / 360.0);

        FORTNIGHT.conversions.put("day", 15.0);
        FORTNIGHT.conversions.put("fortnight", 1.0);
        FORTNIGHT.conversions.put("month", 0.5);
        FORTNIGHT.conversions.put("bimonthly", 0.25);
        FORTNIGHT.conversions.put("quarter", 0.1667);
        FORTNIGHT.conversions.put("four_monthly", 0.125);
        FORTNIGHT.conversions.put("semester", 0.0833);
        FORTNIGHT.conversions.put("year", 0.0417);

        MONTH.conversions.put("day", 30.0);
        MONTH.conversions.put("fortnight", 2.0);
        MONTH.conversions.put("month", 1.0);
        MONTH.conversions.put("bimonthly", 0.5);
        MONTH.conversions.put("quarter", 0.3333);
        MONTH.conversions.put("four_monthly", 0.25);
        MONTH.conversions.put("semester", 0.1667);
        MONTH.conversions.put("year", 0.0833);

        BIMONTHLY.conversions.put("day", 60.0);
        BIMONTHLY.conversions.put("fortnight", 4.0);
        BIMONTHLY.conversions.put("month", 2.0);
        BIMONTHLY.conversions.put("bimonthly", 1.0);
        BIMONTHLY.conversions.put("quarter", 0.6667);
        BIMONTHLY.conversions.put("four_monthly", 0.5);
        BIMONTHLY.conversions.put("semester", 0.3333);
        BIMONTHLY.conversions.put("year", 0.1667);

        QUARTER.conversions.put("day", 90.0);
        QUARTER.conversions.put("fortnight", 6.0);
        QUARTER.conversions.put("month", 3.0);
        QUARTER.conversions.put("bimonthly", 1.5);
        QUARTER.conversions.put("quarter", 1.0);
        QUARTER.conversions.put("four_monthly", 0.75);
        QUARTER.conversions.put("semester", 0.5);
        QUARTER.conversions.put("year", 0.25);

        FOUR_MONTHLY.conversions.put("day", 120.0);
        FOUR_MONTHLY.conversions.put("fortnight", 8.0);
        FOUR_MONTHLY.conversions.put("month", 4.0);
        FOUR_MONTHLY.conversions.put("bimonthly", 2.0);
        FOUR_MONTHLY.conversions.put("quarter", 1.3333);
        FOUR_MONTHLY.conversions.put("four_monthly", 1.0);
        FOUR_MONTHLY.conversions.put("semester", 0.6667);
        FOUR_MONTHLY.conversions.put("year", 0.3333);

        SEMESTER.conversions.put("day", 180.0);
        SEMESTER.conversions.put("fortnight", 12.0);
        SEMESTER.conversions.put("month", 6.0);
        SEMESTER.conversions.put("bimonthly", 3.0);
        SEMESTER.conversions.put("quarter", 2.0);
        SEMESTER.conversions.put("four_monthly", 1.5);
        SEMESTER.conversions.put("semester", 1.0);
        SEMESTER.conversions.put("year", 0.5);

        YEAR.conversions.put("day", 360.0);
        YEAR.conversions.put("fortnight", 24.0);
        YEAR.conversions.put("month", 12.0);
        YEAR.conversions.put("bimonthly", 6.0);
        YEAR.conversions.put("quarter", 4.0);
        YEAR.conversions.put("four_monthly", 3.0);
        YEAR.conversions.put("semester", 2.0);
        YEAR.conversions.put("year", 1.0);
    }

    public double convertTo(String targetUnit, double quantity) {
        Double factor = conversions.get(targetUnit.toLowerCase());
        if (factor == null) {
            throw new IllegalArgumentException("Unsupported target unit: " + targetUnit);
        }
        return quantity * factor;
    }

    public static PeriodType fromFrequency(Frequency frequency) {
        return switch (frequency) {
            case day -> DAY;
            case fortnight -> FORTNIGHT;
            case month -> MONTH;
            case bimonthly -> BIMONTHLY;
            case quarter -> QUARTER;
            case four_monthly -> FOUR_MONTHLY;
            case semester -> SEMESTER;
            case year -> YEAR;
        };
    }



    public double toDays(double quantity) {
        return convertTo("day", quantity);
    }

    public double toMonths(double quantity) {
        return convertTo("month", quantity);
    }

    public double toFortnights(double quantity) {
        return convertTo("fortnight", quantity);
    }

    public double toBimonths(double quantity) {
        return convertTo("bimonthly", quantity);
    }

    public double toQuarters(double quantity) {
        return convertTo("quarter", quantity);
    }

    public double toFourMonths(double quantity) {
        return convertTo("four_monthly", quantity);
    }

    public double toSemesters(double quantity) {
        return convertTo("semester", quantity);
    }

    public double toYears(double quantity) {
        return convertTo("year", quantity);
    }
}
