package eu.radlinski.playground.exchangerates.tools;

import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.services.DateTools;
import eu.radlinski.playground.exchangerates.services.RatesOutput;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

public class RatesOutputContentCreator {
    private final static int DATABASE_SIZE = 24;
    public final static Set<CurrencyType> AVAILABLE_CURRENCIES = EnumSet.of(CurrencyType.USD, CurrencyType.GBP, CurrencyType.HKD);

    public static List<RatesOutput> provideRatesOutputDb(final LocalDate date){
        LocalDate movingDate = DateTools.firstDayOfMonth(date);
        List<RatesOutput> rates = new ArrayList<>();
        for(int i=0; i<DATABASE_SIZE; i++){
            Map<CurrencyType, BigDecimal> ratesMap = new EnumMap<>(CurrencyType.class);
            AVAILABLE_CURRENCIES.forEach(ct -> ratesMap.put(ct, BigDecimal.valueOf(Math.random())));
            RatesOutput dailyRates = new RatesOutput(movingDate,CurrencyType.EUR, ratesMap);
            rates.add(dailyRates);
            movingDate = movingDate.minusMonths(1);
        }
        return rates;
    }

}
