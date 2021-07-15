package eu.radlinski.playground.exchangerates.tools;

import eu.radlinski.playground.exchangerates.model.CurrencyRate;
import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.model.DailyRates;
import eu.radlinski.playground.exchangerates.services.DateTools;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

public class DatabaseContentCreator {
    private final static int DATABASE_SIZE = 24;
    public final static Set<CurrencyType> AVAILABLE_CURRENCIES = EnumSet.of(CurrencyType.USD, CurrencyType.GBP, CurrencyType.HKD);

    public static List<DailyRates> provideDailyRatesDb(final LocalDate date){
        LocalDate movingDate = DateTools.firstDayOfMonth(date);
        List<DailyRates> rates = new ArrayList<>();
        for(int i=0; i<DATABASE_SIZE; i++){
            final DailyRates dailyRates = new DailyRates(movingDate,CurrencyType.EUR);
            List<CurrencyRate> currencyRates = AVAILABLE_CURRENCIES.stream()
                    .map(ct -> createCurrencyRating(ct, dailyRates) )
                    .collect(Collectors.toList());
            dailyRates.setRates(currencyRates);
            rates.add(dailyRates);
            movingDate = movingDate.minusMonths(1);
        }
        return rates;
    }

    private static CurrencyRate createCurrencyRating(final CurrencyType currency, final DailyRates dailyRates){
        return new CurrencyRate(dailyRates, currency, BigDecimal.valueOf(Math.random()));
    }
}
