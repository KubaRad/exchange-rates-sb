package eu.radlinski.playground.exchangerates.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

public class CurrencyRateId implements Serializable {
    private CurrencyType targetCurrency;
    private DailyRates dailyRates;

    public CurrencyType getTargetCurrency() {
        return targetCurrency;
    }

    public DailyRates getDailyRates() {
        return dailyRates;
    }

    /*
     * For JPA spec
     */
    public CurrencyRateId() {
    }

    public CurrencyRateId(CurrencyType targetCurrency, DailyRates dailyRates) {
        this.targetCurrency = targetCurrency;
        this.dailyRates = dailyRates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyRateId that = (CurrencyRateId) o;
        return targetCurrency == that.targetCurrency && Objects.equals(dailyRates, that.dailyRates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetCurrency, dailyRates);
    }
}
