package eu.radlinski.playground.exchangerates.services;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

class DateToolsTest {

    @Test
    void firstDayOfMonth_valid_date() {
       LocalDate date = LocalDate.of(2020,4,15);
       LocalDate firstDayDate = LocalDate.of(2020,4,1);
       assertEquals(firstDayDate, DateTools.firstDayOfMonth(date));
    }

    @Test
    void firstDayOfMonth_invalid_date() {
        assertNull(DateTools.firstDayOfMonth(null));
    }


    @Test
    void firstDayOfMonthNow() {
        LocalDate date = LocalDate.now();
        LocalDate firstDayDate = LocalDate.of(date.getYear(),date.getMonth(),1);
        assertEquals(firstDayDate, DateTools.firstDayOfMonthNow());
    }

    @Test
    void generateLastYearFirstDays_valid_date() {
        LocalDate date = LocalDate.now();
        LocalDate firstDayDate = LocalDate.of(date.getYear(),date.getMonth(),1);
        List<LocalDate> yearDates = DateTools.generateLastYearFirstDays(date);
        assertNotNull(yearDates);
        assertEquals(12, yearDates.size());
        LocalDate minDate = yearDates.stream().min(LocalDate::compareTo).orElse(null);
        assertNotNull(minDate);
        LocalDate maxDate = yearDates.stream().max(LocalDate::compareTo).orElse(null);
        assertNotNull(maxDate);
        assertTrue(firstDayDate.isEqual(maxDate));
        assertTrue(maxDate.minusMonths(11).isEqual(minDate));
        for(int i = 1; i < yearDates.size(); i++){
            LocalDate actual = yearDates.get(i);
            LocalDate previous = yearDates.get(i-1);
            assertEquals(1, actual.getDayOfMonth());
            assertEquals(1, previous.getDayOfMonth());
            assertTrue(previous.plusMonths(1).isEqual(actual));
        }
    }

    @Test
    void generateLastYearFirstDays_invalid_date() {
        List<LocalDate> yearDates = DateTools.generateLastYearFirstDays(null);
        assertNotNull(yearDates);
        assertEquals(0, yearDates.size());
    }



    @Test
    void generateLastYearFirstDaysForNow() {
        LocalDate date = LocalDate.now();
        LocalDate firstDayDate = LocalDate.of(date.getYear(),date.getMonth(),1);
        List<LocalDate> validYearDates = DateTools.generateLastYearFirstDays(firstDayDate);
        List<LocalDate> yearDates = DateTools.generateLastYearFirstDaysForNow();
        assertEquals(validYearDates, yearDates);
    }
}