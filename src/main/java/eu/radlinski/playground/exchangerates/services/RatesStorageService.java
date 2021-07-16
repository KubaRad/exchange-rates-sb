package eu.radlinski.playground.exchangerates.services;

import eu.radlinski.playground.exchangerates.model.CurrencyRate;
import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.repository.DailyRatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
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


    public void storeTestData(){
        List<RatesOutput> newData = provideRatesOutputDb(LocalDate.now());
        storeData(newData);
    }

    private final static int DATABASE_SIZE = 24;
    @Value("${exchange-rates.import-currencies}")
    public  Set<CurrencyType> AVAILABLE_CURRENCIES;

    public  List<RatesOutput> provideRatesOutputDb(final LocalDate date){
        LocalDate movingDate = DateTools.firstDayOfMonth(date);
        List<RatesOutput> rates = new ArrayList<>();
        for(int i=0; i<DATABASE_SIZE; i++){
            Map<CurrencyType, BigDecimal> ratesMap = new EnumMap<>(CurrencyType.class);
            AVAILABLE_CURRENCIES.forEach(ct -> ratesMap.put(ct, BigDecimal.valueOf(Math.random())));
            RatesOutput dailyRates = new RatesOutput(movingDate,CurrencyType.EUR, ratesMap);
            rates.add(dailyRates);
            movingDate = movingDate.minusMonths(1);
        }
        return rates;
    }
}
