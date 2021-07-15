package eu.radlinski.playground.exchangerates.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */
@Entity
@Table(name = "daily_rates")
@NamedQueries({
        @NamedQuery(
                name="DailyRates.findRatesByDate",
                query="SELECT r from DailyRates r WHERE r.ratesDate = :selectedDate"),
        @NamedQuery(
                name="DailyRates.findRatesAfterDate",
                query="SELECT r from DailyRates r  WHERE r.ratesDate > :startDate "),
        @NamedQuery(
                name="DailyRates.findRatesBetweenDates",
                query="SELECT r from DailyRates r  WHERE r.ratesDate >= :startDate AND r.ratesDate <= :endDate ")
})
@IdClass(DailyRatesId.class)
public class DailyRates {
    @Id
    @Column(name = "rates_date")
    @NotNull
    private LocalDate ratesDate;

    @Id
    @Column(name = "source_currency")
    @Enumerated(EnumType.STRING)
    @NotNull
    private CurrencyType sourceCurrency;


    @OneToMany(mappedBy = "dailyRates", cascade = CascadeType.ALL)
    private List<CurrencyRate> rates;

    public LocalDate getRatesDate() {
        return ratesDate;
    }

    public CurrencyType getSourceCurrency() {
        return sourceCurrency;
    }

    public List<CurrencyRate> getRates() {
        return rates;
    }

    public void setRates(List<CurrencyRate> ratings) {
        this.rates = ratings;
    }

    public DailyRates() {
    }

    public DailyRates(LocalDate ratesDate, CurrencyType sourceCurrency) {
        this.ratesDate = ratesDate;
        this.sourceCurrency = sourceCurrency;
        this.rates = Collections.emptyList();
    }
}
