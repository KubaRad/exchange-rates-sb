package eu.radlinski.playground.exchangerates.restservice;

import eu.radlinski.playground.exchangerates.model.CurrencyType;
import org.springframework.core.convert.converter.Converter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Collectors;


public class CurrencyTypeConverter implements Converter<String, CurrencyType> {

    @Override
    public CurrencyType convert(String s) {
        CurrencyType result = null;
        try {
            result = CurrencyType.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new CurrencyTypeConversionException(s);
        }
        return result;
    }

    public static class CurrencyTypeConversionException extends RuntimeException{
        public CurrencyTypeConversionException(String receivedCurrencyString) {
            super(MessageFormat.format("Error parsing currency code parameter. Text: ''{0}'' is not valid currency code. Available codes: {1}"
                    , receivedCurrencyString
                    , Arrays.stream(CurrencyType.values())
                            .map(CurrencyType::name)
                            .collect(Collectors.joining(","))));
        }
    }
}