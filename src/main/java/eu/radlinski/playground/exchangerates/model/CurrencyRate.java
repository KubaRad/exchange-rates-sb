package eu.radlinski.playground.exchangerates.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@Entity()
@Table(name = "currency_rate")
@NamedQueries({
        @NamedQuery(
                name="CurrencyRate.findRatesBetweenDates",
                query="SELECT r from CurrencyRate r  WHERE r.rateDate >= :startDate AND r.rateDate <= :endDate ")
})

@IdClass(CurrencyRateId.class)
public class CurrencyRate {
    @Id
    @Column(name = "rate_date")
    @NotNull
    private LocalDate rateDate;

    @Id
    @Column(name = "source_currency")
    @Enumerated(EnumType.STRING)
    @NotNull
    private CurrencyType sourceCurrency;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "target_currency")
    @NotNull
    private CurrencyType targetCurrency;

    @Column(name = "rate_value", precision = 10, scale = 6)
    @NotNull
    private BigDecimal rateValue;


    public LocalDate getRateDate() {
        return rateDate;
    }

    public CurrencyType getSourceCurrency() {
        return sourceCurrency;
    }

    public CurrencyType getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRateValue() {
        return rateValue;
    }

    public CurrencyRate() {
    }

    public CurrencyRate(LocalDate rateDate, CurrencyType sourceCurrency, CurrencyType targetCurrency, BigDecimal rateValue) {
        this.rateDate = rateDate;
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.rateValue = rateValue;
    }
}
