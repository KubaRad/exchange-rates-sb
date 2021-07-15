package eu.radlinski.playground.exchangerates.model;

import java.io.Serializable;

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

    public CurrencyRateId(CurrencyType targetCurrency, DailyRates dailyRates) {
        this.targetCurrency = targetCurrency;
        this.dailyRates = dailyRates;
    }
}
