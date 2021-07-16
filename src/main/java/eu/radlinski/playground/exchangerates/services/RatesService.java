package eu.radlinski.playground.exchangerates.services;

import eu.radlinski.playground.exchangerates.model.CurrencyRate;
import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.repository.DailyRatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@Service
public class RatesService {

    private final DailyRatesRepository ratesFacade;

    @Autowired
    public RatesService(DailyRatesRepository ratesFacade) {
        this.ratesFacade = ratesFacade;
    }

    public List<RatesOutput> lastYearRates(){
        LocalDate oneYearAgo = DateTools.firstDayOfMonth(LocalDate.now()).minusYears(1);
        return convertFromCurrencyRate(ratesFacade.findAllByRateDateAfter(oneYearAgo));
    }

    public List<RatesOutput> lastYearRates(final CurrencyType targetCurrency){
        LocalDate oneYearAgo = DateTools.firstDayOfMonth(LocalDate.now()).minusYears(1);
        return convertFromCurrencyRate(ratesFacade.findAllByRateDateAfter(oneYearAgo).stream()
                .filter(cr -> cr.getTargetCurrency() == targetCurrency)
                .collect(Collectors.toList()));
    }

    public Optional<RatesOutput> ratesForDate(final LocalDate selectedDate){
        return convertFromCurrencyRate(ratesFacade.findByRateDate(selectedDate)).stream().findFirst();
    }

    public Optional<RatesOutput> ratesForDate(final LocalDate selectedDate, final CurrencyType targetCurrency){
        return convertFromCurrencyRate(ratesFacade.findByRateDate(selectedDate).stream()
                .filter(cr -> cr.getTargetCurrency() == targetCurrency)
                .collect(Collectors.toList())).stream().findFirst();
    }

    public List<RatesOutput> ratesForDate(final LocalDate startDate, final LocalDate endDate){
        return convertFromCurrencyRate(ratesFacade.findRatesBetweenDates(startDate, endDate));
    }

    public List<RatesOutput> ratesForDate(final LocalDate startDate, final LocalDate endDate, final CurrencyType targetCurrency){
        return convertFromCurrencyRate(ratesFacade.findRatesBetweenDates(startDate, endDate).stream()
                .filter(cr -> cr.getTargetCurrency() == targetCurrency)
                .collect(Collectors.toList()));
    }

    private List<RatesOutput> convertFromCurrencyRate(List<CurrencyRate> rates){
        Map<DateAndSourceCurrency, RatesOutput> convertedValues = new HashMap<>();
        rates.forEach(r ->{
            DateAndSourceCurrency ratesKey = new DateAndSourceCurrency(r.getRateDate(), r.getSourceCurrency());
            Map<CurrencyType, BigDecimal> newRates = new EnumMap<>(CurrencyType.class);
            if(convertedValues.containsKey(ratesKey)){
                newRates.putAll(convertedValues.get(ratesKey).getRates());
            }
            newRates.put(r.getTargetCurrency(), r.getRateValue());
            RatesOutput ratesOutput = new RatesOutput(r.getRateDate(), r.getSourceCurrency(), newRates);
            convertedValues.put(ratesKey, ratesOutput);
        });
        return convertedValues.values().stream().sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate())).collect(Collectors.toList());
    }


    private static class DateAndSourceCurrency{
        private final LocalDate date;
        private final CurrencyType currency;

        public DateAndSourceCurrency(LocalDate date, CurrencyType currency) {
            this.date = date;
            this.currency = currency;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DateAndSourceCurrency that = (DateAndSourceCurrency) o;
            return date.equals(that.date) && currency == that.currency;
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, currency);
        }
    }

}
