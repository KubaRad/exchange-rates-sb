package eu.radlinski.playground.exchangerates.repository;

import eu.radlinski.playground.exchangerates.model.CurrencyRate;
import eu.radlinski.playground.exchangerates.model.CurrencyRateId;
import eu.radlinski.playground.exchangerates.model.CurrencyType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */
@Repository
public interface DailyRatesRepository extends CrudRepository<CurrencyRate, CurrencyRateId> {

    List<CurrencyRate> findByRateDate(LocalDate selectedDate);

    List<CurrencyRate> findByRateDateAndSourceCurrency(LocalDate selectedDate, CurrencyType sourceCurrency);


    List<CurrencyRate> findRatesBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate")LocalDate endDate);

    List<CurrencyRate> findAllByRateDateAfter(LocalDate date);



}
