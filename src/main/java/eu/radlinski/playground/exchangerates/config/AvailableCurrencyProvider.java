package eu.radlinski.playground.exchangerates.config;

import eu.radlinski.playground.exchangerates.model.CurrencyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@Component
@Scope("singleton")
public class AvailableCurrencyProvider {

    @Value("${exchange-rates.import-currencies}")
    Set<String> currenciesProperty;

    Set<CurrencyType> currencies;


    public Set<CurrencyType> availableCurrencies(){
       return currencies;
    }

    @PostConstruct
    public void init(){
        if(currenciesProperty == null || currenciesProperty.isEmpty()){
            currencies = Collections.emptySet();
        } else {
            currencies = Collections.unmodifiableSet(currenciesProperty.stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toUpperCase)
                    .map(this::convertCurrency)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
        }
    }

    private CurrencyType convertCurrency(String currencyCode){
        CurrencyType result = null;
        try{
            result = CurrencyType.valueOf(currencyCode);
        } catch ( IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getLocalizedMessage());
        }
        return result;
    }



}
