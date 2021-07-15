package eu.radlinski.playground.exchangerates.services;

import eu.radlinski.playground.exchangerates.model.CurrencyRate;
import eu.radlinski.playground.exchangerates.model.DailyRates;
import eu.radlinski.playground.exchangerates.repository.DailyRatesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */
@Service
public class RatesStorageService {

    private DailyRatesRepository ratesFacade;

    public void storeData(List<RatesOutput> rates){
        List<DailyRates> newData = rates.stream().map(this::fromRatesOutput).collect(Collectors.toList());
        ratesFacade.saveAll(newData);
    }

    private DailyRates fromRatesOutput(RatesOutput ratesOutput){
        DailyRates dailyRates = new DailyRates(ratesOutput.getDate(), ratesOutput.getSource());
        List<CurrencyRate> currencyRates = ratesOutput.getRates().entrySet().stream().map(r -> new CurrencyRate(dailyRates, r.getKey(), r.getValue())).collect(Collectors.toList());
        dailyRates.setRates(currencyRates);
        return dailyRates;
    }
}
