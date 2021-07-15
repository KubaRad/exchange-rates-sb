package eu.radlinski.playground.exchangerates.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@Entity(name = "currency_rate")
@IdClass(CurrencyRateId.class)
public class CurrencyRate {
    @Id
    @Enumerated(EnumType.STRING)
    private CurrencyType targetCurrency;

    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "daily_rates_rates_date", referencedColumnName = "rates_date"),
            @JoinColumn(name = "daily_rates_source_currency", referencedColumnName = "source_currency"),
    })
    private DailyRates dailyRates;

    @Column(name = "rate_value", precision = 9, scale = 6)
    @NotNull
    private BigDecimal rateValue;

    public CurrencyType getTargetCurrency() {
        return targetCurrency;
    }

    public DailyRates getDailyRates() {
        return dailyRates;
    }

    public BigDecimal getRateValue() {
        return rateValue;
    }

    public CurrencyRate() {
    }

    public CurrencyRate(DailyRates dailyRates, CurrencyType targetCurrency, BigDecimal rateValue) {
        this.targetCurrency = targetCurrency;
        this.dailyRates = dailyRates;
        this.rateValue = rateValue;
    }
}
