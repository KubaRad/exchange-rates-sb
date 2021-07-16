package eu.radlinski.playground.exchangerates.restservice;


import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.services.RatesOutput;
import eu.radlinski.playground.exchangerates.services.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@RestController
@RequestMapping("/api/rates")
public class RatesResource {

    public final  Set<CurrencyType> availableCurrencies;

    private final RatesService ratesService;

    @Autowired
    public RatesResource(@Value("${exchange-rates.import-currencies}") Set<CurrencyType> availableCurrencies, RatesService ratesService) {
        this.availableCurrencies = availableCurrencies;
        this.ratesService = ratesService;
    }

    @GetMapping("/{selectedDate}")
    public RatesOutput getRatesForDate(@PathVariable(value = "selectedDate") LocalDate selectedDate, @RequestParam(value = "targetCurrency", required = false) CurrencyType targetCurrency){
        if(targetCurrency == null){
            return ratesService.ratesForDate(selectedDate)
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, null, null));
        }

        if(!availableCurrencies.contains(targetCurrency)) {
            throw new CurrencyNotStoredException(targetCurrency, availableCurrencies);
        }
        return ratesService.ratesForDate(selectedDate, targetCurrency)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, null, null));
    }

    @GetMapping()
    public List<RatesOutput> getRatesForDates(@RequestParam(value = "startDate", required = false)LocalDate startDate, @RequestParam(value = "endDate", required = false) LocalDate endDate, @RequestParam(value = "targetCurrency", required = false) CurrencyType targetCurrency){

        if(startDate == null && endDate == null && targetCurrency == null){
            return ratesService.lastYearRates();
        }

        if(startDate == null && endDate == null ){
            if(!availableCurrencies.contains(targetCurrency)) {
                throw new CurrencyNotStoredException(targetCurrency, availableCurrencies);
            }
            return ratesService.lastYearRates(targetCurrency);
        }

        if(startDate != null && endDate != null && targetCurrency == null){
            return ratesService.ratesForDate(startDate, endDate);
        }

        if(startDate != null && endDate != null){
            if(!availableCurrencies.contains(targetCurrency)) {
                throw new CurrencyNotStoredException(targetCurrency, availableCurrencies);
            }
            return ratesService.ratesForDate(startDate, endDate, targetCurrency);
        }

        throw new OnlyOneDateParameterException();
    }


    public static class OnlyOneDateParameterException extends RuntimeException{
        public OnlyOneDateParameterException() {
            super("Both 'startDate' and 'endDate' parameters should be set to obtain rates in defined period");
        }
    }

    public static class CurrencyNotStoredException extends RuntimeException{
        public CurrencyNotStoredException(final CurrencyType receivedCurrency, final Set<CurrencyType> availableCurrencies) {
            super(MessageFormat.format("Stored exchange rates do not contains rate for currency: {0}. Available currencies: {1}"
                    , receivedCurrency
                    , availableCurrencies.stream()
                            .map(CurrencyType::name)
                            .collect(Collectors.joining(","))));
        }
    }

}
