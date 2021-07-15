package eu.radlinski.playground.exchangerates.services;

import eu.radlinski.playground.exchangerates.model.CurrencyRate;
import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.model.DailyRates;
import eu.radlinski.playground.exchangerates.repository.DailyRatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        return ratesFacade.findRatesAfterDate(oneYearAgo).stream()
                .map(this::fromDailyRates)
                .collect(Collectors.toList());
    }

    public List<RatesOutput> lastYearRates(final CurrencyType targetCurrency){
        LocalDate oneYearAgo = DateTools.firstDayOfMonth(LocalDate.now()).minusYears(1);
        return ratesFacade.findRatesAfterDate(oneYearAgo).stream()
                .map(r -> fromDailyRates(r, targetCurrency).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<RatesOutput> ratesForDate(final LocalDate selectedDate){
        return ratesFacade.findByRatesDate(selectedDate).flatMap(r -> Optional.of(fromDailyRates(r)));
    }

    public Optional<RatesOutput> ratesForDate(final LocalDate selectedDate, final CurrencyType targetCurrency){
        return ratesFacade.findByRatesDate(selectedDate).flatMap(r -> fromDailyRates(r, targetCurrency));
    }

    public List<RatesOutput> ratesForDate(final LocalDate startDate, final LocalDate endDate){
        return ratesFacade.findRatesBetweenDates(startDate, endDate).stream()
                .map(this::fromDailyRates)
                .collect(Collectors.toList());
    }

    public List<RatesOutput> ratesForDate(final LocalDate startDate, final LocalDate endDate, final CurrencyType targetCurrency){
        return ratesFacade.findRatesBetweenDates(startDate, endDate).stream()
                .map(r -> fromDailyRates(r, targetCurrency).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private RatesOutput fromDailyRates(final DailyRates dailyRates){
        Map<CurrencyType, BigDecimal> currencyRatings = dailyRates.getRates().stream()
                .collect(Collectors.toMap(CurrencyRate::getTargetCurrency, CurrencyRate::getRateValue));
        return new RatesOutput(dailyRates.getRatesDate(), dailyRates.getSourceCurrency(), currencyRatings);
    }

    private Optional<RatesOutput> fromDailyRates(final DailyRates dailyRates, final CurrencyType targetCurrency){
        Map<CurrencyType, BigDecimal> currencyRatings = dailyRates.getRates().stream()
                .filter(cr -> (cr.getTargetCurrency() == targetCurrency))
                .collect(Collectors.toMap(CurrencyRate::getTargetCurrency, CurrencyRate::getRateValue));
        return currencyRatings.isEmpty() ? Optional.empty() : Optional.of(new RatesOutput(dailyRates.getRatesDate(), dailyRates.getSourceCurrency(), currencyRatings));
    }

}
