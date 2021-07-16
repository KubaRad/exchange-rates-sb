package eu.radlinski.playground.exchangerates.restservice;

import eu.radlinski.playground.exchangerates.config.AvailableCurrencyProvider;
import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.services.RatesOutput;
import eu.radlinski.playground.exchangerates.services.RatesService;
import eu.radlinski.playground.exchangerates.tools.RatesOutputContentCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@ExtendWith(MockitoExtension.class)
class RatesResourceTest {

    private List<RatesOutput> ratesOutputDb;
    private final Random rand = new Random();

    @Mock
    AvailableCurrencyProvider availableCurrencyProvider;

    @Mock
    RatesService ratesService;

    @InjectMocks
    RatesResource ratesResource;

    @BeforeEach
    void initEach(){
        ratesOutputDb = RatesOutputContentCreator.provideRatesOutputDb(LocalDate.now());
    }

    @Test

    void getRatesForDate_invalid_currency() {
        Mockito.when(availableCurrencyProvider.availableCurrencies()).thenReturn(RatesOutputContentCreator.AVAILABLE_CURRENCIES);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            ratesResource.getRatesForDate(ratesOutputDb.get(0).getDate(), CurrencyType.PLN);
        });
        assertNotNull(exception);
        assertTrue(exception instanceof RatesResource.CurrencyNotStoredException);
    }

    @Test
    void getRatesForDate_valid_date() {
        RatesOutput expected = ratesOutputDb.get(random(0, ratesOutputDb.size()-1));
        Mockito.when(ratesService.ratesForDate(expected.getDate())).thenReturn(Optional.of(expected));
        RatesOutput response = ratesResource.getRatesForDate(expected.getDate(), null);
        assertNotNull(response);
        assertEquals(expected, response);
    }

    @Test
    void getRatesForDate_valid_date_valid_currency() {
        CurrencyType validCurrency = CurrencyType.USD;
        RatesOutput expected = createOutputWithSelectedCurrency(ratesOutputDb.get(random(0, ratesOutputDb.size()-1)), validCurrency);
        Mockito.when(ratesService.ratesForDate(expected.getDate(), validCurrency)).thenReturn(Optional.of(expected));
        Mockito.when(availableCurrencyProvider.availableCurrencies()).thenReturn(RatesOutputContentCreator.AVAILABLE_CURRENCIES);
        RatesOutput response = ratesResource.getRatesForDate(expected.getDate(), validCurrency);
        assertNotNull(response);
        assertEquals(expected, response);
    }


    @Test
    void getRatesForDate_valid_date_invalid_currency() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            ratesResource.getRatesForDate(LocalDate.now(), CurrencyType.PLN);
        });
        assertNotNull(exception);
        assertTrue(exception instanceof RatesResource.CurrencyNotStoredException);
    }

    @Test
    void getRatesForDate_invalid_date() {
        LocalDate invalidDate = ratesOutputDb.stream()
                .map(RatesOutput::getDate)
                .min(LocalDate::compareTo)
                .flatMap(d -> Optional.of(d.minusDays(5)))
                .orElse(null);
        assertNotNull(invalidDate);
        Mockito.when(ratesService.ratesForDate(invalidDate)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            ratesResource.getRatesForDate(invalidDate, null);
        });
        assertNotNull(exception);
        assertTrue(exception instanceof ResponseStatusException);
        assertEquals(HttpStatus.NOT_FOUND, ((ResponseStatusException) exception).getStatus());
    }

    @Test
    void getRatesForDates_empty_parameters_list() {
        List<RatesOutput> expected = ratesOutputDb.subList(ratesOutputDb.size()-12,ratesOutputDb.size());
        Mockito.when(ratesService.lastYearRates()).thenReturn(expected);
        List<RatesOutput> response = ratesResource.getRatesForDates(null, null, null);
        assertNotNull(response);
        assertEquals(12, response.size());
        assertEquals(expected, response);
    }

    @Test
    void getRatesForDates_empty_date_parameters_valid_currency() {
        CurrencyType validCurrency = CurrencyType.USD;
        List<RatesOutput> expected = ratesOutputDb.subList(ratesOutputDb.size()-12,ratesOutputDb.size()).stream()
                .map(ro -> createOutputWithSelectedCurrency(ro, validCurrency))
                .collect(Collectors.toList());
        Mockito.when(ratesService.lastYearRates(validCurrency)).thenReturn(expected);
        Mockito.when(availableCurrencyProvider.availableCurrencies()).thenReturn(RatesOutputContentCreator.AVAILABLE_CURRENCIES);
        List<RatesOutput> response = ratesResource.getRatesForDates(null, null, validCurrency);
        assertNotNull(response);
        assertEquals(12, response.size());
        assertEquals(expected, response);
    }

    @Test
    void getRatesForDates_only_first_empty_date_parameter() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            ratesResource.getRatesForDates(LocalDate.now(), null, null);
        });
        assertNotNull(exception);
        assertTrue(exception instanceof RatesResource.OnlyOneDateParameterException);
    }

    @Test
    void getRatesForDates_only_second_empty_date_parameter() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            ratesResource.getRatesForDates(null, LocalDate.now(), null);
        });
        assertNotNull(exception);
        assertTrue(exception instanceof RatesResource.OnlyOneDateParameterException);
    }

    @Test
    void getRatesForDates_valid_date_parameters() {
        List<RatesOutput> expected = ratesOutputDb.subList(2, 9);
        LocalDate startDate = expected.stream().map(RatesOutput::getDate).min(LocalDate::compareTo).orElse(null);
        assertNotNull(startDate);
        LocalDate endDate = expected.stream().map(RatesOutput::getDate).max(LocalDate::compareTo).orElse(null);
        assertNotNull(startDate);
        Mockito.when(ratesService.ratesForDate(startDate, endDate)).thenReturn(expected);
        List<RatesOutput> response = ratesResource.getRatesForDates(startDate, endDate, null);
        assertNotNull(response);
        assertEquals(expected.size(), response.size());
        assertEquals(expected, response);
    }

    @Test
    void getRatesForDates_valid_date_parameters_valid_currency() {
        CurrencyType validCurrency = CurrencyType.USD;
        List<RatesOutput> expected = ratesOutputDb.subList(2, 9).stream()
                .map(ro -> createOutputWithSelectedCurrency(ro, validCurrency))
                .collect(Collectors.toList());;
        LocalDate startDate = expected.stream().map(RatesOutput::getDate).min(LocalDate::compareTo).orElse(null);
        assertNotNull(startDate);
        LocalDate endDate = expected.stream().map(RatesOutput::getDate).max(LocalDate::compareTo).orElse(null);
        assertNotNull(startDate);
        Mockito.when(ratesService.ratesForDate(startDate, endDate, validCurrency)).thenReturn(expected);
        Mockito.when(availableCurrencyProvider.availableCurrencies()).thenReturn(RatesOutputContentCreator.AVAILABLE_CURRENCIES);
        List<RatesOutput> response = ratesResource.getRatesForDates(startDate, endDate, validCurrency);
        assertNotNull(response);
        assertEquals(expected.size(), response.size());
        assertEquals(expected, response);
    }


    private int random(final int min, final int max){
        return rand.nextInt(max - min + 1) + min;
    }

    private RatesOutput createOutputWithSelectedCurrency(RatesOutput output, CurrencyType selectedCurrency){
        Map<CurrencyType, BigDecimal> ratesMap = new EnumMap<>(CurrencyType.class);
        if(output.getRates().containsKey(selectedCurrency)){
            ratesMap.put(selectedCurrency, output.getRates().get(selectedCurrency));
        }
        return new RatesOutput(output.getDate(), output.getSource(), ratesMap);
    }
}