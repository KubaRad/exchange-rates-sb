package eu.radlinski.playground.exchangerates.services;

import eu.radlinski.playground.exchangerates.model.CurrencyRate;
import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.model.DailyRates;
import eu.radlinski.playground.exchangerates.repository.DailyRatesRepository;
import eu.radlinski.playground.exchangerates.tools.DatabaseContentCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@ExtendWith(MockitoExtension.class)
class RatesServiceTest {

    private List<DailyRates> dailyRatesDb;
    private final Random rand = new Random();

    @Mock
    DailyRatesRepository dailyRatesFacade;

    @InjectMocks
    RatesService ratesService;

    @BeforeEach
    void init(){
        dailyRatesDb = DatabaseContentCreator.provideDailyRatesDb(LocalDate.now());
    }


    @Test
    void lastYearRates_all() {
        LocalDate oneYearAgo  =DateTools.firstDayOfMonth(LocalDate.now()).minusYears(1);
        List<DailyRates> lastYearDailyRates = dailyRatesDb.stream()
                .filter(dr -> dr.getRatesDate().isAfter(oneYearAgo))
                .collect(Collectors.toList());
        Mockito.when(dailyRatesFacade.findRatesAfterDate(oneYearAgo)).thenReturn(lastYearDailyRates);
        List<RatesOutput> ratesOutputList = ratesService.lastYearRates();
        assertNotNull(ratesOutputList);
        compareRatesLists(lastYearDailyRates, ratesOutputList);

    }


    @Test
    void lastYearRates_with_valid_currency() {
        LocalDate oneYearAgo = DateTools.firstDayOfMonth(LocalDate.now()).minusYears(1);
        List<DailyRates> lastYearDailyRates = dailyRatesDb.stream()
                .filter(dr -> dr.getRatesDate().isAfter(oneYearAgo))
                .collect(Collectors.toList());
        Mockito.when(dailyRatesFacade.findRatesAfterDate(oneYearAgo)).thenReturn(lastYearDailyRates);
        CurrencyType expectedCurrency = CurrencyType.GBP;
        List<RatesOutput> ratesOutputList = ratesService.lastYearRates(expectedCurrency);
        assertNotNull(ratesOutputList);
        compareRatesLists(lastYearDailyRates, ratesOutputList, expectedCurrency);

    }

    @Test
    void lastYearRates_with_invalid_currency() {
        LocalDate oneYearAgo = DateTools.firstDayOfMonth(LocalDate.now()).minusYears(1);
        List<DailyRates> lastYearDailyRates = dailyRatesDb.stream()
                .filter(dr -> dr.getRatesDate().isAfter(oneYearAgo))
                .collect(Collectors.toList());
        Mockito.when(dailyRatesFacade.findRatesAfterDate(oneYearAgo)).thenReturn(lastYearDailyRates);
        CurrencyType expectedCurrency = CurrencyType.PLN;
        List<RatesOutput> ratesOutputList = ratesService.lastYearRates(expectedCurrency);
        assertNotNull(ratesOutputList);
        assertEquals(0, ratesOutputList.size());

    }

    @Test
    void ratesForDate_valid_date(){
        DailyRates selectedRates = dailyRatesDb.get(random(0,11));
        Mockito.when(dailyRatesFacade.findByRatesDate(selectedRates.getRatesDate())).thenReturn(Optional.of(selectedRates));
        Optional<RatesOutput> result = ratesService.ratesForDate(selectedRates.getRatesDate());
        assertNotNull(result);
        assertTrue(result.isPresent());
        compareRatesObjects(selectedRates, result.get(), null);
    }

