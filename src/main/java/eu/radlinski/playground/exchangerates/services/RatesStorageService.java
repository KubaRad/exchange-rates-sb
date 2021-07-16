package eu.radlinski.playground.exchangerates.services;

import eu.radlinski.playground.exchangerates.model.CurrencyRate;
import eu.radlinski.playground.exchangerates.repository.DailyRatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */
@Service
public class RatesStorageService {

    private final DailyRatesRepository ratesRepository;

    @Autowired
    public RatesStorageService(DailyRatesRepository ratesRepository) {
        this.ratesRepository = ratesRepository;
    }

    public void storeData(final List<RatesOutput> rates){
        List<CurrencyRate> newData = rates.stream().map(this::fromDailyRates).collect(ArrayList::new, List::addAll, List::addAll);
        ratesRepository.saveAll(newData);
    }

    private List<CurrencyRate> fromDailyRates(final RatesOutput ratesOutput){
        return ratesOutput.getRates().entrySet().stream()
                .map(e -> new CurrencyRate(ratesOutput.getDate(), ratesOutput.getSource(), e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

}
