package eu.radlinski.playground.exchangerates.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

public class ExchangeRatesData {
    private Boolean success;
    private Long timestamp;
    private Boolean historical;
    private String base;
    private LocalDate date;
    private Map<String, BigDecimal> rates;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getHistorical() {
        return historical;
    }

    public void setHistorical(Boolean historical) {
        this.historical = historical;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }
}
