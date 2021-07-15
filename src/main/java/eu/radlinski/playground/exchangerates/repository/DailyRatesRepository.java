package eu.radlinski.playground.exchangerates.repository;

import eu.radlinski.playground.exchangerates.model.DailyRates;
import eu.radlinski.playground.exchangerates.model.DailyRatesId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */
@Repository
public interface DailyRatesRepository extends CrudRepository<DailyRates, DailyRatesId> {

    Optional<DailyRates> findByRatesDate(LocalDate selectedDate);

    List<DailyRates> findRatesBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate")LocalDate endDate);

    List<DailyRates> findRatesAfterDate(@Param("startDate") LocalDate startDate);


}
