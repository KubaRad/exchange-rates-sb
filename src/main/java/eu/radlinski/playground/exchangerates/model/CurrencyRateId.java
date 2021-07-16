package eu.radlinski.playground.exchangerates.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

public class CurrencyRateId implements Serializable {
    private LocalDate rateDate;
    private CurrencyType sourceCurrency;
    private CurrencyType targetCurrency;

    public LocalDate getRateDate() {
        return rateDate;
    }

    public CurrencyType getSourceCurrency() {
        return sourceCurrency;
    }

    public CurrencyType getTargetCurrency() {
        return targetCurrency;
    }

    public CurrencyRateId() {
    }

    public CurrencyRateId(LocalDate rateDate, CurrencyType sourceCurrency, CurrencyType targetCurrency) {
        this.rateDate = rateDate;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyRateId that = (CurrencyRateId) o;
        return Objects.equals(rateDate, that.rateDate) && sourceCurrency == that.sourceCurrency && targetCurrency == that.targetCurrency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rateDate, sourceCurrency, targetCurrency);
    }
}
