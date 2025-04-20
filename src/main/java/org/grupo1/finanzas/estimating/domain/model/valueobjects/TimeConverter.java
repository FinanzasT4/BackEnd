package org.grupo1.finanzas.estimating.domain.model.valueobjects;

import java.util.Map;

public record TimeConverter(PeriodType period_type_time_converter, double numberOfPeriods) {
    private static final Map<String, Double> TIME_CONVERSION_FACTORS_TO_DAYS = Map.of(
            "day", 1.0,
            "month", 30.0,
            "fortnight", 15.0,
            "bimonthly", 60.0,
            "quarter", 90.0,
            "four_monthly", 120.0,
            "semester", 180.0,
            "year", 360.0
    );

    private static final Map<String, Double> TIME_CONVERSION_FACTORS_TO_FORTNIGHT = Map.of(
            "day", 15.0,
            "fortnight", 1.0,
            "month", 2.0,
            "bimonthly", 60.0,
            "quarter", 90.0,
            "four_monthly", 120.0,
            "semester", 180.0,
            "year", 365.0
    );

    private static final Map<String, Double> TIME_CONVERSION_FACTORS_TO_MONTHS = Map.of(
            "day", 0.0333333333,
            "fortnight", 0.5,
            "month", 1.0,
            "bimonthly", 2.0,
            "quarter", 3.0,
            "four_monthly", 4.0,
            "semester", 6.0,
            "year", 12.0
    );

    private static final Map<String, Double> TIME_CONVERSION_FACTORS_TO_BIMONTHLY = Map.of(
            "day", 0.01666666666666666666666666666667,
            "fortnight", 0.25,
            "month", 0.5,
            "bimonthly", 1.0,
            "quarter", 1.5,
            "four_monthly", 2.0,
            "semester", 3.0,
            "year", 6.0
    );

    private static final Map<String, Double> TIME_CONVERSION_FACTORS_TO_QUARTER = Map.of(
            "day", 0.01111111111111111111111111111111,
            "fortnight", 0.16666666666666666666666666666667,
            "month", 0.33333333333333333333333333333333,
            "bimonthly", 0.66666666666666666666666666666667,
            "quarter", 1.0,
            "four_monthly", 1.3333333333333333333333333333333,
            "semester", 2.0,
            "year", 3.0
    );

    private static final Map<String, Double> TIME_CONVERSION_FACTORS_TO_FOUR_MONTHLY = Map.of(
            "day", 0.00833333333333333333333333333333,
            "fortnight", 0.125,
            "month", 0.25,
            "bimonthly", 0.5,
            "quarter", 0.75,
            "four_monthly", 1.0,
            "semester", 1.5,
            "year", 3.0
    );

    private static final Map<String, Double> TIME_CONVERSION_FACTORS_TO_SEMESTER = Map.of(
            "day", 0.00555555555555555555555555555556,
            "fortnight", 0.08333333333333333333333333333333,
            "month", 0.16666666666666666666666666666667,
            "bimonthly", 0.33333333333333333333333333333333,
            "quarter", 0.5,
            "four_monthly", 0.66666666666666666666666666666667,
            "semester", 1.0,
            "year", 2.0
    );

    private static final Map<String, Double> TIME_CONVERSION_FACTORS_TO_YEAR = Map.of(
            "day", 0.00277777777777777777777777777778,
            "fortnight", 0.04166666666666666666666666666667,
            "month", 0.08333333333333333333333333333333,
            "bimonthly", 0.16666666666666666666666666666667,
            "quarter", 0.25,
            "four_monthly", 0.33333333333333333333333333333333,
            "semester", 0.5,
            "year", 1.0
    );

    public double convertToDays() {
        if (!TIME_CONVERSION_FACTORS_TO_DAYS.containsKey(period_type_time_converter.toString())) {
            throw new IllegalArgumentException("Tipo de período no válido: " + period_type_time_converter);
        }
        double conversionFactor = TIME_CONVERSION_FACTORS_TO_DAYS.get(period_type_time_converter.toString());
        return numberOfPeriods * conversionFactor;
    }

    public double convertToFortnight() {
        if (!TIME_CONVERSION_FACTORS_TO_FORTNIGHT.containsKey(period_type_time_converter.toString())) {
            throw new IllegalArgumentException("Tipo de período no válido: " + period_type_time_converter);
        }
        double conversionFactor = TIME_CONVERSION_FACTORS_TO_FORTNIGHT.get(period_type_time_converter.toString());
        return numberOfPeriods * conversionFactor;
    }

    public double convertToMonths() {
        if (!TIME_CONVERSION_FACTORS_TO_MONTHS.containsKey(period_type_time_converter.toString())) {
            throw new IllegalArgumentException("Tipo de período no válido: " + period_type_time_converter);
        }
        double conversionFactor = TIME_CONVERSION_FACTORS_TO_MONTHS.get(period_type_time_converter.toString());
        return numberOfPeriods * conversionFactor;
    }

    public double convertToBimonthly() {
        if (!TIME_CONVERSION_FACTORS_TO_BIMONTHLY.containsKey(period_type_time_converter.toString())) {
            throw new IllegalArgumentException("Tipo de período no válido: " + period_type_time_converter);
        }
        double conversionFactor = TIME_CONVERSION_FACTORS_TO_BIMONTHLY.get(period_type_time_converter.toString());
        return numberOfPeriods * conversionFactor;
    }

    public double convertToQuarter() {
        if (!TIME_CONVERSION_FACTORS_TO_QUARTER.containsKey(period_type_time_converter.toString())) {
            throw new IllegalArgumentException("Tipo de período no válido: " + period_type_time_converter);
        }
        double conversionFactor = TIME_CONVERSION_FACTORS_TO_QUARTER.get(period_type_time_converter.toString());
        return numberOfPeriods * conversionFactor;
    }

    public double convertToFourMonthly() {
        if (!TIME_CONVERSION_FACTORS_TO_FOUR_MONTHLY.containsKey(period_type_time_converter.toString())) {
            throw new IllegalArgumentException("Tipo de período no válido: " + period_type_time_converter);
        }
        double conversionFactor = TIME_CONVERSION_FACTORS_TO_FOUR_MONTHLY.get(period_type_time_converter.toString());
        return numberOfPeriods * conversionFactor;
    }

    public double convertToSemester() {
        if (!TIME_CONVERSION_FACTORS_TO_SEMESTER.containsKey(period_type_time_converter.toString())) {
            throw new IllegalArgumentException("Tipo de período no válido: " + period_type_time_converter);
        }
        double conversionFactor = TIME_CONVERSION_FACTORS_TO_SEMESTER.get(period_type_time_converter.toString());
        return numberOfPeriods * conversionFactor;
    }

    public double convertToYear() {
        if (!TIME_CONVERSION_FACTORS_TO_YEAR.containsKey(period_type_time_converter.toString())) {
            throw new IllegalArgumentException("Tipo de período no válido: " + period_type_time_converter);
        }
        double conversionFactor = TIME_CONVERSION_FACTORS_TO_YEAR.get(period_type_time_converter.toString());
        return numberOfPeriods * conversionFactor;
    }
}
