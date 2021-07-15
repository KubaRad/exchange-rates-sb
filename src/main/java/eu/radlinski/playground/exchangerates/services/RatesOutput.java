package eu.radlinski.playground.exchangerates.services;

import eu.radlinski.playground.exchangerates.model.CurrencyType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

public class RatesOutput {
    private final LocalDate date;
    private final CurrencyType source;
    private final Map<CurrencyType, BigDecimal> rates;

    public LocalDate getDate() {
        return date;
    }

    public CurrencyType getSource() {
        return source;
    }

    public Map<CurrencyType, BigDecimal> getRates() {
        return rates;
    }

    public RatesOutput(LocalDate ratingDate, CurrencyType source, Map<CurrencyType, BigDecimal> rates) {
        this.date = ratingDate;
        this.source = source;
        this.rates = rates;
    }
}
