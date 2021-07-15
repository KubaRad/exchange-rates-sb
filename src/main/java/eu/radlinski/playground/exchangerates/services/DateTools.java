package eu.radlinski.playground.exchangerates.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class provide tools for assessing first days of month and collection of first days for last year (including
 * current month)
 * Due to restriction of assessment and restriction in exchangerates.io free plan service operates only
 * on limited amount of data: the rates for first day of the month.
 *
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

public class DateTools {

    public static LocalDate firstDayOfMonth(LocalDate date){
        if(date==null){
            return null;
        }
        return LocalDate.of(date.getYear(), date.getMonth(), 1);
    }

    public static LocalDate firstDayOfMonthNow(){
        return firstDayOfMonth(LocalDate.now());
    }

    public static List<LocalDate> generateLastYearFirstDays(LocalDate date){
        if(date == null) {
            return Collections.emptyList();
        }
        List<LocalDate> result = new ArrayList<>(12);
        LocalDate movingDate = firstDayOfMonth(date);
        for(int i = 0; i < 12; i++){
            result.add(movingDate);
            movingDate = movingDate.minusMonths(1);
        }
        return result.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<LocalDate> generateLastYearFirstDaysForNow(){
        return generateLastYearFirstDays(LocalDate.now());
    }
}
