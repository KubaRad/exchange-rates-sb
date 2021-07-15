package eu.radlinski.playground.exchangerates.restservice;


import eu.radlinski.playground.exchangerates.config.AvailableCurrencyProvider;
import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.services.RatesOutput;
import eu.radlinski.playground.exchangerates.services.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@RestController
@RequestMapping("/api/rates")
public class RatesResource {

    private final AvailableCurrencyProvider availableCurrencyProvider;

    private final RatesService ratesService;

    @Autowired
    public RatesResource(AvailableCurrencyProvider availableCurrencyProvider, RatesService ratesService) {
        this.availableCurrencyProvider = availableCurrencyProvider;
        this.ratesService = ratesService;
    }

    @GetMapping("/{selectedDate}")
    public RatesOutput getRatesForDate(@PathVariable(value = "selectedDate") LocalDate selectedDate, @RequestParam(value = "targetCurrency", required = false) CurrencyType targetCurrency){
        if(targetCurrency == null){
            return ratesService.ratesForDate(selectedDate)
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, null, null));
        }

        if(!availableCurrencyProvider.availableCurrencies().contains(targetCurrency)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, currencyNotStoredExceptionMsg(targetCurrency), null);
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
            if(!availableCurrencyProvider.availableCurrencies().contains(targetCurrency)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, currencyNotStoredExceptionMsg(targetCurrency), null);
            }
            return ratesService.lastYearRates(targetCurrency);
        }

        if(startDate != null && endDate != null && targetCurrency == null){
            return ratesService.ratesForDate(startDate, endDate);
        }

        if(startDate != null && endDate != null){
            if(!availableCurrencyProvider.availableCurrencies().contains(targetCurrency)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, currencyNotStoredExceptionMsg(targetCurrency), null);
            }
            return ratesService.ratesForDate(startDate, endDate, targetCurrency);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, onlyOneDateParameterExceptionMsg(), null);
    }


    private String onlyOneDateParameterExceptionMsg(){
        return "Both 'startDate' and 'endDate' parameters should be set to obtain rates in defined period";
    }

    private String currencyNotStoredExceptionMsg(CurrencyType receivedCurrency){
        String currenciesString = availableCurrencyProvider.availableCurrencies().stream()
                .map(CurrencyType::name)
                .collect(Collectors.joining(","));
        return MessageFormat.format("Stored exchange rates do not contains rate for currency: {0}. Available currencies: {1}"
                , receivedCurrency
                , currenciesString);

    }

}
