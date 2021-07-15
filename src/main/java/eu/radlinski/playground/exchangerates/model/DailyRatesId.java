package eu.radlinski.playground.exchangerates.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

public class DailyRatesId implements Serializable {
    private LocalDate ratesDate;
    private CurrencyType sourceCurrency;

    public LocalDate getRatesDate() {
        return ratesDate;
    }

    public CurrencyType getSourceCurrency() {
        return sourceCurrency;
    }

    public DailyRatesId(LocalDate ratesDate, CurrencyType sourceCurrency) {
        this.ratesDate = ratesDate;
        this.sourceCurrency = sourceCurrency;
    }
}
