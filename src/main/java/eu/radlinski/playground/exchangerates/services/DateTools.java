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
        LocalDate now = LocalDate.now();
        /*
         * According to exchangeratesapi.io restrictions the data for the current day isn't accessible
         * so if we call function on 1st day we have to 'move' to the previous month to get whole previous year
         */
        return now.getDayOfMonth() > 1 ? firstDayOfMonth(now) : firstDayOfMonth(now.minusMonths(1).plusDays(1));
    }

    public static List<LocalDate> generateLastYearFirstDays(final LocalDate date){
        if(date == null) {
            return Collections.emptyList();
        }
        List<LocalDate> result = new ArrayList<>(12);
        LocalDate movingDate = date.getDayOfMonth() > 1 ? firstDayOfMonth(date) : date;
        for(int i = 0; i < 12; i++){
            result.add(movingDate);
            movingDate = movingDate.minusMonths(1);
        }
        return result.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<LocalDate> generateLastYearFirstDaysForNow(){

        return generateLastYearFirstDays(firstDayOfMonth(LocalDate.now()));
    }
}
