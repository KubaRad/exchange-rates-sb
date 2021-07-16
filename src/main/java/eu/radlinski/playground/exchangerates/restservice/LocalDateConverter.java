package eu.radlinski.playground.exchangerates.restservice;

import org.springframework.core.convert.converter.Converter;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * @author Kuba Radliński
 */

public class LocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String s) {
        LocalDate result = null;
        try {
            result = LocalDate.parse(s);
        } catch (DateTimeException e) {
            throw new LocalDateConversionException(e);
        }
        return result;
    }

    public static class LocalDateConversionException extends RuntimeException{
        public LocalDateConversionException(Exception e) {
            super("Error parsing date parameter. " + e.getMessage());
        }
    }
}