    @Test
    void ratesForDate_invalid_date(){
        Optional<RatesOutput> result = ratesService.ratesForDate(null);
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    void ratesForDate_out_of_range_date(){
        LocalDate invalidDate = dailyRatesDb.stream().map(DailyRates::getRatesDate).max(LocalDate::compareTo).orElse(LocalDate.MIN);
        Mockito.when(dailyRatesFacade.findByRatesDate(invalidDate)).thenReturn(Optional.empty());
        Optional<RatesOutput> result = ratesService.ratesForDate(invalidDate);
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    void ratesForDate_valid_date_valid_currency(){
        DailyRates selectedRates = dailyRatesDb.get(random(0,11));
        CurrencyType validCurrency = CurrencyType.USD;
        Mockito.when(dailyRatesFacade.findByRatesDate(selectedRates.getRatesDate())).thenReturn(Optional.of(selectedRates));
        Optional<RatesOutput> result = ratesService.ratesForDate(selectedRates.getRatesDate(), validCurrency);
        assertNotNull(result);
        assertTrue(result.isPresent());
        compareRatesObjects(selectedRates, result.get(), validCurrency);
    }

    @Test
    void ratesForDate_valid_date_invalid_currency(){
        DailyRates selectedRates = dailyRatesDb.get(random(0,11));
        CurrencyType invalidCurrency = CurrencyType.PLN;
        Mockito.when(dailyRatesFacade.findByRatesDate(selectedRates.getRatesDate())).thenReturn(Optional.of(selectedRates));
        Optional<RatesOutput> result = ratesService.ratesForDate(selectedRates.getRatesDate(), invalidCurrency);
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    void ratesForDate_valid_date_null_currency(){
        DailyRates selectedRates = dailyRatesDb.get(random(0,11));
        CurrencyType invalidCurrency = null;
        Mockito.when(dailyRatesFacade.findByRatesDate(selectedRates.getRatesDate())).thenReturn(Optional.of(selectedRates));
        Optional<RatesOutput> result = ratesService.ratesForDate(selectedRates.getRatesDate(), invalidCurrency);
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    void ratesForDate_valid_dates(){
        List<DailyRates> selectedRates = dailyRatesDb.subList(3,8);
        LocalDate startDate = selectedRates.stream().map(DailyRates::getRatesDate).min(LocalDate::compareTo).orElse(null);
        assertNotNull(startDate);
        LocalDate endDate = selectedRates.stream().map(DailyRates::getRatesDate).max(LocalDate::compareTo).orElse(null);
        assertNotNull(endDate);
        Mockito.when(dailyRatesFacade.findRatesBetweenDates(startDate, endDate)).thenReturn(selectedRates);
        List<RatesOutput> ratesOutputList = ratesService.ratesForDate(startDate, endDate);
        assertNotNull(ratesOutputList);
        compareRatesLists(selectedRates, ratesOutputList);
    }

    @Test
    void ratesForDate_valid_dates_valid_currency(){
        List<DailyRates> selectedRates = dailyRatesDb.subList(3,8);
        CurrencyType validCurrency = CurrencyType.USD;
        LocalDate startDate = selectedRates.stream().map(DailyRates::getRatesDate).min(LocalDate::compareTo).orElse(null);
        assertNotNull(startDate);
        LocalDate endDate = selectedRates.stream().map(DailyRates::getRatesDate).max(LocalDate::compareTo).orElse(null);
        assertNotNull(endDate);
        Mockito.when(dailyRatesFacade.findRatesBetweenDates(startDate, endDate)).thenReturn(selectedRates);
        List<RatesOutput> ratesOutputList = ratesService.ratesForDate(startDate, endDate, validCurrency);
        assertNotNull(ratesOutputList);
        compareRatesLists(selectedRates, ratesOutputList, validCurrency);
    }

    @Test
    void ratesForDate_valid_dates_invalid_currency(){
        List<DailyRates> selectedRates = dailyRatesDb.subList(3,8);
        LocalDate startDate = selectedRates.stream().map(DailyRates::getRatesDate).min(LocalDate::compareTo).orElse(null);
        assertNotNull(startDate);
        LocalDate endDate = selectedRates.stream().map(DailyRates::getRatesDate).max(LocalDate::compareTo).orElse(null);
        assertNotNull(endDate);
        Mockito.when(dailyRatesFacade.findRatesBetweenDates(startDate, endDate)).thenReturn(selectedRates);
        List<RatesOutput> ratesOutputList = ratesService.ratesForDate(startDate, endDate, null);
        assertNotNull(ratesOutputList);
        assertEquals(0, ratesOutputList.size());
    }

    @Test
    void ratesForDate_invalid_dates(){
        List<DailyRates> selectedRates = dailyRatesDb.subList(3,8);
        LocalDate startDate = selectedRates.stream().map(DailyRates::getRatesDate).min(LocalDate::compareTo).orElse(null);
        assertNotNull(startDate);
        LocalDate endDate = selectedRates.stream().map(DailyRates::getRatesDate).max(LocalDate::compareTo).orElse(null);
        assertNotNull(endDate);
        Mockito.when(dailyRatesFacade.findRatesBetweenDates(endDate, startDate)).thenReturn(Collections.emptyList());
        List<RatesOutput> ratesOutputList = ratesService.ratesForDate(endDate, startDate);
        assertNotNull(ratesOutputList);
        assertEquals(0, ratesOutputList.size());
    }

    private void compareRatesLists(List<DailyRates> sourceList, List<RatesOutput> targetList){
        assertEquals(sourceList.size(), targetList.size());
        for(int i=0; i < sourceList.size(); i++){
            compareRatesObjects(sourceList.get(i), targetList.get(i), null);
        }

    }

    private void compareRatesLists(final List<DailyRates> sourceList, final List<RatesOutput> targetList, CurrencyType expectedCurrency){
        final Map<LocalDate,DailyRates> sourceMap = sourceList.stream()
                .filter(dr ->{
                   return dr.getRates().stream().anyMatch(cr -> cr.getTargetCurrency() == expectedCurrency);
                })
                .collect(Collectors.toMap(DailyRates::getRatesDate, Function.identity()));
        assertEquals(sourceMap.size(), targetList.size());
        targetList.forEach(ro -> {
            assertNotNull(ro);
            assertTrue(sourceMap.containsKey(ro.getDate()));
            DailyRates dr = sourceMap.get(ro.getDate());
            assertNotNull(dr);
            compareRatesObjects(dr, ro, expectedCurrency);
        });

    }

    private void compareRatesObjects(final DailyRates source, final RatesOutput target, CurrencyType expectedCurrency){
        assertNotNull(source);
        assertNotNull(target);
        assertEquals(source.getRatesDate(), target.getDate());
        assertEquals(source.getSourceCurrency(), target.getSource());
        assertNotNull(source.getRates());
        assertNotNull(target.getRates());
        assertNotEquals(0, source.getRates().size());
        assertNotEquals(0, target.getRates().size());
        if (expectedCurrency == null) {
            assertEquals(source.getRates().size(), target.getRates().size());
            source.getRates().forEach(cr -> {
               assertTrue(target.getRates().containsKey(cr.getTargetCurrency()));
               BigDecimal rateValue = target.getRates().get(cr.getTargetCurrency());
               assertNotNull(rateValue);
               assertEquals(cr.getRateValue(), rateValue);
            });
        } else {

            assertEquals(1, target.getRates().size());
            CurrencyRate cr = source.getRates().stream().filter(c -> c.getTargetCurrency() == expectedCurrency).findFirst().orElse(null);
            assertNotNull(cr);
            assertTrue(target.getRates().containsKey(expectedCurrency));
            BigDecimal rateValue = target.getRates().get(expectedCurrency);
            assertNotNull(rateValue);
            assertEquals(cr.getRateValue(), rateValue);
        }
    }

    private int random(final int min, final int max){
        return rand.nextInt(max - min + 1) + min;
    }
}