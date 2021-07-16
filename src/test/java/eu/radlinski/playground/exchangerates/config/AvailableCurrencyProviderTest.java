package eu.radlinski.playground.exchangerates.config;

import eu.radlinski.playground.exchangerates.model.CurrencyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */


@ExtendWith(MockitoExtension.class)
class AvailableCurrencyProviderTest {

    private final static Set<CurrencyType> AVAILABLE_CURRENCIES = EnumSet.of(CurrencyType.USD, CurrencyType.GBP, CurrencyType.HKD);

    @Mock
    Set<String> currenciesProperty;

    @InjectMocks
    AvailableCurrencyProvider availableCurrencyProvider;

    @Test
    void availableCurrencies_empty_config_value() {
        final Set<String> currenciesPropertyStrings = AVAILABLE_CURRENCIES.stream().map(CurrencyType::name).collect(Collectors.toSet());

        Mockito.when(currenciesProperty.stream()).thenReturn(currenciesPropertyStrings.stream());
        availableCurrencyProvider.init();
        assertEquals(3, availableCurrencyProvider.availableCurrencies().size());
    }

    @Test
    void availableCurrencies_valid_currencies() {
        final Set<String> currenciesPropertyStrings = AVAILABLE_CURRENCIES.stream().map(CurrencyType::name).collect(Collectors.toSet());

        Mockito.when(currenciesProperty.stream()).thenReturn(currenciesPropertyStrings.stream());
        availableCurrencyProvider.init();
        Set<CurrencyType> currencyTypes = availableCurrencyProvider.availableCurrencies();
        assertEquals(3, currencyTypes.size());
        assertEquals(AVAILABLE_CURRENCIES, currencyTypes);
    }

    @Test
    void availableCurrencies_with_invalid_currency() {
        final Set<String> currenciesPropertyStrings = AVAILABLE_CURRENCIES.stream().map(CurrencyType::name).collect(Collectors.toSet());
        currenciesPropertyStrings.add("COS1");
        currenciesPropertyStrings.add("COS2");
        Mockito.when(currenciesProperty.stream()).thenReturn(currenciesPropertyStrings.stream());
        availableCurrencyProvider.init();
        Set<CurrencyType> currencyTypes = availableCurrencyProvider.availableCurrencies();
        assertEquals(3, currencyTypes.size());
        assertEquals(AVAILABLE_CURRENCIES, currencyTypes);
    }

}