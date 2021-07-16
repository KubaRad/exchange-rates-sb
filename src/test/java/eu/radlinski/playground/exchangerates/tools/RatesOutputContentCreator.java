package eu.radlinski.playground.exchangerates.tools;

import eu.radlinski.playground.exchangerates.model.CurrencyRate;
import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.services.DateTools;
import eu.radlinski.playground.exchangerates.services.RatesOutput;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
        rates.sort((o1, o2) -> o2.getDate()
                .compareTo(o1.getDate()));
        return rates;
    }

    public static List<CurrencyRate> convert2CurrencyRate(final List<RatesOutput> rates){
        return rates.stream().map(RatesOutputContentCreator::fromDailyRates).collect(ArrayList::new, List::addAll, List::addAll);
    }

    private static List<CurrencyRate> fromDailyRates(final RatesOutput ratesOutput){
        return ratesOutput.getRates().entrySet().stream()
                .map(e -> new CurrencyRate(ratesOutput.getDate(), ratesOutput.getSource(), e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }


}
