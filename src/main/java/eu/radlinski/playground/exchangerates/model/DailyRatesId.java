package eu.radlinski.playground.exchangerates.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

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

    /*
     * For JPA spec
     */
    public DailyRatesId() {
    }

    public DailyRatesId(LocalDate ratesDate, CurrencyType sourceCurrency) {
        this.ratesDate = ratesDate;
        this.sourceCurrency = sourceCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyRatesId that = (DailyRatesId) o;
        return Objects.equals(ratesDate, that.ratesDate) && sourceCurrency == that.sourceCurrency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ratesDate, sourceCurrency);
    }
}
